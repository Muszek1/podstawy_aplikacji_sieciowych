package pl.lodz.p.repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import pl.lodz.p.model.db.*;

import java.util.ArrayList;
import java.util.List;

public class MongoDatabaseProvider {

    public static MongoClient createClient(String connectionString) {
        // Jawna rejestracja wszystkich klas modelu bazodanowego (najpierw hierarchia GuestTypeEnt!)
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                .conventions(Conventions.DEFAULT_CONVENTIONS)
                .register(
                        GuestTypeEnt.class,
                        GuestTypeDefaultEnt.class,
                        GuestTypeExtraEnt.class,
                        GuestTypePremiumEnt.class,
                        AddressEnt.class,
                        UserEnt.class,
                        RoomEnt.class,
                        RentEnt.class
                )
                .automatic(true)
                .build();

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(pojoCodecProvider)
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .writeConcern(WriteConcern.MAJORITY)
                .codecRegistry(pojoCodecRegistry)
                .build();

        return MongoClients.create(settings);
    }
    public static MongoDatabase initDatabase(MongoClient client, String dbName) {
        MongoDatabase db = client.getDatabase(dbName);
        List<String> existingCollections = db.listCollectionNames().into(new ArrayList<>());

        // 1. Kolekcja ROOMS ze schematem walidacyjnym (price > 0, beds > 0)
        if (!existingCollections.contains("rooms")) {
            Document roomSchema = Document.parse("""
                {
                  "bsonType": "object",
                  "required": ["entity_uuid", "number", "beds", "price", "archived", "rented"],
                  "properties": {
                    "number": { "bsonType": "string", "minLength": 1 },
                    "beds": { "bsonType": "int", "minimum": 1 },
                    "price": { "bsonType": "double", "minimum": 0, "exclusiveMinimum": true },
                    "archived": { "bsonType": "bool" },
                    "rented": { "bsonType": "bool" }
                  }
                }
                """);
            db.createCollection("rooms", new CreateCollectionOptions().validationOptions(
                    new ValidationOptions().validator(Filters.jsonSchema(roomSchema))
                            .validationLevel(ValidationLevel.STRICT)
                            .validationAction(ValidationAction.ERROR)
            ));
        }
        db.getCollection("rooms").createIndex(Indexes.ascending("number"), new IndexOptions().unique(true));
        db.getCollection("rooms").createIndex(Indexes.ascending("entity_uuid"), new IndexOptions().unique(true));

        // 2. Kolekcja GUESTS ze schematem walidacyjnym
        if (!existingCollections.contains("guests")) {
            Document guestSchema = Document.parse("""
                {
                  "bsonType": "object",
                  "required": ["entity_uuid", "first_name", "last_name", "personal_id", "archived", "address", "guest_type"],
                  "properties": {
                    "first_name": { "bsonType": "string", "minLength": 1 },
                    "last_name": { "bsonType": "string", "minLength": 1 },
                    "personal_id": { "bsonType": "string", "minLength": 1 },
                    "archived": { "bsonType": "bool" }
                  }
                }
                """);
            db.createCollection("guests", new CreateCollectionOptions().validationOptions(
                    new ValidationOptions().validator(Filters.jsonSchema(guestSchema))
                            .validationLevel(ValidationLevel.STRICT)
                            .validationAction(ValidationAction.ERROR)
            ));
        }
        db.getCollection("guests").createIndex(Indexes.ascending("personal_id"), new IndexOptions().unique(true));
        db.getCollection("guests").createIndex(Indexes.ascending("entity_uuid"), new IndexOptions().unique(true));

        // 3. Kolekcja RENTS ze schematem walidacyjnym
        if (!existingCollections.contains("rents")) {
            Document rentSchema = Document.parse("""
                {
                  "bsonType": "object",
                  "required": ["entity_uuid", "begin_time", "end_time", "client", "room", "rent_cost", "active"],
                  "properties": {
                    "begin_time": { "bsonType": "date" },
                    "end_time": { "bsonType": "date" },
                    "rent_cost": { "bsonType": "double", "minimum": 0 },
                    "active": { "bsonType": "bool" }
                  }
                }
                """);
            db.createCollection("rents", new CreateCollectionOptions().validationOptions(
                    new ValidationOptions().validator(Filters.jsonSchema(rentSchema))
                            .validationLevel(ValidationLevel.STRICT)
                            .validationAction(ValidationAction.ERROR)
            ));
        }
        db.getCollection("rents").createIndex(Indexes.ascending("entity_uuid"), new IndexOptions().unique(true));

        return db;
    }
}