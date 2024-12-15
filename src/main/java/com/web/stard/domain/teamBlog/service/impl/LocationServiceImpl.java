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
import java.util.*;

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
     * @return Location latitude 추천 위치 위도, longitude 추천 위치 경도
     */
    @Override
    public Location recommendation(Long studyId, List<String> places) {
        Study study = studyService.findById(studyId);
        studyService.isStudyInProgress(study);

        List<Location> locations = new ArrayList<>();

        locations = places.stream()
                .distinct() // 중복 주소 제거
                .map(this::geocoder) // 위도, 경도 변환
                .toList();

        return calculate(locations);
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

    /**
     * 위도, 경도 -> 주소 변환
     *
     * @param location 위도, 경도
     * @return Location 위도, 경도, 장소명
     */
    private Location reverseGeocoder (Location location) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.add("X-NCP-APIGW-API-KEY", clientSecret);

            String coords = String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude());

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc")
                    .queryParam("coords", coords)
                    .queryParam("orders", "roadaddr")
                    .queryParam("output", "json");

            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(builder.toUriString()));

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) { // 200
                String jsonResponse = response.getBody();

                // JSON 파싱 - Jackson ObjectMapper
                ObjectMapper objectMapper = new ObjectMapper();

                JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                JsonNode resultNode = jsonNode.path("results").get(0);

                String area1 = resultNode.path("region").path("area1").path("name").asText();
                String area2 = resultNode.path("region").path("area2").path("name").asText();
                String roadName1 = resultNode.path("land").path("name").asText();
                String roadName2 = resultNode.path("land").path("number1").asText();
                String roadName3 = resultNode.path("land").path("number2").asText();

                String address = area1 + " " + area2;
                address += " " + roadName1;
                if (!roadName2.isBlank()) {
                    address += " " + roadName2;

                    if (!roadName3.isBlank()) {
                        address += "-" + roadName3;
                    }
                }

                location.updateAddress(address);

                return location;
            } else {
                throw new CustomException(ErrorCode.REVERSE_GEOCODING_FAILED);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomException(ErrorCode.REVERSE_GEOCODING_FAILED);
        }
    }

    // 중간지점 계산
    private Location calculate(List<Location> locations) {
        List<Location> borderLocations = grahamScan(locations);

        double totalWeightedLat = 0;
        double totalWeightedLon = 0;

        for (Location loc : borderLocations) {
            totalWeightedLat += loc.getLatitude();
            totalWeightedLon += loc.getLongitude();
        }

        return new Location(totalWeightedLat / borderLocations.size(), totalWeightedLon / borderLocations.size());
    }

    // 바깥쪽에 위치한 점들 가져오기 (그라함 스캔 알고리즘)
    private List<Location> grahamScan(List<Location> locationList) {
        // 가변리스트로 복사
        List<Location> locations = new ArrayList<>(locationList);

        // 기준점 (가장 좌측 하단 -> 좌표(위도, 경도) 최솟값)
        Location standard = Collections.min(locations, Comparator.comparingDouble(Location::getLatitude)
                .thenComparing(Location::getLongitude));

        // 좌표 정렬
        locations.sort((loc1, loc2) -> {
            double angle1 = Math.atan2(loc1.getLatitude() - standard.getLatitude(), loc1.getLongitude() - standard.getLongitude());
            double angle2 = Math.atan2(loc2.getLatitude() - standard.getLatitude(), loc2.getLongitude() - standard.getLongitude());
            return Double.compare(angle1, angle2);
        });

        // 스택으로 convex hull
        Stack<Location> hull = new Stack<>();

        for (Location loc : locations) {
            while (hull.size() >= 2) {
                // -2번, -1번, 새로 넣으려는 점 ccw 체크
                Location loc1 = hull.pop(); // 스택 가장 위 요소 제거 (-1번)
                Location loc2 = hull.peek(); // 스택 가장 위 요소 선택 (-1번 제거 후 제일 위 요소 : -2번)

                if (ccw(loc2, loc1, loc) > 0) { // 반시계 방향일 경우
                    hull.push(loc1); // -1번 점 다시 삽입 후 반복문 종료
                    break;
                }
                // 시계 방향일 경우 -1번은 그대로 제거하고 -2번, -3번, 새로 넣으려는 점 다시 ccw 비교
            }
            hull.push(loc);
        }

        return hull.stream().toList();
    }

    // ccw
    private int ccw (Location a, Location b, Location c) {
        // ca * ab
        double ccwResult = (b.getLongitude() - a.getLongitude()) * (c.getLatitude() - a.getLatitude()) -
                (c.getLongitude() - a.getLongitude()) * (b.getLatitude() - a.getLatitude());

        if (ccwResult > 0) {
            return 1; // 반시계 방향
        } else if (ccwResult < 0) {
            return -1; // 시계 방향
        } else {
            return 0; // 직선
        }
    }
}
