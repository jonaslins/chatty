import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Message } from "../message";
import { ChatRoomService } from "../chat-room.service";

@Component({
  selector: 'app-chat-room',
  templateUrl: './chat-room.component.html',
  styleUrls: ['./chat-room.component.css']
})
export class ChatRoomComponent implements OnInit {

  message: Message = new Message();
  chat: Array<Message> = [];

  constructor(
    private chatRoomService: ChatRoomService,
    private router: Router
  ) { }

  ngOnInit() {
    this.subscribtChat();
  }

  subscribtChat(){
    this.chatRoomService
      .subscribeToChat<string>("default")
      .subscribe(data => {
        let dataJson = JSON.parse(data)
        console.log(dataJson)
        let message = { author: dataJson.author, text: dataJson.text, date: new Date(dataJson.date)}
        this.chat = [...this.chat, message];
      })
  }

  save() {
    this.chatRoomService
      .sendMessage(this.message)
      .subscribe(data => {
        this.message = { author: this.message.author, text: null}
      })
  }

  onSubmit() {
    this.save();
  }
}
