package personthecat.fastnoise.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import personthecat.fastnoise.function.DistanceFunction2;
import personthecat.fastnoise.function.DistanceFunction3;
import personthecat.fastnoise.function.ReturnFunction2;
import personthecat.fastnoise.function.ReturnFunction3;

import static personthecat.fastnoise.util.NoiseUtils.value2;
import static personthecat.fastnoise.util.NoiseUtils.value3;

@Data
@NoArgsConstructor
@Accessors(fluent = true)
public class Cellular1EdgeCustomizer {
    private DistanceFunction2 distance2 = CellularDistanceType.EUCLIDEAN;
    private DistanceFunction3 distance3 = CellularDistanceType.EUCLIDEAN;
    private ReturnFunction2 return2 = (x, z, d) -> value2(0, x, z);
    private ReturnFunction3 return3 = (x, y, z, d) -> value3(0, x, y, z);

    public Cellular1EdgeCustomizer(final CellularDistanceType distance) {
        this.distance2 = distance;
        this.distance3 = distance;
    }
}
