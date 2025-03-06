package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class ColumnMapping {
    private final Properties properties;

    public ColumnMapping() throws IOException {
        // Načtení souboru database.properties
        Resource resource = new ClassPathResource("database.properties");
        this.properties = PropertiesLoaderUtils.loadProperties(resource);
    }

    public String getColumn(String table, String column) {
        // Vrátí název sloupce na základě klíče (např. "users.login")
        return properties.getProperty(table + "." + column, column); // Pokud nenajde, vrátí původní název
    }
}

