package com.dorrisd.elevationtest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ElevationTest extends JavaPlugin implements Listener {

    private static ElevationTest plugin;

    private BufferedImage image;

    public static ElevationTest getPlugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;

        String imageName = ElevationTest.getPlugin().getConfig().getString("imageName");
        image = imageFromFile(imageName);
    }

    public void onDisable() {
        plugin = null;
    }

    public BufferedImage getImage() {
        return image;
    }

    public static void warning(String string) {
        ElevationTest.getPlugin().getLogger().warning(string);
    }

    public static void info(String string) {
        ElevationTest.getPlugin().getLogger().info(string);
    }

    public void doImageStuff(String imageFileName) throws IOException {
        BufferedImage image = imageFromFile(imageFileName);

    }

    public BufferedImage imageFromFile(String imageFileName) {
        try {
            File file = new File(ElevationTest.getPlugin().getDataFolder(), imageFileName);
            if (!file.exists()) {
                ElevationTest.getPlugin().saveResource(imageFileName, false);
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
