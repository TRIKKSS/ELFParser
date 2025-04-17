package elfp;

public class NotAnELFException extends Exception
{
    public NotAnELFException() {}

    // Constructor that accepts a message
    public NotAnELFException(String message)
    {
        super(message);
    }
}