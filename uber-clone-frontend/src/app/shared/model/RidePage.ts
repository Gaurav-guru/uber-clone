import {RideCreated} from "./RideCreated";

export class RidePage {
  public totalCount: number;
  public results: RideCreated[]
  constructor(totalCount: number, results: RideCreated[]) {
    this.totalCount = totalCount;
    this.results = results;
  }
}
