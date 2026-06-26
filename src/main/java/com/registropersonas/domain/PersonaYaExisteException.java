package com.registropersonas.domain;

/**
 * Excepcion lanzada al intentar registrar una persona cuyo DNI ya existe.
 */
public class PersonaYaExisteException extends RuntimeException {

    public PersonaYaExisteException(String dni) {
        super("Ya existe una persona registrada con el DNI: " + dni);
    }
}
