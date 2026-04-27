import java.util.Objects;


enum WeightUnit {

    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double toKgFactor;

    WeightUnit(double toKgFactor) {
        this.toKgFactor = toKgFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * toKgFactor; // to kilograms
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toKgFactor; // from kilograms
    }
}


class QuantityWeight {

    private final double value;
    private final WeightUnit unit;

    public QuantityWeight(double value, WeightUnit unit) {
        if (unit == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid value or unit");
        }
        this.value = value;
        this.unit = unit;
    }

    public QuantityWeight convertTo(WeightUnit targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        double base = unit.convertToBaseUnit(value);
        double converted = targetUnit.convertFromBaseUnit(base);

        return new QuantityWeight(converted, targetUnit);
    }


    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        QuantityWeight other = (QuantityWeight) obj;

        double thisBase = this.unit.convertToBaseUnit(this.value);
        double otherBase = other.unit.convertToBaseUnit(other.value);

        return Math.abs(thisBase - otherBase) < 1e-6;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.convertToBaseUnit(value));
    }

    @Override
    public String toString() {
        return "Quantity(" + value + ", " + unit + ")";
    }


    public QuantityWeight add(QuantityWeight other) {
        return add(other, this.unit);
    }

    public QuantityWeight add(QuantityWeight other, WeightUnit targetUnit) {

        if (other == null || targetUnit == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);

        double sumBase = base1 + base2;
        double result = targetUnit.convertFromBaseUnit(sumBase);

        return new QuantityWeight(result, targetUnit);
    }
}


class QuantityMeasurementApp {

    public static void main(String[] args) {

        QuantityWeight w1 = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight w2 = new QuantityWeight(1000.0, WeightUnit.GRAM);

        System.out.println(w1.equals(w2)); // true

        System.out.println(w1.convertTo(WeightUnit.GRAM)); // 1000 g

        System.out.println(w1.add(w2)); // 2 kg

        System.out.println(w1.add(w2, WeightUnit.POUND)); // in pounds
    }
}


class QuantityMeasurementTest {

    public static void main(String[] args) {

        // Equality
        assert new QuantityWeight(1.0, WeightUnit.KILOGRAM)
                .equals(new QuantityWeight(1000.0, WeightUnit.GRAM));

        assert new QuantityWeight(2.20462, WeightUnit.POUND)
                .equals(new QuantityWeight(1.0, WeightUnit.KILOGRAM));

        // Conversion
        assert Math.abs(
                new QuantityWeight(1.0, WeightUnit.KILOGRAM)
                        .convertTo(WeightUnit.GRAM).toString()
                        .contains("1000")) : true;


        QuantityWeight a = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight b = new QuantityWeight(2.0, WeightUnit.KILOGRAM);

        assert a.add(b).equals(new QuantityWeight(3.0, WeightUnit.KILOGRAM));


        QuantityWeight c = new QuantityWeight(1000.0, WeightUnit.GRAM);

        assert a.add(c).equals(new QuantityWeight(2.0, WeightUnit.KILOGRAM));

        QuantityWeight result = a.add(c, WeightUnit.GRAM);

        assert result.equals(new QuantityWeight(2000.0, WeightUnit.GRAM));

        System.out.println("All UC9 tests passed.");
    }
}