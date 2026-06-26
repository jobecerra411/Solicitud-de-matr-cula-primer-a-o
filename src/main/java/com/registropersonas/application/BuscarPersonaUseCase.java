package com.registropersonas.application;

import com.registropersonas.domain.Persona;
import com.registropersonas.domain.port.PersonaRepository;

import java.util.Optional;

/**
 * Caso de uso: buscar una persona puntual por su DNI.
 */
public class BuscarPersonaUseCase {

    private final PersonaRepository repositorio;

    public BuscarPersonaUseCase(PersonaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Optional<Persona> ejecutar(String dni) {
        return repositorio.buscarPorDni(dni);
    }
}
