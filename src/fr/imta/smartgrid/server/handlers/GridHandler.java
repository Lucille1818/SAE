package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Grid;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

/**
Handler gérant les routes liées aux grilles électriques (Grid).
Une grille regroupe des personnes et des capteurs.
 */

public class GridHandler {
    private EntityManager db;

    /**
    Constructeur : reçoit l'EntityManager partagé pour accéder à la base de données.
     */

    public GridHandler(EntityManager db) {
        this.db = db;
    }

    /**
    GET /grids
    Retourne la liste des IDs de toutes les grilles présentes en base via la requête SQL : SELECT g.id FROM grid as g
     */

    public void getGrids(RoutingContext ctx) {
        List<Integer> grids = db.createNativeQuery("SELECT g.id from grid as g").getResultList();
        ctx.json(grids);
    }


    /**
    ET /grid/:id
    Retourne le détail d'une grille identifiée par son ID.
    Utilise db.find() plutôt qu'une requête SQL manuelle.
    Répond 404 si la grille n'existe pas.
    Les données sont retournées via toJSON() défini dans Grid.java, incluant le nom, la description, la liste des personnes et des capteurs associés.
     */

    public void getById(RoutingContext ctx) {
        Grid g = db.find(Grid.class, Integer.parseInt(ctx.pathParam("id")));
        if (g == null){
            ctx.fail(404);
        } 
        else {
            ctx.json(g.toJSON());
        }
    }
    
}
