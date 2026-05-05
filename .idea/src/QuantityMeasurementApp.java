public class QuantityMeasurementApp {

    public static void main(String[] args) {

        Quantity<LengthUnit> length1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> length2 = new Quantity<>(6.0, LengthUnit.INCHES);

        System.out.println("Length Subtract (implicit): " + length1.subtract(length2));
        System.out.println("Length Subtract (INCHES): " + length1.subtract(length2, LengthUnit.INCHES));
        System.out.println("Length Divide: " + length1.divide(new Quantity<>(2.0, LengthUnit.FEET)));

        Quantity<WeightUnit> weight1 = new Quantity<>(10.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> weight2 = new Quantity<>(5000.0, WeightUnit.GRAM);

        System.out.println("Weight Subtract: " + weight1.subtract(weight2));
        System.out.println("Weight Divide: " + weight1.divide(new Quantity<>(5.0, WeightUnit.KILOGRAM)));

        Quantity<VolumeUnit> vol1 = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> vol2 = new Quantity<>(2.0, VolumeUnit.LITRE);

        System.out.println("Volume Subtract (ML): " + vol1.subtract(vol2, VolumeUnit.MILLILITRE));
        System.out.println("Volume Divide: " + vol1.divide(new Quantity<>(10.0, VolumeUnit.LITRE)));
    }

    interface IMeasurable {
        double toBaseUnit(double value);
        double fromBaseUnit(double baseValue);
    }

    static class Quantity<U extends IMeasurable> {

        private final double value;
        private final U unit;

        public Quantity(double value, U unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
            this.value = value;
            this.unit = unit;
        }

        public double getValue() {
            return value;
        }

        public U getUnit() {
            return unit;
        }

        private enum ArithmeticOperation {
            ADD((a, b) -> a + b),
            SUBTRACT((a, b) -> a - b),
            DIVIDE((a, b) -> {
                if (b == 0.0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            });

            private final java.util.function.DoubleBinaryOperator op;

            ArithmeticOperation(java.util.function.DoubleBinaryOperator op) {
                this.op = op;
            }

            public double compute(double a, double b) {
                return op.applyAsDouble(a, b);
            }
        }

        private void validateArithmeticOperands(Quantity<U> other, U targetUnit, boolean targetRequired) {

            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            if (this.unit == null || other.unit == null) {
                throw new IllegalArgumentException("Units must not be null");
            }

            if (!this.unit.getClass().equals(other.unit.getClass())) {
                throw new IllegalArgumentException("Cross-category operation not allowed");
            }

            if (!Double.isFinite(this.value) || !Double.isFinite(other.value)) {
                throw new IllegalArgumentException("Values must be finite");
            }

            if (targetRequired && targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }
        }

        private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation) {
            double base1 = this.unit.toBaseUnit(this.value);
            double base2 = other.unit.toBaseUnit(other.value);

            return operation.compute(base1, base2);
        }

        private double roundToTwoDecimals(double val) {
            return Math.round(val * 100.0) / 100.0;
        }

        public Quantity<U> add(Quantity<U> other) {
            return add(other, this.unit);
        }

        public Quantity<U> add(Quantity<U> other, U targetUnit) {
            validateArithmeticOperands(other, targetUnit, true);

            double baseResult = performBaseArithmetic(other, ArithmeticOperation.ADD);
            double converted = targetUnit.fromBaseUnit(baseResult);

            return new Quantity<>(roundToTwoDecimals(converted), targetUnit);
        }

        public Quantity<U> subtract(Quantity<U> other) {
            return subtract(other, this.unit);
        }

        public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
            validateArithmeticOperands(other, targetUnit, true);

            double baseResult = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
            double converted = targetUnit.fromBaseUnit(baseResult);

            return new Quantity<>(roundToTwoDecimals(converted), targetUnit);
        }

        public double divide(Quantity<U> other) {
            validateArithmeticOperands(other, null, false);

            return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }

    enum LengthUnit implements IMeasurable {
        FEET(1.0),
        INCHES(1.0 / 12.0);

        private final double toFeet;

        LengthUnit(double toFeet) {
            this.toFeet = toFeet;
        }

        public double toBaseUnit(double value) {
            return value * toFeet;
        }

        public double fromBaseUnit(double baseValue) {
            return baseValue / toFeet;
        }
    }

    enum WeightUnit implements IMeasurable {
        KILOGRAM(1.0),
        GRAM(0.001);

        private final double toKg;

        WeightUnit(double toKg) {
            this.toKg = toKg;
        }

        public double toBaseUnit(double value) {
            return value * toKg;
        }

        public double fromBaseUnit(double baseValue) {
            return baseValue / toKg;
        }
    }

    enum VolumeUnit implements IMeasurable {
        LITRE(1.0),
        MILLILITRE(0.001);

        private final double toLitre;

        VolumeUnit(double toLitre) {
            this.toLitre = toLitre;
        }

        public double toBaseUnit(double value) {
            return value * toLitre;
        }

        public double fromBaseUnit(double baseValue) {
            return baseValue / toLitre;
        }
    }
}