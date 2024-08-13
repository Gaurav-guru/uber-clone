import {Location} from "./Location";
import {UserIdEmail} from "./UserIdEmail";
import {Route} from "./Route";

export interface RideReservation {
  locations: Route[],
  passengers: UserIdEmail[],
  vehicleType: string,
  babyTransport: boolean,
  petTransport: boolean,
  scheduledTime: Date
}
