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

        public double convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) throw new IllegalArgumentException("Target unit null");

            double feet = unit.toFeet(value);
            return targetUnit.fromFeet(feet);
        }

        public Quantity add(Quantity other) {
            if (other == null) throw new IllegalArgumentException("Other cannot be null");

            double thisFeet = unit.toFeet(this.value);
            double otherFeet = other.unit.toFeet(other.value);
            double sumFeet = thisFeet + otherFeet;

            double resultValue = unit.fromFeet(sumFeet);

            return new Quantity(resultValue, this.unit);
        }

        public static Quantity add(Quantity q1, Quantity q2) {
            return q1.add(q2);
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Quantity other = (Quantity) obj;

            double f1 = unit.toFeet(value);
            double f2 = other.unit.toFeet(other.value);

            return Double.compare(f1, f2) == 0;
        }
    }

    public static void main(String[] args) {

        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCH);

        System.out.println(q1.add(q2));

        Quantity q3 = new Quantity(12.0, LengthUnit.INCH);
        Quantity q4 = new Quantity(1.0, LengthUnit.FEET);

        System.out.println(q3.add(q4));

        Quantity q5 = new Quantity(1.0, LengthUnit.YARD);
        Quantity q6 = new Quantity(3.0, LengthUnit.FEET);

        System.out.println(q5.add(q6));

        Quantity q7 = new Quantity(2.54, LengthUnit.CENTIMETER);
        Quantity q8 = new Quantity(1.0, LengthUnit.INCH);

        System.out.println(q7.add(q8));
    }
}