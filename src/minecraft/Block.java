package minecraft;

import minecraft.BlockTexture.BlockTextureType;

public class Block {
    public enum BlockFaces {
        Back,
        Front,
        Left,
        Right,
        Bottom,
        Top,
    }
    
    private BlockTextureType type;
    private boolean isActive;
    private int x, y, z;
    
    public Block(BlockTextureType type, int x, int y, int z){
        this.type = type;
        this.isActive = false;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int getX()
    {
        return x;
    }
    
     public int getY()
    {
        return y;
    }
    
     public int getZ()
    {
        return z;
    }
     
    public boolean getBlockState()
    {
        return isActive;
    }
    
    public void setBlockState(boolean isActive)
    {
        this.isActive = isActive;
    }

    public BlockTextureType getBlockType(){
        return type;
    }
}