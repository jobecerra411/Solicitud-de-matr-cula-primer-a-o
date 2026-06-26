package com.registropersonas.domain;

/**
 * Excepcion lanzada cuando se busca, edita o elimina una persona
 * cuyo DNI no esta registrado.
 */
public class PersonaNoEncontradaException extends RuntimeException {

    public PersonaNoEncontradaException(String dni) {
        super("No existe ninguna persona registrada con el DNI: " + dni);
    }
}
