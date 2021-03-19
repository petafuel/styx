package net.petafuel.styx.core.library.sepa.camt052;

import net.petafuel.jsepa.exception.SEPAParsingException;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.facades.Camt052Converter;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.model.TransactionReport;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;

class Camt052ParserTest {

    @Test
    void test() throws SEPAParsingException, IOException {

        String camt052Response =IOUtils.toString(getClass().getResourceAsStream("/library/camt052/camt052.xml"), Charset.defaultCharset());
        Camt052Converter converter = new Camt052Converter();
        TransactionReport transactionReport = converter.processReport(camt052Response);
        Assert.assertNotNull(transactionReport);
    }

}
