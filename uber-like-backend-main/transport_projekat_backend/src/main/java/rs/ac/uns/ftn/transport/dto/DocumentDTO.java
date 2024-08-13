package rs.ac.uns.ftn.transport.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Integer id;

    @Length(max = 100)
    @Pattern(regexp = "Vozacka dozvola|Vozačka dozvola|Saobracajna dozvola|Saobraćajna dozvola",
            flags = Pattern.Flag.CASE_INSENSITIVE, message = "{format}")
    @NotBlank
    private String name;

    @NotBlank
    private String documentImage;

    private int driverId;
}
