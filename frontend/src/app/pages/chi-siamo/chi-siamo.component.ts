import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeaturesComponent } from '../../components/features/features.component';

@Component({
  selector: 'app-chi-siamo',
  standalone: true,
  imports: [CommonModule, FeaturesComponent],
  templateUrl: './chi-siamo.component.html',
  styleUrls: ['./chi-siamo.component.css']
})
export class ChiSiamoComponent {
  title = 'Karma Rende - Chi siamo';
}
