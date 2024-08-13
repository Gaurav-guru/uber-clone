import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-ride-not-reserved-popup',
  templateUrl: './ride-not-reserved-popup.component.html',
  styleUrls: ['./ride-not-reserved-popup.component.css','../../app.component.css']
})
export class RideNotReservedPopupComponent {

  @Input() popupOpened = true;
  @Output() popupClosed = new EventEmitter<boolean>();
  @Input() errorMessage = '';
  close(){
    this.popupClosed.emit(true);
  }
}
