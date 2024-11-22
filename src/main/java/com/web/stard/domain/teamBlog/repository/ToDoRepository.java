package com.web.stard.domain.teamBlog.repository;

import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.teamBlog.domain.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    List<ToDo> findAllByStudyAndDueDateBetween(Study study, LocalDate start, LocalDate end);
}
