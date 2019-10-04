package net.petafuel.styx.api.v1.callback.control;

public class CallbackHandler {

    public void handle(String xRequestId) {

        System.out.println("Handle Request " + xRequestId);
    }
}
