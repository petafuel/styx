package net.petafuel.styx.api.v1.sad.control;

import net.petafuel.styx.api.v1.sad.entity.ASPSPResponse;
import net.petafuel.styx.api.v1.sad.entity.AvailableSCAApproaches;
import net.petafuel.styx.api.v1.sad.entity.SupportedServices;
import net.petafuel.styx.api.v1.sad.entity.SupportedServicesAIS;
import net.petafuel.styx.api.v1.sad.entity.SupportedServicesCOF;
import net.petafuel.styx.api.v1.sad.entity.SupportedServicesPIS;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Objects;

public class ASPSPProcessor {
    private static final Logger LOG = LogManager.getLogger(ASPSPProcessor.class);

    private ASPSPProcessor() {
        // empty private constructor for sonarQube
    }

    public static ASPSPResponse process(String bic) throws BankLookupFailedException, BankNotFoundException {
        try {
            Aspsp aspsp =  getAspspByBic(bic);
            ASPSPResponse aspspResponse = new ASPSPResponse();

            if (Objects.nonNull(aspsp)) {
                Map<String, ImplementerOption> implementerOptions = aspsp.getConfig().getImplementerOptions();

                SupportedServicesPIS supportedServicesPIS = processSupportServicesPis(implementerOptions);
                SupportedServicesAIS supportedServicesAIS = processSupportServicesAis(implementerOptions);
                SupportedServicesCOF supportedServicesCOF = processSupportedServicesCof();
                SupportedServices supportedServices = new SupportedServices(supportedServicesPIS, supportedServicesAIS, supportedServicesCOF);

                aspspResponse.setName(aspsp.getName());
                aspspResponse.setActive(aspsp.isActive());
                aspspResponse.setScaApproaches(processScaApproaches(implementerOptions));
                aspspResponse.setSupportedServices(supportedServices);
                aspspResponse.setPrestepRequired(processPrestepRequired(implementerOptions));
                aspspResponse.setMulticurrencyAccountsSupported(proccessMulticurrencyAccountsSupported(implementerOptions));

                return aspspResponse;
            } else {
                return aspspResponse;
            }
        } catch (BankNotFoundException bankNotFoundException) {
            LOG.error("Bank not found for bic={} in SAD", bic);
            return null;
        }
    }

    /**
     * Gets the aspsp for the provided bic
     * @return Aspsp | null
     */
    private static Aspsp getAspspByBic(String bic) throws BankLookupFailedException, BankNotFoundException {
        SAD sad = new SAD();
        return sad.getBankByBIC(bic).getAspsp();
    }

    /**
     *
     * @param implementerOptionMap
     * @return AvailableSCAApproaches
     */
    private static AvailableSCAApproaches processScaApproaches(Map<String, ImplementerOption> implementerOptionMap) {
        AvailableSCAApproaches availableSCAApproaches = new AvailableSCAApproaches();

        availableSCAApproaches.setRedirect(implementerOptionMap.get("IO5").getOptions().get("redirect"));
        availableSCAApproaches.setoAuth(implementerOptionMap.get("IO5").getOptions().get("oauth"));
        availableSCAApproaches.setDecoupled(implementerOptionMap.get("IO5").getOptions().get("decoupled"));
        availableSCAApproaches.setEmbedded(implementerOptionMap.get("IO5").getOptions().get("embedded"));

        return availableSCAApproaches;
    }

    /**
     *
     * @param implementerOptionMap
     * @return Boolean
     */
    private static Boolean processPrestepRequired(Map<String, ImplementerOption> implementerOptionMap) {
        if (implementerOptionMap.containsKey("IO6")) {
            return implementerOptionMap.get("IO6").getOptions().get("required");
        } else {
            return false;
        }
    }

    /**
     *
     * @param implementerOptionMap
     * @return Boolean
     */
    private static Boolean proccessMulticurrencyAccountsSupported(Map<String, ImplementerOption> implementerOptionMap) {
        if (implementerOptionMap.containsKey("IO17")) {
            return implementerOptionMap.get("IO17").getOptions().get("available");
        } else {
            return false;
        }
    }

    /**
     *
     * @param implementerOptionMap
     * @return SupportedServicesPIS
     */
    private static SupportedServicesPIS processSupportServicesPis(Map<String, ImplementerOption> implementerOptionMap) {
        SupportedServicesPIS supportedServicesPIS = new SupportedServicesPIS();

        supportedServicesPIS.setSinglePayments(implementerOptionMap.get("IO2").getOptions().containsValue(true));
        supportedServicesPIS.setBulkPayments(implementerOptionMap.get("IO3").getOptions().containsValue(true));
        supportedServicesPIS.setPeriodicPayments(implementerOptionMap.get("IO4").getOptions().containsValue(true));
        supportedServicesPIS.setFutureDatedPayments(implementerOptionMap.get("IO21").getOptions().get("available"));

        return supportedServicesPIS;
    }

    /**
     *
     * @param implementerOptionMap
     * @return SupportedServicesAIS
     */
    private static SupportedServicesAIS processSupportServicesAis(Map<String, ImplementerOption> implementerOptionMap) {
        SupportedServicesAIS supportedServicesAIS = new SupportedServicesAIS();

        supportedServicesAIS.setAccountDetails(true);
        supportedServicesAIS.setAccountList(true);
        if (implementerOptionMap.containsKey("IO32")) {
            supportedServicesAIS.setAccountsWithBalance(implementerOptionMap.get("IO32").getOptions().get("accounts_with_balance"));
            supportedServicesAIS.setAccountsAccountIdWithBalance(implementerOptionMap.get("IO32").getOptions().get("accounts_account-id_with_balance"));
            supportedServicesAIS.setAccountsAccountIdTransactionsWithBalance(implementerOptionMap.get("IO32").getOptions().get("accounts_account-id_transactions_with_balance"));
            supportedServicesAIS.setAccountsAccountIdTransactionsResourceId(implementerOptionMap.get("IO32").getOptions().get("accounts_account-id_transactions_resourceId"));
        } else {
            supportedServicesAIS.setAccountsWithBalance(false);
            supportedServicesAIS.setAccountsAccountIdWithBalance(false);
            supportedServicesAIS.setAccountsAccountIdTransactionsWithBalance(false);
            supportedServicesAIS.setAccountsAccountIdTransactionsResourceId(false);
        }

        return supportedServicesAIS;
    }

    /**
     * @return SupportedServicesCOF
     */
    private static SupportedServicesCOF processSupportedServicesCof() {
        SupportedServicesCOF supportedServicesCOF = new SupportedServicesCOF();
        supportedServicesCOF.setFundsConfirmation(true);

        return supportedServicesCOF;
    }
}
