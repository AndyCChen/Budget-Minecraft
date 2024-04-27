package minecraft;

import static org.lwjgl.opengl.GL11.*;

public class CameraController {
    private final Vector3f position;
    
    public float pitch = 0.0f; // rotation around x axis
    public float yaw = 0.0f;   // rotation around y axis
    
    public CameraController(float x, float y, float z)
    {
        position = new Vector3f(-x, -y, z);
    }
    
    public void strafeLeft(float distance)
    {
        float x_offset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float z_offset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        
        position.x -= x_offset;
        position.z += z_offset;
    }
    
    public void strafeRight(float distance)
    {
        float x_offset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float z_offset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        
        position.x -= x_offset;
        position.z += z_offset;
    }
    
    public void move_foward(float distance)
    {
        float x_offset = distance * (float) Math.sin(Math.toRadians(yaw));
        float z_offset = distance * (float) Math.cos(Math.toRadians(yaw));
        
        position.x -= x_offset;
        position.z += z_offset;
    }
    
    public void move_backward(float distance)
    {
        float x_offset = distance * (float) Math.sin(Math.toRadians(yaw));
        float z_offset = distance * (float) Math.cos(Math.toRadians(yaw));
        
        position.x += x_offset;
        position.z -= z_offset;
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
    }
}
