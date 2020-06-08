package net.petafuel.styx.core.keepalive.tasks;

import com.google.gson.JsonObject;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.TaskFinalFailureException;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.UUID;

public class FinalFailureTask extends WorkableTask {
    private final String signature;

    public FinalFailureTask() {
        this.signature = "FinalFailureTask" + UUID.randomUUID().toString();
    }

    @Override
    public String getSignature() {
        return this.signature;
    }

    @Override
    public void execute() {
        throw new TaskFinalFailureException("Test Task for final failure");
    }

    @Override
    public JsonObject getGoal() {
        return Json.createObjectBuilder().build();
    }

    @Override
    public FinalFailureTask buildFromRecovery(JsonObject goal) {
        return new FinalFailureTask();
    }
}
