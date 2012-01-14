package org.getspout.testplugin.maze;

// 
//
//
//
//

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public abstract class Piece2D<E extends Enum<E>> implements Cloneable {

  private final int xSize, ySize, zSize;

  public Piece2D(int xSize, int ySize, int zSize) {
    this.xSize = xSize;
    this.ySize = ySize;
    this.zSize = zSize;
  }

  public abstract E getFaceType(BlockFace face);

  public abstract Material getMaterialAt(int xOffset, int yOffset, int zOffset);

  public final int getXSize() {
    return xSize;
  }

  public final int getYSize() {
    return ySize;
  }

  public final int getZSize() {
    return zSize;
  }

  public String toString() {
    String s = "{";
    for(BlockFace face : BlockFace.values()) {
      if(face != BlockFace.NORTH && face != BlockFace.SOUTH && face != BlockFace.EAST && face != BlockFace.WEST) {
        continue;
      }
      if(getFaceType(face) != null) {
        s += face.toString().charAt(0) + ":" + getFaceType(face).toString().charAt(0) + ",";
      }
    }
    return s + "}";
  }

  public final BlockFace getFaceFor(Vector vector) {
    int i = 0;
    double maxVal = vector.getX();
    if(Math.abs(vector.getY()) > Math.abs(maxVal)) {
      maxVal = vector.getY();
      i = 1;
    }
    if(Math.abs(vector.getZ()) > Math.abs(maxVal)) {
      maxVal = vector.getZ();
      i = 2;
    }
    BlockFace face = null;
    if(i == 0) {
      face = BlockFace.NORTH;
    } else if (i == 1) {
      face = BlockFace.UP;
    } else if (i == 2) {
      face = BlockFace.EAST;
    }
    return maxVal < 0 ? face.getOppositeFace() : face;
  }
}
