package personthecat.fastnoise;

import personthecat.fastnoise.data.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NoiseViewer {

    private static final int IMAGE_SIZE = 500;
    private static final int PAGE_SIZE = 5;
    private static final int COORDINATE_RANGE = 1 << 16;

    public static void main(final String[] args) {
        new Context(new Scanner(System.in)).run();
    }

    private static class Context {
        final List<NoiseDescriptor> lookups = new ArrayList<>();
        final Scanner scanner;
        NoiseDescriptor descriptor;
        FastNoise generator;
        final Random rand;
        boolean threshold;
        boolean threeD;
        int x;
        int y;
        int z;
        JLabel label;
        final JFrame frame;

        Context(final Scanner s) {
            this.scanner = s;
            this.descriptor = new NoiseDescriptor();
            this.generator = descriptor.generate();
            this.rand = new Random();
            this.threshold = false;
            this.threeD = true;
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
                } catch (final IllegalArgumentException e) {
                    System.err.println("Invalid argument. Try again.\n");
                }
            }
        }

        void getNextInput() {
            System.out.println("n: New image");
            System.out.println("i: Move up");
            System.out.println("k: Move down");
            System.out.println("t: Toggle threshold");
            System.out.println("d: Toggle 3D / 2D");
            System.out.println("s: Store as lookup");
            System.out.println("a: Apply lookups from storage");
            System.out.println("p: Print settings");
            System.out.println("<key> <value>: Set property");
            System.out.println("q: Exit");

            final String command = this.scanner.nextLine();
            switch (command) {
                case "n": this.next(); break;
                case "i": this.up(); break;
                case "k": this.down(); break;
                case "t": this.toggle(); break;
                case "d": this.dimensions(); break;
                case "s": this.storeLookup(); break;
                case "a": this.applyLookups(); break;
                case "p": this.print(); break;
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
                    if (length == 1) { System.out.println(this.descriptor.noise()); return; }
                    final NoiseType noise = NoiseType.from(value);
                    if (noise == null) throw new IllegalArgumentException();
                    this.descriptor.noise(noise);
                    break;
                case "fractal":
                    if (length == 1) { System.out.println(this.descriptor.fractal()); return; }
                    final FractalType fractal = FractalType.from(value);
                    if (fractal == null) throw new IllegalArgumentException();
                    this.descriptor.fractal(fractal);
                    break;
                case "warp":
                    if (length == 1) { System.out.println(this.descriptor.warp()); return; }
                    final DomainWarpType domainWarp = DomainWarpType.from(value);
                    if (domainWarp == null) throw new IllegalArgumentException();
                    this.descriptor.warp(domainWarp);
                    break;
                case "distance":
                    if (length == 1) { System.out.println(this.descriptor.distance()); return; }
                    final CellularDistanceType distance = CellularDistanceType.from(value);
                    if (distance == null) throw new IllegalArgumentException();
                    this.descriptor.distance(distance);
                    break;
                case "cellularReturn":
                    if (length == 1) { System.out.println(this.descriptor.cellularReturn()); return; }
                    final CellularReturnType cellularReturn = CellularReturnType.from(value);
                    if (cellularReturn == null) throw new IllegalArgumentException();
                    this.descriptor.cellularReturn(cellularReturn);
                    break;
                case "seed":
                    if (length == 1) { System.out.println(this.descriptor.seed()); return; }
                    this.descriptor.seed(Integer.parseInt(value));
                    break;
                case "frequencyX":
                    if (length == 1) { System.out.println(this.descriptor.frequencyX()); return; }
                    this.descriptor.frequencyX(Float.parseFloat(value));
                    break;
                case "frequencyY":
                    if (length == 1) { System.out.println(this.descriptor.frequencyY()); return; }
                    this.descriptor.frequencyY(Float.parseFloat(value));
                    break;
                case "frequencyZ":
                    if (length == 1) { System.out.println(this.descriptor.frequencyZ()); return; }
                    this.descriptor.frequencyZ(Float.parseFloat(value));
                    break;
                case "frequency":
                    if (length == 1) {
                        System.out.println("x: " + this.descriptor.frequencyX());
                        System.out.println("y: " + this.descriptor.frequencyY());
                        System.out.println("z: " + this.descriptor.frequencyZ());
                        return;
                    }
                    this.descriptor.frequency(Float.parseFloat(value));
                    break;
                case "octaves":
                    if (length == 1) { System.out.println(this.descriptor.octaves()); return; }
                    this.descriptor.octaves(Integer.parseInt(value));
                    break;
                case "lacunarityX":
                    if (length == 1) { System.out.println(this.descriptor.lacunarityX()); return; }
                    this.descriptor.lacunarityX(Float.parseFloat(value));
                    break;
                case "lacunarityY":
                    if (length == 1) { System.out.println(this.descriptor.lacunarityY()); return; }
                    this.descriptor.lacunarityY(Float.parseFloat(value));
                    break;
                case "lacunarityZ":
                    if (length == 1) { System.out.println(this.descriptor.lacunarityZ()); return; }
                    this.descriptor.lacunarityZ(Float.parseFloat(value));
                    break;
                case "lacunarity":
                    if (length == 1) {
                        System.out.println("x: " + this.descriptor.lacunarityX());
                        System.out.println("y: " + this.descriptor.lacunarityY());
                        System.out.println("z: " + this.descriptor.lacunarityZ());
                        return;
                    }
                    this.descriptor.lacunarity(Float.parseFloat(value));
                    break;
                case "gain":
                    if (length == 1) { System.out.println(this.descriptor.gain()); return; }
                    this.descriptor.gain(Float.parseFloat(value));
                    break;
                case "pingPongStrength":
                    if (length == 1) { System.out.println(this.descriptor.pingPongStrength()); return; }
                    this.descriptor.pingPongStrength(Float.parseFloat(value));
                    break;
                case "jitterX":
                    if (length == 1) { System.out.println(this.descriptor.jitterX()); return; }
                    this.descriptor.jitterX(Float.parseFloat(value));
                    break;
                case "jitterY":
                    if (length == 1) { System.out.println(this.descriptor.jitterY()); return; }
                    this.descriptor.jitterY(Float.parseFloat(value));
                    break;
                case "jitterZ":
                    if (length == 1) { System.out.println(this.descriptor.jitterZ()); return; }
                    this.descriptor.jitterZ(Float.parseFloat(value));
                    break;
                case "jitter":
                    if (length == 1) {
                        System.out.println("x: " + this.descriptor.jitterX());
                        System.out.println("y: " + this.descriptor.jitterY());
                        System.out.println("z: " + this.descriptor.jitterZ());
                        return;
                    }
                    this.descriptor.jitter(Float.parseFloat(value));
                    break;
                case "warpAmplitudeX":
                    if (length == 1) { System.out.println(this.descriptor.warpAmplitudeX()); return; }
                    this.descriptor.warpAmplitudeX(Float.parseFloat(value));
                    break;
                case "warpAmplitudeY":
                    if (length == 1) { System.out.println(this.descriptor.warpAmplitudeY()); return; }
                    this.descriptor.warpAmplitudeY(Float.parseFloat(value));
                    break;
                case "warpAmplitudeZ":
                    if (length == 1) { System.out.println(this.descriptor.warpAmplitudeZ()); return; }
                    this.descriptor.warpAmplitudeZ(Float.parseFloat(value));
                    break;
                case "warpAmplitude":
                    if (length == 1) {
                        System.out.println("x: " + this.descriptor.warpAmplitudeX());
                        System.out.println("y: " + this.descriptor.warpAmplitudeY());
                        System.out.println("z: " + this.descriptor.warpAmplitudeZ());
                        return;
                    }
                    this.descriptor.warpAmplitude(Float.parseFloat(value));
                    break;
                case "warpFrequencyX":
                    if (length == 1) { System.out.println(this.descriptor.warpFrequencyX()); return; }
                    this.descriptor.warpFrequencyX(Float.parseFloat(value));
                    break;
                case "warpFrequencyY":
                    if (length == 1) { System.out.println(this.descriptor.warpFrequencyY()); return; }
                    this.descriptor.warpFrequencyY(Float.parseFloat(value));
                    break;
                case "warpFrequencyZ":
                    if (length == 1) { System.out.println(this.descriptor.warpFrequencyZ()); return; }
                    this.descriptor.warpFrequencyZ(Float.parseFloat(value));
                    break;
                case "warpFrequency":
                    if (length == 1) {
                        System.out.println("x: " + this.descriptor.warpFrequencyX());
                        System.out.println("y: " + this.descriptor.warpFrequencyY());
                        System.out.println("z: " + this.descriptor.warpFrequencyZ());
                        return;
                    }
                    this.descriptor.warpFrequency(Float.parseFloat(value));
                    break;
                case "offsetX":
                    if (length == 1) { System.out.println(this.descriptor.offsetX()); return; }
                    this.descriptor.offsetX(Float.parseFloat(value));
                    break;
                case "offsetY":
                    if (length == 1) { System.out.println(this.descriptor.offsetY()); return; }
                    this.descriptor.offsetY(Float.parseFloat(value));
                    break;
                case "offsetZ":
                    if (length == 1) { System.out.println(this.descriptor.offsetZ()); return; }
                    this.descriptor.offsetZ(Float.parseFloat(value));
                    break;
                case "offset":
                    if (length == 1) {
                        System.out.println("x: " + this.descriptor.offsetX());
                        System.out.println("y: " + this.descriptor.offsetY());
                        System.out.println("z: " + this.descriptor.offsetZ());
                        return;
                    }
                    this.descriptor.offset(Integer.parseInt(value));
                    break;
                case "invert":
                    if (length == 1) { System.out.println(this.descriptor.invert()); return; }
                    if (!("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)))
                        throw new IllegalArgumentException();
                    this.descriptor.invert(Boolean.parseBoolean(value));
                    break;
                case "scaleAmplitude":
                    if (length == 1) { System.out.println(this.descriptor.scaleAmplitude()); return; }
                    this.descriptor.scaleAmplitude(Float.parseFloat(value));
                    break;
                case "scaleOffset":
                    if (length == 1) { System.out.println(this.descriptor.scaleOffset()); return; }
                    this.descriptor.scaleOffset(Float.parseFloat(value));
                    break;
                case "range":
                    if (length == 1) {
                        final float amp = this.descriptor.scaleAmplitude();
                        final float ost = this.descriptor.scaleOffset();
                        final float min = -amp + ost;
                        final float max = amp + ost;
                        System.out.println(min + " ~ " + max);
                        return;
                    }
                    if (length != 3) {
                        System.err.println("Missing second argument");
                        throw new IllegalArgumentException();
                    }
                    this.descriptor.range(Float.parseFloat(value), Float.parseFloat(args[2]));
                    break;
                case "minThreshold":
                    if (length == 1) { System.out.println(this.descriptor.minThreshold()); return; }
                    this.descriptor.minThreshold(Float.parseFloat(value));
                    break;
                case "maxThreshold":
                    if (length == 1) { System.out.println(this.descriptor.maxThreshold()); return; }
                    this.descriptor.maxThreshold(Float.parseFloat(value));
                    break;
                case "threshold":
                    if (length == 1) {
                        final float min = this.descriptor.minThreshold();
                        final float max = this.descriptor.maxThreshold();
                        System.out.println(min + " ~ " + max);
                        return;
                    }
                    if (length != 3) {
                        System.err.println("Missing second argument");
                        throw new IllegalArgumentException();
                    }
                    this.descriptor.threshold(Float.parseFloat(value), Float.parseFloat(args[2]));
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

        void dimensions() {
            this.threeD = !this.threeD;
            this.regen();
            System.out.println("Now in " + (this.threeD ? "3D" : "2D"));
        }

        void storeLookup() {
            this.lookups.add(this.descriptor);
            System.out.println("Added current settings to storage. Values were reset.");
            this.descriptor = new NoiseDescriptor();
        }

        void applyLookups() {
            this.descriptor.noiseLookup(this.lookups);
            System.out.println("Lookups applied. Set type to multi or cell value to use.");
        }

        void print() {
            for (final String value : this.descriptor.toString().split(",\\s?")) {
                System.out.println(value);
            }
        }

        void regen() {
            this.generator = this.descriptor.generate();
            this.label.setIcon(new ImageIcon(this.createNextImage()));
        }

        BufferedImage createNextImage() {
            final BufferedImage image = createBlankImage();
            for (int h = 0; h < IMAGE_SIZE; h++) {
                for (int w = 0; w < IMAGE_SIZE; w++) {
                    final int rgb;
                    if (this.threshold) {
                        final boolean b = this.threeD
                            ? this.generator.getBoolean(this.x + w, this.y, this.z + h)
                            : this.generator.getBoolean(this.x + w, this.z + h);
                        rgb = b ? Integer.MAX_VALUE : 0;
                    } else {
                        final float n = this.threeD
                            ? this.generator.getNoise(this.x + w, this.y, this.z + h)
                            : this.generator.getNoise(this.x + w, this.z + h);
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
