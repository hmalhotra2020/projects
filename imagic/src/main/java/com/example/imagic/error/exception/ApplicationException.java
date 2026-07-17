package com.example.imagic.error.exception;

import com.example.imagic.error.ExceptionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationException extends RuntimeException  {
    private ExceptionResponse exceptionResponse;
}
