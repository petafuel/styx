package net.petafuel.styx.core.keepalive.tasks;

import com.google.gson.JsonObject;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TestSleepTask extends WorkableTask {
    private final static Logger LOG = LogManager.getLogger(TestSleepTask.class);

    private String signature;

    public TestSleepTask()
    {
        this.signature = "TestSleepTask" + UUID.randomUUID().toString();
    }

    @Override
    public String getSignature() {
        return this.signature;
    }

    @Override
    public void execute() {
        LOG.debug("Started test sleep task");
        try {
            int randomNum = ThreadLocalRandom.current().nextInt(500, 10000 + 1);
            Thread.sleep(randomNum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.debug("Test sleep task is done");
    }

    @Override
    public JsonObject getGoal() {
        return new JsonObject();
    }

    @Override
    public TestSleepTask buildFromRecovery(JsonObject goal) {
        return new TestSleepTask();
    }
}
