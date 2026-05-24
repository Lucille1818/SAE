package fr.imta.smartgrid.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


/**
Classe abstraite représentant un capteur physique.
C'est la classe parente de toute la hiérarchie : Producer, Consumer, WindTurbine, SolarPanel, EVCharger.
Un capteur est associé à une grille, peut avoir plusieurs propriétaires (Person) et plusieurs mesures (Measurement).
 */

@Entity
@Table(name = "sensor")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    // Grille à laquelle appartient ce capteur
    @ManyToOne
    @JoinColumn(name = "grid")
    private Grid grid;
    
    // Personnes propriétaires de ce capteur (relation many-to-many)
    @ManyToMany(mappedBy = "sensors")
    private List<Person> owners = new ArrayList<>();

    // Mesures associées à ce capteur
    @OneToMany(mappedBy = "sensor")
    private List<Measurement> measurements = new ArrayList<>();

    /**
    Retourne les données du capteur au format JSON.
    Inclut l'id, le nom, la description, la grille associée, la liste des propriétaires et la liste des mesures.
    Cette méthode est surchargée dans les sous-classes pour y ajouter leurs champs spécifiques.
     */
    public JsonObject toJSON() {
        JsonObject res = new JsonObject();

        res.put("id", this.getId());
        res.put("name", this.getName());
        res.put("description", this.getDescription());
        res.put("grid", this.getGrid() != null ? this.getGrid().getId() : null);

        List<Integer> ownerIds = new ArrayList<>();
        for (Person p : this.getOwners()) {
            ownerIds.add(p.getId());
        }
        res.put("owners", ownerIds);

        List<Integer> measurementIds = new ArrayList<>();
        for (Measurement m : this.getMeasurements()) {
            measurementIds.add(m.getId());
        }
        res.put("measurements", measurementIds);
        
        return res;
    }

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

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public List<Person> getOwners() {
        return owners;
    }

    public void setOwners(List<Person> owners) {
        this.owners = owners;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
    
}
