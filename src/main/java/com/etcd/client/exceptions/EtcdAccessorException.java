package com.etcd.client.exceptions;

public class EtcdAccessorException extends Exception
{
    private static final long serialVersionUID = -4835588674395286367L;

    private String errorCode;
    private String errorMsg;
    private Throwable exception;

    public EtcdAccessorException(String errorCode, String errorMsg,
        Throwable ex)
    {
	this.errorCode = errorCode;
	this.errorMsg = errorMsg;
	this.exception = ex;
    }

    @Override
    public String getMessage()
    {
	return errorMsg;
    }

    public String getErrorCode()
    {
	return errorCode;
    }

    public Throwable getException()
    {
	return exception;
    }
}
