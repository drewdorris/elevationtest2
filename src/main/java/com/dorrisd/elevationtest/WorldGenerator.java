package com.dorrisd.elevationtest;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class WorldGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        BufferedImage image = ElevationTest.getPlugin().getImage();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int elevation = getMaxElevation(image, (chunkX * 16) + x, (chunkZ * 16) + z);

                if (elevation == 62 || elevation == 61 || elevation == 60) {
                    elevation = 62;
                    chunk.setBlock(x, elevation + 1, z, Material.WATER);
                    chunk.setBlock(x, elevation - 1, z, Material.SANDSTONE);
                    chunk.setBlock(x, elevation, z, Material.SAND);
                    continue;
                }
                if (elevation < 60) continue;
                chunk.setBlock(x, elevation, z, Material.GRASS_BLOCK);
                chunk.setBlock(x, elevation - 1, z, Material.DIRT);
                chunk.setBlock(x, elevation - 2, z, Material.DIRT);
            }
        }
        return chunk;
    }

    /**
    public void populate(World world, Random random, Chunk source) {
        ElevationTest.info("uhhh");
        BufferedImage image = ElevationTest.getPlugin().getImage();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Location location = source.getBlock(x, 0, z).getLocation();
                int elevation = getMaxElevation(image, location.getBlockX(), location.getBlockZ());
                location = location.add(0, elevation, 0);

                if (elevation <= 62) {
                    elevation = 62;
                    ElevationTest.set(world.getBlockAt(location), Material.SAND);
                    ElevationTest.set(world.getBlockAt(location.add(0, -1, 0)), Material.SAND);
                    continue;
                }
                ElevationTest.set(world.getBlockAt(location), Material.GRASS_BLOCK);
                ElevationTest.set(world.getBlockAt(location.add(0, -1, 0)), Material.DIRT);
                ElevationTest.set(world.getBlockAt(location.add(0, -1, 0)), Material.DIRT);
            }
        }
    }
     */

    public int getMaxElevation(BufferedImage image, int x, int z) {
        int pixel;
        try {
            pixel = image.getRGB(x, z);
        } catch (Exception e) {
            return 0;
        }

        // if pixel is transparent, make it null
        if ((pixel >> 24) == 0x00) {
            return 0;
        }

        Color color = new Color(pixel);
        int actual = color.getRed();
        int mcActual = (int) (((double) .31 * actual) - 5);
        return mcActual += 61;
    }

}
