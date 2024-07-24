package personthecat.fastnoise.function;

public interface FractalFunction {
    FractalFunction NO_OP = f -> 0;

    float fractal(float f);
}
