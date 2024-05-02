package minecraft;

import java.io.IOException;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.opengl.PNGDecoder;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

public final class BlockTexture {
    public enum BlockAlphaType {
        Transparent,
        Opaque,
    }
    
     public enum BlockTextureType {
        Air,
        Grass,
        Sand,
        Water,
        Dirt,
        Stone,
        Bedrock,
    }
    
    private static int textureID;
    
    private BlockTexture() {} // private constructor
    
    public static void loadTextures()
    {
        try
        {
            PNGDecoder textureAtlas = new PNGDecoder(ResourceLoader.getResourceAsStream("/res/jolicraft.png"));
            ByteBuffer buffer = BufferUtils.createByteBuffer(4 * textureAtlas.getWidth() * textureAtlas.getHeight());
            textureAtlas.decode(buffer, textureAtlas.getWidth() * 4, PNGDecoder.RGBA);
            buffer.flip();
            textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureAtlas.getWidth(), textureAtlas.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }
        catch(IOException e)
        {
            System.err.println("Error loading texture: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
    
    public static int getTextureID()
    {
        return textureID;
    }
}
