package pl.lodz.p;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Map;

@QuarkusTest
@QuarkusTestResource(AbstractTest.MongoTestResource.class)
public abstract class AbstractTest {
    public static class MongoTestResource implements QuarkusTestResourceLifecycleManager {
        static final MongoDBContainer MONGO = new MongoDBContainer("mongo:6.0");

        @Override
        public Map<String, String> start() {
            MONGO.start();
            return Map.of("quarkus.mongodb.connection-string", MONGO.getReplicaSetUrl());
        }

        @Override
        public void stop() {
            MONGO.stop();
        }
    }
}