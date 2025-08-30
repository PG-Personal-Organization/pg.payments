package pg.payments.standalone.test;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pg.imports.plugin.api.data.*;
import pg.payments.standalone.config.PaymentsImportsIntegrationTest;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static pg.lib.common.spring.auth.HeaderNames.CONTEXT_TOKEN;

@PaymentsImportsIntegrationTest
@AutoConfigureWireMock(port = 4099, stubs = "classpath:/wiremock/mappings", files = "classpath:/wiremock")
@EmbeddedKafka(brokerProperties = { "transaction.max.timeout.ms=3600000" })
@Testcontainers
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
class PaymentsAccountTransfersImportIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry) {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
    }

    @MethodSource("shouldParseAndImportsRecordsCorrectlyData")
    @ParameterizedTest
    @SneakyThrows
    void shouldParseAndImportRecordsCorrectly(final String pluginCode, final String fileName, final int expectedPartitionsCount, final int expectedRecordsCount) {
        // given
        String file = "test/data/" + fileName;
        try (InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(file)) {
            var fileId = RestAssured
                    .given()
                    .header(CONTEXT_TOKEN, "MOCK_CONTEXT_TOKEN")
                    .multiPart("file", fileName, fileInputStream)

                    .when()
                    .post("/api/v1/files/rest/upload")

                    .then()
                    .statusCode(200)
                    .extract().as(UUID.class);

            // when
            var importId = RestAssured
                    .given()
                    .header(CONTEXT_TOKEN, "MOCK_CONTEXT_TOKEN")

                    .when()
                    .post("/api/v1/imports/start/{fileId}/{pluginCode}", fileId, pluginCode)

                    .then()
                    .statusCode(200)
                    .extract().as(ImportId.class);

            // then
            AtomicReference<ImportData> importData = new AtomicReference<>();
            AtomicReference<ImportStatus> status = new AtomicReference<>();
            await()
                    .atMost(60, TimeUnit.SECONDS)
                    .pollInterval(5, TimeUnit.SECONDS)
                    .until(() -> {
                        var data = Optional.ofNullable(getImportData(importId.id(), List.of(ImportStatus.PARSING_FINISHED, ImportStatus.PARSING_FAILED)));
                        status.set(data.map(ImportData::getStatus).orElse(null));
                        importData.set(data.orElse(null));
                        return data.isPresent();
                    });

            if (status.get() == null || status.get() == ImportStatus.PARSING_FAILED) {
                throw new RuntimeException("Import with id " + importId.id() + " failed parsing.");
            }

            var partitions = getImportRecords(importId.id()).getPartitions();
            Assertions.assertEquals(expectedPartitionsCount, partitions.size());
            Assertions.assertTrue(partitions.stream().allMatch(partition -> partition.getRecordsStatus().equals(RecordsStatus.PARSED)));
            Assertions.assertEquals(expectedRecordsCount, partitions.stream().mapToLong(partition -> partition.getSuccessRecordIds().size()).sum());

            System.out.println("\n\nImport parsing with id finished in: "
                    + Duration.between(importData.get().getStartedParsingOn(), importData.get().getEndedParsingOn()).toMillis() + " ms.\n\n");

            TestTransaction.flagForCommit();
            TestTransaction.end();
            TestTransaction.start();

            // when
            RestAssured
                    .given()
                    .header(CONTEXT_TOKEN, "MOCK_CONTEXT_TOKEN")

                    .when()
                    .post("/api/v1/imports/confirm/{importId}", importId.id())

                    .then()
                    .statusCode(200);

            await()
                    .atMost(60, TimeUnit.SECONDS)
                    .pollInterval(5, TimeUnit.SECONDS)
                    .until(() -> {
                        var data = Optional.ofNullable(getImportData(importId.id(), List.of(ImportStatus.IMPORTING_COMPLETED, ImportStatus.IMPORTING_FAILED)));
                        status.set(data.map(ImportData::getStatus).orElse(null));
                        importData.set(data.orElse(null));
                        return data.isPresent();
                    });

            if (status.get() == null || status.get() == ImportStatus.IMPORTING_FAILED) {
                throw new RuntimeException("Import with id " + importId.id() + " failed importing.");
            }

            partitions = getImportRecords(importId.id()).getPartitions();
            Assertions.assertTrue(partitions.stream().allMatch(partition -> partition.getRecordsStatus().equals(RecordsStatus.IMPORTED)));

            System.out.println("\n\nImport importing with id finished in: "
                    + Duration.between(importData.get().getStartedImportingOn(), importData.get().getFinishedImportingOn()).toMillis() + " ms.\n\n");
        }
    }

    private ImportData getImportData(final String importId, final List<ImportStatus> statuses) {
        return RestAssured
                .given()
                .header(CONTEXT_TOKEN, "MOCK_CONTEXT_TOKEN")

                .when()
                .get("/api/v1/imports/status/{importId}/{statuses}", importId, statuses.stream().map(Enum::name).reduce((s, s2) -> s.concat("," + s2)).orElse(""))

                .then()
                .statusCode(200)
                .extract().as(ImportDataResponse.class).getData();
    }

    private ImportRecordsData getImportRecords(final String importId) {
        return RestAssured
                .given()
                .header(CONTEXT_TOKEN, "MOCK_CONTEXT_TOKEN")

                .when()
                .get("/api/v1/imports/records/{importId}", importId)

                .then()
                .statusCode(200)
                .extract().as(ImportRecordsData.class);
    }

    static Stream<Arguments> shouldParseAndImportsRecordsCorrectlyData() {
        return Stream.of(
                Arguments.of("SIMPLE_ACCOUNT_TRANSFERS", "import_500.csv", 3, 500),
                Arguments.of("SIMPLE_ACCOUNT_TRANSFERS", "import_1000.csv", 5, 1000),
                Arguments.of("SIMPLE_ACCOUNT_TRANSFERS", "import_2000.csv", 10, 2000),
                Arguments.of("SIMPLE_ACCOUNT_TRANSFERS", "import_10000.csv", 50, 10000),

                Arguments.of("SIMPLE_SELF_STORING_ACCOUNT_TRANSFERS", "import_500.csv", 3, 500),
                Arguments.of("SIMPLE_SELF_STORING_ACCOUNT_TRANSFERS", "import_1000.csv", 5, 1000),
                Arguments.of("SIMPLE_SELF_STORING_ACCOUNT_TRANSFERS", "import_2000.csv", 10, 2000),
                Arguments.of("SIMPLE_SELF_STORING_ACCOUNT_TRANSFERS", "import_10000.csv", 50, 10000),

                Arguments.of("PARALLEL_ACCOUNT_TRANSFERS", "import_500.csv", 3, 500),
                Arguments.of("PARALLEL_ACCOUNT_TRANSFERS", "import_1000.csv", 5, 1000),
                Arguments.of("PARALLEL_ACCOUNT_TRANSFERS", "import_2000.csv", 10, 2000),
                Arguments.of("PARALLEL_ACCOUNT_TRANSFERS", "import_10000.csv", 50, 10000),

                Arguments.of("DISTRIBUTED_ACCOUNT_TRANSFERS", "import_500.csv", 3, 500),
                Arguments.of("DISTRIBUTED_ACCOUNT_TRANSFERS", "import_1000.csv", 5, 1000),
                Arguments.of("DISTRIBUTED_ACCOUNT_TRANSFERS", "import_2000.csv", 10, 2000),
                Arguments.of("DISTRIBUTED_ACCOUNT_TRANSFERS", "import_10000.csv", 50, 10000),

                Arguments.of("DISTRIBUTED_MONGO_ACCOUNT_TRANSFERS", "import_500.csv", 3, 500),
                Arguments.of("DISTRIBUTED_MONGO_ACCOUNT_TRANSFERS", "import_1000.csv", 5, 1000),
                Arguments.of("DISTRIBUTED_MONGO_ACCOUNT_TRANSFERS", "import_2000.csv", 10, 2000),
                Arguments.of("DISTRIBUTED_MONGO_ACCOUNT_TRANSFERS", "import_10000.csv", 50, 10000)
        );
    }
}
