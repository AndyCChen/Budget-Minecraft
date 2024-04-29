package minecraft;

import java.util.ArrayList;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import minecraft.BlockTexture.BlockTextureType;
import minecraft.Block.BlockFaces;

public class Chunk {
    public static final int CHUNK_SIZE = 16;
    public static final int BLOCK_LENGTH = 2;
    
    final private Block[][][] chunk_block;
    private int total_faces;
    private int start_x, start_y, start_z;
    
    private int vertex_VBO;
    private int color_VBO;
    private int VBOTextureHandle;
    
    public Chunk(int start_x, int start_y, int start_z)
    {
        chunk_block = new Block[CHUNK_SIZE][World.WORLD_HEIGHT][CHUNK_SIZE];
        vertex_VBO = glGenBuffers();
        color_VBO = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int y = 0; y < World.WORLD_HEIGHT; ++y)
            {
                for (int z = 0; z < CHUNK_SIZE; ++z)
                {
                    chunk_block[x][y][z] = new Block(x, y, z );
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
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, BlockTexture.getTexture().getTextureID());
            glTexCoordPointer(2,GL_FLOAT,0,0L);
            glBindBuffer(GL_ARRAY_BUFFER, vertex_VBO);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, color_VBO);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0, total_faces * 4); // total blocks * number of vertices per block
        glPopMatrix();
    }
    
    public final void rebuildMesh(int start_x, int start_y, int start_z)
    {
        total_faces = 0;
        this.start_x = start_x;
        this.start_y = start_y;
        this.start_z = start_z;
        
        FloatBuffer vertexPositionBuffer = BufferUtils.createFloatBuffer(CHUNK_SIZE * World.WORLD_HEIGHT * CHUNK_SIZE * 6 * 12); // 12 floats for each face of a block or 3 floats per vertex
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(CHUNK_SIZE * World.WORLD_HEIGHT * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * World.WORLD_HEIGHT * CHUNK_SIZE) * 6 * 8);

        int[][] heightMap = new int[CHUNK_SIZE][CHUNK_SIZE];
        
        WorldGeneration.generateTerrain(start_x, start_z, chunk_block, heightMap); // build the shape of the chunk with stone blocks with water placed starting a certain sea level
        WorldGeneration.decorateTerrain(chunk_block, heightMap);
        
        // building mesh for entire chunk
        // only faces that are adjacent to inactive block will be rendered
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int z = 0; z < CHUNK_SIZE; ++z)
            {
                for (int y = 0; y < World.WORLD_HEIGHT; ++y)
                {
                    ArrayList<Float> cubeMesh = new ArrayList<>();
                    ArrayList<Float> cubeTextureCoordinates = new ArrayList<>();
                    ArrayList<Float> cubeColor = new ArrayList<>();
                    
                    if (chunk_block[x][y][z].isBlockAir()) continue;
                    
                    // check back neighbor
                    if ( z == 0 || chunk_block[x][y][z - 1].isBlockAir() )
                    {
                        addCubeFace(BlockFaces.Back, cubeMesh, cubeTextureCoordinates, cubeColor, chunk_block[x][y][z], x, y, z);
                    }
                    // check front neighbor
                    if ( z + 1 == CHUNK_SIZE || chunk_block[x][y][z + 1].isBlockAir() )
                    {
                        addCubeFace(BlockFaces.Front, cubeMesh, cubeTextureCoordinates, cubeColor, chunk_block[x][y][z], x, y, z);
                    }
                    
                    // check left neighbor
                    if ( x == 0 || chunk_block[x-1][y][z].isBlockAir() )
                    {
                        addCubeFace(BlockFaces.Left, cubeMesh, cubeTextureCoordinates, cubeColor, chunk_block[x][y][z], x, y, z);
                    }
                    // check right neighbor
                    if ( x + 1 == CHUNK_SIZE || chunk_block[x+1][y][z].isBlockAir() )
                    {
                        addCubeFace(BlockFaces.Right, cubeMesh, cubeTextureCoordinates, cubeColor, chunk_block[x][y][z], x, y, z);
                    }
                    
                    // check bottom neighbor
                    if ( y > 0 && chunk_block[x][y - 1][z].isBlockAir() )
                    {
                        addCubeFace(BlockFaces.Bottom, cubeMesh, cubeTextureCoordinates, cubeColor, chunk_block[x][y][z], x, y, z);
                    }
                    // check top neightbor
                    if ( y + 1 == World.WORLD_HEIGHT || chunk_block[x][y + 1][z].isBlockAir() )
                    {
                        addCubeFace(BlockFaces.Top, cubeMesh, cubeTextureCoordinates, cubeColor, chunk_block[x][y][z], x, y, z);
                    }
                    
                    for (int i = 0; i < cubeMesh.size(); ++i) 
                    {
                        vertexPositionBuffer.put( cubeMesh.get(i) );
                    }
                    for (int i = 0; i < cubeTextureCoordinates.size(); ++i) 
                    {
                        VertexTextureData.put( cubeTextureCoordinates.get(i) );
                    }
                    for (int i = 0; i < cubeColor.size(); ++i) 
                    {
                        colorBuffer.put( cubeColor.get(i) );
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
    
    private void addCubeFace(BlockFaces face, ArrayList cubeMesh, ArrayList textureCord, ArrayList cubeColors, Block cube, int cubeX, int cubeY, int cubeZ)  
    {
        total_faces += 1;
        int x = start_x * 2 * CHUNK_SIZE + cubeX * BLOCK_LENGTH;
        int y = start_y * 2 * World.WORLD_HEIGHT + cubeY * BLOCK_LENGTH;
        int z = start_z * 2 * CHUNK_SIZE + cubeZ * BLOCK_LENGTH;
        
        float color[] = getCubeColor(cube);
        
        switch (face) {
            case Back:
                createCubeMeshFace(x, y, z, BlockFaces.Back, cubeMesh);
                createCubeFaceTexture(cube, BlockFaces.Back, textureCord);
                createCubeFaceColor(color, cubeColors);
                break;
            case Front:
                createCubeMeshFace(x, y, z, BlockFaces.Front, cubeMesh);
                createCubeFaceTexture(cube, BlockFaces.Front, textureCord);
                createCubeFaceColor(color, cubeColors);
                break;
            case Left:
                createCubeMeshFace(x, y, z, BlockFaces.Left, cubeMesh);
                createCubeFaceTexture(cube, BlockFaces.Left, textureCord);
                createCubeFaceColor(color, cubeColors);
                break;
            case Right:
                createCubeMeshFace(x, y, z, BlockFaces.Right, cubeMesh);
                createCubeFaceTexture(cube, BlockFaces.Right, textureCord);
                createCubeFaceColor(color, cubeColors);
                break;
            case Bottom:
                createCubeMeshFace(x, y, z, BlockFaces.Bottom, cubeMesh);
                createCubeFaceTexture(cube, BlockFaces.Bottom, textureCord);
                createCubeFaceColor(color, cubeColors);
                break;
            case Top:
                createCubeMeshFace(x, y, z, BlockFaces.Top, cubeMesh);
                createCubeFaceTexture(cube, BlockFaces.Top, textureCord);
                createCubeFaceColor(color, cubeColors);
                break;    
        }
    }
    
    private void createCubeFaceColor(float[] color, ArrayList<Float> cubeColors) 
    {
        for (int i = 0; i < color.length * 4; ++i)
        {
            cubeColors.add( color[i % color.length] );
        }
    }

    private float[] getCubeColor(Block block) 
    {
        return new float[] { 0.7f, 0.7f, 0.7f };
    }
    
    private void createCubeMeshFace(float x, float y, float z, BlockFaces face, ArrayList<Float> cubeMesh)
    {
        int offset = BLOCK_LENGTH / 2;
        switch (face)
        {
            case Back:
            {
                float back[] = new float[] {
                    x + offset, y + offset, z - offset,
                    x + offset, y - offset, z - offset,
                    x - offset, y - offset, z - offset,
                    x - offset, y + offset, z - offset,
                };
                moveFloatArrayToArrayList(cubeMesh, back);
                break;
            }
            case Front:
            {
                float front[] = new float[] {
                    x + offset, y - offset, z + offset,
                    x + offset, y + offset, z + offset,
                    x - offset, y + offset, z + offset,
                    x - offset, y - offset, z + offset,
                };
                moveFloatArrayToArrayList(cubeMesh, front);
                break;
            }
            case Left:
            {
                float left[] = new float[] {
                    x - offset, y + offset, z + offset,
                    x - offset, y + offset, z - offset,
                    x - offset, y - offset, z - offset,
                    x - offset, y - offset, z + offset,
                };
                moveFloatArrayToArrayList(cubeMesh, left);
                break;
            }
            case Right:
            {
                float right[] = new float[] {
                    x + offset, y + offset, z - offset,
                    x + offset, y + offset, z + offset,
                    x + offset, y - offset, z + offset,
                    x + offset, y - offset, z - offset,
                };
                moveFloatArrayToArrayList(cubeMesh, right);
                break;
            }
            case Bottom:
            {
                float bottom[] = new float[] {
                    x + offset, y - offset, z + offset,
                    x - offset, y - offset, z + offset,
                    x - offset, y - offset, z - offset,
                    x + offset, y - offset, z - offset,
                };
                moveFloatArrayToArrayList(cubeMesh, bottom);
                break;
            }
            case Top:
            {
                float top[] = new float[] {
                    x - offset, y + offset, z + offset,
                    x + offset, y + offset, z + offset,
                    x + offset, y + offset, z - offset,  
                    x - offset, y + offset, z - offset,
                };
                moveFloatArrayToArrayList(cubeMesh, top);
                break;
            }
        }
    }
    
    private void moveFloatArrayToArrayList(ArrayList<Float> destination, float[] data)
    {
        for (int i = 0; i < data.length; ++i)
        {
            destination.add( data[i] );
        }
    }
    
    private void addElements(int startIndex, int numberOfElementsToAdd, float[] data, ArrayList<Float> destination)
    {
        for (int i = startIndex; i < startIndex + numberOfElementsToAdd; ++i)
        {
            destination.add( data[i] );
        }
    }
    
    private BlockTextureType generateBlockType(int r)
    {
        BlockTextureType[] types = BlockTextureType.values();
        return types[ Math.abs(r % types.length) ];
    }
    
    public void createCubeFaceTexture(Block block, BlockFaces face, ArrayList<Float> faceTextureCoordinates) {
        float x = 0.0f;
        float y = 0.0f;
        float offset_x = (1024.0f / 64) / 1024f;
        float offset_y = ( 512.0f / 32 ) / 512.0f;
        
        float[] coordinates = null;
        
        switch (block.getBlockType())
        {
            case Grass:
                coordinates = new float[] {
                    // BACK QUAD
                    x + offset_x*20, y + offset_y*17,
                    x + offset_x*20, y + offset_y*18,
                    x + offset_x*21, y + offset_y*18,
                    x + offset_x*21, y + offset_y*17,
                    // FRONT QUAD
                    x + offset_x*21, y + offset_y*18,
                    x + offset_x*21, y + offset_y*17,
                    x + offset_x*20, y + offset_y*17,
                    x + offset_x*20, y + offset_y*18,
                    
                    // LEFT QUAD
                    x + offset_x*21, y + offset_y*17,
                    x + offset_x*20, y + offset_y*17,
                    x + offset_x*20, y + offset_y*18,
                    x + offset_x*21, y + offset_y*18,
                    // RIGHT QUAD
                    x + offset_x*20, y + offset_y*17,
                    x + offset_x*21, y + offset_y*17,
                    x + offset_x*21, y + offset_y*18,
                    x + offset_x*20, y + offset_y*18,
                    // BOTTOM
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                    // TOP
                    x + offset_x*23, y + offset_y*17,
                    x + offset_x*24, y + offset_y*17,
                    x + offset_x*24, y + offset_y*18,
                    x + offset_x*23, y + offset_y*18,  
                };
                break;
            case Sand:
                coordinates = new float[] {
                    // back
                    x + offset_x * 1, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 32,
                    // front
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
                    // bottom
                    x + offset_x * 1, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 32,
                    // top
                    x + offset_x * 2, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 32,
                    x + offset_x * 1, y + offset_y * 31,
                    x + offset_x * 2, y + offset_y * 31,  
                };
                break;
            case Stone:
                coordinates = new float[] {
                    // back
                    x + offset_x * 34, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 1,
                    x + offset_x * 34, y + offset_y * 1,
                    // front
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
                    // bottom
                    x + offset_x * 34, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 1,
                    x + offset_x * 34, y + offset_y * 1,
                    // top
                    x + offset_x * 34, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 0,
                    x + offset_x * 35, y + offset_y * 1,
                    x + offset_x * 34, y + offset_y * 1,
                };
                break;
            case Bedrock:
                coordinates = new float[] {
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
                break;
            case Water:
                coordinates = new float[] {
                    // back
                    x + offset_x * 37, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 8,
                    x + offset_x * 37, y + offset_y * 8,
                    // front
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
                    // bottom
                    x + offset_x * 37, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 8,
                    x + offset_x * 37, y + offset_y * 8,
                    // top
                    x + offset_x * 37, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 7,
                    x + offset_x * 38, y + offset_y * 8,
                    x + offset_x * 37, y + offset_y * 8,   
                };
                break;
            case Dirt:
                coordinates = new float[] {
                    // back
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                    // front
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
                    // bottom
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3, 
                    // top
                    x + offset_x*25, y + offset_y*2,
                    x + offset_x*26, y + offset_y*2,
                    x + offset_x*26, y + offset_y*3,
                    x + offset_x*25, y + offset_y*3,
                };
                break;
            default:
                System.err.println("Missing texture!");
        }
        
        switch (face)
        {
            case Back:
            {
                addElements(0, 8, coordinates, faceTextureCoordinates);
                break;
            }
            case Front:
            {
                addElements(8, 8, coordinates, faceTextureCoordinates);
                break;
            }
            case Left:
            {
                addElements(16, 8, coordinates, faceTextureCoordinates);
                break;
            }
            case Right:
            {
                addElements(24, 8, coordinates, faceTextureCoordinates);
                break;
            }
            case Bottom:
            {
                addElements(32, 8, coordinates, faceTextureCoordinates);
                break;
            }
            case Top:
            {
                addElements(40, 8, coordinates, faceTextureCoordinates);
                break;
            }
        }
    }
    
    public int get_x()
    {
        return start_x;
    }
    
    public int get_y()
    {
        return start_y;
    }
    
    public int get_z()
    {
        return start_z;
    }
}
