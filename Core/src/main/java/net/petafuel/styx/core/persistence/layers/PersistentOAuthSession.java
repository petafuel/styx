package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class PersistentOAuthSession {

    private static final Logger LOG = LogManager.getLogger(PersistentOAuthSession.class);

    public OAuthSession create(OAuthSession model) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call create_oauth_session(?, ?, ?, ?, ?, ?)}")) {
            query.setString(1, model.getTppRedirectUrl());
            query.setString(2, model.getAuthorizationEndpoint());
            query.setString(3, model.getTokenEndpoint());
            query.setString(4, model.getCodeVerifier());
            query.setString(5, model.getState());
            query.setString(6, model.getScope());

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
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()) {
                return this.dbToModel(resultSet);
            }
            throw new PersistenceException("No OAuth session found for the given state");
        } catch (SQLException e) {
            logSQLError(e);
            throw new PersistenceException(e.getMessage(), e.getCause());
        }
    }

    public OAuthSession update(OAuthSession model) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call update_oauth_session(?, ?, ?, ?, ?)}")) {
            query.setString(1, model.getAccessToken());
            query.setString(2, model.getTokenType());
            query.setString(3, model.getRefreshToken());
            query.setTimestamp(4, new Timestamp(model.getExpiresAt().getTime()));
            query.setString(5, model.getState());
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()) {
                return this.dbToModel(resultSet);
            }
            throw new PersistenceException("No OAuthSession found for the given state");
        } catch (SQLException e) {
            logSQLError(e);
            throw new PersistenceException(e.getMessage(), e.getCause());
        }
    }

    private OAuthSession dbToModel(ResultSet resultSet) throws SQLException {
        OAuthSession model = new OAuthSession();
        model.setId(resultSet.getInt("id"));
        model.setTppRedirectUrl(resultSet.getString("tpp_redirect_uri"));
        model.setAuthorizationEndpoint(resultSet.getString("authorization_endpoint"));
        model.setTokenEndpoint(resultSet.getString("token_endpoint"));
        model.setCodeVerifier(resultSet.getString("code_verifier"));
        model.setState(resultSet.getString("state"));
        model.setScope(resultSet.getString("scope"));
        model.setAccessToken(resultSet.getString("access_token"));
        model.setTokenType(resultSet.getString("token_type"));
        model.setRefreshToken(resultSet.getString("refresh_token"));
        model.setExpiresAt(resultSet.getTimestamp("expires_at"));
        model.setAuthorizedAt(resultSet.getTimestamp("authorized_at"));
        model.setCreatedAt(resultSet.getTimestamp("created_at"));
        return model;
    }

    private void logSQLError(SQLException e) {
        LOG.error("Error executing SQL Query: {} SQL State: {}, StackTrace: {}", e.getMessage(), e.getSQLState(), e.getStackTrace());
        throw new PersistenceException(e.getMessage(), e.getCause());
    }
}
