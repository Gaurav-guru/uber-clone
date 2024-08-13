import {Location} from "./Location";

export interface RouteEstimatesRequestBody {
  locations : Array<Location>,
  vehicleType : string,
  babyTransport : boolean,
  petTransport : boolean
}
