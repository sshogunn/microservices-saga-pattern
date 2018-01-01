package kz.jug.saga.sail.boundary;

import kz.jug.saga.sail.control.SailVikingsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/sails", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class SailEndpoint {
    private final SailVikingsService sailVikingsService;

    @PostMapping
    public ResponseEntity<String> requestSailing(@RequestBody SailRequest sailRequest) {
        this.sailVikingsService.startSail(sailRequest.getVikingsNumber(), sailRequest.getSailType(), sailRequest.getEquipment());
        return new ResponseEntity<>("accepted", HttpStatus.ACCEPTED);
    }
}
