package ca.jrvs.exercise.mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NotSoSimpleCalculator_Test {
    @Mock
    SimpleCalculator calculator;
    NotSoSimpleCalculator notSoSimpleCalculator;
    
    @Before
    public void init() {
        this.notSoSimpleCalculator = new NotSoSimpleCalculatorImpl(calculator);
    }

    @Test
    public void test_abs_negative() {
        assertNotNull(calculator);
        when(this.calculator.multiply(-10, -1)).thenReturn(10);
        int expected = 10;
        int actual = this.notSoSimpleCalculator.abs(-10);
        assertEquals(expected, actual);
        verify(this.calculator, times(1)).multiply(-10, -1);
    }
    @Test
    public void test_abs_postitive() {
        assertNotNull(calculator);
        int expected = 10;
        int actual = this.notSoSimpleCalculator.abs(10);
        assertEquals(expected, actual);
        verify(this.calculator, never()).multiply(10, -1);
    }
    @Test
    public void test_pow() {
        // try(MockedStatic<Math> mockedMath = Mockito.mockStatic(Math.class);) {
        //     mockedMath.when(() -> Math.pow(3, 2)).thenReturn(9.0);
        int result = notSoSimpleCalculator.power(3, 2);
        assertEquals(9, result);

            // Verify that Math.pow was called with correct arguments
        //     mockedMath.verify(() -> Math.pow(3, 2), times(1));
        // }
    }
}
