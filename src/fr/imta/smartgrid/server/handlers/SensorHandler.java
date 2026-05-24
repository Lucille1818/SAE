package fr.imta.smartgrid.server.handlers;

import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Consumer;
import fr.imta.smartgrid.model.Producer;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.EntityManager;

/**
 Handler gérant les routes liées aux capteurs (Sensor).
 Le capteur est un appareil physique (éolienne, panneau solaire…)associé à une grille et pouvant avoir plusieurs mesures.
 */

public class SensorHandler {

    private EntityManager db;

    /**
    Constructeur : reçoit l'EntityManager partagé pour accéder à la base de données.
     */

    public SensorHandler(EntityManager db) {
        this.db = db;
    }
    /**
    GET /sensor
    Retourne la liste des IDs de tous les capteurs présents en base.
     */
    public void getSensors(RoutingContext ctx) {
        List<Integer> sensors = db.createNativeQuery("SELECT s.id from sensors as s").getResultList();
        ctx.json(sensors);
    }

    /**
    GET /sensor/:id
    Retourne le détail d'un capteur identifié par son ID.
    Répond 404 si le capteur n'existe pas.
     */
    public void getById(RoutingContext ctx) {
        Sensor s = db.find(Sensor.class, Integer.parseInt(ctx.pathParam("id")));
        if (s == null){
            ctx.fail(404);
        } else {
            ctx.json(s.toJSON());  // ← ajoute .toJSON()
        }
    }
    /**
     POST /sensor/:id
     Met à jour les champs "name" et/ou "description" d'un capteur existant.
     Le corps de la requête doit être un JSON contenant les champs à modifier.
     Répond 404 si le capteur n'existe pas, 400 si le corps est absent.
     */
    public void update(RoutingContext ctx) {
        Sensor s = db.find(Sensor.class, Integer.parseInt(ctx.pathParam("id")));
        if (s == null) {
            ctx.fail(404);
            return;
        }

        JsonObject body = ctx.body().asJsonObject();
        if (body == null) {
            ctx.fail(400);
            return;
        }

        // Mise à jour uniquement des champs présents dans le corps de la requête
        db.getTransaction().begin();
        if (body.containsKey("name"))        s.setName(body.getString("name"));
        if (body.containsKey("description")) s.setDescription(body.getString("description"));
        db.getTransaction().commit();
        
        // Retourne le capteur mis à jour
        ctx.json(s.toJSON());
    }
    
}
