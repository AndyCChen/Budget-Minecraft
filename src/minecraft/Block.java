package minecraft;

import minecraft.BlockTexture.BlockTextureType;

public class Block {
    private boolean IsActive;
    private BlockTextureType type;
    
    public Block(BlockTextureType type){
        this.type = type;
    }

    public boolean IsActive() {
        return IsActive;
    }
    public void SetActive(boolean active){
        IsActive=active;
    }
    public BlockTextureType getBlockType(){
        return type;
    }
}