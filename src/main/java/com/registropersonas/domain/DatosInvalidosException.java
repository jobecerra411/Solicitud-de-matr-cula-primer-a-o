package com.registropersonas.domain;

/**
 * Excepcion lanzada cuando los datos de una {@link Persona} no cumplen
 * las reglas de negocio (campos obligatorios, formato de DNI, rango de edad, etc).
 */
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
