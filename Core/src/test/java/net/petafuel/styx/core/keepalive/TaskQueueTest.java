package net.petafuel.styx.core.keepalive;


import net.petafuel.styx.core.keepalive.tasks.FinalFailureTask;
import net.petafuel.styx.core.keepalive.tasks.FreezeTask;
import net.petafuel.styx.core.keepalive.tasks.RetryFailureTask;
import net.petafuel.styx.core.keepalive.tasks.SuccessTask;
import net.petafuel.styx.core.keepalive.threads.ThreadManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
public class TaskQueueTest {

    @Test
    public void testIdealTaskScenarios() {
        ThreadManager.getInstance().start();
        Assertions.assertAll(() -> {
            ThreadManager.getInstance().queueTask(new SuccessTask());
            ThreadManager.getInstance().queueTask(new RetryFailureTask());
            ThreadManager.getInstance().queueTask(new FreezeTask());
            ThreadManager.getInstance().queueTask(new FinalFailureTask());
        });
        Assertions.assertTrue(ThreadManager.getInstance().getCoreQueue().size() > 0);
    }
}
