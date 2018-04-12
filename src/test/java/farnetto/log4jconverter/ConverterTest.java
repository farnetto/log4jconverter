package farnetto.log4jconverter;

import javax.xml.bind.JAXBException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ConverterTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ConverterTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ConverterTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws JAXBException e
     */
    public void testConverter() throws JAXBException
    {
        new Converter().convert(getClass().getResourceAsStream("/corona/log4j.fortest.xml"));
    }
}
