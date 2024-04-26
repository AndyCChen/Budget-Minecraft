package minecraft;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import java.util.Random;
import minecraft.BlockTexture.BlockTextureType;

/**
 *
 * @author Andy
 */
public class Chunk {
    final private static SimplexNoise noise = new SimplexNoise(128, 0.5, new Random().nextInt());
    public static final int CHUNK_SIZE = 30;
    public static final int BLOCK_LENGTH = 2;
    
    private Block[][][] chunk_block;
    private int vertex_VBO;
    private int color_VBO;
    private int start_x, start_y, start_z;
    
    private int VBOTextureHandle;
    
    public Chunk(int start_x, int start_y, int start_z)
    {
        Random r = new Random();
        
        chunk_block = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int y = 0; y < CHUNK_SIZE; ++y)
            {
                for (int z = 0; z < CHUNK_SIZE; ++z)
                {
                    chunk_block[x][y][z] = new Block( generateBlockType( r.nextInt() ) );
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
            glBindTexture(GL_TEXTURE_2D, BlockTexture.getTexture().getTextureID());
            glTexCoordPointer(2,GL_FLOAT,0,0L);
            glBindBuffer(GL_ARRAY_BUFFER, vertex_VBO);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, color_VBO);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    public void rebuildMesh(int start_x, int start_y, int start_z)
    {
        this.start_x = start_x;
        this.start_y = start_y;
        this.start_z = start_z;
        
        vertex_VBO = glGenBuffers();
        color_VBO = glGenBuffers();
        
        FloatBuffer vertexPositionBuffer = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12); // 12 floats for each face of a block or 3 floats per vertex
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        
        // Set noise scale and height factor
        double heightFactor = CHUNK_SIZE / 2;  // Controls the variation in height

        // Calculate maximum height for each column based on noise
        int[][] maxHeight = new int[CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; ++x) {
            for (int z = 0; z < CHUNK_SIZE; ++z) {
                // Using getNoise to generate height
                double noiseValue = noise.getNoise( start_x * CHUNK_SIZE + x,start_z * CHUNK_SIZE + z );
                int calculatedHeight = (int)(noiseValue * heightFactor + CHUNK_SIZE / 4);
                maxHeight[x][z] = Math.max(1, Math.min(calculatedHeight, CHUNK_SIZE - 1));  // Ensure height is within bounds
            }
        }
        
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int y = 0; y < CHUNK_SIZE; ++y)
            {
                for (int z = 0; z < CHUNK_SIZE; ++z)
                {
                    if(y < maxHeight[x][z]){
                        vertexPositionBuffer.put( createCube( (start_x * 2 * CHUNK_SIZE) + (x * BLOCK_LENGTH), (start_y * 2 * CHUNK_SIZE) + (y * BLOCK_LENGTH), (start_z * 2 * CHUNK_SIZE) + (z * BLOCK_LENGTH) ) );
                        VertexTextureData.put(createTexCube((float) 0, (float) 0,chunk_block[x][y][z]));
                        colorBuffer.put( createCubeVertexCol( getCubeColor( chunk_block[x][y][z] ) ) );
                    }
                }
            }
        }

        vertexPositionBuffer.flip();
        colorBuffer.flip();
        VertexTextureData.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, vertex_VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexPositionBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, color_VBO);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData,GL_STATIC_DRAW);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0); // unbind buffer
    }
    
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }

    private float[] getCubeColor(Block block) {
        return new float[] { 0.7f, 0.7f, 0.7f };
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
    
    private BlockTextureType generateBlockType(int r)
    {
        BlockTextureType[] types = BlockTextureType.values();
        return types[ Math.abs(r % types.length) ];
    }
    
    public static float[] createTexCube(float x, float y, Block block) {
        float offset_x = (1024.0f / 64) / 1024f;
        float offset_y = ( 512.0f / 32 ) / 512.0f;
        
        switch (block.getBlockType())
        {
            case Grass:
                return new float[] {
                    // TOP
                    x + offset_x*23, y + offset_y*17,
                    x + offset_x*24, y + offset_y*17,
                    x + offset_x*24, y + offset_y*18,
                    x + offset_x*23, y + offset_y*18,
                    // BOTTOM
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                    // FRONT QUAD
                    
                    x + offset_x*20, y + offset_y*18,
                    x + offset_x*21, y + offset_y*18,
                    x + offset_x*21, y + offset_y*17,
                    x + offset_x*20, y + offset_y*17,
                    
                    // BACK QUAD
                    x + offset_x*20, y + offset_y*18,
                    x + offset_x*21, y + offset_y*18,
                    x + offset_x*21, y + offset_y*17,
                    x + offset_x*20, y + offset_y*17,
                    
                    // LEFT QUAD
                    x + offset_x*20, y + offset_y*17,
                    x + offset_x*21, y + offset_y*17,
                    x + offset_x*21, y + offset_y*18,
                    x + offset_x*20, y + offset_y*18,
                    // RIGHT QUAD
                    x + offset_x*20, y + offset_y*17,
                    x + offset_x*21, y + offset_y*17,
                    x + offset_x*21, y + offset_y*18,
                    x + offset_x*20, y + offset_y*18,
                };
            case Sand:
                return new float[] {
                    // top
                    x + offset_x * 2, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 31,
                    // bottom
                    x + offset_x * 1, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 32,
                    // front
                    x + offset_x * 1, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 32,
                    // back
                    x + offset_x * 1, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 32,
                    // left
                    x + offset_x * 1, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 32,
                    // right
                    x + offset_x * 1, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 32,
                };
            case Stone:
                return new float[] {
                    // top
                    x + offset_x * 34, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 1,
                    x + offset_x * 34, y + offset_y * 1,
                    // bottom
                    x + offset_x * 34, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 1,
                    x + offset_x * 34, y + offset_y * 1,
                    // front
                    x + offset_x * 34, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 1,
                    x + offset_x * 34, y + offset_y * 1,
                    // back
                    x + offset_x * 34, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 1,
                    x + offset_x * 34, y + offset_y * 1,
                    // left
                    x + offset_x * 34, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 1,
                    x + offset_x * 34, y + offset_y * 1,
                    // right
                    x + offset_x * 34, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 1,
                    x + offset_x * 34, y + offset_y * 1,
                };
            case Bedrock:
                return new float[] {
                    // top
                    x + offset_x * 14, y + offset_y * 5,
                    x + offset_x * 13, y + offset_y * 5,
                    x + offset_x * 13, y + offset_y * 4,
                    x + offset_x * 14, y + offset_y * 4,
                    // bottom
                    x + offset_x * 14, y + offset_y * 5,
                    x + offset_x * 13, y + offset_y * 5,
                    x + offset_x * 13, y + offset_y * 4,
                    x + offset_x * 14, y + offset_y * 4,
                    // front
                    x + offset_x * 14, y + offset_y * 4,
                    x + offset_x * 13, y + offset_y * 4,
                    x + offset_x * 13, y + offset_y * 5,
                    x + offset_x * 14, y + offset_y * 5,
                    // back
                    x + offset_x * 13, y + offset_y * 4,
                    x + offset_x * 14, y + offset_y * 4,
                    x + offset_x * 14, y + offset_y * 5,
                    x + offset_x * 13, y + offset_y * 5,
                    // left
                    x + offset_x * 13, y + offset_y * 5,
                    x + offset_x * 14, y + offset_y * 5,
                    x + offset_x * 14, y + offset_y * 4,
                    x + offset_x * 13, y + offset_y * 4,
                    // right
                    x + offset_x * 14, y + offset_y * 5,
                    x + offset_x * 13, y + offset_y * 5,
                    x + offset_x * 13, y + offset_y * 4,
                    x + offset_x * 14, y + offset_y * 4,
                };
            case Water:
                return new float[] {
                    // top
                    x + offset_x * 37, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 8,
                    x + offset_x * 37, y + offset_y * 8,
                    // bottom
                    x + offset_x * 37, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 8,
                    x + offset_x * 37, y + offset_y * 8,
                    // front
                    x + offset_x * 37, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 8,
                    x + offset_x * 37, y + offset_y * 8,
                    // back
                    x + offset_x * 37, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 8,
                    x + offset_x * 37, y + offset_y * 8,
                    // left
                    x + offset_x * 37, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 8,
                    x + offset_x * 37, y + offset_y * 8,
                    // right
                    x + offset_x * 37, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 8,
                    x + offset_x * 37, y + offset_y * 8,
                };
            case Dirt:
                return new float[] {
                    // top
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                    // bottom
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                    // front
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                    // back
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                    // left
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                    // right
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                };
            default:
                System.err.println("Missing texture!");
                return null;
        } 
    }
}
