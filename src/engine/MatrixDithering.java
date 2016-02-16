package engine;

import java.awt.*;

public class MatrixDithering {
    public static int[][] floydSteinbergDithering(int[][] matrix) {

        C3[] palette = new C3[] {
                new C3(  0,   0,   0),
                new C3(  0,   0, 255),
                new C3(  0, 255,   0),
                new C3(  0, 255, 255),
                new C3(255,   0,   0),
                new C3(255,   0, 255),
                new C3(255, 255,   0),
                new C3(255, 255, 255)
        };

        int w = matrix[0].length;
        int h = matrix.length;

        C3[][] d = new C3[h][w];

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                d[y][x] = new C3(matrix[y][x]);

        for (int y = 0; y < matrix.length - 1; y++) {
            for (int x = 0; x < matrix[y].length - 1; x++) {

                C3 oldColor = d[y][x];
                C3 newColor = findClosestPaletteColor(oldColor, palette);
                matrix[y][x] = newColor.toColor().getRGB();

                C3 err = oldColor.sub(newColor);

                if (x+1 < w)         d[y  ][x+1] = d[y  ][x+1].add(err.mul(7./16));
                if (x-1>=0 && y+1<h) d[y+1][x-1] = d[y+1][x-1].add(err.mul(3./16));
                if (y+1 < h)         d[y+1][x  ] = d[y+1][x  ].add(err.mul(5./16));
                if (x+1<w && y+1<h)  d[y+1][x+1] = d[y+1][x+1].add(err.mul(1./16));
            }
        }

        return matrix;
    }

    public static float[][] floydSteinbergDithering(float[][] matrix) {

        C3[] palette = new C3[] {
                new C3(  0,   0,   0),
                new C3(  0,   0, 255),
                new C3(  0, 255,   0),
                new C3(  0, 255, 255),
                new C3(255,   0,   0),
                new C3(255,   0, 255),
                new C3(255, 255,   0),
                new C3(255, 255, 255)
        };

        int w = matrix[0].length;
        int h = matrix.length;

        C3[][] d = new C3[h][w];

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                d[y][x] = new C3((int) matrix[y][x]);

        for (int y = 0; y < matrix.length - 1; y++) {
            for (int x = 0; x < matrix[y].length - 1; x++) {

                C3 oldColor = d[y][x];
                C3 newColor = findClosestPaletteColor(oldColor, palette);
                matrix[y][x] = newColor.toColor().getRGB();

                C3 err = oldColor.sub(newColor);

                if (x+1 < w)         d[y  ][x+1] = d[y  ][x+1].add(err.mul(7./16));
                if (x-1>=0 && y+1<h) d[y+1][x-1] = d[y+1][x-1].add(err.mul(3./16));
                if (y+1 < h)         d[y+1][x  ] = d[y+1][x  ].add(err.mul(5./16));
                if (x+1<w && y+1<h)  d[y+1][x+1] = d[y+1][x+1].add(err.mul(1./16));
            }
        }

        return matrix;
    }

    private static C3 findClosestPaletteColor(C3 c, C3[] palette) {
        C3 closest = palette[0];

        for (C3 n : palette)
            if (n.diff(c) < closest.diff(c))
                closest = n;

        return closest;
    }

    static class C3 {
        int r, g, b;

        public C3(int c) {
            Color color = new Color(c);
            this.r = color.getRed();
            this.g = color.getGreen();
            this.b = color.getBlue();
        }
        public C3(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public C3 add(C3 o) {
            return new C3(r + o.r, g + o.g, b + o.b);
        }
        public C3 sub(C3 o) {
            return new C3(r - o.r, g - o.g, b - o.b);
        }
        public C3 mul(double d) {
            return new C3((int) (d * r), (int) (d * g), (int) (d * b));
        }
        public int diff(C3 o) {
            return Math.abs(r - o.r) +  Math.abs(g - o.g) +  Math.abs(b - o.b);
        }

        public int toRGB() {
            return toColor().getRGB();
        }
        public Color toColor() {
            return new Color(clamp(r), clamp(g), clamp(b));
        }
        public int clamp(int c) {
            return Math.max(0, Math.min(255, c));
        }
    }
}
