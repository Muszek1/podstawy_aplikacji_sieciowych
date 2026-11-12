package pl.lodz.p;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class MongoDatabaseInitializer {
    private final MongoClient mongoClient;

    public MongoDatabaseInitializer(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @PostConstruct
    public void configureDatabase() {
        MongoDatabase db = mongoClient.getDatabase("pasdb");

        String userSchema = """
        {
          $jsonSchema: {
            bsonType: "object",
            required: ["login", "firstName", "lastName", "version"],
            properties: {
              login: { 
                bsonType: "string", 
                pattern: "^(?!\\\\s*$).+" 
              },
              firstName: { 
                bsonType: "string", 
                pattern: "^(?!\\\\s*$).+" 
              },
              lastName: { 
                bsonType: "string", 
                pattern: "^(?!\\\\s*$).+" 
              },
              version: {
                bsonType: ["long", "int", "number"]
              }
            }
          }
        }
        """;

        applySchemaAndIndexes(db, "users", userSchema, "login");

        String roomSchema = """
        {
          $jsonSchema: {
            bsonType: "object",
            required: ["number", "bedsAmount", "price", "version"],
            properties: {
              number: { 
                bsonType: "string", 
                pattern: "^(?!\\\\s*$).+",
                description: "Numer musi być niepustym stringiem"
              },
              bedsAmount: { 
                bsonType: ["int", "long", "number"],
                description: "Liczba łóżek nie może być nullem"
              },
              price: { 
                bsonType: ["double", "number"],
                description: "Cena nie może być nullem"
              },
              version: { 
                bsonType: ["long", "int", "number"] 
              }
            }
          }
        }
        """;

        applySchemaAndIndexes(db, "rooms", roomSchema, "number");

        String rentSchema = """
        {
          $jsonSchema: {
            bsonType: "object",
            required: ["beginTime", "client", "room", "version"],
            properties: {
              beginTime: { 
                bsonType: ["date", "string"],
                description: "Data rozpoczęcia nie może być nullem"
              },
              client: { 
                bsonType: "objectId",
                description: "Odwołanie do klienta musi być prawidłowym identyfikatorem bazy"
              },
              room: { 
                bsonType: "objectId",
                description: "Odwołanie do pokoju musi być prawidłowym identyfikatorem bazy"
              },
              version: { 
                bsonType: ["long", "int", "number"] 
              }
            }
          }
        }
        """;

        applySchemaAndIndexes(db, "rents", rentSchema, null);
    }

    private void applySchemaAndIndexes(MongoDatabase db, String collectionName, String jsonSchema, String uniqueField) {
        Document validator = Document.parse(jsonSchema);
        ValidationOptions validationOptions = new ValidationOptions().validator(validator);

        boolean exists = db.listCollectionNames().into(new java.util.ArrayList<>()).contains(collectionName);
        if (!exists) {
            db.createCollection(collectionName, new CreateCollectionOptions().validationOptions(validationOptions));
        } else {
            db.runCommand(new Document("collMod", collectionName).append("validator", validator));
        }

        if (uniqueField != null) {
            db.getCollection(collectionName).createIndex(
                    Indexes.ascending(uniqueField),
                    new IndexOptions().unique(true)
            );
        }
    }
}
