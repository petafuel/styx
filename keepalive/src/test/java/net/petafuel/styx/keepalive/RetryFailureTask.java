package net.petafuel.styx.core.keepalive.tasks;

import com.google.gson.JsonObject;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.TaskRetryFailureException;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.UUID;

public class RetryFailureTask extends WorkableTask {
    private final String signature;

    public RetryFailureTask() {
        this.signature = "RetryFailureTask" + UUID.randomUUID().toString();
    }

    @Override
    public String getSignature() {
        return this.signature;
    }

    @Override
    public void execute() {
        throw new TaskRetryFailureException("Test Task for retry failure");
    }

    @Override
    public JsonObject getGoal() {
        return Json.createObjectBuilder().build();
    }

    @Override
    public RetryFailureTask buildFromRecovery(JsonObject goal) {
        return new RetryFailureTask();
    }
}
