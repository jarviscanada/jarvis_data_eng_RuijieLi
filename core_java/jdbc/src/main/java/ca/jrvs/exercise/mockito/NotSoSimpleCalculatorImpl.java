package ca.jrvs.exercise.mockito;

public class NotSoSimpleCalculatorImpl implements NotSoSimpleCalculator {
    
    private SimpleCalculator calc;
    
    public NotSoSimpleCalculatorImpl(SimpleCalculator calc) {
        this.calc = calc;
    }

    @Override
    public int power(int x, int y) {
        return (int)Math.pow(x, y);
    }

    @Override
    public int abs(int x) {
        return x < 0 ? calc.multiply(x, -1) : x;
    }

    @Override
    public double sqrt(int x) {
        return Math.sqrt(x);
    }
}