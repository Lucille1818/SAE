package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
Classe représentant un producteur d'énergie.
Hérite de Sensor et est elle-même héritée par WindTurbine et SolarPanel.
Ajoute le champ powerSource qui indique la source d'énergie du producteur.
 */

@Entity
@Table(name = "producer")
@PrimaryKeyJoinColumn(name = "id")
public abstract class Producer extends Sensor {
    @Column(name = "power_source")
    private String powerSource;

    public String getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(String powerSource) {
        this.powerSource = powerSource;
    }


}
