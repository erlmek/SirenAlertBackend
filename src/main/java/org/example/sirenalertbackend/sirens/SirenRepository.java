package org.example.sirenalertbackend.sirens;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SirenRepository extends JpaRepository<Siren, Integer> {
}
