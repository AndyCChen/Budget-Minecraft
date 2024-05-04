package minecraft;

import minecraft.BlockTexture.BlockTextureType;

public class Tree {
    private Vector3f position; // Position using Vector3f
    private int height;
    private int leavesRadius;

    public Tree(Vector3f position, int height, int leavesRadius) {
        this.position = position;
        this.height = height;
        this.leavesRadius = leavesRadius;
    }

    public void generate(Block[][][] chunk) {
        // Generate the trunk
        for (int y = 0; y < height; y++) {
            int x = Math.round(position.x);
            int z = Math.round(position.z);
            int newY = Math.round(position.y + y);
            if (x >= 0 && x < chunk.length && newY >= 0 && newY < chunk[x].length && z >= 0 && z < chunk[x][newY].length) {
                chunk[x][newY][z] = new Block(x, newY, z); // Create block first
                chunk[x][newY][z].setBlockType(BlockTextureType.Wood); // Then set the block type
            }
        }

        // Generate the leaves
        int startY = Math.round(position.y + height);
        for (int dy = -leavesRadius; dy <= leavesRadius; dy++) {
            for (int dx = -leavesRadius; dx <= leavesRadius; dx++) {
                for (int dz = -leavesRadius; dz <= leavesRadius; dz++) {
                    if (Math.sqrt(dx * dx + dy * dy + dz * dz) <= leavesRadius) {
                        int nx = Math.round(position.x + dx);
                        int ny = startY + dy;
                        int nz = Math.round(position.z + dz);
                        if (nx >= 0 && nx < chunk.length && ny >= 0 && ny < chunk[nx].length && nz >= 0 && nz < chunk[nx][ny].length) {
                            chunk[nx][ny][nz] = new Block(nx, ny, nz); // Create block
                            chunk[nx][ny][nz].setBlockType(BlockTextureType.Leaf); // Set block type
                        }
                    }
                }
            }
        }
    }
}




