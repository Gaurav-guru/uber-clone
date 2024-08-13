package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.transport.model.Panic;

public interface PanicRepository extends JpaRepository<Panic, Integer> {
}
