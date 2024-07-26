package personthecat.fastnoise;

import personthecat.fastnoise.data.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NoiseViewer {

    private static final int IMAGE_SIZE = 500;
    private static final int IMAGE_CENTER = IMAGE_SIZE / 2;
    private static final int LINE_WIDTH = 3;
    private static final int CENTER_LINE_COLOR = -12566464;
    private static final int DEFAULT_SCALE = 3;
    private static final int PAGE_SIZE = 5;
    private static final int PAN_AMOUNT = 10;
    private static final int PAN_DELAY = 3;
    private static final int COORDINATE_RANGE = 1 << 16;

    public static void main(final String[] args) {
        new Context(new Scanner(System.in)).run();
    }

    private static class Context {
        final List<NoiseBuilder> references = new ArrayList<>();
        final Scanner scanner;
        NoiseBuilder builder;
        FastNoise generator;
        final Random rand;
        DrawMode mode;
        int scale;
        boolean threeD;
        int x;
        int y;
        int z;
        JLabel label;
        final JFrame frame;

        Context(final Scanner s) {
            this.scanner = s;
            this.builder = new NoiseBuilder();
            this.generator = builder.build();
            this.rand = new Random();
            this.mode = DrawMode.STANDARD;
            this.scale = DEFAULT_SCALE;
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
            System.out.println("i: Move up (hold shift to pan)");
            System.out.println("k: Move down (hold shift to pan)");
            System.out.println("t: Toggle threshold / standard / line mode");
            System.out.println("d: Toggle 3D / 2D");
            System.out.println("l: Convert settings to noiseLookup");
            System.out.println("s: Store as reference");
            System.out.println("a: Apply references from storage");
            System.out.println("p: Print settings");
            System.out.println("r: Reset settings");
            System.out.println("b: Redraw settings (debug)");
            System.out.println("scale <num>: Update 1D scale (for line mode)");
            System.out.println("<key> <value>: Set property");
            System.out.println("q: Exit");

            final String command = this.scanner.nextLine();
            switch (command.trim()) {
                case "n": this.next(); break;
                case "i": this.up(); break;
                case "k": this.down(); break;
                case "I": this.panUp(); break;
                case "K": this.panDown(); break;
                case "t": this.toggle(); break;
                case "d": this.dimensions(); break;
                case "l": this.convertToLookup(); break;
                case "s": this.storeReferences(); break;
                case "a": this.applyReferences(); break;
                case "p": this.print(); break;
                case "r": this.reset(); break;
                case "b": this.regen(); break;
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

        void panUp() {
            for (int i = 0; i < PAGE_SIZE * PAN_AMOUNT; i++) {
                this.y++;
                try { Thread.sleep(PAN_DELAY); } catch (Exception ignored) {}
                this.regen();
            }
            System.out.println("Panned up");
        }

        void panDown() {
            for (int i = 0; i < PAGE_SIZE * PAN_AMOUNT; i++) {
                this.y--;
                try { Thread.sleep(PAN_DELAY); } catch (Exception ignored) {}
                this.regen();
            }
            System.out.println("Panned up");
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
                case "scale":
                    if (length == 1) { System.out.println(this.scale); return; }
                    this.scale = Integer.parseInt(value);
                    break;
                case "mode":
                case "noise":
                case "type":
                    if (length == 1) { System.out.println(this.builder.type()); return; }
                    final NoiseType type = NoiseType.from(value);
                    if (type == null) throw new IllegalArgumentException();
                    this.builder.type(type);
                    break;
                case "fractal":
                    if (length == 1) { System.out.println(this.builder.fractal()); return; }
                    final FractalType fractal = FractalType.from(value);
                    if (fractal == null) throw new IllegalArgumentException();
                    this.builder.fractal(fractal);
                    break;
                case "warp":
                    if (length == 1) { System.out.println(this.builder.warp()); return; }
                    final WarpType domainWarp = WarpType.from(value);
                    if (domainWarp == null) throw new IllegalArgumentException();
                    this.builder.warp(domainWarp);
                    break;
                case "distance":
                    if (length == 1) { System.out.println(this.builder.distance()); return; }
                    final DistanceType distance = DistanceType.from(value);
                    if (distance == null) throw new IllegalArgumentException();
                    this.builder.distance(distance);
                    break;
                case "return":
                case "cellularReturn":
                    if (length == 1) { System.out.println(this.builder.cellularReturn()); return; }
                    final ReturnType cellularReturn = ReturnType.from(value);
                    if (cellularReturn == null) throw new IllegalArgumentException();
                    this.builder.cellularReturn(cellularReturn);
                    break;
                case "multi":
                    if (length == 1) { System.out.println(this.builder.multi()); return; }
                    final MultiType multi = MultiType.from(value);
                    if (multi == null) throw new IllegalArgumentException();
                    this.builder.multi(multi);
                    break;
                case "seed":
                    if (length == 1) { System.out.println(this.builder.seed()); return; }
                    this.builder.seed(Integer.parseInt(value));
                    break;
                case "frequencyX":
                    if (length == 1) { System.out.println(this.builder.frequencyX()); return; }
                    this.builder.frequencyX(Float.parseFloat(value));
                    break;
                case "frequencyY":
                    if (length == 1) { System.out.println(this.builder.frequencyY()); return; }
                    this.builder.frequencyY(Float.parseFloat(value));
                    break;
                case "frequencyZ":
                    if (length == 1) { System.out.println(this.builder.frequencyZ()); return; }
                    this.builder.frequencyZ(Float.parseFloat(value));
                    break;
                case "frequency":
                    if (length == 1) {
                        System.out.println("x: " + this.builder.frequencyX());
                        System.out.println("y: " + this.builder.frequencyY());
                        System.out.println("z: " + this.builder.frequencyZ());
                        return;
                    }
                    this.builder.frequency(Float.parseFloat(value));
                    break;
                case "octaves":
                    if (length == 1) { System.out.println(this.builder.octaves()); return; }
                    this.builder.octaves(Integer.parseInt(value));
                    break;
                case "lacunarityX":
                    if (length == 1) { System.out.println(this.builder.lacunarityX()); return; }
                    this.builder.lacunarityX(Float.parseFloat(value));
                    break;
                case "lacunarityY":
                    if (length == 1) { System.out.println(this.builder.lacunarityY()); return; }
                    this.builder.lacunarityY(Float.parseFloat(value));
                    break;
                case "lacunarityZ":
                    if (length == 1) { System.out.println(this.builder.lacunarityZ()); return; }
                    this.builder.lacunarityZ(Float.parseFloat(value));
                    break;
                case "lacunarity":
                    if (length == 1) {
                        System.out.println("x: " + this.builder.lacunarityX());
                        System.out.println("y: " + this.builder.lacunarityY());
                        System.out.println("z: " + this.builder.lacunarityZ());
                        return;
                    }
                    this.builder.lacunarity(Float.parseFloat(value));
                    break;
                case "gain":
                    if (length == 1) { System.out.println(this.builder.gain()); return; }
                    this.builder.gain(Float.parseFloat(value));
                    break;
                case "pingPongStrength":
                    if (length == 1) { System.out.println(this.builder.pingPongStrength()); return; }
                    this.builder.pingPongStrength(Float.parseFloat(value));
                    break;
                case "jitterX":
                    if (length == 1) { System.out.println(this.builder.jitterX()); return; }
                    this.builder.jitterX(Float.parseFloat(value));
                    break;
                case "jitterY":
                    if (length == 1) { System.out.println(this.builder.jitterY()); return; }
                    this.builder.jitterY(Float.parseFloat(value));
                    break;
                case "jitterZ":
                    if (length == 1) { System.out.println(this.builder.jitterZ()); return; }
                    this.builder.jitterZ(Float.parseFloat(value));
                    break;
                case "jitter":
                    if (length == 1) {
                        System.out.println("x: " + this.builder.jitterX());
                        System.out.println("y: " + this.builder.jitterY());
                        System.out.println("z: " + this.builder.jitterZ());
                        return;
                    }
                    this.builder.jitter(Float.parseFloat(value));
                    break;
                case "warpAmplitudeX":
                    if (length == 1) { System.out.println(this.builder.warpAmplitudeX()); return; }
                    this.builder.warpAmplitudeX(Float.parseFloat(value));
                    break;
                case "warpAmplitudeY":
                    if (length == 1) { System.out.println(this.builder.warpAmplitudeY()); return; }
                    this.builder.warpAmplitudeY(Float.parseFloat(value));
                    break;
                case "warpAmplitudeZ":
                    if (length == 1) { System.out.println(this.builder.warpAmplitudeZ()); return; }
                    this.builder.warpAmplitudeZ(Float.parseFloat(value));
                    break;
                case "warpAmplitude":
                    if (length == 1) {
                        System.out.println("x: " + this.builder.warpAmplitudeX());
                        System.out.println("y: " + this.builder.warpAmplitudeY());
                        System.out.println("z: " + this.builder.warpAmplitudeZ());
                        return;
                    }
                    this.builder.warpAmplitude(Float.parseFloat(value));
                    break;
                case "warpFrequencyX":
                    if (length == 1) { System.out.println(this.builder.warpFrequencyX()); return; }
                    this.builder.warpFrequencyX(Float.parseFloat(value));
                    break;
                case "warpFrequencyY":
                    if (length == 1) { System.out.println(this.builder.warpFrequencyY()); return; }
                    this.builder.warpFrequencyY(Float.parseFloat(value));
                    break;
                case "warpFrequencyZ":
                    if (length == 1) { System.out.println(this.builder.warpFrequencyZ()); return; }
                    this.builder.warpFrequencyZ(Float.parseFloat(value));
                    break;
                case "warpFrequency":
                    if (length == 1) {
                        System.out.println("x: " + this.builder.warpFrequencyX());
                        System.out.println("y: " + this.builder.warpFrequencyY());
                        System.out.println("z: " + this.builder.warpFrequencyZ());
                        return;
                    }
                    this.builder.warpFrequency(Float.parseFloat(value));
                    break;
                case "offsetX":
                    if (length == 1) { System.out.println(this.builder.offsetX()); return; }
                    this.builder.offsetX(Float.parseFloat(value));
                    break;
                case "offsetY":
                    if (length == 1) { System.out.println(this.builder.offsetY()); return; }
                    this.builder.offsetY(Float.parseFloat(value));
                    break;
                case "offsetZ":
                    if (length == 1) { System.out.println(this.builder.offsetZ()); return; }
                    this.builder.offsetZ(Float.parseFloat(value));
                    break;
                case "offset":
                    if (length == 1) {
                        System.out.println("x: " + this.builder.offsetX());
                        System.out.println("y: " + this.builder.offsetY());
                        System.out.println("z: " + this.builder.offsetZ());
                        return;
                    }
                    this.builder.offset(Integer.parseInt(value));
                    break;
                case "invert":
                    if (length == 1) { System.out.println(this.builder.invert()); return; }
                    if (!("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)))
                        throw new IllegalArgumentException();
                    this.builder.invert(Boolean.parseBoolean(value));
                    break;
                case "scaleAmplitude":
                    if (length == 1) { System.out.println(this.builder.scaleAmplitude()); return; }
                    this.builder.scaleAmplitude(Float.parseFloat(value));
                    break;
                case "scaleOffset":
                    if (length == 1) { System.out.println(this.builder.scaleOffset()); return; }
                    this.builder.scaleOffset(Float.parseFloat(value));
                    break;
                case "range":
                    if (length == 1) {
                        final float amp = this.builder.scaleAmplitude();
                        final float ost = this.builder.scaleOffset();
                        final float min = -amp + ost;
                        final float max = amp + ost;
                        System.out.println(min + " ~ " + max);
                        return;
                    }
                    if (length != 3) {
                        System.err.println("Missing second argument");
                        throw new IllegalArgumentException();
                    }
                    this.builder.range(Float.parseFloat(value), Float.parseFloat(args[2]));
                    break;
                case "minThreshold":
                    if (length == 1) { System.out.println(this.builder.minThreshold()); return; }
                    this.builder.minThreshold(Float.parseFloat(value));
                    break;
                case "maxThreshold":
                    if (length == 1) { System.out.println(this.builder.maxThreshold()); return; }
                    this.builder.maxThreshold(Float.parseFloat(value));
                    break;
                case "threshold":
                    if (length == 1) {
                        final float min = this.builder.minThreshold();
                        final float max = this.builder.maxThreshold();
                        System.out.println(min + " ~ " + max);
                        return;
                    }
                    if (length != 3) {
                        System.err.println("Missing second argument");
                        throw new IllegalArgumentException();
                    }
                    this.builder.threshold(Float.parseFloat(value), Float.parseFloat(args[2]));
                    break;
                case "":
                    return;
                default:
                    System.err.println("Unknown key: " + key);
                    this.set(this.scanner.nextLine());
                    return;
            }
            System.out.println("Set " + key);
            this.regen();
        }

        void toggle() {
            switch (this.mode) {
                case STANDARD: this.mode = DrawMode.THRESHOLD; break;
                case THRESHOLD: this.mode = DrawMode.LINE; break;
                case LINE: this.mode = DrawMode.STANDARD;
            }
            System.out.println("In " + this.mode.name().toLowerCase() + " mode");
            this.regen();
        }

        void dimensions() {
            this.threeD = !this.threeD;
            this.regen();
            System.out.println("Now in " + (this.threeD ? "3D" : "2D"));
        }

        void convertToLookup() {
            this.builder = new NoiseBuilder().noiseLookup(this.builder);
            System.out.println("Converted previous settings to noise lookup. Set typed to cellular or warped to use.");
            this.regen();
        }

        void storeReferences() {
            this.references.add(this.builder);
            System.out.println("Added current settings to storage. Values were reset.");
            System.out.println("You now have " + this.references.size() + " lookups in storage.");
            this.builder = new NoiseBuilder();
            this.regen();
        }

        void applyReferences() {
            System.out.println("References in storage:");
            for (final NoiseBuilder reference : this.references) {
                System.out.println(" * " + reference);
            }
            this.builder.references(this.references);
            System.out.println("References applied. Set type to fractal, warped, or multi to use.");
        }

        void print() {
            for (final String value : this.builder.toString().split(",\\s?")) {
                System.out.println(value);
            }
            System.out.println("Class: " + this.generator.getClass());
        }

        void reset() {
            this.references.clear();
            this.builder = new NoiseBuilder();
            this.regen();
        }

        void regen() {
            this.generator = this.builder.build();
            this.label.setIcon(new ImageIcon(this.createNextImage()));
        }

        BufferedImage createNextImage() {
            final BufferedImage image = createBlankImage();
            switch (this.mode) {
                case STANDARD: this.drawStandard(image); break;
                case THRESHOLD: this.drawThreshold(image); break;
                default: this.drawLine(image);
            }
            return image;
        }

        void drawStandard(final BufferedImage image) {
            for (int h = 0; h < IMAGE_SIZE; h++) {
                for (int w = 0; w < IMAGE_SIZE; w++) {
                    final float n = this.threeD
                        ? this.generator.getNoise(this.x + w, this.y, this.z + h)
                        : this.generator.getNoise(this.x + w, this.z + h);
                    final int v = Math.max(0, Math.min(255, (int) ((1.0F + n) * 127.0F)));
                    image.setRGB(w, h, new Color(v, v, v).getRGB());
                }
            }
        }

        void drawThreshold(final BufferedImage image) {
            for (int h = 0; h < IMAGE_SIZE; h++) {
                for (int w = 0; w < IMAGE_SIZE; w++) {
                    final boolean b = this.threeD
                        ? this.generator.getBoolean(this.x + w, this.y, this.z + h)
                        : this.generator.getBoolean(this.x + w, this.z + h);
                    image.setRGB(w, h, b ? Integer.MAX_VALUE : 0);
                }
            }
        }

        void drawLine(final BufferedImage image) {
            final int increment = IMAGE_SIZE / (this.scale * 2);
            for (int w = 0; w < IMAGE_SIZE; w++) {
                final float v = this.generator.getNoiseScaled(this.x + w);
                final int y = IMAGE_CENTER + (int) (v * increment);

                image.setRGB(w, IMAGE_CENTER, CENTER_LINE_COLOR);

                if (y > LINE_WIDTH && y < IMAGE_SIZE - LINE_WIDTH) {
                    for (int h = y; h < y + LINE_WIDTH; h++) {
                        image.setRGB(w, IMAGE_SIZE - h, Integer.MAX_VALUE);
                    }
                }
            }
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
            label.addMouseListener((MouseClickedLister) e ->
                System.out.printf("(%s, %s) = %.2f (%s)\n",
                    e.getX(),
                    e.getY(),
                    this.getNoiseAtOffset(e.getX(), e.getY()),
                    this.getBooleanAtOffset(e.getX(), e.getY()) ? "in" : "out"));
            return frame;
        }

        float getNoiseAtOffset(int x, int z) {
            if (this.threeD) {
                return this.generator.getNoiseScaled(this.x + x, this.y, this.z + z);
            }
            return this.generator.getNoiseScaled(this.x + x, this.z + z);
        }

        boolean getBooleanAtOffset(int x, int z) {
            if (this.threeD) {
                return this.generator.getBoolean(this.x + x, this.y, this.z + z);
            }
            return this.generator.getBoolean(this.x + x, this.z + z);
        }
    }

    private enum DrawMode {
        STANDARD,
        THRESHOLD,
        LINE
    }

    private interface MouseClickedLister extends MouseListener {
        @Override default void mousePressed(MouseEvent e) {}
        @Override default void mouseReleased(MouseEvent e) {}
        @Override default void mouseEntered(MouseEvent e) {}
        @Override default void mouseExited(MouseEvent e) {}
    }
}
