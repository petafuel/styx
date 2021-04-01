package net.petafuel.styx.api.filter.output.control;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ReferenceHeaderFilterUnitTest {
    @Mock
    ContainerResponseContext containerResponseContext;

    /**
     * Test whether the reference header is added by filter and has the correct name
     *
     * @throws IOException if the Filter is not working correctly
     */
    @Test
    void testReferenceHeader() throws IOException {
        ReferenceHeaderFilter referenceHeaderFilter = new ReferenceHeaderFilter();
        Mockito.lenient().when(containerResponseContext.getHeaders()).thenReturn(new MultivaluedHashMap<>());
        referenceHeaderFilter.filter(null, containerResponseContext);
        Assertions.assertTrue(containerResponseContext.getHeaders().containsKey("Reference"));
    }
}