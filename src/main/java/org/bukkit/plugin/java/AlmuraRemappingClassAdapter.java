package org.bukkit.plugin.java;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

public class AlmuraRemappingClassAdapter extends RemappingClassAdapter {

    public AlmuraRemappingClassAdapter(ClassVisitor visitor, Remapper remapper) {
        super(visitor, remapper);
    }

    @Override
    protected MethodVisitor createRemappingMethodAdapter(int arg0, String arg1,
            MethodVisitor parent) {
        return super.createRemappingMethodAdapter(arg0, arg1, new AlmuraMethodVisitor(parent));
    }
}
