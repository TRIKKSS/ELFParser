package elfp;
import java.io.*;

public class MyFuncs {

    /**
     * read bytes and format it to long in little endian format.
     * @f RandomAccessFile object
     * @size how many bytes to read
     */
    public static long readBytesToInt(RandomAccessFile f, int size) 
    {
        long result = 0;
        
        try {
            for (int i=0; i < size; i++) {
                result |= (long)f.read() << (i * 8);
            }
        } catch (IOException e) {
            e.printStackTrace();
			System.exit(1);
        }
        return result;
    }

    /**
     * read a string until null byte.
     * @f RandomAccessFile object
     */
    public static String readString(RandomAccessFile f)
    {
        String result = "";
        try {
            while (true)
            {
                char letter = (char)f.read();
                if (letter == '\0')
                {
                    break;
                }
                result += letter;
            }
        } catch (IOException e) {
            e.printStackTrace();
			System.exit(1);
        }
        return result;
    }


    /**
     * write a string into a binary file (adding a null byte at the end of the string.)
     * @f RandomAccessFile object
     * @str string to write
     */
    public static void writeString(RandomAccessFile f, String str)
    {
        try {
            for (int i=0; i < str.length(); i++)
            {
                f.writeByte(str.charAt(i));
            }
            f.writeByte(0); // null byte de fin de str
        } catch (IOException e) {
            e.printStackTrace();
			System.exit(1);
        }
    }


    /**
     * write a long as byte formatted in little endian.
     * @f RandomAccessFile object
     * @data integer to write
     * @size number of bytes to write
     */
    public static void writeIntAsLittleEndian(RandomAccessFile f, long data, int size)
    {
        try {
            for (int i = 0; i < size; i++)
            {
                f.writeByte((char)((data >> (i*8)) & 0xFF));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}