import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter',
  standalone: true, // Adicione essa linha para torná-lo standalone
})
export class FilterPipe implements PipeTransform {
  transform(items: any[], searchTerm: string): any[] {
    if (!items || !searchTerm) {
      return items;
    }
    return items.filter(
      item => item.nome.toLowerCase().includes(searchTerm.toLowerCase()), // Verifique se 'nome' é a propriedade correta
    );
  }
}
