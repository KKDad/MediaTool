package org.westfield.action;

class UnknownTokenException extends Exception
{
    private final String token;

    UnknownTokenException(String token)
    {
        this.token = token;
    }

    String getToken()
    {
        return this.token;
    }
}
