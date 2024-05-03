package minecraft;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

public class CameraController {
    private final Vector3f position;
    private final Vector3f lightCamera;
    
    public float pitch = 0.0f; // rotation around x axis
    public float yaw = 0.0f;   // rotation around y axis
    public CameraController(float x, float y, float z)
    {
        position = new Vector3f(x, y, z);
        lightCamera = new Vector3f(x, y, z);
        
    }
    
    public void strafeLeft(float distance)
    {
        float x_offset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float z_offset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        
        position.x -= x_offset;
        position.z += z_offset;
        
        
        lightCamera.x += x_offset;
        lightCamera.z -= z_offset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lightCamera.x).put(position.y).put(lightCamera.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        
        updatePlayerWorldPosition();
    }
    
    public void strafeRight(float distance)
    {
        float x_offset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float z_offset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        
        position.x -= x_offset;
        position.z += z_offset;
        
        lightCamera.x += x_offset;
        lightCamera.z -= z_offset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lightCamera.x).put(position.y).put(lightCamera.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        
        updatePlayerWorldPosition();
    }
    
    public void move_foward(float distance)
    {
        float x_offset = distance * (float) Math.sin(Math.toRadians(yaw));
        float z_offset = distance * (float) Math.cos(Math.toRadians(yaw));
        
        position.x -= x_offset;
        position.z += z_offset;
        
        lightCamera.x += x_offset;
        lightCamera.z -= z_offset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lightCamera.x).put(position.y).put(lightCamera.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        
        updatePlayerWorldPosition();
    }
    
    public void move_backward(float distance)
    {
        float x_offset = distance * (float) Math.sin(Math.toRadians(yaw));
        float z_offset = distance * (float) Math.cos(Math.toRadians(yaw));
        
        position.x += x_offset;
        position.z -= z_offset;
        
        
        lightCamera.x -= x_offset;
        lightCamera.z += z_offset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lightCamera.x).put(position.y).put(lightCamera.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        updatePlayerWorldPosition();
    }
    
    public void move_up(float distance)
    {
        position.y -= distance;
    }
    
    public void move_down(float distance)
    {
        position.y += distance;
    }
    
    public void lookThrough()
    {
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);

        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lightCamera.x).put(100).put(lightCamera.z+500).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    private void updatePlayerWorldPosition()
    {
        int pos_x = (int) position.x;
        int pos_z = (int) position.z;
        float pos_x_float = (float) pos_x / Chunk.BLOCK_LENGTH;
        float pos_z_float = (float) pos_z / Chunk.BLOCK_LENGTH;
        
        World.updatePlayerPosition_x( pos_x_float >= 0 ? (int) Math.ceil( pos_x_float ) : (int) Math.floor( pos_x_float ) );
        World.updatePlayerPosition_z( pos_z_float >= 0 ? (int) Math.ceil( pos_z_float ) : (int) Math.floor( pos_z_float ) );
        
    }
    
    public void printCameraCoords()
    {
        System.out.print("Position xyz ");
        System.out.print(position.x + " ");
        System.out.print(position.y + " ");
        System.out.println(position.z);
    }
}
