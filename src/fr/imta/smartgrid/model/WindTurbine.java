package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
Représente une éolienne, type de producteur d'énergie.
Hérite de Producer (qui hérite de Sensor).
Ajoute les champs height (hauteur en mètres) et bladeLength (longueur des pales en mètres).
 */

@Entity
@Table(name = "wind_turbine")
@PrimaryKeyJoinColumn(name = "id")
public class WindTurbine extends Producer {
    private Double height;
    private Double bladeLength;

    // Hauteur de l'éolienne en mètres
    public Double getHeight() {
        return height;
    }
    public void setHeight(Double height) {
        this.height = height;
    }
     // Longueur des pales en mètres
    public Double getBladeLength() {
        return bladeLength;
    }
    public void setBladeLength(Double bladeLength) {
        this.bladeLength = bladeLength;
    }

    /**
    Retourne les données de l'éolienne au format JSON.
    Appelle super.toJSON() pour récupérer les champs hérités de Sensor et Producer, puis ajoute height et bladeLength.
     */
    @Override
    public JsonObject toJSON() {
        JsonObject res = super.toJSON();
        res.put("height", this.getHeight());
        res.put("bladeLength", this.getBladeLength());
        return res;
    }

}
