package com.web.stard.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
@Builder
public class ErrorResponseEntity {

    private int status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .message(e.getMessage())
                        .build());
    }
}