export class Location {
  address : string;
  longitude: number;
  latitude : number;

  constructor(address : string,longtitude : number, latitude : number) {
    this.address = address;
    this.longitude = longtitude;
    this.latitude = latitude
  }

  static getEmptyLocation() : Location {
    return new Location('',0,0);
  }
}
