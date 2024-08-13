package rs.ac.uns.ftn.transport.service.interfaces;

import rs.ac.uns.ftn.transport.model.Document;

import java.util.Set;

public interface IDocumentService {
    Document save(Document document);
    Set<Document> findAllByDriver_Id(Integer id);

    Integer deleteAllByDriver_Id(Integer id);
    void deleteById(Integer id);
    Document findOne(Integer id);
}