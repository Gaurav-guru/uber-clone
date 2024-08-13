import {Route} from "./Route";
import {UserIdEmail} from "./UserIdEmail";

export class FavoriteRideCreated{
  id: number;
  favoriteName : string;
  locations : Route[];
  passengers : UserIdEmail[];
  babyTransport : boolean;
  petTransport : boolean;
  vehicleType : string;

  constructor(id: number,
              favoriteName: string,
              locations: Route[],
              passengers: UserIdEmail[],
              babyTransport: boolean,
              petTransport: boolean,
              vehicleType: string) {
    this.id = id;
    this.favoriteName = favoriteName;
    this.locations = locations;
    this.passengers = passengers;
    this.babyTransport = babyTransport;
    this.petTransport = petTransport;
    this.vehicleType = vehicleType;
  }
  static getEmptyFavoriteRideCreated() : FavoriteRideCreated{
    return new FavoriteRideCreated(-1,'',[],[],false,false,'');
  }
}
