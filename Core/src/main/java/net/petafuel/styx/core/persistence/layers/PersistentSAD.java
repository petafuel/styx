package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.persistence.StyxifySQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersistentSAD {
    private static final Logger LOG = LogManager.getLogger(PersistentSAD.class);

    private PersistentSAD() {
    }

    /**
     * Returns a fully initialized Aspsp.class object if the bic was found in SAD
     *
     * @param bic
     * @return
     */
    public static Aspsp getByBIC(String bic) {
        Connection connection = Persistence.getInstance().getConnection();
        Aspsp aspsp = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM get_bank_by_bic(?)")) {

            query.setString(1, bic);
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    aspsp = StyxifySQL.fetchModel(Aspsp.class, resultSet);
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            LOG.error("Error mapping resultset to Aspsp.class message={}", e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing get_bank_by_bic() for bic={} state={} message={}", bic, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return aspsp;
    }
}
