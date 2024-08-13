import {UserIdEmail} from "./UserIdEmail";

export interface ReviewReturned {
  id : number,
  rating: number,
  comment: string,
  passenger : UserIdEmail
}
