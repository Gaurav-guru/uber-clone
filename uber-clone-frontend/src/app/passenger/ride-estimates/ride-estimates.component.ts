import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {OpenStreetMapProvider} from 'leaflet-geosearch';
import {Location} from "../../shared/model/Location";
import {RouteEstimates} from "../../shared/model/RouteEstimates";
import {MapService} from "../../shared/map/map.service";

@Component({
  selector: 'app-ride-estimates',
  templateUrl: './ride-estimates.component.html',
  styleUrls: ['./ride-estimates.component.css','../../app.component.css']
})
export class RideEstimatesComponent implements OnInit{

  mapRoutingOnly = false;
  modeButtonText = 'Mark on Map';  // Translated from 'Označi na mapi'
  returnedEstimates = RouteEstimates.getEmptyRouteEstimates();
  @Output() orderIsClicked = new EventEmitter<boolean>();
  @Input() enableReservation = false;
  valid = false;

  searchForm : FormGroup = new FormGroup({
    departure: new FormControl('', { validators: [Validators.required] }),
    destination: new FormControl('', { validators: [Validators.required] }),
  });

  estimatesForm : FormGroup = new FormGroup({
    time: new FormControl(''),
    price: new FormControl(''),
  });

  provider = new OpenStreetMapProvider();
  destinationSuggestions : Location[] = [];
  departureSuggestions : Location[] = [];

  constructor(private mapService : MapService) { }
  
  ngOnInit() {
    this.estimatesForm.disable();
    this.mapService.estimates$.subscribe(estimates => this.returnedEstimates = estimates);
    this.mapService.returnEstimates$.subscribe(() => {
      this.fillEstimatesForm();
      this.valid = true;
    });
    this.mapService.clearMap$.subscribe(() => {
      this.clearEstimatesForm();
      this.clearSearchForm();
      this.valid = false;
    });
    this.mapService.mapRoutingOnly$.subscribe(status => this.mapRoutingOnly = status);
  }

  private mapGeoSearchObjectToLocation(geoSearchObj : any) : Location {
    return new Location(
      geoSearchObj.label,
      geoSearchObj.x,
      geoSearchObj.y)
  }

  private filterToTop5Suggestions(suggestions : object[]) : Location[] {
    suggestions = suggestions.slice(0, 5);
    const top5 : Location[] = [];
    for(const suggestion of suggestions) {
      top5.push(this.mapGeoSearchObjectToLocation(suggestion));
    }
    return top5;
  }

  public getDepartureSuggestions() {
    setTimeout(() => this.filterDepartureSuggestions(), 1000);
  }

  public getDestinationSuggestions() {
    setTimeout(() => this.filterDestinationSuggestions(), 1000);
  }

  private async filterDepartureSuggestions(){
    this.departureSuggestions = await this.provider.search({query: this.searchForm.value.departure})
      .then((results: object[]) => this.filterToTop5Suggestions(results));
  }

  private async filterDestinationSuggestions()  {
    this.destinationSuggestions = await this.provider.search({query: this.searchForm.value.destination})
      .then((results: object[]) => this.filterToTop5Suggestions(results));
  }

  public displayLabel(location : Location) : string { return location.address; }

  public sendLocationInputToDraw(value : Location, which : string) {
    if(which == 'departure') this.mapService.setDeparture(value);
    else this.mapService.setDestination(value);
  }
  
  public drawRoute() {
    if(this.searchForm.valid) {
      this.mapService.setDrawRouteRequest();
      this.valid = true;
    }
  }

  public clearMapMarkersAndRoute() { this.mapService.setClearMap(); }
  
  toggleMode() {
    if(!this.mapRoutingOnly) this.disableSearch();
    else this.enableSearch();
  }
  
  enableSearch() {
    this.modeButtonText = 'Mark on Map';  // Translated from 'Označi na mapi'
    this.searchForm.enable();
    this.mapService.setMapRoutingOnly(false);
    this.clearMapMarkersAndRoute();
  }

  disableSearch() {
    this.modeButtonText = 'Enter in Fields';  // Translated from 'Unesi u polja'
    this.clearSearchForm();
    this.searchForm.disable();
    this.mapService.setMapRoutingOnly(true);
    this.clearMapMarkersAndRoute();
  }

  public showOrderForm() { this.orderIsClicked.emit(true); }

  public fillEstimatesForm() {
    this.estimatesForm.enable();
    this.estimatesForm.controls['time'].setValue(this.returnedEstimates.estimatedTimeInMinutes + " min");
    this.estimatesForm.controls['price'].setValue(this.returnedEstimates.estimatedCost + " rs");
    this.estimatesForm.disable();
  }

  public clearEstimatesForm() {
    this.estimatesForm.enable();
    this.estimatesForm.controls['time'].setValue('');
    this.estimatesForm.controls['price'].setValue('');
    this.estimatesForm.disable();
  }

  public clearSearchForm() {
    this.searchForm.controls['destination'].setValue('');
    this.searchForm.controls['departure'].setValue('');
  }
}
