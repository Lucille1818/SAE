package fr.imta.smartgrid.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
Représente une personne utilisatrice de la grille.
Une personne est associée à une grille et peut posséder plusieurs capteurs.
 */

@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;

    // Grille à laquelle cette personne appartient
    @ManyToOne
    @JoinColumn(name = "grid")
    private Grid grid;

    // Capteurs possédés par cette personne (relation many-to-many via table person_sensor)
    @ManyToMany
    @JoinTable(
            name = "person_sensor", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "sensor_id"))
    private List<Sensor> sensors = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    /**
    Retourne les données de la personne au format JSON.
    Inclut l'id, le prénom, le nom, la grille associée et la liste des ids des capteurs possédés.
     */

    public JsonObject toJSON() {
        JsonObject result = new JsonObject();

        result.put("id", this.getId());
        result.put("firstName", this.getFirstName());
        result.put("lastName", this.getLastName());
        result.put("grid", this.getGrid() != null ? this.getGrid().getId() : null);

        List <Integer> ownedSensors = new ArrayList<>();
        for (Sensor s : this.getSensors()) {
            ownedSensors.add(s.getId());
        }
        
        result.put("sensors", ownedSensors);

        return result;
    } 
    
}

