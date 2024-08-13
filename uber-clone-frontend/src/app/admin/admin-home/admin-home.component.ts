import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, Subject} from "rxjs";
import {Location} from "../../shared/model/Location";
import {MapService} from "../../shared/map/map.service";

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css', '../../app.component.css']
})

export class AdminHomeComponent implements OnInit{
  public departure : BehaviorSubject<Location> = new BehaviorSubject<Location>(Location.getEmptyLocation());
  public destination : BehaviorSubject<Location> = new BehaviorSubject<Location>(Location.getEmptyLocation());
  public passenger : Subject<boolean> = new Subject<boolean>();
  public displayCar = true;

  constructor(public mapService : MapService) {
  }

  ngOnInit(): void {
    console.log('Loaded Admin Home');
  }
}
