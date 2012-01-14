package org.getspout.testplugin.maze;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Stairs;
import org.bukkit.util.Vector;
import org.getspout.testplugin.Builder;

import java.util.logging.Logger;


public class StairBuilder {
  private static final Logger logger = Logger.getLogger(StairBuilder.class.getName());

  /**
   * Spiral staircase, left-handed (clockwise ascending)
   * @param location of starting stair block
   * @param stairWidth number of blocks wide the stairs are (>= 1)
   * @param centerWidth number of blocks in the center column (>= 0)
   * @param height (in blocks) or depth if negative)
   * @param startFace of initial stair block
   */
  public void buildStairs(Location location, int stairWidth, int centerWidth,
      int height, BlockFace startFace) {
    int totalHeight = 0;
    while(totalHeight < height) {
  /*    buildStairSegment(location, stairWidth, centerWidth, startFace);
      Vector offset = new Vector(startFace.getModX(), startFace.getModY(), startFace.getModZ());
      offset = offset.multiply(-centerWidth);
      offset.setY(offset.getY() + centerWidth); // watch for sign!
      buildLanding(location.clone().add(offset), , stairWidth);
   */ }

    Block b = location.getBlock();
    b.setType(Material.COBBLESTONE_STAIRS);
    Stairs stairs = new Stairs(Material.COBBLESTONE_STAIRS);
    stairs.setFacingDirection(startFace);
    b.setData(stairs.getData());
  }

  /**
   *
   * @param start if you stand here, looking up the stairs, you are are the outmost/left side of
   * the bottom.
   * @param orientation the direction of forward and up the stairs from start
   * @param width the number of blocks wide the stairs are
   * @param height the number of blocks forward/up the stairs are
   */
  public void buildStairSegment(Location start, Vector orientation, int width, int height) {
    Builder builder = new Builder();
    Vector across = builder.rotate(orientation.clone(), Builder.Rotation.CLOCK_1);
    logger.info("orientation: " + orientation + "\nacross: " + across);
    Location currentLocation = start.clone();
    for(int h = 0; h < height; h++) {
      Location rowLocation = currentLocation.clone();
      for(int w = 0; w < width; w++) {
        Block b = rowLocation.getBlock();
        b.setType(Material.STONE);
        rowLocation = rowLocation.add(across);
      }
      currentLocation = currentLocation.add(orientation);
    }
  }

  public void buildLanding(Location start, Vector orientation, int size) {

  }


}
