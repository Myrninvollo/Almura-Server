package org.bukkit.plugin.java;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.net.JarURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
public class PluginClassLoader extends URLClassLoader {
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private static final CodeSigner[] emptySigner = new CodeSigner[0];

    public PluginClassLoader(final JavaPluginLoader loader, final URL[] urls, final ClassLoader parent) {
        super(urls, parent);

        this.loader = loader;
    }

    @Override
    public void addURL(URL url) { // Override for access level!
        super.addURL(url);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    private void ensurePackageLoaded(String name) {
        int i = name.lastIndexOf('.');
        if (i != -1) {
            String pkgname = name.substring(0, i);
            // If not yet defined, define it
            if (getPackage(pkgname) == null) {
                definePackage(pkgname, null, null, null, null, null, null, null);
            }
        }
    }

    protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                try {
                    // Ensure that the package is loaded
                    ensurePackageLoaded(name);

                    // Load the resource to the name
                    String path = name.replace('.', '/').concat(".class");
                    URL res = this.findResource(path);
                    if (res != null) {
                        InputStream stream = res.openStream();
                        if (stream != null) {
                            // Remap the classes
                            byte[] data = ClassRemapper.remap(stream);
                            // Define (create) the class using the modified byte code
                            // The top-child class loader is used for this to prevent access violations
                            // Set the codesource to the jar, not within the jar, for compatibility with
                            // plugins that do new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()))
                            // instead of using getResourceAsStream - see https://github.com/AlmuraDev/Libigot/issues/5
                            JarURLConnection jarURLConnection = (JarURLConnection) res.openConnection(); // parses only
                            URL jarURL = jarURLConnection.getJarFileURL();
                            CodeSource cs = new CodeSource(jarURL, emptySigner);
                            result = this.defineClass(name, data, 0, data.length, cs);
                            if (result != null) {
                                // Resolve it - sets the class loader of the class
                                this.resolveClass(result);
                            }
                        }
                    }
                } catch (Throwable t) {
                    //t.printStackTrace();
                    throw new ClassNotFoundException("Failed to remap class "+ name, t);
            	}

                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    public Set<String> getClasses() {
        return classes.keySet();
    }
}
