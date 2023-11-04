package uniandes.dpoo.taller4.interfaz;
import uniandes.dpoo.taller4.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Juego {

    private Tablero tablero;
    private TableroPanel tableroPanel;
    private JFrame ventana;
    private JComboBox<String> tamanosComboBox;
    private JRadioButton facilButton;
    private JRadioButton medioButton;
    private JRadioButton dificilButton;
    private ButtonGroup dificultadGroup;
    private JLabel jugadasLabel;
    private Color colorEncendido = Color.YELLOW;
    private Color colorApagado = Color.BLACK;
    private String nombreUsuario;
    
    

    public Juego() {
        ventana = new JFrame("Lights Out");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout());

        String[] tamanos = {"3x3", "4x4", "5x5", "6x6", "7x7", "8x8", "9x9", "10x10"};
        tamanosComboBox = new JComboBox<>(tamanos);
        tamanosComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) tamanosComboBox.getSelectedItem();
                int nuevoTamano = Integer.parseInt(seleccion.substring(0, seleccion.indexOf("x")));
                tablero = new Tablero(nuevoTamano);
                tablero.desordenar(20);
                tableroPanel.repaint();
                reiniciarJugadas();
            }
        });

        JPanel opcionesPanel = new JPanel();
        opcionesPanel.add(new JLabel("Tamaño del tablero: "));
        opcionesPanel.add(tamanosComboBox);

        facilButton = new JRadioButton("Fácil");
        medioButton = new JRadioButton("Medio");
        dificilButton = new JRadioButton("Difícil");
        dificultadGroup = new ButtonGroup();
        dificultadGroup.add(facilButton);
        dificultadGroup.add(medioButton);
        dificultadGroup.add(dificilButton);

        facilButton.setSelected(true);

        ActionListener dificultadListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) tamanosComboBox.getSelectedItem();
                int nuevoTamano = Integer.parseInt(seleccion.substring(0, seleccion.indexOf("x")));
                tablero = new Tablero(nuevoTamano);
                if (facilButton.isSelected()) {
                    tablero.desordenar(3);
                } else if (medioButton.isSelected()) {
                    tablero.desordenar(5);
                } else if (dificilButton.isSelected()) {
                    tablero.desordenar(8);
                }
                tableroPanel.repaint();
                reiniciarJugadas();
            }
        };

        facilButton.addActionListener(dificultadListener);
        medioButton.addActionListener(dificultadListener);
        dificilButton.addActionListener(dificultadListener);

        JPanel dificultadPanel = new JPanel();
        dificultadPanel.add(facilButton);
        dificultadPanel.add(medioButton);
        dificultadPanel.add(dificilButton);

        JPanel opcionesDificultadPanel = new JPanel();
        opcionesDificultadPanel.setLayout(new BorderLayout());
        opcionesDificultadPanel.add(opcionesPanel, BorderLayout.NORTH);
        opcionesDificultadPanel.add(dificultadPanel, BorderLayout.CENTER);

        tableroPanel = new TableroPanel();

        jugadasLabel = new JLabel("Jugadas: 0");

        JButton nuevoButton = new JButton("Nuevo");
        nuevoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) tamanosComboBox.getSelectedItem();
                int nuevoTamano = Integer.parseInt(seleccion.substring(0, seleccion.indexOf("x")));
                tablero = new Tablero(nuevoTamano);
                if (facilButton.isSelected()) {
                    tablero.desordenar(3);
                } else if (medioButton.isSelected()) {
                    tablero.desordenar(5);
                } else if (dificilButton.isSelected()) {
                    tablero.desordenar(8);
                }
                tableroPanel.repaint();
                reiniciarJugadas();
            }
        });

        JButton reiniciarButton = new JButton("Reiniciar");
        reiniciarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tablero.reiniciar();
                tableroPanel.repaint();
                reiniciarJugadas();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1));
        buttonPanel.add(nuevoButton);
        buttonPanel.add(reiniciarButton);

        ventana.add(opcionesDificultadPanel, BorderLayout.NORTH);
        ventana.add(tableroPanel, BorderLayout.CENTER);
        ventana.add(jugadasLabel, BorderLayout.SOUTH);
        ventana.add(buttonPanel, BorderLayout.EAST);

        tablero = new Tablero(3);
        tablero.desordenar(20);

        tableroPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = e.getY() / tableroPanel.getTamanoCelda();
                int columna = e.getX() / tableroPanel.getTamanoCelda();
                tablero.jugar(fila, columna);
                tableroPanel.repaint();
                actualizarJugadas();
                if (tablero.tableroIluminado()) {
                    JOptionPane.showMessageDialog(ventana, "Ganaste!", "Felicidades!", JOptionPane.INFORMATION_MESSAGE);
                    ActualizarTop10(nombreUsuario, tablero.darJugadas());
                }
            }
        });

        JButton top10Button = new JButton("TOP-10");
        top10Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarTop10();
            }
        });
        
        JButton cambiarJugador = new JButton("Cambiar Jugador");
        cambiarJugador.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		nombreUsuario = JOptionPane.showInputDialog(ventana, "Por favor, ingrese su nombre:");
        	}
        });

        buttonPanel.add(top10Button);
        buttonPanel.add(cambiarJugador);

        ventana.pack();
        ventana.setVisible(true);
    }

    private void mostrarTop10() {
        List<String> top10 = leerTop10DesdeCSV();

        if (top10.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "No hay datos en el top 10.", "TOP-10 Vacío", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JDialog top10Dialog = new JDialog(ventana, "TOP-10", true);
            top10Dialog.setLayout(new BorderLayout());

            JTextArea textArea = new JTextArea(15, 30);
            textArea.setEditable(false);
            textArea.setText("TOP-10:\n\n");

            for (String registro : top10) {
                textArea.append(registro + "\n");
            }

            JScrollPane scrollPane = new JScrollPane(textArea);
            top10Dialog.add(scrollPane, BorderLayout.CENTER);

            top10Dialog.pack();
            top10Dialog.setLocationRelativeTo(ventana);
            top10Dialog.setVisible(true);
        }
    }

    private List<String> leerTop10DesdeCSV() {
        List<String> top10 = new ArrayList<>();
        String csvFile = "data/top10.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                top10.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return top10;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Juego());
    }

    private void reiniciarJugadas() {
        tablero.reiniciar();
        tableroPanel.repaint();
        actualizarJugadas();
    }

    private void actualizarJugadas() {
        int jugadas = tablero.darJugadas();
        jugadasLabel.setText("Jugadas: " + jugadas);
    }

    private void ActualizarTop10(String nombreUsuario, int puntaje) {
        List<String> top10 = leerTop10DesdeCSV();
        boolean agregado = false;
        
        for (int i = 0; i < top10.size(); i++) {
            String[] datos = top10.get(i).split(",");
            int puntajeExistente = Integer.parseInt(datos[1]);
            
            if (puntaje < puntajeExistente) {
                top10.add(i, nombreUsuario + "," + puntaje);
                agregado = true;
                break;
            }
        }
        
        if (!agregado && top10.size() < 10) {
            top10.add(nombreUsuario + "," + puntaje);
        }
        
        if (top10.size() > 10) {
            top10.remove(10);
        }
        guardarTop10(top10);
    }
    private void guardarTop10(List<String> top10) {
        String csvFile = "data/top10.csv";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (String registro : top10) {
                bw.write(registro);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    
    private class TableroPanel extends JPanel {

        private int tamanoCelda = 50;
        
        public int getTamanoCelda() {
            return tamanoCelda;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            boolean[][] matriz = tablero.darTablero();

            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    if (matriz[i][j]) {
                        g.setColor(colorEncendido);
                    } else {
                        g.setColor(colorApagado);
                    }
                    g.fillRect(j * tamanoCelda, i * tamanoCelda, tamanoCelda, tamanoCelda);
                }
            }
        }
    }
}
