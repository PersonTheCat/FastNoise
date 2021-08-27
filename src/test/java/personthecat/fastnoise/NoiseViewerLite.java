package personthecat.fastnoise;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Scanner;

public class NoiseViewerLite {

    private static final int IMAGE_SIZE = 500;
    private static final int PAGE_SIZE = 5;
    private static final int COORDINATE_RANGE = 1 << 16;

    public static void main(final String[] args) {
        new Context(new Scanner(System.in)).run();
    }

    private static class Context {
        final Scanner scanner;
        FastNoiseLite generator;
        final Random rand;
        boolean threshold;
        int x;
        int y;
        int z;
        JLabel label;
        final JFrame frame;

        Context(final Scanner s) {
            this.scanner = s;
            this.generator = new FastNoiseLite();
            this.rand = new Random();
            this.threshold = false;
            this.x = 0;
            this.y = 0;
            this.z = 0;
            this.label = this.createLabel();
            this.frame = this.createFrame(label);
        }

        @SuppressWarnings("InfiniteLoopStatement")
        void run() {
            while (true) {
                try {
                    this.getNextInput();
                    System.out.println();
                } catch (final IllegalArgumentException ignored) {
                    System.err.println("Invalid argument. Try again.\n");
                }
            }
        }

        void getNextInput() {
            System.out.println("n: New image");
            System.out.println("i: Move up");
            System.out.println("k: Move down");
            System.out.println("t: Toggle threshold");
            System.out.println("p: Print settings");
            System.out.println("<key> <value>: Set property");
            System.out.println("q: Exit");

            final String command = this.scanner.nextLine();
            switch (command) {
                case "n": this.next(); break;
                case "i": this.up(); break;
                case "k": this.down(); break;
                case "t": this.toggle(); break;
                case "p": System.out.println(this.generator); break;
                case "q": System.exit(0);
                default: this.set(command);
            }
        }

        void next() {
            this.x = this.rand.nextInt(COORDINATE_RANGE);
            this.y = this.rand.nextInt(COORDINATE_RANGE);
            this.z = this.rand.nextInt(COORDINATE_RANGE);
            System.out.println("Next image");
            this.regen();
        }

        void up() {
            this.y += PAGE_SIZE;
            System.out.println("Move up");
            this.regen();
        }

        void down() {
            this.y -= PAGE_SIZE;
            System.out.println("Move down");
            this.regen();
        }

        void set(final String command) {
            final String[] args = command.split(" ");
            final int length = args.length;

            if (length > 3) {
                System.err.println("Invalid syntax");
                this.set(this.scanner.nextLine());
                return;
            }

            final String key = args[0];
            final String value = length > 1 ? args[1] : null;

            switch (key) {
                case "noise":
                    if (length == 1) { System.out.println(this.generator.mNoiseType); return; }
                    final FastNoiseLite.NoiseType noise = FastNoiseLite.NoiseType.valueOf(value);
                    this.generator.SetNoiseType(noise);
                    break;
                case "fractal":
                    if (length == 1) { System.out.println(this.generator.mFractalType); return; }
                    final FastNoiseLite.FractalType fractal = FastNoiseLite.FractalType.valueOf(value);
                    this.generator.SetFractalType(fractal);
                    break;
                case "warp":
                    if (length == 1) { System.out.println(this.generator.mDomainWarpType); return; }
                    final FastNoiseLite.DomainWarpType domainWarp = FastNoiseLite.DomainWarpType.valueOf(value);
                    this.generator.SetDomainWarpType(domainWarp);
                    break;
                case "rotation":
                    if (length == 1) { System.out.println(this.generator.mRotationType3D); return; }
                    final FastNoiseLite.RotationType3D rotation = FastNoiseLite.RotationType3D.valueOf(value);
                    this.generator.SetRotationType3D(rotation);
                    break;
                case "distance":
                    if (length == 1) { System.out.println(this.generator.mCellularDistanceFunction); return; }
                    final FastNoiseLite.CellularDistanceFunction distance = FastNoiseLite.CellularDistanceFunction.valueOf(value);
                    this.generator.SetCellularDistanceFunction(distance);
                    break;
                case "cellularReturn":
                    if (length == 1) { System.out.println(this.generator.mCellularReturnType); return; }
                    final FastNoiseLite.CellularReturnType cellularReturn = FastNoiseLite.CellularReturnType.valueOf(value);
                    this.generator.SetCellularReturnType(cellularReturn);
                    break;
                case "seed":
                    if (length == 1) { System.out.println(this.generator.mSeed); return; }
                    this.generator.SetSeed(Integer.parseInt(value));
                    break;
                case "frequency":
                    if (length == 1) { System.out.println(this.generator.mFrequency); return; }
                    this.generator.SetFrequency(Float.parseFloat(value));
                    break;
                case "octaves":
                    if (length == 1) { System.out.println(this.generator.mOctaves); return; }
                    this.generator.SetFractalOctaves(Integer.parseInt(value));
                    break;
                case "lacunarity":
                    if (length == 1) { System.out.println(this.generator.mLacunarity); return; }
                    this.generator.SetFractalLacunarity(Float.parseFloat(value));
                    break;
                case "gain":
                    if (length == 1) { System.out.println(this.generator.mGain); return; }
                    this.generator.SetFractalGain(Float.parseFloat(value));
                    break;
                case "pingPongStrength":
                    if (length == 1) { System.out.println(this.generator.mPingPongStength); return; }
                    this.generator.SetFractalPingPongStrength(Float.parseFloat(value));
                    break;
                case "jitter":
                    if (length == 1) { System.out.println(this.generator.mCellularJitterModifier); return; }
                    this.generator.SetCellularJitter(Float.parseFloat(value));
                    break;
                case "warpAmplitude":
                    if (length == 1) { System.out.println(this.generator.mDomainWarpAmp); return; }
                    this.generator.SetDomainWarpAmp(Float.parseFloat(value));
                    break;
                case "minThreshold":
                    if (length == 1) { System.out.println(this.generator.mMinThreshold); return; }
                    this.generator.SetMinThreshold(Float.parseFloat(value));
                    break;
                case "maxThreshold":
                    if (length == 1) { System.out.println(this.generator.mMaxThreshold); return; }
                    this.generator.SetMaxThreshold(Float.parseFloat(value));
                    break;
                case "threshold":
                    if (length == 1) {
                        final float min = this.generator.mMinThreshold;
                        final float max = this.generator.mMaxThreshold;
                        System.out.println(min + " ~ " + max);
                        return;
                    }
                    if (length != 3) {
                        System.err.println("Missing second argument");
                        throw new IllegalArgumentException();
                    }
                    this.generator.SetThreshold(Float.parseFloat(value), Float.parseFloat(args[2]));
                    break;
                default:
                    System.err.println("Unknown key: " + key);
                    this.set(this.scanner.nextLine());
                    return;
            }
            System.out.println("Set " + key);
            this.regen();
        }

        void toggle() {
            this.threshold = !this.threshold;
            this.regen();
        }

        void regen() {
            this.label.setIcon(new ImageIcon(this.createNextImage()));
        }

        BufferedImage createNextImage() {
            final BufferedImage image = createBlankImage();
            for (int h = 0; h < IMAGE_SIZE; h++) {
                for (int w = 0; w < IMAGE_SIZE; w++) {
                    final int rgb;
                    if (this.threshold) {
                        final boolean b = this.generator.GetBoolean(this.x + w, this.z + h);
                        rgb = b ? Integer.MAX_VALUE : 0;
                    } else {
                        final float n = this.generator.GetNoise(this.x + w,  this.z + h);
                        final int v = (int) ((1.0F + n) * 127.0F);
                        rgb = new Color(v, v, v).getRGB();
                    }
                    image.setRGB(w, h, rgb);
                }
            }
            return image;
        }

        JLabel createLabel() {
            return new JLabel(new ImageIcon(this.createNextImage()));
        }

        BufferedImage createBlankImage() {
            return new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
        }

        JFrame createFrame(final JLabel label) {
            final JFrame frame = new JFrame();
            frame.add(label);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            return frame;
        }
    }
}
