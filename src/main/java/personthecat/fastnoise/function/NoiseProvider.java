package personthecat.fastnoise.function;

import personthecat.fastnoise.data.NoiseBuilder;
import personthecat.fastnoise.FastNoise;

import java.util.function.Function;

@FunctionalInterface
public interface NoiseProvider extends Function<NoiseBuilder, FastNoise> {}
