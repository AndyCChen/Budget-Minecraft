package minecraft;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import java.util.Random;

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
    
    public Chunk(float start_x, float start_y, float start_z)
    {
        chunk_block = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int y = 0; y < CHUNK_SIZE; ++y)
            {
                for (int z = 0; z < CHUNK_SIZE; ++z)
                {
                    chunk_block[x][y][z] = new Block(); 
                }
            }
        }
        
        this.start_x = start_x;
        this.start_y = start_y;
        this.start_z = start_z;
        rebuildMesh(start_x, start_y, start_z);
    }
    
    public void render()
    {
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, vertex_VBO);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, color_VBO);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    public void rebuildMesh(float start_x, float start_y, float start_z)
    {
        SimplexNoise noise = new SimplexNoise(128, 0.5, new Random().nextInt());
        
        vertex_VBO = glGenBuffers();
        color_VBO = glGenBuffers();
        
        FloatBuffer vertexPositionBuffer = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12); // 12 floats for each face of a block or 3 floats per vertex
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        
        float[] tempColors = new float[6 * 4 * 3];
        for (int i = 0; i < tempColors.length; ++i)
        {
            tempColors[i] = Math.abs( (float) Math.sin(i) );
        }
        
        // Set noise scale and height factor
    double scale = 0.1;
    double heightFactor = CHUNK_SIZE / 2;  // Controls the variation in height

    // Calculate maximum height for each column based on noise
    int[][] maxHeight = new int[CHUNK_SIZE][CHUNK_SIZE];
    for (int x = 0; x < CHUNK_SIZE; ++x) {
        for (int z = 0; z < CHUNK_SIZE; ++z) {
            // Using getNoise to generate height
            double noiseValue = noise.getNoise(x, z);
            int calculatedHeight = (int)(noiseValue * heightFactor + CHUNK_SIZE / 2);
            maxHeight[x][z] = Math.max(0, Math.min(calculatedHeight, CHUNK_SIZE - 1));  // Ensure height is within bounds
        }
    }
        
        
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int y = 0; y < CHUNK_SIZE; ++y)
            {
                for (int z = 0; z < CHUNK_SIZE; ++z)
                {
                    if(y < maxHeight[x][z]){
                        vertexPositionBuffer.put( createCube( start_x + x * BLOCK_LENGTH, start_y + y * BLOCK_LENGTH, start_z + z * BLOCK_LENGTH ) );
                        colorBuffer.put(tempColors);
                    }
                }
            }
        }
        
        vertexPositionBuffer.flip();
        colorBuffer.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, vertex_VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexPositionBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, color_VBO);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0); // unbind buffer
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
}
