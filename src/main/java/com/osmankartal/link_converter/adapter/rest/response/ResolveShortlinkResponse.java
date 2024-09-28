package com.osmankartal.link_converter.adapter.rest.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Data
public class ResolveShortlinkResponse {
    private String deeplink;
    private String url;
}
