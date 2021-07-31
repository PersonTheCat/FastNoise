package personthecat.fastnoise.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import personthecat.fastnoise.function.DistanceFunction2;
import personthecat.fastnoise.function.DistanceFunction3;
import personthecat.fastnoise.function.ReturnFunction3Edge;

@Data
@NoArgsConstructor
@Accessors(fluent = true)
public class Cellular3EdgeCustomizer {
    private DistanceFunction2 distance2 = CellularDistanceType.EUCLIDEAN;
    private DistanceFunction3 distance3 = CellularDistanceType.EUCLIDEAN;
    private ReturnFunction3Edge return3 = (d1, d2, d3) -> d3 - 1;

    public Cellular3EdgeCustomizer(final CellularDistanceType distance) {
        this.distance2 = distance;
        this.distance3 = distance;
    }
}
