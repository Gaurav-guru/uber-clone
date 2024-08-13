package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.uns.ftn.transport.model.User;

public interface UserRepository extends JpaRepository<User,Integer> {
    @Query("select u from User u where u.email = ?1 and u.password = ?2")
    User findByLogin(String email, String password);

    User findByEmail(String emial);
}
