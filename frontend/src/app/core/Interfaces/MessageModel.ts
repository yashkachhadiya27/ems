export interface Message {
  id: number;
  senderId: number;
  chatRoomId: number;
  content: string;
  timestamp: string;
  contentType: MessageType;
  filePath?: string | null;
  deleted: boolean;
  edited: boolean;
}
export interface ChatRoom {
  id: number;
  name: string;
  type: ChatRoomType;
  users: ChatUser[];
}

export enum ChatRoomType {
  PRIVATE = 'PRIVATE',
  GROUP = 'GROUP',
}

export interface ChatUser {
  id: number;
  image: string;
  name: string;
  email: string;
  status: UserStatus;
}

export enum UserStatus {
  ONLINE = 'ONLINE',
  OFFLINE = 'OFFLINE',
}

export enum MessageType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  FILE = 'FILE',
  VIDEO = 'VIDEO',
}
