package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
Représente un panneau solaire, type de producteur d'énergie.
Hérite de Producer (qui hérite de Sensor).
Ajoute le champ efficiency (rendement du panneau, entre 0 et 1).
 */

@Entity
@Table(name = "solar_panel")
@PrimaryKeyJoinColumn(name = "id")
public class SolarPanel extends Producer {
    // Rendement du panneau solaire (ex: 0.20= 20%)
    private float efficiency;

    public float getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
    }

    /**
    Retourne les données du panneau solaire au format JSON.
    Appelle super.toJSON() pour récupérer les champs hérités de Sensor et Producer, puis ajoute le champ efficiency.
     */
   @Override
    public JsonObject toJSON() {
        JsonObject res = super.toJSON();
        res.put("efficiency", this.getEfficiency());
        return res;
    }


}
