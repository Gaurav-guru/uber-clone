package rs.ac.uns.ftn.transport.service.interfaces;

import org.springframework.http.ResponseEntity;
import rs.ac.uns.ftn.transport.model.ResponseMessage;

public interface IImageService {
    ResponseEntity<ResponseMessage> decodeAndValidateImage(String encodedImage);
}
