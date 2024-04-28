package minecraft;

// static class to track world state
public class World {
    public static final int WORLD_HEIGHT = 16;
    
    private static int player_block_pos_x = 0;
    private static int player_block_pos_z = 0;
    
    private static int player_chunk_pos_x = 0;
    private static int player_chunk_pos_z = 0;
    
    private static int northMostChunk = 0;
    private static int southMostChunk = 0;
    private static int westMostChunk = 0;
    private static int eastMostChunk = 0;
    private static int chunk_radius; // radius around chunk 0,0
    private static int world_dimension; //  number of chunks in x and z axis
    
    private static Chunk chunk_list[][];
    
    private World(){} // private constructor because this class is static
    
    public static void updatePlayerPosition_x(int x)
    {
        player_block_pos_x = -x;
        player_chunk_pos_x = (int) Math.floor( (float) player_block_pos_x / Chunk.CHUNK_SIZE );
    }
    
    public static void updatePlayerPosition_z(int z)
    {
        player_block_pos_z = -z;
        player_chunk_pos_z = (int) Math.floor( (float) player_block_pos_z / Chunk.CHUNK_SIZE );
    }
    
    public static int getPlayerPosition_x()
    {
        return player_block_pos_x;
    }
    
    public static int getPlayerPostion_z()       
    {
        return player_block_pos_z;
    }
    
    public static void createChunks(int radius)
    {
        chunk_radius = radius;
        northMostChunk = -radius;
        southMostChunk = radius;
        westMostChunk = -radius;
        eastMostChunk = radius;
        
        world_dimension = radius * 2 + 1;
        
        chunk_list = new Chunk[world_dimension][world_dimension];
        int x_pos = -radius;
        int z_pos = -radius;
        
        for (int i = 0; i < world_dimension; ++i)
        {  
            for (int j = 0; j < world_dimension; ++j)
            {
                chunk_list[i][j] = new Chunk(x_pos, 0, z_pos);
                z_pos += 1;
            }
            x_pos += 1;
            z_pos = -radius;
        }
    }
    
    public static void renderChunks()
    {
        for (int i = 0; i < world_dimension; ++i)
        {
            for (int j = 0; j < world_dimension; ++j)
            {
                chunk_list[i][j].render();
            }
        }
    }
    
    // generate new chunks as the player moves forward while removing chunks from the back
    public static void checkChunks()
    {
        /**
         * In general the chunks behind the camera will be rebuild to be infront of the player.
         * So for example the south chunks could be rebuild to now be the northmost chunk.
         * After this happens the chunks must be reordered in the chunks list by shifting by
         * 1 in the corresponding direction. So if the southmost chunk becomes the northmost chunk,
         * all chunks are shifted down in the 2d list and the southmost chunk is moved to the top.
         */
        
        // north
        if (northMostChunk - player_chunk_pos_z > -chunk_radius)
        {
            for (int x = 0; x < world_dimension; ++x)
            {
                int x_pos = chunk_list[x][world_dimension - 1].get_x();
                chunk_list[x][world_dimension - 1].rebuildMesh(x_pos, 0, northMostChunk - 1);
                Chunk southChunk = chunk_list[x][world_dimension - 1];
                
                for (int i = 0; i < world_dimension - 1; ++i)
                {
                    chunk_list[x][world_dimension - 1 - i] = chunk_list[x][world_dimension - 2 - i];
                }
                
                chunk_list[x][0] = southChunk;
            }
            northMostChunk -= 1;
            southMostChunk -= 1;
        }
        
        // south
        if (southMostChunk - player_chunk_pos_z < chunk_radius)
        {
            for (int x = 0; x < world_dimension; ++x)
            {
                int x_pos = chunk_list[x][0].get_x();
                chunk_list[x][0].rebuildMesh(x_pos, 0, southMostChunk + 1);
                Chunk northChunk = chunk_list[x][0];
                
                for (int i = 0; i < world_dimension - 1; ++i)
                {
                    chunk_list[x][i] = chunk_list[x][i + 1];
                }
                
                chunk_list[x][world_dimension - 1] = northChunk;
            }
            northMostChunk += 1;
            southMostChunk += 1;
        }
        
        // west
        if (westMostChunk - player_chunk_pos_x > -chunk_radius)
        {
            for (int z = 0; z < world_dimension; ++z)
            {
                int z_pos = chunk_list[world_dimension - 1][z].get_z();
                chunk_list[world_dimension - 1][z].rebuildMesh(westMostChunk - 1, 0, z_pos);
                Chunk westChunk = chunk_list[world_dimension - 1][z];
                
                for (int i = 0; i < world_dimension - 1; ++i)
                {
                    chunk_list[world_dimension - i - 1][z] = chunk_list[world_dimension - i - 2][z];
                }
                
                chunk_list[0][z] = westChunk;
            }
            westMostChunk -= 1;
            eastMostChunk -= 1;
        }
        
        // east
        if (eastMostChunk - player_chunk_pos_x < chunk_radius)
        {
            for (int z = 0; z < world_dimension; ++z)
            {
                int z_pos = chunk_list[0][z].get_z();
                chunk_list[0][z].rebuildMesh(eastMostChunk + 1, 0, z_pos);
                Chunk westChunk = chunk_list[0][z];
                
                for (int i = 0; i < world_dimension - 1; ++i)
                {
                    chunk_list[i][z] = chunk_list[i + 1][z];
                }
                
                chunk_list[world_dimension - 1][z] = westChunk;
            }
            westMostChunk += 1;
            eastMostChunk += 1;
        }
    }
    
    public static void printCoor()
    {
        System.out.print("Block XZ: " + player_block_pos_x + " " + player_block_pos_z );
        System.out.println(" Chunk XZ: " + player_chunk_pos_x + " " + player_chunk_pos_z);  
    }
}
