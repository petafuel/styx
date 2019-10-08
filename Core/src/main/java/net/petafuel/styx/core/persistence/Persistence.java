package net.petafuel.styx.core.persistence;

import net.petafuel.dbutils.DbHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * General Database Connection Class
 * Singleton, we only need one connection for the database
 */
public class Persistence {
    private static final Logger LOG = LogManager.getLogger(Persistence.class);

    private static final String DATASOURCE_NAME = "prep_styx";
    private static Persistence singletonInstance;

    private Connection databaseConnection;

    private Persistence() {
        try {
            this.databaseConnection = new DbHelper().getConnection(DATASOURCE_NAME);
        } catch (SQLException | NamingException e) {
            LOG.error("Error connecting to the database: {} \n {}", e.getMessage(), e.getCause());
            throw new PersistenceException(e.getMessage(), e.getCause());
        }
    }

    public static Persistence getInstance() {

        if (Persistence.singletonInstance == null) {
            Persistence.singletonInstance = new Persistence();
        }

        try {
            //if connection was closed/interrupted within the singleton instance
            //try to reconnect to the database server
            if (!Persistence.singletonInstance.databaseConnection.isValid(1)) {
                Persistence.singletonInstance.databaseConnection.close();
                LOG.warn("Database connection was invalid, trying to reconnect...");
                Persistence.singletonInstance = new Persistence();
            }
        } catch (SQLException e) {
            LOG.error("Error trying to validate the database connection: {} \n {}", e.getMessage(), e.getCause());
            throw new PersistenceException(e.getMessage(), e.getCause());
        }
        return Persistence.singletonInstance;
    }

    public Connection getConnection() {
        return this.databaseConnection;
    }
}
