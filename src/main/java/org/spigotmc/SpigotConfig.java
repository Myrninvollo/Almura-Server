package org.spigotmc;

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.command.TicksPerSecondCommand;

public class SpigotConfig
{

    private static final File CONFIG_FILE = new File( "spigot.yml" );
    private static final String HEADER = "This is the main configuration file for Spigot.\n"
            + "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
            + "with caution, and make sure you know what each option does before configuring.\n"
            + "For a reference for any variable inside this file, check out the Spigot wiki at\n"
            + "http://www.spigotmc.org/wiki/spigot-configuration/\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to Spigot,\n"
            + "join us at the IRC or drop by our forums and leave a post.\n"
            + "\n"
            + "IRC: #spigot @ irc.esper.net ( http://webchat.esper.net/?channel=spigot )\n"
            + "Forums: http://www.spigotmc.org/forum/\n";
    /*========================================================================*/
    static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;
    /*========================================================================*/
    private static Metrics metrics;

    public static void init()
    {
        config = YamlConfiguration.loadConfiguration( CONFIG_FILE );
        config.options().header( HEADER );
        config.options().copyDefaults( true );

        commands = new HashMap<String, Command>();

        version = getInt( "config-version", 3 );
        set( "config-version", 3 );
        readConfig( SpigotConfig.class, null );
    }

    public static void registerCommands()
    {
        for ( Map.Entry<String, Command> entry : commands.entrySet() )
        {
            MinecraftServer.getServer().server.getCommandMap().register( entry.getKey(), "Spigot", entry.getValue() );
        }

        if ( metrics == null )
        {
            try
            {
                metrics = new Metrics();
                metrics.start();
            } catch ( IOException ex )
            {
                Bukkit.getServer().getLogger().log( Level.SEVERE, "Could not start metrics service", ex );
            }
        }
    }

    static void readConfig(Class<?> clazz, Object instance)
    {
        for ( Method method : clazz.getDeclaredMethods() )
        {
            if ( Modifier.isPrivate( method.getModifiers() ) )
            {
                if ( method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE )
                {
                    try
                    {
                        method.setAccessible( true );
                        method.invoke( instance );
                    } catch ( InvocationTargetException ex )
                    {
                        Throwables.propagate( ex.getCause() );
                    } catch ( Exception ex )
                    {
                        Bukkit.getLogger().log( Level.SEVERE, "Error invoking " + method, ex );
                    }
                }
            }
        }

        try
        {
            config.save( CONFIG_FILE );
        } catch ( IOException ex )
        {
            Bukkit.getLogger().log( Level.SEVERE, "Could not save " + CONFIG_FILE, ex );
        }
    }

    private static void set(String path, Object val)
    {
        config.set( path, val );
    }

    private static boolean getBoolean(String path, boolean def)
    {
        config.addDefault( path, def );
        return config.getBoolean( path, config.getBoolean( path ) );
    }

    private static int getInt(String path, int def)
    {
        config.addDefault( path, def );
        return config.getInt( path, config.getInt( path ) );
    }

    private static <T> List getList(String path, T def)
    {
        config.addDefault( path, def );
        return (List<T>) config.getList( path, config.getList( path ) );
    }

    private static String getString(String path, String def)
    {
        config.addDefault( path, def );
        return config.getString( path, config.getString( path ) );
    }

    public static boolean preventProxies;
    private static void preventProxies()
    {
        preventProxies = getBoolean( "settings.prevent-proxies", false );
    }

    private static void tpsCommand()
    {
        commands.put( "tps", new TicksPerSecondCommand( "tps" ) );
    }

    public static class Listener
    {

        public String host;
        public int port;
        public boolean netty;
        public long connectionThrottle;

        public Listener(String host, int port, boolean netty, long connectionThrottle)
        {
            this.host = host;
            this.port = port;
            this.netty = netty;
            this.connectionThrottle = connectionThrottle;
        }
    }
    public static List<Listener> listeners = new ArrayList<Listener>();
    public static int nettyThreads;
    private static void listeners()
    {
        listeners.clear(); // We don't rebuild listeners on reload but we should clear them out!

        Map<String, Object> def = new HashMap<String, Object>();
        def.put( "host", "default" );
        def.put( "port", "default" );
        def.put( "netty", true );
        // def.put( "throttle", "default" );

        config.addDefault( "listeners", Collections.singletonList( def ) );
        for ( Map<String, Object> info : (List<Map<String, Object>>) config.getList( "listeners" ) )
        {
            String host = (String) info.get( "host" );
            if ( "default".equals( host ) )
            {
                host = Bukkit.getIp();
            } else
            {
                throw new IllegalArgumentException( "Can only bind listener to default! Configure it in server.properties" );
            }
            int port ;
            
            if (info.get( "port" ) instanceof Integer){
                throw new IllegalArgumentException( "Can only bind port to default! Configure it in server.properties");
            } else{
                port = Bukkit.getPort();
            }
            boolean netty = (Boolean) info.get( "netty" );
            // long connectionThrottle = ( info.get( "throttle" ) instanceof Number ) ? ( (Number) info.get( "throttle" ) ).longValue() : Bukkit.getConnectionThrottle();
            listeners.add( new Listener( host, port, netty, Bukkit.getConnectionThrottle() ) );
        }
        if ( listeners.size() != 1 )
        {
            throw new IllegalArgumentException( "May only have one listener!" );
        }

        nettyThreads = getInt( "settings.netty-threads", 3 );
    }
    public static List<String> bungeeAddresses = Arrays.asList( new String[]
    {
        "127.0.0.1"
    } );
    public static boolean bungee = true;
    private static void bungee()
    {
        bungeeAddresses = getList( "settings.bungeecord-addresses", bungeeAddresses );
        bungee = getBoolean( "settings.bungeecord", true );
    }
}
