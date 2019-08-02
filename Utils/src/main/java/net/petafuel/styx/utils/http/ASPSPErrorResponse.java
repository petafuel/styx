package net.petafuel.styx.utils.http;

public interface ASPSPErrorResponse
{
    ASPSPErrorCode getCode();
    ASPSPErrorCategory getCategory();
    String getMessage();

}
