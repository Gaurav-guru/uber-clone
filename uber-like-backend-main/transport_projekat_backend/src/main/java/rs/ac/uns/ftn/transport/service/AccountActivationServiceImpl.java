package rs.ac.uns.ftn.transport.service;


import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.UserActivation;
import rs.ac.uns.ftn.transport.repository.UserActivationRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IAccountActivationService;

@Service
public class AccountActivationServiceImpl implements IAccountActivationService {

    private final UserActivationRepository userActivationRepository;

    public AccountActivationServiceImpl(UserActivationRepository userActivationRepository) {
        this.userActivationRepository = userActivationRepository;
    }

    @Override
    public UserActivation save(UserActivation activation) {
        return userActivationRepository.save(activation);
    }

    @Override
    public UserActivation findOne(Integer id) {
        return userActivationRepository.findById(id).orElseGet(null);
    }
}
