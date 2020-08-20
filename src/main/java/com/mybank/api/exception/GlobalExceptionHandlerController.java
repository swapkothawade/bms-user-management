package com.mybank.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandlerController {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

   /* @ExceptionHandler(CustomException.class)
    public void handleCustomException(HttpServletResponse res, CustomException e) throws IOException {
        LOG.error("ERROR", e);
        res.sendError(e.getHttpStatus().value(), e.getMessage());
    }*/

    
    @ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorMessage> handleCustomException(CustomException exception) {
    	LOG.error("ERROR", exception);
		ErrorMessage errmsg = new ErrorMessage(404, exception.getMessage());
		return new ResponseEntity<>(errmsg, HttpStatus.NOT_FOUND);
	}


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(HttpServletResponse res, IllegalArgumentException e) throws IOException {
        LOG.error("ERROR", e);
        ErrorMessage errmsg = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
        return new ResponseEntity<>(errmsg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(HttpServletResponse res, Exception e) throws IOException {
        LOG.error("ERROR", e);
        ErrorMessage errmsg = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
        return new ResponseEntity<>(errmsg, HttpStatus.BAD_REQUEST);
    }


}
