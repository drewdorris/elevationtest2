package com.dorrisd.elevationtest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ElevationTest extends JavaPlugin implements Listener {

    private static ElevationTest plugin;

    private Set<ElevationImage> images;

    public static ElevationTest get() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        this.images = new HashSet<>();

        saveDefaultConfig();
    }

    public void onDisable() {
        plugin = null;
    }

    public static void warning(String string) {
        ElevationTest.get().getLogger().warning(string);
    }

    public static void info(String string) {
        ElevationTest.get().getLogger().info(string);
    }

    public ElevationImage getImage(String fileName) {
        for (ElevationImage image : this.images) {
            if (image.getName().equals(fileName)) return image;
        }
        return null;
    }

    public synchronized Set<ElevationImage> getImages(int x, int z) {
        Set<ElevationImage> elevationImages = new HashSet<>();
        for (ElevationImage image : this.images) {
            if (image.withinBounds(x, z)) elevationImages.add(image);
        }
        if (elevationImages.size() > 0) return elevationImages;

        ConfigurationSection imageSection = getConfig().getConfigurationSection("images");
        if (imageSection == null) return null;

        for (String imagePart : imageSection.getKeys(false)) {
            int minX = getConfig().getInt("images." + imagePart + ".minX");
            int minZ = getConfig().getInt("images." + imagePart + ".minZ");

            x += 6; z += 6;

            if (x < minX + 6 || z < minZ + 6) continue;
            if (x + 6 > minX + 10000 || z + 6 > minZ + 10000) continue;

            String fileName = getConfig().getString("images." + imagePart + ".fileName");
            if (getImage(fileName) == null) {
                ElevationImage image = loadImage(x, z);
                if (image == null) continue;
                elevationImages.add(image);
            } else {
                elevationImages.add(getImage(fileName));
            }
        }
        return elevationImages;
    }

    /**
     * Loads an image that should be at a certain coord
     *
     * @param x
     * @param z
     * @return
     */
    public ElevationImage loadImage(int x, int z) {
        ConfigurationSection imageSection = getConfig().getConfigurationSection("images");
        if (imageSection == null) return null;

        for (String imagePart : imageSection.getKeys(false)) {
            int minX = getConfig().getInt("images." + imagePart + ".minX");
            int minZ = getConfig().getInt("images." + imagePart + ".minZ");

            if (x < minX + 6 || z < minZ + 6) continue;
            if (x + 6 > minX + 10000 || z + 6 > minZ + 10000) continue;

            String imageFile = getConfig().getString("images." + imagePart + ".imageFile");
            BufferedImage buff = imageFromFile(imageFile);
            if (buff == null) continue;
            int minElevation = getConfig().getInt("images." + imagePart + ".minElevation");
            int maxElevation = getConfig().getInt("images." + imagePart + ".maxElevation");

            ElevationImage image = new ElevationImage(imageFile, buff, minX, minZ, minElevation, maxElevation);
            this.images.add(image);
            return image;
        }
        return null;
    }

    public BufferedImage imageFromFile(String imageFileName) {
        try {
            File file = new File(
                    ElevationTest.get().getDataFolder() + File.separator + "images", imageFileName);
            if (!file.exists()) {
                ElevationTest.get().saveResource(imageFileName, false);
            }
            BufferedImage bufferedImage;
            bufferedImage = ImageIO.read(file);
            if (bufferedImage == null) {
                throw new IOException("Error while attempting to read image: " + imageFileName);
            }
            return bufferedImage;
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new WorldGenerator();
    }

    public static Block set(Block block, Material material) {
        block.setType(material, false);
        return block;
    }

}
