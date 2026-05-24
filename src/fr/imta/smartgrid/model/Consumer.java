package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
Classe représentant un consommateur d'énergie.
Hérite de Sensor et est elle-même héritée par EVCharger.
Ajoute le champ maxPower qui indique la puissance maximale consommable.
Surcharge toJSON() pour y ajouter ce champ en plus des champs hérités de Sensor.
 */

@Entity
@Table(name = "consumer")
@PrimaryKeyJoinColumn(name = "id")
public abstract class Consumer extends Sensor {
    @Column(name = "max_power")
    private Double maxPower;

    public Double getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(Double maxPower) {
        this.maxPower = maxPower;
    }
    /**
    Retourne les données du consommateur au format JSON.
    Appelle d'abord super.toJSON() pour récupérer les champs de Sensor, puis ajoute le champ maxPower spécifique aux consommateurs.
     */

    @Override
    public JsonObject toJSON() {
        JsonObject res = super.toJSON();
        res.put("maxPower", this.getMaxPower());
        return res;
    }
}

