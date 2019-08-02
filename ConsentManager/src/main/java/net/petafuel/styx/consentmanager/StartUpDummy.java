package net.petafuel.styx.consentmanager;

import net.petafuel.styx.consentmanager.consent.berlingroup.control.BerlinGroupConsentService;
import net.petafuel.styx.consentmanager.consent.berlingroup.entity.ConsentCreateModel;
import net.petafuel.styx.consentmanager.consent.berlingroup.entity.ConsentCreateRequest;
import net.petafuel.styx.utils.banklookup.BankInterfaceDescription;
import net.petafuel.styx.utils.standards.berlingroup.BerlinGroupSigner;
import net.petafuel.styx.utils.standards.berlingroup.BerlinGroupStandard;
import net.petafuel.styx.utils.standards.berlingroup.entity.Customer;
import net.petafuel.styx.utils.standards.berlingroup.entity.PSU;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

public class StartUpDummy
{
    public static void main(String[] args) throws IOException
    {

        BerlinGroupSigner berlinGroupSigner = new BerlinGroupSigner();
        BerlinGroupStandard berlinGroupStandard = new BerlinGroupStandard();
        BankInterfaceDescription styxSADBerlinGroup = new BankInterfaceDescription(new URL("https://xs2a-test.fiduciagad.de/xs2a/"), berlinGroupStandard);

        BerlinGroupConsentService service = new BerlinGroupConsentService(berlinGroupSigner, styxSADBerlinGroup);



        ConsentCreateModel consentCreateModel = new ConsentCreateModel(
                new PSU("PSU-1234"),
                new Date(),
                4,
                false,
                false);

        consentCreateModel.setxRequestId(String.valueOf(UUID.randomUUID()));
        consentCreateModel.setDate(new Date());

        Customer reoccuringCustomer = new Customer("DE40100100103307118608", Customer.CustomerType.IBAN);
        consentCreateModel.getBalances().add(reoccuringCustomer);
        consentCreateModel.getTransactions().add(reoccuringCustomer);

        consentCreateModel.getBalances().add(new Customer("DE02100100109307118603", Customer.CustomerType.IBAN));
        consentCreateModel.getBalances().add(new Customer("DE67100100101306118605", Customer.CustomerType.IBAN));


        ConsentCreateRequest consentCreateRequest = new ConsentCreateRequest(consentCreateModel);
        service.createConsent(consentCreateRequest);
    }
}
