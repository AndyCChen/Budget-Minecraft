package minecraft;

import static org.lwjgl.opengl.GL11.*;

public class Cube {
    public void render()
    {
        glBegin(GL_QUADS);
            // top 
            
            // back
            
            // left
            
            // right
        
            // front
            glColor3f(1.0f, 0.0f, 0.0f); // red
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            
            // back
            
        glEnd();
    }  
}
