package com.persou.prontosus.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@TestConfiguration
@Profile("test")
public class DatabaseTestConfig {

    @Bean
    @Primary
    public DatabaseCleaner databaseCleaner(DataSource dataSource) {
        return new DatabaseCleaner(dataSource);
    }

    public static class DatabaseCleaner {
        private final JdbcTemplate jdbcTemplate;

        public DatabaseCleaner(DataSource dataSource) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }

        @Transactional
        public void cleanDatabase() {
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

            jdbcTemplate.execute("DELETE FROM file_attachments");
            jdbcTemplate.execute("DELETE FROM medical_records");
            jdbcTemplate.execute("DELETE FROM appointments");
            jdbcTemplate.execute("DELETE FROM patients");
            jdbcTemplate.execute("DELETE FROM users");

            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS file_attachments_seq RESTART WITH 1");
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS medical_records_seq RESTART WITH 1");
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS appointments_seq RESTART WITH 1");
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS patients_seq RESTART WITH 1");
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS users_seq RESTART WITH 1");
        }
    }
}