package biblioteca.ui;

import java.awt.*;
import java.nio.file.*;
import javax.swing.*;

/** Formulario Swing con eventos y BorderLayout / FlowLayout, unidad 5. */
public final class SwingExportar {
  private SwingExportar() {}

  public static void abrir(String contenido) {
    SwingUtilities.invokeLater(
        () -> {
          UIManager.put("Panel.background", Color.WHITE);
          UIManager.put("OptionPane.background", Color.WHITE);
          UIManager.put("OptionPane.messageForeground", new Color(84, 37, 44));
          UIManager.put("Button.background", new Color(189, 41, 55));
          UIManager.put("Button.foreground", Color.WHITE);
          UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 13));
          UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
          JFrame f = new JFrame("Henry System Library · Exportar reporte (Swing)");
          try {
            f.setIconImages(java.util.List.of(
                javax.imageio.ImageIO.read(SwingExportar.class.getResource("/imagenes/logo-32.png")),
                javax.imageio.ImageIO.read(SwingExportar.class.getResource("/imagenes/logo-256.png"))));
          } catch (java.io.IOException ex) { biblioteca.service.Logger.getInstance().registrar("No se pudo cargar el ícono Swing: " + ex.getMessage()); }
          f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          f.setLayout(new BorderLayout(14, 14));
          JTextField nombre = new JTextField("reporte.csv", 25);
          JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
          arriba.add(new JLabel("Nombre del archivo *"));
          arriba.add(nombre);
          f.add(arriba, BorderLayout.NORTH);
          JTextArea vista = new JTextArea(contenido, 18, 65);
          vista.setEditable(false);
          vista.setBackground(new Color(255, 247, 248));
          vista.setForeground(new Color(84, 37, 44));
          vista.setFont(new Font("Consolas", Font.PLAIN, 13));
          f.add(new JScrollPane(vista), BorderLayout.CENTER);
          JButton guardar = new JButton("Guardar CSV"), cerrar = new JButton("Cerrar");
          JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
          guardar.setBackground(new Color(189, 41, 55));
          guardar.setForeground(Color.WHITE);
          cerrar.setBackground(new Color(255, 235, 238));
          cerrar.setForeground(new Color(169, 35, 49));
          acciones.add(guardar);
          acciones.add(cerrar);
          f.add(acciones, BorderLayout.SOUTH);
          guardar.addActionListener(
              ev -> {
                if (nombre.getText().isBlank()) {
                  JOptionPane.showMessageDialog(f, "Ingrese un nombre de archivo.");
                  return;
                }
                JFileChooser c = new JFileChooser();
                c.setSelectedFile(
                    new java.io.File(
                        nombre.getText().endsWith(".csv")
                            ? nombre.getText()
                            : nombre.getText() + ".csv"));
                if (c.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) {
                  java.io.File dest = c.getSelectedFile();
                  if (dest.exists()
                      && JOptionPane.showConfirmDialog(
                              f,
                              "¿Reemplazar el archivo existente?",
                              "Confirmar",
                              JOptionPane.YES_NO_OPTION)
                          != JOptionPane.YES_OPTION) return;
                  try {
                    Files.writeString(dest.toPath(), "\ufeff" + contenido);
                    JOptionPane.showMessageDialog(f, "Reporte guardado.");
                    f.dispose();
                  } catch (Exception ex) {
                    JOptionPane.showMessageDialog(f, "No se pudo guardar: " + ex.getMessage());
                  }
                }
              });
          cerrar.addActionListener(e -> f.dispose());
          f.pack();
          f.setLocationRelativeTo(null);
          f.setVisible(true);
        });
  }
}
