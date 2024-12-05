package com.web.stard.domain.teamBlog.service;

import com.web.stard.domain.teamBlog.domain.dto.response.Location;

import java.util.List;

public interface LocationService {
    Location recommendation(Long studyId, List<String> places);
}
