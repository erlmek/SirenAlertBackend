package org.example.sirenalertbackend.fires;

import org.example.sirenalertbackend.sirens.Siren;

import java.util.List;

public interface FireService {

    public Fire fireExtinguish(int id, boolean status);

    public List<Fire> activeFires();

    public Fire createRandomFire();

    public List<Fire> findAllByActivatedSirensContaining(Siren siren);

    public void save(Fire fire);
}
