package com.registropersonas.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Crea y administra el ciclo de vida del pool de conexiones HikariCP.
 *
 * Es la unica clase de la aplicacion que conoce los detalles de HikariCP;
 * el resto de la infraestructura solo recibe un {@link javax.sql.DataSource}.
 */
public final class FabricaDataSource implements AutoCloseable {

    private final HikariDataSource dataSource;

    public FabricaDataSource(ConfiguracionBaseDeDatos configuracion) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(configuracion.getUrl());
        config.setUsername(configuracion.getUsuario());
        config.setPassword(configuracion.getPassword());
        config.setMaximumPoolSize(configuracion.getMaximumPoolSize());
        config.setMinimumIdle(configuracion.getMinimumIdle());
        config.setConnectionTimeout(configuracion.getConnectionTimeoutMs());
        config.setIdleTimeout(configuracion.getIdleTimeoutMs());
        config.setMaxLifetime(configuracion.getMaxLifetimeMs());
        config.setPoolName("registro-personas-pool");

        this.dataSource = new HikariDataSource(config);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
