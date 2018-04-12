package farnetto.log4jconverter;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Hello world!
 *
 */
@SuppressWarnings("restriction")
public class Converter
{
    public void convert(InputStream is) throws JAXBException
    {
        System.out.println("Hello World!");
        Unmarshaller u = JAXBContext.newInstance("org.apache.log4j.xml").createUnmarshaller();
        Object a = u.unmarshal(is);
        System.out.println(a);
    }
}
