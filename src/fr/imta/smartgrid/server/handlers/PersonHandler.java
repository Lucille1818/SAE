package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Person;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;


/**
Handler gérant les routes liées aux personnes (Person).
Une personne est associée à une grille et peut posséder plusieurs capteurs.
 */

public class PersonHandler {
    private EntityManager db;

    /**
    Constructeur : reçoit l'EntityManager partagé pour accéder à la base de données.
     */

    public PersonHandler(EntityManager db) {
        this.db = db;
    }

    /**
    GET /persons
    Retourne la liste des IDs de toutes les personnes présentes en base
    via la requête SQL : SELECT p.id FROM person as p
     */

    public void getPersons(RoutingContext ctx) {
        List <Integer> personIds = db.createNativeQuery("SELECT p.id from Person as p").getResultList();
        ctx.json(personIds);
    }

    /**
    GET /person/:id
    Retourne le détail d'une personne identifiée par son ID.
    Utilise db.find() plutôt qu'une requête SQL manuelle.
    Répond 404 si la personne n'existe pas.
    Les données sont retournées via toJSON() défini dans Person.java, incluant firstName, lastName, la grille associée et la liste des capteurs possédés.
     */

    public void getById(RoutingContext ctx) {
        Person p = db.find(Person.class, Integer.parseInt(ctx.pathParam("id")));
        if (p == null){
            ctx.fail(404);
        } 
        else {
            ctx.json(p.toJSON());
        }
    }


    /**
    DELETE /person/:id
    Supprime une personne à partir de son ID.
    Répond :
    - 200 si suppression réussie
    - 404 si personne introuvable
    - 500 en cas d'erreur serveur
     */

    public void delete(RoutingContext ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));

            // Recherche de la personne
            Person p = db.find(Person.class, id);

            // Vérifie si la personne existe
            if (p == null) {

                ctx.response()
                   .setStatusCode(404)
                   .end("Person not found");

                return;
            }

            // Début transaction
            db.getTransaction().begin();

            // Suppression
            db.remove(p);

            // Validation transaction
            db.getTransaction().commit();

            // Réponse succès
            ctx.response()
               .setStatusCode(200)
               .end("Deleted successfully");

        }
        catch (Exception e) {

            // Annule la transaction si erreur
            if (db.getTransaction().isActive()) {
                db.getTransaction().rollback();
            }

            ctx.response()
               .setStatusCode(500)
               .end("Error during deletion");
        }
    }
}
