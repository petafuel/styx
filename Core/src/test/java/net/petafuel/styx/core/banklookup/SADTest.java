package net.petafuel.styx.core.banklookup;

import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.banklookup.sad.entities.Aspsp;
import net.petafuel.styx.core.persistence.Persistence;
import net.petafuel.styx.core.persistence.layers.PersistentSAD;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.MultipleFailuresError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;


//😭
@Tag("integration")
public class SADTest {
    static Stream<Arguments> BICProvider() {
        Stream.Builder<Arguments> streamBuilder = Stream.builder();

        Connection connection = Persistence.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement("SELECT bic FROM aspsps")) {

            try (ResultSet resultSet = query.executeQuery()) {
                while (resultSet.next()) {
                    streamBuilder.add(arguments(resultSet.getString("bic")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return streamBuilder.build();
    }

    @ParameterizedTest
    @MethodSource("BICProvider")
    public void testPersistentSAD(String bic) throws SQLException {
        Aspsp bank = PersistentSAD.getByBIC(bic);

        Assert.assertNotNull(bank);
        Assert.assertEquals(bic, bank.getBic());
        Assert.assertNotNull(bank.getAspspGroup());
        Assert.assertNotNull(bank.getConfig());

        Connection connection = Persistence.getInstance().getConnection();
        ResultSet resultSet;

        int sandboxUrlsId = bank.getSandboxUrl().getId();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from urls WHERE id = ?")) {
            preparedStatement.setInt(1, sandboxUrlsId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Assert.assertNotNull(bank.getSandboxUrl());
            if (bank.getSandboxUrl().getCommonUrl() == null) {
                Assert.assertNotNull(bank.getSandboxUrl().getAisUrl());
                Assert.assertEquals(resultSet.getString("ais_url"), bank.getSandboxUrl().getAisUrl());

                Assert.assertNotNull(bank.getSandboxUrl().getPisUrl());
                Assert.assertEquals(resultSet.getString("pis_url"), bank.getSandboxUrl().getPisUrl());

                Assert.assertNotNull(bank.getSandboxUrl().getPiisUrl());
                Assert.assertEquals(resultSet.getString("piis_url"), bank.getSandboxUrl().getPiisUrl());
            } else {
                Assert.assertNotNull(bank.getSandboxUrl().getCommonUrl());
                Assert.assertEquals(resultSet.getString("url"), bank.getSandboxUrl().getCommonUrl());
            }
        }

        int productionUrlsId = bank.getProductionUrl().getId();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from urls WHERE id = ?")) {
            preparedStatement.setInt(1, productionUrlsId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            Assert.assertNotNull(bank.getProductionUrl());
            if (bank.getProductionUrl().getCommonUrl() == null) {
                Assert.assertNotNull(bank.getProductionUrl().getAisUrl());
                Assert.assertEquals(resultSet.getString("ais_url"), bank.getProductionUrl().getAisUrl());

                Assert.assertNotNull(bank.getProductionUrl().getPisUrl());
                Assert.assertEquals(resultSet.getString("pis_url"), bank.getProductionUrl().getPisUrl());

                Assert.assertNotNull(bank.getProductionUrl().getPiisUrl());
                Assert.assertEquals(resultSet.getString("piis_url"), bank.getProductionUrl().getPiisUrl());
            } else {
                Assert.assertNotNull(bank.getProductionUrl().getCommonUrl());
                Assert.assertEquals(resultSet.getString("url"), bank.getProductionUrl().getCommonUrl());
            }
        }
    }

    @ParameterizedTest
    @MethodSource("BICProvider")
    @DisplayName(
            "testXS2AStandardInitialisationProduction() -> Check if PRODUCTION XS2AStandards can be created for all available BICs in SAD")
    public void testXS2AStandardInitialisationProduction(String bic) throws BankNotFoundException, BankLookupFailedException {
        SAD sad = new SAD();
        Aspsp bank = PersistentSAD.getByBIC(bic);
        XS2AStandard xs2AStandard = sad.getBankByBIC(bic);
        try {
            Assertions.assertAll(() -> {
                if (bank.getProductionUrl().getCommonUrl() == null) {
                    if (bank.getProductionUrl().getAisUrl() != null) {
                        Assert.assertTrue("AIS Url was specified but no Consent Service was initialized", xs2AStandard.isCSImplemented());
                        Assert.assertTrue("AIS Url was specified but no related Service was initialized", xs2AStandard.isAISImplemented());
                    }
                    if (bank.getProductionUrl().getPisUrl() != null) {
                        Assert.assertTrue("PIS Url was specified but no related Service was initialized", xs2AStandard.isPISImplemented());
                    }
                    if (bank.getProductionUrl().getPiisUrl() != null) {
                        Assert.assertTrue("PIIS Url was specified but no related Service was initialized", xs2AStandard.isPIISImplemented());
                    }
                } else {
                    boolean atLeastOneServiceImplemented = false;
                    if ((xs2AStandard.isAISImplemented() && xs2AStandard.isCSImplemented()) || xs2AStandard.isPIISImplemented() || xs2AStandard.isPISImplemented()) {
                        atLeastOneServiceImplemented = true;
                    }
                    Assert.assertTrue("General Url was specified but no related Service was initialized", atLeastOneServiceImplemented);
                }
            });
        } catch (MultipleFailuresError e) {
            if (!bank.isActive()) {
                System.err.println("Test failed for bic=" + bic + " but bank is deactivated in styx: ");
                e.getFailures().forEach(throwable -> System.err.println(throwable.getMessage()));
                Assert.assertTrue(true);
            } else {
                System.err.println("Test failed for bic=" + bic + ": " + e.getMessage());
                e.getFailures().forEach(throwable -> System.err.println(throwable.getMessage()));
                Assert.fail();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("BICProvider")
    @DisplayName(
            "testXS2AStandardInitialisationSandbox() -> Check if SANDBOX XS2AStandards can be created for all available BICs in SAD")
    public void testXS2AStandardInitialisationSandbox(String bic) throws BankNotFoundException, BankLookupFailedException {
        SAD sad = new SAD();
        Aspsp bank = PersistentSAD.getByBIC(bic);
        XS2AStandard xs2AStandard = sad.getBankByBIC(bic, true);
        try {
            Assertions.assertAll(() -> {
                if (bank.getSandboxUrl().getCommonUrl() == null) {
                    if (bank.getSandboxUrl().getAisUrl() != null) {
                        Assert.assertTrue("AIS Url was specified but no Consent Service was initialized", xs2AStandard.isCSImplemented());
                        Assert.assertTrue("AIS Url was specified but no related Service was initialized", xs2AStandard.isAISImplemented());
                    }
                    if (bank.getSandboxUrl().getPisUrl() != null) {
                        Assert.assertTrue("PIS Url was specified but no related Service was initialized", xs2AStandard.isPISImplemented());
                    }
                    if (bank.getSandboxUrl().getPiisUrl() != null) {
                        Assert.assertTrue("PIIS Url was specified but no related Service was initialized", xs2AStandard.isPIISImplemented());
                    }
                } else {
                    boolean atLeastOneServiceImplemented = false;
                    if ((xs2AStandard.isAISImplemented() && xs2AStandard.isCSImplemented()) || xs2AStandard.isPIISImplemented() || xs2AStandard.isPISImplemented()) {
                        atLeastOneServiceImplemented = true;
                    }
                    Assert.assertTrue("General Url was specified but no related Service was initialized", atLeastOneServiceImplemented);
                }
            });
        } catch (MultipleFailuresError e) {
            if (!bank.isActive()) {
                System.err.println("Test failed for bic=" + bic + " but bank is deactivated in styx: ");
                e.getFailures().forEach(throwable -> System.err.println(throwable.getMessage()));
                Assert.assertTrue(true);
            } else {
                System.err.println("Test failed for bic=" + bic + ": " + e.getMessage());
                e.getFailures().forEach(throwable -> System.err.println(throwable.getMessage()));
                Assert.fail();
            }
        }
    }

    @Test
    @DisplayName("testUnknowBank() -> Error Log is expected")
    public void testUnknowBank() throws BankNotFoundException {
        SAD sad = new SAD();
        Assertions.assertThrows(BankNotFoundException.class, () -> {
            sad.getBankByBIC("4432512");
        });
    }
}
