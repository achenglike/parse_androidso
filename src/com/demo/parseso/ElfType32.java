package com.demo.parseso;

import java.util.ArrayList;

public class ElfType32 {
	
	public elf32_rel rel;//重定位表项
	public elf32_rela rela;//重定位表项
	public ArrayList<Elf32_Sym> symList = new ArrayList<Elf32_Sym>();//符号表(Symbol Table)
	public elf32_hdr hdr;//elf头部信息
	public ArrayList<elf32_phdr> phdrList = new ArrayList<elf32_phdr>();//可能会有多个程序头
	public ArrayList<elf32_shdr> shdrList = new ArrayList<elf32_shdr>();//可能会有多个段头
	public ArrayList<elf32_strtb> strtbList = new ArrayList<elf32_strtb>();//可能会有多个字符串值
	
	public ElfType32() {
		rel = new elf32_rel();
		rela = new elf32_rela();
		hdr = new elf32_hdr();
	}
	
	/**
	 *  typedef struct elf32_rel {
		  Elf32_Addr	r_offset;
		  Elf32_Word	r_info;
		} Elf32_Rel;
	 *
	 */
	public class elf32_rel {
		public byte[] r_offset = new byte[4];
		public byte[] r_info = new byte[4];
		
		@Override
		public String toString(){
			return "r_offset:"+Utils.bytes2HexString(r_offset)+";r_info:"+Utils.bytes2HexString(r_info);
		}
	}
	
	/**
	 *  typedef struct elf32_rela{
		  Elf32_Addr	r_offset;
		  Elf32_Word	r_info;
		  Elf32_Sword	r_addend;
		} Elf32_Rela;
	 */
	public class elf32_rela{
		//此成员给出了重定位动作所适用的位置。对于一个可重定位文件而言， 此值是从节区头部开始到将被重定位影响的存储单位之间的字节偏 移。对于可执行文件或者共享目标文件而言，其取值是被重定位影响 到的存储单元的虚拟地址。
		public byte[] r_offset = new byte[4];
		//此成员给出要进行重定位的符号表索引，以及将实施的重定位类型。 例如一个调用指令的重定位项将包含被调用函数的符号表索引。如果 索引是 STN_UNDEF，那么重定位使用 0 作为“符号值”。重定位类型是和处理器相关的。当程序代码引用一个重定位项的重定位类型或 者符号表索引，则表示对表项的 r_info 成员应用 ELF32_R_TYPE 或 者 ELF32_R_SYM 的结果。#define ELF32_R_SYM(i) ((i)>>8) #define ELF32_R_TYPE(i) ((unsigned char)(i)) #define ELF32_R_INFO(s, t) (((s)<<8) + (unsigned char)(t))
		public byte[] r_info = new byte[4];
		//此成员给出一个常量补齐，用来计算将被填充到可重定位字段的数值。
		public byte[] r_addend = new byte[4];
		
		@Override
		public String toString(){
			return "r_offset:"+Utils.bytes2HexString(r_offset)+";r_info:"+Utils.bytes2HexString(r_info)+";r_addend:"+Utils.bytes2HexString(r_info);
		}
	}
	
	/**
	 * typedef struct elf32_sym{
		  Elf32_Word	st_name;
		  Elf32_Addr	st_value;
		  Elf32_Word	st_size;
		  unsigned char	st_info;
		  unsigned char	st_other;
		  Elf32_Half	st_shndx;
		} Elf32_Sym;
		符号表(Symbol Table)
	 */
	public static class Elf32_Sym{
		public byte[] st_name = new byte[4];//包含目标文件符号字符串表的索引，其中包含符号名的字符串表示。如 果该值非 0，则它表示了给出符号名的字符串表索引，否则符号表项没 有名称。注:外部 C 符号在 C 语言和目标文件的符号表中具有相同的名称。
		public byte[] st_value = new byte[4];//此成员给出相关联的符号的取值。依赖于具体的上下文，它可能是一个 绝对值、一个地址等等。
		public byte[] st_size = new byte[4];//很多符号具有相关的尺寸大小。例如一个数据对象的大小是对象中包含 的字节数。如果符号没有大小或者大小 知，则此成员为 0。
		public byte st_info;//符号类型和绑定信息，操纵方式
		public byte st_other;//该成员当前包含 0，其含义没有定义。
		public byte[] st_shndx = new byte[2];//每个符号表项都以和其他节区间的关系的方式给出定义。此成员给出相 关的节区头部表索引。某些索引具有特殊含义。
		
		@Override
		public String toString(){
			return "st_name:"+Utils.bytes2HexString(st_name)
					+"\nst_value:"+Utils.bytes2HexString(st_value)
					+"\nst_size:"+Utils.bytes2HexString(st_size)
					+"\nst_info:"+(st_info/16)
					+"\nst_other:"+(((short)st_other) & 0xF)
					+"\nst_shndx:"+Utils.bytes2HexString(st_shndx);
		}
	}
	
	public void printSymList(){
		for(int i=0;i<symList.size();i++){
			System.out.println();
			System.out.println("The "+(i+1)+" Symbol Table:");
			System.out.println(symList.get(i).toString());
		}
	}
	
	//Bind字段==》st_info
	public static final int STB_LOCAL = 0;
	public static final int STB_GLOBAL = 1;
	public static final int STB_WEAK = 2;
	//Type字段==》st_other
	public static final int STT_NOTYPE = 0;
	public static final int STT_OBJECT = 1;
	public static final int STT_FUNC = 2;
	public static final int STT_SECTION = 3;
	public static final int STT_FILE = 4;
	/**
	 * 这里需要注意的是还需要做一次转化
	 *  #define ELF_ST_BIND(x)	((x) >> 4)
		#define ELF_ST_TYPE(x)	(((unsigned int) x) & 0xf)
	 */
	
	/**
	 * typedef struct elf32_hdr{
		  unsigned char	e_ident[EI_NIDENT];
		  Elf32_Half	e_type;
		  Elf32_Half	e_machine;
		  Elf32_Word	e_version;
		  Elf32_Addr	e_entry;  // Entry point
		  Elf32_Off	e_phoff;
		  Elf32_Off	e_shoff;
		  Elf32_Word	e_flags;
		  Elf32_Half	e_ehsize;
		  Elf32_Half	e_phentsize;
		  Elf32_Half	e_phnum;
		  Elf32_Half	e_shentsize;
		  Elf32_Half	e_shnum;
		  Elf32_Half	e_shstrndx;
		} Elf32_Ehdr;
	 */
	public class elf32_hdr{
		public byte[] e_ident = new byte[16];
		public byte[] e_type = new byte[2];
		public byte[] e_machine = new byte[2];
		public byte[] e_version = new byte[4];
		public byte[] e_entry = new byte[4];
		public byte[] e_phoff = new byte[4];
		public byte[] e_shoff = new byte[4];
		public byte[] e_flags = new byte[4];
		public byte[] e_ehsize = new byte[2];
		public byte[] e_phentsize = new byte[2];
		public byte[] e_phnum = new byte[2];
		public byte[] e_shentsize = new byte[2];
		public byte[] e_shnum = new byte[2];
		public byte[] e_shstrndx = new byte[2];
		
		@Override
		public String toString(){
			return  "magic:"+ Utils.bytes2HexString(e_ident) 
					+"\ne_type:"+Utils.bytes2HexString(e_type)
					+"\ne_machine:"+Utils.bytes2HexString(e_machine)
					+"\ne_version:"+Utils.bytes2HexString(e_version)
					+"\ne_entry:"+Utils.bytes2HexString(e_entry)
					+"\ne_phoff:"+Utils.bytes2HexString(e_phoff)
					+"\ne_shoff:"+Utils.bytes2HexString(e_shoff)
					+"\ne_flags:"+Utils.bytes2HexString(e_flags)
					+"\ne_ehsize:"+Utils.bytes2HexString(e_ehsize)
					+"\ne_phentsize:"+Utils.bytes2HexString(e_phentsize)
					+"\ne_phnum:"+Utils.bytes2HexString(e_phnum)
					+"\ne_shentsize:"+Utils.bytes2HexString(e_shentsize)
					+"\ne_shnum:"+Utils.bytes2HexString(e_shnum)
					+"\ne_shstrndx:"+Utils.bytes2HexString(e_shstrndx);
		}
	}
	
	/**
	 * typedef struct elf32_phdr{
		  Elf32_Word	p_type;
		  Elf32_Off	p_offset;
		  Elf32_Addr	p_vaddr;
		  Elf32_Addr	p_paddr;
		  Elf32_Word	p_filesz;
		  Elf32_Word	p_memsz;
		  Elf32_Word	p_flags;
		  Elf32_Word	p_align;
		} Elf32_Phdr;
	 */
	public static class elf32_phdr{
		public byte[] p_type = new byte[4];
		public byte[] p_offset = new byte[4];
		public byte[] p_vaddr = new byte[4];
		public byte[] p_paddr = new byte[4];
		public byte[] p_filesz = new byte[4];
		public byte[] p_memsz = new byte[4];
		public byte[] p_flags = new byte[4];
		public byte[] p_align = new byte[4];
		
		@Override
		public String toString(){
			return "p_type:"+ Utils.bytes2HexString(p_type)
					+"\np_offset:"+Utils.bytes2HexString(p_offset)
					+"\np_vaddr:"+Utils.bytes2HexString(p_vaddr)
					+"\np_paddr:"+Utils.bytes2HexString(p_paddr)
					+"\np_filesz:"+Utils.bytes2HexString(p_filesz)
					+"\np_memsz:"+Utils.bytes2HexString(p_memsz)
					+"\np_flags:"+Utils.bytes2HexString(p_flags)
					+"\np_align:"+Utils.bytes2HexString(p_align);
		}
	}
	
	public void printPhdrList(){
		for(int i=0;i<phdrList.size();i++){
			System.out.println();
			System.out.println("The "+(i+1)+" Program Header:");
			System.out.println(phdrList.get(i).toString());
		}
	}
	
	/**
	 * typedef struct elf32_shdr {
		  Elf32_Word	sh_name;
		  Elf32_Word	sh_type;
		  Elf32_Word	sh_flags;
		  Elf32_Addr	sh_addr;
		  Elf32_Off	sh_offset;
		  Elf32_Word	sh_size;
		  Elf32_Word	sh_link;
		  Elf32_Word	sh_info;
		  Elf32_Word	sh_addralign;
		  Elf32_Word	sh_entsize;
		} Elf32_Shdr;
	 */
	public static class elf32_shdr{
		public byte[] sh_name = new byte[4];
		public byte[] sh_type = new byte[4];
		public byte[] sh_flags = new byte[4];
		public byte[] sh_addr = new byte[4];
		public byte[] sh_offset = new byte[4];
		public byte[] sh_size = new byte[4];
		public byte[] sh_link = new byte[4];
		public byte[] sh_info = new byte[4];
		public byte[] sh_addralign = new byte[4];
		public byte[] sh_entsize = new byte[4];
		
		@Override
		public String toString(){
			return "sh_name:"+Utils.bytes2HexString(sh_name)/*Utils.byte2Int(sh_name)*/
					+"\nsh_type:"+Utils.bytes2HexString(sh_type)
					+"\nsh_flags:"+Utils.bytes2HexString(sh_flags)
					+"\nsh_add:"+Utils.bytes2HexString(sh_addr)
					+"\nsh_offset:"+Utils.bytes2HexString(sh_offset)
					+"\nsh_size:"+Utils.bytes2HexString(sh_size)
					+"\nsh_link:"+Utils.bytes2HexString(sh_link)
					+"\nsh_info:"+Utils.bytes2HexString(sh_info)
					+"\nsh_addralign:"+Utils.bytes2HexString(sh_addralign)
					+"\nsh_entsize:"+ Utils.bytes2HexString(sh_entsize);
		}
	}
	
	/****************sh_type 节区类型********************/
	public static final int SHT_NULL = 0;
	public static final int SHT_PROGBITS = 1;
	public static final int SHT_SYMTAB = 2;
	public static final int SHT_STRTAB = 3;
	public static final int SHT_RELA = 4;
	public static final int SHT_HASH = 5;
	public static final int SHT_DYNAMIC = 6;
	public static final int SHT_NOTE = 7;
	public static final int SHT_NOBITS = 8;
	public static final int SHT_REL = 9;
	public static final int SHT_SHLIB = 10;
	public static final int SHT_DYNSYM = 11;
	public static final int SHT_NUM = 12;
	public static final int SHT_LOPROC = 0x70000000;
	public static final int SHT_HIPROC = 0x7fffffff;
	public static final int SHT_LOUSER = 0x80000000;
	public static final int SHT_HIUSER = 0xffffffff;
	public static final int SHT_MIPS_LIST = 0x70000000;
	public static final int SHT_MIPS_CONFLICT = 0x70000002;
	public static final int SHT_MIPS_GPTAB = 0x70000003;
	public static final int SHT_MIPS_UCODE = 0x70000004;
	
	/*****************sh_flagsh_flags 字段定义了一个节区中包含的内容是否可以修改、是否可以执行等信息。 如果一个标志位被设置，则该位取值为 1。 定义的各位都设置为 0***********************/
	public static final int SHF_WRITE = 0x1;
	public static final int SHF_ALLOC = 0x2;//此节区在进程执行过程中占用内存。某些控制节区并不出现于目标文件的内存映像中，对于那些节区，此位应设置为 0。
	public static final int SHF_EXECINSTR = 0x4;//节区包含可执行的机器指令。
	public static final int SHF_MASKPROC = 0xf0000000;//所有包含于此掩码中的四位都用于处理器专用的语义
	public static final int SHF_MIPS_GPREL = 0x10000000;
	
	public void printShdrList(){
		for(int i=0;i<shdrList.size();i++){
			System.out.println();
			System.out.println("The "+(i+1)+" Section Header:");
			System.out.println(shdrList.get(i));
		}
	}
	
	
	public static class elf32_strtb{
		public byte[] str_name;
		public int len;
		
		@Override
		public String toString(){
			return "str_name:"+str_name
					+"len:"+len;
		}
	}
}
