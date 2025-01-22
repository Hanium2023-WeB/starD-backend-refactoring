package com.web.stard.domain.file.api;

import com.web.stard.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequiredArgsConstructor
@Tag(name = "files", description = "파일 관련 API")
public class FileController {

    private final MemberService memberService;

    @Operation(summary = "프로필 이미지 파일 조회")
    @GetMapping("/profile/{image}")
    public ResponseEntity<Resource> getProfileImageFile(@PathVariable("image") String image) throws IOException {
        Resource resource = memberService.getProfileImageFile(image);
        return ResponseEntity.ok().header("Content-Type", Files.probeContentType(resource.getFile().toPath()))
                .body(resource);
    }
}
