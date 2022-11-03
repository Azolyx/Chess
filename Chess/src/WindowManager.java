import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WindowManager {
    public JFrame window;
    public boolean mouseDown = false;

    public void init(String title, int width, int height) {
        window = new CreateWindow(title, width, height, this);
    }

    public void clear(Graphics2D g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());
    }

    public void drawSquare(Graphics2D g, int x, int y, int width, int height, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public void drawImage(Graphics2D g, int x, int y, int width, int height, BufferedImage img) {
        g.drawImage(img, x, y, width, height, null);
    }

    public void drawSphere(Graphics2D g, int x, int y, int width, int height, Color color) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }

    public void drawText(Graphics2D g, int x, int y, int size, int style, String font, String text, Color color, boolean center) {
        Font textFont = new Font(font, style, size);
        g.setColor(color);
        g.setFont(new Font(font, style, size));
        int fontWidth = g.getFontMetrics(textFont).stringWidth(text);
        int fontHeight = g.getFontMetrics(textFont).getHeight();
        if (center) { g.drawString(text, x - fontWidth / 2, y + fontHeight / 2); }
        else { g.drawString(text, x, y); }
    }
}