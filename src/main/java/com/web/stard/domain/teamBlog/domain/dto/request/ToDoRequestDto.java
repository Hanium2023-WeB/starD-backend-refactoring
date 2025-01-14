package com.web.stard.domain.teamBlog.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ToDoRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "TodoCreateDto")
    public static class CreateDto {
        @Schema(description = "담당 업무")
        @NotBlank(message = "담당 업무를 입력하세요.")
        @Size(max = 50, message = "최대 {max}자까지 입력 가능합니다.")
        private String task;

        @Schema(description = "마감일")
        @NotNull(message = "마감일을 입력하세요.")
        private LocalDate dueDate;

        @Schema(description = "담당자 닉네임")
        private List<String> assignees;
    }

}
