package org.getspout.testplugin.maze;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class PieceBuilder<E extends Enum<E>> {
  private static final Logger logger = Logger.getLogger(PieceBuilder.class.getName());
  private final List<? extends Piece2D<E>> pieces;
  private final World world;
  private final Piece2D[][] maze;
  private final Random random;

  public PieceBuilder(World world, List<? extends Piece2D<E>> choices, int xSize, int zSize, long randSeed) {
    this.world = world;
    this.pieces = choices;
    this.maze = new Piece2D[xSize][];
    for(int x = 0; x < xSize; x++) {
      maze[x] = new Piece2D[zSize];
    }
    this.random = new Random(randSeed);
  }

  public void buildPiece(Piece2D piece, Location location) {
    logger.info("building " + piece +  "\nat: " + location.toString());
    for(int x = 0; x < piece.getXSize(); x++) {
      for(int y = 0; y < piece.getYSize(); y++) {
        for(int z = 0; z < piece.getZSize(); z++) {
          Material m = piece.getMaterialAt(x, y, z);
          location.clone().add(x, y, z).getBlock().setType(m);
        }
      }
    }
  }

  public void buildMaze(Location origin) {
    int xPieceSize = maze.length;
    int zPieceSize = maze[0].length;
    logger.info(String.format("Building maze[%d][%d] at location: %s", xPieceSize, zPieceSize,
        origin.toString()));
    for(int x = 0; x < xPieceSize; x++) {
      for(int z = 0; z < zPieceSize; z++) {
        Piece2D<E> north = x == 0 ? null : maze[x - 1][z];
        Piece2D<E> south = x == xPieceSize - 1 ? null : maze[x + 1][z];
        Piece2D<E> west = z == zPieceSize - 1 ? null : maze[x][z + 1];
        Piece2D<E> east = z == 0 ? null : maze[x][z - 1];
        List<Piece2D<E>> choices = Lists.newArrayList();
        logger.info("N: " + north + ", E: " + east + ", S: " + south + ", W: " + west);
        for(Piece2D<E> piece : pieces) {
          if(isAllowedBlock(piece, north, south, west, east)) {
            logger.info("allowed: " + piece);
            choices.add(piece);
          } else {
            logger.info("disallowed: " + piece);
          }
        }
        if(!choices.isEmpty()) {
          Piece2D<E> choice = choices.get(random.nextInt(choices.size()));
          buildPiece(choice, origin.clone().add(x * (choice.getXSize()), 0, z * (choice.getZSize())));
          maze[x][z] = choice;
        } else {
          logger.info("Unable to pick a piece for spot: " + origin.toString());
        }
      }
    }
  }

  public static <E extends Enum<E>> boolean isAllowedBlock(Piece2D<E> piece,
      Piece2D<E> north, Piece2D<E> south, Piece2D<E> west, Piece2D<E> east) {
    return (north == null || north.getFaceType(BlockFace.SOUTH) == piece.getFaceType(BlockFace.NORTH)) &&
            (south == null || south.getFaceType(BlockFace.NORTH) == piece.getFaceType(BlockFace.SOUTH)) &&
            (west == null || west.getFaceType(BlockFace.EAST) == piece.getFaceType(BlockFace.WEST)) &&
            (east == null || east.getFaceType(BlockFace.WEST) == piece.getFaceType(BlockFace.EAST));
  }
}
