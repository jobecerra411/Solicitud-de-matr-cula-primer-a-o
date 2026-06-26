package com.registropersonas.infrastructure.persistence;

import com.registropersonas.domain.Persona;
import com.registropersonas.domain.PersonaNoEncontradaException;
import com.registropersonas.domain.PersonaYaExisteException;
import com.registropersonas.domain.PersistenciaException;
import com.registropersonas.domain.port.PersonaRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida (arquitectura hexagonal): implementa el puerto
 * {@link PersonaRepository} usando JDBC puro contra MySQL 8.
 *
 * Todo el SQL esta embebido aqui mediante {@link PreparedStatement} con
 * parametros vinculados (placeholders "?"), nunca concatenando texto del
 * usuario dentro de la sentencia SQL. Esto elimina por diseno el riesgo
 * de inyeccion SQL.
 *
 * Esta es la UNICA clase de todo el proyecto que sabe que existe una
 * base de datos relacional: el dominio y la aplicacion solo conocen
 * la interfaz {@link PersonaRepository}.
 */
public class PersonaRepositoryJdbc implements PersonaRepository {

    private static final String SQL_INSERTAR = """
            INSERT INTO personas
                (dni, apellido_nombre, nacionalidad, edad, domicilio,
                 apellido_nombre_padre, domicilio_padre)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_ACTUALIZAR = """
            UPDATE personas
               SET apellido_nombre = ?,
                   nacionalidad = ?,
                   edad = ?,
                   domicilio = ?,
                   apellido_nombre_padre = ?,
                   domicilio_padre = ?
             WHERE dni = ?
            """;

    private static final String SQL_ELIMINAR = """
            DELETE FROM personas WHERE dni = ?
            """;

    private static final String SQL_BUSCAR_POR_DNI = """
            SELECT dni, apellido_nombre, nacionalidad, edad, domicilio,
                   apellido_nombre_padre, domicilio_padre
              FROM personas
             WHERE dni = ?
            """;

    private static final String SQL_LISTAR_TODAS = """
            SELECT dni, apellido_nombre, nacionalidad, edad, domicilio,
                   apellido_nombre_padre, domicilio_padre
              FROM personas
             ORDER BY dni ASC
            """;

    private final DataSource dataSource;

    public PersonaRepositoryJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void guardar(Persona persona) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_INSERTAR)) {

            statement.setString(1, persona.getDni());
            vincularDatosPersona(statement, persona);

            statement.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new PersonaYaExisteException(persona.getDni());
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Error al guardar la persona con DNI " + persona.getDni(), e);
        }
    }

    @Override
    public void actualizar(Persona persona) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            statement.setString(1, persona.getApellidoNombre());
            statement.setString(2, persona.getNacionalidad());
            statement.setInt(3, persona.getEdad());
            statement.setString(4, persona.getDomicilio());
            statement.setString(5, persona.getApellidoNombrePadre());
            statement.setString(6, persona.getDomicilioPadre());
            statement.setString(7, persona.getDni());

            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas == 0) {
                throw new PersonaNoEncontradaException(persona.getDni());
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Error al actualizar la persona con DNI " + persona.getDni(), e);
        }
    }

    @Override
    public void eliminarPorDni(String dni) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_ELIMINAR)) {

            statement.setString(1, dni);

            int filasEliminadas = statement.executeUpdate();
            if (filasEliminadas == 0) {
                throw new PersonaNoEncontradaException(dni);
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Error al eliminar la persona con DNI " + dni, e);
        }
    }

    @Override
    public Optional<Persona> buscarPorDni(String dni) {
        try (Connection conexion = dataSource.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_BUSCAR_POR_DNI)) {

            statement.setString(1, dni);

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(mapearFila(resultado));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Error al buscar la persona con DNI " + dni, e);
        }
    }

    @Override
    public List<Persona> listarTodas() {
        List<Persona> personas = new ArrayList<>();

        try (Connection conexion = dataSource.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_LISTAR_TODAS);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                personas.add(mapearFila(resultado));
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar las personas", e);
        }

        return personas;
    }

    /**
     * Vincula los campos editables de una persona a un PreparedStatement
     * de insercion, comenzando desde el indice de parametro 2
     * (el indice 1 -DNI- se asigna por separado en {@link #guardar}).
     */
    private void vincularDatosPersona(PreparedStatement statement, Persona persona) throws SQLException {
        statement.setString(2, persona.getApellidoNombre());
        statement.setString(3, persona.getNacionalidad());
        statement.setInt(4, persona.getEdad());
        statement.setString(5, persona.getDomicilio());
        statement.setString(6, persona.getApellidoNombrePadre());
        statement.setString(7, persona.getDomicilioPadre());
    }

    private Persona mapearFila(ResultSet resultado) throws SQLException {
        return new Persona(
                resultado.getString("dni"),
                resultado.getString("apellido_nombre"),
                resultado.getString("nacionalidad"),
                resultado.getInt("edad"),
                resultado.getString("domicilio"),
                resultado.getString("apellido_nombre_padre"),
                resultado.getString("domicilio_padre")
        );
    }
}
