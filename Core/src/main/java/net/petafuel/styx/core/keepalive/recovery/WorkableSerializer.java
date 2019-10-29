package net.petafuel.styx.core.keepalive.recovery;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;

import java.lang.reflect.Type;

public class WorkableSerializer implements JsonSerializer<WorkableTask> {

    @Override
    public JsonElement serialize(WorkableTask src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject recoveryTask = new JsonObject();
        recoveryTask.addProperty("class", src.getClass().getName());
        recoveryTask.addProperty("goal", src.getGoal());
        return recoveryTask;
    }
}
