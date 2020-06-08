package net.petafuel.styx.core.xs2a.factory;

import net.petafuel.styx.core.xs2a.contracts.SCARequest;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.exceptions.XS2AFactoryException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

/**
 * Factory to create Authorisation Requests for pis and ais SCAs
 */
public class SCARequestFactory implements XS2ARequestFactory<SCARequest> {
    @Override
    public SCARequest create(Class<? extends SCARequest> providedRequest, XS2AFactoryInput factoryInput) {
        try {
            SCARequest scaRequest;
            if (factoryInput.getConsentId() == null && factoryInput.getConsent() == null) {
                Constructor<? extends SCARequest> constructor = providedRequest.getConstructor(PaymentService.class, PaymentProduct.class, String.class);
                scaRequest = constructor.newInstance(factoryInput.getPaymentService(), factoryInput.getPaymentProduct(), factoryInput.getPaymentId());
            } else {
                Constructor<? extends SCARequest> constructor = providedRequest.getConstructor(String.class);
                scaRequest = constructor.newInstance(factoryInput.getConsentId());
            }
            scaRequest.setAuthroisationId(factoryInput.getAuthorisationId());
            scaRequest.setScaAuthenticationData(factoryInput.getScaAuthenticationData());
            scaRequest.setAuthorisationMethodId(factoryInput.getAuthorisationMethodId());
            scaRequest.setPsuData(factoryInput.getPsuData());
            scaRequest.setPsu(factoryInput.getPsu());
            return scaRequest;
        } catch (NoSuchMethodException e) {
            throw new XS2AFactoryException(MessageFormat.format("No viable constructor found for request={0} error={1}", providedRequest, e.getMessage()), e);
        } catch (IllegalAccessException e) {
            throw new XS2AFactoryException(MessageFormat.format("Unable to access constructor for request={0} error={1}", providedRequest, e.getMessage()), e);
        } catch (InstantiationException e) {
            throw new XS2AFactoryException(MessageFormat.format("Request class is abstract, no viable constructor found for request={0} error={1}", providedRequest, e.getMessage()), e);
        } catch (InvocationTargetException e) {
            throw new XS2AFactoryException(MessageFormat.format("Request constructor threw an exception for request={0} error={1}", providedRequest, e.getMessage()), e);
        } catch (IllegalArgumentException e) {
            throw new XS2AFactoryException(MessageFormat.format("The constructor signature was invalid for this Factory for request={0} error={1}", providedRequest, e.getMessage()), e);
        }
    }
}
