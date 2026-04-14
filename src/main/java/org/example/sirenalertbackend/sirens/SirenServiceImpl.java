package org.example.sirenalertbackend.sirens;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.example.sirenalertbackend.fires.Fire;
import org.example.sirenalertbackend.fires.FireRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SirenServiceImpl implements SirenService {

    private SirenRepository sirenRepository;
    private FireRepository fireRepository;

    public SirenServiceImpl(SirenRepository sirenRepository, FireRepository fireRepository) {
        this.sirenRepository = sirenRepository;
        this.fireRepository = fireRepository;
    }

    @Override
    public List<Siren> getAll() {
        return sirenRepository.findAll();
    }

    @Override
    public Siren create(Siren siren) {
        return sirenRepository.save(siren);
    }


    @Override
    public Siren update(Integer id, Siren siren) {
        // Finder eksisterende sirener
        Optional<Siren> existingSirenOpt = sirenRepository.findById(id);

        if (existingSirenOpt.isPresent()) {
            Siren existingSiren = existingSirenOpt.get();

            // opdatere felterne
            existingSiren.setName(siren.getName());
            existingSiren.setLatitude(siren.getLatitude());
            existingSiren.setLongitude(siren.getLongitude());
            existingSiren.setStatus(siren.getStatus());
            existingSiren.setDisabled(siren.isDisabled());

            // gemmer til database og sender updateret tilbage.
            Siren updatedSiren = sirenRepository.save(existingSiren);
            System.out.println("Updated siren: " + updatedSiren.getName() + " (ID: " + updatedSiren.getId() + ")");
            return updatedSiren;
        } else {
            throw new RuntimeException("Siren with ID " + id + " not found");
        }
    }

    @Override
    public void delete(Integer id) {
        Siren siren = sirenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Siren with id " + id + " not found"));
        List<Fire> fires = fireRepository.findAllByActivatedSirensContaining(siren);

        for (Fire fire : fires) {
            fire.getActivatedSirens().remove(siren);
            fireRepository.save(fire);
        }

        sirenRepository.delete(siren);
    }

    public void save(Siren siren) {
        sirenRepository.save(siren);
    }

    private static final int EARTH_RADIUS_KM = 6371;

    public double calculateDistanceKM(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }


    /**
     * Metoden beregner afstanden mellem den angivne position (latitude og longitude) og alle sirener i databasen,
     * og returnerer kun de sirener, som ligger inden for den angivne radius i kilometer.
     * Sirener, der er markeret som disabled, udelukkes fra resultatet.
     */

    public List<Siren> findSirensWithinRadius(double fireLat, double fireLon, double radiusKm) {
        return sirenRepository.findAll().stream()
                .filter(siren -> !siren.isDisabled())
                .filter(siren -> calculateDistanceKM(fireLat, fireLon,
                        siren.getLatitude(), siren.getLongitude()) <= radiusKm)
                .collect(Collectors.toList());
    }
}
