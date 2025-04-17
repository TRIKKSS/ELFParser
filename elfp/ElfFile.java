package elfp;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.*;


public class ElfFile {
    static final Map<Long, String> operatingSystemStr = new HashMap<Long, String>() {{
        put(0x0L, "System V");
        put(0x1L, "HP-UX");
        put(0x2L, "NetBSD");
        put(0x3L, "Linux");
        put(0x04L, "GNU Hurd");
        put(0x06L, "Solaris");
        put(0x07L, "AIX (Monterey)");
        put(0x08L, "IRIX");
        put(0x09L, "FreeBSD");
        put(0x0AL, "Tru64");
        put(0x0BL, "Novell Modesto");
        put(0x0CL, "OpenBSD");
        put(0x0DL, "OpenVMS");
        put(0x0EL, "NonStop Kernel");
        put(0x0FL, "AROS");
        put(0x10L, "FenixOS");
        put(0x11L, "Nuxi CloudABI");
        put(0x12L, "Stratus Technologies OpenVOS");
    }};
    
    static final Map<Long, String> objectFileTypeStr = new HashMap<Long, String>() {{
        put(0x00L,  "NONE - Unknown.");
        put(0x01L,  "REL - Relocatable file.");
        put(0x02L,  "EXEC - Executable file.");
        put(0x03L,  "DYN - Shared object.");
        put(0x04L,  "CORE - Core file.");
        put(0xFE00L,  "LOOS - Reserved inclusive range. Operating system specific.");
        put(0xFEFFL,  "HIOS - Reserved inclusive range. Operating system specific.");
        put(0xFF00L,  "LOPROC - Reserved inclusive range. Processor specific.");
        put(0xFFFFL,  "HIPROC - Reserved inclusive range. Operating system specific.");
    }};

    static final Map<Long, String> architectureStr = new HashMap<Long, String>() {{
        put(0x00L, 	"No specific instruction set");
        put(0x01L, 	"AT&T WE 32100");
        put(0x02L, 	"SPARC");
        put(0x03L, 	"x86");
        put(0x04L, 	"Motorola 68000 (M68k)");
        put(0x05L, 	"Motorola 88000 (M88k)");
        put(0x06L, 	"Intel MCU");
        put(0x07L, 	"Intel 80860");
        put(0x08L, 	"MIPS");
        put(0x09L, 	"IBM System/370");
        put(0x0AL, 	"MIPS RS3000 Little-endian");
        put(0x0FL, 	"Hewlett-Packard PA-RISC");
        put(0x13L, 	"Intel 80960");
        put(0x14L, 	"PowerPC");
        put(0x15L, 	"PowerPC (64-bit)");
        put(0x16L, 	"S390, including S390x");
        put(0x17L, 	"IBM SPU/SPC");
        put(0x24L, 	"NEC V800");
        put(0x25L, 	"Fujitsu FR20");
        put(0x26L, 	"TRW RH-32");
        put(0x27L, 	"Motorola RCE");
        put(0x28L, 	"Arm (up to Armv7/AArch32)");
        put(0x29L, 	"Digital Alpha");
        put(0x2AL, 	"SuperH");
        put(0x2BL, 	"SPARC Version 9");
        put(0x2CL, 	"Siemens TriCore embedded processor");
        put(0x2DL, 	"Argonaut RISC Core");
        put(0x2EL, 	"Hitachi H8/300");
        put(0x2FL, 	"Hitachi H8/300H");
        put(0x30L, 	"Hitachi H8S");
        put(0x31L, 	"Hitachi H8/500");
        put(0x32L, 	"IA-64");
        put(0x33L, 	"Stanford MIPS-X");
        put(0x34L, 	"Motorola ColdFire");
        put(0x35L, 	"Motorola M68HC12");
        put(0x36L, 	"Fujitsu MMA Multimedia Accelerator");
        put(0x37L, 	"Siemens PCP");
        put(0x38L, 	"Sony nCPU embedded RISC processor");
        put(0x39L, 	"Denso NDR1 microprocessor");
        put(0x3AL, 	"Motorola Star*Core processor");
        put(0x3BL, 	"Toyota ME16 processor");
        put(0x3CL, 	"STMicroelectronics ST100 processor");
        put(0x3DL, 	"Advanced Logic Corp. TinyJ embedded processor family");
        put(0x3EL, 	"AMD x86-64");
        put(0x3FL, 	"Sony DSP Processor");
        put(0x40L, 	"Digital Equipment Corp. PDP-10");
        put(0x41L, 	"Digital Equipment Corp. PDP-11");
        put(0x42L, 	"Siemens FX66 microcontroller");
        put(0x43L, 	"STMicroelectronics ST9+ 8/16 bit microcontroller");
        put(0x44L, 	"STMicroelectronics ST7 8-bit microcontroller");
        put(0x45L, 	"Motorola MC68HC16 Microcontroller");
        put(0x46L, 	"Motorola MC68HC11 Microcontroller");
        put(0x47L, 	"Motorola MC68HC08 Microcontroller");
        put(0x48L, 	"Motorola MC68HC05 Microcontroller");
        put(0x49L, 	"Silicon Graphics SVx");
        put(0x4AL, 	"STMicroelectronics ST19 8-bit microcontroller");
        put(0x4BL, 	"Digital VAX");
        put(0x4CL, 	"Axis Communications 32-bit embedded processor");
        put(0x4DL, 	"Infineon Technologies 32-bit embedded processor");
        put(0x4EL, 	"Element 14 64-bit DSP Processor");
        put(0x4FL, 	"LSI Logic 16-bit DSP Processor");
        put(0x8CL, 	"TMS320C6000 Family");
        put(0xAFL, 	"MCST Elbrus e2k");
        put(0xB7L, 	"Arm 64-bits (Armv8/AArch64)");
        put(0xDCL, 	"Zilog Z80");
        put(0xF3L, 	"RISC-V");
        put(0xF7L, 	"Berkeley Packet Filter");
        put(0x101L, 	"WDC 65C816"); 
    }};

    public String filename;

    public int arch;
    public String endianness;
    public long operatingSystem;
    public long objectFileType;
    public long architecture;
    public long entryPoint;
    public long phOffset;
    public long shOffset;
    public long headerSize;
    public long shTSectionIndex; // index of the sh table entry which contain section names

    public long phEntrySize;
    public long phNumberOfEntries;

    public long shEntrySize;
    public long shNumberOfEntries;


    public ProgramHeaderTable phTable;
    public SecHdrTableEntry shTable;

    public ArrayList<SecHdrTableEntry> SHTEntries = new ArrayList<SecHdrTableEntry>();
    public ArrayList<ProgHdrTableEntry> PHTEntries = new ArrayList<ProgHdrTableEntry>();
    
    public void displayHeader() {
        System.out.println(String.format("Class:\t\t\t\t\tELF%d", this.arch));
        System.out.println(String.format("Endianness:\t\t\t\t%s endian", this.endianness));
        System.out.println(String.format("OS/ABI:\t\t\t\t\t%s", getOperatingSystem()));
        System.out.println(String.format("Type:\t\t\t\t\t%s", getFileType()));
        System.out.println(String.format("Machine:\t\t\t\t%s", getArchitecture()));
        System.out.println(String.format("Entry point address:\t\t\t0x%016x", this.entryPoint));
        System.out.println(String.format("Start of program headers:\t\t0x%016x", this.phOffset));
        System.out.println(String.format("Start of section headers:\t\t0x%016x", this.shOffset));
        System.out.println(String.format("Size of this header:\t\t\t%d", this.headerSize));
        System.out.println(String.format("Size of program headers:\t\t%d", this.phEntrySize));
        System.out.println(String.format("Number of program headers:\t\t%d", this.phNumberOfEntries));
        System.out.println(String.format("Size of section headers:\t\t%d", this.shEntrySize));
        System.out.println(String.format("Number of section headers:\t\t%d", this.shNumberOfEntries));
        System.out.println(String.format("Section header string table index:\t%d\n", this.shTSectionIndex));
    }

    /**
     * display section header table
     */
    public void displaySHTable()
    {
        System.out.println("name                \ttype                \traw addr\tvirt addr\tsize    \tflags");
        for (int i=0; i < this.SHTEntries.size(); i++)
        {
            this.SHTEntries.get(i).display();
        }
        System.out.println("\nKey to Flags:");
        System.out.println("W (write), A (alloc), X (execute), M (merge), S (strings), I (info),");
        System.out.println("L (link order), O (extra OS processing required), G (group), T (TLS),");
        System.out.println("o (OS specific), E (exclude), p (processor specific)\n");
    }

    /**
     * display program header table
     */
    public void displayPHTable()
    {
        System.out.println("raw addr\tvirt addr\traw size\tvirt size\tflags\ttype");
        for (int i=0; i < this.PHTEntries.size(); i++)
        {
            this.PHTEntries.get(i).display();
        }
    }


    /**
     * get operating system
     */
    public String getOperatingSystem()
    {
        return this.operatingSystemStr.get(this.operatingSystem);
    }

    /**
     * get file type
     */
    public String getFileType()
    {
        return this.objectFileTypeStr.get(this.objectFileType);
    }

    /**
     * get architecture
     */
    public String getArchitecture()
    {
        return this.architectureStr.get(this.architecture);
    }

    public long getEntryPoint()
    {
        return this.entryPoint;
    }
}