package net.petafuel.styx.core.persistence.layers;

import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.PersistenceException;
import net.petafuel.styx.core.persistence.StyxifySQL;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.persistence.models.PaymentEntry;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersistentPayment {
    private static final Logger LOG = LogManager.getLogger(PersistentPayment.class);
    private static final String COLUMN_CLIENT_TOKEN = "client_token";
    private static final String COLUMN_STATUS = "status";
    private static final String ERROR_MODEL_MAPPING = "Error mapping resultset to PaymentEntry.class message={}";

    private PersistentPayment() {
    }

    public static PaymentEntry create(String id, String paymentId, String clientToken, String bic, TransactionStatus status) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM create_payment(?,?,?,?,?)")) {

            query.setString(1, id);
            query.setString(2, paymentId);
            query.setString(3, clientToken);
            query.setString(4, bic);
            query.setString(5, status.name());

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = PersistentAccessToken.get(resultSet.getString(COLUMN_CLIENT_TOKEN));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing create_payment() for paymentId={} bic={} state={} message={}", paymentId, bic, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry getById(String id) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM get_payment_by_id(?)")) {

            query.setString(1, id);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    if (resultSet.getString(COLUMN_CLIENT_TOKEN) != null) {
                        AccessToken accessToken = PersistentAccessToken.get(resultSet.getString(COLUMN_CLIENT_TOKEN));
                        paymentEntry.setClientToken(accessToken);
                    }
                    if (resultSet.getString(COLUMN_STATUS) != null) {
                        paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                    }
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing get_payment_by_id() for id={} state={} message={}", id, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry getByPaymentId(String paymentId) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM get_payment_by_payment_id(?)")) {

            query.setString(1, paymentId);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    if (resultSet.getString(COLUMN_CLIENT_TOKEN) != null) {
                        AccessToken accessToken = PersistentAccessToken.get(resultSet.getString(COLUMN_CLIENT_TOKEN));
                        paymentEntry.setClientToken(accessToken);
                    }
                    if (resultSet.getString(COLUMN_STATUS) != null) {
                        paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                    }
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing get_payment_by_payment_id() for payment_id={} state={} message={}", paymentId, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry updateByPaymentId(String paymentId, String clientToken, String bic, TransactionStatus transactionStatus) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM update_payment_by_payment_id(?, ?, ?, ?)")) {

            query.setString(1, paymentId);
            query.setString(2, clientToken);
            query.setString(3, bic);
            query.setString(4, transactionStatus.name());

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = PersistentAccessToken.get(resultSet.getString(COLUMN_CLIENT_TOKEN));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing update_payment_by_payment_id() for paymentId={} bic={} state={} message={}", paymentId, bic, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry updateById(String id, String clientToken, String bic, TransactionStatus transactionStatus) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM update_payment_by_id(?, ?, ?, ?)")) {

            query.setString(1, id);
            query.setString(2, clientToken);
            query.setString(3, bic);
            query.setString(4, transactionStatus.name());

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = PersistentAccessToken.get(resultSet.getString(COLUMN_CLIENT_TOKEN));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing update_payment_by_id() for id={} bic={} state={} message={}", id, bic, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry updateStatusById(String id, TransactionStatus transactionStatus) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM update_payment_status_by_id(?, ?)")) {

            query.setString(1, id);
            query.setString(2, transactionStatus.name());

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = PersistentAccessToken.get(resultSet.getString(COLUMN_CLIENT_TOKEN));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing update_payment_status_by_id() for id={} state={} message={}", id, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry updateStatusByPaymentId(String paymentId, TransactionStatus transactionStatus) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM update_payment_status_by_payment_id(?, ?)")) {

            query.setString(1, paymentId);
            query.setString(2, transactionStatus.name());

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = PersistentAccessToken.get(resultSet.getString(COLUMN_CLIENT_TOKEN));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing update_payment_status_by_payment_id() for paymentId={} state={} message={}", paymentId, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry deleteByPaymentId(String paymentId) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM delete_payment_by_payment_id(?)")) {

            query.setString(1, paymentId);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = PersistentAccessToken.get(resultSet.getString(COLUMN_CLIENT_TOKEN));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing delete_payment_by_payment_id() for paymentId={} state={} message={}", paymentId, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }

    public static PaymentEntry deleteById(String id) {
        Connection connection = Persistence.getInstance().getConnection();
        PaymentEntry paymentEntry = null;
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM delete_payment_by_id(?)")) {

            query.setString(1, id);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    paymentEntry = StyxifySQL.fetchModel(PaymentEntry.class, resultSet);
                    AccessToken accessToken = PersistentAccessToken.get(resultSet.getString(COLUMN_CLIENT_TOKEN));
                    paymentEntry.setClientToken(accessToken);
                    paymentEntry.setStatus(TransactionStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
                }
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            LOG.error(ERROR_MODEL_MAPPING, e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error("Error executing delete_payment_by_id() for id={} state={} message={}", id, e.getSQLState(), e.getMessage());
            throw new PersistenceException(e.getMessage(), e);
        }
        return paymentEntry;
    }
}
