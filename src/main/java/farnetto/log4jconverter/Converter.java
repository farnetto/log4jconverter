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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String EOL = System.getProperty("line.separator");

    private static final String CONFIG_TAG = "log4j:configuration";

    private static final Pattern NAME_PATTERN = Pattern.compile("name=[\"'](.*?)[\"']");

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
            throw new NullPointerException("input must not be null");
        }

        Map<String,String> comments = parseComments(log4jInput);

        System.out.println(comments);

        parseXml(log4jInput, log4j2Output, comments);
    }

    /**
     * @param log4jInput
     * @param log4j2Output
     */
    private void parseXml(File log4jInput, OutputStream log4j2Output, Map<String,String> comments)
    {
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
        input.put("statusLevel", Boolean.valueOf(log4jConfig.getDebug()) ? "debug" : "warn");
        input.put("appenders", log4jConfig.getAppender());
        input.put("loggers", log4jConfig.getCategoryOrLogger());
        input.put("root", log4jConfig.getRoot());
        input.put("comments", comments);

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

    /**
     * @param log4jInput
     * @return
     */
    private Map<String,String> parseComments(File log4jInput)
    {
        Map<String,String> comments = new HashMap<>();

        try
        {
            List<String> lines = Files.readAllLines(Paths.get(log4jInput.toURI()));
            boolean comment = false;
            StringBuilder aComment = new StringBuilder();
            for (int i = 0; i < lines.size(); i++)
            {
                String line = lines.get(i);

                if (line.contains("<!--") && !line.contains("-->"))
                {
                    aComment.append(line.trim()).append(EOL);
                    comment = true;
                }
                else if (line.contains("-->"))
                {
                    aComment.append(line).append(EOL);
                    comment = false;
                    // process end of comment
                    for (i++; i < lines.size(); i++)
                    {
                        line = lines.get(i);
                        if (line.contains("name=") || line.contains(CONFIG_TAG) || line.contains("root"))
                        {
                            String key = parseName(line);
                            comments.put(key, aComment.toString());
                            aComment.setLength(0);
                            break;
                        }
                    }
                }
                else if (comment)
                {
                    aComment.append(line).append(EOL);
                }
            }
        }
        catch (IOException e1)
        {
            throw new ConverterException("Can not process file " + log4jInput, e1);
        }
        return comments;
    }

    /**
     * @param line
     * @return
     */
    private String parseName(String line)
    {
        if (line.contains("root"))
        {
            return "root";
        }
        if (line.contains(CONFIG_TAG))
        {
            return "log4jconfiguration";
        }
        Matcher m = NAME_PATTERN.matcher(line);
        if (m.find())
        {
            return m.group(1);
        }
        throw new ConverterException("can not find attribute name in line: " + line);
    }
}
