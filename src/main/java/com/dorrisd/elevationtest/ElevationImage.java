package com.dorrisd.elevationtest;

import java.awt.image.BufferedImage;

public class ElevationImage {

    private String name;
    private BufferedImage image;
    private int minX;
    private int minZ;
    private double minElevation;
    private double maxElevation;

    public ElevationImage(String name, BufferedImage image, int minX, int minZ,
                          double minElevation, double maxElevation) {
        this.name = name;
        this.image = image;
        this.minX = minX;
        this.minZ = minZ;
        this.minElevation = minElevation;
        this.maxElevation = maxElevation;
    }

    public String getName() {
        return this.name;
    }

    public double getMinElevation() {
        return this.minElevation;
    }

    public double getMaxElevation() {
        return this.maxElevation;
    }

    public int getMinX() { return this.minX; }

    public int getMinZ() { return this.minZ; }

    public boolean withinBounds(int x, int z) {
        x += 6;
        z += 6;
        if (x < minX + 6 || z < minZ + 6) return false;
        if (x + 6 > minX + 10000 || z + 6 > minZ + 10000) return false;
        return true;
    }

    public int getElevation(int x, int z) {
        x += 6;
        z += 6;
        if (x < minX || x > minX + 10000) return -1;
        if (z < minZ || z > minZ + 10000) return -1;
        int pixel;
        try {
            pixel = image.getRGB(x - minX, z - minZ);
        } catch (Exception e) {
            return -1;
        }

        // if pixel is transparent, make it null (use jpeg!)
        if ((pixel >> 24) == 0x00) {
            return -1;
        }

        java.awt.Color color = new java.awt.Color(pixel);
        int actual = color.getRed();

        double elevationAboveMin = (maxElevation - minElevation) * (actual / 255);
        double irlElevation = elevationAboveMin + minElevation;

        int mcElevation = getMCElevation(irlElevation);

        return actual;
    }

    private static int getMCElevation(double irlElevation) {
        float underSqrt = 552.70976F - (.8754F * (612.51526F - (float) irlElevation));
        int elevation = (int) ((23.50978D + underSqrt) / .4377D);
        return elevation;
    }

}
