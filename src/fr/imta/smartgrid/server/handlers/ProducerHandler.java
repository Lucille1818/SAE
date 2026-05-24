package fr.imta.smartgrid.server.handlers;

import java.util.List;
import io.vertx.ext.web.RoutingContext;
import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Producer;
import jakarta.persistence.EntityManager;

public class ProducerHandler {
    private EntityManager db;

    public ProducerHandler(EntityManager db) {
        this.db = db;
    }

    public void getProducers(RoutingContext ctx) {
        List <Integer> producerIds = db.createNativeQuery("SELECT pr.id from Person as pr").getResultList();
        ctx.json(producerIds);
    }

    public void getById(RoutingContext ctx) {
        Producer pr = db.find(Producer.class, Integer.parseInt(ctx.pathParam("id")));
        if (pr == null){
            ctx.fail(404);
        } else {
            ctx.json(pr.toJSON());
        }
    }
        
}

        
