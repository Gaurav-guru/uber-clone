package rs.ac.uns.ftn.transport.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.transport.dto.PanicDTO;
import rs.ac.uns.ftn.transport.dto.PanicPageDTO;
import rs.ac.uns.ftn.transport.mapper.PanicDTOMapper;
import rs.ac.uns.ftn.transport.model.Panic;
import rs.ac.uns.ftn.transport.service.interfaces.IPanicService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping(value="api/panic")
public class PanicController {
    private IPanicService panicService;

    public PanicController(IPanicService panicService){this.panicService = panicService;}

    @GetMapping
    public ResponseEntity<PanicPageDTO> getPanics(){
        List<Panic> panics = panicService.findAll();

        Set<PanicDTO> panicDTOs = panics.stream()
                .map(PanicDTOMapper::fromPanictoDTO)
                .collect(Collectors.toSet());


        return new ResponseEntity<>(new PanicPageDTO((long) panics.size(), panicDTOs), HttpStatus.OK);
    }
}
