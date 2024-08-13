import { Component } from '@angular/core';
import {MapService} from "../map/map.service";


@Component({
  selector: 'app-ride-inconsistency',
  templateUrl: './ride-inconsistency.component.html',
  styleUrls: ['./ride-inconsistency.component.css', '../../app.component.css']
})
export class RideInconsistencyComponent {
  constructor(public mapService: MapService) {}
}
