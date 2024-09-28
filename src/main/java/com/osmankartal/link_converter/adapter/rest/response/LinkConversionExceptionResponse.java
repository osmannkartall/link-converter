package com.osmankartal.link_converter.adapter.rest.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;


@Builder
@Jacksonized
@Data
public class LinkConversionExceptionResponse {
    private static final boolean result = false;
    private Object message;
}
