package minecraft;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import org.lwjgl.util.glu.GLU;

public class Minecraft {

    public static void main(String[] args) {
        final int RADIUS = 1;
        Minecraft app = new Minecraft();
        app.init();
        
        // -1 -1
        // -1  0
        //  0 -1
        //  0  0
        
        BlockTexture.loadTextures();

        CameraController camera = new CameraController(0.0f, Chunk.BLOCK_LENGTH * -15.0f, 0.0f);
        World.createChunks(RADIUS);
        
        float dt = 0;
        long previous_time = 0;
        long current_time = 0;
        
        float velocity = 15.0f;
        float mouse_sensitivity = 0.1f;
        
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
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            
            World.renderChunks();
            World.printCoor();
            
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
            
            Display.setDisplayMode(new DisplayMode(1280, 720));
            
            Display.setTitle("Minecraft: Budget Edition");
            Display.create();
            Mouse.setGrabbed(true);
            
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            GLU.gluPerspective(45.0f, (float) Display.getWidth()/ (float) Display.getHeight(), 0.1f, 500.0f);
            glMatrixMode(GL_MODELVIEW);
            glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
            glEnable(GL_DEPTH_TEST);
            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);
            glEnable(GL_TEXTURE_2D); //Texture
            glEnableClientState (GL_TEXTURE_COORD_ARRAY);
            glEnable(GL_CULL_FACE);
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            //glEnable(GL_BLEND);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
