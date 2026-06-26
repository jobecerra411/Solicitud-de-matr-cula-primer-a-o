package com.registropersonas.infrastructure.ui;

import com.registropersonas.application.AgregarPersonaUseCase;
import com.registropersonas.application.EditarPersonaUseCase;
import com.registropersonas.application.EliminarPersonaUseCase;
import com.registropersonas.application.ListarPersonasUseCase;
import com.registropersonas.application.ImprimirSolicitudUseCase;
import com.registropersonas.domain.port.PersonaRepository;
import com.registropersonas.domain.port.GeneradorReportePort;
import com.registropersonas.infrastructure.config.ConfiguracionBaseDeDatos;
import com.registropersonas.infrastructure.config.FabricaDataSource;
import com.registropersonas.infrastructure.persistence.PersonaRepositoryJdbc;
import com.registropersonas.infrastructure.report.GeneradorReporteJasper;

import javax.swing.*;

/**
 * Punto de entrada de la aplicacion.
 *
 * Esta clase actua como "composition root": es la unica responsable de
 * instanciar y conectar entre si todas las piezas de las distintas capas
 * (infraestructura, aplicacion, dominio).
 */
public final class AplicacionPrincipal {

    private AplicacionPrincipal() {
        // Clase utilitaria, no instanciable.
    }

    public static void main(String[] args) {

        ConfiguracionBaseDeDatos configuracion = new ConfiguracionBaseDeDatos();
        FabricaDataSource fabricaDataSource = new FabricaDataSource(configuracion);

        // Instanciar Puertos / Adaptadores
        PersonaRepository personaRepository = new PersonaRepositoryJdbc(fabricaDataSource.getDataSource());
        GeneradorReportePort generadorReporte = new GeneradorReporteJasper();

        // Instanciar Casos de Uso
        AgregarPersonaUseCase agregarPersonaUseCase = new AgregarPersonaUseCase(personaRepository);
        EditarPersonaUseCase editarPersonaUseCase = new EditarPersonaUseCase(personaRepository);
        EliminarPersonaUseCase eliminarPersonaUseCase = new EliminarPersonaUseCase(personaRepository);
        ListarPersonasUseCase listarPersonasUseCase = new ListarPersonasUseCase(personaRepository);
        ImprimirSolicitudUseCase imprimirSolicitudUseCase = new ImprimirSolicitudUseCase(generadorReporte);

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(
                    agregarPersonaUseCase,
                    editarPersonaUseCase,
                    eliminarPersonaUseCase,
                    listarPersonasUseCase,
                    imprimirSolicitudUseCase, // Inyectamos el nuevo caso de uso
                    fabricaDataSource::close
            );
            ventana.setVisible(true);
        });
    }
}