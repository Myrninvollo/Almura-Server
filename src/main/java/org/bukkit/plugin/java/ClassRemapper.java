package org.bukkit.plugin.java;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;

/**
 * Re-maps class references from non-versioned packages to the correct versioned package
 */
public class ClassRemapper extends Remapper {
    private static final String[] PACKAGE_ROOTS = { "net/minecraft/server/", "org/bukkit/craftbukkit/" };
    private static final String version = "v1_6_R3"; //TODO: Keep updated for API version
    private static final ClassRemapper instance = new ClassRemapper();

    @Override
    public String mapDesc(String desc) {
        return filter(desc);
    }

    @Override
    public String map(String typeName) {
        return filter(typeName);
    }

    private static String filter(String text) {
        int idx;
        for (String packageRoot : PACKAGE_ROOTS) {
            if ((idx = text.indexOf(packageRoot)) != -1) {
                return convert(text, packageRoot, idx);
            }
        }
        return text;
    }

    private static String convert(String text, String packagePath, int startIndex) {
        String name = text.substring(startIndex + packagePath.length());
        String header = text.substring(0, startIndex);
        if (name.startsWith("v") && !name.contains("CraftServer")) {
            int firstidx = name.indexOf('_');
            if (firstidx != -1) {
                // Check if the major version is a valid number
                String major = name.substring(1, firstidx);
                try {
                    Integer.parseInt(major);
                    // Major test success
                    int end = name.indexOf('/');
                    if (end != -1) {
                        // Get rid of the version (removes 'v1_4_5.')
                        name = name.substring(end + 1);
                    }
                } catch (NumberFormatException ex) {
                    // Major test fail
                }
            }
        } else if (name.startsWith("CraftServer")) {
            // We keep the CraftServer on the versioned package path
            return header + packagePath  + version + "/" + name;
        }
        return header + packagePath + name;
    }

    public static byte[] remap(InputStream stream) throws IOException {
        ClassReader classReader = new ClassReader(stream);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        classReader.accept(new AlmuraRemappingClassAdapter(classWriter, instance), ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    public static Class<?> fakeForName(String name) throws ClassNotFoundException {
        return fakeForName(name, sun.reflect.Reflection.getCallerClass().getClassLoader());
    }

    public static Class<?> fakeForName(String name, ClassLoader loader) throws ClassNotFoundException {
        return fakeForName(name, true, loader);
    }

    public static Class<?> fakeForName(String name, boolean initialize) throws ClassNotFoundException {
        return fakeForName(name, sun.reflect.Reflection.getCallerClass().getClassLoader());
    }

    public static Class<?> fakeForName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
        return Class.forName(filter(name.replace('.', '/')).replace('/', '.'), initialize, loader);
    }
}
