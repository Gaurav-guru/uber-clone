import {Component, Inject, OnInit} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DateStartEnd} from "../model/DateStartEnd";
import {MatDatepickerInputEvent} from "@angular/material/datepicker";
import {Router} from "@angular/router";

@Component({
  selector: 'app-ride-history-dialog',
  templateUrl: './ride-history-dialog.component.html',
  styleUrls: ['./ride-history-dialog.component.css', '../../app.component.css']
})
export class RideHistoryDialogComponent{
  private today = new Date()
  constructor(
    public dialogRef: MatDialogRef<RideHistoryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DateStartEnd
  ) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  changeFrom(event: MatDatepickerInputEvent<Date>) {
    if(event.value != null)
      this.data.start = event.value
  }

  changeTo(event: MatDatepickerInputEvent<Date>) {
    if(event.value != null)
      this.data.end = event.value
  }
}
