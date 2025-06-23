import { Injectable } from '@angular/core';
import {
  ChatRoom,
  ChatRoomType,
  ChatUser,
  Message,
} from '../Interfaces/MessageModel';
import { HttpClient, HttpParams } from '@angular/common/http';
import { StompService } from './stomp.service';
import { catchError, map, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private baseUrl = 'http://localhost:9090/api/chatroom';

  private stompClient: any;

  constructor(private http: HttpClient, private stompService: StompService) {
    this.initializeWebSocketConnection();
  }
  private async initializeWebSocketConnection() {
    try {
      this.stompClient = await this.stompService.connect(); // Wait for the connection to establish
      console.log('STOMP client connected');
    } catch (error) {
      console.error('Failed to connect to STOMP client', error);
    }
  }

  createChatRoom(
    name: string,
    type: ChatRoomType,
    userIds: number[]
  ): Observable<ChatRoom> {
    const params = new HttpParams()
      .set('name', name)
      .set('type', type)
      .set('userIds', userIds.join(','));

    return this.http.post<ChatRoom>(
      `http://localhost:9090/api/chatroom/create`,
      params
    );
  }

  addUsersToChatRoom(
    chatRoomId: number,
    userIds: number[]
  ): Observable<ChatRoom> {
    return this.http.post<ChatRoom>(
      `${this.baseUrl}/${chatRoomId}/add-users`,
      userIds
    );
  }

  removeUsersFromChatRoom(
    chatRoomId: number,
    userIds: number[]
  ): Observable<ChatRoom> {
    return this.http.post<ChatRoom>(
      `${this.baseUrl}/${chatRoomId}/remove-users`,
      userIds
    );
  }

  getChatRoom(chatRoomId: number): Observable<ChatRoom> {
    return this.http.get<ChatRoom>(`${this.baseUrl}/${chatRoomId}`);
  }

  getAllChatRooms(): Observable<ChatRoom[]> {
    return this.http.get<ChatRoom[]>('http://localhost:9090/api/chatrooms');
  }

  sendMessage(chatRoomId: number, message: Message): void {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.send(
        `/app/chat/${chatRoomId}`,
        {},
        JSON.stringify(message)
      );
    } else {
      console.error('STOMP client not connected');
    }
  }

  getMessages(chatRoomId: number): Observable<Message[]> {
    return this.http.get<Message[]>(
      `http://localhost:9090/api/chatroom/${chatRoomId}/messages`
    );
  }

  searchUsersByEmail(email: string) {
    return this.http.get(`${this.baseUrl}/search?email=${email}`);
  }

  editMessage(messageId: number, newContent: string): void {
    this.stompClient.send(`/app/chat/edit/${messageId}`, {}, newContent);
  }

  deleteMessage(messageId: number): void {
    this.stompClient.send(`/app/chat/delete/${messageId}`, {});
  }

  subscribeToMessages(
    chatRoomId: number,
    callback: (message: Message) => void
  ): void {
    this.stompClient.subscribe(
      `/topic/${chatRoomId}`,
      (message: { body: string }) => {
        callback(JSON.parse(message.body));
      }
    );
  }
  uploadFile(selectedFile: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', selectedFile);

    return this.http
      .post<{ filePath: string }>(
        'http://localhost:9090/api/files/upload',
        formData
      )
      .pipe(
        map((response) => response.filePath),
        catchError((error) => {
          console.error('File upload failed', error);
          return throwError(error);
        })
      );
  }

  getUserChatRooms(userId: number): Observable<ChatRoom[]> {
    return this.http.get<ChatRoom[]>(
      `http://localhost:9090/api/chatrooms/user/${userId}`
    );
  }
  getGroupChatRoomsByUserId(userId: number): Observable<ChatRoom[]> {
    return this.http.get<ChatRoom[]>(
      `http://localhost:9090/api/chatrooms/groups/user/${userId}`
    );
  }
  getContacts(userId: number): Observable<ChatUser[]> {
    return this.http.get<ChatUser[]>(
      `http://localhost:9090/api/chatroom/contacts/${userId}`
    );
  }
}
