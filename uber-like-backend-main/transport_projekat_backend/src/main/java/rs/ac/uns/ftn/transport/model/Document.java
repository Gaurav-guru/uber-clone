package rs.ac.uns.ftn.transport.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import rs.ac.uns.ftn.transport.model.enumerations.DocumentType;

import java.util.Objects;

@Entity
@Table(name = "documents")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private DocumentType name;

    @Column(name = "documentImage")
    @Lob
    private String documentImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "driverId")
    @ToString.Exclude
    private Driver driver;

    public Document(DocumentType name, String documentImage, Driver driver) {
        this.name = name;
        this.documentImage = documentImage;
        this.driver = driver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Document document = (Document) o;
        return id != null && Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
