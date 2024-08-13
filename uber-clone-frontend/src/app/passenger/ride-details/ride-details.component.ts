import {Component, EventEmitter, Input, Output} from '@angular/core';
import {RideCreated} from "../../shared/model/RideCreated";
import * as moment from "moment";

@Component({
  selector: 'app-ride-details',
  templateUrl: './ride-details.component.html',
  styleUrls: ['./ride-details.component.css',"../../app.component.css"]
})
export class RideDetailsComponent {

  @Input() passengerAdded = false;
  @Output() detailsClose = new EventEmitter<boolean>();
  @Input() route = '';
  @Input() scheduleTime = '';
  @Input() totalPrice = '';
  @Input() pricePerPerson = '';
  pay() {
    this.detailsClose.emit(true);
  }
}
