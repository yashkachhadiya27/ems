import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import { Component, OnInit, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Todo } from '../../../core/Interfaces/TodoModel';
import { TodoService } from '../../../core/Services/todo.service';

@Component({
  selector: 'app-todo',
  standalone: true,
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    FormsModule,
    ReactiveFormsModule,
    DragDropModule,
  ],
  templateUrl: './todo.component.html',
  styleUrl: './todo.component.css',
})
export class TodoComponent implements OnInit {
  private todoService = inject(TodoService);
  todoForm!: FormGroup;
  todo: Todo[] = [];
  inProgress: Todo[] = [];
  done: Todo[] = [];
  updateIndex: any;
  isEditEnabled = signal<boolean>(false);
  userId!: number;
  constructor(private fb: FormBuilder) {}
  ngOnInit(): void {
    this.userId = +(localStorage.getItem('empId') as string);
    this.todoForm = this.fb.group({
      item: ['', Validators.required],
    });
    this.getTasks();
  }

  getTasks(): void {
    this.todo = [];
    this.inProgress = [];
    this.done = [];
    this.todoService.getAllTask(this.userId).subscribe((response: any) => {
      response.forEach((todo: any) => {
        if (todo.status === 'TODO') {
          this.todo.push(todo);
        } else if (todo.status === 'IN_PROGRESS') {
          this.inProgress.push(todo);
        } else if (todo.status === 'DONE') {
          this.done.push(todo);
        }
      });
    });
  }

  addTask(): void {
    const newTodo: Todo = {
      registerId: this.userId,
      description: this.todoForm.value.item,
      status: 'TODO',
    };
    this.todoService.addTask(newTodo).subscribe((response: any) => {
      this.todo.push(newTodo);
      this.todoForm.reset();
    });
  }
  deleteTask(taskId: number | undefined) {
    this.todoService.deleteTask(taskId).subscribe((res) => {
      this.getTasks();
    });
  }
  onEdit(item: any) {
    this.todoForm.controls['item'].setValue(item.description);
    this.updateIndex = item.id;
    this.isEditEnabled.set(true);
  }
  updateTask() {
    this.todoService
      .updateTask(this.updateIndex, this.todoForm.value.item)
      .subscribe((res) => {
        this.getTasks();
      });
    this.todoForm.reset();
    this.updateIndex = undefined;
    this.isEditEnabled.set(false);
  }

  drop(event: CdkDragDrop<any[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    } else {
      var taskId = event.previousContainer.data[event.previousIndex].id;
      const newStatus = event.container.id;
      this.todoService
        .updateTaskStatus(taskId, newStatus)
        .subscribe((response) => {
          transferArrayItem(
            event.previousContainer.data,
            event.container.data,
            event.previousIndex,
            event.currentIndex
          );
        });
    }
  }
}
