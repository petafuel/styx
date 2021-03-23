package net.petafuel.styx.core.xs2a.utils.sepa.camt052;

import net.petafuel.jsepa.exception.SEPAParsingException;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.control.Camt052Converter;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;

class Camt052ParserUnitTest {

    @Test
    void test() throws SEPAParsingException, IOException {

        String camt052Response = IOUtils.toString(getClass().getResourceAsStream("camt052.xml"), Charset.defaultCharset());
        Camt052Converter converter = new Camt052Converter();
        TransactionContainer transactionContainer = converter.processReport(camt052Response);
        Assertions.assertNotNull(transactionContainer);
    }

}
