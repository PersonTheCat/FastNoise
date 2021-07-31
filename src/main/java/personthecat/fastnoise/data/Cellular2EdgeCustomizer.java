package personthecat.fastnoise.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import personthecat.fastnoise.function.DistanceFunction2;
import personthecat.fastnoise.function.DistanceFunction3;
import personthecat.fastnoise.function.ReturnFunction2Edge;

@Data
@NoArgsConstructor
@Accessors(fluent = true)
public class Cellular2EdgeCustomizer {
    private DistanceFunction2 distance2 = CellularDistanceType.EUCLIDEAN;
    private DistanceFunction3 distance3 = CellularDistanceType.EUCLIDEAN;
    private ReturnFunction2Edge return2 = (d1, d2) -> d2 - 1;

    public Cellular2EdgeCustomizer(final CellularDistanceType distance) {
        this.distance2 = distance;
        this.distance3 = distance;
    }
}
