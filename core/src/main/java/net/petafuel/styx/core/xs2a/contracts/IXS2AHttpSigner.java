package net.petafuel.styx.core.xs2a.contracts;

/**
 * Interface to define a sign function for HTTP Requests on Application Level
 *
 * <p>
 * <br>Signature generation is dependent on the order and case-sensitivity of the Header Field Names and Values
 * <br>
 * <br>Therefore Header Field Names defined as constants should be lowercase, this will avoid any errors throughout
 * <br>the signing process caused by structural/ordering or case-sensitivity
 * <br>RFC 7230 (HTTP/1.1) - Header Fields are case-<b>INsensitive</b>
 * <br>RFC 7540 (HTTP/2) - Header Fields remain case-<b>INsensitive</b>
 * </p>
 */
public interface IXS2AHttpSigner {

    void sign(XS2ARequest xs2aRequest);
}
