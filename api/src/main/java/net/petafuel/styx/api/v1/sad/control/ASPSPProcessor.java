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

    public static ASPSPResponse process(String bic) {
        try {
            Aspsp aspsp = getAspspByBic(bic);
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
            }
            return aspspResponse;
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
     * @param implementerOptionMap
     * @return AvailableSCAApproaches
     */
    private static AvailableSCAApproaches processScaApproaches(Map<String, ImplementerOption> implementerOptionMap) {
        AvailableSCAApproaches availableSCAApproaches = new AvailableSCAApproaches();

        availableSCAApproaches.setRedirect(
                getIoValueFromIoMapForGetMethod(
                        implementerOptionMap,
                        "IO5",
                        "redirect"
                )
        );
        availableSCAApproaches.setoAuth(
                getIoValueFromIoMapForGetMethod(
                        implementerOptionMap,
                        "IO5",
                        "oauth"
                )
        );
        availableSCAApproaches.setDecoupled(
                getIoValueFromIoMapForGetMethod(
                        implementerOptionMap,
                        "IO5",
                        "decoupled"
                )
        );
        availableSCAApproaches.setEmbedded(
                getIoValueFromIoMapForGetMethod(
                        implementerOptionMap,
                        "IO5",
                        "embedded"
                )
        );

        return availableSCAApproaches;
    }

    /**
     * @param implementerOptionMap
     * @return Boolean
     */
    private static Boolean processPrestepRequired(Map<String, ImplementerOption> implementerOptionMap) {
        return getIoValueFromIoMapForGetMethod(implementerOptionMap, "IO6", "required");
    }

    /**
     * @param implementerOptionMap
     * @return Boolean
     */
    private static Boolean proccessMulticurrencyAccountsSupported(Map<String, ImplementerOption> implementerOptionMap) {
        return getIoValueFromIoMapForGetMethod(implementerOptionMap, "IO17", "available");
    }

    /**
     * @param implementerOptionMap
     * @return SupportedServicesPIS
     */
    private static SupportedServicesPIS processSupportServicesPis(Map<String, ImplementerOption> implementerOptionMap) {
        SupportedServicesPIS supportedServicesPIS = new SupportedServicesPIS();

        supportedServicesPIS.setSinglePayments(
                getIoValueFromIoMapForContainsValueMethod(
                        implementerOptionMap,
                        "IO2"
                )
        );
        supportedServicesPIS.setBulkPayments(
                getIoValueFromIoMapForContainsValueMethod(
                        implementerOptionMap,
                        "IO3"
                )
        );
        supportedServicesPIS.setPeriodicPayments(
                getIoValueFromIoMapForContainsValueMethod(
                        implementerOptionMap,
                        "IO4"
                )
        );
        supportedServicesPIS.setFutureDatedPayments(
                getIoValueFromIoMapForGetMethod(
                        implementerOptionMap,
                        "IO21",
                        "available")
        );

        return supportedServicesPIS;
    }

    /**
     * @param implementerOptionMap
     * @return SupportedServicesAIS
     */
    private static SupportedServicesAIS processSupportServicesAis(Map<String, ImplementerOption> implementerOptionMap) {
        SupportedServicesAIS supportedServicesAIS = new SupportedServicesAIS();

        supportedServicesAIS.setAccountDetails(true);
        supportedServicesAIS.setAccountList(true);

        supportedServicesAIS.setAccountsWithBalance(
                getIoValueFromIoMapForGetMethod(
                        implementerOptionMap,
                        "IO32",
                        "accounts_with_balance"
                )
        );
        supportedServicesAIS.setAccountsAccountIdWithBalance(
                getIoValueFromIoMapForGetMethod(
                        implementerOptionMap,
                        "IO32",
                        "accounts_account-id_with_balance"
                )
        );
        supportedServicesAIS.setAccountsAccountIdTransactionsWithBalance(
                getIoValueFromIoMapForGetMethod(
                        implementerOptionMap,
                        "IO32",
                        "accounts_account-id_transactions_with_balance"
                )
        );
        supportedServicesAIS.setAccountsAccountIdTransactionsResourceId(
                getIoValueFromIoMapForGetMethod(
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
     * @param implementerOptionMap
     * @param io
     * @param key
     * @return boolean
     */
    private static Boolean getIoValueFromIoMapForGetMethod(Map<String, ImplementerOption> implementerOptionMap, String io, String key) {
        if (implementerOptionMap.containsKey(io)) {
            return implementerOptionMap.get(io).getOptions().get(key);
        }
        return false;
    }

    /**
     * @param implementerOptionMap
     * @param io
     * @return boolean
     */
    private static Boolean getIoValueFromIoMapForContainsValueMethod(Map<String, ImplementerOption> implementerOptionMap, String io) {
        if (implementerOptionMap.containsKey(io)) {
            return implementerOptionMap.get(io).getOptions().containsValue(true);
        }
        return false;
    }

}
