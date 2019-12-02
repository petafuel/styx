package net.petafuel.styx.core.keepalive.recovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.TaskFinalFailureCode;
import net.petafuel.styx.core.keepalive.entities.WorkerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Factory for WorkableTasks. This can create Task Objects for recovered Tasks
 */
final class TaskRecoveryFactory {
    private static final Logger LOG = LogManager.getLogger(TaskRecoveryFactory.class);

    private TaskRecoveryFactory() {
    }

    private static WorkableTask factory(JsonObject jsonGoal) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz;
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(WorkableTask.class, new WorkableSerializer())
                .create();

        JsonElement goal = gson.fromJson(jsonGoal, JsonElement.class);
        String fullClassName = "net.petafuel.styx.core.keepalive.tasks." + goal.getAsJsonObject().get("class").getAsString();
        clazz = Class.forName(fullClassName);
        WorkableTask recoveredTask = (WorkableTask) clazz.getDeclaredConstructor().newInstance();

        recoveredTask = recoveredTask.buildFromRecovery(goal.getAsJsonObject().get("goal").getAsJsonObject());
        return recoveredTask;
    }

    public static Map<WorkableTask, WorkerType> modelFromDatabase(ResultSet resultSet) throws SQLException {
        LinkedHashMap<WorkableTask, WorkerType> tasks = new LinkedHashMap<>();
        while (resultSet.next()) {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String goal = resultSet.getString("goal");
            WorkerType type = WorkerType.valueOf(resultSet.getString("worker_type"));
            WorkableTask recoveredTask;
            try {
                Gson gson = new GsonBuilder().serializeNulls().create();
                recoveredTask = TaskRecoveryFactory.factory(gson.fromJson(goal, JsonObject.class));
                TaskRecoveryDB.setFinallyFailed(id, "Task was recovered and re-queued as new Task", TaskFinalFailureCode.RECOVERED_AND_QUEUED);
                tasks.put(recoveredTask, type);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                LOG.error("Unable to recover Task {}: {}", id, e.getMessage());
            } catch (Exception unknown) {
                TaskRecoveryDB.setFinallyFailed(id, "Unable to recover Task: " + unknown.getMessage(), TaskFinalFailureCode.UNABLE_TO_RECOVER);
                LOG.error("Unable to recover Task {} due to an unexpected exception: {}", id, unknown.getMessage());
            }
        }
        return tasks;
    }
}
