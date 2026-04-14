package org.example.sirenalertbackend.sirens;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/sirens")
public class SirenController {

    private final SirenServiceImpl sirenService;

    public SirenController(SirenServiceImpl sirenService) {
        this.sirenService = sirenService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<Siren>> getAll() {
        try {
            List<Siren> sirens = sirenService.getAll();
            return ResponseEntity.ok(sirens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Siren> create(@RequestBody Siren siren) {
        try {
            Siren createdSiren = sirenService.create(siren);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSiren);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/siren/{id}")
    public ResponseEntity<Siren> update(@PathVariable Integer id, @RequestBody Siren siren) {
        try {
            Siren updatedSiren = sirenService.update(id, siren);
            return ResponseEntity.ok(updatedSiren);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/siren/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            sirenService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
