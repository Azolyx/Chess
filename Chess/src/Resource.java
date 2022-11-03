import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Resource {
    public static BufferedImage getImage(String imageName) {
        try {
            return ImageIO.read(Resource.class.getResource(imageName));
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }
}
