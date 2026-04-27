public class QuantityMeasurementApp {

    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARD(3.0),
        CENTIMETER(0.0328084);

        private final double toFeet;

        LengthUnit(double toFeet) {
            this.toFeet = toFeet;
        }

        public double toFeet(double value) {
            return value * toFeet;
        }

        public double fromFeet(double feetValue) {
            return feetValue / toFeet;
        }
    }

    static class Quantity {
        private double value;
        private LengthUnit unit;

        public Quantity(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid number");
            }
            this.value = value;
            this.unit = unit;
        }
        public double convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double valueInFeet = unit.toFeet(value);
            return targetUnit.fromFeet(valueInFeet);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Quantity other = (Quantity) obj;

            double thisFeet = unit.toFeet(value);
            double otherFeet = other.unit.toFeet(other.value);

            return Double.compare(thisFeet, otherFeet) == 0;
        }
    }

    public static double convert(double value, LengthUnit source, LengthUnit target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Units cannot be null");
        }
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid number");
        }

        double valueInFeet = source.toFeet(value);
        return target.fromFeet(valueInFeet);
    }

    public static void main(String[] args) {

        System.out.println(convert(1.0, LengthUnit.FEET, LengthUnit.INCH));     // 12.0
        System.out.println(convert(3.0, LengthUnit.YARD, LengthUnit.FEET));     // 9.0
        System.out.println(convert(36.0, LengthUnit.INCH, LengthUnit.YARD));    // 1.0
        System.out.println(convert(1.0, LengthUnit.CENTIMETER, LengthUnit.INCH)); // ~0.3937

        Quantity q = new Quantity(1.0, LengthUnit.YARD);
        System.out.println(q.convertTo(LengthUnit.INCH)); // 36.0
    }
}