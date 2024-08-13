package rs.ac.uns.ftn.transport.service.interfaces;

import rs.ac.uns.ftn.transport.model.UserActivation;

public interface IAccountActivationService {

    UserActivation save(UserActivation activation);
    UserActivation findOne(Integer id);
}
