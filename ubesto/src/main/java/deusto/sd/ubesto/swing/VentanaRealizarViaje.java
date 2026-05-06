package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.springframework.data.jpa.repository.query.Jpa21Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.ObjectInputFilter.Status;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.ArrayList;

public class VentanaRealizarViaje extends JFrame {
    
    final Color fondoClarito_verde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47,158,68),2,true);
    final Color btnNormalVerde =new Color(79,201,95); // Color verde estilo boceto: Color(100, 200, 100)
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47,158,68),2,true);
    final EmptyBorder paddingBtnAtras =  new EmptyBorder(5, 10, 5, 10);

    public VentanaRealizarViaje(Long idConductor, String email) {
        setTitle("Panel de Conducción");
        setSize(345, 430);
        setLocationRelativeTo(null);
        JPanel pFondo= new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        add(pFondo);
        pFondo.setBackground(fondoClarito_verde);
        setBackground(fondoClarito_verde);

        String[] columnas = {"ID", "Origen", "Destino", "Precio"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tablaViajes = new JTable(modeloTabla);
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        JScrollPane scrollPane = new JScrollPane(tablaViajes);
        JButton btnActualizar = new JButton("Actualizar viajes");
        
        tablaViajes.setBackground(Color.white);
        actualizarDatosTabla(columnas, modeloTabla, tablaViajes, centro, scrollPane);

        pFondo.add(scrollPane);
        scrollPane.setBackground(fondoClarito_verde);

        JPanel panelDatos = new JPanel();
        panelDatos.setBackground(fondoClarito_verde);
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));

        JPanel panel1BtnActualizar = new JPanel();
        panel1BtnActualizar.setBackground(fondoClarito_verde);

        JPanel panel2IDtexto_area = new JPanel();
        panel2IDtexto_area.setBackground(fondoClarito_verde);
        
        JPanel panel3Botones = new JPanel();
        panel3Botones.setBackground(fondoClarito_verde);
        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(Color.white);
        btnVolver.setForeground(btnSalirFont);
        btnVolver.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        
        JButton btnAceptar = new JButton("ACEPTAR Y EMPEZAR");
        btnAceptar.setBackground(btnNormalVerde);
        btnAceptar.setForeground(Color.white);
        btnAceptar.setFont(fontBtnNormal);
        btnAceptar.setBorder(new CompoundBorder(btnNormalBorde, paddingBtnAtras));

        pFondo.add(panelDatos);
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
        panel3Botones.add(btnAceptar);
        
       
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
                    actualizarDatosTabla(columnas, modeloTabla, tablaViajes,centro, scrollPane);

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
        tablaViajes.setPreferredScrollableViewportSize(new Dimension(260,160));
    }

    
}