package temp.blockmover;

import javax.swing.*;

public class BlockMoverFrame extends JFrame {
    public BlockMoverFrame() {
        setTitle("Block Mover");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        Block block = new Block(200,250);
        Player player = new Player(block);

        addKeyListener(player);
        add(block);

        Thread t1 = new Thread(player);

        Block block2 = new Block(100,200);
        Player player2 = new Player(block2);

        addKeyListener(player2);
        add(block2);

        Thread t2 = new Thread(player2);// Start the player thread

        t1.start();
        t2.start();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BlockMoverFrame());
    }
}