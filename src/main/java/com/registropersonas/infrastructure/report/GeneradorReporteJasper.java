package com.registropersonas.infrastructure.report;

import com.registropersonas.domain.Persona;
import com.registropersonas.domain.PersistenciaException;
import com.registropersonas.domain.port.GeneradorReportePort;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;

/**
 * Adaptador de salida que implementa la generación de reportes usando JasperReports.
 */
public class GeneradorReporteJasper implements GeneradorReportePort {

    @Override
    public void generarReporteSolicitud(Persona persona) {
        try {
            // Se carga el reporte compilado (.jasper) generado por el plugin de Maven
            InputStream reporteStream = getClass().getResourceAsStream("/jasper/solicitud_ingreso.jasper");
            
            if (reporteStream == null) {
                throw new IllegalStateException("No se encontró el archivo compilado solicitud_ingreso.jasper");
            }

            // Usamos una colección de un solo elemento (la persona actual) como DataSource
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(persona));
            
            // Llenamos el reporte. No pasamos parámetros extra, por eso el HashMap está vacío.
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, new HashMap<>(), dataSource);
            
            // Mostramos el visor de Jasper (false para que no cierre toda la app al cerrar el visor)
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            // Reutilizamos la excepción de infraestructura existente o podríamos crear una ReporteException
            throw new PersistenciaException("Error al generar el reporte con JasperReports", e);
        }
    }
}