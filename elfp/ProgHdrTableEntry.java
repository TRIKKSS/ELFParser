package elfp;


import java.io.*;


class ProgHdrTableEntry implements ProgramHeaderTable {
    public long type;             // 4 bytes
    public long flags;            // 4 bytes
    public long rawAddr;          // 4 or 8 bytes (where it is located in the binary)
    public long virtualAddr;      // 4 or 8 bytes
    public long rawSegmentSize;   // 4 or 8 bytes
    public long virtSegmentSize;

    private RandomAccessFile fp;
    private long arch;            // 32 or 64
    private long offset;          // raw offset of the section in the ELF file.

    public ProgHdrTableEntry(RandomAccessFile fileAccess, long arch, long offset) {
        this.fp = fileAccess;
        this.arch = arch;
        this.offset = offset;
        this.parse();
    }

    /**
     * parse the segment
     */
    public void parse()
    {
        try {
            this.fp.seek(this.offset); // on se met au bon endroit dans le fichier.

            this.type = MyFuncs.readBytesToInt(this.fp, 4);

            if (this.arch == 64)
            {
                this.flags = MyFuncs.readBytesToInt(this.fp, 4);
            }

            this.rawAddr = MyFuncs.readBytesToInt(this.fp, this.arch == 64 ? 8 : 4);
            this.virtualAddr = MyFuncs.readBytesToInt(this.fp, this.arch == 64 ? 8 : 4);
            this.fp.skipBytes(this.arch == 64 ? 8 : 4); // On systems where physical address is relevant, reserved for segment's physical address. 
            this.rawSegmentSize = MyFuncs.readBytesToInt(this.fp, this.arch == 64 ? 8 : 4);
            this.virtSegmentSize = MyFuncs.readBytesToInt(this.fp, this.arch == 64 ? 8 : 4);

            if (this.arch == 32)
            {
                this.flags = MyFuncs.readBytesToInt(this.fp, 4);
            }


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * display the segment informations
     */
    public void display()
    {
        System.out.print(String.format("%08x\t%08x\t%08x\t%08x\t", this.rawAddr, this.virtualAddr, this.rawSegmentSize, this.virtSegmentSize));
        System.out.print(this.getFlag() + "\t");
        System.out.println(this.getType());
    }

    /**
     * get segment type
     */
    public String getType()
    {
        return segmentTypeStr.get(this.type);
    }

    /**
     * return flags (read, write, exec)
     */
    public String getFlag()
    {
        String flagsAsStr = "";

        flagsAsStr += (this.flags & 0x4) == 0x4 ? "R" : "-";
        flagsAsStr += (this.flags & 0x2) == 0x2 ? "W" : "-";
        flagsAsStr += (this.flags & 0x1) == 0x1 ? "X" : "-";

        return flagsAsStr;
    }
}