public class QuantityMeasurementApp {

    // 1. Interface
    interface IMeasurable {
        double getConversionFactor();

        default double convertToBaseUnit(double value) {
            return value * getConversionFactor();
        }

        default double convertFromBaseUnit(double baseValue) {
            return baseValue / getConversionFactor();
        }

        String getUnitName();
    }

    // 2. Length Units
    enum LengthUnit implements IMeasurable {
        FEET(12.0),
        INCHES(1.0),
        YARDS(36.0),
        CENTIMETERS(0.393701);

        private final double factor;

        LengthUnit(double factor) {
            this.factor = factor;
        }

        public double getConversionFactor() {
            return factor;
        }

        public String getUnitName() {
            return name();
        }
    }

    // 3. Weight Units
    enum WeightUnit implements IMeasurable {
        KILOGRAM(1000.0),
        GRAM(1.0);

        private final double factor;

        WeightUnit(double factor) {
            this.factor = factor;
        }

        public double getConversionFactor() {
            return factor;
        }

        public String getUnitName() {
            return name();
        }
    }

    // 4. Generic Quantity Class
    static class Quantity<U extends IMeasurable> {

        private final double value;
        private final U unit;

        public Quantity(double value, U unit) {
            if (unit == null || Double.isNaN(value) || Double.isInfinite(value)) {
                throw new IllegalArgumentException("Invalid value or unit");
            }
            this.value = value;
            this.unit = unit;
        }

        public Quantity<U> convertTo(U targetUnit) {
            double base = unit.convertToBaseUnit(value);
            double converted = targetUnit.convertFromBaseUnit(base);
            return new Quantity<>(round(converted), targetUnit);
        }

        public Quantity<U> add(Quantity<U> other, U targetUnit) {
            double base1 = unit.convertToBaseUnit(value);
            double base2 = other.unit.convertToBaseUnit(other.value);

            double sum = base1 + base2;
            double result = targetUnit.convertFromBaseUnit(sum);

            return new Quantity<>(round(result), targetUnit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Quantity<?> other)) return false;

            // Prevent cross-category comparison
            if (!unit.getClass().equals(other.unit.getClass())) return false;

            double base1 = unit.convertToBaseUnit(value);
            double base2 = other.unit.convertToBaseUnit(other.value);

            return Double.compare(base1, base2) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(unit.convertToBaseUnit(value));
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit.getUnitName() + ")";
        }

        private double round(double val) {
            return Math.round(val * 100.0) / 100.0;
        }
    }

    // 5. Main Method (Demo)
    public static void main(String[] args) {

        // Length
        Quantity<LengthUnit> q1 = new Quantity<>(1, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12, LengthUnit.INCHES);

        System.out.println("Length Equal: " + q1.equals(q2));
        System.out.println("Length Convert: " + q1.convertTo(LengthUnit.INCHES));
        System.out.println("Length Add: " + q1.add(q2, LengthUnit.FEET));

        // Weight
        Quantity<WeightUnit> w1 = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000, WeightUnit.GRAM);

        System.out.println("Weight Equal: " + w1.equals(w2));
        System.out.println("Weight Convert: " + w1.convertTo(WeightUnit.GRAM));
        System.out.println("Weight Add: " + w1.add(w2, WeightUnit.KILOGRAM));

        // Cross-category check
        System.out.println("Cross Equal (Length vs Weight): " + q1.equals(w1));
    }
}