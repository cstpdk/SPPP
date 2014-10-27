// For week 8
// sestoft@itu.dk * 2014-10-12

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class TestButtonGui {
  public static void main(String[] args) {
    final Random random = new Random();
    final JFrame frame = new JFrame("TestButtonGui");
    final JPanel panel = new JPanel();
    final JButton button = new JButton("Press here");
    frame.add(panel);
    panel.add(button);
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          panel.setBackground(new Color(random.nextInt()));
        }});
    frame.pack(); frame.setVisible(true);
  }
}

