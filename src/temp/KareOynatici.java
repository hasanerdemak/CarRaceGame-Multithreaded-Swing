package temp;

import javax.swing.*;

public class KareOynatici extends JFrame {
    public KareOynatici() {
        setTitle("temp.Kare Oynatici");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        Kare kare = new Kare();
        add(kare);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                KareOynatici kareOynatici = new KareOynatici();
                kareOynatici.setVisible(true);
            }
        });
    }
}
