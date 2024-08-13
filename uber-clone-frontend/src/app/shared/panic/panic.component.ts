import { Component } from '@angular/core';
import {MapService} from "../map/map.service";

@Component({
  selector: 'app-panic',
  templateUrl: './panic.component.html',
  styleUrls: ['./panic.component.css', '../../app.component.css']
})
export class PanicComponent {
  constructor(public mapService: MapService) {}

}
