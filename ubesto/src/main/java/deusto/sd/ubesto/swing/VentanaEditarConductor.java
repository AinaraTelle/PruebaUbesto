package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.net.URI;
import java.net.http.*;

public class VentanaEditarConductor extends JFrame {
    final Color fondoClarito_verde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47,158,68),2,true);
    final Color btnNormalVerde =new Color(79,201,95); // Color verde estilo boceto: Color(100, 200, 100)
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47,158,68),2,true);
    final EmptyBorder paddingBtnAtras =  new EmptyBorder(10, 15, 10, 15);

    public VentanaEditarConductor(String emailActual, Long idConductor) {
        setTitle("Editar Perfil de Conductor");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel pFondo = new JPanel();
        pFondo.setLayout(new BoxLayout(pFondo,BoxLayout.Y_AXIS));
        add(pFondo);
        pFondo.setBackground(fondoClarito_verde);

        // setLayout(new GridLayout(0, 1, 0, 10));

        JPanel panelDatos = new JPanel();
        panelDatos.setBackground(fondoClarito_verde);
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));
        

        JPanel panelForm1 = new JPanel(new GridLayout(3, 2, 10, 15));
        panelForm1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelForm1.setBackground(fondoClarito_verde);

        // Campos a editar
        JTextField txtNombre = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtLicencia = new JTextField();

        JLabel nom=new JLabel("Nuevo Nombre:");
        nom.setOpaque(true);
        panelForm1.add(nom); 
        panelForm1.add(txtNombre);
        
        JLabel con =new JLabel("Nuevo Contraseña:");
        con.setOpaque(true);
        panelForm1.add(con); 
        panelForm1.add(txtPass);
        
        JLabel lic =new JLabel("Nueva Licencia:");
        lic.setOpaque(true);
        panelForm1.add(lic); 
        panelForm1.add(txtLicencia);

        nom.setBackground(fondoClarito_verde);
        con.setBackground(fondoClarito_verde);
        lic.setBackground(fondoClarito_verde);
        

        JSeparator separador = new JSeparator();
        separador.setOrientation(SwingConstants.HORIZONTAL);


        JPanel panelForm2 = new JPanel(new GridLayout(1, 3, 10, 15));
        panelForm2.setBackground(fondoClarito_verde);
        panelForm2.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        JLabel veh =new JLabel("Cambio Vehiculo (indica la matricula de un vehiulo tuyo existente):");
        veh.setOpaque(true);
        veh.setBackground(fondoClarito_verde);

        panelForm2.add(veh);
        JTextField txtMatricula = new JTextField(); 
        panelForm2.add(txtMatricula);
        panelForm2.setBackground(fondoClarito_verde);
        

        JButton btnCambiarVehiculo = new JButton("Cambiar");
        panelForm2.add(btnCambiarVehiculo);
        btnCambiarVehiculo.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnCambiarVehiculo.setBackground(btnNormalVerde);
        btnCambiarVehiculo.setForeground(Color.white);
        btnCambiarVehiculo.setFont(fontBtnNormal);
        
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(fondoClarito_verde);

        // Botones
        JButton btnVolver = new JButton("Volver");
        btnVolver.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnVolver.setForeground(btnSalirFont);
        btnVolver.setBackground(Color.white);
        
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnGuardar.setBackground(btnNormalVerde);
        btnGuardar.setForeground(Color.white);
        btnGuardar.setFont(fontBtnNormal);

        panelBotones.add(btnVolver); 
        panelBotones.add(btnGuardar);

        panelDatos.add(panelForm1);
        panelDatos.add(separador);
        panelDatos.add(panelForm2);
        panelDatos.add(panelBotones);
        
        pFondo.add(panelDatos, BorderLayout.CENTER);

        // EVENTOS
        btnVolver.addActionListener(e -> {
            new DashboardFrame("CONDUCTOR", emailActual, idConductor).setVisible(true);
            dispose();
        });

        btnGuardar.addActionListener(e -> {
            try {
                String url = "http://localhost:8080/drivers/update/" + idConductor; 
                
                String jsonBody = String.format(
                    "{\"nombre\":\"%s\", \"password\":\"%s\", \"licenciaConducir\":\"%s\"}", 
                    txtNombre.getText(), new String(txtPass.getPassword()), txtLicencia.getText()
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody)) 
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 || response.statusCode() == 204) {
                    JOptionPane.showMessageDialog(this, "¡Datos actualizados correctamente!");
                    btnVolver.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar.\n" + response.body(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) { 
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCambiarVehiculo.addActionListener(e ->{
            try {
                //falta funcionalidad
            } catch (Exception ex) {
                // TODO: handle exception
            }
        });
    }
}