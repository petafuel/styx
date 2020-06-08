package net.petafuel.styx.core.keepalive.tasks;

import com.google.gson.JsonObject;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.UUID;

public class FreezeTask extends WorkableTask {
    private final String signature;

    public FreezeTask() {
        this.signature = "FreezeTask" + UUID.randomUUID().toString();
    }

    @Override
    public String getSignature() {
        return this.signature;
    }

    @Override
    public void execute() {
        try {
            Thread.sleep(900000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JsonObject getGoal() {
        return Json.createObjectBuilder().build();
    }

    @Override
    public FreezeTask buildFromRecovery(JsonObject goal) {
        return new FreezeTask();
    }
}
