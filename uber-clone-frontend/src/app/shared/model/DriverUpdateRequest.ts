export interface DriverUpdateRequest {
  driverId : number,
  name: string;
  surname: string;
  profilePicture: string | null;
  telephoneNumber: string;
  address: string;
  email: string;
}
