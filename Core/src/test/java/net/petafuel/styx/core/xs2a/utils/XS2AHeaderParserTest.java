package net.petafuel.styx.core.xs2a.utils;


import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.DeleteConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;


public class XS2AHeaderParserTest {

    static Stream<Arguments> requestClassProvider() {
        return Stream.of(
                arguments(new CreateConsentRequest()),
                arguments(new StatusConsentRequest()),
                arguments(new GetConsentRequest()),
                arguments(new DeleteConsentRequest())
        );
    }

    @ParameterizedTest
    @MethodSource("requestClassProvider")
    void basicParsing(XS2ARequest xs2ARequest) {
        XS2AHeaderParser.parse(xs2ARequest);
        Assert.assertFalse(xs2ARequest.getHeaders().isEmpty());

        Assert.assertTrue(xs2ARequest.getHeaders().containsKey("date"));
        Assert.assertFalse(xs2ARequest.getHeaders().get("date").isEmpty());

        Assert.assertTrue(xs2ARequest.getHeaders().containsKey("x-request-id"));
        Assert.assertFalse(xs2ARequest.getHeaders().get("x-request-id").isEmpty());
    }

    @Test
    void nestedParsing()
    {
        class ClassDataMember
        {
            @XS2AHeader("id")
            private String id;

            @XS2AHeader("primitiveNumber")
            protected int primitiveNumber;

            @XS2AHeader("isActive")
            public boolean isActive;

            public ClassDataMember()
            {
                this.id = null;
                this.primitiveNumber = 1;
                this.isActive = false;
            }
        }

        class Request implements XS2ARequest
        {

            @XS2AHeader(nested = true)
            ClassDataMember classDataMember;

            @XS2AHeader("notNested")
            private String notNested;

            @XS2AHeader("empty")
            private String empty;

            private LinkedHashMap<String, String> headers;

            public Request()
            {
                this.headers = new LinkedHashMap<>();
                this.notNested = "notNested";
                this.empty = "";
                this.classDataMember = new ClassDataMember();
            }

            @Override
            public String getRawBody() {
                return null;
            }

            @Override
            public void setHeader(String key, String value) {
                this.headers.put(key, value);
            }

            @Override
            public LinkedHashMap<String, String> getHeaders() {
                return this.headers;
            }
        }
        Request request = new Request();
        XS2AHeaderParser.parse(request);

        Assert.assertFalse(request.getHeaders().isEmpty());

        Assert.assertTrue(request.getHeaders().containsKey("notNested"));
        Assert.assertEquals("notNested", request.getHeaders().get("notNested"));

        //Headers should not contain null or empty header values
        Assert.assertFalse(request.getHeaders().containsKey("id"));
        Assert.assertFalse(request.getHeaders().containsKey("empty"));

        Assert.assertTrue(request.getHeaders().containsKey("primitiveNumber"));
        Assert.assertEquals("1", request.getHeaders().get("primitiveNumber"));

        Assert.assertTrue(request.getHeaders().containsKey("isActive"));
        Assert.assertEquals("false", request.getHeaders().get("isActive"));
    }
}
