package net.petafuel.styx.core.persistence;

import net.petafuel.dbutils.DbHelper;
import net.petafuel.styx.core.xs2a.utils.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * General Database Connection Class
 *
 * <p>
 * Singleton, only one connection to the database is allowed<br>
 * <code>Connection connection = Persistence.getInstance().getConnection();</code>
 * </p>
 *
 * @since 1.0-SNAPSHOT
 */
public class Persistence {
    private static final Logger LOG = LogManager.getLogger(Persistence.class);
    private Connection databaseConnection;

    private Persistence() {
        connect();
    }

    public static Persistence getInstance() {
        return Holder.INSTANCE;
    }

    private void connect() {
        try {
            this.databaseConnection = new DbHelper().getConnection(Config.getInstance().getProperties().getProperty("persistence.dbname"));
        } catch (SQLException | NamingException e) {
            LOG.error("Error connecting to the database: {} \n {}", e.getMessage(), e.getCause());
            throw new PersistenceException(e.getMessage(), e);
        }
    }


    public Connection getConnection() {
        try {
            if (this.databaseConnection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            LOG.error("Unable to recreate database connection={}", e.getMessage());
        }
        return this.databaseConnection;
    }

    private static class Holder {
        private static final Persistence INSTANCE = new Persistence();
    }
}
