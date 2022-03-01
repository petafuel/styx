package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceEmptyResultSetException;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.persistence.models.AccessToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersistentAccessToken {
    private static final Logger LOG = LogManager.getLogger(PersistentAccessToken.class);

    private PersistentAccessToken() {
    }

    /**
     * @param accessToken this should be the hashed styx access token
     * @return accesstoken with metadata
     * @throws PersistenceException               if any sql error happened
     * @throws PersistenceEmptyResultSetException if there was no matching token found in the database
     */
    public static AccessToken get(String accessToken) {
        Connection connection = Persistence.getInstance().getConnection();
        AccessToken model = new AccessToken();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM get_token(?);")) {
            query.setString(1, accessToken);
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    model = dbToModel(resultSet);
                } else {
                    throw new PersistenceEmptyResultSetException("No entry found matching the specified token");
                }
            }
        } catch (SQLException e) {
            logSQLError(e);
        }
        return model;
    }

    /**
     * @param masterTokenHash - needs to be the hash of the master token
     * @param accessTokenHash - needs to be the hash of the access token
     * @param serviceType     - service type that will later be mapped to either ais, pis, piis or aispis
     * @param expiresIn       - default is 300
     * @param clientReference - optional reference
     * @return new access token + metadata
     */
    public static AccessToken create(String masterTokenHash, String accessTokenHash, String serviceType, Integer expiresIn, String clientReference) {
        Connection connection = Persistence.getInstance().getConnection();
        AccessToken model = new AccessToken();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM create_token(?, ?, ?, ?, ?);")) {
            query.setString(1, masterTokenHash);
            query.setString(2, accessTokenHash);
            query.setString(3, serviceType);
            query.setInt(4, expiresIn);
            query.setString(5, clientReference);
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    model = dbToModel(resultSet);
                }
            }
        } catch (SQLException e) {
            logSQLError(e);
        }
        return model;
    }

    public static AccessToken setValid(String tokenId, boolean valid) {
        Connection connection = Persistence.getInstance().getConnection();
        AccessToken model = new AccessToken();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM set_token_validity(?,?);")) {
            query.setString(1, tokenId);
            query.setBoolean(2, valid);
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    model = dbToModel(resultSet);
                }
            }
        } catch (SQLException e) {
            logSQLError(e);
        }
        return model;
    }

    public static void updateLastUsedOn(String tokenId) {
        Connection connection = Persistence.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM update_token_usage(?);")) {
            query.setString(1, tokenId);
            query.executeQuery();
        } catch (SQLException e) {
            logSQLError(e);
        }
    }

    private static AccessToken dbToModel(ResultSet resultSet) throws SQLException {
        AccessToken model = new AccessToken();
        model.setId(resultSet.getString("id"));
        model.setClientMasterToken(resultSet.getString("client_master_token"));
        model.setValid(resultSet.getBoolean("valid"));
        model.setCreatedAt(resultSet.getTimestamp("created_at"));
        model.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        model.setServiceType(resultSet.getString("service"));
        model.setExpiresIn(resultSet.getInt("expires_in"));
        model.setLastUsedOn(resultSet.getTimestamp("last_used_on"));
        model.setUsages(resultSet.getInt("usages"));
        model.setClientReference(resultSet.getString("client_reference"));
        return model;
    }

    private static void logSQLError(SQLException e) {
        LOG.error("Error executing SQL Query: {} SQL State: {}", e.getMessage(), e.getSQLState());
        throw new PersistenceException(e.getMessage(), e);
    }
}
