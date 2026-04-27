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

    // Quantity class
    static class Quantity {
        private final double value;
        private final LengthUnit unit;

        public Quantity(double value, LengthUnit unit) {
            if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
            if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid value");

            this.value = value;
            this.unit = unit;
        }

        // UC6: add (result in first unit)
        public Quantity add(Quantity other) {
            return add(other, this.unit);
        }

        // ✅ UC7: add with explicit target unit
        public Quantity add(Quantity other, LengthUnit targetUnit) {
            if (other == null) throw new IllegalArgumentException("Other cannot be null");
            if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");

            // convert both to base unit (feet)
            double thisFeet = unit.toFeet(this.value);
            double otherFeet = other.unit.toFeet(other.value);

            // add
            double sumFeet = thisFeet + otherFeet;

            // convert to target unit
            double resultValue = targetUnit.fromFeet(sumFeet);

            return new Quantity(resultValue, targetUnit);
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }

    public static void main(String[] args) {

        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCH);

        System.out.println(q1.add(q2, LengthUnit.FEET));
        System.out.println(q1.add(q2, LengthUnit.INCH));
        System.out.println(q1.add(q2, LengthUnit.YARD));

        Quantity q3 = new Quantity(36.0, LengthUnit.INCH);
        Quantity q4 = new Quantity(1.0, LengthUnit.YARD);

        System.out.println(q3.add(q4, LengthUnit.FEET));

        Quantity q5 = new Quantity(2.54, LengthUnit.CENTIMETER);
        Quantity q6 = new Quantity(1.0, LengthUnit.INCH);

        System.out.println(q5.add(q6, LengthUnit.CENTIMETER));
    }
}