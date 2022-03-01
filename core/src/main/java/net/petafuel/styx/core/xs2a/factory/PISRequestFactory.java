package net.petafuel.styx.core.xs2a.factory;

import net.petafuel.styx.core.xs2a.callback.control.CallbackProvider;
import net.petafuel.styx.core.xs2a.callback.entity.RealmParameter;
import net.petafuel.styx.core.xs2a.callback.entity.ServiceRealm;
import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.exceptions.XS2AFactoryException;
import org.apache.logging.log4j.ThreadContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

public class PISRequestFactory implements XS2ARequestFactory<PISRequest> {
    /**
     * @param providedRequest should a request Class from XS2ARequestClassProvider
     * @param factoryInput    should contain all data necessary for object construction
     * @return the initialized PISRequest will be returned
     */
    @Override
    public PISRequest create(Class<? extends PISRequest> providedRequest, XS2AFactoryInput factoryInput) {
        try {
            Constructor<? extends PISRequest> constructor = providedRequest.getConstructor(PaymentService.class, PaymentProduct.class, PSU.class, InitializablePayment.class);
            PISRequest pisRequest = constructor.newInstance(factoryInput.getPaymentService(), factoryInput.getPaymentProduct(), factoryInput.getPsu(), factoryInput.getPayment());
            if (factoryInput.getAuthorisationId() != null) {
                pisRequest.setAuthorisationId(factoryInput.getAuthorisationId());
            }
            pisRequest.setPaymentId(factoryInput.getPaymentId());

            pisRequest.setTppRedirectUri(CallbackProvider.generateCallbackUrl(ServiceRealm.PAYMENT, RealmParameter.OK, ThreadContext.get("requestUUID")));
            pisRequest.setTppNokRedirectUri(CallbackProvider.generateCallbackUrl(ServiceRealm.PAYMENT, RealmParameter.FAILED, ThreadContext.get("requestUUID")));

            return pisRequest;
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
