package com.registropersonas.application;

import com.registropersonas.domain.Persona;
import com.registropersonas.domain.PersonaYaExisteException;
import com.registropersonas.domain.port.PersonaRepository;

/**
 * Caso de uso: registrar una nueva persona.
 *
 * Responsabilidad unica (SOLID - S): solo coordina la creacion,
 * delegando la validacion de datos a la entidad {@link Persona}
 * y la persistencia al puerto {@link PersonaRepository}.
 */
public class AgregarPersonaUseCase {

    private final PersonaRepository repositorio;

    public AgregarPersonaUseCase(PersonaRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Ejecuta el caso de uso.
     *
     * @throws com.registropersonas.domain.DatosInvalidosException si los datos no son validos.
     * @throws PersonaYaExisteException si ya existe una persona con ese DNI.
     */
    public void ejecutar(String dni,
                          String apellidoNombre,
                          String nacionalidad,
                          int edad,
                          String domicilio,
                          String apellidoNombrePadre,
                          String domicilioPadre) {

        if (repositorio.buscarPorDni(dni).isPresent()) {
            throw new PersonaYaExisteException(dni);
        }

        Persona persona = new Persona(dni, apellidoNombre, nacionalidad, edad,
                domicilio, apellidoNombrePadre, domicilioPadre);

        repositorio.guardar(persona);
    }
}
