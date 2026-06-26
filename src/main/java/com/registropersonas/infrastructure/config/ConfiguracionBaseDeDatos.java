package com.registropersonas.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Responsable unicamente de cargar la configuracion de conexion
 * a la base de datos desde el archivo database.properties.
 *
 * Principio de Responsabilidad Unica (SOLID - S).
 */
public final class ConfiguracionBaseDeDatos {

    private final Properties propiedades;

    public ConfiguracionBaseDeDatos() {
        this.propiedades = cargarPropiedades();
    }

    private Properties cargarPropiedades() {
        Properties props = new Properties();
        try (InputStream entrada = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (entrada == null) {
                throw new IllegalStateException(
                        "No se encontro el archivo database.properties en el classpath.");
            }
            props.load(entrada);

        } catch (IOException e) {
            throw new IllegalStateException("Error al leer database.properties", e);
        }
        return props;
    }

    public String getUrl() {
        return propiedades.getProperty("db.url");
    }

    public String getUsuario() {
        return propiedades.getProperty("db.user");
    }

    public String getPassword() {
        return propiedades.getProperty("db.password");
    }

    public int getMaximumPoolSize() {
        return Integer.parseInt(propiedades.getProperty("db.pool.maximumPoolSize", "10"));
    }

    public int getMinimumIdle() {
        return Integer.parseInt(propiedades.getProperty("db.pool.minimumIdle", "2"));
    }

    public long getConnectionTimeoutMs() {
        return Long.parseLong(propiedades.getProperty("db.pool.connectionTimeoutMs", "10000"));
    }

    public long getIdleTimeoutMs() {
        return Long.parseLong(propiedades.getProperty("db.pool.idleTimeoutMs", "300000"));
    }

    public long getMaxLifetimeMs() {
        return Long.parseLong(propiedades.getProperty("db.pool.maxLifetimeMs", "1800000"));
    }
}
