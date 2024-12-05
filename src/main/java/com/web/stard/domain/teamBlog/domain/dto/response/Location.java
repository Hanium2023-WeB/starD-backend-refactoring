package com.web.stard.domain.teamBlog.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
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

    public Location(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public static Location calculate(List<Location> locations) {
        double totalWeightedLat = 0;
        double totalWeightedLon = 0;

        for (Location loc : locations) {
            totalWeightedLat += loc.getLatitude();
            totalWeightedLon += loc.getLongitude();

            System.out.println("x : " + loc.getLatitude() + ", y : " + loc.getLongitude());
        }

        return new Location(totalWeightedLat / locations.size(), totalWeightedLon / locations.size());
    }

}
