package net.petafuel.styx.core.keepalive.tasks;

import com.google.gson.JsonObject;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;

import java.util.UUID;

public class SuccessTask extends WorkableTask {

    private String signature;

    public SuccessTask()
    {
        this.signature = "SuccessTask" + UUID.randomUUID().toString();
    }

    @Override
    public String getSignature() {
        return this.signature;
    }

    @Override
    public void execute() {
        //Do nothing
        return;
    }

    @Override
    public JsonObject getGoal() {
        return new JsonObject();
    }

    @Override
    public SuccessTask buildFromRecovery(JsonObject goal) {
        return new SuccessTask();
    }
}
