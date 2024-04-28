package minecraft;

// static class to track world state
public class World {
    public static final int WORLD_HEIGHT = 16;
    
    private static int player_block_pos_x = 0;
    private static int player_block_pos_z = 0;
    
    private static int player_chunk_pos_x = 0;
    private static int player_chunk_pos_z = 0;
    
    private static Chunk chunk_list[][];
    
    private World(){} // private constructor because this class is static
    
    public static void updatePlayerPosition_x(int x)
    {
        player_block_pos_x = x;
        player_chunk_pos_x = player_block_pos_x >= 0 ? player_block_pos_x / Chunk.CHUNK_SIZE : (int) Math.floor( (float) player_block_pos_x / Chunk.CHUNK_SIZE );
    }
    
    public static void updatePlayerPosition_z(int z)
    {
        player_block_pos_z = z;
        player_chunk_pos_z = player_block_pos_z >= 0 ? player_block_pos_z / Chunk.CHUNK_SIZE : (int) Math.floor( (float) player_block_pos_z / Chunk.CHUNK_SIZE );
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
        chunk_list = new Chunk[radius*2][radius*2];
        int x_pos = -radius;
        int z_pos = -radius;
        
        for (int i = 0; i < radius * 2; ++i)
        {  
            for (int j = 0; j < radius * 2; ++j)
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
        for (int i = 0; i < chunk_list.length; ++i)
        {
            for (int j = 0; j < chunk_list[0].length; ++j)
            {
                chunk_list[i][j].render();
            }
        }
    }
    
    public static void printCoor()
    {
        System.out.print("Block XZ: " + player_block_pos_x + " " + player_block_pos_z );
        System.out.println(" Chunk XZ: " + player_chunk_pos_x + " " + player_chunk_pos_z);
    }
}
