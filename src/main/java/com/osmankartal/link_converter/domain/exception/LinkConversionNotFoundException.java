package com.osmankartal.link_converter.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LinkConversionNotFoundException extends RuntimeException {
    private final Object request;

    public LinkConversionNotFoundException(String message, Object request) {
        super(message);
        this.request = request;
    }
}

