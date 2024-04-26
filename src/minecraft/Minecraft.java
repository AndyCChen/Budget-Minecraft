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
        
        BlockTexture.loadTextures();
        
        CameraController camera = new CameraController(0.0f, Chunk.BLOCK_LENGTH * -15.0f, 0.0f);
        Chunk chunk_1 = new Chunk(0, 0, -1);
        Chunk chunk_2 = new Chunk(-1, 0, -1);
        Chunk chunk_3 = new Chunk(0, 0, 0);
        Chunk chunk_4 = new Chunk(-1, 0, 0);
        
        float dt = 0;
        long previous_time = 0;
        long current_time = 0;
        
        float velocity = 15.0f;
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
                camera.move_up(velocity * dt);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) // down
            {
                camera.move_down(velocity * dt);
            }
            
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            chunk_1.render();
            chunk_2.render();
            chunk_3.render();
            chunk_4.render();
            
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
            GLU.gluPerspective(45.0f, (float) Display.getWidth()/ (float) Display.getHeight(), 0.1f, 300.0f);
            glMatrixMode(GL_MODELVIEW);
            glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
            glEnable(GL_DEPTH_TEST);
            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);
            glEnable(GL_TEXTURE_2D); //Texture
            glEnableClientState (GL_TEXTURE_COORD_ARRAY);
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            //glEnable(GL_BLEND);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
