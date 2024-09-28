package com.osmankartal.link_converter.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LinkConversionBusinessException extends RuntimeException {

    public LinkConversionBusinessException(String message) {
        super(message);
    }
}