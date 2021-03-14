package com.dorrisd.elevationtest;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import java.awt.*;
import java.util.Random;
import java.util.Set;

public class WorldGenerator extends ChunkGenerator {

    private Random random = new Random();

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int coordX = (chunkX * 16) + x;
                int coordZ = (chunkZ * 16) + z;
                biome.setBiome(x, z, Biome.FOREST);
                Set<ElevationImage> images = ElevationTest.get().getImages(coordX, coordZ);
                if (images.size() == 0) continue;
                int elevation = Integer.MIN_VALUE;
                ElevationImage currentImage = null;
                for (ElevationImage image : images) {
                    elevation = image.getElevation(coordX, coordZ);
                    currentImage = image;
                    // if there's multiple, certain spots on the img wont be filled in
                    if (elevation <= image.getMinElevation()) continue;
                }

                if (elevation == 62 || elevation == 61 || elevation == 60) {
                    elevation = 62;
                    for (int i = elevation - 3; i >= 1; i--) {
                        chunk.setBlock(x, i, z, Material.STONE);
                    }
                    chunk.setBlock(x, elevation - 2, z, Material.SANDSTONE);
                    chunk.setBlock(x, elevation - 1, z, Material.SANDSTONE);
                    chunk.setBlock(x, elevation, z, Material.SAND);
                    chunk.setBlock(x, elevation + 1, z, Material.WATER);
                    continue;
                }
                if (elevation < 60) continue;

                // set top block
                Color groundColor = currentImage.getPixel(coordX, coordZ);
                if (groundColor == null) {
                    chunk.setBlock(x, elevation, z, Material.GRASS_BLOCK);
                } else {
                    System.out.println(groundColor);
                    MaterialData materialData = RGBBlockColor.getClosestBlockValue(groundColor, true);
                    System.out.println(elevation + ", " + materialData.getMaterial());
                    chunk.setBlock(x, elevation, z, materialData.getMaterial());
                }
                chunk.setBlock(x, elevation - 1, z, Material.DIRT);
                chunk.setBlock(x, elevation - 2, z, Material.DIRT);
                if (chance(3)) {
                    chunk.setBlock(x, elevation - 3, z, Material.STONE);
                } else {
                    chunk.setBlock(x, elevation - 3, z, Material.DIRT);
                }
                for (int i = elevation - 4; i >= 1; i--) {
                    chunk.setBlock(x, i, z, Material.STONE);
                }
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

    private boolean chance(int outOf) {
        return this.random.nextInt(outOf) == 0;
    }

}
