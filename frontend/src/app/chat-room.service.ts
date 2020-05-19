import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from './message';

@Injectable({
  providedIn: 'root'
})
export class ChatRoomService {

  private defaultRoom = "general"
  private baseUrl = 'http://localhost:8080/chatty';

  constructor(private http: HttpClient) { }

  sendMessage(message: Message): Observable<any> {
    return this.http.post(`${this.baseUrl}/${this.defaultRoom}/msg`, message);
  }

  private newEventSource(room: string): EventSource {
    return new EventSource(`${this.baseUrl}/${this.defaultRoom}/subscribe`);
  }

  subscribeToChat<R>(path: string): Observable<R> {
    return new Observable(observer => {
      const eventSource = this.newEventSource(path);
      eventSource.onmessage = event => {
        observer.next(event.data);
      };
      eventSource.onerror = () => {
        if (eventSource.readyState !== eventSource.CONNECTING) {
          observer.error('An error occurred.');
        }
        eventSource.close();
        observer.complete();
      };
      return () => {
        eventSource.close();
      };
    });
  }

}
