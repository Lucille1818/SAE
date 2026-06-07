package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Producer;
import fr.imta.smartgrid.model.SolarPanel;
import fr.imta.smartgrid.model.WindTurbine;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class IngressHandler {
    public EntityManager db;


    public IngressHandler(EntityManager db){
        this.db=db;
    }

    
    /**
    POST /ingress/windturbine
    Point d'entrée pour recevoir une mesure provenant d'une éolienne.
    Délègue à la méthode générique ingressMeasurement avec le type WindTurbine.
     */

    //public void ingressWindTurbine(RoutingContext ctx) {
    //    ingressMeasurement(ctx, WindTurbine.class);
    //}

    /**
    POST /ingress/solarpanel
    Point d'entrée pour recevoir une mesure provenant d'un panneau solaire.
    Délègue à la méthode générique ingressMeasurement avec le type SolarPanel.
     */
    public void ingressSolarPanel(RoutingContext ctx) {
        String body = ctx.body().asString();

        if (body == null) {
            ctx.fail(400);
            return;
        }
        String[] parts = body.split(":");
        int sensorId = Integer.parseInt(parts[0]);
        double value = Double.parseDouble(parts[1]);
        long timestamp = Long.parseLong(parts[3]);

        SolarPanel sensor = db.find(SolarPanel.class, sensorId);
        if (sensor == null) { 
            ctx.fail(404);
            return;
        }

        List<Measurement> measurements = sensor.getMeasurements();
        if (measurements.isEmpty()) {
            ctx.fail(404);
            return;
        }
        Measurement measurement = measurements.get(0);

        DataPoint dp = new DataPoint();
        dp.setTimestamp(timestamp);
        dp.setValue(value);
        dp.setMeasurement(measurement);
        db.getTransaction().begin();
        db.persist(dp);
        db.getTransaction().commit();

        // Réponse 201 Created avec les infos du DataPoint créé
        ctx.response().setStatusCode(201);
        JsonObject res = new JsonObject();
        res.put("id", dp.getId());
        res.put("timestamp", dp.getTimestamp());
        res.put("value", dp.getValue());
        ctx.json(res);

    }

    /**
    Répond 400 si le corps est absent, 404 si le capteur ou ses mesures sont introuvables, 201 avec le DataPoint créé en cas de succès.
     */
    private <T extends Producer> void ingressMeasurement(RoutingContext ctx, Class<T> type) {
        System.out.println("Body brut reçu : " + ctx.body().asString());
        System.out.println("Content-Type : " + ctx.request().getHeader("Content-Type"));
        JsonObject body = ctx.body().asJsonObject();
        if (body == null) {
            ctx.fail(400);
            return;
        }
        int sensorId  = body.getInteger("sensorId");
        double value  = body.getDouble("value");
        if (sensorId == 0 || value == 0) {
            ctx.response().setStatusCode(400).end("Champs 'sensorId' et 'value' obligatoires");
            return;
        }
        // Si le timestamp n'est pas fourni, on utilise l'heure actuelle en secondes
        long timestamp = body.getLong("timestamp",  System.currentTimeMillis() / 1000);

        // Recherche du capteur en base selon son type (WindTurbine ou SolarPanel)
        T sensor = db.find(type, sensorId);
        if (sensor == null) {
            ctx.fail(404);
            return;
        }

        // On prend la première Measurement du capteur (ou tu peux filtrer par nom)
        // On récupère la première mesure associée au capteur
        List<Measurement> measurements = sensor.getMeasurements();
        if (measurements.isEmpty()) {
            ctx.fail(404);
            return;
        }
        Measurement measurement = measurements.get(0);

        // Création et persistance du nouveau point de données
        DataPoint dp = new DataPoint();
        dp.setTimestamp(timestamp);
        dp.setValue(value);
        dp.setMeasurement(measurement);
        db.getTransaction().begin();
        db.persist(dp);
        db.getTransaction().commit();

        // Réponse 201 Created avec les infos du DataPoint créé
        ctx.response().setStatusCode(201);
        JsonObject res = new JsonObject();
        res.put("id", dp.getId());
        res.put("timestamp", dp.getTimestamp());
        res.put("value", dp.getValue());
        ctx.json(res);
    }

}
