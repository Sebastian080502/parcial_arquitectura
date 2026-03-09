package com.iglesia.dto.response;

import com.iglesia.entity.Church;

public record ChurchResponse(
    Long id,
    String name,
    String address
) {
    public static ChurchResponse from(Church church) {
        return new ChurchResponse(church.getId(), church.getName(), church.getAddress());
    }
}