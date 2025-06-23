import { Component, ElementRef, Renderer2, signal } from '@angular/core';
import {
  ChatRoom,
  ChatRoomType,
  ChatUser,
  Message,
  MessageType,
} from '../../../core/Interfaces/MessageModel';
import { EmployeeService } from '../../../core/Services/employee.service';
import { ChatService } from '../../../core/Services/chat.service';
import { FormsModule } from '@angular/forms';
import { DatePipe, NgClass, NgFor, NgIf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    FormsModule,
    NgClass,
    MatButtonModule,
    MatIconModule,
    DatePipe,
    MatTooltipModule,
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
})
export class ChatComponent {
  users: ChatUser[] = [];
  usersForGroupCreate: ChatUser[] = [];
  chatRooms: ChatRoom[] = [];
  allChatRooms: ChatRoom[] = [];
  groupChatRoom: ChatRoom[] = [];
  selectedUserIds: number[] = [];
  searchEmail: string = '';
  selectedChatRoomId: number | null = null;
  newMessageContent: string = '';
  currentUserId!: number;
  messages: any;
  selectedFileName: string = '';
  currentMessageId: number | null = null;
  editingMessageId: number | null = null;
  selectedChatRoomUsers: { fname: string; lname: string }[] = [];
  selectedChatRoomName: string = '';
  showMoreUsers: boolean = false;
  private messageSubscription: any;
  private messageIds: Set<number> = new Set();

  constructor(
    private employeeService: EmployeeService,
    private chatService: ChatService,
    private renderer: Renderer2,
    private el: ElementRef
  ) {}
  triggerFileInput(): void {
    const fileInput = this.el.nativeElement.querySelector('#file-input');
    this.renderer.selectRootElement(fileInput).click();
  }
  ngOnInit(): void {
    this.currentUserId = +(localStorage.getItem('empId') as string);
    this.fetchUsers();
    this.fetchChatRooms();
    // this.fetchAllChatRooms();
    this.fetchUserGroupChatRooms();
    this.fetchUsersForGroupChat();
  }

  ngOnDestroy(): void {
    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe();
    }
  }

  fetchUsers(): void {
    this.chatService
      .getContacts(this.currentUserId)
      .subscribe((users: ChatUser[]) => {
        this.users = users.filter((user) => user.id !== this.currentUserId);
        this.users.forEach((user: ChatUser) => {
          this.employeeService.getEmployeeImage(user.image).subscribe({
            next: (blob) => {
              const objectURL = URL.createObjectURL(blob);
              user.image = objectURL;
            },
          });
        });
      });
  }
  // fetchAllChatRooms() {
  //   this.chatService.getAllChatRooms().subscribe((rooms: ChatRoom[]) => {
  //     this.allChatRooms = rooms;
  //     console.log(this.allChatRooms);
  //   });
  // }
  fetchChatRooms(): void {
    this.chatService
      .getUserChatRooms(this.currentUserId)
      .subscribe((rooms: ChatRoom[]) => {
        this.chatRooms = rooms;
        console.log('Chat rooms:', this.chatRooms);
      });
  }
  getAdditionalUsers(): string {
    if (this.selectedChatRoomUsers.length > 2) {
      this.showMoreUsers = !this.showMoreUsers;
      return this.selectedChatRoomUsers
        .slice(2)
        .map((user) => `${user.fname} ${user.lname}`)
        .join(', ');
    }
    return '';
  }
  fetchUsersForGroupChat() {
    this.employeeService
      .searchUsersByEmail('')
      .subscribe((users: ChatUser[]) => {
        this.usersForGroupCreate = users.filter(
          (user) => user.id !== this.currentUserId
        );
      });
  }
  searchUsersByEmail(): void {
    if (this.searchEmail !== '') {
      this.employeeService
        .searchUsersByEmail(this.searchEmail)
        .subscribe((users: ChatUser[]) => {
          this.users = users.filter((user) => user.id !== this.currentUserId);
          this.users.forEach((user: ChatUser) => {
            this.employeeService.getEmployeeImage(user.image).subscribe({
              next: (blob) => {
                const objectURL = URL.createObjectURL(blob);
                user.image = objectURL;
              },
            });
          });
        });
    }
  }

  createGroupChat(groupName: string): void {
    const userIds = [...this.selectedUserIds, this.currentUserId];
    this.chatService
      .createChatRoom(groupName, ChatRoomType.GROUP, userIds)
      .subscribe((chatRoom: ChatRoom) => {
        this.fetchChatRooms();
      });
  }
  fetchUserGroupChatRooms(): void {
    this.chatService.getGroupChatRoomsByUserId(this.currentUserId).subscribe({
      next: (rooms: ChatRoom[]) => {
        this.groupChatRoom = rooms;
      },
      error: (error) => {
        console.error('Error fetching group chat rooms:', error);
      },
    });
  }
  isNewDate(currentMessageIndex: number): boolean {
    if (currentMessageIndex === 0) return true;

    const currentMessage = this.messages[currentMessageIndex];
    const previousMessage = this.messages[currentMessageIndex - 1];

    const currentDate = new Date(currentMessage.timestamp).toDateString();
    const previousDate = new Date(previousMessage.timestamp).toDateString();

    return currentDate !== previousDate;
  }
  selectChatRoom(userId: number, isGroupChat: boolean = false): void {
    if (isGroupChat) {
      this.selectedChatRoomId = userId;
      this.messages = [];
      this.messageIds.clear();

      const selectedRoom = this.chatRooms.find((room) => room.id === userId);

      this.selectedChatRoomName = selectedRoom?.name || '';
      this.selectedChatRoomUsers =
        selectedRoom?.users.map((user: any) => ({
          fname: user.fname,
          lname: user.lname,
        })) || [];

      this.fetchMessagesForRoom(this.selectedChatRoomId);
      this.subscribeToMessages(this.selectedChatRoomId);
    } else {
      const privateRoom = this.chatRooms.find(
        (room) =>
          room.users.some((user) => user.id === userId) &&
          room.type == 'PRIVATE'
      );

      if (privateRoom) {
        this.selectedChatRoomId = privateRoom.id;
        this.messages = [];
        this.messageIds.clear();
        const otherUser: any = privateRoom.users.find(
          (user) => user.id !== this.currentUserId
        );
        this.selectedChatRoomName = otherUser
          ? `${otherUser.fname} ${otherUser.lname}`
          : 'Private Chat';
        this.fetchMessagesForRoom(this.selectedChatRoomId);
        this.subscribeToMessages(this.selectedChatRoomId);
      } else {
        const chatRoomData = {
          name: `Chat with ${userId}`,
          type: ChatRoomType.PRIVATE,
          userIds: [this.currentUserId, userId],
        };
        this.chatService
          .createChatRoom(
            chatRoomData.name,
            chatRoomData.type,
            chatRoomData.userIds as number[]
          )
          .subscribe({
            next: (newChatRoom: ChatRoom) => {
              this.selectedChatRoomId = newChatRoom.id;
              this.chatRooms.push(newChatRoom);
              this.messages = [];
              this.messageIds.clear();

              this.fetchMessagesForRoom(newChatRoom.id);
              this.subscribeToMessages(this.selectedChatRoomId);
            },
            error: (error) => {
              console.error('Error creating chat room:', error);
            },
          });
      }
    }
  }

  fetchMessagesForRoom(roomId: number): void {
    this.chatService.getMessages(roomId).subscribe({
      next: (messages: Message[]) => {
        this.messages = messages;
        this.messageIds = new Set(messages.map((msg) => msg.id));
        console.log('Work as it is2.............');
      },
      error: (error) => {
        console.error('Error fetching messages:', error);
      },
    });
  }
  sendMessage(): void {
    this.selectedFileName = '';
    if (
      this.selectedChatRoomId &&
      (this.newMessageContent || this.selectedFile) &&
      this.editingMessageId === null
    ) {
      if (this.selectedFile) {
        this.chatService
          .uploadFile(this.selectedFile)
          .subscribe((filePath: string) => {
            this.sendMessageWithFilePath(filePath);
          });
      } else {
        this.sendMessageWithFilePath(null);
      }
    } else {
      console.log('Editing id is not null');

      this.editMessage(this.editingMessageId as number, this.newMessageContent);

      this.editingMessageId = null;
    }
  }
  private sendMessageWithFilePath(filePath: string | null): void {
    if (
      this.selectedChatRoomId &&
      (this.newMessageContent || this.selectedFile)
    ) {
      const message: Message = {
        id: 0,
        senderId: this.currentUserId,
        chatRoomId: this.selectedChatRoomId,
        content: this.newMessageContent || '',
        timestamp: new Date().toISOString(),
        contentType: filePath
          ? this.getFileType(this.selectedFile!)
          : MessageType.TEXT,
        filePath: filePath,
        deleted: false,
        edited: false,
      };

      this.chatService.sendMessage(this.selectedChatRoomId, message);

      this.newMessageContent = '';
      this.selectedFile = null;
    }
  }

  onUserSelect(event: any): void {
    this.selectedUserIds = Array.from(event.target.selectedOptions).map(
      (option: any) => option.value
    );
  }

  subscribeToMessages(chatRoomId: number): void {
    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe();
    }

    this.messageSubscription = this.chatService.subscribeToMessages(
      chatRoomId,
      (message: Message) => {
        if (!this.messageIds.has(message.id)) {
          this.messages.push(message);
          this.messageIds.add(message.id);
        } else {
        }
      }
    );
  }

  selectedFile: File | null = null;
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.selectedFileName = this.selectedFile.name;
    }
  }
  getFileType(file: File): MessageType {
    const fileType = file.type.split('/')[0];
    if (fileType === 'image') {
      return MessageType.IMAGE;
    } else if (fileType === 'video') {
      return MessageType.VIDEO;
    }
    return MessageType.TEXT;
  }
  isImage(filePath: string): boolean {
    const extension = filePath.split('.').pop()?.toLowerCase();
    return ['jpg', 'jpeg', 'png', 'gif'].includes(extension || '');
  }
  getFileUrl(filePath: string): string {
    return `http://localhost:9090/api/files/${filePath}`;
  }
  showActions(messageId: number): void {
    this.currentMessageId = messageId;
  }

  hideActions(): void {
    this.currentMessageId = null;
  }

  isActionsVisible(messageId: number): boolean {
    return this.currentMessageId === messageId;
  }

  editMessage(messageId: number, newContent: string): void {
    this.chatService.editMessage(messageId, newContent);
    const messageIndex = this.messages.findIndex(
      (msg: any) => msg.id === messageId
    );
    if (messageIndex !== -1) {
      this.messages[messageIndex].content = newContent;
    }
    this.newMessageContent = '';
  }
  onEditMessageClick(messageId: number, content: string): void {
    this.editingMessageId = messageId;
    this.newMessageContent = content;
  }
  deleteMessage(messageId: number): void {
    this.chatService.deleteMessage(messageId);
    this.messages = this.messages.filter((msg: any) => msg.id !== messageId);
  }
}
