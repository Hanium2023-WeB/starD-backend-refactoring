package com.web.stard.domain.teamBlog.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.domain.teamBlog.domain.dto.response.Location;
import com.web.stard.domain.teamBlog.service.LocationService;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    private final StudyService studyService;

    /**
     * 스터디 - 장소 추천 (중간 지점)
     *
     * @param studyId 해당 study 고유 id
     * @param places 장소 리스트
     *
     * @return Item latitude 추천 위치 위도, longitude 추천 위치 경도
     */
    @Override
    public Location recommendation(Long studyId, List<String> places) {
        Study study = studyService.findById(studyId);
        studyService.isStudyInProgress(study);

        List<Location> locations = new ArrayList<>();

        locations = places.stream()
                .distinct() // 중복 주소 제거
                .map(this::geocoder)
                .toList();

        return Location.calculate(locations);
    }

    /**
     * 주소 -> 위도, 경도 변환
     *
     * @param address 주소
     * @return Location 위도, 경도
     */
    private Location geocoder (String address) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.add("X-NCP-APIGW-API-KEY", clientSecret);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode")
                    .queryParam("query", address);

            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(builder.toUriString()));

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) { // 200
                String jsonResponse = response.getBody();

                // JSON 파싱 - Jackson ObjectMapper
                ObjectMapper objectMapper = new ObjectMapper();

                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode addressesNode = rootNode.path("addresses");

                if (addressesNode.isArray() && addressesNode.size() > 0) {
                    JsonNode addressNode = addressesNode.get(0);

                    double x = Double.parseDouble(addressNode.get("x").asText());
                    double y = Double.parseDouble(addressNode.get("y").asText());

                    return new Location(x, y);
                } else {
                    throw new CustomException(ErrorCode.GEOCODING_FAILED);
                }
            } else {
                throw new CustomException(ErrorCode.GEOCODING_FAILED);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GEOCODING_FAILED);
        }
    }
}
