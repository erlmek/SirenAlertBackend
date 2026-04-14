package org.example.sirenalertbackend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.example.sirenalertbackend.sirens.Siren;
import org.example.sirenalertbackend.sirens.SirenRepository;

@Component
public class InitData implements CommandLineRunner {


    private SirenRepository sirenRepository;

    public InitData(SirenRepository sirenRepository) {
        this.sirenRepository = sirenRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only create sirens if none exist
        if (sirenRepository.count() == 0) {
            createInitialSirens();
        }
    }

    private void createInitialSirens() {
        // 15 sirens around Monica Beach and Pacific Palisades area
        // Coordinates are spread across the LA coastal area
        double[][] sirenLocations = {
                {34.0195, -118.4912}, // Santa Monica Beach
                {34.0522, -118.5270}, // Pacific Palisades
                {34.1478, -118.4442}, // Beverly Hills
                {34.0928, -118.3287}, // West Hollywood
                {34.0522, -118.2437}, // Downtown LA
                {34.0900, -118.3617}, // Hollywood
                {34.1369, -118.3531}, // Universal City
                {34.2014, -118.5794}, // Woodland Hills
                {34.2684, -118.6075}, // Calabasas
                {34.0194, -118.4108}, // Venice
                {34.0127, -118.4958}, // Marina del Rey
                {34.1030, -118.4104}, // Century City
                {34.0736, -118.4004}, // Culver City
                {34.1808, -118.3090}, // Los Feliz
                {34.0259, -118.7798}  // Malibu
        };

        String[] sirenNames = {
                "Santa Monica Beach Siren",
                "Pacific Palisades Siren",
                "Beverly Hills Siren",
                "West Hollywood Siren",
                "Downtown LA Siren",
                "Hollywood Siren",
                "Universal City Siren",
                "Woodland Hills Siren",
                "Calabasas Siren",
                "Venice Beach Siren",
                "Marina del Rey Siren",
                "Century City Siren",
                "Culver City Siren",
                "Los Feliz Siren",
                "Malibu Siren"
        };

        for (int i = 0; i < sirenLocations.length; i++) {
            Siren siren = new Siren();
            siren.setName(sirenNames[i]);
            siren.setLatitude(sirenLocations[i][0]);
            siren.setLongitude(sirenLocations[i][1]);
            siren.setStatus("Fred");
            siren.setDisabled(false);
            sirenRepository.save(siren);

            System.out.println("Created siren: " + sirenNames[i] +
                    " at (" + sirenLocations[i][0] + ", " + sirenLocations[i][1] + ")");
        }

        System.out.println("Successfully created 15 sirens in the Monica Beach and Pacific Palisades area!");
    }
}
