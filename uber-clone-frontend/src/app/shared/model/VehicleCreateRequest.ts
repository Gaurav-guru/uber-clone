export interface VehicleCreateRequest {
  driverId : number,
  vehicleId : number,
  vehicleType : string,
  model : string,
  licenseNumber : string,
  passengerSeats : number,
  babyTransport : boolean,
  petTransport : boolean
}
