package net.petafuel.styx.core.keepalive;

import net.petafuel.styx.core.keepalive.tasks.FinalFailureTask;
import net.petafuel.styx.core.keepalive.tasks.FreezeTask;
import net.petafuel.styx.core.keepalive.tasks.RetryFailureTask;
import net.petafuel.styx.core.keepalive.tasks.SuccessTask;
import net.petafuel.styx.core.keepalive.threads.ThreadManager;
import org.junit.jupiter.api.Test;

public class TaskQueueTest {

    @Test
    public void testIdealTaskScenarios() {
        ThreadManager.getInstance().start();
        ThreadManager.getInstance().queueTask(new SuccessTask());
        ThreadManager.getInstance().queueTask(new RetryFailureTask());
        ThreadManager.getInstance().queueTask(new FreezeTask());
        ThreadManager.getInstance().queueTask(new FinalFailureTask());
    }
}
