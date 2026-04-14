package org.example.sirenalertbackend.fires;

import org.springframework.data.repository.CrudRepository;
import org.example.sirenalertbackend.sirens.Siren;

import java.util.List;

public interface FireRepository extends CrudRepository<Fire, Integer> {
    List<Fire> findAllByActivatedSirensContaining(Siren siren);

    List<Fire> findByStatusTrue();
}
