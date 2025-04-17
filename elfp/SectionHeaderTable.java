package elfp;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

interface SectionHeaderTable {

    static final Map<Long, String> sectionTypeStr = new HashMap<Long, String>() {{
        put(0x0L,  "SHT_NULL");
        put(0x1L,  "SHT_PROGBITS");
        put(0x2L,  "SHT_SYMTAB");
        put(0x3L,  "SHT_STRTAB");
        put(0x4L,  "SHT_RELA");
        put(0x5L,  "SHT_HASH");
        put(0x6L,  "SHT_DYNAMIC");
        put(0x7L,  "SHT_NOTE");
        put(0x8L,  "SHT_NOBITS");
        put(0x9L,  "SHT_REL, no addends");
        put(0x0AL,  "SHT_SHLIB");
        put(0x0BL,  "SHT_DYNSYM");
        put(0x0EL,  "SHT_INIT_ARRAY");
        put(0x0FL,  "SHT_FINI_ARRAY");
        put(0x10L,  "SHT_PREINIT_ARRAY");
        put(0x11L,  "SHT_GROUP");
        put(0x12L,  "SHT_SYMTAB_SHNDX");
        put(0x13L,  "SHT_NUM");
        put(0x60000000L, "SHT_LOOS");
    }};

    public void parse();
    public void display();
}
