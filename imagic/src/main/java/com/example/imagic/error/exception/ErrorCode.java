package com.example.imagic.error.exception;

public enum ErrorCode {

    E_NS("Not Supported Operation"),
    E_MU("Malformed URL"),
    E_DE("Error writing file or directore. Could be disk issue"),
    E_CE("Error while converting file"),
    E_FT("File Type Error");

    private final String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }
}
