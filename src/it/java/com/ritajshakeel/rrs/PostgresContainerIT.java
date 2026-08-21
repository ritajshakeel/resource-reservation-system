package com.ritajshakeel.rrs;

import static org.assertj.core.api.Assertions.assertThat;
import java.sql.Connection;
import java.sql.DriverManager;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerIT {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Test
    public void testCanConnectToContainer() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            assertThat(connection.isValid(2)).isTrue();
        }
    }
}