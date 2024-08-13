import {Injectable} from "@angular/core";
import {User} from "../app/shared/model/User";
import {BehaviorSubject, Observable} from "rxjs";
import {UserRetrieved} from "../app/shared/model/UserRetrieved";

@Injectable()
export class PassengerMockService {
  registerPassenger(newData : User) : Observable<UserRetrieved> {
    const user : UserRetrieved = {
      id: 1,
      name: newData["name"],
      surname: newData["surname"],
      profilePicture: newData["profilePicture"] === null ? "" : newData["profilePicture"],
      telephoneNumber: newData["telephoneNumber"],
      email: newData["email"],
      address: newData["address"]
    }
    return new BehaviorSubject(user).asObservable();
  }
}
