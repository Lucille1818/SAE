package fr.imta.smartgrid.server;

import java.util.Map;

import org.eclipse.persistence.logging.SessionLog;

import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Producer;
import fr.imta.smartgrid.server.handlers.*;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import static org.eclipse.persistence.config.PersistenceUnitProperties.*;

/**
 Classe principale du serveur HTTP.
 Initialise la connexion à la base de données et enregistre toutes les routes de l'API REST.
 */

public class VertxServer {
    private Vertx vertx;
    private EntityManager db; // database object

    /**
    Constructeur : initialise Vert.x et la connexion JPA à la base PostgreSQL.
     */
    
    public VertxServer() {
        this.vertx = Vertx.vertx();

        // setup database connexion
        // Configuration du niveau de log JPA (WARNING par défaut, FINE pour voir les requêtes SQL)
        Map<String, String> properties = Map.of(
            LOGGING_LEVEL, SessionLog.WARNING_LABEL // change to FINE_LABEL to get details on SQL query to database
        );

        // Création de l'EntityManagerFactory à partir du fichier persistence.xml
        var emf = Persistence.createEntityManagerFactory("smart-grid", properties);
        db = emf.createEntityManager();
    }
    /**
    Démarre le serveur HTTP sur le port 8080.
    Déclare toutes les routes et associe chaque route à son handler.
     */
    public void start() {
        Router router = Router.router(vertx);

        // add handlers for payload parsing and to allow swagger to send requests
        // => permet de lire le corps des requêtes POST/PUT
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create().addOrigin("*").allowedMethod(HttpMethod.DELETE).allowedMethod(HttpMethod.PUT));

        // create handlers and registers routes
        GridHandler gh = new GridHandler(db);
        router.get("/grids").handler(gh::getGrids);
        // => liste toutes les grilles
        router.get("/grid/:id").handler(gh::getById);
        // => détail d'une grille par ID
        // add methods to GridHandler to handle other grid related routes

        PersonHandler ph = new PersonHandler(db);
        router.get("/persons").handler(ph::getPersons);
        // => liste toutes les personnes
        router.get("/person/:id").handler(ph::getById);
        // => détail d'une personne par ID
        //router.post("/person/:id").handler(ph::create);
        //router.put("/person/:id").handler(ph::update)
        //router.delete("/person/:id").handler(ph::delete)
        // => à implementer dans personhandler

        // same as GridHandler

        // do the same for other routes
        
        MeasurementHandler mh = new MeasurementHandler(db);
        router.get("/measurement").handler(mh::getMeasurements);
        // => liste tous les IDs
        router.get("/measurement/:id").handler(mh::getById);
        // => détail d'une mesure
        router.get("/measurement/:id/values").handler(mh::getValues);
        // => datapoints d'une mesure

        ProducerHandler prh = new ProducerHandler(db);
        router.get("/producer").handler(prh::getProducers);
        // => liste les producteurs
        router.get("/producer/:id").handler(prh::getById);
        // => détail d'un producteur
        router.post("/ingress/windturbine").handler(prh::ingressWindTurbine);
        // => reçoit une mesure d'éolienne
        router.post("/ingress/solarpanel").handler(prh::ingressSolarPanel);
        // => reçoit une mesure de panneau solaire

        ConsumerHandler ch = new ConsumerHandler(db);
        router.get("/consumer").handler(ch::getConsumers);
        // => liste les consommateurs
        router.get("/consumer/:id").handler(ch::getById);
        // => détail d'un consommateur

        SensorHandler sh = new SensorHandler(db);
        router.get("/sensor").handler(sh::getSensors);
        // => liste les IDs de tous les capteurs
        router.get("/sensor/:id").handler(sh::getById);
        // => détail d'un capteur
        router.post("/sensor/:id").handler(sh::update);
        // => mise à jour nom/description d'un capteur 

        
        // start the server => Démarrage du serveur sur le port 8080
        vertx.createHttpServer().requestHandler(router).listen(8080)
            .onSuccess(e -> 
                System.out.println("Server is listening on localhost:" + e.actualPort())
            ).onFailure(e -> {
                System.out.println("Cannot start server, got error: " + e.getLocalizedMessage());
                System.exit(1);
            });
    }

    public static void main(String[] args) {
        new VertxServer().start();
    }
}
