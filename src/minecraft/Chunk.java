package minecraft;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import java.util.Random;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


/**
 *
 * @author Andy
 */
public class Chunk {
    public static final int CHUNK_SIZE = 30;
    public static final int BLOCK_LENGTH = 2;
    
    private Block[][][] chunk_block;
    private int vertex_VBO;
    private int color_VBO;
    private float start_x, start_y, start_z;
    
    private Random r;
    
    private int VBOTextureHandle;
    private Texture texture;
    
    public Chunk(float start_x, float start_y, float start_z)
    {
        try
        {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("/res/terrain.png"));
        }
        catch(Exception e)
        {
            System.err.println("Error loading texture: " + e.getMessage());
            e.printStackTrace();
        }
        r= new Random();
        chunk_block = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int y = 0; y < CHUNK_SIZE; ++y)
            {
                for (int z = 0; z < CHUNK_SIZE; ++z)
                {
                        chunk_block[x][y][z] = new
                        Block(Block.BlockType.BlockType_Grass);
                   
                    
                }
            }
        }
        
        vertex_VBO = glGenBuffers();
        color_VBO = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        
        this.start_x = start_x;
        this.start_y = start_y;
        this.start_z = start_z;
        rebuildMesh(start_x, start_y, start_z);
    }
    
    public void render()
    {
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2,GL_FLOAT,0,0L);
            glBindBuffer(GL_ARRAY_BUFFER, vertex_VBO);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, color_VBO);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    public void rebuildMesh(float start_x, float start_y, float start_z)
    {
       
        FloatBuffer vertexPositionBuffer = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12); // 12 floats for each face of a block or 3 floats per vertex
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int y = 0; y < CHUNK_SIZE; ++y)
            {
                for (int z = 0; z < CHUNK_SIZE; ++z)
                {
                    vertexPositionBuffer.put( createCube( start_x + x * BLOCK_LENGTH, start_y + y * BLOCK_LENGTH, start_z + z * BLOCK_LENGTH ) );
                    VertexTextureData.put(createTexCube((float) 0, (float) 0,chunk_block[x][y][z]));
                    colorBuffer.put(createCubeVertexCol(getCubeColor(chunk_block[(int) x][(int) y][(int) z])));
                }
            }
        }

        vertexPositionBuffer.flip();
        colorBuffer.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vertex_VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexPositionBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, color_VBO);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0); // unbind buffer
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle); //texture
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }
    
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }

    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
}

    
    private float[] createCube(float x, float y, float z)
    {
        int offset = BLOCK_LENGTH / 2;
        
        return new float[] {
            // top
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - BLOCK_LENGTH,
            x + offset, y + offset, z - BLOCK_LENGTH,
            
            // bottom
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - BLOCK_LENGTH,
            x + offset, y - offset, z - BLOCK_LENGTH,
            
            // front
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            
            // back
            x + offset, y - offset, z - BLOCK_LENGTH,
            x - offset, y - offset, z - BLOCK_LENGTH,
            x - offset, y + offset, z - BLOCK_LENGTH,
            x + offset, y + offset, z - BLOCK_LENGTH,
            
            // left
            x - offset, y + offset, z - BLOCK_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - BLOCK_LENGTH,
        
            // right
            x + offset, y + offset, z - BLOCK_LENGTH,
            x + offset, y + offset, z,
            x + offset, y - offset, z,
            x + offset, y - offset, z - BLOCK_LENGTH,
        };
    }
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*0, y + offset*0,
                x + offset*1, y + offset*0,
                x + offset*1, y + offset*1,
                x + offset*0, y + offset*1,
                // TOP!
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*1,
                x + offset*4, y + offset*1,
                x + offset*4, y + offset*0,
                x + offset*3, y + offset*0,
                // BACK QUAD
                x + offset*4, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*4, y + offset*1,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1};

        }

}
