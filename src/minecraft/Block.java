package minecraft;

import static org.lwjgl.opengl.GL11.*;

public class Block {
    private boolean IsActive;
    private BlockType Type;
    private float x,y,z;

    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);

        private int BlockID;

        BlockType(int i) {
            BlockID=i;
        }

        public int GetID(){
            return BlockID;
        }

        public void SetID(int i){
            BlockID = i;
        }
    }
 
    
    public Block(BlockType type){
        Type= type;
    }
    public void setCoords(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public boolean IsActive() {
        return IsActive;
    }
    public void SetActive(boolean active){
        IsActive=active;
    }
    public int GetID(){
        return Type.GetID();
    }


    public void render()
    {
        glBegin(GL_QUADS);
            // top 
            glColor3f(0.0f, 1.0f, 1.0f); //cyan
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            // back
            glColor3f(1.0f, 1.0f, 0.0f); //yellow
            glVertex3f(1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            
            // left
            glColor3f(0.0f, 0.0f, 1.0f); //blue
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            
            // right
            glColor3f(1.0f, 0.0f, 1.0f); //purple
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
        
            // front
            glColor3f(1.0f, 0.0f, 0.0f); // red
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            
            //bottom
            glColor3f(.0f, 1.0f, 0.0f); //green
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            
        glEnd();
    }
}
