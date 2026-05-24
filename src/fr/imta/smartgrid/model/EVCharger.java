package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
Représente une borne de recharge pour véhicule électrique.
C'est le seul type de consommateur existant dans le projet.
Hérite de Consumer (qui hérite de Sensor).
Ajoute les champs voltage, maxAmp et type de connecteur.
 */

@Entity
@Table(name = "ev_charger")
@PrimaryKeyJoinColumn(name = "id")
public class EVCharger extends Consumer {
    // Tension en volts
    private int voltage;
    // Intensité maximale en ampères
    private int maxAmp;

    // Type de connecteur 
    @Column(name = "connector_type")
    private String type;

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getMaxAmp() {
        return maxAmp;
    }

    public void setMaxAmp(int maxAmp) {
        this.maxAmp = maxAmp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
