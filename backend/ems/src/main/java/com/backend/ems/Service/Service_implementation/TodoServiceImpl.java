package com.backend.ems.Service.Service_implementation;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.backend.ems.DTO.TodoDto;
import com.backend.ems.Entity.Todo;
import com.backend.ems.Enums.TodoStatus;
import com.backend.ems.Repository.TodoRepository;
import com.backend.ems.Service.Service_Interface.TodoServiceInterface;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoServiceInterface {

    private final TodoRepository todoRepository;

    @Override
    public boolean addTask(Todo todo) {
        try {
            todoRepository.save(todo);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public List<Todo> getAllTasksByUserId(int id) {
        List<Todo> td = todoRepository.findByRegisterId(id, Sort.by(Sort.Direction.ASC, "id"));
        return td;
    }

    @Override
    public boolean updateTaskStatus(int id, TodoStatus status) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo != null) {
            todo.setStatus(status);
            todoRepository.save(todo);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTask(int id) {
        try {
            todoRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public boolean updateTaskDescription(int id, String newDescription) {
        Todo todo = todoRepository.findById(id)
                .orElse(null);
        if (todo != null) {
            todo.setDescription(newDescription);
            todoRepository.save(todo);
            return true;
        }
        return false;
    }

    @Override
    public List<TodoDto> getNotDoneTodoTasks(int userId) {
        return todoRepository.getNotDoneTodoTasks(userId);
    }

}
