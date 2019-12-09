package net.petafuel.styx.core.keepalive.recovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.TaskFinalFailureCode;
import net.petafuel.styx.core.keepalive.entities.TaskState;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import net.petafuel.styx.core.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * DB Layer for Task related stored functions
 */
public final class TaskRecoveryDB {
    private static final Logger LOG = LogManager.getLogger(TaskRecoveryDB.class);

    private TaskRecoveryDB() {
    }

    /**
     * Mark a task as positioned in an execution queue
     *
     * @param task       task that is being queued
     * @param workerType worker type which the task should be executed by
     */
    public static void createTask(WorkableTask task, WorkerType workerType) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call create_task(?,?,?,?)}")) {
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
     * Change current state of a task
     *
     * @param taskId Requires a task id for identification
     * @param state  A state this task should be changed to
     */
    public static void updateState(UUID taskId, TaskState state) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call update_task_state(?, ?)}")) {
            query.setObject(1, taskId);
            query.setString(2, state.toString());

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to set task as running in recovery db: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

    /**
     * Marks a task as being ultimately failed. These task will not be attempted to be resolved by retrying execution
     *
     * @param taskId      Corresponding task id
     * @param failureText A variable text that should describe the error which lead to a final failure
     * @param failureCode A unique code that can identify the error
     * @see TaskFinalFailureCode
     */
    public static void setFinallyFailed(UUID taskId, String failureText, TaskFinalFailureCode failureCode) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call update_task_finally_failed(?,?,?)}")) {
            query.setObject(1, taskId);
            query.setInt(2, failureCode.getValue());
            query.setString(3, failureText);

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to set task as finally failed in recovery db: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

    /**
     * Changes the Worker type of the coresponding task
     *
     * @param taskId     Id of the designated task
     * @param workerType Type of the worker which this task should be executed by
     * @see WorkerType
     */
    public static void updateWorker(UUID taskId, WorkerType workerType) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call update_task_worker(?,?)}")) {
            query.setObject(1, taskId);
            query.setString(2, workerType.toString());

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to update task worker type: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

    /**
     * Increment the execution counter for one task to avoid excessive retry-failure execution of a task
     *
     * @param taskId id of the corresponding task
     * @return returns the already incremented execution counter
     */
    public static int incrementExecutionCounter(UUID taskId) {
        Connection connection = Persistence.getInstance().getConnection();
        int retryAmount = 0;
        try (CallableStatement query = connection.prepareCall("{call increment_task_execution_counter(?)}")) {
            query.setObject(1, taskId);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    retryAmount = resultSet.getInt("increment_task_execution_counter");
                }
            }
        } catch (SQLException e) {
            LOG.error("Unable to increment execution counter: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
        return retryAmount;
    }

    /**
     * Returns all persisted tasks that were previously marked as running/executing and were unable to be resolved
     * within the previous application runtime
     *
     * @return Returns a map of K: Task Object | V: Assigned Worker Type
     */
    public static Map<WorkableTask, WorkerType> getInterruptedTasks() {
        Connection connection = Persistence.getInstance().getConnection();
        Map<WorkableTask, WorkerType> interruptedTasks = new LinkedHashMap<>();
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
     *
     * @return Returns a map of K: Task Object | V: Assigned Worker Type
     */
    public static Map<WorkableTask, WorkerType> getQueuedTasks() {
        Connection connection = Persistence.getInstance().getConnection();
        Map<WorkableTask, WorkerType> queuedTasks = new LinkedHashMap<>();
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
