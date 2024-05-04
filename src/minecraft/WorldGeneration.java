package minecraft;

import static minecraft.Chunk.CHUNK_SIZE;
import minecraft.BlockTexture.BlockTextureType;
import java.util.Random;

public class WorldGeneration {
    private static final int SEA_LEVEL = 18;
    
    //private static final double [] ampl = new double[] {2, 1, 0.5, 0.25, 0.13};
    //private static final double [] freq = new double[] {0.001, 0.04, 0.05, 0.06, 0.07};
    private static final double [] ampl = new double[] {1, 0.5, 0.25};
    private static final double [] freq = new double[] {0.007, 0.01, 0.03};    
    private static final SimplexNoise_octave octaves[] = new SimplexNoise_octave[] {
        new SimplexNoise_octave( World.WORLD_SEED.nextInt() ),
        new SimplexNoise_octave( World.WORLD_SEED.nextInt() ),
        new SimplexNoise_octave( World.WORLD_SEED.nextInt() ),
        //new SimplexNoise_octave( World.WORLD_SEED.nextInt() ),
        //new SimplexNoise_octave( World.WORLD_SEED.nextInt() ),
    };
    
    final private static SimplexNoise_octave dirtNoise = new SimplexNoise_octave( World.WORLD_SEED.nextInt() );
    final private static SimplexNoise_octave sandNoise = new SimplexNoise_octave( World.WORLD_SEED.nextInt() );
    final private static SimplexNoise_octave BedRockNoise = new SimplexNoise_octave( World.WORLD_SEED.nextInt() );
    
    private WorldGeneration(){} // private contructor because this is a static class
    
    public static void generateTerrain(int start_x, int start_z, Block chunk_block[][][], int[][] heightMap)
    {
        // Calculate maximum height for each column based on noise
        
        
        for (int x = 0; x < CHUNK_SIZE; ++x) {
            for (int z = 0; z < CHUNK_SIZE; ++z) {
                // Using getNoise to generate height
                
                double nx = start_x * CHUNK_SIZE + x;
                double ny = start_z * CHUNK_SIZE + z;
                
                double noiseValue = 0;
                for (int i = 0; i < octaves.length; ++i)
                {
                    noiseValue += ampl[i] * (octaves[i].noise(freq[i] * nx, freq[i] * ny) );
                    
                }
                
                noiseValue = noiseValue / 2.0 + 0.5;
                noiseValue = Math.pow(Math.abs(noiseValue), 1.48);
                
                heightMap[x][z] = (int) Math.round( 7 + (noiseValue * 30) );
                if (heightMap[x][z] <= 0) heightMap[x][z] = 1;
                else if (heightMap[x][z] > World.WORLD_HEIGHT) heightMap[x][z] = World.WORLD_HEIGHT;
                for (int y = 0; y < World.WORLD_HEIGHT; ++y)
                {
                    if (y < heightMap[x][z])
                    {
                        chunk_block[x][y][z].setBlockType(BlockTextureType.Stone);
                    }
                    else
                    {
                        if (y < SEA_LEVEL)
                        {
                            chunk_block[x][y][z].setBlockType(BlockTextureType.Water);
                        }
                        else
                        {
                            chunk_block[x][y][z].setBlockType(BlockTextureType.Air);
                        }
                        
                    }
                }
            }
        }
    }
    
    public static void decorateTerrain(Block chunk_block[][][], int[][] heightMap)
    {
        Random random = new Random();
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int z = 0; z < CHUNK_SIZE; ++z)
            {
                int surface_y = heightMap[x][z] - 1 ;
                
                chunk_block[x][surface_y][z].setBlockType(BlockTextureType.Grass); // surface is grass
                
                double d = 5 * dirtNoise.noise(0.01 * x, 0.01 * z) / 2.0 + 0.5;
                int dirtVariation = (int) Math.round(d + 3);
                
                double s = sandNoise.noise(0.008 * x, 0.008 * z) / 2.0 + 0.5;
                int sandVariation = (int) Math.round(s + 2);
                
                for (int i = 0; i <= sandVariation; ++i)
                {
                    if (surface_y - i > 0 && surface_y <= WorldGeneration.SEA_LEVEL + sandVariation)
                    {
                        chunk_block[x][surface_y - i + 1][z].setBlockType(BlockTextureType.Sand);
                    }
                }
                
                for (int i = 0; i <= dirtVariation; ++i)
                {
                    if (surface_y - 2 - i >= 0 )
                        chunk_block[x][surface_y - 1 - i][z].setBlockType(BlockTextureType.Dirt);
                }
                
                double b = 2 *  BedRockNoise.noise(0.8 * x, 0.8 * z) / 2.0 + 0.5;
                int bedRockVariation = (int) Math.round(b + 1);
                
                for (int i = 0; i < bedRockVariation; ++i)
                {
                    chunk_block[x][i][z].setBlockType(BlockTextureType.Bedrock);
                }
                
                if (surface_y < World.WORLD_HEIGHT - 2 && chunk_block[x][surface_y][z].getBlockType() == BlockTextureType.Grass && 
                    chunk_block[x][surface_y + 1][z].getBlockType() == BlockTextureType.Air && //Ensures surface block is grass and block above the surface is air
                    (surface_y > 0 && chunk_block[x][surface_y - 1][z].getBlockType() != BlockTextureType.Sand)){
                    if(random.nextDouble() < 0.005) {  // chance to place a tree
                        Tree tree = new Tree(new Vector3f(x, surface_y + 1, z), 6, 3);
                        tree.generate(chunk_block);
                    }
                }
            }
        }
    }
}
