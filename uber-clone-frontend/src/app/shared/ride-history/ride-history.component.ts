import {Component, OnInit, ViewChild} from '@angular/core';
import {NotificationService} from "../../shared/notification-service/notification.service";
import {UserService} from "../../shared/user.service";
import {MatDialog} from "@angular/material/dialog";
import {RideHistoryDialogComponent} from "../ride-history-dialog/ride-history-dialog.component";
import {DateStartEnd} from "../model/DateStartEnd";
import {MatPaginator} from "@angular/material/paginator";
import {MatTableDataSource} from "@angular/material/table";
import {SimpleRide} from "../model/SimpleRide";
import {RideHistoryService} from "./ride-gistory.service";
import {RideCreated} from "../model/RideCreated";

export interface RideShort{
  route:string
  startEnd:string
  price:string
}

@Component({
  selector: 'app-ride-history',
  templateUrl: './ride-history.component.html',
  styleUrls: ['./ride-history.component.css', '../../app.component.css']
})

export class RideHistoryComponent implements OnInit{
  public chartsEnabled = 'none'
  public tableEnabled = ''
  public dates: DateStartEnd | undefined
  public selected: RideShort | undefined
  public columns: string[]  = ["rute", "startEnd", "price"]
  public dataSource!: MatTableDataSource<SimpleRide>;
  public rides!: SimpleRide[];
  public ridesFull!: RideCreated[];

  @ViewChild(MatPaginator) paginator!: MatPaginator

  constructor(private notificationService : NotificationService,
              private userService : UserService,
              private dialog: MatDialog,
              private rideService: RideHistoryService) { }

  generateReport(): void{
    const dialogRef = this.dialog.open(RideHistoryDialogComponent, {data: this.dates})
  }

  ngOnInit(): void {
    this.rideService.getPassengerRidesRequest(parseInt(<string>sessionStorage.getItem("user_id"))).subscribe(
      (res) =>{
        this.ridesFull = res.results
        for(let i = 0; i < this.ridesFull.length; i++){
          const temp = this.ridesFull[i]
          this.rides.push(new SimpleRide(temp.locations[0].departure.address + "-" + temp.locations[0].destination.address,
            temp.startTime.toString() + " / " + temp.endTime, temp.totalCost.toString()));
          this.dataSource = new MatTableDataSource<SimpleRide>(this.rides);
          this.dataSource.paginator = this.paginator;
        }
      }
    )
    this.dates = new DateStartEnd(new Date(), new Date())
    this.dataSource.data = [
      {rute:"fawf", startEnd:"faga", price:"fawfae"},
      {rute:"fawdawff", startEnd:"fagaegaga", price:"faawfae"},
      {rute:"fawawgff", startEnd:"fagagea", price:"fawfdae"},
      {rute:"fawagwwaf", startEnd:"fagagea", price:"fawgfae"}]
  }

  chart: any;

  numberOfRidesOptions = {
    theme: "light2",
    animationEnabled: true,
    zoomEnabled: true,
    title: {
      text: "Broj vožnji"
    },
    axisY: {
      labelFormatter: (e: any) => {

        const order = Math.max(Math.floor(Math.log(e.value) / Math.log(1000)), 0);
        return (e.value / Math.pow(1000, order));
      }
    },
    data: [{
      type: "line",
      xValueFormatString: "DD/MM/YYYY",
      yValueFormatString: "###",
      dataPoints: [
        { x: new Date(2023, 1, 1), y: 1 },
        { x: new Date(2023, 2, 5), y: 20 }
      ]
    }]
  }
}
