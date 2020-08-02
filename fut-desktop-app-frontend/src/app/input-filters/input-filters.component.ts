import {Component, EventEmitter, HostListener, Input, OnInit, Output} from '@angular/core';
import {FormControl} from '@angular/forms';
import {debounceTime} from 'rxjs/operators';

@Component({
  moduleId: module.id,
  selector: 'app-input-filters',
  templateUrl: 'input-filters.component.html',
  styleUrls: ['input-filters.component.scss']
})
export class InputFiltersComponent implements OnInit {

  ID_PREFIX = 'input-filer-';

  @Input() id;

  /** List to filter */
  @Input() allLists = [];
  /** Output value to consumer */
  @Output() outputValue: EventEmitter<any> = new EventEmitter();
  /** Placeholder text for input*/
  @Input() placeholder;
  /** Display value instead of description */
  @Input() displayValue = false;

  itemsFound = new Set();
  searchTerm: FormControl = new FormControl();
  itemSelected: boolean;
  selectedItem: any;

  constructor() {
  }

  /**
   * Close menu if we click outside of it.
   */
  @HostListener('document:click', ['item']) clickedOutside($event) {
    this.itemsFound = new Set();
  }

  ngOnInit() {
    this.searchTerm.valueChanges.pipe(debounceTime(400))
      .subscribe(data => {
        if (data && !this.itemSelected) {
          this.itemsFound = this.searchItem(data);
        }
      });
  }

  /**
   * Populate search terms
   *
   * @param term
   * @returns {Set<any>}
   */
  searchItem(term) {
    this.itemSelected = false;
    const foundItems = new Set<any>();
    this.itemsFound = new Set<any>();

    this.allLists.forEach(p => {
      if (foundItems.size > 4) {
        return foundItems;
      }

      let alreadyContains = false;
      // if contains 'p'
      // If first name and last name are the same and then asset id is greater, don't add.
      foundItems.forEach(aap => {
        if (aap.value === p.value) {
          alreadyContains = true;
        }
      });

      if (alreadyContains) {
        return foundItems;
      }

      const name = this.displayValue ? p.value : p.description;
      // Search full name with case insensitivity.
      if (name.search(new RegExp(term, 'i')) >= 0) {
        foundItems.add(p);
      }
    });

    return foundItems;
  }

  /**
   * Player search filter listener.
   *
   * @param {Event} event
   */
  itemSearchChange(event: Event) {
    this.itemSelected = false;
  }

  /**
   * Select item from search box.
   */
  selectItem(item: any) {
    this.setSelectedItem(item);
    this.outputValue.emit(this.selectedItem);
  }

  /**
   * Clear inputs
   */
  reset() {
    this.itemsFound = new Set();
    this.searchTerm.reset();
    this.itemSelected = false;
    this.selectedItem = null;
  }

  /**
   * Set item
   *
   * @param itemValue Item value in list. (Assuming item has a value)
   */
  setValue(itemValue: number) {
    this.allLists.forEach(x => {
      if (x.value === itemValue) {
        this.setSelectedItem(x);
      }
    });
  }

  /**
   * Helper to set selected items and reset other variables
   * @param item Item to set
   */
  setSelectedItem(item: any) {
    this.selectedItem = item;
    this.searchTerm.setValue(this.displayValue ? this.selectedItem.value : this.selectedItem.description);
    this.itemsFound = new Set();
    this.itemSelected = true;
  }
}
