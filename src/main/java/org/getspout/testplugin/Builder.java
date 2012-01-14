package org.getspout.testplugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Builder {
  private static final Logger logger = Logger.getLogger(Builder.class.getName());
  
  public static enum Dim {
    X(1, 0, 0), Y(0, 1, 0), Z(0, 0, 1), F(0, 0 ,0);
    private int x,y,z;
    private Dim(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    public Vector toVector() {
      return new Vector(x, y, z);
    }
  }

  private final Map<Player, List<Block>> playersMarks = Maps.newHashMap();
  private final Map<Player, Material> markMaterials = Maps.newHashMap();
  private final Map<Player, Material> altMaterials = Maps.newHashMap();
  private final Map<Player, Material> buildMaterials = Maps.newHashMap();
  private final Map<Player, List<List<BlockState>>> workHistories = Maps.newHashMap();
  private final Map<Player, Integer> undoOffset = Maps.newHashMap();
  private final Map<Player, Map<String, List<BlockState>>> scratchPad = Maps.newHashMap();
  private final Map<Player, Map<String, Block>> scratchMark = Maps.newHashMap();

  public void mark(Player player, Command command, String label, String[] args) {
    Block targetBlock = player.getTargetBlock(null, 1000);
    Material material = targetBlock.getType();
    logger.info(Arrays.toString(args));
    Builder.Dim dim;
    int offset;
    if(args.length > 0) {
      if(args[0].equalsIgnoreCase("clear")) {
        clearMarks(player);
        return;
      } else if(args[0].equalsIgnoreCase("with")) {
        if(args.length == 3) {
          setMaterials(player, Material.matchMaterial(args[1]), Material.matchMaterial(args[2]));
        }
        return;
      } else if((dim = Builder.Dim.valueOf(args[0].toUpperCase())) != null) {
        offset = Integer.parseInt(args[1]);
        Location location = targetBlock.getLocation();
        if(dim == Dim.F) {
          double[] offsets = getOffset(player, targetBlock);
          dim = getForward(offsets);
          offset *= getForwardSign(offsets, dim);
        }
        switch (dim) {
          case X:
            location.setX(location.getX() + offset);
            break;
          case Y:
            location.setY(location.getY() + offset);
            break;
          case Z:
            location.setZ(location.getZ() + offset);
            break;
          default:
            // impossible?
        }
        targetBlock = targetBlock.getWorld().getBlockAt(location);
        material = targetBlock.getType();
      } else {
        material = Material.matchMaterial(args[0]);
      }
    }
    Material markMaterial = getMarkMaterial(player);
    if(markMaterial != null && !markMaterial.equals(material)) {
      targetBlock.setType(markMaterial);
    } else {
      markMaterial = getAltMaterial(player);
      if(markMaterial != null && !markMaterial.equals(material)) {
        targetBlock.setType(markMaterial);
      }
    }
    createMark(player, targetBlock, null);
  }

  public static double[] getOffset(Player player, Block targetBlock) {
    Location tLoc = targetBlock.getLocation();
    Location pLoc = player.getEyeLocation();
    double[] offsets = new double[3];
    offsets[0] = tLoc.getX() - pLoc.getX();
    offsets[1] = tLoc.getY() - pLoc.getY();
    offsets[2] = tLoc.getZ() - pLoc.getZ();
    return offsets;
  }

  private Dim getForward(Vector v) {
    return getForward(new double[] {v.getX(), v.getY(), v.getZ()});
  }

  public static Dim getForward(double[] offsets) {
    int bigIdx = 0;
    double big = Math.abs(offsets[0]);
    for(int i = 0; i < offsets.length; i++) {
      if(Math.abs(offsets[i]) > big) {
        big = Math.abs(offsets[i]);
        bigIdx = i;
      }
    }
    return Dim.values()[bigIdx];
  }

  private int getForwardSign(Vector offsets, Dim dim) {
    return getForwardSign(new double[] {offsets.getX(), offsets.getY(), offsets.getY()}, dim);
  }

  private int getForwardSign(double[] offsets, Dim dim) {
    return offsets[dim.ordinal()] > 0 ? 1 : -1;
  }

  private static BlockFace directionOf(Vector vector) {
    double maxCosAngle = -2;
    BlockFace directionFace = null;
    for(BlockFace face : BlockFace.values()) {
      double cosAngle = vector.dot(new Vector(face.getModX(), face.getModY(), face.getModZ()));
      if(cosAngle > maxCosAngle) {
        maxCosAngle = cosAngle;
        directionFace = face;
      }
    }
    return directionFace;
  }

  public Vector rotate(Vector v, Rotation rotation) {
    switch (rotation) {
      case CLOCK_1:
        return rotateClockwise(v);
      case CLOCK_2:
        return rotateClockwise(rotateClockwise(v));
      case CLOCK_3:
        return rotateClockwise(rotateClockwise(rotateClockwise(v)));
      case UP:
        return rotateUp(v);
      case UP_CLOCK_1:
        return rotateUp(rotateClockwise(v));
      case UP_CLOCK_2:
        return rotateUp(rotateClockwise(rotateClockwise(v)));
      case UP_CLOCK_3:
        return rotateUp(rotateClockwise(rotateClockwise(rotateClockwise(v))));
      case DOWN:
        return rotateDown(v);
      case DOWN_CLOCK_1:
        return rotateDown(rotateClockwise(v));
      case DOWN_CLOCK_2:
        return rotateDown(rotateClockwise(rotateClockwise(v)));
      case DOWN_CLOCK_3:
        return rotateDown(rotateClockwise(rotateClockwise(rotateClockwise(v))));
      case IDENTITY:
      default:
        return v;
    }
  }

  private Vector rotateDown(Vector v) {
    double y = v.getY();
    double z = v.getZ();
    return v.setZ(y).setY(-z);
  }

  private Vector rotateUp(Vector v) {
    double y = v.getY();
    double z = v.getZ();
    return v.setZ(-y).setY(z);
  }

  private Vector rotateClockwise(Vector v) {
    double x = v.getX();
    double z = v.getZ();
    return v.setX(z).setZ(-x);
  }


  public static enum Rotation {
    IDENTITY,
    CLOCK_1,
    CLOCK_2,
    CLOCK_3,
    UP,
    UP_CLOCK_1,
    UP_CLOCK_2,
    UP_CLOCK_3,
    DOWN,
    DOWN_CLOCK_1,
    DOWN_CLOCK_2,
    DOWN_CLOCK_3;

    public static Rotation between(Vector start, Vector finish) {
      return null;
    }
  }

  public void createMark(Player player, Block block, String name) {
    logger.info(player.getDisplayName() + " marking block at " + block.getLocation());
    if(!playersMarks.containsKey(player)) {
      playersMarks.put(player, new ArrayList<Block>());
    }
    playersMarks.get(player).add(block);
  }

  public void clearMarks(Player player) {
    playersMarks.remove(player);
  }

  public void setMaterials(Player player, Material markMaterial, Material altMaterial) {
    logger.info("Setting default mark material to: " + markMaterial + ", and alternate material to: " + altMaterial);
    markMaterials.put(player, markMaterial);
    altMaterials.put(player, altMaterial);
  }

  public Material getMarkMaterial(Player player) {
    return markMaterials.get(player);
  }

  public Material getAltMaterial(Player player) {
    return altMaterials.get(player);
  }

  public Material getBuildMaterial(Player player) {
    return buildMaterials.get(player);
  }

  public void setBuildMaterial(Player player, Material material) {
    buildMaterials.put(player, material);
  }

  public List<Block> getMarks(Player player) {
    return playersMarks.get(player);
  }

  public void undo(Player player) {
    if(!undoOffset.containsKey(player) || undoOffset.get(player) < 0) {
      player.sendMessage("At beginning of undo history, can't undo!");
      return;
    }
    int offset = undoOffset.get(player);
    List<List<BlockState>> history = workHistories.get(player);
    if(history.size() - 1 < offset) {
      offset = history.size() - 1;
      undoOffset.put(player, offset);
    }
    List<BlockState> oldState = history.get(offset);
    replay(player.getWorld(), oldState);
    undoOffset.put(player, ++offset);
  }

  public void replay(World world, List<BlockState> oldState) {
    replay(world, oldState, 0, 0, 0);
  }

  public void replay(World world, List<BlockState> oldState, int xoff, int yoff, int zoff) {
    for(BlockState blockState : oldState) {
      Block block = world.getBlockAt(blockState.getX() + xoff, blockState.getY() + yoff, blockState.getZ() + zoff);
      block.setType(blockState.getType());
    }
  }

  public void redo(Player player) {
    if(!undoOffset.containsKey(player) || undoOffset.get(player) >= workHistories.get(player).size() - 1) {
      player.sendMessage("At end of undo history, no more to redo!");
      return;
    }
    int offset = undoOffset.get(player);
    List<List<BlockState>> history = workHistories.get(player);
    List<BlockState> oldState = history.get(++offset);
    replay(player.getWorld(), oldState);
    undoOffset.put(player, offset);
  }

  public void copy(Player player, String name) {
    if(!scratchPad.containsKey(player)) {
      scratchPad.put(player, Maps.<String, List<BlockState>>newHashMap());
      scratchMark.put(player, Maps.<String, Block>newHashMap());
    }
    scratchPad.get(player).put(name, captureState(getMarks(player)));
    scratchMark.get(player).put(name, getMarks(player).get(0));
  }

  public void paste(Player player, String name) {
    Block pasteMark = getMarks(player).get(0);
    Block scratch = scratchMark.get(player).get(name);
    replay(pasteMark.getWorld(), scratchPad.get(player).get(name),
        pasteMark.getX() - scratch.getX(),
        pasteMark.getY() - scratch.getY(),
        pasteMark.getZ() - scratch.getZ());
    clearMarks(player);
  }

  /**
   * for example:
   * 122, 41, 34
   * 125, 20, 34
   * @param player
   * @param material
   */
  public void buildBasedOnMarks(Player player, Material material) {
    List<Block> markedBlocks = getMarks(player);
    List<BlockState> oldState = captureState(markedBlocks);
    if(!workHistories.containsKey(player)) {
      List<BlockState> empty = Lists.newArrayList();
      List<List<BlockState>> workHistory = Lists.newArrayList();
      workHistory.add(empty);
      workHistories.put(player, workHistory);
      undoOffset.put(player, -1);
    }
    World world = player.getWorld();
    for(BlockState blockState : oldState) {
      Block block = world.getBlockAt(blockState.getX(), blockState.getY(), blockState.getZ());
      block.setType(material);
    }
    List<List<BlockState>> workHistory = workHistories.get(player);
    if(undoOffset.get(player) < workHistory.size() - 1) {
      workHistory = workHistory.subList(0, undoOffset.get(player) + 1);
    }
    workHistory.add(oldState);
    undoOffset.put(player, undoOffset.get(player) + 1);
    workHistories.put(player, workHistory);
  }

  private List<BlockState> captureState(List<Block> markedBlocks) {
    if(markedBlocks == null || markedBlocks.size() < 2) {
      throw new RuntimeException("Invalid blocks supplied: " + markedBlocks);
    }
    World world = markedBlocks.get(0).getWorld();
    Map<Dim, Integer> lowCorners = lowest(markedBlocks);
    Map<Dim, Integer> highCorners = highest(markedBlocks);
    logger.info("low corners: " + toString(lowCorners));
    logger.info("high corners: " + toString(highCorners));
    for(Dim d : Dim.values()) {
      if(lowCorners.get(d) > highCorners.get(d)) {
        throw new IllegalArgumentException("There are low corners higher than high corners!");
      }
    }
    List<BlockState> oldState = Lists.newArrayList();
    for(int x = lowCorners.get(Dim.X); x <= highCorners.get(Dim.X); x++) {
      for(int y = lowCorners.get(Dim.Y); y <= highCorners.get(Dim.Y); y++) {
        for(int z = lowCorners.get(Dim.Z); z <= highCorners.get(Dim.Z); z++) {
          Block block = world.getBlockAt(x, y, z);
          oldState.add(block.getState());
        }
      }
    }
    return oldState;
  }

  private static String toString(Map<?, ?> map) {
    String s = "{";
    for(Map.Entry e : map.entrySet()) {
      s += e.getKey() + " => " + e.getValue() + ",";
    }
    if(s.length() > 1) {
      s = s.substring(0, s.length() - 2);
    }
    return s + "}";
  }
  

  private Map<Dim,Integer> highest(List<Block> markedBlocks) {
    return edge(markedBlocks, false);
  }

  private Map<Dim,Integer> lowest(List<Block> markedBlocks) {
    return edge(markedBlocks, true);
  }


  private Map<Dim,Integer> edge(List<Block> markedBlocks, boolean low) {
    Map<Dim, Integer> m = new EnumMap<Dim, Integer>(Dim.class);
    for(Dim dim : Dim.values()) {
      int edge = low ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      for(Block block : markedBlocks) {
        edge = low ? Math.min(edge, get(block, dim)) : Math.max(edge, get(block, dim));
      }
      m.put(dim, edge);
    }
    return m;
  }

  private static int get(Block block, Dim dim) {
    switch (dim) {
      case X:
        return block.getX();
      case Y:
        return block.getY();
      case Z:
        return block.getZ();
      default:
        return -1;
    }
  }
}
