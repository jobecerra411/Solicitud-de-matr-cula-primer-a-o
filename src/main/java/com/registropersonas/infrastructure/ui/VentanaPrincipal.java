package com.registropersonas.infrastructure.ui;

import com.registropersonas.application.AgregarPersonaUseCase;
import com.registropersonas.application.EditarPersonaUseCase;
import com.registropersonas.application.EliminarPersonaUseCase;
import com.registropersonas.application.ListarPersonasUseCase;
import com.registropersonas.application.ImprimirSolicitudUseCase;
import com.registropersonas.domain.DatosInvalidosException;
import com.registropersonas.domain.Persona;
import com.registropersonas.domain.PersonaNoEncontradaException;
import com.registropersonas.domain.PersonaYaExisteException;
import com.registropersonas.domain.PersistenciaException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Adaptador de entrada (arquitectura hexagonal): interfaz grafica Swing
 * que permite al usuario interactuar con los casos de uso de la aplicacion.
 */
public class VentanaPrincipal extends JFrame {

    private final AgregarPersonaUseCase agregarPersonaUseCase;
    private final EditarPersonaUseCase editarPersonaUseCase;
    private final EliminarPersonaUseCase eliminarPersonaUseCase;
    private final ListarPersonasUseCase listarPersonasUseCase;
    private final ImprimirSolicitudUseCase imprimirSolicitudUseCase;
    private final Runnable accionAlCerrar;

    private final ModeloTablaPersonas modeloTabla = new ModeloTablaPersonas();
    private final JTable tablaPersonas = new JTable(modeloTabla);
    private final PanelFormularioPersona panelFormulario = new PanelFormularioPersona();

    private final JButton botonAgregar = new JButton("Agregar");
    private final JButton botonGuardarEdicion = new JButton("Guardar cambios");
    private final JButton botonCancelarEdicion = new JButton("Cancelar edicion");
    private final JButton botonEditar = new JButton("Editar seleccionada");
    private final JButton botonEliminar = new JButton("Eliminar seleccionada");
    private final JButton botonRefrescar = new JButton("Refrescar");
    private final JButton botonImprimir = new JButton("Imprimir Solicitud"); // Nuevo Botón

    private boolean editandoPersonaExistente = false;

    public VentanaPrincipal(AgregarPersonaUseCase agregarPersonaUseCase,
                             EditarPersonaUseCase editarPersonaUseCase,
                             EliminarPersonaUseCase eliminarPersonaUseCase,
                             ListarPersonasUseCase listarPersonasUseCase,
                             ImprimirSolicitudUseCase imprimirSolicitudUseCase,
                             Runnable accionAlCerrar) {

        super("Registro de Personas");
        this.agregarPersonaUseCase = agregarPersonaUseCase;
        this.editarPersonaUseCase = editarPersonaUseCase;
        this.eliminarPersonaUseCase = eliminarPersonaUseCase;
        this.listarPersonasUseCase = listarPersonasUseCase;
        this.imprimirSolicitudUseCase = imprimirSolicitudUseCase;
        this.accionAlCerrar = accionAlCerrar;

        construirInterfaz();
        configurarEventos();
        actualizarEstadoBotonesParaModoAlta();
        cargarPersonasDesdeBaseDeDatos();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        tablaPersonas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabla = new JScrollPane(tablaPersonas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Personas registradas"));

        JPanel panelFormularioConBorde = new JPanel(new BorderLayout());
        panelFormularioConBorde.setBorder(BorderFactory.createTitledBorder("Datos de la persona"));
        panelFormularioConBorde.add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotonesFormulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotonesFormulario.add(botonAgregar);
        panelBotonesFormulario.add(botonGuardarEdicion);
        panelBotonesFormulario.add(botonCancelarEdicion);
        panelFormularioConBorde.add(panelBotonesFormulario, BorderLayout.SOUTH);

        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotonesTabla.add(botonEditar);
        panelBotonesTabla.add(botonEliminar);
        panelBotonesTabla.add(botonRefrescar);
        panelBotonesTabla.add(botonImprimir); // Se agrega a la barra superior

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(scrollTabla, BorderLayout.CENTER);
        panelSuperior.add(panelBotonesTabla, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.CENTER);
        add(panelFormularioConBorde, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        botonAgregar.addActionListener(e -> agregarPersona());
        botonGuardarEdicion.addActionListener(e -> guardarEdicion());
        botonCancelarEdicion.addActionListener(e -> cancelarEdicion());
        botonEditar.addActionListener(e -> iniciarEdicionDeSeleccionada());
        botonEliminar.addActionListener(e -> eliminarSeleccionada());
        botonRefrescar.addActionListener(e -> cargarPersonasDesdeBaseDeDatos());
        botonImprimir.addActionListener(e -> imprimirSeleccionada()); // Evento nuevo

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                accionAlCerrar.run();
                dispose();
            }
        });
    }

    private void cargarPersonasDesdeBaseDeDatos() {
        try {
            modeloTabla.actualizarDatos(listarPersonasUseCase.ejecutar());
        } catch (PersistenciaException e) {
            mostrarError("No se pudo cargar el listado de personas.", e);
        }
    }

    private void agregarPersona() {
        try {
            agregarPersonaUseCase.ejecutar(
                    panelFormulario.getDni(),
                    panelFormulario.getApellidoNombre(),
                    panelFormulario.getNacionalidad(),
                    panelFormulario.getEdad(),
                    panelFormulario.getDomicilio(),
                    panelFormulario.getApellidoNombrePadre(),
                    panelFormulario.getDomicilioPadre()
            );

            JOptionPane.showMessageDialog(this, "Persona agregada correctamente.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            panelFormulario.limpiar();
            cargarPersonasDesdeBaseDeDatos();

        } catch (DatosInvalidosException | PersonaYaExisteException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (PersistenciaException e) {
            mostrarError("No se pudo agregar la persona.", e);
        }
    }

    private void iniciarEdicionDeSeleccionada() {
        int filaSeleccionada = tablaPersonas.getSelectedRow();
        if (filaSeleccionada < 0) {
            mostrarAdvertencia("Seleccione una persona de la tabla para editar.");
            return;
        }

        Persona persona = modeloTabla.getPersonaEnFila(filaSeleccionada);
        panelFormulario.cargarDatosDe(persona);
        editandoPersonaExistente = true;
        actualizarEstadoBotonesParaModoEdicion();
    }

    private void guardarEdicion() {
        try {
            editarPersonaUseCase.ejecutar(
                    panelFormulario.getDni(),
                    panelFormulario.getApellidoNombre(),
                    panelFormulario.getNacionalidad(),
                    panelFormulario.getEdad(),
                    panelFormulario.getDomicilio(),
                    panelFormulario.getApellidoNombrePadre(),
                    panelFormulario.getDomicilioPadre()
            );

            JOptionPane.showMessageDialog(this, "Persona actualizada correctamente.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            cancelarEdicion();
            cargarPersonasDesdeBaseDeDatos();

        } catch (DatosInvalidosException | PersonaNoEncontradaException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (PersistenciaException e) {
            mostrarError("No se pudo actualizar la persona.", e);
        }
    }

    private void cancelarEdicion() {
        panelFormulario.limpiar();
        editandoPersonaExistente = false;
        actualizarEstadoBotonesParaModoAlta();
    }

    private void eliminarSeleccionada() {
        int filaSeleccionada = tablaPersonas.getSelectedRow();
        if (filaSeleccionada < 0) {
            mostrarAdvertencia("Seleccione una persona de la tabla para eliminar.");
            return;
        }

        Persona persona = modeloTabla.getPersonaEnFila(filaSeleccionada);
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Confirma que desea eliminar a " + persona.getApellidoNombre()
                        + " (DNI " + persona.getDni() + ")?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            eliminarPersonaUseCase.ejecutar(persona.getDni());
            cargarPersonasDesdeBaseDeDatos();

        } catch (PersonaNoEncontradaException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (PersistenciaException e) {
            mostrarError("No se pudo eliminar la persona.", e);
        }
    }

    // Nuevo método para manejar el evento de impresión
    private void imprimirSeleccionada() {
        int filaSeleccionada = tablaPersonas.getSelectedRow();
        if (filaSeleccionada < 0) {
            mostrarAdvertencia("Seleccione una persona de la tabla para imprimir su solicitud.");
            return;
        }
        
        try {
            Persona persona = modeloTabla.getPersonaEnFila(filaSeleccionada);
            imprimirSolicitudUseCase.ejecutar(persona);
        } catch (Exception e) {
            mostrarError("Ocurrió un error al intentar generar el reporte.", e);
        }
    }

    private void actualizarEstadoBotonesParaModoAlta() {
        botonAgregar.setEnabled(true);
        botonGuardarEdicion.setEnabled(false);
        botonCancelarEdicion.setEnabled(false);
        botonEditar.setEnabled(true);
        botonEliminar.setEnabled(true);
        botonImprimir.setEnabled(true);
        panelFormulario.habilitarEdicionDni(true);
    }

    private void actualizarEstadoBotonesParaModoEdicion() {
        botonAgregar.setEnabled(false);
        botonGuardarEdicion.setEnabled(true);
        botonCancelarEdicion.setEnabled(true);
        botonEditar.setEnabled(false);
        botonEliminar.setEnabled(false);
        botonImprimir.setEnabled(false);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atencion", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje, Exception excepcion) {
        JOptionPane.showMessageDialog(this,
                mensaje + "\nDetalle: " + excepcion.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}