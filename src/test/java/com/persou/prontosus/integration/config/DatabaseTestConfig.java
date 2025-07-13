package com.persou.prontosus.integration.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
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
                log.debug("Iniciando limpeza do banco de dados...");

                jdbcTemplate.execute("SET session_replication_role = 'replica'");

                List<String> tableNames = jdbcTemplate.queryForList("""
                    SELECT tablename FROM pg_tables 
                    WHERE schemaname = 'public' 
                    AND tablename NOT LIKE 'pg_%' 
                    AND tablename NOT LIKE 'sql_%'
                    AND tablename NOT LIKE 'flyway_%'
                    """, String.class);

                log.debug("Tabelas encontradas para limpeza: {}", tableNames);

                for (String tableName : tableNames) {
                    try {
                        jdbcTemplate.execute("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE");
                        log.trace("Tabela {} limpa com sucesso", tableName);
                    } catch (DataAccessException e) {
                        log.warn("Erro ao limpar tabela {}: {}", tableName, e.getMessage());
                        // Tentar método alternativo
                        try {
                            jdbcTemplate.execute("DELETE FROM " + tableName);
                            log.trace("Tabela {} limpa com DELETE", tableName);
                        } catch (DataAccessException e2) {
                            log.error("Falha ao limpar tabela {} com DELETE: {}", tableName, e2.getMessage());
                        }
                    }
                }

                resetSequences();

                jdbcTemplate.execute("SET session_replication_role = 'origin'");

                log.debug("Limpeza do banco concluída. {} tabelas processadas", tableNames.size());

            } catch (Exception e) {
                log.error("Erro crítico na limpeza do banco: {}", e.getMessage(), e);

                try {
                    jdbcTemplate.execute("SET session_replication_role = 'origin'");
                } catch (Exception ex) {
                    log.error("Erro ao reabilitar constraints: {}", ex.getMessage());
                }

                throw new RuntimeException("Falha na limpeza do banco de dados", e);
            }
        }

        @Transactional
        public void cleanSpecificTables(String... tableNames) {
            try {
                log.debug("Limpando tabelas específicas: {}", String.join(", ", tableNames));

                for (String tableName : tableNames) {
                    try {
                        jdbcTemplate.execute("DELETE FROM " + tableName);
                        log.trace("Tabela {} limpa", tableName);
                    } catch (DataAccessException e) {
                        log.warn("Erro ao limpar tabela {}: {}", tableName, e.getMessage());
                    }
                }

                log.debug("Limpeza de tabelas específicas concluída");

            } catch (Exception e) {
                log.error("Erro ao limpar tabelas específicas: {}", e.getMessage(), e);
                throw new RuntimeException("Falha na limpeza das tabelas", e);
            }
        }

        @Transactional
        public void resetSequences() {
            try {
                log.debug("Resetando sequências...");

                List<String> sequences = jdbcTemplate.queryForList("""
                    SELECT sequence_name FROM information_schema.sequences 
                    WHERE sequence_schema = 'public'
                    """, String.class);

                for (String sequence : sequences) {
                    try {
                        jdbcTemplate.execute("ALTER SEQUENCE " + sequence + " RESTART WITH 1");
                        log.trace("Sequência {} resetada", sequence);
                    } catch (DataAccessException e) {
                        log.warn("Erro ao resetar sequência {}: {}", sequence, e.getMessage());
                    }
                }

                log.debug("Reset de {} sequências concluído", sequences.size());

            } catch (Exception e) {
                log.error("Erro ao resetar sequências: {}", e.getMessage(), e);
            }
        }

        public void testConnection() {
            try {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                log.trace("Conexão com banco testada com sucesso");
            } catch (Exception e) {
                log.error("Falha no teste de conexão: {}", e.getMessage());
                throw new RuntimeException("Banco não está acessível", e);
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
            try {
                databaseCleaner.cleanDatabase();
            } catch (Exception e) {
                log.error("Erro na limpeza completa: {}", e.getMessage(), e);
                throw e;
            }
        }

        public void cleanTables(String... tableNames) {
            try {
                databaseCleaner.cleanSpecificTables(tableNames);
            } catch (Exception e) {
                log.error("Erro na limpeza de tabelas específicas: {}", e.getMessage(), e);
                throw e;
            }
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
                Long count = jdbcTemplate.queryForObject("""
                    SELECT COUNT(*) FROM information_schema.tables 
                    WHERE table_schema = 'public' 
                    AND table_name = ?
                    """, Long.class, tableName.toLowerCase());
                return count != null && count > 0;
            } catch (Exception e) {
                log.warn("Erro ao verificar existência da tabela {}: {}", tableName, e.getMessage());
                return false;
            }
        }

        public void testConnection() {
            databaseCleaner.testConnection();
        }

        @Transactional
        public void insertTestUsers() {
            try {
                log.debug("Inserindo usuários de teste...");

                insertUserIfNotExists(
                    "admin-test-id", "admin", "Admin Test", "admin@test.com",
                    "ADMIN001", "ADMIN", null
                );

                insertUserIfNotExists(
                    "doctor-test-id", "doctor", "Dr. Test", "doctor@test.com",
                    "CRM123456", "DOCTOR", "Clínico Geral"
                );

                insertUserIfNotExists(
                    "nurse-test-id", "nurse", "Nurse Test", "nurse@test.com",
                    "COREN123456", "NURSE", "Enfermagem Geral"
                );

                log.debug("Usuários de teste inseridos com sucesso");

            } catch (Exception e) {
                log.error("Erro ao inserir usuários de teste: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao criar usuários de teste", e);
            }
        }

        private void insertUserIfNotExists(String id, String username, String fullName,
                                           String email, String document, String role, String specialty) {
            try {
                Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users WHERE username = ?",
                    Long.class, username
                );

                if (count == 0) {
                    if (specialty != null) {
                        jdbcTemplate.update("""
                            INSERT INTO users (id, username, password, full_name, email, 
                                             professional_document, role, specialty, active, created_at, updated_at)
                            VALUES (?, ?, '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
                                   ?, ?, ?, ?, ?, true, NOW(), NOW())
                            """, id, username, fullName, email, document, role, specialty);
                    } else {
                        jdbcTemplate.update("""
                            INSERT INTO users (id, username, password, full_name, email, 
                                             professional_document, role, active, created_at, updated_at)
                            VALUES (?, ?, '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
                                   ?, ?, ?, ?, true, NOW(), NOW())
                            """, id, username, fullName, email, document, role);
                    }
                    log.trace("Usuário {} inserido", username);
                } else {
                    log.trace("Usuário {} já existe", username);
                }
            } catch (Exception e) {
                log.error("Erro ao inserir usuário {}: {}", username, e.getMessage(), e);
                throw new RuntimeException("Falha ao inserir usuário: " + username, e);
            }
        }

        public void waitForDatabase() {
            int maxAttempts = 30;
            for (int i = 0; i < maxAttempts; i++) {
                try {
                    testConnection();
                    log.debug("Database ready after {} attempts", i + 1);
                    return;
                } catch (Exception e) {
                    if (i == maxAttempts - 1) {
                        throw new RuntimeException("Database not ready after " + maxAttempts + " attempts", e);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted waiting for database", ie);
                    }
                }
            }
        }

        public void createTestSchema() {
            try {
                log.debug("Verificando/criando schema de teste...");

                if (!tableExists("users")) {
                    log.info("Criando schema básico para testes...");
                    createBasicSchema();
                }

            } catch (Exception e) {
                log.error("Erro ao criar schema de teste: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao criar schema de teste", e);
            }
        }

        private void createBasicSchema() {
            log.debug("Schema será criado automaticamente pelo Hibernate");
        }
    }
}