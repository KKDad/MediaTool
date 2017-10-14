package org.westfield.action;

public class UnknownTokenException extends Exception
{
    private final String token;

    public UnknownTokenException(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return this.token;
    }
}
