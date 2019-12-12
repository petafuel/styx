package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.persistence.models.ClientApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PersistentClientApp {

    private static final Logger LOG = LogManager.getLogger(PersistentClientApp.class);

    public ClientApp get(UUID masterToken) {
        Connection connection = Persistence.getInstance().getConnection();
        ClientApp model = new ClientApp();
        try (CallableStatement query = connection.prepareCall("{call get_client_app(?)}")) {
            query.setObject(1, masterToken);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    model = this.dbToModel(resultSet);
                }
            }
        } catch (SQLException e) {
            logSQLError(e);
        }
        return model;
    }

    private ClientApp dbToModel(ResultSet resultSet) throws SQLException {
        ClientApp model = new ClientApp();
        model.setName(resultSet.getString("name"));
        model.setMasterToken(UUID.fromString(resultSet.getString("master_token")));
        model.setRedirectUrl(resultSet.getString("redirect_url"));
        model.setEnabled(resultSet.getBoolean("enabled"));
        model.setCreatedAt(resultSet.getTimestamp("created_at"));
        model.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return model;
    }

    private void logSQLError(SQLException e) {
        LOG.error("Error executing SQL Query: {} SQL State: {}", e.getMessage(), e.getSQLState());
        throw new PersistenceException(e.getMessage(), e.getCause());
    }
}
