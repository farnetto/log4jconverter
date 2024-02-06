package farnetto.log4jconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * Tests the Converter class.
 */
public class ConverterTest
{
    /**
     * Test with a relatively complicated configuration.
     */
    @Test
    public void testConverter() throws JAXBException, ParserConfigurationException, SAXException, TemplateNotFoundException, MalformedTemplateNameException,
            ParseException, IOException, TemplateException
    {
        convert("/in/log4j.fortest.xml");
    }

    @Test
    public void simple()
    {
        convert("/in/log4j.simple.xml");
    }

    @Test
    public void simpleCompare()
    {
        convertAndCompare("simple.xml");
    }

    @Test
    public void embeddedComment()
    {
        convertAndCompare("embeddedComment.xml");
    }

    /**
     * @param xmlFileSuffix
     */
    private void convertAndCompare(String xmlFileSuffix)
    {
        File outDir = new File("target/test-out");
        if (!outDir.exists())
        {
            if (!outDir.mkdirs())
            {
                fail("Could not create output directory: " + outDir);
            }
        }
        File f = new File(outDir, "log4j2." + xmlFileSuffix);
        if (f.exists())
        {
            if (!f.delete())
            {
                fail("could not delete " + f);
            }
        }
        try
        {
            FileOutputStream fos = new FileOutputStream(f);
            convert("/in/log4j." + xmlFileSuffix, fos);
        }
        catch (FileNotFoundException e)
        {
            fail("could not create file " + f + ", exception: " + e);
        }

        // compare ignoring blank lines
        String expectedOutputFileName = "/out/log4j2." + xmlFileSuffix;
        try
        {
            Path expectedOut = Paths.get(getClass().getResource(expectedOutputFileName).toURI());
            Path actualOut = outDir.toPath().resolve("log4j2." + xmlFileSuffix);
            List<String> expectedOutLines = removeBlanks(Files.readAllLines(expectedOut, StandardCharsets.UTF_8));
            List<String> actualOutLines = removeBlanks(Files.readAllLines(actualOut, StandardCharsets.UTF_8));
            for (int idx = 0; idx < expectedOutLines.size(); idx++)
            {
                String expectedLine = expectedOutLines.get(idx).trim();
                String actualLine = actualOutLines.get(idx).trim();
                assertEquals(expectedOutputFileName + " at line " + idx, expectedLine, actualLine);
            }
        }
        catch (URISyntaxException | IOException e)
        {
            fail("could not read file " + e);
        }
    }

    /**
     * remove empty lines
     */
    private List<String> removeBlanks(List<String> readAllLines)
    {
        return readAllLines.stream().filter(s -> !s.trim().isEmpty()).collect(Collectors.toList());
    }

    @Test(expected = NullPointerException.class)
    public void nullInput()
    {
        convert(null);
    }

    /**
     * Helper method.
     */
    private void convert(String file)
    {
        convert(file, System.out);
    }

    private void convert(String file, OutputStream out)
    {
        try
        {
            new Converter().convert(new File(getClass().getResource(file).toURI()), out);
        }
        catch (URISyntaxException e)
        {
            fail(e.toString());
        }
    }

    @Test
    public void appenderThreshold()
    {
        convertAndCompare("appenderThreshold.xml");
    }
}
