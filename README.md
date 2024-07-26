# PersonTheCat/FastNoise

A rewrite of the legacy [FastNoise_Java](https://github.com/Auburn/FastNoise_Java) project. Unlike the
original, this library is highly extensible. It provides concrete implementations for each of the original
FastNoise generators and even includes a few of the newer features from FastNoiseLite.

**Note that this API is currently unstable and will change in a future release**.

# Motivations and Goals

It is my hope that this library will facilitate generation of custom, highly-optimized noise generators.
As of this time, it is merely a work-in-progress skeleton used exclusively for performance testing. 
Please create an issue here if you are able to contribute to the project and help us achieve this goal.

# Installation

This fork of FastNoise is available on Maven Central! To use it in your project, add the following
dependency in your build.gradle or pom.xml:

```groovy
implementation group: 'com.personthecat', name: 'fastnoise', version: '0.10'
```

# Using This Library

The create a new generator, start by constructing a `NoiseBuilder`. The Noise Builder API is a configuration
capable of automatically generating the recursive data structure used by this library.

```java
final FastNoise generator = FastNoise.builder()
  .type(NoiseType.SIMPLEX)
  .fractal(FractalType.FBM)
  .frequency(0.1F)
  .build();
```

## Custom Noise Generators

To supply a custom noise generator into the wrapper, create a class extending from `NoiseGenerator`.
Constructing this object requires an instance of `NoiseBuilder`, which is what enables your generator
to take advantage of the common configurations.

```java
final FastNoise generator = FastNoise.builder()
  .provider(MyNoiseGenerator::new)
  .build();
```

The Noise Builder API will correctly handle wrapping your generator to provide fractal, warped, or 
scaled output.

```java
final FastNoise generator = FastNoise.builder()
  .provider(MyNoiseGenerator::new)
  .fractal(FractalType.FBM)
  .warp(WarpType.BASIC_GRID)
  .build();
```

## Directly Passing Noise References

In some cases, it is desirable to manually configure wrapped noise generators. This can be achieved by
passing noise references directly to the Noise Builder API.

```java
final FastNoise generator = FastNoise.builder()
  .type(NoiseType.FRACTAL)
  .reference(new SimplexNoise())
  .frequency(0.02F)
  .build();
```

### Be Careful with Modifier Placement

Most wrapper generators deliberately ignore frequency, offset, and scale settings from their references.
For this reason, **you must apply all noise modifier settings directly to the wrapper**.

## Combining Generators

## Wrapping Bare-bones Noise Functions

Alternatively, FastNoise is capable of wrapping raw noise functions. A convenient way to use this feature
is to construct a `NoiseWrapper`. The output of this wrapper can either be modified by a `NoiseBuilder` 
or used directly as a passthrough generator.

```java
// Modifiers are significant
final FastNoise modified = FastNoise.wrapper()
  .wrapNoise((s, x, y) -> 1)
  .createBuilder()
  .frequency(0.2F)
  .build();

// Modifiers are not significant
final FastNoise passthrough = FastNoise.wrapper()
  .wrapNoise((s, x, y, z) -> 1)
  .generatePassthrough();
```

Notice that the `wrapNoise` method is overloaded to accept `NoiseFunction`s of 1, 2, and 3 dimensions.

```java
final NoiseWrapper wrapper = FastNoise.wrapper()
  .wrapNoise((s, x) -> 1);
```

## Using Noise Modifiers

`NoiseBuilder` also contains a few settings related to the amplitude of the generator output and
thresholds used for creating booleans from the output. The options can be used as follows:

```java
final FastNoise generator = FastNoise.builder()
  .threshold(0.2F, 0.5F)
  .range(-25.0F, 50.0F)
  .build();

// True if original value is in 0.2 ~ 0.5
final boolean demo1 = generator.getBoolean(1, 2);

// Instead of -1 to 1, is in -25 to 50
final float demo2 = generator.getNoiseScaled(3, 4);
```

### Noise Lookups

Some generators support dynamically resolving values from other generators. This can be used to 
distort the output in ways not possible with simple config values.

### Cellular Return Lookup

```java
final FastNoise generator = FastNoise.builder()
  .type(NoiseType.CELLULAR)
  .cellularReturn(ReturnType.NOISE_LOOKUP)
  .noiseLookup(new SimplexNoise())
  .build();
```

### Domain Warp Lookup

```java
final FastNoise generator = FastNoise.builder()
  .type(NoiseType.SIMPLEX)
  .warp(WarpType.NOISE_LOOKUP)
  .noiseLookup(new SimplexNoise())
  .build();
```

## Using Injectable Functions

A handful of generators support fully custom functions as lambda expressions. These can be used to
configure the noise output in ways not possible with simple config values.

### Scale Functions

```java
final FastNoise mountains = FastNoise.builder()
  .scale(n -> n <= 0 ? n : (float) (((0.000000002 * Math.pow(y, 6)) / 6) + (9 * Math.sqrt(y))))
  .build();
```

### Fractal Functions

```java
final FastNoise log = FastNoise.builder()
  .fractalFunction(Math::log)
  .build();
```

### Cellular Distance Functions

```java
final FastNoise twoDimensions = FastNoise.builder()
  .type(NoiseType.CELLULAR)
  .distanceFunction((dx, dy) -> minkowski(dx, dy, 0.5))
  .build();

final FastNoise threeDimensions = FastNoise.builder()
  .type(NoiseType.CELLULAR)
  .distanceFunction((dx, dy, dz) -> minkowski(dx, dy, dz, 0.5))
  .build();
```

### Cellular Return Functions

```java
final FastNoise twoDimensions = FastNoise.builder()
  .type(NoiseType.CELLULAR)
  .returnFunction((x, y, d1, d2, d3) -> avg(d1, d2, d3))
  .build();

final FastNoise threeDimensions = FastNoise.builder()
  .type(NoiseType.CELLULAR)
  .returnFunction((x, y, z, d1, d2, d3) -> avg(d1, d2, d3))
  .build();
```

### Multi Functions

```java
// directly apply noise output
final FastNoise combined = FastNoise.builder()
  .multiFunction((output) -> DoubleStream.of(output).sum())
  .references(new SimplexNoise(), new PerlinNoise())
  .build();

// apply (x, y) and generators
final FastNoise twoDimension = FastNoise.builder()
  .multiFunction((x, y, generators) -> {
    float acc = 0;
    for (FastNoise n : generators) acc ^= n.getNoise(x, y);
    return acc;
  })
  .references(new SimplexNoise(), new PerlinNoise())
  .build();

// apply (x, y, z) and generators
final FastNoise threeDimension = FastNoise.builder()
  .multiFunction((x, y, z, generators) -> {
    float acc = 0;
    for (FastNoise n : generators) acc ^= n.getNoise(x, y, z);
    return acc;
  })
  .references(new SimplexNoise(), new PerlinNoise())
  .build();
```

## Testing Noise Output

Currently, this library is still very experimental. If you would like to tinker with a few of the new
and modified settings, you can boot up NoiseViewer from the test source set. It contains a very simple
command line interface and will render a noise image to the screen as you update the underlying noise
descriptor.

