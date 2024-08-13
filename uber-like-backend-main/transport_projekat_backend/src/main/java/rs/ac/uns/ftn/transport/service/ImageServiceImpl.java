package rs.ac.uns.ftn.transport.service;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.ResponseMessage;
import rs.ac.uns.ftn.transport.service.interfaces.IImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Locale;

@Service
public class ImageServiceImpl implements IImageService {
    private final MessageSource messageSource;

    public ImageServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ResponseEntity<ResponseMessage> decodeAndValidateImage(String encodedImage) {
        byte[] image;
        try {
            image = Base64.getDecoder().decode(encodedImage);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("imageFormat", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        if (image == null || image.length == 0) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("imageNull", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        try {
            BufferedImage imageTest = ImageIO.read(new ByteArrayInputStream(image));
            if (imageTest == null) {
                return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("imageFormat", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("imageFormat", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        int MAX_FILE_SIZE = 5 * 1024 * 1024;
        if (image.length > MAX_FILE_SIZE) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("imageSize", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }
        return null;
    }

}
