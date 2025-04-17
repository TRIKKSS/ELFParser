package elfp;

import java.util.HashMap;
import java.util.Map;


interface ProgramHeaderTable {
    static final Map<Long, String> segmentTypeStr = new HashMap<Long, String>() {{
        put(0x00000000L, "PT_NULL");
        put(0x00000001L, "PT_LOAD");
        put(0x00000002L, "PT_DYNAMIC");
        put(0x00000003L, "PT_INTERP");
        put(0x00000004L, "PT_NOTE");
        put(0x00000005L, "PT_SHLIB");
        put(0x00000006L, "PT_PHDR");
        put(0x00000007L, "PT_TLS");
        put(0x60000000L, "PT_LOOS");
        put(0x6FFFFFFFL, "PT_HIOS");
        put(0x70000000L, "PT_LOPROC");
        put(0x7FFFFFFFL, "PT_HIPROC");
        put(0x6474e550L, "GNU_EH_FRAME"); /* Frame unwind information */
        put(0x6474e551L, "GNU_STACK"); /* Stack flags */
        put(0x6474e552L, "GNU_RELRO"); /* Read-only after relocation */
        put(0x6474e553L, "GNU_PROPERTY"); /* GNU property */
        put(0x6474e554L, "GNU_SFRAME"); /* SFrame stack trace information */
        put(0x65a3dbe5L, "OPENBSD_MUTABLE"  );  /* Like bss, but not immutable.  */
        put(0x65a3dbe6L, "OPENBSD_RANDOMIZE");  /* Fill with random data.  */
        put(0x65a3dbe7L, "OPENBSD_WXNEEDED" );  /* Program does W^X violations.  */
        put(0x65a3dbe8L, "OPENBSD_NOBTCFI"  );  /* No branch target CFI.  */
        put(0x65a3dbe9L, "OPENBSD_SYSCALLS" );  /* System call sites.  */
        put(0x65a41be6L, "OPENBSD_BOOTDATA" );  /* Section for boot arguments.  */
        put(0x1000L, "GNU_MBIND_NUM");
        put(0x6474e555L, "GNU_MBIND_LO");
        put(0x6474f554L, "GNU_MBIND_HI");
    }};

    public void parse();
    public void display();
}