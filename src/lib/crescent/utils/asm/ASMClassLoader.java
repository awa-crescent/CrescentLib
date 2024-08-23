package lib.crescent.utils.asm;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ASMClassLoader extends ClassLoader {
	private byte[] bytecode;
	protected ClassWriter class_writer;
	protected ClassReader class_reader;

	public Class<?> findClass(String name) throws ClassNotFoundException {
		InputStream classData = getResourceAsStream(name.replace('.', '/') + ".class");
		if (classData == null) {
			throw new ClassNotFoundException("ASMClassLoader cannot find class " + name);
		}
		try {
			bytecode = classData.readAllBytes();
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "ASMClassLoader cannot read class " + name, ex);
		}
		class_reader = new ClassReader(bytecode);
		// class_write=new ClassWriter();
		modify();
		return defineClass(name, bytecode, 0, bytecode.length);
	}

	/**
	 * 子类重写该方法以修改字节码
	 */
	protected void modify() {

	}

	protected final void setClassModifier() {

	}
}
