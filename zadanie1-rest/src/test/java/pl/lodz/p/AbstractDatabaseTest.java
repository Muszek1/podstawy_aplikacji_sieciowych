package pl.lodz.p;

import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import pl.lodz.p.repository.MongoDatabaseProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDatabaseTest {

    protected static final String CONNECTION_STRING =
            "mongodb://host.docker.internal:27017,host.docker.internal:27018,host.docker.internal:27019/?replicaSet=rs0";
    protected static final String TEST_DB_NAME = "nbd_hotel_test";

    protected static MongoClient mongoClient;
    protected static MongoDatabase database;

    @BeforeAll
    public static void setupDatabase() {
        mongoClient = MongoDatabaseProvider.createClient(CONNECTION_STRING);
        mongoClient.getDatabase(TEST_DB_NAME).drop();
        database = MongoDatabaseProvider.initDatabase(mongoClient, TEST_DB_NAME);
    }

    protected void clearDatabase() {
        database.getCollection("rents").deleteMany(new Document());
        database.getCollection("rooms").deleteMany(new Document());
        database.getCollection("guests").deleteMany(new Document());
    }

    protected String stopPrimaryNode() {
        Document hello = database.runCommand(new Document("hello", 1), ReadPreference.primaryPreferred());
        String oldPrimaryHost = hello.getString("primary");
        if (oldPrimaryHost == null) {
            throw new IllegalStateException("Brak aktywnego węzła Primary przed testem failover");
        }

        String containerName = mapHostToContainerName(oldPrimaryHost);

        try {
            new ProcessBuilder("docker", "stop", containerName).start().waitFor(30, TimeUnit.SECONDS);

            // 1. Monitorowanie stanu kontenera aż faktycznie się zatrzyma
            waitForContainerRunningState(containerName, false);

            // 2. Monitorowanie klastra aż zostanie wybrany NOWY węzeł Primary
            waitForNewPrimary(oldPrimaryHost);

            return containerName;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas zatrzymywania węzła Primary: " + containerName, e);
        }
    }

    protected void startNode(String containerName) {
        if (containerName == null) {
            return;
        }
        try {
            new ProcessBuilder("docker", "start", containerName).start().waitFor(30, TimeUnit.SECONDS);

            // 1. Monitorowanie stanu kontenera aż faktycznie wystartuje
            waitForContainerRunningState(containerName, true);

            // 2. Monitorowanie klastra aż wszystkie 3 węzły będą w pełni zsynchronizowane (PRIMARY/SECONDARY)
            waitForAllThreeNodesHealthy();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas ponownego uruchamiania węzła: " + containerName, e);
        }
    }

    private String mapHostToContainerName(String host) {
        return switch (host) {
            case "host.docker.internal:27017", "localhost:27017", "mongo1:27017" -> "mongo1";
            case "host.docker.internal:27018", "localhost:27018", "mongo2:27018" -> "mongo2";
            case "host.docker.internal:27019", "localhost:27019", "mongo3:27019" -> "mongo3";
            default -> throw new IllegalStateException("Nieznany adres węzła Primary: " + host);
        };
    }

    private void waitForContainerRunningState(String containerName, boolean expectedRunning) throws Exception {
        for (int i = 0; i < 60; i++) {
            Process process = new ProcessBuilder(
                    "docker", "inspect", "-f", "{{.State.Running}}", containerName
            ).start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String output = reader.readLine();
                process.waitFor(5, TimeUnit.SECONDS);
                if (output != null && Boolean.parseBoolean(output.trim()) == expectedRunning) {
                    return;
                }
            }
            Thread.sleep(250);
        }
        throw new IllegalStateException("Kontener " + containerName + " nie osiągnął stanu Running=" + expectedRunning);
    }

    private void waitForNewPrimary(String oldPrimaryHost) throws InterruptedException {
        for (int i = 0; i < 60; i++) {
            try {
                Document hello = database.runCommand(new Document("hello", 1), ReadPreference.primaryPreferred());
                String currentPrimary = hello.getString("primary");
                if (currentPrimary != null && !currentPrimary.equals(oldPrimaryHost)) {
                    return;
                }
            } catch (Exception ignored) {
            }
            Thread.sleep(500);
        }
        throw new IllegalStateException("Klaster nie wybrał nowego węzła Primary po wyłączeniu " + oldPrimaryHost);
    }

    @SuppressWarnings("unchecked")
    private void waitForAllThreeNodesHealthy() throws InterruptedException {
        Set<String> readyStates = Set.of("PRIMARY", "SECONDARY");
        for (int i = 0; i < 60; i++) {
            try {
                Document status = mongoClient.getDatabase("admin")
                        .runCommand(new Document("replSetGetStatus", 1), ReadPreference.primaryPreferred());
                List<Document> members = (List<Document>) status.get("members");
                long readyCount = members.stream()
                        .filter(m -> Double.valueOf(1.0).equals(m.getDouble("health"))
                                && readyStates.contains(m.getString("stateStr")))
                        .count();
                if (readyCount == 3) {
                    return;
                }
            } catch (Exception ignored) {
            }
            Thread.sleep(500);
        }
        throw new IllegalStateException("Nie wszystkie 3 węzły klastra wróciły do stanu PRIMARY/SECONDARY");
    }

    @AfterAll
    public static void closeDatabase() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}