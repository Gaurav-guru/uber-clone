package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.Location;
import rs.ac.uns.ftn.transport.model.Message;
import rs.ac.uns.ftn.transport.model.User;

import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    public Set<Message> findBySender(@Param("sender") User user);
}
