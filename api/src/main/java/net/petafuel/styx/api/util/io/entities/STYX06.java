package net.petafuel.styx.api.util.io.entities;

import net.petafuel.styx.api.util.IOParser;
import net.petafuel.styx.api.util.io.contracts.ApplicableImplementerOption;
import net.petafuel.styx.api.util.io.contracts.IOOrder;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.XS2AResponse;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionsRequest;

/**
 * Sets the incoming Read Transaction Requests as an XML Request.
 * This will be handled in net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupAIS#getTransactionsByAccount(net.petafuel.styx.core.xs2a.contracts.AISRequest)
 */
public class STYX06 extends ApplicableImplementerOption {
    private static final String IO = "STYX06";

    public STYX06(IOParser ioParser) {
        super(ioParser);
    }

    @Override
    public boolean apply(XS2AFactoryInput xs2AFactoryInput, XS2ARequest xs2ARequest, XS2AResponse xs2AResponse) throws ImplementerOptionException {
        Boolean optionRequired = ioParser.getOption(IO, IOParser.Option.REQUIRED);
        //return immediately if this options is not required
        if (optionRequired == null || !optionRequired || !(xs2ARequest instanceof ReadTransactionsRequest)) {
            return false;
        }
        ReadTransactionsRequest request = (ReadTransactionsRequest) xs2ARequest;
        request.setXml(true);
        return true;
    }

    @Override
    public IOOrder order() {
        return IOOrder.POST_CREATION;
    }
}
