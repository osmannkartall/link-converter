package com.osmankartal.link_converter.adapter.rest;

import com.osmankartal.link_converter.adapter.rest.response.CreateShortlinkResponse;
import com.osmankartal.link_converter.adapter.rest.response.ResolveShortlinkResponse;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkConversionMapper {
    LinkConversionMapper INSTANCE = Mappers.getMapper(LinkConversionMapper.class);

    ResolveShortlinkResponse linkConversionToServeShortlinkResponse(LinkConversion linkConversion);

    CreateShortlinkResponse linkConversionToCreateShortlinkResponse(LinkConversion linkConversion);
}
