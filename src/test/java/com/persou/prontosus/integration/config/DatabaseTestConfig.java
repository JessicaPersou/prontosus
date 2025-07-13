package com.persou.prontosus.integration.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("test")
public class DatabaseTestConfig {

    @Component
    @Profile("test")
    public static class DatabaseCleaner {

        @PersistenceContext
        private EntityManager entityManager;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Transactional
        public void cleanDatabase() {
            try {
                log.info("Limpando banco de dados para testes...");

                entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();


                List<String> tableNames = jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE'",
                    String.class
                );

                for (String tableName : tableNames) {
                    if (!tableName.toLowerCase().contains("flyway")) {
                        try {
                            jdbcTemplate.execute("TRUNCATE TABLE " + tableName + " CASCADE");
                            log.debug("Tabela {} limpa", tableName);
                        } catch (Exception e) {
                            log.warn("Erro ao limpar tabela {}: {}", tableName, e.getMessage());
                        }
                    }
                }

                entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();

                log.info("Limpeza do banco de dados concluída");

            } catch (Exception e) {
                log.error("Erro ao limpar banco de dados: {}", e.getMessage(), e);
                throw new RuntimeException("Falha na limpeza do banco de dados", e);
            }
        }

        @Transactional
        public void cleanSpecificTables(String... tableNames) {
            try {
                log.info("Limpando tabelas específicas: {}", String.join(", ", tableNames));

                for (String tableName : tableNames) {
                    try {
                        jdbcTemplate.execute("DELETE FROM " + tableName);
                        log.debug("Tabela {} limpa", tableName);
                    } catch (Exception e) {
                        log.warn("Erro ao limpar tabela {}: {}", tableName, e.getMessage());
                    }
                }

                log.info("Limpeza das tabelas específicas concluída");

            } catch (Exception e) {
                log.error("Erro ao limpar tabelas específicas: {}", e.getMessage(), e);
                throw new RuntimeException("Falha na limpeza das tabelas", e);
            }
        }

        @Transactional
        public void resetSequences() {
            try {
                log.info("Resetando sequências...");

                // Para PostgreSQL, reseta todas as sequências
                List<String> sequences = jdbcTemplate.queryForList(
                    "SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public'",
                    String.class
                );

                for (String sequence : sequences) {
                    try {
                        jdbcTemplate.execute("ALTER SEQUENCE " + sequence + " RESTART WITH 1");
                        log.debug("Sequência {} resetada", sequence);
                    } catch (Exception e) {
                        log.warn("Erro ao resetar sequência {}: {}", sequence, e.getMessage());
                    }
                }

                log.info("Reset de sequências concluído");

            } catch (Exception e) {
                log.error("Erro ao resetar sequências: {}", e.getMessage(), e);
            }
        }
    }

    @Component
    @Profile("test")
    public static class DatabaseHelper {

        @Autowired
        private DatabaseCleaner databaseCleaner;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        public void cleanAll() {
            databaseCleaner.cleanDatabase();
            databaseCleaner.resetSequences();
        }

        public void cleanTables(String... tableNames) {
            databaseCleaner.cleanSpecificTables(tableNames);
        }

        public long countRecords(String tableName) {
            try {
                return jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + tableName,
                    Long.class
                );
            } catch (Exception e) {
                log.warn("Erro ao contar registros da tabela {}: {}", tableName, e.getMessage());
                return 0L;
            }
        }

        public boolean tableExists(String tableName) {
            try {
                Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?",
                    Long.class,
                    tableName.toLowerCase()
                );
                return count != null && count > 0;
            } catch (Exception e) {
                log.warn("Erro ao verificar existência da tabela {}: {}", tableName, e.getMessage());
                return false;
            }
        }

        public void insertTestData() {
            try {
                log.info("Inserindo dados de teste...");

                Long adminCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users WHERE username = 'admin'",
                    Long.class
                );

                if (adminCount == 0) {
                    jdbcTemplate.update("""
                        INSERT INTO users (id, username, password, full_name, email, professional_document, role, active, created_at, updated_at)
                        VALUES ('admin-test-id', 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
                                'Admin Test', 'admin@test.com', 'ADMIN001', 'ADMIN', true, NOW(), NOW())
                        """);
                    log.debug("Usuário admin de teste criado");
                }

                // Inserir usuário doctor se não existir
                Long doctorCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users WHERE username = 'doctor'",
                    Long.class
                );

                if (doctorCount == 0) {
                    jdbcTemplate.update("""
                        INSERT INTO users (id, username, password, full_name, email, professional_document, role, specialty, active, created_at, updated_at)
                        VALUES ('doctor-test-id', 'doctor', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
                                'Dr. Test', 'doctor@test.com', 'CRM123456', 'DOCTOR', 'Clínico Geral', true, NOW(), NOW())
                        """);
                    log.debug("Usuário doctor de teste criado");
                }

                // Inserir usuário nurse se não existir
                Long nurseCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users WHERE username = 'nurse'",
                    Long.class
                );

                if (nurseCount == 0) {
                    jdbcTemplate.update("""
                        INSERT INTO users (id, username, password, full_name, email, professional_document, role, specialty, active, created_at, updated_at)
                        VALUES ('nurse-test-id', 'nurse', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
                                'Nurse Test', 'nurse@test.com', 'COREN123456', 'NURSE', 'Enfermagem Geral', true, NOW(), NOW())
                        """);
                    log.debug("Usuário nurse de teste criado");
                }

                log.info("Dados de teste inseridos com sucesso");

            } catch (Exception e) {
                log.error("Erro ao inserir dados de teste: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao inserir dados de teste", e);
            }
        }
    }
}