package org.getspout.testplugin.maze;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/***
 *  only 16 different basic structures:
 *   o   x      x      x       x      x
 *  o o o o(4) o x(4) o x(4)  o o(2) x x
 *   o   o      o      x       x      x
 *
 *        0 [0 1 2 3 ... xSize-1]
 *        1 [0 1 2 3 ... xSize-1]
 *   ...
 * ySize-1  [0 1 2 3 ... xSize-1]
 */
public class Simple2DPiece extends Piece2D<Simple2DEdge> {
  private static final Logger logger = Logger.getLogger(Simple2DPiece.class.getName());
  private final Material material;
  private final Map<BlockFace, Simple2DEdge> configuration;

  public Simple2DPiece(int xSize, int ySize, int zSize, Material material,
      Simple2DEdge north, Simple2DEdge east, Simple2DEdge south, Simple2DEdge west) {
    super(xSize, ySize, zSize);
    this.material = material;
    configuration = Maps.newEnumMap(BlockFace.class);
    configuration.put(BlockFace.NORTH, north);
    configuration.put(BlockFace.EAST, east);
    configuration.put(BlockFace.SOUTH, south);
    configuration.put(BlockFace.WEST, west);
  }

  public static List<Simple2DPiece> allPossible(int size, Material material) {
    return allPossible(size, size, size, material);
  }

  public static List<Simple2DPiece> allPossible(int xSize, int ySize, int zSize, Material material) {
    List<Simple2DPiece> pieces = Lists.newArrayList();
    List<boolean[]> allChoices = getAllChoices();
    for(boolean[] choice : allChoices) {
      pieces.add(new Simple2DPiece(xSize, ySize, zSize, material,
          choice[0] ? Simple2DEdge.OPEN : Simple2DEdge.CLOSED,
          choice[1] ? Simple2DEdge.OPEN : Simple2DEdge.CLOSED,
          choice[2] ? Simple2DEdge.OPEN : Simple2DEdge.CLOSED,
          choice[3] ? Simple2DEdge.OPEN : Simple2DEdge.CLOSED));
    }
    return pieces;
  }

  private static List<boolean[]> getAllChoices() {
    List<boolean[]> choices = Lists.newArrayList();
    for(int i = 0; i < 16; i++) {
      choices.add(new boolean[] {(8 & i) != 0, (4 & i) != 0, (2 & i) != 0, (1 & i) != 0});
    }
    return choices;
  }

  @Override
  public Simple2DEdge getFaceType(BlockFace face) {
    return configuration.containsKey(face) ? configuration.get(face) : Simple2DEdge.CLOSED;
  }

  public boolean isFullyClosed() {
    return Simple2DEdge.CLOSED == getFaceType(BlockFace.NORTH) &&
           Simple2DEdge.CLOSED == getFaceType(BlockFace.EAST) &&
           Simple2DEdge.CLOSED == getFaceType(BlockFace.SOUTH) &&
           Simple2DEdge.CLOSED == getFaceType(BlockFace.WEST);
  }

  @Override
  public Material getMaterialAt(int xOffset, int yOffset, int zOffset) {
    // floors and ceilings
    if(yOffset == 0 || yOffset == getYSize() - 1) {
      return material;
    }
    //debug: remove later
    if(yOffset == getYSize() - 1) {
      return Material.AIR;
    }
    if(isFullyClosed()) {
      return material;
    }
    // middle hallways
    if(xOffset > 0 && xOffset < getXSize() - 1 && zOffset > 0 && zOffset < getZSize() - 1) {
      return Material.AIR;
    }
    if((xOffset == 0 && (zOffset == 0 || zOffset == getZSize() - 1) ||
       (xOffset == getXSize() - 1 && (zOffset == 0 || zOffset == getZSize() - 1)))) {
      return material;
    }
    Simple2DEdge edge = null;
    BlockFace face = null;
    if(xOffset == 0) {
      face = BlockFace.NORTH;
    } else if (xOffset == getXSize() - 1) {
      face = BlockFace.SOUTH;
    } else if (zOffset == 0) {
      face = BlockFace.EAST;
    } else if (zOffset == getZSize() - 1) {
      face = BlockFace.WEST;
    }
    if(face != null) {
      edge = getFaceType(face);
    }
    if(edge != null) {
     // logger.info("onFace: " + face);
    }
    return edge == null || edge == Simple2DEdge.OPEN ? Material.AIR : material;
  }
}
