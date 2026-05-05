public class QuantityMeasurementApp {

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

    enum LengthUnit implements IMeasurable {
        FEET(12.0),
        INCHES(1.0);

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

    enum VolumeUnit implements IMeasurable {
        LITRE(1.0),
        MILLILITRE(0.001),
        GALLON(3.78541);

        private final double factor;

        VolumeUnit(double factor) {
            this.factor = factor;
        }

        public double getConversionFactor() {
            return factor;
        }

        public String getUnitName() {
            return name();
        }
    }


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
            double result = targetUnit.convertFromBaseUnit(base);
            return new Quantity<>(round(result), targetUnit);
        }


        public Quantity<U> add(Quantity<U> other, U targetUnit) {
            validate(other);
            double sum = toBase() + other.toBase();
            return new Quantity<>(round(targetUnit.convertFromBaseUnit(sum)), targetUnit);
        }

        public Quantity<U> subtract(Quantity<U> other) {
            return subtract(other, unit);
        }

        public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
            validate(other);
            if (targetUnit == null) throw new IllegalArgumentException("Target unit null");

            double result = toBase() - other.toBase();
            return new Quantity<>(round(targetUnit.convertFromBaseUnit(result)), targetUnit);
        }

        public double divide(Quantity<U> other) {
            validate(other);
            double divisor = other.toBase();

            if (divisor == 0) throw new ArithmeticException("Divide by zero");

            return toBase() / divisor;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Quantity<?> other)) return false;

            if (!unit.getClass().equals(other.unit.getClass())) return false;

            return Double.compare(toBase(), other.toBase()) == 0;
        }

        private double toBase() {
            return unit.convertToBaseUnit(value);
        }

        private void validate(Quantity<U> other) {
            if (other == null) throw new IllegalArgumentException("Null quantity");
            if (!unit.getClass().equals(other.unit.getClass()))
                throw new IllegalArgumentException("Different measurement types");
        }

        private double round(double v) {
            return Math.round(v * 100.0) / 100.0;
        }

        public String toString() {
            return "Quantity(" + value + ", " + unit.getUnitName() + ")";
        }
    }

    public static void main(String[] args) {

        Quantity<LengthUnit> l1 = new Quantity<>(10, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6, LengthUnit.INCHES);

        System.out.println("Subtract Length: " + l1.subtract(l2));
        System.out.println("Subtract Length (in inches): " + l1.subtract(l2, LengthUnit.INCHES));
        System.out.println("Divide Length: " + l1.divide(new Quantity<>(2, LengthUnit.FEET)));

        Quantity<WeightUnit> w1 = new Quantity<>(10, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5000, WeightUnit.GRAM);

        System.out.println("Subtract Weight: " + w1.subtract(w2));
        System.out.println("Divide Weight: " + w1.divide(new Quantity<>(5, WeightUnit.KILOGRAM)));

        Quantity<VolumeUnit> v1 = new Quantity<>(5, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500, VolumeUnit.MILLILITRE);

        System.out.println("Subtract Volume: " + v1.subtract(v2));
        System.out.println("Divide Volume: " + v1.divide(new Quantity<>(10, VolumeUnit.LITRE)));

        System.out.println("Negative: " + v2.subtract(v1));

        System.out.println("Zero: " + v1.subtract(new Quantity<>(5000, VolumeUnit.MILLILITRE)));
    }
}