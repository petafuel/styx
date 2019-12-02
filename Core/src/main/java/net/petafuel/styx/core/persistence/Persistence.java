package net.petafuel.styx.core.persistence;

import net.petafuel.dbutils.DbHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * General Database Connection Class
 *
 * <p>
 *     Singleton, only one connection to the database is allowed<br>
 *     <code>Connection connection = Persistence.getInstance().getConnection();</code>
 * </p>
 * @since 1.0-SNAPSHOT
 */
public class Persistence {
    private static final Logger LOG = LogManager.getLogger(Persistence.class);

    private static final String DATASOURCE_NAME = "prep_styx";
    private static Persistence singletonInstance;

    private Connection databaseConnection;

    private static class Holder {
        private static final Persistence INSTANCE = new Persistence();
    }

    private Persistence() {
        try {
            this.databaseConnection = new DbHelper().getConnection(DATASOURCE_NAME);
        } catch (SQLException | NamingException e) {
            LOG.error("Error connecting to the database: {} \n {}", e.getMessage(), e.getCause());
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    public static Persistence getInstance() {
        return Holder.INSTANCE;
    }

    public Connection getConnection() {
        return this.databaseConnection;
    }
}
