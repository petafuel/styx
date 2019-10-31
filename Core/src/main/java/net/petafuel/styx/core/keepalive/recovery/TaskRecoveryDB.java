package net.petafuel.styx.core.keepalive.recovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.TaskFinalFailureCode;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import net.petafuel.styx.core.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * DB Layer for Task related stored functions
 */
public class TaskRecoveryDB {
    private final static Logger LOG = LogManager.getLogger(TaskRecoveryDB.class);

    /**
     * Mark a task as positioned in an execution queue
     *
     * @param task       task that is being queued
     * @param workerType worker type which the task should be executed by
     */
    public static void setQueued(WorkableTask task, WorkerType workerType) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call set_task_queued(?,?,?,?)}")) {
            query.setObject(1, task.getId());
            query.setString(2, task.getSignature());
            query.setString(3, workerType.toString());
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(WorkableTask.class, new WorkableSerializer())
                    .create();

            query.setString(4, gson.toJson(task, WorkableTask.class));

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to set task as queued in recovery db: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

    /**
     * Marks a task as currently being executed by a Worker Thread
     * @param task
     */
    public static void setRunning(WorkableTask task) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call set_task_running(?)}")) {
            query.setObject(1, task.getId());

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to set task as running in recovery db: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

    /**
     * Marks a task as being successfully done
     * @param task
     */
    public static void setDone(WorkableTask task) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call set_task_done(?)}")) {
            query.setObject(1, task.getId());

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to set task as done in recovery db: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

    /**
     * Marks a task as being ultimately failed. These task will not be attempted to be resolved by retrying execution
     *
     * @param id
     * @param failureText
     * @param failureCode
     */
    public static void setFinallyFailed(UUID id, String failureText, TaskFinalFailureCode failureCode) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call update_task_finally_failed(?,?,?)}")) {
            query.setObject(1, id);
            query.setInt(2, failureCode.getValue());
            query.setString(3, failureText);

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to set task as finally failed in recovery db: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

    /**
     * @param task
     * @param failureText
     * @param failureCode
     * @see TaskRecoveryDB#setFinallyFailed(UUID, String, TaskFinalFailureCode)
     */
    public static void setFinallyFailed(WorkableTask task, String failureText, TaskFinalFailureCode failureCode) {
        setFinallyFailed(task.getId(), failureText, failureCode);
    }

    /**
     * Changes the Worker type of the coresponding task
     * @param task
     * @param workerType
     */
    public static void changeWorker(WorkableTask task, WorkerType workerType) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call update_task_worker(?,?)}")) {
            query.setObject(1, task.getId());
            query.setString(2, workerType.toString());

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to update task worker type: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

    /**
     * Returns all persisted tasks that were previously marked as running/executing and were unable to be resolved
     * within the previous application runtime
     * @return
     */
    public static LinkedHashMap<WorkerType, WorkableTask> getInterruptedTasks() {
        Connection connection = Persistence.getInstance().getConnection();
        LinkedHashMap<WorkerType, WorkableTask> interruptedTasks = new LinkedHashMap<>();
        try (CallableStatement query = connection.prepareCall("{call get_interrupted_tasks()}")) {

            try (ResultSet resultSet = query.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    return interruptedTasks;
                }
                interruptedTasks = TaskRecoveryFactory.modelFromDatabase(resultSet);
            }
        } catch (SQLException e) {
            LOG.error("Unable to load interrupted tasks: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
        return interruptedTasks;
    }
    /**
     * Returns all persisted tasks that were previously marked as queued/waiting for execution and were not yet executed
     * by a Worker Thread within the previous application runtime
     * @return
     */
    public static LinkedHashMap<WorkerType, WorkableTask> getQueuedTasks() {
        Connection connection = Persistence.getInstance().getConnection();
        LinkedHashMap<WorkerType, WorkableTask> queuedTasks = new LinkedHashMap<>();
        try (CallableStatement query = connection.prepareCall("{call get_queued_tasks()}")) {

            try (ResultSet resultSet = query.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    return queuedTasks;
                }
                queuedTasks = TaskRecoveryFactory.modelFromDatabase(resultSet);
            }
        } catch (SQLException e) {
            LOG.error("Unable to load queued tasks: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
        return queuedTasks;
    }
}
