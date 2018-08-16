package farnetto.log4jconverter;

import org.apache.logging.log4j.LogManager;

public class Loader
{
    public static void main(String[] args)
    {
        System.setProperty("log4j2.debug", "true");
        System.setProperty("log4j2.configurationFile", args[0]);
        LogManager.getLogger(Loader.class).info("hello");
    }
}
