package net.petafuel.styx.keepalive.recovery;

import net.petafuel.styx.keepalive.contracts.WorkableTask;
import net.petafuel.styx.keepalive.entities.TaskFinalFailureCode;
import net.petafuel.styx.keepalive.entities.WorkerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
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

    //the full qualified classname is build by the Factory itself and only takes "input" from the database as trusted source
    @SuppressWarnings("squid:S1523")
    private static WorkableTask factory(String classname, JsonObject jsonGoal) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = Class.forName(classname);
        WorkableTask recoveredTask = (WorkableTask) clazz.getDeclaredConstructor().newInstance();

        recoveredTask = recoveredTask.buildFromRecovery(jsonGoal);
        return recoveredTask;
    }

    static Map<WorkableTask, WorkerType> modelFromDatabase(ResultSet resultSet) throws SQLException {
        LinkedHashMap<WorkableTask, WorkerType> tasks = new LinkedHashMap<>();
        while (resultSet.next()) {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String goal = resultSet.getString("goal");
            String classname = resultSet.getString("class");
            WorkerType type = WorkerType.valueOf(resultSet.getString("worker_type"));
            WorkableTask recoveredTask;
            try (Jsonb jsonb = JsonbBuilder.create()) {
                recoveredTask = TaskRecoveryFactory.factory(classname, jsonb.fromJson(goal, JsonObject.class));
                TaskRecoveryDB.setFinallyFailed(id, "Task was recovered and re-queued as new Task", TaskFinalFailureCode.RECOVERED_AND_QUEUED);
                tasks.put(recoveredTask, type);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                LOG.error("Unable to recover Task {}: {}", id, e.getMessage());
            } catch (NoSuchMethodException unavailableConstructor) {
                LOG.error("Unable to recover Task {}: Seems as if there is no empty public constructor available for reflection -> {}", id, unavailableConstructor.getMessage());
            } catch (Exception unknown) {
                TaskRecoveryDB.setFinallyFailed(id, "Unable to recover Task: " + unknown.getMessage(), TaskFinalFailureCode.UNABLE_TO_RECOVER);
                LOG.error("Unable to recover Task {} due to an unexpected exception: {}", id, unknown.getMessage());
            }
        }
        return tasks;
    }
}
