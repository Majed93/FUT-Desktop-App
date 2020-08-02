import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {BidCriteriaType} from './bid.criteria.type';

// jQuery imports for modal.
declare const jquery: any;
declare const $: any;

/**
 * Component for bidding criteria
 */
@Component({
  moduleId: module.id,
  selector: 'fut-bid-criteria',
  templateUrl: 'bid-criteria.component.html',
  styleUrls: ['bid-criteria.component.scss']
})
export class BidCriteriaComponent implements OnInit, OnDestroy {

  /** Emitter to listen for toggling modal. */
  @Output() toggleModal = new EventEmitter<any>();

  @Input() bidCriteria: BidCriteriaType;

  /**
   * Constructor.
   */
  constructor() {
    // NOOP
  }

  /**
   * On initialization
   */
  ngOnInit(): void {
    this.bidCriteria = {
      bid: true,
      binOnly: false,
      binFirst: false,
      timeLimit: 3600,
      maxResults: 9,
      minBuyNow: 0
    };

    this.hideModal();
  }

  /**
   * On destroy
   */
  ngOnDestroy(): void {
    this.hideModal();
  }

  /**
   * Hide the modal.
   */
  hideModal(): void {
    $('#bidCriteriaModel').modal('hide');
  }

  /**
   * Show the modal.
   */
  showModal(): void {
    $('#bidCriteriaModel').modal('show');
  }

  /**
   * If Bid is false then bin first must also be false.
   */
  onBidChange(event: Event): void {
    if (!event) {
      this.bidCriteria.binFirst = false;
    }
  }

  /**
   * Logic to determine what can be true AND false and what cannot.
   * binOnly AND binFirst cannot both be true - it's either one or the other
   */
  onBinFirstChange(event: Event): void {
    if (event) {
      this.bidCriteria.bid = true;
      this.bidCriteria.binOnly = false;
    }
  }

  /**
   * Same docs as above
   */
  onBinOnlyChange(event: Event): void {
    if (event) {
      this.bidCriteria.binFirst = false;
      this.bidCriteria.bid = false;
    }
  }
}
