package net.petafuel.styx.core.persistence.layers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.persistence.PersistentDatabaseInterface;
import net.petafuel.styx.core.xs2a.entities.Access;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.ConsentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class PersistentConsent implements PersistentDatabaseInterface<Consent> {

    private static final Logger LOG = LogManager.getLogger(PersistentConsent.class);

    /**
     * Save a consent model to the database
     *
     * @param consent Consent that will be inserted into the database
     * @return Consent that was created in the database
     */
    @Override
    public Consent create(Consent consent) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call create_consent(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}")) {
            setQueryValues(query, consent);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    consent = this.dbToModel(resultSet);
                }
            }
        } catch (SQLException e) {
            logSQLError(e);
        }
        return consent;
    }

    /**
     * get a consent model by id, id has to be set within the consent parameter
     *
     * @param consent A consent that must contain the consent id
     * @return Consent retrieved by the consent id
     */
    @Override
    public Consent get(Consent consent) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call get_consent(?)}")) {
            query.setString(1, consent.getId());
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    consent = this.dbToModel(resultSet);
                }
            }
        } catch (SQLException e) {
            logSQLError(e);
        }
        return consent;
    }

    /**
     * Update a consent model
     *
     * @param consent Consent with matching consent id in the database will be overwritten
     * @return Returns the updated Consent
     */
    @Override
    public Consent update(Consent consent) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call update_consent(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}")) {
            setQueryValues(query, consent);
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    consent = this.dbToModel(resultSet);
                }
            }
        } catch (SQLException e) {
            logSQLError(e);
        }
        return consent;
    }

    /**
     * Update the state of a consent
     *
     * @param consent Consent required to have at least the consent id set
     * @param state New state of the consent
     * @return Returns the consent with an updated state
     */
    public Consent updateState(Consent consent, Consent.State state) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call update_consent_state(?,?)}")) {
            query.setString(1, consent.getId()); // consent id
            query.setInt(2, state.getIndex()); // consent status

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    consent = this.dbToModel(resultSet);
                }
            }
        } catch (SQLException e) {
            logSQLError(e);
        }
        return consent;
    }

    /**
     * Delete a consent model from the database
     *
     * @param consent Consent must have at least the consent id defined
     * @return Returns the full Consent Model which was just deleted from the database
     */
    @Override
    public Consent delete(Consent consent) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call delete_consent(?)}")) {
            query.setString(1, consent.getId());
            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    consent = this.dbToModel(resultSet);
                }
            }
        } catch (SQLException e) {
            logSQLError(e);
        }
        return consent;
    }

    /**
     * Maps the query ResultSet to a consent model
     *
     * @param resultSet Result set of a selecting database function
     * @return Mapped Consent from the database columns
     * @throws SQLException If the expected database column is not available
     */
    private Consent dbToModel(ResultSet resultSet) throws SQLException {
        Consent consent = new Consent();
        consent.setId(resultSet.getString("id"));

        Gson gson = new GsonBuilder().serializeNulls().create();
        Access consentAccess = gson.fromJson(resultSet.getString("access"), Access.class);
        consent.getAccess().setTransactions(consentAccess.getTransactions());
        consent.getAccess().setBalances(consentAccess.getBalances());

        consent.setRecurringIndicator(resultSet.getBoolean("recurring_indicator"));
        consent.setValidUntil(new Date(resultSet.getTimestamp("valid_until").getTime()));
        consent.setLastUpdated(new Date(resultSet.getTimestamp("last_updated").getTime()));
        consent.setCreatedAt(new Date(resultSet.getTimestamp("created_at").getTime()));
        consent.setFrequencyPerDay(resultSet.getInt("frequency_per_day"));
        consent.setState(Consent.State.getByString(resultSet.getString("state")));
        consent.getSca().setApproach(SCA.Approach.valueOf(resultSet.getString("chosen_sca_method")));
        consent.setCombinedServiceIndicator(resultSet.getBoolean("combined_service_indicator"));
        consent.setxRequestId(UUID.fromString(resultSet.getString("x_request_id")));

        PSU psu = new PSU(resultSet.getString("psu_id"));
        psu.setIdType(resultSet.getString("psu_id_type"));
        psu.setIp(resultSet.getString("psu_ip_address"));
        psu.setPort(resultSet.getInt("psu_ip_port"));
        psu.setUserAgent(resultSet.getString("psu_user_agent"));
        psu.setGeoLocation(resultSet.getString("psu_geo_location"));
        psu.setCorporateId(resultSet.getString("psu_corporate_id"));
        psu.setCorporateIdType(resultSet.getString("psu_corporate_id_type"));
        consent.setPsu(psu);
        return consent;
    }

    /**
     * set all consent values in the database query, modifies the query parameter
     *
     * @param query current query object
     * @param consent Consent to retrieve the query parameters from
     * @throws SQLException in case the function call within the query does not match with the retrieved parameters
     */
    private void setQueryValues(CallableStatement query, Consent consent) throws SQLException {
        query.setString(1, consent.getId()); // consent id
        query.setInt(2, consent.getState().getIndex()); // consent status

        Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Consent.class, new ConsentSerializer()).create();
        query.setString(3, gson.toJsonTree(consent.getAccess()).toString()); // access string

        query.setBoolean(4, consent.isRecurringIndicator()); // recurring consent
        query.setTimestamp(5, new Timestamp(consent.getValidUntil().getTime())); // valid until
        query.setInt(6, consent.getFrequencyPerDay()); // frequency per day
        query.setInt(7, (consent.getSca().getApproach().ordinal() + 1)); // sca approach
        query.setBoolean(8, consent.isCombinedServiceIndicator()); // combined service
        query.setString(9, consent.getPsu().getId()); // psu id
        query.setString(10, consent.getPsu().getIp()); // psu ip
        query.setObject(11, consent.getPsu().getPort(), Types.INTEGER);  // psu port
        query.setString(12, consent.getPsu().getUserAgent()); // psu user agent
        query.setString(13, consent.getPsu().getGeoLocation()); // psu geo location
        query.setString(14, consent.getPsu().getIdType()); // psu id type
        query.setString(15, consent.getPsu().getCorporateId()); // psu corporate id
        query.setString(16, consent.getPsu().getCorporateIdType()); // psu corporate id type
        query.setObject(17, consent.getxRequestId()); // x request id
    }

    private void logSQLError(SQLException e) {
        LOG.error("Error executing SQL Query: {} SQL State: {}", e.getMessage(), e.getSQLState());
        throw new PersistenceException(e.getMessage(), e.getCause());
    }
}
