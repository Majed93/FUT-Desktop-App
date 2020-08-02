import {Pipe, PipeTransform} from '@angular/core';
import {isNullOrUndefined, isNumber} from 'util';

@Pipe({
  name: 'orderBy'
})
export class OrderByPipe implements PipeTransform {

  transform(array, orderBy, desc = false) {
    if (!orderBy || orderBy.trim() === '') {
      return array;
    }

    // descending
    if (desc) {
      return Array.from(array).sort((item1: any, item2: any) => {
        // Need to do this because it might be nested props.
        if (orderBy.includes('.')) {
          const temp = orderBy.split('.');
          return this.orderByComparator(item1[temp[0]][temp[1]], item2[temp[0]][temp[1]]);
        }
        return this.orderByComparator(item1[orderBy], item2[orderBy]);
      });
    } else {
      // not desc
      return Array.from(array).sort((item1: any, item2: any) => {
        // Need to do this because it might be nested props.
        if (orderBy.includes('.')) {
          const temp = orderBy.split('.');
          return this.orderByComparator(item2[temp[0]][temp[1]], item1[temp[0]][temp[1]]);
        }
        return this.orderByComparator(item2[orderBy], item1[orderBy]);
      });
    }

  }

  orderByComparator(a: string, b: string): number {
    if (!isNullOrUndefined(a) || !isNullOrUndefined(b)) {
      if ((!isNumber(a) || !(isNumber(b)))) {
        // Isn't a number so lowercase the string to properly compare
        if (a.toLowerCase() < b.toLowerCase()) {
          return -1;
        }
        if (a.toLowerCase() > b.toLowerCase()) {
          return 1;
        }
      } else {
        // Parse strings as numbers to compare properly
        if (parseFloat(a) < parseFloat(b)) {
          return -1;
        }
        if (parseFloat(a) > parseFloat(b)) {
          return 1;
        }
      }
    }
    return 0; // equal each other
  }
}
