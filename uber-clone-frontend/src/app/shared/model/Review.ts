import {UserIdEmail} from "./UserIdEmail";

export interface Review {
  rating : number,
  comment: string,
  reviewer: UserIdEmail
}
