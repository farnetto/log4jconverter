package farnetto.log4jconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import farnetto.log4jconverter.jaxb.Log4JConfiguration;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

/**
 * Converts log4j 1.2 xml configuration to concise (schemaless) log4j2 xml.
 */
public class Converter
{
    private static final String FREEMARKER_VERSION = "2.3.28";

    public static void main(String[] args) throws FileNotFoundException
    {
        new Converter().convert(new File(args[0]), System.out);
    }

    /**
     * @param log4jInput
     * @param log4j2Output
     */
    public void convert(File log4jInput, OutputStream log4j2Output)
    {
        if (log4jInput == null)
        {
            throw new NullPointerException("xmlInput must not be null");
        }

        try
        {
            List<String> lines = Files.readAllLines(Paths.get(log4jInput.toURI()));
            boolean comment = false;
            for (int i = 0; i < lines.size(); i++)
            {
                String line = lines.get(i);
                if (comment || line.contains("<!--"))
                {
                    System.out.println(line);
                    comment = true;
                }
                if (comment && line.contains("-->"))
                {
                    System.out.println(line);
                    comment = false;
                }
            }
        }
        catch (IOException e1)
        {
            throw new ConverterException("Can not process file " + log4jInput, e1);
        }

        Log4JConfiguration log4jConfig = null;
        try
        {
            Unmarshaller unmarshaller = JAXBContext.newInstance("farnetto.log4jconverter.jaxb").createUnmarshaller();

            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            spf.setFeature("http://xml.org/sax/features/validation", false);

            XMLReader xmlReader = spf.newSAXParser().getXMLReader();

            InputSource inputSource = new InputSource(new FileInputStream(log4jInput));
            SAXSource source = new SAXSource(xmlReader, inputSource);

            log4jConfig = (Log4JConfiguration) unmarshaller.unmarshal(source);
        }
        catch (JAXBException | ParserConfigurationException | SAXException | FileNotFoundException e)
        {
            throw new ConverterException("Can not initialize Unmarshaller", e);
        }

        Map<String,Object> input = new HashMap<String,Object>();
        input.put("statusLevel", "warn");
        input.put("appenders", log4jConfig.getAppender());
        input.put("loggers", log4jConfig.getCategoryOrLogger());
        input.put("root", log4jConfig.getRoot());

        Configuration cfg = new Configuration(new Version(FREEMARKER_VERSION));
        cfg.setClassForTemplateLoading(getClass(), "template");
        try (Writer log4j2OutputWriter = new OutputStreamWriter(log4j2Output))
        {
            Template template = cfg.getTemplate("log4j2.ftl");
            template.process(input, log4j2OutputWriter);
        }
        catch (IOException | TemplateException e)
        {
            throw new ConverterException("Cannot process template", e);
        }
    }
}
