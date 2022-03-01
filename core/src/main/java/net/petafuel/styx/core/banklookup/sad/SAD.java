package net.petafuel.styx.core.banklookup.sad;

import net.petafuel.styx.core.banklookup.BankLookUpInterface;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.banklookup.sad.entities.ImplementerOption;
import net.petafuel.styx.core.banklookup.sad.entities.Url;
import net.petafuel.styx.core.persistence.layers.PersistentSAD;
import net.petafuel.styx.core.xs2a.contracts.AISInterface;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.PIISInterface;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;
import net.petafuel.styx.core.xs2a.exceptions.SADException;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.factory.XS2ARequestClassProvider;
import net.petafuel.styx.core.xs2a.utils.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;


public class SAD implements BankLookUpInterface {
    private static final Logger LOG = LogManager.getLogger(SAD.class);
    private static final String SAD_BANK_NOT_FOUND = "SAD_BANK_NOT_FOUND";
    private static final String SERVICES_PACKAGE_PATH = "net.petafuel.styx.core.xs2a.standards.";
    private final Map<XS2AServices, String> urlMap = new EnumMap<>(XS2AServices.class);
    private String xs2aClassPrefix;
    private String bic;

    /**
     * Returns a fully initialized XS2AStandard including instances of the service classes if available/implemented for bank standard
     * If there is no entry found for the bic, a BankNotFoundException is thrown
     *
     * @param bic       The bic which should be searched for in SAD
     * @param isSandbox Should the service classes be initialized using the xs2a sandbox environment or production environment of the bank
     * @return returns a XS2AStandard object which should contain fully initialized service objects for all service types if applicable/implemented
     * @throws BankNotFoundException     the bic was not found in the SAD Database
     * @throws BankLookupFailedException there was an error initializing a service which is necessary for
     */
    public XS2AStandard getBankByBIC(String bic, boolean isSandbox) throws BankLookupFailedException, BankNotFoundException {
        this.bic = bic;

        //Read aspsp data from SAD database into Aspsp.class model
        Aspsp aspsp = PersistentSAD.getByBIC(bic);
        if (aspsp == null) {
            LOG.error("The requested bank for bic={} is not avaiable in SAD {}", bic, SAD_BANK_NOT_FOUND);
            throw new BankNotFoundException("The requested aspsp for bic " + bic + " is not available in SAD");
        }
        XS2AStandard xs2AStandard = new XS2AStandard();
        //parse the implementer options into the implementerOptions List of the aspsp object
        parseImplementerOptions(aspsp);
        xs2AStandard.setAspsp(aspsp);

        String standardClassName = aspsp.getConfig().getStandard().getName();
        String standardPackage = standardClassName.toLowerCase();

        Version version = new Version(aspsp.getConfig().getStandard().getVersion());
        String interfaceVersion = ".v" + version.getMajor() + "_" + version.getMinor();

        //build a full qualified class name without service type suffix
        xs2aClassPrefix = SERVICES_PACKAGE_PATH + standardPackage + interfaceVersion + "." + standardClassName;

        //Check if requesting sandbox or production urls
        if (isSandbox) {
            LOG.warn("SAD is using sandbox environment bic={}", bic);
            mapASPSPUrlSetup(aspsp.getSandboxUrl());
        } else {
            mapASPSPUrlSetup(aspsp.getProductionUrl());
        }

        //HttpSigner will be used in all following service initialisations and is therefore initialized first
        //Signer might be null if the target standard does not require or did not implement the class in its package
        Class<?> httpSignerClazz = getServiceClass(xs2aClassPrefix + XS2AServices.HTTP_SIGNER.getValue());
        try {
            IXS2AHttpSigner httpSignerInstance = null;
            if (httpSignerClazz != null) {
                httpSignerInstance = (IXS2AHttpSigner) httpSignerClazz.getConstructor().newInstance();
            }
            //initializing all service classes per service type and setting them into the xs2aStandard
            CSInterface csServiceInstance = (CSInterface) reflectServiceInstance(
                    httpSignerInstance,
                    XS2AServices.CS
            );
            xs2AStandard.setCs(csServiceInstance);

            AISInterface aisServiceInstance = (AISInterface) reflectServiceInstance(
                    httpSignerInstance,
                    XS2AServices.AIS
            );
            xs2AStandard.setAis(aisServiceInstance);

            PISInterface pisServiceInstance = (PISInterface) reflectServiceInstance(
                    httpSignerInstance,
                    XS2AServices.PIS

            );
            xs2AStandard.setPis(pisServiceInstance);

            PIISInterface piisServiceInstance = (PIISInterface) reflectServiceInstance(
                    httpSignerInstance,
                    XS2AServices.PIIS

            );
            xs2AStandard.setPiis(piisServiceInstance);

            Class<?> requestClassProviderClazz = getServiceClass(xs2aClassPrefix + XS2AServices.REQUEST_CLASS_PROVIDER.getValue());
            if (requestClassProviderClazz == null) {
                throw new SADException("RequestClassProvider was not found for the requested XS2A Standard but is required");
            }
            XS2ARequestClassProvider requestClassProviderInstance;
            requestClassProviderInstance = (XS2ARequestClassProvider) requestClassProviderClazz.getConstructor().newInstance();
            xs2AStandard.setRequestClassProvider(requestClassProviderInstance);


        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Error initialising service class through SAD: {}", e.getMessage(), e);
            throw new BankLookupFailedException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            LOG.error("Error calling service class constructor through SAD: {}", e.getMessage(), e);
            throw new BankLookupFailedException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            LOG.error("The service class has no matching constructor for initialisation through SAD: {}", e.getMessage(), e);
            throw new BankLookupFailedException(e.getMessage(), e);
        }
        return xs2AStandard;
    }

    /**
     * Gets the XS2AStandard. By default it is initialized for production environments
     *
     * @param bic
     * @return
     * @throws BankNotFoundException
     * @throws BankLookupFailedException
     */
    @Override
    public XS2AStandard getBankByBIC(String bic) throws BankNotFoundException, BankLookupFailedException {
        return getBankByBIC(bic, false);
    }

    /**
     * Parsing the implementer options json and mapping to ImplementerOption List within the aspsp
     *
     * @param aspsp Aspsp object that should be used and should be modified with the parsed implementer options
     */
    private void parseImplementerOptions(Aspsp aspsp) {
        String rawDefaultConfig;
        //Check if there is a config on aspsp level and use the standard config template if not present for aspsp
        if ((rawDefaultConfig = aspsp.getConfig().getConfiguration()) == null) {
            rawDefaultConfig = aspsp.getConfig().getStandard().getConfigTemplate();
        }
        JsonObject defaultConfig = null;

        try(Jsonb jsonb = JsonbBuilder.create()){
            defaultConfig = jsonb.fromJson(rawDefaultConfig, JsonObject.class);
        } catch (Exception e) {
            throw new SerializerException("unable to deserialize implementer-options json on SAD Service usage", e);
        }

        //Check if there is a styx config on aspsp level
        //If not present on aspsp level use styx config template of related standard
        String rawStyxConfig;
        if ((rawStyxConfig = aspsp.getConfig().getStyxConfiguration()) == null) {
            rawStyxConfig = aspsp.getConfig().getStandard().getStyxConfigTemplate();
        }
        //Merge the styx config options into the defaultConfig
        JsonObject styxConfig = null;

        try(Jsonb jsonb = JsonbBuilder.create()){
            styxConfig = jsonb.fromJson(rawStyxConfig, JsonObject.class);
        } catch (Exception e) {
            throw new SerializerException("unable to deserialize styx-options json on SAD Service usage", e);
        }
        JsonObjectBuilder defaultConfigJsonBuilder = Json.createObjectBuilder(defaultConfig);
        defaultConfigJsonBuilder.addAll(Json.createObjectBuilder(styxConfig));
        defaultConfig = defaultConfigJsonBuilder.build();
        defaultConfig.entrySet().stream().forEach(entry -> {
            JsonObject currentOption = entry.getValue().asJsonObject();
            ImplementerOption implementerOption = new ImplementerOption();
            implementerOption.setId(entry.getKey());
            implementerOption.setDescription(currentOption.getString("description", "default"));

            currentOption.get("options").asJsonObject()
                    .entrySet()
                    .stream()
                    .forEach(option -> implementerOption.addOption(option.getKey(), Boolean.valueOf(option.getValue().toString())));
            aspsp.getConfig().getImplementerOptions().put(implementerOption.getId(), implementerOption);
        });
    }

    /**
     * Create an instance of a XS2AService class for the specified service type
     *
     * @param httpSignerInstance
     * @param xs2AReflection
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private Object reflectServiceInstance(IXS2AHttpSigner httpSignerInstance, XS2AServices xs2AReflection) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> serviceClazz = getServiceClass(this.xs2aClassPrefix + xs2AReflection.getValue());
        Object serviceInstance = null;
        if (serviceClazz != null) {
            serviceInstance = serviceClazz
                    .getConstructor(String.class, IXS2AHttpSigner.class)
                    .newInstance(this.urlMap.get(xs2AReflection), httpSignerInstance);
        }

        return serviceInstance;
    }

    /**
     * Returns a Class representation for a xs2a service class using a string classpath
     *
     * @param classPath
     * @return null if the class was not found
     */
    //The classPath is generated by Styx itself using the Styx Database as trusted source
    @SuppressWarnings("squid:S1523")
    private Class<?> getServiceClass(String classPath) {
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            LOG.warn("Class {} was not found within package while instanciating XS2AStandard from SAD for bic={}", classPath, this.bic);
            return null;
        }
    }

    /**
     * Parse the urls to the correct service type depending on multiple or single route xs2a aspsp interfaces
     *
     * @param urls
     */
    private void mapASPSPUrlSetup(Url urls) {
        String aisUrl;
        String pisUrl;
        String piisUrl;

        if (urls.getCommonUrl() == null) {
            aisUrl = urls.getAisUrl();
            pisUrl = urls.getPisUrl();
            piisUrl = urls.getPiisUrl();
        } else {
            aisUrl = pisUrl = piisUrl = urls.getCommonUrl();
        }
        urlMap.put(XS2AServices.AIS, aisUrl);
        urlMap.put(XS2AServices.CS, aisUrl);
        urlMap.put(XS2AServices.PIS, pisUrl);
        urlMap.put(XS2AServices.PIIS, piisUrl);
    }

    /**
     * Defines the class suffix for service related reflection
     * {StandardName}|{Suffix}
     * BerlinGroup|Signer
     */
    enum XS2AServices {
        AIS("AIS"),
        CS("CS"),
        PIS("PIS"),
        PIIS("PIIS"),
        HTTP_SIGNER("Signer"),
        REQUEST_CLASS_PROVIDER("RequestProvider");

        private final String value;

        XS2AServices(String constant) {
            this.value = constant;
        }

        public String getValue() {
            return value;
        }
    }
}
