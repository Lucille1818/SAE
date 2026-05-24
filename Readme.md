# Smart Grid - API REST  / Ajout de la persistance

Il s'agit d'un projet pour la gestion d'une grille électrique intelligente (capteurs, producteurs, consommateurs).

## Technologies utilisées:

- **Java 25** avec **Vert.x** (serveur HTTP)
- **JPA / EclipseLink** (accès base de données)
- **PostgreSQL** (base de données)
- **Docker** (via `docker-compose.yml`)

## Lancer le projet:

1. Démarrer la base de données, dans le terminal:
   docker-compose up -d
2. Lancer le serveur depuis VS Code en exécutant `VertxServer.java`
3. Le serveur écoute sur `http://localhost:8080`.

---

## Structure du projet pour l'instant:

src/fr/imta/smartgrid/
├── model/
│   ├── Consume.java        
│   ├── DataPoint.java
│   ├── EVCharger.java
│   ├── Grid.java
│   ├── Measurement.java
│   ├── Person.java
│   ├── Producer.java
│   ├── Sensor.java
│   ├── SolarPanel.java
│   └── WindTurbine.java
└── server/
    ├── VertxServer.java
    └── handlers/
        ├── SensorHandler.java
        ├── MeasurementHandler.java
        ├── ProducerHandler.java
        ├── ConsumerHandler.java
        ├── PersonHandler.java
        └── GridHandler.java

---

## Endpoints disponibles:

### Grilles
| Méthode |    Route    |       Description        |
|---------|-------------|--------------------------|
| GET     | `/grids`    | Liste toutes les grilles |
| GET     | `/grid/:id` | Détail d'une grille      |

### Personnes
| Méthode |     Route     |        Description         |
|---------|---------------|----------------------------|
| GET     | `/persons`    | Liste toutes les personnes |
| GET     | `/person/:id` | Détail d'une personne      |

### Capteurs
| Méthode |      Route    |              Description                 |
|---------|---------------|------------------------------------------|
| GET     | `/sensor`     | Liste les IDs de tous les capteurs       |
| GET     | `/sensor/:id` | Détail d'un capteur                      |
| POST    | `/sensor/:id` | Mise à jour nom/description d'un capteur |

### Mesures
| Méthode |            Route          |             Description             |
|---------|---------------------------|-------------------------------------|
| GET.    | `/measurement`            | Liste les IDs de toutes les mesures |
| GET     | `/measurement/:id`        | Détail d'une mesure                 |
| GET     | `/measurement/:id/values` | Liste des datapoints d'une mesure   |

### Producteurs
| Méthode |       Route     |       Description      |
|---------|-----------------|------------------------|
| GET     | `/producer`     | Liste les producteurs  |
| GET     | `/producer/:id` | Détail d'un producteur |

### Consommateurs
| Méthode |      Route      |       Description        |
|---------|-----------------|--------------------------|
| GET     | `/consumer`     | Liste les consommateurs  |
| GET     | `/consumer/:id` | Détail d'un consommateur |

### Ingress (partie avancée)
| Méthode |           Route        |                 Description                   |
|---------|------------------------|-----------------------------------------------|
| POST    | `/ingress/windturbine` | Enregistre une mesure pour une éolienne       |
| POST    | `/ingress/solarpanel`  | Enregistre une mesure pour un panneau solaire |

---

## Routes implémentées:

→ GET /grids
Fourni au départ. On récupère l'ensemble des grilles à l'aide de la requête SQL:
SELECT g.id FROM grid as g, c'est-à-dire en récupérant l'ensemble des id de la table grid.


→ GET /grid/{id}
On utilise directement db.find(Grid.class, id) qui renvoie la ligne de données de la classe Grid correspondant à l'id donné.
Si aucune grille ne correspond à cet id, on renvoie une erreur 404.
Pour la renvoyer, on utilise la fonction toJSON() implémentée dans Grid.java qui retourne les données de la grille au format JSON, incluant la liste des personnes et des capteurs associés.

→ GET /persons
On récupère l'ensemble des personnes à l'aide de la requête SQL : SELECT p.id FROM person as p,
c'est-à-dire en récupérant l'ensemble des id de la table person.

→ GET /person/{id}
Au lieu d'utiliser une requête SQL de type SELECT * FROM person WHERE id = ?, on utilise directement db.find(Person.class, id) qui renvoie la ligne de données de la classe Person correspondant à l'id donné.
Pour la renvoyer, on utilise la fonction toJSON() ajoutée à Person.java qui retourne au format JSON les champs id, firstName, lastName, grid, et la liste des sensors possédés.

→ GET /measurement/{id}
On récupère les informations d'une mesure grâce à db.find(Measurement.class, id).
Ces informations sont renvoyées avec la fonction toJSON() implémentée dans Measurement.java, qui retourne l'id, l'unit, le name et la liste des ids des datapoints associés.

→ GET /measurement/{id}/values
On récupère la mesure avec db.find, puis on parcourt l'ensemble de ses DataPoint.
Pour chaque point, on construit un objet JSON contenant son id, son timestamp et sa value.
On renvoie la liste de tous ces objets JSON. Si la mesure n'existe pas, on retourne 404.

→ GET /sensor/{id}
On récupère les détails d'un capteur grâce à db.find(Sensor.class, id).
Si aucun capteur ne correspond à cet id, on renvoie une erreur 404.
Ces informations sont renvoyées avec la fonction toJSON() implémentée dans Sensor.java.
La classe Sensor est abstraite et héritée par WindTurbine, SolarPanel et EVCharger : toJSON() est surchargée dans chaque sous-classe pour ajouter les champs spécifiques au type de capteur.

→ POST /sensor/{id}
On commence par vérifier que le capteur existe avec db.find. Si ce n'est pas le cas, on renvoie 404.
On lit ensuite le corps de la requête au format JSON. À l'aide de containsKey, on ne modifie que les champs effectivement renseignés par l'utilisateur (name et/ou description).
On ouvre une transaction avec db.getTransaction().begin(), on applique les modifications, puis on la valide avec db.getTransaction().commit().
On retourne le capteur mis à jour au format JSON.

→ POST /ingress/windturbine et POST /ingress/solarpanel
Ces deux routes utilisent une méthode générique ingressMeasurement paramétrée par le type du capteur (WindTurbine ou SolarPanel).
Le corps JSON attendu pour les routes ingress serait : 
{
  "sensorId": 1,
  "value": 42.5,
  "timestamp": 1716570000
}

On commence par lire le corps de la requête. On vérifie que le capteur existe en base avec db.find. On récupère ensuite la première mesure (Measurement) associée à ce capteur. On crée alors un nouveau DataPoint avec le timestamp, la value, et la référence à la mesure. On ouvre une transaction, on persiste le DataPoint avec db.persist, on valide avec db.getTransaction().commit(), et on retourne une réponse 201 avec les informations du point créé.
Si le timestamp n'est pas fourni, on utilise l'heure actuelle en secondes.

---

## Ce qu'il reste à faire:

- POST /person - Créer une personne
- PUT /person/:id — Mettre à jour une personne
- DELETE /person/:id — Supprimer une personne
  => les routes ont été mises en commentaires dans le VertxServer.java
- GET /grid/:id/production — Production totale d'une grille
- GET /grid/:id/consumption — Consommation totale d'une grille
- GET /sensors/{kind} — Lister les capteurs par type (SolarPanel, WindTurbine, EVCharger)

---

## Difficultés rencontrées:

- Il m'a fallu un certain moment pour comprendre les Json, mais je me base beaucoup sur les exemples donnés pour les créer. J'ai peur de ne pas savoir les faire sans aucun exemple. Ce n'est pas assez instinctif pour moi, je dois encore les étudier un peu plus.

- Parfois au démarrage du VertxServer, l'erreur "Address already" in use apparaissait car une ancienne instance du serveur tournait encore en arrière-plan. 
Je me suis permise d'utiliser l'IA pour savoir ce qu'il se passait et Claude m'a fourni ceci à rentrer dans mon terminal, la commande: lsof -ti:8080 | xargs kill -9, ceci permettrait de tuer le processus occupant le port avec la avant de relancer.

- Safari semble incompatible avec localhost, il bloque les connexions vers localhost et affiche ERR_CONNECTION_REFUSED...  Il est donc nécessaire d'utiliser Chrome pour tester les routes GET directement depuis le navigateur.

- Erreurs lors des tests des routes POST:
Les routes POST renvoie des erreurs car elles sont testées depuis le navigateur, qui ne semble pas pouvoir envoyer de JSON. Je n'ai pas encore trouvé de solution.

- Affichage des détails d'un capteur:
Au début, quand j'essayais d'afficher les détails d'un capteur, le serveur renvoyait une erreur 500. 
J'ai testé de convertir l'objet en JSON manuellement avec toJSON() avant de l'envoyer, ça semble fonctionner pour l'instant.

- Je n'ai pas encore pu me pencher sur la partie HARD des consignes, j'ai peur de ne pas avoir le temps de le faire ou de ne pas comprendre comment l'entreprendre...
