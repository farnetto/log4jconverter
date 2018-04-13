package farnetto.log4jconverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
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
 * Converts log4j 1.2 xml in log4j2 (schema-free) xml configuration.
 */
public class Converter
{
    private static final String FREEMARKER_VERSION = "2.3.28";

    /**
     * @param log4jInput
     * @param log4j2Output
     */
    public void convert(InputStream log4jInput, OutputStream log4j2Output)
    {
        if (log4jInput == null)
        {
            throw new NullPointerException("xmlInput must not be null");
        }

        Log4JConfiguration log4jConfig = null;
        try
        {
            Unmarshaller unmarshaller = JAXBContext.newInstance("farnetto.log4jconverter.jaxb").createUnmarshaller();

            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            spf.setFeature("http://xml.org/sax/features/validation", false);

            XMLReader xmlReader = spf.newSAXParser().getXMLReader();

            InputSource inputSource = new InputSource(log4jInput);
            SAXSource source = new SAXSource(xmlReader, inputSource);

            log4jConfig = (Log4JConfiguration) unmarshaller.unmarshal(source);
        }
        catch (JAXBException | ParserConfigurationException | SAXException e)
        {
            throw new ConverterException("Cannot initialize Unmarshaller", e);
        }

        Map<String,Object> input = new HashMap<String,Object>();
        input.put("statusLevel", "warn");
        input.put("appenders", log4jConfig.getAppender());
        input.put("loggers", log4jConfig.getCategoryOrLogger());
        input.put("root", log4jConfig.getRoot());

        Configuration cfg = new Configuration(new Version(FREEMARKER_VERSION));
        cfg.setClassForTemplateLoading(getClass(), "template");
        try
        {
            Template template = cfg.getTemplate("log4j2.ftl");
            Writer consoleWriter = new OutputStreamWriter(log4j2Output);
            template.process(input, consoleWriter);
        }
        catch (IOException | TemplateException e)
        {
            throw new ConverterException("Cannot process template", e);
        }
    }
}
