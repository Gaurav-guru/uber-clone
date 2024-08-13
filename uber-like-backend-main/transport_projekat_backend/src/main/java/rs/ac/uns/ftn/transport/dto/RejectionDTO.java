package rs.ac.uns.ftn.transport.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RejectionDTO {

    private String reason;
    private LocalDateTime timeOfRejection;
}
