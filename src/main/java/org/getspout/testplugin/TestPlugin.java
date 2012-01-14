package org.getspout.testplugin;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.testplugin.maze.PieceBuilder;
import org.getspout.testplugin.maze.Simple2DEdge;
import org.getspout.testplugin.maze.Simple2DPiece;
import org.getspout.testplugin.maze.StairBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TestPlugin extends JavaPlugin {
    private static final Logger logger = Logger.getLogger(TestPlugin.class.getName());

    public static enum BoundKeys {
      TOP_CAT(Keyboard.KEY_C, "Changes global skin to topcat."),
      PARTY_TIME(Keyboard.KEY_M, "Creates a Party Test Notification"),
      TEST_SCREENIE(Keyboard.KEY_B, "Tests screenshot grabbing"),
      LIST_TEST(Keyboard.KEY_L, "List Test"),
      RECT(Keyboard.KEY_N, "Builds a rectangular solid shell");

      private final Keyboard key;
      private final String desc;
      BoundKeys(Keyboard key, String desc) {
        this.key = key;
        this.desc = desc;
      }
      public Keyboard getKey() {
        return key;
      }
      public String getDesc() {
        return desc;
      }
    }

    public static enum Commands {
      BUILD,
      MAZE,
      MARK,
      COPY,
      PASTE,
      UNDO,
      REDO,
      STAIR
    }

    private static final int id = 8;

	public static CustomBlock testBlock;
	public static TestPlugin instance;

	public void onDisable() {
		Bukkit.getLogger().log(Level.INFO, "[Spout Test Plugin " + id + "] Disabled!");
	}

	public void onEnable() {
		instance = this;

		testBlock = new TestBlock(this);
		
		getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, new TestSpoutListener(), Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, new TestScreenListener(), Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, new TestBlockListener(), Priority.Normal, this);

		TestKeyBinding binding = new TestKeyBinding();
        for(BoundKeys boundKey : BoundKeys.values()) {
            logger.info("binding key " + boundKey);
            SpoutManager.getKeyBindingManager().registerBinding(boundKey.name(),
                boundKey.getKey(), boundKey.getDesc(), binding, this);
        }

        Builder builder = new Builder();
        BuilderExecutor builderExecutor = new BuilderExecutor(this, builder);
        for(Commands command : Commands.values()) {
          getCommand(command.name().toLowerCase()).setExecutor(builderExecutor);
        }

		Bukkit.getLogger().log(Level.INFO, "[Spout Test Plugin " + id + "] Enabled!");
	}

    private static class BuilderExecutor implements CommandExecutor {
      private TestPlugin testPlugin;
      private Builder builder;

      public BuilderExecutor(TestPlugin plugin, Builder builder) {
        testPlugin = plugin;
        this.builder = builder;
      }

      @Override
      public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Commands c = Commands.valueOf(command.getName().toUpperCase());
        if(sender instanceof Player) {
          Player player = (Player)sender;
          switch (c) {
            case BUILD:
              onBuild(player, command, label, args);
              break;
            case MAZE:
              onMaze(player, command, label, args);
              break;
            case MARK:
              onMark(player, command, label, args);
              break;
            case COPY:
              onCopy(player, command, label, args);
              break;
            case PASTE:
              onPaste(player, command, label, args);
              break;
            case UNDO:
              onUndo(player, command, label, args);
              break;
            case REDO:
              onRedo(player, command, label, args);
              break;
            case STAIR:
              onStair(player, command, label, args);
              break;
            default:
              //
          }
        } else {
          onConsoleCommand(sender, command, label, args);
        }
        return true;
      }

      private void onStair(Player player, Command command, String label, String[] args) {
        Builder.Dim forward = Builder.getForward(Builder.getOffset(player, player.getTargetBlock(null, 1000)));
        StairBuilder stairBuilder = new StairBuilder();
        stairBuilder.buildStairSegment(player.getTargetBlock(null, 1000).getLocation(),
            forward.toVector(), 5, 3);
      }

      private void onMaze(Player player, Command command, String label, String[] args) {
        PieceBuilder<Simple2DEdge> pieceBuilder;
        if(args.length == 6) {
          Material material = Material.matchMaterial(args[0]);
          int size = Integer.parseInt(args[1]);
          Simple2DPiece piece = new Simple2DPiece(size, size, size, material,
              args[2].charAt(0) == 'o' ? Simple2DEdge.OPEN : Simple2DEdge.CLOSED,
              args[3].charAt(0) == 'o' ? Simple2DEdge.OPEN : Simple2DEdge.CLOSED,
              args[4].charAt(0) == 'o' ? Simple2DEdge.OPEN : Simple2DEdge.CLOSED,
              args[5].charAt(0) == 'o' ? Simple2DEdge.OPEN : Simple2DEdge.CLOSED);
          pieceBuilder = new PieceBuilder<Simple2DEdge>(player.getWorld(),
              Lists.newArrayList(piece), 1, 1, 1);
        } else {
          long seed = args.length > 4 ? args[4].hashCode() : player.getName().hashCode();
          pieceBuilder = new PieceBuilder<Simple2DEdge>(player.getWorld(),
            Simple2DPiece.allPossible(Integer.parseInt(args[1]), Material.matchMaterial(args[0])),
            Integer.parseInt(args[2]), Integer.parseInt(args[3]), seed);
        }
        logger.info("will be building a maze of " + Material.matchMaterial(args[0]));
        pieceBuilder.buildMaze(player.getTargetBlock(null, 1000).getLocation());
      }

      private void onRedo(Player player, Command command, String label, String[] args) {
        builder.redo(player);
      }

      private void onCopy(Player player, Command command, String label, String[] args) {
        builder.copy(player, args[0]);
        builder.clearMarks(player);
      }

      private void onPaste(Player player, Command command, String label, String[] args) {
        builder.paste(player, args[0]);
      }

      private void onUndo(Player player, Command command, String label, String[] args) {
        builder.undo(player);
      }

      private void onConsoleCommand(CommandSender sender, Command command, String label,
          String[] args) {
        
      }

      private void onMark(Player player, Command command, String label, String[] args) {
        builder.mark(player, command, label, args);
      }

      /*
       *  /build stone [h w d off_x off_y off_z]
       */
      private void onBuild(Player sender, Command command, String label, String[] args) {
        logger.info("execute player build command: "
                    + command.getName() + " [" + command.getLabel() + "] : " + label + ", "
                    + Arrays.toString(args));
        Material material = builder.getBuildMaterial(sender);
        if(args.length > 0) {
          if(args[0].equalsIgnoreCase("with")) {
            builder.setBuildMaterial(sender, Material.matchMaterial(args[1]));
            return;
          } else {
            material = Material.matchMaterial(args[1]);
          }
        }
        if(material == null) {
          material = Material.STONE;
        }
        List<Block> marks = builder.getMarks(sender);
        if(marks != null && !marks.isEmpty()) {
          builder.buildBasedOnMarks(sender, material);
          marks.clear();
          return;
        } else {
          sender.sendMessage("You haven't marked anything yet!");
        }
      }
    }

}
