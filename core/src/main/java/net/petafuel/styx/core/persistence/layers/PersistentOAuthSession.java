package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceEmptyResultSetException;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class PersistentOAuthSession {
    private static final Logger LOG = LogManager.getLogger(PersistentOAuthSession.class);

    private PersistentOAuthSession() {
    }

    public static OAuthSession create(OAuthSession model) {
        Connection connection = Persistence.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM create_oauth_session(?, ?, ?, ?, ?, ?, ?)")) {
            query.setString(1, model.getAuthorizationEndpoint());
            query.setString(2, model.getTokenEndpoint());
            query.setString(3, model.getCodeVerifier());
            query.setString(4, model.getState());
            query.setString(5, model.getScope());
            query.setObject(6, model.getId());
            query.setObject(7, model.getxRequestId());

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

    /**
     * Retrieve an existing oauth session from the database using the state attribute
     *
     * @param state state
     * @return found oauth session
     * @throws PersistenceEmptyResultSetException in case there was no match within the database
     * @throws PersistenceException               if an unexpected SQL Error occurred
     */
    public static OAuthSession getByState(String state) {
        Connection connection = Persistence.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM get_oauth_session_by_state(?)")) {
            query.setString(1, state);
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    return dbToModel(resultSet);
                }
            }
            throw new PersistenceEmptyResultSetException("No OAuth session found for the given state");
        } catch (SQLException e) {
            logSQLError(e);
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    /**
     * Retrieve an existing oauth session from the database using the id
     *
     * @param uuid (used as a preauthId during the pre-step)
     * @return found oauth session
     * @throws PersistenceEmptyResultSetException in case there was no match within the database
     * @throws PersistenceException               if an unexpected SQL Error occurred
     */
    public static OAuthSession getById(UUID uuid) {
        Connection connection = Persistence.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM get_oauth_session_by_id(?)")) {
            query.setObject(1, uuid);
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    return dbToModel(resultSet);
                }
            }
            throw new PersistenceEmptyResultSetException("No OAuth session found for the given id");
        } catch (SQLException e) {
            logSQLError(e);
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    /**
     * Retrieve an existing oauth session from the database using the x_request_id
     *
     * @param uuid (X-Request-id used during the creation of a consent or payment)
     * @return found oauth session
     * @throws PersistenceEmptyResultSetException in case there was no match within the database
     * @throws PersistenceException               if an unexpected SQL Error occurred
     */
    public static OAuthSession getByXRequestId(UUID uuid) {
        Connection connection = Persistence.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM get_oauth_session_by_x_request_id(?)")) {
            query.setObject(1, uuid);
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    return dbToModel(resultSet);
                }
            }
            throw new PersistenceEmptyResultSetException("No OAuth session found for the given x_request_id");
        } catch (SQLException e) {
            logSQLError(e);
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    public static OAuthSession update(OAuthSession model) {
        Connection connection = Persistence.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM update_oauth_session(?, ?, ?, ?, ?, ?)")) {
            query.setString(1, model.getAccessToken());
            query.setString(2, model.getTokenType());
            query.setString(3, model.getRefreshToken());
            query.setTimestamp(4, new Timestamp(model.getAccessTokenExpiresAt().getTime()));
            query.setTimestamp(5, new Timestamp(model.getRefreshTokenExpiresAt().getTime()));
            query.setString(6, model.getState());
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    return dbToModel(resultSet);
                }
            }
            throw new PersistenceException("No OAuthSession found for the given state");
        } catch (SQLException e) {
            logSQLError(e);
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    private static OAuthSession dbToModel(ResultSet resultSet) throws SQLException {
        OAuthSession model = new OAuthSession();
        model.setId(UUID.fromString(resultSet.getString("id")));
        if (resultSet.getString("x_request_id") != null) {
            model.setxRequestId(UUID.fromString(resultSet.getString("x_request_id")));
        }
        model.setAuthorizationEndpoint(resultSet.getString("authorization_endpoint"));
        model.setTokenEndpoint(resultSet.getString("token_endpoint"));
        model.setCodeVerifier(resultSet.getString("code_verifier"));
        model.setState(resultSet.getString("state"));
        model.setScope(resultSet.getString("scope"));
        model.setAccessToken(resultSet.getString("access_token"));
        model.setTokenType(resultSet.getString("token_type"));
        model.setRefreshToken(resultSet.getString("refresh_token"));
        model.setAccessTokenExpiresAt(resultSet.getTimestamp("access_token_expires_at"));
        model.setRefreshTokenExpiresAt(resultSet.getTimestamp("refresh_token_expires_at"));
        model.setAuthorizedAt(resultSet.getTimestamp("authorized_at"));
        model.setCreatedAt(resultSet.getTimestamp("created_at"));
        return model;
    }

    private static void logSQLError(SQLException e) {
        LOG.error("Error executing SQL Query: {} SQL State: {}, StackTrace: {}", e.getMessage(), e.getSQLState(), e.getStackTrace());
        throw new PersistenceException(e.getMessage(), e);
    }
}
