import { Injectable } from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import { Observable } from 'rxjs';
import SockJS from 'sockjs-client';
@Injectable({
  providedIn: 'root',
})
export class StompService {
  private stompClient: any;
  private reconnectAttempts: number = 0;
  private maxReconnectAttempts: number = 5;
  private reconnectDelay: number = 1000;

  constructor() {
    this.connect();
  }

  connect(): Promise<any> {
    return new Promise((resolve, reject) => {
      const socket = new SockJS('http://localhost:9090/ws');
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect(
        {},
        (frame: string) => {
          console.log('Connected: ' + frame);
          this.reconnectAttempts = 0;
          resolve(this.stompClient);
        },
        (error: any) => {
          console.error('WebSocket connection error:', error);
          this.reconnect();
          reject(error);
        }
      );
    });
  }

  onConnect() {
    console.log('Connected to WebSocket');
    this.reconnectAttempts = 0;
  }

  onError(error: any) {
    console.error('WebSocket connection error:', error);
    this.reconnect();
  }

  reconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts);
      console.log(`Reconnecting in ${delay / 1000} seconds...`);

      setTimeout(() => {
        this.connect()
          .then((client) => {
            // Optionally do something with the reconnected client
          })
          .catch((err) => {
            console.error('Reconnection failed:', err);
          });
      }, delay);
    } else {
      console.error('Max reconnect attempts reached. Could not reconnect.');
    }
  }

  onMessage(destination: string): Observable<any> {
    return new Observable((observer) => {
      const subscription = this.stompClient.subscribe(
        destination,
        (message: any) => {
          observer.next(JSON.parse(message.body));
        }
      );

      return () => {
        subscription.unsubscribe();
      };
    });
  }
  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected from WebSocket');
      });
    }
  }
}
