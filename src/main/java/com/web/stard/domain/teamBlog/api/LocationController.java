package com.web.stard.domain.teamBlog.api;

import com.web.stard.domain.teamBlog.domain.dto.response.Location;
import com.web.stard.domain.teamBlog.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studies/{studyId}/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "장소 추천 (중간 지점 찾기)")
    @GetMapping("/find")
    public ResponseEntity<Location> recommendation(@PathVariable(name = "studyId") Long studyId,
                                                   @RequestParam @NotEmpty(message = "장소 목록은 필수입니다.")
                                                   @Size(min = 2, message = "장소는 최소 2개 이상 입력해야 합니다.") List<String> places) {
        return  ResponseEntity.ok(locationService.recommendation(studyId, places));
    }
}
