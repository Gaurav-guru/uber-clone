package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.Document;

import java.util.Set;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    Set<Document> findAllByDriver_Id(@Param("id") Integer id);
    Integer deleteAllByDriver_Id(@Param("id") Integer id);
}
