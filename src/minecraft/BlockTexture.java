package minecraft;

import java.io.IOException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import static org.lwjgl.opengl.GL11.*;

public final class BlockTexture {
     public enum BlockTextureType {
        Air,
        Grass,
        Sand,
        Water,
        Dirt,
        Stone,
        Bedrock,
    }
    
    private static Texture texture;
    
    private BlockTexture() {} // private constructor
    
    public static void loadTextures()
    {
        try
        {
            texture = TextureLoader.getTexture( "PNG", ResourceLoader.getResourceAsStream("/res/jolicraft.png"), true );
            texture.setTextureFilter(GL_NEAREST); // get that 16 by 16 pixel art style look
        }
        catch(IOException e)
        {
            System.err.println("Error loading texture: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Texture getTexture()
    {
        return texture;
    }
}
