import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MapComponent} from "./map/map.component";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {PanicComponent} from './panic/panic.component';
import {ChangePasswordComponent} from './change-password/change-password.component';
import {MatListModule} from "@angular/material/list";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LeafletModule} from "@asymmetrik/ngx-leaflet";
import {RideInconsistencyComponent} from "./ride-inconsistency/ride-inconsistency.component";
import {RideHistoryComponent} from "./ride-history/ride-history.component";
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatDialogModule} from "@angular/material/dialog";
import {RideHistoryDialogComponent} from "./ride-history-dialog/ride-history-dialog.component";
import {MatDatepickerModule} from "@angular/material/datepicker";
import * as CanvasJSAngularChart from '../../assets/canvasjs.angular.component';
const CanvasJSChart = CanvasJSAngularChart.CanvasJSChart;

@NgModule({
  declarations: [
    CanvasJSChart,
    RideHistoryComponent,
    RideHistoryDialogComponent,
    MapComponent,
    PanicComponent,
    ChangePasswordComponent,
    RideInconsistencyComponent,
  ],
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    LeafletModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    MatDialogModule,
    MatDatepickerModule
  ],
  exports: [
    MapComponent,
    PanicComponent,
    ChangePasswordComponent,
    RideInconsistencyComponent,
    RideHistoryComponent
  ],
})
export class SharedModule {
}
