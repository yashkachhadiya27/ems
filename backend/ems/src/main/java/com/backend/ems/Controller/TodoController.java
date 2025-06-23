package com.backend.ems.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.CustomResponse;
import com.backend.ems.DTO.TodoDto;
import com.backend.ems.Entity.Todo;
import com.backend.ems.Enums.TodoStatus;
import com.backend.ems.Service.Service_implementation.TodoServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class TodoController {
    private final TodoServiceImpl todoServiceImpl;

    @GetMapping("/getAllTask/{userId}")
    public ResponseEntity<List<Todo>> getAllTasksByUserId(@PathVariable int userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(todoServiceImpl.getAllTasksByUserId(userId));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/addTask")
    public ResponseEntity<CustomResponse> addTodo(@RequestBody Todo todo) {
        boolean status = todoServiceImpl.addTask(todo);
        if (status) {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("success", 200));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("fail", 400));
    }

    @PatchMapping("/changeStatus/{taskId}")
    public ResponseEntity<CustomResponse> updateTaskStatus(@PathVariable int taskId,
            @RequestParam(value = "status") String status) {
        TodoStatus todoStatus;
        if ("inProgress".equalsIgnoreCase(status)) {

            todoStatus = TodoStatus.IN_PROGRESS;
        } else {
            todoStatus = TodoStatus.valueOf(status.toUpperCase());
        }
        boolean status1 = todoServiceImpl.updateTaskStatus(taskId, todoStatus);

        if (status1) {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("success", 200));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("fail", 400));
    }

    @DeleteMapping("/deleteTask/{id}")
    public ResponseEntity<CustomResponse> deleteTask(@PathVariable int id) {
        boolean status2 = todoServiceImpl.deleteTask(id);
        if (status2) {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("success", 200));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("fail", 400));
    }

    @PatchMapping("/updateTask/{id}")
    public ResponseEntity<CustomResponse> updateTaskDescription(@PathVariable int id,
            @RequestBody String newDescription) {
        boolean status3 = todoServiceImpl.updateTaskDescription(id, newDescription);
        if (status3) {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("success", 200));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("fail", 400));
    }

    @GetMapping("/notDoneTaks/{userId}")
    public List<TodoDto> getNotDoneTask(@PathVariable int userId) {
        return todoServiceImpl.getNotDoneTodoTasks(userId);
    }
}
