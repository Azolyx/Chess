import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CreateWindow extends JFrame implements MouseListener {
    WindowManager windowManager;

    public CreateWindow(String title, int width, int height, WindowManager windowManager) {
        super(title);
        this.windowManager = windowManager;
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        addMouseListener(this);
        createBufferStrategy(3);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        windowManager.mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        windowManager.mouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
