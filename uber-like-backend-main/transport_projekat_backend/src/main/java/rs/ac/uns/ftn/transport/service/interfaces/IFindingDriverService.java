package rs.ac.uns.ftn.transport.service.interfaces;

import rs.ac.uns.ftn.transport.model.Driver;
import rs.ac.uns.ftn.transport.model.Ride;

public interface IFindingDriverService {
    Driver findSuitableDriver(Ride order, boolean isReservation);

}
