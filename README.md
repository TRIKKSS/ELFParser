# ELFParser

ELFParser is a Java library that allows you to parse and patch ELF (Executable and Linkable Format) files.
This project was a school project designed to get familiar with the Java language. As such, bugs or imperfections may exist.

## Compiling the Native Capstone Wrapper

The library relies on Capstone, To install it, refer to the official documentation: [capstone download](https://www.capstone-engine.org/download.html)

```
cd elfp/capstonewrapper
gcc -I /usr/lib/jvm/java-21-openjdk/include  -I /usr/lib/jvm/java-21-openjdk/include/linux/ -lcapstone -fPIC -shared -o libmycapstone.so CapstoneWrapper.c
javac CapstoneWrapper.java
```

## Example Usage

Here's a simple example showing how to use the library:

```java
import elfp.*;

public class Test {
	public static void main(String[] args)
	{
		try {
			if (args.length != 1)
			{
				System.out.println("Usage :  java prog_name ELF_FILE");
				return;
			}
			
            // Create an ElfParser instance with the given ELF file.
            // The constructor will automatically parse the file.
			ElfParser elf = new ElfParser(args[0]);
			
			// Display ELF headers
			elf.displayHeader();
			// Display section headers
			elf.displaySHTable();
			// Display program headers
			elf.displayPHTable();


			System.out.println("Disassembling assembly code at entrypoint : ");
			// Disassemble 50 bytes of code starting from the entry point
			elf.disas_at(elf.getEntryPoint(), 50);

			// Modify values (Warning: this may corrupt the binary!)
			elf.setEntryPoint(0x1337133713371337L);
			elf.setProgHdrFlags(0, "W");
			elf.setSecHdrName(1, ".hello");
		} catch (NotAnELFException e) {
            e.printStackTrace();
			System.exit(1);
		}
	}
}
```

### Compile and Run

```
javac Test.java
java -Djava.library.path=$PWD/elfp/capstonewrapper Test <ELF_FILE>
```

## Générer la javadoc

```
javadoc -d doc elfp
```