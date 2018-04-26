package farnetto.log4jconverter;

public class ConverterException extends RuntimeException
{
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     * @param cause
     */
    public ConverterException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Konstruktor
     * 
     * @param message
     */
    public ConverterException(String message)
    {
        super(message);
    }

}
