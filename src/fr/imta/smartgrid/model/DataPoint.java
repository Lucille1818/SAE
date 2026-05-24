package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


/**
Représente un point de données enregistré à un instant donné.
Un DataPoint est toujours rattaché à une Measurement.
Il contient un timestamp (en secondes Unix) et une valeur numérique.
 */

@Entity
@Table(name = "datapoint")
public class DataPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Horodatage en secondes (format Unix timestamp)
    private long timestamp;

     // Valeur mesurée (ex: vitesse en km/h, puissance en W...)
    private double value;

    // Mesure à laquelle ce point de données est rattaché
    @ManyToOne
    @JoinColumn(name = "measurement")
    private Measurement measurement;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }


    
}
