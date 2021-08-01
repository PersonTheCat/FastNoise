package personthecat.fastnoise.function;

import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.generator.FastNoise;

import java.util.function.Function;

@FunctionalInterface
public interface NoiseProvider extends Function<NoiseDescriptor, FastNoise> {}