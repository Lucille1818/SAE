package fr.imta.smartgrid.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "measurement")
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String unit;

    private String name;

    @ManyToOne
    @JoinColumn(name = "sensor")
    private Sensor sensor;

    @OneToMany(mappedBy = "measurement", fetch = FetchType.EAGER)
    private List<DataPoint> datapoints = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public List<DataPoint> getDatapoints() {
        return datapoints;
    }

    public void setDatapoints(List<DataPoint> datapoints) {
        this.datapoints = datapoints;
    }

    public JsonObject toJSON() {
        JsonObject result = new JsonObject();
        result.put("id", this.getId());
        result.put("unit", this.getUnit());
        result.put("name", this.getName());

        List<Integer> datapointIds = new ArrayList<>();
        for (DataPoint dp : this.getDatapoints()) {
            datapointIds.add(dp.getId());
        }
        result.put("datapoints", datapointIds);

        return result; 
    }

    
}
