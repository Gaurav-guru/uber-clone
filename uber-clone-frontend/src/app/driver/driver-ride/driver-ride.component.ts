import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Location} from "../../shared/model/Location";
import {MapService} from "../../shared/map/map.service";
import {DriverService} from "../driver.service";
import {NotificationService} from "../../shared/notification-service/notification.service";

@Component({
  selector: 'app-driver-ride',
  templateUrl: './driver-ride.component.html',
  styleUrls: ['../../passenger/ride-estimates/ride-estimates.component.css', './driver-ride.component.css', '../../app.component.css']
})
export class DriverRideComponent implements OnInit {
  constructor(public mapService: MapService,
              private driverService: DriverService,
              private notificationService: NotificationService) {
  }

  searchForm: FormGroup = new FormGroup({
    departure: new FormControl('', { validators: [Validators.required] }),
    destination: new FormControl('', { validators: [Validators.required] }),
  });

  @Output() chosenDeparture = new EventEmitter<Location>();
  @Output() chosenDestination = new EventEmitter<Location>();
  @Output() bothLocationsSelected = new EventEmitter<boolean>();

  rideInProgress = false;

  ngOnInit() {
    if (this.driverService.receivedRide) {
      const ride = this.driverService.currentRide;
      if (ride?.driver.id == parseInt(sessionStorage.getItem("user_id")!)) {
        this.searchForm.disable();
        this.searchForm.controls['departure'].setValue(ride?.locations[0].departure.address);
        this.searchForm.controls['destination'].setValue(ride?.locations[0].destination.address);
        this.mapService.setIsDriver(true);
        this.mapService.setDeparture(<Location>ride?.locations[0].departure);
        this.mapService.setDestination(<Location>ride?.locations[0].destination);
        this.bothLocationsSelected.emit(true);
      }
    } else {
      this.searchForm.disable();
      this.searchForm.controls['departure'].setValue('Gogoljeva Street 16-28, 21102, Novi Sad, South Bačka District, Vojvodina');
      this.searchForm.controls['destination'].setValue('Sime Matavulja Street, 21102, Novi Sad, South Bačka District, Vojvodina');
      this.mapService.setIsDriver(true);
      this.mapService.setDeparture(new Location("Vladimir Perić Valter Street 1-3, 21102, Novi Sad, South Bačka District, Vojvodina", 19.850956499576572, 45.245972209988224));
      this.mapService.setDestination(new Location("Gogoljeva Street 16-28, 21102, Novi Sad, South Bačka District, Vojvodina", 19.833455085754398, 45.24670303487374));
      this.bothLocationsSelected.emit(true);
      this.mapService.rideInProgress$.subscribe(rideInProgress => {
        this.rideInProgress = rideInProgress;
      });
    }
  }

  public drawRoute(): void {
    if (this.searchForm.valid) {
      this.bothLocationsSelected.emit(true);
    }
  }

  public accept(): void {
    if (!this.driverService.rideToDepartureDone) {
      this.notificationService.createNotification('You must first reach the starting point to begin the ride.', 2000);
      return;
    }
    this.driverService.acceptRide().subscribe({
      next: (value) => {
        this.driverService.startRide().subscribe({
          next: (value) => {
            this.mapService.setRideInProgress(true);
            this.chosenDeparture.emit(this.driverService.currentRide?.locations[0].departure);
            this.chosenDestination.emit(this.driverService.currentRide?.locations[0].destination);
            this.bothLocationsSelected.emit(true);
            this.mapService.simulateMovementToDestination = true;
            this.mapService.setSimulateToDestination(true);
            this.driverService.rideToDepartureDone = false;
          },
          error: (error) => {
            console.log(error.error.message);
          }
        })
      },
      error: (error) => {
        console.log(error.error.message);
      }
    });
  }

  public deny(): void {
    this.mapService.setRideDenied(true);
  }

  public finish(): void {
    if (!this.driverService.rideToDestinationDone) {
      this.notificationService.createNotification('You must first reach the destination to finish the ride.', 2000);
      return;
    }

    this.driverService.endRide().subscribe({
      next: (value) => {
        this.mapService.setRideInProgress(false);
        this.mapService.setRideReceived(false);
        setTimeout(() => {
          window.location.reload();
        }, 250);
      },
      error: (error) => {
        console.log(error.error.message);
      }
    });
  }
}
