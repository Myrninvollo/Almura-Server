package org.bukkit.plugin.java;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AlmuraMethodVisitor extends MethodVisitor {

    public AlmuraMethodVisitor(MethodVisitor parent) {
        super(Opcodes.ASM4, parent);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (owner.equals("java/lang/Class") && name.equals("forName")) {
            owner = "org/bukkit/plugin/java/ClassRemapper";
            name = "fakeForName";
        }
        super.visitMethodInsn(opcode, owner, name, desc);
    }
}
