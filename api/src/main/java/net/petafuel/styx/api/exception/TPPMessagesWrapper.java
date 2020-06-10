package net.petafuel.styx.api.exception;

import net.petafuel.styx.core.xs2a.entities.TPPMessageInformation;

import java.util.List;

public class TPPMessagesWrapper {
    private List<TPPMessageInformation> tppMessages;

    public List<TPPMessageInformation> getTppMessages() {
        return tppMessages;
    }

    public void setTppMessages(List<TPPMessageInformation> tppMessage) {
        this.tppMessages = tppMessage;
    }
}
