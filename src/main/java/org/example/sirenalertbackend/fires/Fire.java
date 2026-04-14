package org.example.sirenalertbackend.fires;

import jakarta.persistence.*;
import org.example.sirenalertbackend.sirens.Siren;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Fire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private double latitude;
    private double longitude;

    private boolean status;

    private LocalDateTime time;

    @ManyToMany
    private List<Siren> activatedSirens;

    public Fire() {

    }

    public Fire(String name, double latitude, double longitude, boolean status, LocalDateTime time) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.time = time;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public List<Siren> getActivatedSirens() {
        return activatedSirens;
    }

    public void setActivatedSirens(List<Siren> activatedSirens) {
        this.activatedSirens = activatedSirens;
    }
}
