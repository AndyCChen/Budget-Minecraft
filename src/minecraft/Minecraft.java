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
    
    
    private static float timeOfDay = 0;
    
    public static void main(String[] args) {
        Minecraft app = new Minecraft();
        app.init();
        
        CameraController camera = new CameraController(0.0f, Chunk.BLOCK_LENGTH * -20.0f, 0.0f);
        
        final int RADIUS = 5;
        World.createChunks(RADIUS);
        
        float dt = 0;
        long previous_time = 0;
        long current_time = 0;
        
        float velocity = 75.0f;
        float mouse_sensitivity = 0.1f;
        
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            current_time = Sys.getTime(); 
            dt = (float) (current_time - previous_time) / Sys.getTimerResolution();
            previous_time = current_time;
            
            initLight();
            updateDayNightCycle();

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
            glEnable(GL_NORMALIZE);
            glEnable(GL_LIGHTING);
            glEnable(GL_LIGHT0);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_BLEND);
            

        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
               
        BlockTexture.loadTextures();
    }
     
    private static float calculateDaylightIntensity() {
        // Example: brighter during the day, darker at night
        return Math.max(0.2f, Math.min(1.0f, 1.0f - Math.abs(timeOfDay - 0.5f) * 2.0f));
    }
    private static void updateDayNightCycle() {
        // Simulate time passing (you can adjust this based on your game's speed)
        timeOfDay += 0.001f;
        if (timeOfDay >= 1.0f) {
            timeOfDay = 0; // Reset time of day to start a new day
        }
    }
    private static void initLight(){
        // Set ambient light
        float ambient[] = {0.1f, 0.1f, 0.1f, 1.0f};
        FloatBuffer ambientBuffer = BufferUtils.createFloatBuffer(4);
        ambientBuffer.put(ambient).flip();
        glLightModel(GL_LIGHT_MODEL_AMBIENT, ambientBuffer);
        // Set directional light
        float lightPosition[] = {0.0f, 0.0f, 0.0f, 1.0f}; 
        FloatBuffer lightPositionBuffer = BufferUtils.createFloatBuffer(4);
        lightPositionBuffer.put(lightPosition).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPositionBuffer);

        float daylightIntensity = calculateDaylightIntensity();
        float diffuse[] = {daylightIntensity, daylightIntensity, daylightIntensity, 1.0f};
        FloatBuffer diffuseBuffer = BufferUtils.createFloatBuffer(4);
        diffuseBuffer.put(diffuse).flip();
        glLight(GL_LIGHT0, GL_DIFFUSE, diffuseBuffer);
    }
}
