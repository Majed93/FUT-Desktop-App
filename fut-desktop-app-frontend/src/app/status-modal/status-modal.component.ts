import {AfterContentChecked, AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {StateService} from 'app/status-modal/state-service';

// jQuery imports for closing modal.
declare const jquery: any;
declare const $: any;

/**
 * Component to handle status modal.
 */
@Component({
  moduleId: module.id,
  selector: 'app-status-modal',
  templateUrl: 'status-modal.component.html',
  styleUrls: ['status-modal.component.scss']
})
export class StatusModalComponent implements OnInit, AfterViewInit, AfterContentChecked {

  @Input() textareaText: string;
  @Input() disableClose: boolean;

  @Input() pauseEndpoint: string;
  @Input() stopEndpoint: string;

  paused = false;

  @Output() clearClicked = new EventEmitter<any>();
  @Input() statusTextRows = 30;

  /**
   * Constructor
   */
  constructor(private stateService: StateService, private cdr: ChangeDetectorRef) {
    // NOOP
  }

  /**
   * On init.
   */
  ngOnInit(): void {
    this.stateService.pauseEndpoint = this.pauseEndpoint;
    this.stateService.stopEndpoint = this.stopEndpoint;
  }

  ngAfterViewInit(): void {
    this.cdr.detectChanges();
  }

  ngAfterContentChecked(): void {
    this.cdr.detectChanges();
  }

  /**
   * On cancel.
   */
  onCancel(): void {
    this.stateService.stop().subscribe(resp => {
      console.log('Stopping: ' + resp);
    }, error => {
      console.error(error);
    });
  }

  /**
   * On pause.
   */
  onPause(): void {
    this.stateService.pause().subscribe(resp => {
      console.log('Paused: ' + resp);
      this.paused = !this.paused;
    }, error => {
      console.error(error);
    });
  }

  /**
   * Clear textarea
   */
  clear(): void {
    this.clearClicked.emit();
  }

  /**
   * Hide the modal
   */
  hideModal(): void {
    $('#reListingModal').modal('hide');
  }
}
