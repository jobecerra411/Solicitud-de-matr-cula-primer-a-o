package com.registropersonas.infrastructure.ui;

import com.registropersonas.domain.Persona;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de tabla Swing dedicado a mostrar una lista de {@link Persona}.
 *
 * Responsabilidad unica (SOLID - S): solo se ocupa de adaptar la lista
 * de personas al contrato que espera un JTable.
 */
public class ModeloTablaPersonas extends AbstractTableModel {

    private static final String[] COLUMNAS = {
            "DNI", "Apellido y Nombre", "Nacionalidad", "Edad",
            "Domicilio", "Apellido y Nombre del padre", "Domicilio del padre"
    };

    private List<Persona> personas = new ArrayList<>();

    public void actualizarDatos(List<Persona> nuevasPersonas) {
        this.personas = new ArrayList<>(nuevasPersonas);
        fireTableDataChanged();
    }

    public Persona getPersonaEnFila(int fila) {
        return personas.get(fila);
    }

    @Override
    public int getRowCount() {
        return personas.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Persona persona = personas.get(fila);
        return switch (columna) {
            case 0 -> persona.getDni();
            case 1 -> persona.getApellidoNombre();
            case 2 -> persona.getNacionalidad();
            case 3 -> persona.getEdad();
            case 4 -> persona.getDomicilio();
            case 5 -> persona.getApellidoNombrePadre();
            case 6 -> persona.getDomicilioPadre();
            default -> throw new IllegalArgumentException("Columna invalida: " + columna);
        };
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }
}
