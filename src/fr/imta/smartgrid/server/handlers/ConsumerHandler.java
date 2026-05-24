package fr.imta.smartgrid.server.handlers;

import java.util.List;

import io.vertx.ext.web.RoutingContext;
import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Consumer;
import fr.imta.smartgrid.model.Producer;
import jakarta.persistence.EntityManager;
/**
Handler gérant les routes liées aux consommateurs d'énergie (Consumer).
Un consommateur est un capteur qui consomme de l'énergie, par exemple un EVCharger.
 */

public class ConsumerHandler {
    private EntityManager db;

    /**
    Constructeur : reçoit l'EntityManager partagé pour accéder à la base de données.
     */
    public ConsumerHandler(EntityManager db) {
        this.db = db;
    }

    /**
    GET /consumer
    Retourne la liste des IDs de tous les consommateurs présents en base via la requête JPQL : SELECT c.id FROM Consumer as c
     */
    public void getConsumers(RoutingContext ctx) {
        List <Integer> consumerIds = db.createQuery("SELECT c.id from Consumer as c").getResultList();
        ctx.json(consumerIds);
    }


    /**
    GET /consumer/:id
    Retourne le détail d'un consommateur identifié par son ID.
    Utilise db.find() pour récupérer directement l'objet en base.
    Répond 404 si le consommateur n'existe pas.
     */

    public void getById(RoutingContext ctx) {
        Consumer c = db.find(Consumer.class, Integer.parseInt(ctx.pathParam("id")));
        if (c == null){
            ctx.fail(404);
        } else {
            ctx.json(c);
        }
    }
        
}
