package elfp;

import java.io.*;
import elfp.capstonewrapper.*;

public class ElfParser extends ElfFile {

    private static int[] magicNumber = {0x7f, 0x45, 0x4c, 0x46};
    private RandomAccessFile f;

    /**
     * @filename a linux ELF file
     */
    public ElfParser(String filename) throws NotAnELFException
    {
        this.filename = filename;
        try {
            this.f = new RandomAccessFile(filename, "rw");
            this.f.seek(0);
            if (!checkMagicNumber())
            {
                throw new NotAnELFException("wrong magic number");
            }
            this.parser();


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * this method will parse the binary file.
     */
    private void parser() throws IOException 
    {
        if (this.f.read() == 1) {
            // x86 binary
            this.arch = 32;
        } else {
            // x64 binary
            this.arch = 64;
        }

        if (f.read() == 1) {
            this.endianness = "little";
        } else {
            this.endianness = "big";
        }
        f.skipBytes(1); // version, we dont save it for now.
        this.operatingSystem = this.f.read();

        // 8 useless byte (padding and further specifie ABI version)
        this.f.skipBytes(8);

        this.objectFileType = MyFuncs.readBytesToInt(this.f, 2); // f.readUnsignedShort();
        this.architecture = MyFuncs.readBytesToInt(this.f, 2); // f.readUnsignedShort();

        this.f.skipBytes(4); // another version, useless.

        this.entryPoint = MyFuncs.readBytesToInt(this.f, this.arch == 64 ? 8 : 4);

        this.phOffset =   MyFuncs.readBytesToInt(this.f, this.arch == 64 ? 8 : 4);
        this.shOffset =   MyFuncs.readBytesToInt(this.f, this.arch == 64 ? 8 : 4);

        this.f.skipBytes(4); // eflags, we skipBytes them for now.

        this.headerSize = MyFuncs.readBytesToInt(this.f, 2); // f.readUnsignedShort();

        this.phEntrySize = MyFuncs.readBytesToInt(this.f, 2);
        this.phNumberOfEntries = MyFuncs.readBytesToInt(this.f, 2);

        this.shEntrySize = MyFuncs.readBytesToInt(this.f, 2);
        this.shNumberOfEntries = MyFuncs.readBytesToInt(this.f, 2);

        this.shTSectionIndex = MyFuncs.readBytesToInt(this.f, 2); // pointeur vers .shstrtab qui contient les noms de nos sections
    
        this.parseSecHdrTable();
        this.parseProgHdrTable();
    }


    /**
     * parse Section header table and save it into an array of SecHdrTableEntry
     */
    private void parseSecHdrTable() throws IOException
    {
        // on va parcourir toutes les entrées et les parser une par une.
        for (int i=0; i < this.shNumberOfEntries; i++)
        {
            SecHdrTableEntry tmp = new SecHdrTableEntry(this.f, this.arch, this.shOffset +  i * this.shEntrySize, getShrstrtabAddr());
            this.SHTEntries.add(tmp);
        }
    }

    /**
     * parse program header table and save it into an array of ProgHdrTableEntry
     */
    private void parseProgHdrTable()
    {
        for (int i=0; i < this.phNumberOfEntries; i++)
        {
            ProgHdrTableEntry tmp = new ProgHdrTableEntry(this.f, this.arch, this.phOffset + i * this.phEntrySize);
            this.PHTEntries.add(tmp);
        }
    }

    /**
     * return address of the shrstrtab section which contains section names
     */
    private long getShrstrtabAddr() throws IOException
    {
        this.f.seek(this.shOffset + this.shTSectionIndex * this.shEntrySize + (this.arch == 64 ? 0x18 : 0x10));
        return MyFuncs.readBytesToInt(this.f, this.arch == 64 ? 8 : 4);
    }

    /**
     * check if the magic bytes of the input file correspond to an ELF
     */
    private boolean checkMagicNumber()
    {
        for (int i=0; i < magicNumber.length; i++)
        {
            try {
                if (this.f.read() != magicNumber[i])
                {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
			    System.exit(1);
            }
        }
        return true;
    }

    /**
     * Rewrite entrypoint address of the input ELF
     * @entry_point_addr new entry point address
     */
    public void setEntryPoint(long entry_point_addr)
    {
        try {
            long fp_save = this.f.getFilePointer();
            this.f.seek(0x18); // entrypoint raw offset

            this.entryPoint = entry_point_addr;
            MyFuncs.writeIntAsLittleEndian(this.f, entry_point_addr, this.arch == 64 ? 8 : 4);

            this.f.seek(fp_save);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Rewrite flags of a segment
     * @segmentIdx index of the segment to modify
     * @flags new flags like "RWX"
     */
    public boolean setProgHdrFlags(int segmentIdx, String flags)
    {
        if (segmentIdx >= this.phNumberOfEntries)
        {
            System.err.println(segmentIdx + " : Invalid segment index !");
            return false;
        }
        long segmentOffset = this.phOffset +  segmentIdx * this.phEntrySize + (this.arch == 64 ? 0x4: 0x18); // raw offset of flags

        long flagsAsInt = 0;

        for(int i=0; i < flags.length(); i++)
        {
            switch(flags.charAt(i))
            {
                case 'R':
                    flagsAsInt |= 0x4;
                    break;
                case 'W':
                    flagsAsInt |= 0x2;
                    break;
                case 'X':
                    flagsAsInt |= 0x1;
                    break;
                default:
                    System.err.println(flags.charAt(i) + " : Invalid flags !");
                    return false;
            }
        }
        try {
            long fp_save = this.f.getFilePointer();
            this.f.seek(segmentOffset);

            MyFuncs.writeIntAsLittleEndian(this.f, flagsAsInt, 4);
            this.PHTEntries.get(segmentIdx).flags = flagsAsInt;
            
            this.f.seek(fp_save);
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        return true;
    }

    /**
     * change the name of a section.
     * @secIdx index of section to modify.
     * @name nouveau nom de la section.
     */
    public boolean setSecHdrName(int secIdx, String name)
    {
        // idée : ajouter une recherche par de section par nom et pas par index

        /*
        // j'ai retiré cette vérification car si l'on change une première fois le nom d'une section en un nom plus petit, 
        // il n'est plus possible de définir à nouveau un nom de l'ancienne taille, néanmoins la taille reste disponible.

        if (name.length() > this.SHTEntries.get(secIdx).name.length())
        {
            // le nouveau nom ne doit pas être supérieur à l'ancien, sinon on risque de réécrire par dessus le prochain nom de section
            System.err.println("Section name too long.");
            return false;
        }
        */

       System.err.println("[Warning setSecHdrName()] Be careful that your new name does not overwrite other names.");

        try {
            if (getShrstrtabAddr() == 0)
            {
                System.err.println("No shrstrtab section in binary.");
                return false;
            }

            long fp_save = this.f.getFilePointer();
            this.f.seek(getShrstrtabAddr() + this.SHTEntries.get(secIdx).nameOffset);

            MyFuncs.writeString(this.f, name);
            this.SHTEntries.get(secIdx).name=name;

            this.f.seek(fp_save);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return true;
    }

    public void disas_at(long address, int size)
    {
        CapstoneWrapper.disas_func_at(this.f, address, size);
    }
}