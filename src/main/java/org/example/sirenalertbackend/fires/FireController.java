package org.example.sirenalertbackend.fires;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fires")
@CrossOrigin("*")
public class FireController {

    private FireServiceImpl fireService;

    public FireController(FireServiceImpl fireService) {
        this.fireService = fireService;
    }



    @PostMapping("/create")
    public ResponseEntity<Fire> createRandomFire() {
        try {
            Fire createdFire = fireService.createRandomFire();
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFire);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/activeFires")
    public ResponseEntity<List<Fire>> activeFires() {
        try {
            List<Fire> fires = fireService.activeFires();
            if(fires == null || fires.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            return ResponseEntity.ok(fires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/allFires")
    public ResponseEntity<List<Fire>> allFires() {
        try {
            List<Fire> fires = fireService.allFires();
            if(fires == null || fires.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            return ResponseEntity.ok(fires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    /**
     * Opdaterer status på en brand og opdaterer tilknyttede sirener.
     *
     * @param id      ID på branden der skal opdateres
     * @param status  Ny status (true = aktiv, false = slukket)
     * @return        Den opdaterede Fire-entitet
     */
    @PutMapping("/fires/{id}")
    public ResponseEntity<Fire> fireExtinguish(@PathVariable int id,@RequestBody boolean status) {
        try {
            Fire updatedFire = fireService.fireExtinguish(id, status);
            return ResponseEntity.ok(updatedFire);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
