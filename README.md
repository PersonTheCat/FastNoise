# PersonTheCat/FastNoise

A rewrite of the legacy [FastNoise_Java](https://github.com/Auburn/FastNoise_Java) project. Unlike the
original, this library is highly extensible. It provides concrete implementations for each of the original
FastNoise generators and even includes a few of the newer features from FastNoiseLite.

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
  .noise(NoiseType.SIMPLEX)
  .fractal(FractalType.FBM)
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
is to construct a `DummyNoiseWrapper`. The output of this wrapper can either be modified by a 
`NoiseDescriptor` or used directly as a passthrough generator.

```java
final FastNoise modified = new DummyNoiseWrapper()
  .wrapNoise2((s, x, y) -> 1)
  .createDescriptor()
  .frequency(0.2F)
  .generate();

final FastNoise passthrough = new DummyNoiseWrapper()
  .wrapNoise3((s, x, y, z) -> 1)
  .generatePassthrough();
```

## Using Noise Modifiers

`NoiseDescriptor` also contains a few settings related to the amplitude of the generator output and
thresholds used for creating booleans from the output. The options can be used as follows:

```java

final FastNoise generator = FastNoise.createDescriptor()
  .threshold(0.2F, 0.5F)
  .range(-25.0F, 50.0F)
  .generate();

// True if original value is in 0.2 ~ 0.5
final boolean demo1 = generator.getBoolean(1, 2);

// Instead of -1 to 1, is in -25 to 50
final float demo2 = generator.getNoiseScaled(3, 4);
```

## Testing Noise Output

Currently, this library is still very experimental. If you would like to tinker with a few of the new
and modified settings, you can boot up NoiseViewer from the test source set. It contains a very simple
command line interface and will render a noise image to the screen as you update the underlying noise
descriptor.

