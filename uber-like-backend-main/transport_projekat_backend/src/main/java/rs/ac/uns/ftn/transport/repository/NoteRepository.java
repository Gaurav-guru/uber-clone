package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.Note;
import rs.ac.uns.ftn.transport.model.User;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    Page<Note> findByUser(@Param(value = "user")User user, Pageable page);
}
