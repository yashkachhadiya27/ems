package com.backend.ems.Repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.ems.DTO.TodoDto;
import com.backend.ems.Entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {

    List<Todo> findByRegisterId(int id, Sort sort);

    @Query("select new com.backend.ems.DTO.TodoDto(t.description,t.status) from Todo t where t.registerId=:userId and t.status in ('IN_PROGRESS','TODO')")
    List<TodoDto> getNotDoneTodoTasks(int userId);
}
