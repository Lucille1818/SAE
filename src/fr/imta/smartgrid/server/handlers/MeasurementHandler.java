package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Person;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

/**
Handler gérant les routes liées aux mesures (Measurement).
Une mesure est associée à un capteur et contient une liste de points de données (DataPoint).
 */

public class MeasurementHandler {
    private EntityManager db;

    /**
    Constructeur : reçoit l'EntityManager partagé pour accéder à la base de données.
     */

    public MeasurementHandler(EntityManager db) {
        this.db = db;
    }

    /**
    GET /measurement
    Retourne la liste des IDs de toutes les mesures présentes en base via la requête SQL : SELECT m.id FROM measurement as m
     */

    public void getMeasurements(RoutingContext ctx) {
        List<Integer> measurements = db.createNativeQuery("SELECT m.id from measurement as m").getResultList();
        ctx.json(measurements);
    }

    /**
    GET /measurement/:id
    Retourne le détail d'une mesure identifiée par son ID.
    Utilise db.find() plutôt qu'une requête SQL manuelle.
    Répond 404 si la mesure n'existe pas.
    Les données sont retournées via toJSON() défini dans Measurement.java.
     */

    public void getById(RoutingContext ctx) {
        Measurement m = db.find(Measurement.class, Integer.parseInt(ctx.pathParam("id")));
        if (m == null){
            ctx.fail(404);
        } else {
            ctx.json(m.toJSON());
        }
    }

    /**
    GET /measurement/:id/values
    Retourne la liste des points de données (DataPoint) associés à une mesure.
    Pour chaque DataPoint, on construit un objet JSON avec son id, timestamp et value.
    Répond 404 si la mesure n'existe pas.
     */

    public void getValues(RoutingContext ctx) {
        Measurement m = db.find(Measurement.class, Integer.parseInt(ctx.pathParam("id")));
        if (m == null) {
            ctx.fail(404);
            return;
        }
        io.vertx.core.json.JsonArray result = new io.vertx.core.json.JsonArray();
        for (fr.imta.smartgrid.model.DataPoint dp : m.getDatapoints()) {
            io.vertx.core.json.JsonObject point = new io.vertx.core.json.JsonObject();
            point.put("id", dp.getId());
            point.put("timestamp", dp.getTimestamp());
            point.put("value", dp.getValue());
            result.add(point);
        }
        ctx.json(result);
    }
    
}
