import {Route} from "./Route";
import {UserIdEmail} from "./UserIdEmail";

export class FavoriteRideCreation {
  favoriteName : string;
  locations : Route[];
  passengers : UserIdEmail[];
  babyTransport : boolean;
  petTransport : boolean;
  vehicleType : string;

  constructor(favoriteName: string,
              locations: Route[],
              passengers: UserIdEmail[],
              babyTransport: boolean,
              petTransport: boolean,
              vehicleType: string) {
    this.favoriteName = favoriteName;
    this.locations = locations;
    this.passengers = passengers;
    this.babyTransport = babyTransport;
    this.petTransport = petTransport;
    this.vehicleType = vehicleType;
  }

  static getEmptyFavoriteRideCreation() : FavoriteRideCreation {
    return new FavoriteRideCreation('',[],[],false,false,'');
  }
}
