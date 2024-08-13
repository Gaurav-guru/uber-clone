import {TestBed} from "@angular/core/testing";
import {PassengerService} from "./passenger.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

describe('PassengerService', () => {
  let service: PassengerService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(PassengerService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return registered passenger', () => {
    const user = {
      name: 'Test',
      surname: 'Test',
      profilePicture: null,
      telephoneNumber: '+38162345679',
      address: 'Test',
      email: 'mail234@mail.com',
      password: 'Test1test'
    }

    service.registerPassenger(user).subscribe(result => {
      expect(result).toBeTruthy();
      if (result) {
        expect(result.name).toEqual('Test');
        expect(result.surname).toEqual('Test');
        expect(result.profilePicture).toBeNull();
        expect(result.telephoneNumber).toEqual('+38162345679');
        expect(result.address).toEqual('Test');
        expect(result.email).toEqual('mail234@mail.com');
      }
    })

    const req = httpController.expectOne({
      method: 'POST',
      url: 'http://localhost:8080/api/passenger'
    });
    req.flush(user);
  })
});
