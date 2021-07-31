# PersonTheCat/FastNoise

A rewrite of the legacy [FastNoise_Java](https://github.com/Auburn/FastNoise_Java) project. Unlike the
original, this library is highly extensible. It replaces most of the existing enum types with functional
interfaces and provides concrete implementations for each of the original FastNoise generators. 

# Motivations and Goals

It is my hope that this library will facilitate generation of custom, highly-optimized noise generators.
As of this time, it is merely a work-in-progress skeleton used exclusively for performance testing. 
Please create an issue here if you are able to contribute to the project and help us achieve this goal.

# Using This Library

To create a new generator, start by constructing a `NoiseDescriptor`.  Noise Descriptor is a data
transfer object designed to create and configure one of the included generator types. It provides an
API very similar to that of the original library and can be used to **generate** a new FastNoise wrapper
object.

```java
final FastNoise generator = FastNoise.createDescriptor()
  .noise(NoiseType.SIMPLEX_FRACTAL)
  .interpolation(InterpolationType.FBM)
  .frequency(0.1F)
  .generate();
```

## Custom Noise Generators

To supply a custom noise generator into the wrapper, create a class extending from `NoiseGenerator`.
Constructing this object requires an instance of `NoiseDescriptor`, which is what enables your generator
to take advantage of the common configurations.

```java
final FastNoise generator = FastNoise.createDescriptor()
  .provider(MyNoiseGenerator::new)
  .generate();
```

## Wrapping Bare-bones Noise Functions

Alternatively, FastNoise is capable of wrapping raw noise functions. A convenient way to use this feature
is to construct a `DummyNoiseWrapper` and pass it directly into the FastNoise constructor.

```java
final NoiseWrapper wrapper = new DummyNoiseWrapper()
  .wrapNoise2((x, y) -> 1);

final FastNoise generator = new FastNoise(wrapper);
```

## Using Noise Modifiers

`NoiseModifier` is a data transfer object which contains a few settings related to the amplitude of the
generator output and thresholds used for creating booleans from the output. It can be constructed with a
similar API.

```java
final NoiseWrapper wrapper = new DummyNoiseWrapper()
  .wrapNoise2((x, y) -> 0.5)
  .wrapNoise3((x, y, z) -> 0.2);

final NoiseModifier modifier = new NoiseModifier()
  .minThreshold(0.2)
  .maxThreshold(0.5);

final FastNoise generator = new FastNoise(wrapper, modifier);

final boolean demo = generator.getBoolean(1, 2);
```


