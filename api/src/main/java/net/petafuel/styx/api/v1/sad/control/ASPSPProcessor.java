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

    /**
     *
     * @param bic - BIC provided by the client
     * @return ASPSPResponse | null
     */
    public static ASPSPResponse process(String bic) {
        try {
            Aspsp aspsp = getAspspByBic(bic);

            if (Objects.nonNull(aspsp)) {
                ASPSPResponse aspspResponse = new ASPSPResponse();
                Map<String, ImplementerOption> implementerOptions = aspsp.getConfig().getImplementerOptions();

                SupportedServicesPIS supportedServicesPIS = processSupportedServicesPis(implementerOptions);
                SupportedServicesAIS supportedServicesAIS = processSupportedServicesAis(implementerOptions);
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
                return null;
            }
        } catch (BankNotFoundException | BankLookupFailedException bankNotFoundException) {
            LOG.error("Bank not found for bic={} in SAD", bic);
            return null;
        }
    }

    /**
     * Gets the aspsp for the provided bic
     *
     * @return Aspsp | null
     */
    private static Aspsp getAspspByBic(String bic) throws BankLookupFailedException, BankNotFoundException {
        SAD sad = new SAD();
        return sad.getBankByBIC(bic).getAspsp();
    }

    /**
     * @param implementerOptionMap - ImplementerOption map for the ASPSP
     * @return AvailableSCAApproaches
     */
    private static AvailableSCAApproaches processScaApproaches(Map<String, ImplementerOption> implementerOptionMap) {
        AvailableSCAApproaches availableSCAApproaches = new AvailableSCAApproaches();

        availableSCAApproaches.setRedirect(
                getIoValueByKey(
                        implementerOptionMap,
                        "IO5",
                        "redirect"
                )
        );
        availableSCAApproaches.setoAuth(
                getIoValueByKey(
                        implementerOptionMap,
                        "IO5",
                        "oauth"
                )
        );
        availableSCAApproaches.setDecoupled(
                getIoValueByKey(
                        implementerOptionMap,
                        "IO5",
                        "decoupled"
                )
        );
        availableSCAApproaches.setEmbedded(
                getIoValueByKey(
                        implementerOptionMap,
                        "IO5",
                        "embedded"
                )
        );

        return availableSCAApproaches;
    }

    /**
     * @param implementerOptionMap - ImplementerOption map for the ASPSP
     * @return boolean
     */
    private static boolean processPrestepRequired(Map<String, ImplementerOption> implementerOptionMap) {
        return getIoValueByKey(implementerOptionMap, "IO6", "required");
    }

    /**
     * @param implementerOptionMap - ImplementerOption map for the ASPSP
     * @return boolean
     */
    private static boolean proccessMulticurrencyAccountsSupported(Map<String, ImplementerOption> implementerOptionMap) {
        return getIoValueByKey(implementerOptionMap, "IO17", "available");
    }

    /**
     * @param implementerOptionMap - ImplementerOption map for the ASPSP
     * @return SupportedServicesPIS
     */
    private static SupportedServicesPIS processSupportedServicesPis(Map<String, ImplementerOption> implementerOptionMap) {
        SupportedServicesPIS supportedServicesPIS = new SupportedServicesPIS();

        supportedServicesPIS.setSinglePayments(
                isIoTrue(
                        implementerOptionMap,
                        "IO2"
                )
        );
        supportedServicesPIS.setBulkPayments(
                isIoTrue(
                        implementerOptionMap,
                        "IO3"
                )
        );
        supportedServicesPIS.setPeriodicPayments(
                isIoTrue(
                        implementerOptionMap,
                        "IO4"
                )
        );
        supportedServicesPIS.setFutureDatedPayments(
                getIoValueByKey(
                        implementerOptionMap,
                        "IO21",
                        "available")
        );

        return supportedServicesPIS;
    }

    /**
     * @param implementerOptionMap - ImplementerOption map for the ASPSP
     * @return SupportedServicesAIS
     */
    private static SupportedServicesAIS processSupportedServicesAis(Map<String, ImplementerOption> implementerOptionMap) {
        SupportedServicesAIS supportedServicesAIS = new SupportedServicesAIS();

        supportedServicesAIS.setAccountDetails(true);
        supportedServicesAIS.setAccountList(true);

        supportedServicesAIS.setAccountsWithBalance(
                getIoValueByKey(
                        implementerOptionMap,
                        "IO32",
                        "accounts_with_balance"
                )
        );
        supportedServicesAIS.setAccountsAccountIdWithBalance(
                getIoValueByKey(
                        implementerOptionMap,
                        "IO32",
                        "accounts_account-id_with_balance"
                )
        );
        supportedServicesAIS.setAccountsAccountIdTransactionsWithBalance(
                getIoValueByKey(
                        implementerOptionMap,
                        "IO32",
                        "accounts_account-id_transactions_with_balance"
                )
        );
        supportedServicesAIS.setAccountsAccountIdTransactionsResourceId(
                getIoValueByKey(
                        implementerOptionMap,
                        "IO32",
                        "accounts_account-id_transactions_resourceId"
                )
        );

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

    /**
     * Method returns the value for a provided implementer option and key
     * @param implementerOptionMap - ImplementerOption map for the ASPSP
     * @param io - required implementer option
     * @param key - required key
     * @return boolean
     */
    private static boolean getIoValueByKey(Map<String, ImplementerOption> implementerOptionMap, String io, String key) {
        if (implementerOptionMap.containsKey(io)) {
            return implementerOptionMap.get(io).getOptions().get(key);
        }
        return false;
    }

    /**
     * Method checks if the options for a provided implementer option containing at least one boolean true as value
     * @param implementerOptionMap - ImplementerOption map for the ASPSP
     * @param io required implementer option
     * @return boolean
     */
    private static boolean isIoTrue(Map<String, ImplementerOption> implementerOptionMap, String io) {
        if (implementerOptionMap.containsKey(io)) {
            return implementerOptionMap.get(io).getOptions().containsValue(true);
        }
        return false;
    }

}
