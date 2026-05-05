package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VentanaAuth extends JFrame {
     private String rol;
    final Color fondoClarito_verde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47,158,68),2,true);
    final Color btnNormalVerde =new Color(79,201,95); // Color verde estilo boceto: Color(100, 200, 100)
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47,158,68),2,true);
    final EmptyBorder paddingBtnAtras =  new EmptyBorder(5, 10, 5, 10);
   
    public VentanaAuth(String rol) {
        this.rol = rol;
        setTitle("Autenticación - " + rol);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(fondoClarito_verde);

        JPanel panelMain = new JPanel();
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
        panelMain.setBackground(fondoClarito_verde);

        // --- SECCIÓN REGISTRO ---
        JPanel panelRegistro = new JPanel(new GridLayout(0, 2, 5, 5));
        panelRegistro.setBorder(BorderFactory.createTitledBorder("REGISTER " + rol));
        
        JTextField txtRegNombre = new JTextField();
        JTextField txtRegEmail = new JTextField();
        JPasswordField txtRegPass = new JPasswordField();
        
        panelRegistro.add(new JLabel("User name:")); panelRegistro.add(txtRegNombre);
        panelRegistro.add(new JLabel("Email:")); panelRegistro.add(txtRegEmail);
        panelRegistro.add(new JLabel("Password:")); panelRegistro.add(txtRegPass);
        panelRegistro.setBackground(fondoClarito_verde);
        
        // Campos extra para conductor
        JTextField txtLicencia = new JTextField();
        if (rol.equals("CONDUCTOR")) {
            panelRegistro.add(new JLabel("LicenciaConducir:")); panelRegistro.add(txtLicencia);
        }

        JButton btnRegister = new JButton("REGISTER");
        btnRegister.setBackground(btnNormalVerde);
        btnRegister.setForeground(Color.white);
        btnRegister.setFont(fontBtnNormal);
        btnRegister.setBorder(btnNormalBorde);

        panelRegistro.add(new JLabel("")); 
        panelRegistro.add(btnRegister);

        // --- SECCIÓN LOGIN ---
        JPanel panelLogin = new JPanel(new GridLayout(0, 2, 5, 5));
        panelLogin.setBorder(BorderFactory.createTitledBorder("LOGIN"));
        
        JTextField txtLogEmail = new JTextField();
        JPasswordField txtLogPass = new JPasswordField();
        
        panelLogin.add(new JLabel("Email:")); panelLogin.add(txtLogEmail);
        panelLogin.add(new JLabel("Password:")); panelLogin.add(txtLogPass);
        
        JButton btnLogin = new JButton("LOGIN");
        panelLogin.add(new JLabel("")); 
        panelLogin.add(btnLogin);
        panelLogin.setBackground(fondoClarito_verde);
        
        btnLogin.setBackground(btnNormalVerde);
        btnLogin.setForeground(Color.white);
        btnLogin.setFont(fontBtnNormal);
        btnLogin.setBorder(btnNormalBorde);

        // --- SECCIÓN ATRAS ---
        JPanel panelAtras = new JPanel();
        JButton btnAtras = new JButton("Atras");

        btnAtras.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnAtras.setForeground(btnSalirFont);
        btnAtras.setBackground(Color.white);

        panelAtras.setLayout(new BorderLayout());
        panelAtras.add(btnAtras,BorderLayout.WEST);
        panelAtras.setBackground(fondoClarito_verde);

        // --- EVENTOS ---
        
        // EVENTO: REGISTER
        btnRegister.addActionListener(e -> {
            try {
                String nombre = txtRegNombre.getText();
                String email = txtRegEmail.getText();
                String pass = new String(txtRegPass.getPassword());
                
                String url;
                String jsonBody;

                if(nombre.equals("") || email.equals("")  || pass.equals("")){
                    JOptionPane.showMessageDialog(this, "Error al registrar. Debe rellenar todos los campos.", "Error", JOptionPane.INFORMATION_MESSAGE);
                
                }else{
                    if (rol.equals("PASAJERO")) {
                        url = "http://localhost:8080/passengers/registerPassenger"; //
                        jsonBody = String.format(
                            "{\"nombre\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"metodoPago\":\"efectivo\", \"posicionActual\":{\"latitud\":0.0, \"longitud\":0.0}}", 
                            nombre, email, pass
                        ); //
                    } else {
                        url = "http://localhost:8080/drivers/registerDriver"; //
                        String licencia = txtLicencia.getText();
                        jsonBody = String.format(
                            "{\"nombre\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"licenciaConducir\":\"%s\", \"calificacionMedia\":5.0, \"disponible\":true}", 
                            nombre, email, pass, licencia
                        ); //
                    }

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    
                    if (response.statusCode() == 201) { 
                        JOptionPane.showMessageDialog(this, "Registro exitoso en la Base de Datos.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al registrar (Código " + response.statusCode() + ").\n" + response.body(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor Spring Boot.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENTO: LOGIN
        btnLogin.addActionListener(e -> {
            try {
                String email = txtLogEmail.getText();
                String pass = new String(txtLogPass.getPassword());
                
                String url = rol.equals("PASAJERO") ? 
                    "http://localhost:8080/passengers/loginPassenger" : 
                    "http://localhost:8080/drivers/loginDriver";
        
                String jsonBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, pass);
        
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
        
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
                if (response.statusCode() == 200 || response.statusCode() == 202) { 
                    try {
                        // El body ahora debe contener el ID numérico
                        Long idUsuario = Long.parseLong(response.body().trim());
                        
                        JOptionPane.showMessageDialog(this, "¡Login correcto! ID: " + idUsuario);
                        
                        // PASAMOS EL ID AL DASHBOARD
                        new DashboardFrame(rol, email, idUsuario).setVisible(true);
                        dispose(); 
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(this, "Error interno: El servidor no devolvió un ID numérico.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + response.body(), "Login Fallido", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión.");
            }
        });

        // EVENTO: IR ASTRÁS
        btnAtras.addActionListener(e ->{
            try {
                new VentanaPrincipal().setVisible(true);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        add(panelMain, BorderLayout.CENTER);
        panelMain.add(panelRegistro);
        panelMain.add(Box.createVerticalStrut(20)); 
        panelMain.add(panelLogin);
        add(panelAtras,BorderLayout.SOUTH);
    }
}