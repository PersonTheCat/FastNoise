package personthecat.fastnoise.function;

public interface DistanceFunction {
    DistanceFunction.D3 NO_OP = (dX, dY, dZ) -> 0;

    float getDistance(float dX, float dY);
    float getDistance(float dX, float dY, float dZ);

    @FunctionalInterface // (dX, dY) -> d
    interface D2 extends DistanceFunction {
        @Override
        default float getDistance(float dX, float dY, float dZ) {
            return this.getDistance(dX, dZ);
        }
    }

    @FunctionalInterface // (dX, dY, dZ) -> d
    interface D3 extends DistanceFunction {
        @Override
        default float getDistance(float dX, float dY) {
            return this.getDistance(dX, dY, 1337);
        }
    }
}
