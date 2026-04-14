package org.example.sirenalertbackend.sirens;

import java.util.List;

public interface SirenService {

    public List<Siren> getAll();

    public Siren create(Siren siren);

    public Siren update(Integer id,Siren siren);

    public void delete (Integer id);
}
