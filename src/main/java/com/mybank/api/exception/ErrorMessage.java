package com.mybank.api.exception;

public class ErrorMessage {
	
public int ErrorCode;
public String ErrorMessage;

public ErrorMessage(int errorCode, String errorMessage) {
	ErrorCode = errorCode;
	ErrorMessage = errorMessage;
}

public int getErrorCode() {
	return ErrorCode;
}
public void setErrorCode(int errorCode) {
	ErrorCode = errorCode;
}
public String getErrorMessage() {
	return ErrorMessage;
}
public void setErrorMessage(String errorMessage) {
	ErrorMessage = errorMessage;
}

}
