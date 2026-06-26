package com.registropersonas.application;

import com.registropersonas.domain.Persona;
import com.registropersonas.domain.PersonaNoEncontradaException;
import com.registropersonas.domain.port.PersonaRepository;

/**
 * Caso de uso: editar los datos de una persona ya registrada.
 * El DNI no es editable porque es la clave primaria de la tabla.
 */
public class EditarPersonaUseCase {

    private final PersonaRepository repositorio;

    public EditarPersonaUseCase(PersonaRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * @throws PersonaNoEncontradaException si no existe una persona con ese DNI.
     * @throws com.registropersonas.domain.DatosInvalidosException si los nuevos datos no son validos.
     */
    public void ejecutar(String dni,
                          String apellidoNombre,
                          String nacionalidad,
                          int edad,
                          String domicilio,
                          String apellidoNombrePadre,
                          String domicilioPadre) {

        Persona existente = repositorio.buscarPorDni(dni)
                .orElseThrow(() -> new PersonaNoEncontradaException(dni));

        Persona actualizada = existente.conDatosActualizados(
                apellidoNombre, nacionalidad, edad, domicilio,
                apellidoNombrePadre, domicilioPadre);

        repositorio.actualizar(actualizada);
    }
}
