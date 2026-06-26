package com.registropersonas.domain;

/**
 * Excepcion que envuelve cualquier fallo de la capa de persistencia
 * (errores de conexion, SQL, etc.) para que el dominio y la aplicacion
 * no dependan de tipos especificos de JDBC (p.ej. SQLException).
 *
 * Esto respeta la regla de la arquitectura hexagonal: las capas internas
 * solo conocen excepciones propias, nunca detalles de la tecnologia
 * usada en los adaptadores de salida.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
