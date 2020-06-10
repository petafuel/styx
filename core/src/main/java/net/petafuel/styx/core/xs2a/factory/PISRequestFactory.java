package net.petafuel.styx.core.xs2a.factory;

import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.exceptions.XS2AFactoryException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

public class PISRequestFactory implements XS2ARequestFactory<PISRequest> {
    /**
     * @param providedRequest
     * @param factoryInput
     * @return
     */
    @Override
    public PISRequest create(Class<? extends PISRequest> providedRequest, XS2AFactoryInput factoryInput) {
        try {
            Constructor<? extends PISRequest> constructor = providedRequest.getConstructor(PaymentService.class, PaymentProduct.class, PSU.class, InitializablePayment.class);
            PISRequest pisRequest = constructor.newInstance(factoryInput.getPaymentService(), factoryInput.getPaymentProduct(), factoryInput.getPsu(), factoryInput.getPayment());
            if (factoryInput.getAuthorisationId() != null) {
                pisRequest.setAuthroisationId(factoryInput.getAuthorisationId());
            }
            pisRequest.setPaymentId(factoryInput.getPaymentId());

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
