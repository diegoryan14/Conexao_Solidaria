import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IEventos } from '../eventos.model';

@Component({
  standalone: true,
  selector: 'app-eventos-detail',
  templateUrl: './eventos-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class EventosDetailComponent {
  eventos = input<IEventos | null>(null);

  previousState(): void {
    window.history.back();
  }
}
