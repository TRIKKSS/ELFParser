package elfp;


import java.io.*;


class SecHdrTableEntry implements SectionHeaderTable {
    public String name;
    public long nameOffset;
    public long type;        // 4 bytes
    public long flags;       // 4 bytes
    public long rawAddr;     // 4 or 8 bytes (where it is located in the binary)
    public long virtualAddr; // 4 or 8 bytes
    public long sectionSize; // 4 or 8 bytes

    private RandomAccessFile fp;
    private long arch;   // 32 or 64
    private long offset; // raw offset of the section in the ELF file.
    private long shstrtab;

    public SecHdrTableEntry(RandomAccessFile fileAccess, int arch, long offset, long shstrtab) {
        this.fp = fileAccess;
        this.arch = arch;
        this.offset = offset;
        this.shstrtab = shstrtab;
        this.parse();
    }

    /**
     * parse section entry
     */
    public void parse()
    {
        try {
            this.fp.seek(this.offset); // on se met au bon endroit dans le fichier.
            this.nameOffset = MyFuncs.readBytesToInt(this.fp, 4);
            
            // resolve name
            this.name = getName();
            this.type = MyFuncs.readBytesToInt(this.fp, 4);
            this.flags = MyFuncs.readBytesToInt(this.fp, this.arch == 64 ? 8 : 4);

            this.virtualAddr = MyFuncs.readBytesToInt(this.fp, this.arch == 64 ? 8 : 4);
            this.rawAddr = MyFuncs.readBytesToInt(this.fp, this.arch == 64 ? 8 : 4);
            this.sectionSize = MyFuncs.readBytesToInt(this.fp, this.arch == 64 ? 8 : 4);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * get section name
     */
    private String getName() throws IOException
    {
        // si le pointeur vers la section shstrtab n'est pas présent, on ne résout pas le nom.
        if (this.shstrtab == 0)
        {
            return "";
        }

        // on sauvegarde l'addresse actuel du file pointer
        long fp_save = this.fp.getFilePointer();

        this.fp.seek(this.shstrtab + this.nameOffset);

        String result = MyFuncs.readString(this.fp);
    
        // on restore le file pointer.
        this.fp.seek(fp_save);
        return result;
    }

    /**
     * display section information
     */
    public void display()
    {
        System.out.print(String.format("%-20s\t%-20s\t", this.name.length() != 0 ? this.name : "NULL", getType()));
        System.out.print(String.format("%08x\t%08x\t%08x\t", this.rawAddr, this.virtualAddr, this.sectionSize));
        System.out.println(this.getFlag());
    }

    /**
     * get section type
     */
    public String getType()
    {
        return sectionTypeStr.get(this.type);
    }

    /**
     * get section flags
     */
    public String getFlag()
    {
        String flagsAsStr = "";

        flagsAsStr += (this.flags & 0x1) == 0x1 ? "W" : "";
        flagsAsStr += (this.flags & 0x2) == 0x2 ? "A" : "";
        flagsAsStr += (this.flags & 0x4) == 0x4 ? "X" : "";
        flagsAsStr += (this.flags & 0x10) == 0x10 ? "M" : "";
        flagsAsStr += (this.flags & 0x20) == 0x20 ? "S" : "";
        flagsAsStr += (this.flags & 0x40) == 0x40 ? "I" : "";
        flagsAsStr += (this.flags & 0x80) == 0x80 ? "L" : "";
        flagsAsStr += (this.flags & 0x100) == 0x100 ? "O" : "";
        flagsAsStr += (this.flags & 0x200) == 0x200 ? "G" : "";
        flagsAsStr += (this.flags & 0x400) == 0x400 ? "T" : "";
        flagsAsStr += (this.flags & 0x0FF00000) == 0x0FF00000 ? "o" : "";
        flagsAsStr += (this.flags & 0xF0000000) == 0xF0000000 ? "p" : "";
        flagsAsStr += (this.flags & 0x8000000) == 0x8000000 ? "E" : "";

        return flagsAsStr;
    }
}