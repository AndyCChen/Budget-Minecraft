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
    
    public Block(int x, int y, int z){
        this.type = BlockTextureType.Air;
    }
    
    public boolean isBlockAir()
    {
        return this.type == BlockTextureType.Air;
    }

    public BlockTextureType getBlockType(){
        return type;
    }
    
    public void setBlockType(BlockTextureType type)
    {
        this.type = type;
    }
}