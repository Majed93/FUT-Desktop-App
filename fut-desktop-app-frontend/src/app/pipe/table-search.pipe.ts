import {Pipe, PipeTransform} from '@angular/core';
import {GlobalService} from '../globals/global.service';

@Pipe({
  name: 'tableFilter'
})
export class TableSearchPipe implements PipeTransform {

  constructor(private global: GlobalService) {
    // NOOP
  }

  transform(items: any, searchString: string): any {
    if (searchString && searchString !== '') {
      return items.filter((p: any) => {
        searchString = this.global.removeAccents(searchString);
        p.firstName = this.global.removeAccents(p.firstName);
        p.lastName = this.global.removeAccents(p.lastName);
        p.commonName = this.global.removeAccents(p.commonName);
        const firstName = p.firstName.toLowerCase().includes(searchString.toLowerCase());
        const lastName = p.lastName.toLowerCase().includes(searchString.toLowerCase());
        const commonName = p.commonName.toLowerCase().includes(searchString.toLowerCase());

        return (firstName + lastName + commonName); // Not an error
      })
    } else {
      return items;
    }
  }
}
