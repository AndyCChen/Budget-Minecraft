package minecraft;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;


/**
 *
 * @author Andy
 */
public class Minecraft {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Minecraft app = new Minecraft();
        app.init(640, 380);
        
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            
            Display.update();
            Display.sync(60);
        }
        
        Display.destroy();
    }
    
    /**
     * Create window and initialize openGl configurations
     * @param width window width
     * @param height window height
     */
    public void init(int width, int height)
    {
        try
        {
            Display.setFullscreen(false);
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.setTitle("Minecraft");
            
            Display.create();
            
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho( (double) -width/2, (double) width/2, (double) -height/2, (double) height/2, 1, -1 ); // origin is the center of the screen
            glMatrixMode(GL_MODELVIEW);
            glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
            glPointSize(1.0f);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
