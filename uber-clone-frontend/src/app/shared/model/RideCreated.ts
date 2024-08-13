import {UserIdEmail} from "./UserIdEmail";
import {RejectionReason} from "./RejectionReason";
import {RideStatus} from "./RideStatus";
import {Route} from "./Route";

export class RideCreated{
  id : number;
  startTime : string;
  endTime : string;
  totalCost : number;
  driver : UserIdEmail;
  rejection : RejectionReason;
  estimatedTimeInMinutes : number;
  status : RideStatus;
  babyTransport: boolean;
  locations: Route[];
  passengers: UserIdEmail[];
  petTransport: boolean;
  scheduledTime: string;
  vehicleType: string;

  constructor(id: number,
              startTime: string,
              endTime: string,
              totalCost: number,
              driver: UserIdEmail,
              rejection: RejectionReason,
              estimatedTimeInMinutes: number,
              status: RideStatus,
              babyTransport: boolean,
              locations: Route[],
              passengers: UserIdEmail[],
              petTransport: boolean,
              scheduleTime: string,
              vehicleType: string) {
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
    this.totalCost = totalCost;
    this.driver = driver;
    this.rejection = rejection;
    this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    this.status = status;
    this.babyTransport = babyTransport;
    this.locations = locations;
    this.passengers = passengers;
    this.petTransport = petTransport;
    this.scheduledTime = scheduleTime;
    this.vehicleType = vehicleType;
  }

  static getEmptyRideCreated() : RideCreated {
    return new RideCreated(-1,'','',
                       0,UserIdEmail.getEmptyUserIdEmail(),RejectionReason.getEmptyRejectionReason(),
             0,RideStatus.ACCEPTED, false,
                [],[],false,'','');
  }
}
