package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.transport.model.User;
import rs.ac.uns.ftn.transport.model.UserActivation;

public interface UserActivationRepository extends JpaRepository<UserActivation,Integer> {

    UserActivation findByUser(User user);
}
