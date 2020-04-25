package io.github.paul1365972.nocturnalbees;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Beehive;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class NocturnalBees extends JavaPlugin {
	
	private static final int TICKING_INTERVAL = 2400;
	private static final int DIVISIONS = 120;
	
	private final Random random = new Random();
	private int indexOffset = 0;
	
	@Override
	public void onEnable() {
		FileConfiguration config = getConfig();
		config.addDefault("worlds", new String[0]);
		config.options().copyDefaults(true);
		saveConfig();
		
		List<World> worlds = new ArrayList<>();
		for (String worldName : getConfig().getStringList("worlds")) {
			World world = getServer().getWorld(worldName);
			if (world == null)
				throw new IllegalArgumentException("World with name \"" + worldName + "\" does not exist");
			worlds.add(world);
		}
		
		int delay = TICKING_INTERVAL / DIVISIONS;
		
		getServer().getScheduler().runTaskTimer(this, () -> {
			for (World world : worlds) {
				Chunk[] loadedChunks = world.getLoadedChunks();
				for (int i = indexOffset; i < loadedChunks.length; i += DIVISIONS) {
					Chunk chunk = loadedChunks[i];
					for (BlockState tileEntity : chunk.getTileEntities()) {
						if (tileEntity instanceof Beehive) {
							((Beehive) tileEntity).releaseEntities().forEach(bee -> {
								bee.setCannotEnterHiveTicks(1200 + random.nextInt(1200));
							});
						}
					}
				}
			}
			indexOffset++;
			indexOffset %= DIVISIONS;
		}, delay, delay);
	}
}
