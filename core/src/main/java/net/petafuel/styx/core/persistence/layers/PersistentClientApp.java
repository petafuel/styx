package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.persistence.models.MasterToken;
import net.petafuel.styx.core.persistence.models.MasterTokenRestriction;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.GenericType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PersistentClientApp {
    private static final Logger LOG = LogManager.getLogger(PersistentClientApp.class);

    private PersistentClientApp() {
    }

    public static MasterToken get(String masterToken) {
        Connection connection = Persistence.getInstance().getConnection();
        MasterToken model = new MasterToken();
        try (CallableStatement query = connection.prepareCall("{call get_client_app(?)}")) {
            query.setString(1, masterToken);

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

    private static MasterToken dbToModel(ResultSet resultSet) throws SQLException {
        MasterToken model = new MasterToken();
        model.setName(resultSet.getString("name"));
        model.setToken(resultSet.getString("master_token"));
        model.setRedirectUrl(resultSet.getString("redirect_url"));
        model.setEnabled(resultSet.getBoolean("enabled"));
        model.setCreatedAt(resultSet.getTimestamp("created_at"));
        model.setUpdatedAt(resultSet.getTimestamp("updated_at"));

        String restrictions = resultSet.getString("restrictions");
        formatRestrictions(restrictions, model);

        return model;
    }

    private static void logSQLError(SQLException e) {
        LOG.error("Error executing SQL Query: {} SQL State: {}", e.getMessage(), e.getSQLState());
        throw new PersistenceException(e.getMessage(), e);
    }

    public static void formatRestrictions(String restrictions, MasterToken model) {
        if (restrictions != null) {
            try (Jsonb jsonb = JsonbBuilder.create()) {

                Type type = new GenericType<Map<String, MasterTokenRestriction>>() {
                }.getType();
                model.setRestrictions(jsonb.fromJson(restrictions, type));

            } catch (Exception e) {
                throw new SerializerException(e.getMessage(), e);
            }
        }
    }
}
