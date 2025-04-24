package org.example.btreeweb.exception_handling;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.btreeweb.exception.FailedToAddKeyException;
import org.example.btreeweb.exception.FailedToRemoveKeyException;
import org.example.btreeweb.exception.NoSuchKeyException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ProblemDetail>handleIOException(IOException ex, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR
                ,messageSource.getMessage("errors.upload-file",new Object[0],"errors.upload-file",locale));
        problemDetail.setProperty("error",ex.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(problemDetail);
    }

    @ExceptionHandler(FailedToAddKeyException.class)
    public ResponseEntity<ProblemDetail>handleFailedToAddKeyException(FailedToAddKeyException ex, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage("errors.tree.add",new Object[0],"errors.tree.add",locale)
        );
        problemDetail.setProperty("error",ex.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(problemDetail);
    }

    @ExceptionHandler(FailedToRemoveKeyException.class)
    public ResponseEntity<ProblemDetail>handleFailedToRemoveKeyException(FailedToRemoveKeyException ex, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage("errors.tree.remove",new Object[0],"errors.tree.remove",locale)
        );
        problemDetail.setProperty("error",ex.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(problemDetail);
    }

    @ExceptionHandler(NoSuchKeyException.class)
    public ResponseEntity<ProblemDetail>handleNoSuchKeyException(NoSuchKeyException ex, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                messageSource.getMessage("errors.tree.key_not_found",new Object[0],"errors.tree.key_not_found",locale));
        problemDetail.setProperty("error",ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problemDetail);
    }
}
