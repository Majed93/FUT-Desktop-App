import {Pipe, PipeTransform} from '@angular/core';
import {GlobalService} from '../globals/global.service';
import {Player} from '../domain/player';

@Pipe({
  name: 'playerFilter'
})
export class PlayerSearchPipe implements PipeTransform {

  constructor(private global: GlobalService) {
    // NOOP
  }

  /**
   * Transform given values
   *
   * @param {Player[]} values Array to filter against
   * @param {string} searchString Search criteria
   * @param {boolean} closeMenu If true then we're closing the menu
   * @returns {Player[]} Found players
   */
  transform(values: Player[], searchString: string, closeMenu: boolean): Player[] {
    if (closeMenu) {
      return null;
    } else if (!searchString) {
      return null;
    } else {
      if (this.containsLetters(searchString)) {
        // Filter by given text
        return values.filter((p: any) => {
          searchString = this.global.removeAccents(searchString);
          p.firstName = this.global.removeAccents(p.firstName);
          p.lastName = this.global.removeAccents(p.lastName);
          p.commonName = this.global.removeAccents(p.commonName);
          const firstName = p.firstName.toLowerCase().includes(searchString.toLowerCase());
          const lastName = p.lastName.toLowerCase().includes(searchString.toLowerCase());
          const commonName = p.commonName.toLowerCase().includes(searchString.toLowerCase());

          return (firstName + lastName + commonName);
        }).slice(0, 5); // return only first 5 results
      } else {
        return values.filter((p: any) => {
          return p.assetId.toString().includes(searchString.toLowerCase());
        }).slice(0, 5); // return only first 5 results
      }
    }
  }

  /**
   * Check if the search string contains a digit.
   *
   * @param {string} str search string
   * @returns {boolean} True if contains a letter otherwise false.
   */
  private containsLetters(str: string) {
    return isNaN(Number(str));
  }
}
