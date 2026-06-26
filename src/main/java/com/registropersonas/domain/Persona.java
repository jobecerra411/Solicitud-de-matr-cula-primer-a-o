package com.registropersonas.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Entidad de dominio que representa una persona registrada.
 *
 * Es un objeto inmutable: cualquier "modificacion" se hace creando
 * una nueva instancia (metodo {@link #conDatosActualizados}).
 *
 * Esta clase no conoce nada de bases de datos, JDBC ni interfaces grafica;
 * pertenece exclusivamente al nucleo de dominio (regla de la arquitectura
 * hexagonal: el dominio no depende de capas externas).
 */
public final class Persona {

    private static final Pattern PATRON_DNI = Pattern.compile("^[0-9]{6,9}$");

    private final String dni;
    private final String apellidoNombre;
    private final String nacionalidad;
    private final int edad;
    private final String domicilio;
    private final String apellidoNombrePadre;
    private final String domicilioPadre;

    public Persona(String dni,
                    String apellidoNombre,
                    String nacionalidad,
                    int edad,
                    String domicilio,
                    String apellidoNombrePadre,
                    String domicilioPadre) {

        this.dni = validarDni(dni);
        this.apellidoNombre = validarTextoObligatorio(apellidoNombre, "Apellido y Nombre");
        this.nacionalidad = validarTextoObligatorio(nacionalidad, "Nacionalidad");
        this.edad = validarEdad(edad);
        this.domicilio = validarTextoObligatorio(domicilio, "Domicilio");
        this.apellidoNombrePadre = validarTextoObligatorio(apellidoNombrePadre, "Apellido y Nombre del padre");
        this.domicilioPadre = validarTextoObligatorio(domicilioPadre, "Domicilio del padre");
    }

    /**
     * Crea una nueva instancia con los mismos datos pero los campos editables
     * actualizados. El DNI no puede modificarse porque es la clave primaria.
     */
    public Persona conDatosActualizados(String apellidoNombre,
                                         String nacionalidad,
                                         int edad,
                                         String domicilio,
                                         String apellidoNombrePadre,
                                         String domicilioPadre) {
        return new Persona(this.dni, apellidoNombre, nacionalidad, edad,
                domicilio, apellidoNombrePadre, domicilioPadre);
    }

    private static String validarDni(String dni) {
        if (dni == null || !PATRON_DNI.matcher(dni.trim()).matches()) {
            throw new DatosInvalidosException(
                    "El DNI debe contener entre 6 y 9 digitos numericos.");
        }
        return dni.trim();
    }

    private static String validarTextoObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DatosInvalidosException("El campo '" + nombreCampo + "' es obligatorio.");
        }
        if (valor.trim().length() > 200) {
            throw new DatosInvalidosException("El campo '" + nombreCampo + "' es demasiado largo.");
        }
        return valor.trim();
    }

    private static int validarEdad(int edad) {
        if (edad < 0 || edad > 130) {
            throw new DatosInvalidosException("La edad debe estar entre 0 y 130 anios.");
        }
        return edad;
    }

    public String getDni() {
        return dni;
    }

    public String getApellidoNombre() {
        return apellidoNombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public int getEdad() {
        return edad;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public String getApellidoNombrePadre() {
        return apellidoNombrePadre;
    }

    public String getDomicilioPadre() {
        return domicilioPadre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona other)) return false;
        return dni.equals(other.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

    @Override
    public String toString() {
        return "Persona{dni='%s', apellidoNombre='%s'}".formatted(dni, apellidoNombre);
    }
}
