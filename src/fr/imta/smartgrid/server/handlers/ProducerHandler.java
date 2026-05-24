package fr.imta.smartgrid.server.handlers;

import java.util.List;
import io.vertx.ext.web.RoutingContext;
import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Producer;
import jakarta.persistence.EntityManager;
import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.WindTurbine;
import fr.imta.smartgrid.model.SolarPanel;
import io.vertx.core.json.JsonObject;
import java.util.List;

/**
 Handler gérant les routes liées aux producteurs d'énergie (Producer).
 Un producteur est un capteur qui génère de l'énergie : éolienne ou panneau solaire.
 Le handler gère aussi l'ingress : la réception de nouvelles mesures depuis les capteurs.
 */

public class ProducerHandler {
    private EntityManager db;

    /**
    Constructeur : reçoit l'EntityManager partagé pour accéder à la base de données.
     */

    public ProducerHandler(EntityManager db) {
        this.db = db;
    }

     /**
     GET /producer
     Retourne la liste des IDs de tous les producteurs.
     */

    public void getProducers(RoutingContext ctx) {
        List <Integer> producerIds = db.createQuery("SELECT pr.id from Person as pr").getResultList();
        ctx.json(producerIds);
    }

    /**
    GET /producer/:id
    Retourne le détail d'un producteur identifié par son ID.
    Répond 404 si le producteur n'existe pas.
     */

    public void getById(RoutingContext ctx) {
        Producer pr = db.find(Producer.class, Integer.parseInt(ctx.pathParam("id")));
        if (pr == null){
            ctx.fail(404);
        } else {
            ctx.json(pr.toJSON());
        }
    }
        
    /**
    POST /ingress/windturbine
    Point d'entrée pour recevoir une mesure provenant d'une éolienne.
    Délègue à la méthode générique ingressMeasurement avec le type WindTurbine.
     */
    public void ingressWindTurbine(RoutingContext ctx) {
        ingressMeasurement(ctx, WindTurbine.class);
    }

    /**
    POST /ingress/solarpanel
    Point d'entrée pour recevoir une mesure provenant d'un panneau solaire.
    Délègue à la méthode générique ingressMeasurement avec le type SolarPanel.
     */
    public void ingressSolarPanel(RoutingContext ctx) {
        ingressMeasurement(ctx, SolarPanel.class);
    }

    /**
    Méthode générique pour enregistrer une mesure (DataPoint) pour n'importe quel type de producteur.
    
    Corps JSON attendu :
    {
    "sensorId"  : int    — ID du capteur concerné,
    "value"     : double — valeur mesurée,
    "timestamp" : long   — horodatage Unix (optionnel, heure actuelle par défaut)
    }
    
    Répond 400 si le corps est absent, 404 si le capteur ou ses mesures sont introuvables, 201 avec le DataPoint créé en cas de succès.
     */

    private <T extends Producer> void ingressMeasurement(RoutingContext ctx, Class<T> type) {
        JsonObject body = ctx.body().asJsonObject();
        if (body == null) {
            ctx.fail(400);
            return;
        }
        int sensorId  = body.getInteger("sensorId");
        double value  = body.getDouble("value");
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

        
