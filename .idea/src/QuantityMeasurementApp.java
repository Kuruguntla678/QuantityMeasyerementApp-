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
        public String toString() {
            return "Quantity(" + value + ", " + unit.getUnitName() + ")";
        }

        private double round(double val) {
            return Math.round(val * 100000.0) / 100000.0; // more precision for gallons
        }
    }

    public static void main(String[] args) {

        Quantity<LengthUnit> l1 = new Quantity<>(1, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12, LengthUnit.INCHES);

        System.out.println("Length Equal: " + l1.equals(l2));
        System.out.println("Length Convert: " + l1.convertTo(LengthUnit.INCHES));
        System.out.println("Length Add: " + l1.add(l2, LengthUnit.FEET));

        Quantity<WeightUnit> w1 = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000, WeightUnit.GRAM);

        System.out.println("Weight Equal: " + w1.equals(w2));
        System.out.println("Weight Convert: " + w1.convertTo(WeightUnit.GRAM));
        System.out.println("Weight Add: " + w1.add(w2, WeightUnit.KILOGRAM));

        Quantity<VolumeUnit> v1 = new Quantity<>(1, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> v3 = new Quantity<>(1, VolumeUnit.GALLON);

        System.out.println("Volume Equal (L vs mL): " + v1.equals(v2));
        System.out.println("Volume Equal (L vs Gallon): " + v1.equals(v3.convertTo(VolumeUnit.LITRE)));

        System.out.println("1 L to mL: " + v1.convertTo(VolumeUnit.MILLILITRE));
        System.out.println("1 Gallon to L: " + v3.convertTo(VolumeUnit.LITRE));

        System.out.println("Add L + mL: " + v1.add(v2, VolumeUnit.LITRE));
        System.out.println("Add L + Gallon in mL: " + v1.add(v3, VolumeUnit.MILLILITRE));

        System.out.println("Length vs Volume: " + l1.equals(v1));
    }
}