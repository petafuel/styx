package net.petafuel.styx.updater;

import ch.qos.logback.classic.Level;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            if (isOperationRequested(args, "command=dump"))
            {
                String currentTimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

                String params[] = {
                        "--changeLogFile=dump-scheme-" + currentTimeStamp + ".xml",
                        "--logLevel=info",
                        "generateChangeLog",
                };

                liquibase.integration.commandline.Main.main(params);
            }
            else
            {
                setLogLevelInfo();
                Database database = openDbConnection();
                String changeLogFile = selectLiquibaseFile(args);
                Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
                liquibase.update(new Contexts(), new LabelExpression());
                System.out.println("All database changes run successfully");
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Selects liquibase entry point based on supplied command parameter
     * @param args
     * @return
     */
    protected static String selectLiquibaseFile(String ... args)
    {
        for(String argument : args) {
            if(argument.startsWith("command")) {
                switch(argument) {
                    case "command=init":
                        return "liquibase/master-init.xml";
                    case "command=update":
                        return "liquibase/master-update.xml";
                }
            }
        }
        throw new IllegalArgumentException("Unrecognized parameter provided. One of following supported: command=dump, command=init, command=update");
    }

    /**
     * Sets loglevel to INFO to display excecuted SQL queries
     */
    private static void setLogLevelInfo()
    {
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);
    }

    /**
     * Establishes database connection
     * @return Instance of JdbcConnection
     * @throws Exception if config files contents invalid credentials
     */
    private static Database openDbConnection() throws Exception
    {
        System.out.println(new File(".").getAbsoluteFile());
        try (InputStream input = new FileInputStream("liquibase.properties"))
        {
            // load a properties file
            Properties config = new Properties();
            config.load(input);
            Class<?> driver = Class.forName(config.getProperty("driver"));
            Connection connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password"));
            return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        }
        catch (FileNotFoundException ex)
        {
            throw new IllegalArgumentException("Configuration liquibase.properties file not found", ex);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Database connection failed", ex);
        }

    }

    /**
     * Verifies whether operation parameter were provided
     * @param arguments list of parameters programm started with
     * @param operation paramenter to be checked
     * @return true if desired parameter was provided, otherwise false
     */
    protected static boolean isOperationRequested(String[] arguments, String operation)
    {
        if (arguments.length == 0)
        {
            return false;
        }
        for (String argument : arguments)
        {
            if (operation.equalsIgnoreCase(argument))
            {
                checkConfigExists();
                return true;
            }
        }
        return false;
    }

    /**
     * Verfies whether properties file is available
     * @throws IllegalArgumentException if config file is not present
     */
    private static void checkConfigExists()
    {
        File path = new File(".");
        if (!new File("liquibase.properties").exists())
        {
            throw new IllegalArgumentException("Please ensure the config file liquibase.properties exists in the folder " + path.getAbsoluteFile());
        }
    }
}
