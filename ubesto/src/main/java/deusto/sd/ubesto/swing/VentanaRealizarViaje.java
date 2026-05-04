package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.ObjectInputFilter.Status;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.ArrayList;

public class VentanaRealizarViaje extends JFrame {
    
    public VentanaRealizarViaje(Long idConductor, String email) {
        setTitle("Panel de Conducción");
        setSize(345, 440);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        String[] columnas = {"ID", "Origen", "Destino", "Precio"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas,0);
        JTable tablaViajes = new JTable(modeloTabla);
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        JScrollPane scrollPane = new JScrollPane(tablaViajes);
        JButton btnActualizar = new JButton("Actualizar viajes");
        
        actualizarDatosTabla(columnas, modeloTabla, tablaViajes,centro, scrollPane);

        add(scrollPane);

        JPanel panelDatos = new JPanel();
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));

        JPanel panel1BtnActualizar = new JPanel();
        JPanel panel2IDtexto_area = new JPanel();
        JPanel panel3Botones = new JPanel();
        JButton btnVolver = new JButton("Volver");
        JButton btnAceptar = new JButton("ACEPTAR Y EMPEZAR");

        add(panelDatos);
        panelDatos.add(panel1BtnActualizar);
        panelDatos.add(Box.createVerticalStrut(10));
        panelDatos.add(panel2IDtexto_area);
        panelDatos.add(Box.createVerticalStrut(10));
        panelDatos.add(panel3Botones);

        panel1BtnActualizar.add(btnActualizar);

        panel2IDtexto_area.add(new JLabel("ID del Viaje solicitado:"));
        JTextField txtTripId =new JTextField(6);
        txtTripId.setEditable(false);
        panel2IDtexto_area.add(txtTripId);
        
        panel3Botones.add(btnVolver);
        panel3Botones. add(btnAceptar);
        
        btnAceptar.setBackground(new Color(100, 200, 100));
       
        // EVENTO: VOLVER
        btnVolver.addActionListener(e -> {
            new DashboardFrame("CONDUCTOR", email, idConductor).setVisible(true);
            dispose();
        });

        // EVENTO: ACEPTAR VIAJE
        btnAceptar.addActionListener(e -> {
            try {
                String tripId = txtTripId.getText();
                String url = "http://localhost:8080/trips/" + tripId + "/accept/" + idConductor;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody()).build();

                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

                if (res.statusCode() == 200) {
                    JOptionPane.showMessageDialog(this, "¡Viaje iniciado! La simulación está corriendo en el servidor.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo aceptar el viaje: " + res.body());
                }
            } catch (Exception ex) { 
                ex.printStackTrace(); 
                JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnActualizar.addActionListener(e ->{
            try {
                actualizarDatosTabla(columnas, modeloTabla, tablaViajes, centro, scrollPane);

            } catch (Exception ex) {
            }
        });

        tablaViajes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaViajes.getSelectedRow();
                
                if (filaSeleccionada != -1) { // -1 significa que no hay nada seleccionado
                    Object id_seleccionado = tablaViajes.getValueAt(filaSeleccionada, 0);
                    txtTripId.setText(String.valueOf(id_seleccionado));
                    // Aquí puedes abrir otra ventana, mostrar un mensaje, etc.
                }
            }
        });
    }

    public List<String> recargaInfo(){
        try {
            HttpClient client1 = HttpClient.newHttpClient();
            HttpRequest request1 = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/trips/getAllTrips"))
            .header("Accept", "application/json")
            .GET()
            .build();

            HttpResponse<String> response = client1.send(request1, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                // Esto traduce el JSON ["a", "b"] a List<String>
                return mapper.readValue(response.body(), new TypeReference<List<String>>() {});
            } else {
                System.err.println("Error del servidor: " + response.statusCode());
                return null;
            }
        } catch (Exception ex) {
            // TODO: handle exception
            return null;
        }
    }

    public void actualizarDatosTabla(String[] columnas, DefaultTableModel modeloTabla,
        JTable tablaViajes, DefaultTableCellRenderer centro, JScrollPane scrollPane){
        
        centro.setHorizontalAlignment(JLabel.CENTER);
        modeloTabla.setRowCount(0);
        List<String> viajes_bbdd = recargaInfo();
        for(String s1: viajes_bbdd){
            String[] row = s1.split("__");
            modeloTabla.addRow(row);
        }
        for(int i=0; i<tablaViajes.getColumnCount(); i++){
            tablaViajes.getColumnModel().getColumn(i).setCellRenderer(centro);
        }
        tablaViajes.setPreferredScrollableViewportSize(new Dimension(250,160));
    }

    
}