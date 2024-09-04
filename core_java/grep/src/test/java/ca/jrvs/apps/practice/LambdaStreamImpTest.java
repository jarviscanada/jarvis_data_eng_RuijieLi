package ca.jrvs.apps.practice;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
//import org.junit.*;

public class LambdaStreamImpTest {
    private LambdaStreamImp lambdaStreamImp;
    @Before
    public void init() {
        lambdaStreamImp = new LambdaStreamImp();
    }

    @Test
    public void testCreateStrStreamArray() {
        String[] testStrings = {"", "123456789", "QWERTYUIOP", "\n\t\b\t\""};
        Stream<String> stream = lambdaStreamImp.createStrStream(testStrings);
        assertArrayEquals(testStrings, stream.toArray());
    }
    @Test
    public void testCreateStrStreamMultipleParameters() {
        String[] testStrings = {"", "123456789", "QWERTYUIOP", "\n\t\b\t\""};
        Stream<String> stream = lambdaStreamImp.createStrStream("", "123456789", "QWERTYUIOP", "\n\t\b\t\"");
        assertArrayEquals(testStrings, stream.toArray());
    }
    @Test
    public void testToUpperCase() {
        String[] strings = {"", "1a", "1", "a", "Aa"};
        Stream<String> stream = lambdaStreamImp.toUpperCase(strings);
        assertArrayEquals(new String[]{"", "1A", "1", "A", "AA"}, stream.toArray());
    }

    @Test
    public void testFilterRegex() {
        String regex = "abc";
        Stream<String> toFilter = Arrays.stream(new String[]{
            "12",
            "abc",
            "awewewewewabc",
            "abcabcabcabcabc",
            "abDEc"
        });
        String[] answer = new String[] {
            "abc",
            "awewewewewabc",
            "abcabcabcabcabc"
        };
        Stream<String> filtered = lambdaStreamImp.filter(toFilter, regex);
        assertArrayEquals(filtered.toArray(), answer);
    }

    @Test
    public void testCreateIntStream() {
        int[] testArr = {
          1,12,3,4,5,6,0
        };
        int[] answer = {
            1,12,3,4,5,6,0
        };
        IntStream stream = lambdaStreamImp.createIntStream(testArr);
        assertArrayEquals(stream.toArray(), answer);
    }

    @Test
    public void testToListGeneric() {
        class TestObj {
            private int testAttr1;
            private String testAttr2;
            TestObj(int i, String s) {
                testAttr1 = i;
                testAttr2 = s;
            }
            public int getTestAttr1() {
                return testAttr1;
            }
            public String getTestAttr2() {
                return testAttr2;
            }
            public boolean equals(TestObj t) {
                return t.testAttr1 == testAttr1 && Objects.equals(t.testAttr2, testAttr2);
            }
        };
        TestObj[] testObjs = new TestObj[] {
            new TestObj(1, "1"),
            new TestObj(2, "2"),
            new TestObj(3, "3"),
        };
        List<TestObj> list = lambdaStreamImp.toList(
            Arrays.stream(testObjs)
        );
        assertArrayEquals(testObjs, list.toArray());
    }

    @Test
    public void testToListInt() {
        Integer[] answer = {1,2,3};
        List<Integer> list = lambdaStreamImp.toList(Arrays.stream((new int[]{1,2,3})));
        assertArrayEquals(answer, list.toArray());
    }

    @Test
    public void testCreateIntStreamRange() {
        int start = 1;
        int end = 11;
        int[] answer = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        IntStream stream = lambdaStreamImp.createIntStream(start, end);
        assertArrayEquals(answer, stream.toArray());
    }

    @Test
    public void testSquareRootIntStream() {

        DoubleStream stream = lambdaStreamImp.squareRootIntStream(
            Arrays.stream(new int[]{0,1,2,3,4,5,6,7,8,9})
        );
        assertArrayEquals(stream.boxed().toArray(),
            IntStream.range(0, 10)
                .mapToDouble(Math::sqrt)
                .boxed().toArray());
    }

    @Test
    public void testGetOdd() {
        int[] start = {1,2,3,4,5,6,7,8,9};
        int[] answer = {1,3,5,7,9};
        IntStream stream = lambdaStreamImp.getOdd(Arrays.stream(start));
        assertArrayEquals(
            answer,
            stream.toArray()
        );
    }

    private ByteArrayOutputStream errContent;
    private ByteArrayOutputStream outContent;

    private PrintStream originalOut;
    private PrintStream originalErr;


    private void setToCustomIO() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    private void returnToOriginalIO() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testGetLambdaPrinter() {
        String prefix = "pre";
        String suffix = "suf";
        String teststr = "teststr";
        setToCustomIO();
        Consumer<String> consumer = lambdaStreamImp.getLambdaPrinter(prefix, suffix);
        consumer.accept(teststr);
        String result = outContent.toString();
        assertEquals(prefix + teststr + suffix,
            result
                .replace("\n", "")
                .replace("\r", "")
        );
        returnToOriginalIO();
    }

    @Test
    public void testPrintMessages() {
        String prefix = "pre";
        String suffix = "suf";
        String[] testStrings = {
            "",
            "12",
            "QWERTYUIOP"
        };
        String[] correctResult = {
            prefix + suffix,
            prefix + "12" + suffix,
            prefix + "QWERTYUIOP" + suffix
        };
        Consumer<String> consumer = s -> System.out.println(prefix + s + suffix);
        setToCustomIO();
        lambdaStreamImp.printMessages(testStrings, consumer);
        assertArrayEquals(
            correctResult,
            outContent.toString().split("\n")
        );
        returnToOriginalIO();
    }

    @Test
    public void testPrintOdd() {
        int[] integers = {
            1, 2, 3, 4, 5, 6, 7, 8, 9
        };
        String expectedAnswer = "13579";
        IntStream intStream = Arrays.stream(integers);
        setToCustomIO();
        Consumer<String> consumer = System.out::print;
        lambdaStreamImp.printOdd(intStream, consumer);
        assertEquals(expectedAnswer, outContent.toString());
        returnToOriginalIO();
    }

    @Test
    public void testFlatNestedInt() {
        ArrayList<List<Integer>> nestedInts = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(1, 2, 3)),
            new ArrayList<>(Arrays.asList(4, 5, 6)),
            new ArrayList<>(Arrays.asList(7, 8, 9))
        ));
        Integer[] expectedAnswer = {
            1,2,3,4,5,6,7,8,9
        };
        Stream<Integer> stream = lambdaStreamImp.flatNestedInt(nestedInts.stream());
        assertArrayEquals(expectedAnswer, stream.toArray());
    }
}
