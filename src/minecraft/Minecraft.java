package minecraft;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import org.lwjgl.util.glu.GLU;

public class Minecraft {

    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    public static void main(String[] args) {
        Minecraft app = new Minecraft();
        app.init();
        
        CameraController camera = new CameraController(0.0f, Chunk.BLOCK_LENGTH * -20.0f, 0.0f);
        
        final int RADIUS = 4;
        World.createChunks(RADIUS);
        
        float dt = 0;
        long previous_time = 0;
        long current_time = 0;
        
        float velocity = 50.0f;
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
            //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            
            World.renderChunks();
            World.checkChunks();
            //World.printCoor();
            
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
            
            initLightArrays();
            glLight(GL_LIGHT0, GL_POSITION, lightPosition);
            glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
            glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
            glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);
            
            
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
            glEnable(GL_LIGHTING);
            glEnable(GL_LIGHT0);
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            //glEnable(GL_BLEND);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
               
        BlockTexture.loadTextures();
    }
    
    private void initLightArrays(){
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
    }
}
