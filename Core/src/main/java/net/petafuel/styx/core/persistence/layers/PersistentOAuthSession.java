package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PersistentOAuthSession {

    private static final Logger LOG = LogManager.getLogger(PersistentOAuthSession.class);

    public OAuthSession create(OAuthSession model) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call create_oauth_session(?, ?, ?, ?, ?)}")) {
            query.setString(1, model.getAuthorizationEndpoint());
            query.setString(2, model.getTokenEndpoint());
            query.setString(3, model.getCodeVerifier());
            query.setString(4, model.getState());
            query.setString(5, model.getScope());

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


    public OAuthSession get(String state) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call get_oauth_session(?)}")) {
            query.setString(1, state);
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    return this.dbToModel(resultSet);
                }
            }
            throw new PersistenceException("No OAuth session found for the given state");
        } catch (SQLException e) {
            logSQLError(e);
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    public OAuthSession update(OAuthSession model) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call dev_update_oauth_session(?, ?, ?, ?, ?, ?)}")) { // TODO remove prefix dev_
            query.setString(1, model.getAccessToken());
            query.setString(2, model.getTokenType());
            query.setString(3, model.getRefreshToken());
            query.setTimestamp(4, new Timestamp(model.getAccessTokenExpiresAt().getTime()));
            query.setTimestamp(5, new Timestamp(model.getRefreshTokenExpiresAt().getTime()));
            query.setString(6, model.getState());
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    return this.dbToModel(resultSet);
                }
            }
            throw new PersistenceException("No OAuthSession found for the given state");
        } catch (SQLException e) {
            logSQLError(e);
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    private OAuthSession dbToModel(ResultSet resultSet) throws SQLException {
        OAuthSession model = new OAuthSession();
        model.setId(resultSet.getInt("id"));
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

    private void logSQLError(SQLException e) {
        LOG.error("Error executing SQL Query: {} SQL State: {}, StackTrace: {}", e.getMessage(), e.getSQLState(), e.getStackTrace());
        throw new PersistenceException(e.getMessage(), e);
    }
}
