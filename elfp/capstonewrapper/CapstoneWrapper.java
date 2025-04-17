package elfp.capstonewrapper;

import java.io.*;

/*
  todo : 
  - l'ajouter au package elfp (ou créer un autre package je ne sais pas encore)
  - écrire la fonction qui va décompiler le code a une address donnée.
*/

public class CapstoneWrapper {
  static {
    System.loadLibrary("mycapstone"); // myjni.dll (Windows) or libmyjni.so (Unix)
  }

  // Declare a native method disas that receives a list of bytes and decompile it using capstone
  private static native void disas(byte[] assembly);
 
  public static void disas_func_at(RandomAccessFile f, long address, int size)
  {
    /*
    read bytes at address (probleme : we dont know the function size ?)
    peut être rajouter un paramètre size
    call disas with theses bytes
    */
    try {
      long fp_save = f.getFilePointer();
      f.seek(address);

      byte[] buffer = new byte[size];
      f.read(buffer);

      CapstoneWrapper.disas(buffer);

      f.seek(fp_save);
    } catch (IOException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }
}