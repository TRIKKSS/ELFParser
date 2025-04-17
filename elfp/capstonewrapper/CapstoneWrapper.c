#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>
#include <string.h>
#include <capstone/capstone.h>

#include "elfp_capstonewrapper_CapstoneWrapper.h"

JNIEXPORT void JNICALL Java_elfp_capstonewrapper_CapstoneWrapper_disas
  (JNIEnv * env, jclass thiz, jbyteArray input) {
    /*
    convert jbyteArray to unsigned char*
    */
    int jbyteArray_size = (*env)->GetArrayLength(env, input);
    // printf("size of the array is : %d\n", jbyteArray_size);
    unsigned char * CODE = malloc(jbyteArray_size + 1);
    if (CODE == NULL)
    {
        fprintf(stderr, "malloc error.");
        return;
    }
    memcpy(CODE, (*env)->GetByteArrayElements(env, input, 0), jbyteArray_size);

    //printf("array converted to unsigned char *\n");

	csh handle;
	cs_insn *insn;
	size_t count;

	if (cs_open(CS_ARCH_X86, CS_MODE_64, &handle) != CS_ERR_OK)
	{
		fprintf(stderr, "[!] Capstone Wrapper : Failed to disassemble given code!\n");
		return;
	}
	count = cs_disasm(handle, CODE, jbyteArray_size, 0x1000, 0, &insn);
	if (count > 0) {
		size_t j;
		for (j = 0; j < count; j++) {
			printf("0x%"PRIx64":\t%s\t\t%s\n", insn[j].address, insn[j].mnemonic,
					insn[j].op_str);
		}

		cs_free(insn, count);
	} else
		fprintf(stderr, "[!] Capstone Wrapper : Failed to disassemble given code!\n");

	cs_close(&handle);

    return;
}