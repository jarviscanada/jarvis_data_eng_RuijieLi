package ca.jrvs.exercise.mockito;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SimpleCalculator_Test {
	
	SimpleCalculator calculator;
    @Before
    public void init() {
		this.calculator = new SimpleCalculatorImp();
	}
    @Test
	public void test_add() {
		int expected = 2;
		int actual = calculator.add(1, 1);
        assertEquals(expected, actual);
	}
    @Test
	public void test_subtract() {
		int expected = 0;
        int actual = calculator.subtract(10, 10);
        assertEquals(expected, actual);
	}
	
	@Test
	public void test_multiply() {
		//write your test here
        int expected = 100;
        int actual = calculator.multiply(10, 10);
        assertEquals(expected, actual);
	}
	
	@Test
	public void test_divide() {
		//write your test here
        double expected = 2.5;
        double actual = calculator.divide(10, 4);
        assertEquals(expected, actual);
	}

    @Test
    public void test_divide_by_zero() {
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            this.calculator.divide(1, 0);
        });
        assertEquals("Cannot divide by zero", e.getMessage());
    }
}
