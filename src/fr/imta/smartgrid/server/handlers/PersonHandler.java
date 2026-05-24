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
}
