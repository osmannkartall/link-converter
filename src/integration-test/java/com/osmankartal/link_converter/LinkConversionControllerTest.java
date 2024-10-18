package com.osmankartal.link_converter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.osmankartal.link_converter.adapter.rest.LinkConversionController;
import com.osmankartal.link_converter.adapter.rest.request.CreateShortlinkRequest;
import com.osmankartal.link_converter.adapter.rest.response.CreateShortlinkResponse;
import com.osmankartal.link_converter.adapter.rest.response.LinkConversionExceptionResponse;
import com.osmankartal.link_converter.adapter.rest.response.ResolveShortlinkResponse;
import com.osmankartal.link_converter.adapter.test.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LinkConversionController.class)
@Import({FakeCreateShortlinkHandler.class, FakeResolveShortlinkHandler.class})
public class LinkConversionControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createShortlink() throws Exception {
        var request = CreateShortlinkRequest.builder()
                .url(LinkConversionTestConfig.EXAMPLE_URL)
                .deeplink(LinkConversionTestConfig.EXAMPLE_DEEPLINK)
                .build();
        var response = CreateShortlinkResponse.builder().shortlink(LinkConversionTestConfig.EXAMPLE_SHORTLINK).build();

        MvcResult mvcResult = mockMvc.perform(post("/link_conversions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(objectMapper.writeValueAsString(response), mvcResult.getResponse().getContentAsString());
    }

    @Test
    void createShortlinkWithoutDeeplink() throws Exception {
        var request = CreateShortlinkRequest.builder()
                .url(LinkConversionTestConfig.EXAMPLE_URL)
                .build();
        var response = CreateShortlinkResponse.builder().shortlink(LinkConversionTestConfig.EXAMPLE_SHORTLINK).build();

        MvcResult mvcResult = mockMvc.perform(post("/link_conversions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(objectMapper.writeValueAsString(response), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void resolveShortlink() throws Exception {
        var response = ResolveShortlinkResponse.builder()
                .deeplink(LinkConversionTestConfig.EXAMPLE_DEEPLINK)
                .url(LinkConversionTestConfig.EXAMPLE_URL)
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/link_conversions?hash=" + "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(objectMapper.writeValueAsString(response), mvcResult.getResponse().getContentAsString());
    }

    @Test
    void createShortlinkWithNullUrl() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/link_conversions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        var response = LinkConversionExceptionResponse.builder()
                .message("{url=must not be blank}")
                .build();

        assertEquals(objectMapper.writeValueAsString(response), mvcResult.getResponse().getContentAsString());
    }

    @Test
    void createShortlinkWithEmptyUrl() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/link_conversions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"  \"}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        var response = LinkConversionExceptionResponse.builder()
                .message("{url=must not be blank}")
                .build();

        assertEquals(objectMapper.writeValueAsString(response), mvcResult.getResponse().getContentAsString());
    }

    @Test
    void resolveShortlinkWithNullHash() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/link_conversions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        var response = LinkConversionExceptionResponse.builder()
                .message("Required request parameter 'hash' is missing.")
                .build();

        assertEquals(objectMapper.writeValueAsString(response), mvcResult.getResponse().getContentAsString());
    }

    @Test
    void resolveShortlinkNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/link_conversions?hash=shortlink-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals("{\"message\":\"Cannot find the record for the given shortlink\"}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    void redirectToOriginalUrl() throws Exception {
        mockMvc.perform(get("/a123da231ad")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/link_conversions?hash=a123da231ad"));
    }
}
