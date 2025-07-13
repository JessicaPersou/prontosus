package com.persou.prontosus.integration.helpers;

import com.persou.prontosus.integration.config.DatabaseTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHelper {

    @Autowired
    private DatabaseTestConfig.DatabaseCleaner databaseCleaner;

    public void cleanDatabase() {
        databaseCleaner.cleanDatabase();
    }
}