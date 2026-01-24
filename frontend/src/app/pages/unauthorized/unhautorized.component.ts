import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="unauthorized-container">
      <h1>🚫 Accesso Negato</h1>
      <p>Non hai i permessi necessari per accedere a questa pagina.</p>
      <a routerLink="/" class="btn-home">Torna alla Home</a>
    </div>
  `,
  styles: [`
    .unauthorized-container {
      text-align: center;
      padding: 4rem 2rem;
      min-height: 60vh;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }
    h1 {
      font-size: 3rem;
      color: #f44336;
      margin-bottom: 1rem;
    }
    p {
      font-size: 1.2rem;
      margin-bottom: 2rem;
      color: #666;
    }
    .btn-home {
      display: inline-block;
      padding: 0.8rem 2rem;
      background-color: #4CAF50;
      color: white;
      text-decoration: none;
      border-radius: 4px;
      transition: background-color 0.3s;
      font-weight: bold;
    }
    .btn-home:hover {
      background-color: #45a049;
    }
  `]
})
export class UnauthorizedComponent {}
