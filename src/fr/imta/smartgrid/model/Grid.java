package fr.imta.smartgrid.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
Représente une grille électrique intelligente.
Une grille regroupe des personnes et des capteurs (producteurs et consommateurs).
 */

@Entity
@Table(name = "grid")
public class Grid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    // Personnes rattachées à cette grille
    @OneToMany(mappedBy = "grid")
    private List<Person> persons = new ArrayList<>();

     // Capteurs rattachés à cette grille
    @OneToMany(mappedBy = "grid")
    private List<Sensor> sensors = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }


    /**
    Retourne les données de la grille au format JSON.
    Inclut l'id, le nom, la description, la liste des ids des personnes et la liste des ids des capteurs associés.
     */
    public JsonObject toJSON() {
        JsonObject result = new JsonObject();

        result.put("id", this.getId());
        result.put("name", this.getName());
        result.put("description", this.getDescription());

        List <Integer> users = new ArrayList<>();
        for (Person p : this.getPersons()) {
            users.add(p.getId());
        }

        result.put("users", users);

        List <Integer> ownedSensors = new ArrayList<>();
        for (Sensor s : this.getSensors()) {
            ownedSensors.add(s.getId());
        }
        
        result.put("sensors", ownedSensors);

        return result;
    }


    
}
