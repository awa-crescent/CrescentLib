package lib.crescent.utils.asm;

import org.objectweb.asm.MethodVisitor;
import lib.crescent.utils.asm.ByteCodeManipulator.MethodInfo;

public interface MethodOperator {
	public void modify(MethodInfo method_info,MethodVisitor method_visitor);
}
