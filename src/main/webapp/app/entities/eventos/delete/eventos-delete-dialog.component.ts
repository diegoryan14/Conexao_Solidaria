import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IEventos } from '../eventos.model';
import { EventosService } from '../service/eventos.service';

@Component({
  standalone: true,
  templateUrl: './eventos-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class EventosDeleteDialogComponent {
  eventos?: IEventos;

  protected eventosService = inject(EventosService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.eventosService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
