import { Component, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import emailjs from '@emailjs/browser';
import { FeaturesComponent } from '../../components/features/features.component';

@Component({
  selector: 'app-contatti',
  standalone: true,
  imports: [CommonModule, FormsModule, FeaturesComponent],
  templateUrl: './contatti.component.html',
  styleUrls: ['./contatti.component.css']
})
export class ContattiComponent implements AfterViewInit {


  @ViewChild('chatbox') private chatbox!: ElementRef;

  ngAfterViewInit(): void {
    this.scrollToBottom();
  }

  private scrollToBottom() {
    try {
      this.chatbox.nativeElement.scrollTop =
        this.chatbox.nativeElement.scrollHeight;
    } catch (err) {}
  }

  // -----------------------------
  // EMAILJS
  // -----------------------------
  constructor() {
    emailjs.init({
      publicKey: "orXEGyJ2mrr8RIabx"
    });
  }

  onSubmit(e: Event) {
    e.preventDefault();
    const target = e.target as HTMLFormElement;

    emailjs.sendForm('service_0y4ibhm', 'template_7t336qf', target)
      .then(() => {
        alert('Email inviata!');
        target.reset();
      })
      .catch(err => alert('Errore: ' + JSON.stringify(err)));
  }

  // -----------------------------
  // CHATBOT
  // -----------------------------
  chatOpen = false;
  userMessage = "";
  messages: { sender: 'user' | 'bot', text: string }[] = [];

  toggleChat() {
    this.chatOpen = !this.chatOpen;


    setTimeout(() => this.scrollToBottom(), 50);
  }

  async sendMessage() {
    if (!this.userMessage.trim()) return;


    this.messages.push({ sender: 'user', text: this.userMessage });
    const msgToSend = this.userMessage;
    this.userMessage = "";

    this.scrollToBottom();

    try {
      const response = await fetch("http://localhost:8080/api/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ message: msgToSend })
      });

      if (!response.ok) {
        this.messages.push({
          sender: "bot",
          text: "❌ Errore di comunicazione con il server."
        });
        this.scrollToBottom();
        return;
      }

      const data = await response.json();

      this.messages.push({
        sender: 'bot',
        text: data.reply
      });

      setTimeout(() => {
        this.scrollToBottom();
      }, 50);

    } catch (error) {
      this.messages.push({
        sender: 'bot',
        text: "❌ Errore: impossibile contattare il server."
      });

      this.scrollToBottom();
    }
  }

}
