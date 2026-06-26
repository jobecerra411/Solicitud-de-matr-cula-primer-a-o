package com.registropersonas.application;

import com.registropersonas.domain.Persona;
import com.registropersonas.domain.port.GeneradorReportePort;

/**
 * Caso de uso: Imprimir el formulario de solicitud de ingreso de una persona.
 */
public class ImprimirSolicitudUseCase {
    
    private final GeneradorReportePort generadorReporte;

    public ImprimirSolicitudUseCase(GeneradorReportePort generadorReporte) {
        this.generadorReporte = generadorReporte;
    }

    public void ejecutar(Persona persona) {
        generadorReporte.generarReporteSolicitud(persona);
    }
}