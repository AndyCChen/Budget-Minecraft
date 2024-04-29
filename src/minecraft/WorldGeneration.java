package minecraft;

import java.util.Random;
import static minecraft.Chunk.CHUNK_SIZE;
import minecraft.BlockTexture.BlockTextureType;

public class WorldGeneration {
    private static final int SEA_LEVEL = 10;
    
    private static final double [] ampl = new double[] {1, 0.5, 0.25};
    private static final double [] freq = new double[] {0.009, 0.018, 0.036};
    
    private static final SimplexNoise_octave octaves[] = new SimplexNoise_octave[] {
        new SimplexNoise_octave( World.WORLD_SEED.nextInt() ),
        new SimplexNoise_octave( World.WORLD_SEED.nextInt() ),
        new SimplexNoise_octave( World.WORLD_SEED.nextInt() ),
    };
    
    private WorldGeneration(){} // private contructor because this is a static class
    
    public static void generateTerrain(int start_x, int start_z, Block chunk_block[][][], int[][] heightMap)
    {
        // Calculate maximum height for each column based on noise
        
        
        for (int x = 0; x < CHUNK_SIZE; ++x) {
            for (int z = 0; z < CHUNK_SIZE; ++z) {
                // Using getNoise to generate height
                
                double nx = start_x * CHUNK_SIZE + x;
                double ny = start_z * CHUNK_SIZE + z;
                
                double noiseValue = 0, amplSum = 0;
                for (int i = 0; i < octaves.length; ++i)
                {
                    noiseValue += ampl[i] * (octaves[i].noise(freq[i] * nx, freq[i] * ny) / 2.0 + 0.5);
                    amplSum += ampl[i];
                }
                noiseValue /= amplSum;
                noiseValue = Math.pow(noiseValue, 1.8);
                
                heightMap[x][z] = (int) Math.round(noiseValue * (World.WORLD_HEIGHT - 1) + 5);
                if (heightMap[x][z] == 0) heightMap[x][z] = 1;
                else if (heightMap[x][z] > World.WORLD_HEIGHT) heightMap[x][z] -= 1;
                
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
        SimplexNoise_octave dirtNoise = new SimplexNoise_octave( World.WORLD_SEED.nextInt() );
        SimplexNoise_octave BedRockNoise = new SimplexNoise_octave( World.WORLD_SEED.nextInt() );
        
        for (int x = 0; x < CHUNK_SIZE; ++x)
        {
            for (int z = 0; z < CHUNK_SIZE; ++z)
            {
                int surface_y = heightMap[x][z];
                
                chunk_block[x][surface_y - 1][z].setBlockType(BlockTextureType.Grass); // surface is grass
                
                double d = 5 * dirtNoise.noise(0.01 * x, 0.01 * z) / 2.0 + 0.5;
                int dirtVariation = (int) Math.round(d + 3);
                
                for (int i = 0; i <= dirtVariation; ++i)
                {
                    if (surface_y - 2 - i >= 0)
                        chunk_block[x][surface_y - 2 - i][z].setBlockType(BlockTextureType.Dirt);
                }
                
                double b = 2 *  BedRockNoise.noise(0.8 * x, 0.8 * z) / 2.0 + 0.5;
                int bedRockVariation = (int) Math.round(b + 1);
                
                for (int i = 0; i < bedRockVariation; ++i)
                {
                    chunk_block[x][i][z].setBlockType(BlockTextureType.Bedrock);
                }
            }
        }
    }
}
