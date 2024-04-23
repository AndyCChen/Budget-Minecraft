package minecraft;

public class Block {
    public enum BlockType {
        BlockType_Gravel,
        BlockType_Sand,
        BlockType_Water,
        BlockType_Dirt,
        BlockType_Stone,
        BlockType_Bedrock
    }

    private boolean IsActive;
    private BlockType type;
    
    public Block(BlockType type){
        this.type = type;
    }

    public boolean IsActive() {
        return IsActive;
    }
    public void SetActive(boolean active){
        IsActive=active;
    }
    public BlockType getBlockType(){
        return type;
    }
}