package com.example.imagic.error;

import com.example.imagic.error.exception.ApplicationException;
import com.example.imagic.error.exception.ErrorCode;
import org.javatuples.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ControllerAdvice
public class ErrorAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ApplicationException.class, IOException.class, Exception.class})
    public @ResponseBody ExceptionResponse handleIOException(Exception exception, HttpServletRequest request) {
        String bodyOfResponse = "This should be application specific";
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        Pair<String, String> errorDetails = getErrorDetails(exception);

        logger.error("Error:" + errorDetails.toString());
        exceptionResponse.setUrl(request.getRequestURI());
        exceptionResponse.setDesc(errorDetails.getValue1());
        exceptionResponse.setErrorCode(errorDetails.getValue0());

        return exceptionResponse;
    }

    private Pair getErrorDetails(Exception e)   {
        if (e instanceof ApplicationException)
            return new Pair(ErrorCode.E_MU.getErrorCode(),
                    "Exception occured while downloading file, please contact API Admin");

        return new Pair(HttpStatus.INTERNAL_SERVER_ERROR,
                "Exception occured while processing Request, please contact API Admin");
    }

}
