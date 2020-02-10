package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.persistence.StyxifySQL;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.persistence.models.PaymentEntry;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PersistentPayment {
    private static final Logger LOG = LogManager.getLogger(PersistentPayment.class);
    private static final String COLUMN_CLIENT_TOKEN = "client_token";
    private static final String COLUMN_STATUS = "status";
    private static final String ERROR_MODEL_MAPPING = "Error mapping resultset to PaymentEntry.class message={}";

    private PersistentPayment() {
    }

    public static PaymentEntry create(String paymentId, UUID clientToken, String bic, TransactionStatus status) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM create_payment(?,?,?,?)")) {

            query.setString(1, paymentId);
            query.setObject(2, clientToken);
            query.setString(3, bic);
            query.setString(4, status.name());

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = new PersistentAccessToken().get(UUID.fromString(resultSet.getString(COLUMN_CLIENT_TOKEN)));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing create_payment() for paymentId={} bic={} state={} message={}", paymentId, bic, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry get(String paymentId) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM get_payment(?)")) {

            query.setString(1, paymentId);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    if (resultSet.getString(COLUMN_CLIENT_TOKEN) != null) {
                        AccessToken accessToken = new PersistentAccessToken().get(UUID.fromString(resultSet.getString(COLUMN_CLIENT_TOKEN)));
                        paymentEntry.setClientToken(accessToken);
                    }
                    if (resultSet.getString(COLUMN_STATUS) != null) {
                        paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                    }
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing get_payment() for id={} state={} message={}", paymentId, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry update(String paymentId, UUID clientToken, String bic, TransactionStatus transactionStatus) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM update_payment(?, ?, ?, ?)")) {

            query.setString(1, paymentId);
            query.setObject(2, clientToken);
            query.setString(3, bic);
            query.setString(4, transactionStatus.name());

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = new PersistentAccessToken().get(UUID.fromString(resultSet.getString(COLUMN_CLIENT_TOKEN)));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing update_payment() for paymentId={} bic={} state={} message={}", paymentId, bic, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry updateStatus(String paymentId, TransactionStatus transactionStatus) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM update_payment_status(?, ?)")) {

            query.setString(1, paymentId);
            query.setString(2, transactionStatus.name());

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = new PersistentAccessToken().get(UUID.fromString(resultSet.getString(COLUMN_CLIENT_TOKEN)));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing update_payment_status() for paymentId={} state={} message={}", paymentId, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry delete(String paymentId) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM delete_payment(?)")) {

            query.setString(1, paymentId);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = new PersistentAccessToken().get(UUID.fromString(resultSet.getString(COLUMN_CLIENT_TOKEN)));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing delete_payment() for paymentId={} state={} message={}", paymentId, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }
}
