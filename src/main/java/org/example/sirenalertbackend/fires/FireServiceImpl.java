package org.example.sirenalertbackend.fires;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.example.sirenalertbackend.sirens.Siren;
import org.example.sirenalertbackend.sirens.SirenServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class FireServiceImpl implements FireService {

    private FireRepository fireRepository;
    private SirenServiceImpl sirenService;

    public FireServiceImpl(FireRepository fireRepository, SirenServiceImpl sirenService) {
        this.fireRepository = fireRepository;
        this.sirenService = sirenService;
    }

    @Override
    public Fire fireExtinguish(int id, boolean status) {
        Fire fire = fireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fire with ID " + id + " not found"));

        fire.setStatus(status);

        if (!status && fire.getActivatedSirens() != null) {
            fire.getActivatedSirens().forEach(siren -> {
                siren.setStatus("Fred");
                sirenService.save(siren);
            });
        }

        return fireRepository.save(fire);
    }

    @Override
    public List<Fire> activeFires() {
            List<Fire> fire = fireRepository.findByStatusTrue();
            return fire;
    }

    public List<Fire> allFires(){
        try {
            List<Fire> fire = (List<Fire>) fireRepository.findAll();
            return fire;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Opretter en tilfældig brand i et område, hvor mindst to sirener er inden for 10 km radius.
     *
     * Metoden bruges til at simulere brande til testformål og sikrer, at branden kun oprettes,
     * hvis der er mindst to sirener i nærheden, så systemets varsling kan testes realistisk.
     *
     * Den forsøger op til 50 gange at finde en passende lokalitet ved at vælge en tilfældig sirene
     * som udgangspunkt og generere en tilfældig position omkring denne.
     *
     * Når en gyldig lokalitet findes, oprettes en brand med tilfældigt navn fra en foruddefineret liste,
     * og alle sirener inden for 10 km aktiveres med status "Fare".
     *
     * @return den oprettede og gemte Fire-entitet med aktiverede sirener
     * @throws IllegalStateException hvis der ikke findes mindst to sirener i systemet,
     *                               eller hvis der ikke kan findes en gyldig lokalitet efter 50 forsøg
     */
    @Override
    public Fire createRandomFire() {
        List<Siren> allSirens = sirenService.getAll();

        if (allSirens.size() < 2) {
            throw new IllegalStateException("Brug for mindste 2 Sirener for at kunne lave en brænd");
        }
        //Nogle tilfældige LA lokalitets navne til brændende.
        String[] fireNames = {
                "Malibu Creek Fire", "Santa Monica Mountains Fire", "Griffith Park Fire",
                "Hollywood Hills Fire", "Pacific Palisades Fire", "Topanga Canyon Fire",
                "Beverly Hills Fire", "Runyon Canyon Fire", "Mulholland Drive Fire",
                "Laurel Canyon Fire", "Coldwater Canyon Fire", "Franklin Hills Fire",
                "Silver Lake Fire", "Echo Park Fire", "Los Feliz Fire",
                "Beachwood Canyon Fire", "Nichols Canyon Fire", "Benedict Canyon Fire",
                "Mandeville Canyon Fire", "Sunset Strip Fire"
        };

        Random random = new Random();

        // prøver op til 50 gange for at finde en god lokalitet
        for (int attempt = 0; attempt < 50; attempt++) {
            // tager tilfældig sirene som start punkt
            Siren randomSiren = allSirens.get(random.nextInt(allSirens.size()));

            // Laver en tilfældig location indenfor radius af ihvertfald 2 sirener
            double randomLat = randomSiren.getLatitude() + (random.nextDouble() - 0.5) * 0.14; // ~8km range
            double randomLon = randomSiren.getLongitude() + (random.nextDouble() - 0.5) * 0.14;

            // tjekker hvor mange sirener er inden for 10 km
            List<Siren> nearbySirens = sirenService.findSirensWithinRadius(randomLat, randomLon, 10.0);

            if (nearbySirens.size() >= 2) {
                // når en lokalitet er fundet laves en ild.
                Fire fire = new Fire();
                String randomFireName = fireNames[random.nextInt(fireNames.length)];
                fire.setName(randomFireName);
                fire.setLatitude(randomLat);
                fire.setLongitude(randomLon);
                fire.setTime(LocalDateTime.now());
                fire.setStatus(true);

                // Aktivere sirener tæt på.
                nearbySirens.forEach(siren -> {
                    siren.setStatus("Fare");
                    sirenService.save(siren);
                });

                fire.setActivatedSirens(nearbySirens);
                return fireRepository.save(fire);
            }
        }

        throw new IllegalStateException("Could not find valid fire location after 50 attempts");
    }

    public List<Fire> findAllByActivatedSirensContaining(Siren siren){
        return fireRepository.findAllByActivatedSirensContaining(siren);
    }

    public void save(Fire fire){
        fireRepository.save(fire);
    }
}
