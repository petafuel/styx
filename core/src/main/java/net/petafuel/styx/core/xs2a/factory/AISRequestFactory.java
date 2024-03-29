package net.petafuel.styx.core.xs2a.factory;

import net.petafuel.styx.core.xs2a.callback.control.CallbackProvider;
import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.callback.entity.ServiceRealm;
import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.XS2AFactoryException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import org.apache.logging.log4j.ThreadContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

public class AISRequestFactory implements XS2ARequestFactory<AISRequest> {
    @Override
    public AISRequest create(Class<? extends AISRequest> providedRequest, XS2AFactoryInput factoryInput) {
        try {
            Constructor<? extends AISRequest> constructor = providedRequest.getConstructor(Consent.class, String.class, String.class, String.class);
            AISRequest aisRequest = constructor.newInstance(factoryInput.getConsent(), factoryInput.getConsentId(), factoryInput.getAccountId(), factoryInput.getTransactionId());
            if (factoryInput.getAuthorisationId() != null) {
                aisRequest.setAuthorisationId(factoryInput.getAuthorisationId());
            }
            aisRequest.setPsu(factoryInput.getPsu());
            aisRequest.setBookingStatus(factoryInput.getBookingStatus());
            aisRequest.setDateFrom(factoryInput.getDateFrom());
            aisRequest.setDateTo(factoryInput.getDateTo());
            aisRequest.setWithBalance(factoryInput.getWithBalance());
            aisRequest.setEntryReferenceFrom(factoryInput.getEntryReferenceFrom());
            aisRequest.setDeltaList(factoryInput.getDeltaList());
            if (aisRequest instanceof CreateConsentRequest) {
                aisRequest.setTppRedirectUri(CallbackProvider.generateCallbackUrl(ServiceRealm.CONSENT, RealmParameter.OK, ThreadContext.get("requestUUID")));
                aisRequest.setTppNokRedirectUri(CallbackProvider.generateCallbackUrl(ServiceRealm.PAYMENT, RealmParameter.FAILED, ThreadContext.get("requestUUID")));
            }
            return aisRequest;
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
