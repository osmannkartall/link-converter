package com.osmankartal.link_converter.adapter.rest.request;

import com.osmankartal.link_converter.core.command.CreateShortlinkCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
@ToString
public class CreateShortlinkRequest {
    private String deeplink;

    @NotBlank
    private String url;

    public CreateShortlinkCommand toCommand() {
        return CreateShortlinkCommand.builder().url(url).deeplink(deeplink).build();
    }
}
