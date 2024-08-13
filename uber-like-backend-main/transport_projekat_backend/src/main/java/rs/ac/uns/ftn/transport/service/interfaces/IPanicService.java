package rs.ac.uns.ftn.transport.service.interfaces;

import rs.ac.uns.ftn.transport.model.Panic;

import java.util.List;

public interface IPanicService {
    List<Panic> findAll();
    Panic save(Panic panic, Integer rideId);
}
