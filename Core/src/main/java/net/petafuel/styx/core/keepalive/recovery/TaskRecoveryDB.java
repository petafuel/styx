package net.petafuel.styx.core.keepalive.recovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import net.petafuel.styx.core.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskRecoveryDB {
    private final static Logger LOG = LogManager.getLogger(TaskRecoveryDB.class);

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

    public static void setRunning(WorkableTask task) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call set_task_running(?)}")) {
            query.setObject(1, task.getId());

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to set task as running in recovery db: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

    public static void setDone(WorkableTask task) {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call set_task_done(?)}")) {
            query.setObject(1, task.getId());

            query.executeQuery();
        } catch (SQLException e) {
            LOG.error("Unable to set task as done in recovery db: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
    }

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

    public static List<WorkableTask> getInterruptedTasks() {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call get_interrupted_tasks()}")) {

            try (ResultSet resultSet = query.executeQuery()) {
                //TODO factory new WorkableTasks
            }
        } catch (SQLException e) {
            LOG.error("Unable to load interrupted tasks: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
        return new ArrayList<>();
    }

    public static List<WorkableTask> getQueuedTasks() {
        Connection connection = Persistence.getInstance().getConnection();
        try (CallableStatement query = connection.prepareCall("{call get_queued_tasks()}")) {

            try (ResultSet resultSet = query.executeQuery()) {
//TODO factory new WorkableTasks
            }
        } catch (SQLException e) {
            LOG.error("Unable to load queued tasks: SQLState: {} Error: {}", e.getSQLState(), e.getMessage());
        }
        return new ArrayList<>();
    }
}
