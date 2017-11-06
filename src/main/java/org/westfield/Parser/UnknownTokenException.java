package org.westfield.Parser;

public class UnknownTokenException extends Exception
{
    private final String token;

    UnknownTokenException(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return this.token;
    }
}
