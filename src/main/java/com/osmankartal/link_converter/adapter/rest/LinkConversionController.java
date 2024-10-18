package com.osmankartal.link_converter.adapter.rest;

import com.osmankartal.link_converter.adapter.rest.request.CreateShortlinkRequest;
import com.osmankartal.link_converter.adapter.rest.response.CreateShortlinkResponse;
import com.osmankartal.link_converter.adapter.rest.response.ResolveShortlinkResponse;
import com.osmankartal.link_converter.core.command.*;
import com.osmankartal.link_converter.core.handler.Handler;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import com.osmankartal.link_converter.domain.model.LinkConversionConfig;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class LinkConversionController {

    private final Handler<CreateShortlinkCommand, LinkConversion> createShortlinkHandler;

    private final Handler<ResolveShortlinkCommand, LinkConversion> resolveShortlinkHandler;

    @PostMapping("/link_conversions")
    public ResponseEntity<CreateShortlinkResponse> createShortlink(@RequestBody @Valid CreateShortlinkRequest createShortlinkRequest) {
        LinkConversion linkConversion = createShortlinkHandler.execute(createShortlinkRequest.toCommand());
        return ResponseEntity.ok(LinkConversionMapper.INSTANCE.linkConversionToCreateShortlinkResponse(linkConversion));
    }

    @GetMapping("/link_conversions")
    public ResponseEntity<ResolveShortlinkResponse> resolveShortlink(
            @RequestParam(defaultValue = LinkConversionConfig.SHORTLINK_DOMAIN) String domain,
            @RequestParam @Valid @NotBlank String hash) {
        return ResponseEntity.ok(
                LinkConversionMapper.INSTANCE.linkConversionToServeShortlinkResponse(
                        resolveShortlinkHandler.execute(ResolveShortlinkCommand.builder().shortlink(domain + "/" + hash).build())));
    }

    @GetMapping("/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("/link_conversions?hash=" + shortUrl);
    }
}
