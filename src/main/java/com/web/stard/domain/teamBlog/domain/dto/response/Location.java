package com.web.stard.domain.teamBlog.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class Location {
    @Schema(description = "장소의 위도")
    private double latitude;

    @Schema(description = "장소의 경도")
    private double longitude;

    @Schema(description = "장소 명")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String address;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateAddress(String address) {
        this.address = address;
    }
}
