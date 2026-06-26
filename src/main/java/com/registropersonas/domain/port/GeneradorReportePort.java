package com.registropersonas.domain.port;

import com.registropersonas.domain.Persona;

/**
 * Puerto de salida para la generación de reportes.
 * El dominio define que se puede imprimir una persona, pero no sabe 
 * que se utiliza JasperReports ni Swing.
 */
public interface GeneradorReportePort {
    void generarReporteSolicitud(Persona persona);
}