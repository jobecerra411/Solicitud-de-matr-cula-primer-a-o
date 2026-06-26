package com.registropersonas.domain.port;

import com.registropersonas.domain.Persona;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (en terminologia de arquitectura hexagonal).
 *
 * Define el contrato que el nucleo de la aplicacion necesita para
 * persistir personas, sin conocer la tecnologia concreta que lo implementa
 * (JDBC, JPA, un archivo, memoria, etc).
 *
 * Principio de Inversion de Dependencias (SOLID - D): el dominio define
 * esta interfaz y los adaptadores de infraestructura la implementan;
 * el dominio nunca depende de la infraestructura.
 */
public interface PersonaRepository {

    /**
     * Persiste una nueva persona.
     * @throws com.registropersonas.domain.PersonaYaExisteException si el DNI ya existe.
     */
    void guardar(Persona persona);

    /**
     * Actualiza los datos de una persona ya existente.
     * @throws com.registropersonas.domain.PersonaNoEncontradaException si el DNI no existe.
     */
    void actualizar(Persona persona);

    /**
     * Elimina una persona por su DNI.
     * @throws com.registropersonas.domain.PersonaNoEncontradaException si el DNI no existe.
     */
    void eliminarPorDni(String dni);

    /**
     * Busca una persona por su DNI.
     */
    Optional<Persona> buscarPorDni(String dni);

    /**
     * Devuelve todas las personas registradas, ordenadas por DNI.
     */
    List<Persona> listarTodas();
}
