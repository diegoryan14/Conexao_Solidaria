import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

@Pipe({
  standalone: true,
  name: 'customDateTime',
})
export class CustomDateTimePipe implements PipeTransform {
  transform(value: any): string | null {
    const datePipe = new DatePipe('en-US');
    return datePipe.transform(value, 'dd/MM/yyyy HH:mm:ss');
  }
}
