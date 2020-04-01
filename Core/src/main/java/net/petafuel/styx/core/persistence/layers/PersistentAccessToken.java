package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.persistence.models.AccessToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PersistentAccessToken {

    private static final Logger LOG = LogManager.getLogger(PersistentAccessToken.class);

    public AccessToken get(UUID accessToken) {
        Connection connection = Persistence.getInstance().getConnection();
        AccessToken model = new AccessToken();
        try (CallableStatement query = connection.prepareCall("{call get_token(?)}")) {
            query.setObject(1, accessToken);
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

    public AccessToken create(UUID masterToken, UUID accessToken) {
        Connection connection = Persistence.getInstance().getConnection();
        AccessToken model = new AccessToken();
        try (CallableStatement query = connection.prepareCall("{call create_token(?, ?)}")) {
            query.setObject(1, masterToken);
            query.setObject(2, accessToken);
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

    public AccessToken setValid(UUID tokenId, boolean valid) {
        Connection connection = Persistence.getInstance().getConnection();
        AccessToken model = new AccessToken();
        try (CallableStatement query = connection.prepareCall("{call set_token_validity(?,?)}")) {
            query.setObject(1, tokenId);
            query.setBoolean(2, valid);
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

    private AccessToken dbToModel(ResultSet resultSet) throws SQLException {
        AccessToken model = new AccessToken();
        model.setId(UUID.fromString(resultSet.getString("id")));
        model.setClientMasterToken(UUID.fromString(resultSet.getString("client_master_token")));
        model.setValid(resultSet.getBoolean("valid"));
        model.setCreatedAt(resultSet.getTimestamp("created_at"));
        model.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return model;
    }

    private void logSQLError(SQLException e) {
        LOG.error("Error executing SQL Query: {} SQL State: {}", e.getMessage(), e.getSQLState());
        throw new PersistenceException(e.getMessage(), e);
    }
}
