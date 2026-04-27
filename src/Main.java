public class QuantityMeasurementApp {

    enum LengthUnit {

        FEET(1.0),
        INCH(1.0 / 12.0),
        YARD(3.0),
        CENTIMETER(1.0 / 30.48);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        // Convert unit → base unit (FEET)
        public double toBaseUnit(double value) {
            return value * toFeetFactor;
        }

        // Convert base unit (FEET) → this unit
        public double fromBaseUnit(double baseValue) {
            return baseValue / toFeetFactor;
        }
    }

    // ===================== QUANTITY CLASS =====================
    static class Quantity {

        private final double value;
        private final LengthUnit unit;

        public Quantity(double value, LengthUnit unit) {
            if (unit == null || !Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid value or unit");
            }
            this.value = value;
            this.unit = unit;
        }

        public Quantity convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double base = unit.toBaseUnit(value);
            double converted = targetUnit.fromBaseUnit(base);

            return new Quantity(converted, targetUnit);
        }

        public Quantity add(Quantity other) {
            return add(other, this.unit);
        }

        public Quantity add(Quantity other, LengthUnit targetUnit) {
            if (other == null || targetUnit == null) {
                throw new IllegalArgumentException("Null not allowed");
            }

            double sumInBase =
                    this.unit.toBaseUnit(this.value)
                            + other.unit.toBaseUnit(other.value);

            double result = targetUnit.fromBaseUnit(sumInBase);

            return new Quantity(result, targetUnit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Quantity)) return false;

            Quantity other = (Quantity) obj;

            return Double.compare(
                    this.unit.toBaseUnit(this.value),
                    other.unit.toBaseUnit(other.value)
            ) == 0;
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }


    public static void main(String[] args) {

        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCH);


        System.out.println(q1.convertTo(LengthUnit.INCH)); // 12.0


        System.out.println(q1.add(q2)); // 2.0 FEET


        System.out.println(q1.add(q2, LengthUnit.YARD)); // ~0.667 YARD

        System.out.println(
                new Quantity(36.0, LengthUnit.INCH)
                        .equals(new Quantity(1.0, LengthUnit.YARD))
        );

        System.out.println(LengthUnit.INCH.toBaseUnit(12)); // 1.0
        System.out.println(LengthUnit.YARD.fromBaseUnit(3)); // 1.0
    }
}