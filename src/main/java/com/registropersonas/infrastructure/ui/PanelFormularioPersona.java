package com.registropersonas.infrastructure.ui;

import com.registropersonas.domain.Persona;

import javax.swing.*;
import java.awt.*;

/**
 * Panel reutilizable con los campos de captura de datos de una persona.
 *
 * Responsabilidad unica (SOLID - S): solo se ocupa de mostrar y recolectar
 * los datos del formulario; no sabe nada de base de datos ni de la ventana
 * que lo contiene.
 */
public class PanelFormularioPersona extends JPanel {

    private final JTextField campoDni = new JTextField(15);
    private final JTextField campoApellidoNombre = new JTextField(25);
    private final JTextField campoNacionalidad = new JTextField(20);
    private final JSpinner campoEdad = new JSpinner(new SpinnerNumberModel(0, 0, 130, 1));
    private final JTextField campoDomicilio = new JTextField(30);
    private final JTextField campoApellidoNombrePadre = new JTextField(25);
    private final JTextField campoDomicilioPadre = new JTextField(30);

    public PanelFormularioPersona() {
        setLayout(new GridBagLayout());
        construirFormulario();
    }

    private void construirFormulario() {
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.insets = new Insets(4, 4, 4, 4);
        restricciones.anchor = GridBagConstraints.WEST;

        agregarFila(restricciones, 0, "DNI:", campoDni);
        agregarFila(restricciones, 1, "Apellido y Nombre:", campoApellidoNombre);
        agregarFila(restricciones, 2, "Nacionalidad:", campoNacionalidad);
        agregarFila(restricciones, 3, "Edad:", campoEdad);
        agregarFila(restricciones, 4, "Domicilio:", campoDomicilio);
        agregarFila(restricciones, 5, "Apellido y Nombre del padre:", campoApellidoNombrePadre);
        agregarFila(restricciones, 6, "Domicilio del padre:", campoDomicilioPadre);
    }

    private void agregarFila(GridBagConstraints restricciones, int fila, String etiqueta, JComponent campo) {
        restricciones.gridx = 0;
        restricciones.gridy = fila;
        restricciones.fill = GridBagConstraints.NONE;
        add(new JLabel(etiqueta), restricciones);

        restricciones.gridx = 1;
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        add(campo, restricciones);
    }

    public void limpiar() {
        campoDni.setText("");
        campoApellidoNombre.setText("");
        campoNacionalidad.setText("");
        campoEdad.setValue(0);
        campoDomicilio.setText("");
        campoApellidoNombrePadre.setText("");
        campoDomicilioPadre.setText("");
        campoDni.setEnabled(true);
    }

    public void cargarDatosDe(Persona persona) {
        campoDni.setText(persona.getDni());
        campoApellidoNombre.setText(persona.getApellidoNombre());
        campoNacionalidad.setText(persona.getNacionalidad());
        campoEdad.setValue(persona.getEdad());
        campoDomicilio.setText(persona.getDomicilio());
        campoApellidoNombrePadre.setText(persona.getApellidoNombrePadre());
        campoDomicilioPadre.setText(persona.getDomicilioPadre());
        // El DNI es la clave primaria: no debe modificarse durante una edicion.
        campoDni.setEnabled(false);
    }

    public void habilitarEdicionDni(boolean habilitado) {
        campoDni.setEnabled(habilitado);
    }

    public String getDni() {
        return campoDni.getText().trim();
    }

    public String getApellidoNombre() {
        return campoApellidoNombre.getText().trim();
    }

    public String getNacionalidad() {
        return campoNacionalidad.getText().trim();
    }

    public int getEdad() {
        return (int) campoEdad.getValue();
    }

    public String getDomicilio() {
        return campoDomicilio.getText().trim();
    }

    public String getApellidoNombrePadre() {
        return campoApellidoNombrePadre.getText().trim();
    }

    public String getDomicilioPadre() {
        return campoDomicilioPadre.getText().trim();
    }
}
