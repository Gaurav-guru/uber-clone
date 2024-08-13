export class UserIdEmail {
  id: number;
  email: string;

  constructor(id: number, email: string) {
    this.id = id;
    this.email = email;
  }
  static getEmptyUserIdEmail() : UserIdEmail {
    return new UserIdEmail(-1,'');
  }
}
