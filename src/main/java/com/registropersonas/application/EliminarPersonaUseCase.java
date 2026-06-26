package com.registropersonas.application;

import com.registropersonas.domain.port.PersonaRepository;

/**
 * Caso de uso: eliminar una persona registrada, identificada por su DNI.
 */
public class EliminarPersonaUseCase {

    private final PersonaRepository repositorio;

    public EliminarPersonaUseCase(PersonaRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * @throws com.registropersonas.domain.PersonaNoEncontradaException si no existe una persona con ese DNI.
     */
    public void ejecutar(String dni) {
        repositorio.eliminarPorDni(dni);
    }
}
