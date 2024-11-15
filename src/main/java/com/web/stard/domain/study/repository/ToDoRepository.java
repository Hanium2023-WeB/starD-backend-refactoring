package com.web.stard.domain.study.repository;

import com.web.stard.domain.study.domain.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
}
