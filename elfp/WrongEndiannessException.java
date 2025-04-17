package elfp;

public class WrongEndiannessException extends Exception
{
    public WrongEndiannessException()
    {
        System.out.println("endianess can only be 'little' or 'big'");
    }
}