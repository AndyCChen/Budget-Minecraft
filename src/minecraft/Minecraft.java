package minecraft;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import org.lwjgl.util.glu.GLU;

public class Minecraft {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Minecraft app = new Minecraft();
        app.init();
        
        CameraController camera = new CameraController(0.0f, 0.0f, -2.0f);
        Cube cube1 = new Cube();
        
        float dt = 0;
        long previous_time = 0;
        long current_time = 0;
        
        float velocity = 5.0f;
        float mouse_sensitivity = 0.1f;
        
        Mouse.setGrabbed(true);
        
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            current_time = Sys.getTime(); 
            dt = (float) (current_time - previous_time) / Sys.getTimerResolution();
            previous_time = current_time;
            
            camera.yaw += Mouse.getDX()* mouse_sensitivity;
            camera.pitch -= Mouse.getDY() * mouse_sensitivity;
            
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) // strafe left
            {
                camera.strafeLeft(velocity * dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) // strafe right
            {
                camera.strafeRight(velocity * dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) // foward
            {
                camera.move_foward(velocity * dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) // backward
            {
                camera.move_backward(velocity * dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) // up
            {
                
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) // down
            {
                
            }
            
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            cube1.render();
            
            
            Display.update();
            Display.sync(60);
        }
        
        Display.destroy();
    }
    
    /**
     * Create window and initialize openGl configurations
     */
    public void init()
    {
        try
        {
            Display.setFullscreen(false);
            DisplayMode displayMode = null;
            DisplayMode d[] = Display.getAvailableDisplayModes();
            for(int i = 0; i < d.length; ++i)
            {
                if (d[i].getHeight() == 380 && d[i].getWidth() == 640 && d[i].getBitsPerPixel() == 32)
                {
                    displayMode = d[i];
                    break;
                }
            }
            
            if (displayMode != null)
            {
                Display.setDisplayMode(displayMode);
            }
            
            Display.setTitle("Minecraft");
            Display.create();
            
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            GLU.gluPerspective(100.0f, (float) Display.getWidth()/ (float) Display.getHeight(), 0.1f, 300.0f);
            glMatrixMode(GL_MODELVIEW);
            glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
