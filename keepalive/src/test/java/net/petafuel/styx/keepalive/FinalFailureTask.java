package net.petafuel.styx.keepalive;

import net.petafuel.styx.keepalive.contracts.WorkableTask;
import net.petafuel.styx.keepalive.entities.TaskFinalFailureException;

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
