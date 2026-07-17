package com.example.imagic.error;

import lombok.Data;
import lombok.With;

@Data
public class ExceptionResponse {
    private String errorCode;
    private String desc;
    private String url;

    public ExceptionResponse()  {}

    public ExceptionResponse(String errorCode, String desc)    {
        this.errorCode = errorCode;
        this.desc = desc;
    }
}
