package personthecat.fastnoise.function;

public interface CellularReturn {
    CellularReturn.D3 NO_OP = (x, y, z, d1, d2, d3) -> 0;

    float getReturn(int x, int y, float d1, float d2, float d3);
    float getReturn(int x, int y, int z, float d1, float d2, float d3);

    @FunctionalInterface // (x, y, d1, d1, d3) -> n
    interface D2 extends CellularReturn {
        @Override
        default float getReturn(int x, int y, int z, float d1, float d2, float d3) {
            return this.getReturn(x, z, d1, d2, d3);
        }
    }

    @FunctionalInterface // (x, y, z, d1, d1, d3) -> n
    interface D3 extends CellularReturn {
        @Override
        default float getReturn(int x, int y, float d1, float d2, float d3) {
            return this.getReturn(x, 1337, y, d1, d2, d3);
        }
    }
}