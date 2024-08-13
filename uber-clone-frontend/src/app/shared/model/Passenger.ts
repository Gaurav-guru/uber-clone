export class Passenger{
  public name: string;
  public surname: string;
  public profilePicture: string;
  public telephoneNumber: string;
  public email: string;
  public address: string;
  public password: string;

  constructor(name:string,surname:string,pro:string,tel:string,email:string,adress:string,password:string) {
    this.name = name
    this.surname = surname
    this.profilePicture = pro
    this.telephoneNumber = tel
    this.email = email
    this.address = adress
    this.password = password
  }
}
