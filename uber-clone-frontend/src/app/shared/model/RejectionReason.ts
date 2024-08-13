export class RejectionReason {
  reason : string
  constructor(reason: string) {
    this.reason = reason;
  }

  static getEmptyRejectionReason() : RejectionReason {
    return new RejectionReason('');
  }
}
