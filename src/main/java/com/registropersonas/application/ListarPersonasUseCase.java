package com.registropersonas.application;

import com.registropersonas.domain.Persona;
import com.registropersonas.domain.port.PersonaRepository;

import java.util.List;

/**
 * Caso de uso: obtener el listado completo de personas registradas.
 */
public class ListarPersonasUseCase {

    private final PersonaRepository repositorio;

    public ListarPersonasUseCase(PersonaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Persona> ejecutar() {
        return repositorio.listarTodas();
    }
}
