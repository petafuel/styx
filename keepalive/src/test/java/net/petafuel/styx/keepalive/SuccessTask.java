package net.petafuel.styx.keepalive;


import net.petafuel.styx.keepalive.contracts.WorkableTask;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.UUID;

public class SuccessTask extends WorkableTask {

    private final String signature;

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
        return Json.createObjectBuilder().build();
    }

    @Override
    public SuccessTask buildFromRecovery(JsonObject goal) {
        return new SuccessTask();
    }
}
