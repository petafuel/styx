package net.petafuel.styx.core.keepalive.contracts;

import com.google.gson.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Defines an executable task for a Worker
 */
public abstract class WorkableTask {

    private final UUID id;

    protected WorkableTask() {
        id = UUID.randomUUID();
    }

    /**
     * Taskid
     * @return Returns the task id
     */
    public final UUID getId() {
        return id;
    }

    /**
     * Should contain a unique signature of the task
     * @return TaskSignature
     */
    public abstract String getSignature();

    public abstract void execute();

    public abstract JsonObject getGoal();

    public abstract WorkableTask buildFromRecovery(JsonObject goal) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;
}
