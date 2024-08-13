export class RouteEstimates {
  estimatedTimeInMinutes : number;
  estimatedCost : number;

  constructor(estimatedTimeInMinutes : number, estimatedCost : number) {
    this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    this.estimatedCost = estimatedCost;
  }
  static getEmptyRouteEstimates() : RouteEstimates {
    return new RouteEstimates(0.0,0.0);
}
}
