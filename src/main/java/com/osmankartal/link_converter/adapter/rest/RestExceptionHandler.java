package com.osmankartal.link_converter.adapter.rest;

import com.osmankartal.link_converter.adapter.persistence.document.LinkConversionFailureDocument;
import com.osmankartal.link_converter.adapter.persistence.entity.LinkConversionFailureJPAEntity;
import com.osmankartal.link_converter.adapter.persistence.repository.LinkConversionFailureCouchbaseRepository;
import com.osmankartal.link_converter.adapter.persistence.repository.LinkConversionFailureJPARepository;
import com.osmankartal.link_converter.adapter.rest.response.LinkConversionExceptionResponse;
import com.osmankartal.link_converter.domain.exception.LinkConversionBusinessException;
import com.osmankartal.link_converter.domain.exception.LinkConversionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@RestControllerAdvice
public class RestExceptionHandler {

    @Autowired(required = false)
    private LinkConversionFailureJPARepository linkConversionFailureJPARepository;

    @Autowired(required = false)
    private LinkConversionFailureCouchbaseRepository linkConversionFailureCouchbaseRepository;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<LinkConversionExceptionResponse> handleException(Exception e, WebRequest request) {
        System.err.println(e.getMessage());

        LinkConversionExceptionResponse response = LinkConversionExceptionResponse.builder()
                .message("An unexpected error occurred")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<LinkConversionExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest httpServletRequest) {
        String message = getArgumentNotValidMessage(e);

        saveFailure(httpServletRequest.getRequestURI(), message, Optional.ofNullable(e.getTarget()).orElse("").toString());

        LinkConversionExceptionResponse response = LinkConversionExceptionResponse.builder()
                .message(message)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(LinkConversionNotFoundException.class)
    public ResponseEntity<LinkConversionExceptionResponse> handleLinkConversionNotFoundException(LinkConversionNotFoundException e, WebRequest webRequest) {
        saveFailure(((ServletWebRequest) webRequest).getRequest().getRequestURI(), e.getMessage(), e.getRequest().toString());

        LinkConversionExceptionResponse response = LinkConversionExceptionResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<LinkConversionExceptionResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String parameterName = e.getParameterName();
        String message = "Required request parameter '" + parameterName + "' is missing.";

        LinkConversionExceptionResponse response = LinkConversionExceptionResponse.builder()
                .message(message)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(LinkConversionBusinessException.class)
    public ResponseEntity<LinkConversionExceptionResponse> handleLinkConversionBusinessException(LinkConversionBusinessException e, WebRequest webRequest) {
        saveFailure(((ServletWebRequest) webRequest).getRequest().getRequestURI(), e.getMessage(), e.getMessage());

        LinkConversionExceptionResponse response = LinkConversionExceptionResponse.builder()
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private void saveFailure(String endpoint, String message, String request) {
        if (Objects.nonNull(linkConversionFailureJPARepository)) {
            linkConversionFailureJPARepository.save(LinkConversionFailureJPAEntity.builder().endpoint(endpoint).message(message).request(request).build());
        }
        if (Objects.nonNull(linkConversionFailureCouchbaseRepository)) {
            linkConversionFailureCouchbaseRepository.save(LinkConversionFailureDocument.builder().endpoint(endpoint).message(message).request(request).build());
        }
    }

    private String getArgumentNotValidMessage(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();
        String classError = "";
        List<ObjectError> errorList = e.getBindingResult().getAllErrors();

        for (ObjectError objectError : errorList) {
            if (objectError instanceof FieldError fieldError) {
                String fieldName = fieldError.getField();
                String errorMessage = fieldError.getDefaultMessage();
                fieldErrors.put(fieldName, errorMessage);
            } else {
                classError = objectError.getDefaultMessage();
                break;
            }
        }

        return fieldErrors.isEmpty() ? classError : fieldErrors.toString();
    }

}
