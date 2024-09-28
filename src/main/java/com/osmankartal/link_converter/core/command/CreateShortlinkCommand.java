package com.osmankartal.link_converter.core.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateShortlinkCommand {
    
    @NotBlank
    String url;
    
    String deeplink;
}
