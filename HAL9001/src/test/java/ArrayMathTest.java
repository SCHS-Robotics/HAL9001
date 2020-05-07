import com.SCHSRobotics.HAL9001.util.math.FakeNumpy;

import org.junit.Assert;
import org.junit.Test;

/**
 * @// TODO: 12/22/2019
 */
public class ArrayMathTest {

    @Test
    public void validateMaxTypeNonEmpty() {
        Integer[] testArr = new Integer[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        Integer expectedResult = 6;
        Assert.assertEquals(expectedResult, FakeNumpy.max(testArr));
    }

    @Test
    public void validateMaxTypeEmpty() {
        Integer[] testArr = new Integer[] {};
        Integer expectedResult = null;
        Assert.assertEquals(expectedResult, FakeNumpy.max(testArr));
    }

    @Test
    public void validateMaxTypeDoubleNonEmpty() {
        double[] testArr = new double[] {-6.6,-5.5,-4.4,-3.3,-2.2,-1.1,0.1,0.0,1.1,1.1,2.2,3.3,4.4,5.5,6.6,6.0};
        Assert.assertEquals(6.6, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMaxTypeDoubleEmpty() {
        double[] testArr = new double[] {};
        Assert.assertEquals(0.0, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMaxTypeIntegerNonEmpty() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        Assert.assertEquals(6, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMaxTypeIntegerEmpty() {
        int[] testArr = new int[] {};
        Assert.assertEquals(0, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMaxTypeFloatNonEmpty() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        Assert.assertEquals(6.6f, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMaxTypeFloatEmpty() {
        int[] testArr = new int[] {};
        Assert.assertEquals(0, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMaxTypeLongNonEmpty() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        Assert.assertEquals(6L, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMaxTypeLongEmpty() {
        long[] testArr = new long[] {};
        Assert.assertEquals(0, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMaxTypeShortNonEmpty() {
        short[] testArr = new short[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        Assert.assertEquals(6, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMaxTypeShortEmpty() {
        short[] testArr = new short[] {};
        Assert.assertEquals(0, FakeNumpy.max(testArr),0.001);
    }

    @Test
    public void validateMinTypeNonEmpty() {
        Integer[] testArr = new Integer[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        Integer expectedResult = -6;
        Assert.assertEquals(expectedResult, FakeNumpy.min(testArr));
    }

    @Test
    public void validateMinTypeEmpty() {
        Integer[] testArr = new Integer[] {};
        Integer expectedResult = null;
        Assert.assertEquals(expectedResult, FakeNumpy.min(testArr));
    }

    @Test
    public void validateMinTypeDoubleNonEmpty() {
        double[] testArr = new double[] {-6.6,-5.5,-4.4,-3.3,-2.2,-1.1,0.1,0.0,1.1,1.1,2.2,3.3,4.4,5.5,6.6,6.0};
        Assert.assertEquals(-6.6, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateMinTypeDoubleEmpty() {
        double[] testArr = new double[] {};
        Assert.assertEquals(0.0, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateMinTypeIntegerNonEmpty() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        Assert.assertEquals(-6, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateMinTypeIntegerEmpty() {
        int[] testArr = new int[] {};
        Assert.assertEquals(0, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateMinTypeFloatNonEmpty() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        Assert.assertEquals(-6.6f, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateMinTypeFloatEmpty() {
        int[] testArr = new int[] {};
        Assert.assertEquals(0, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateMinTypeLongNonEmpty() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        Assert.assertEquals(-6L, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateMinTypeLongEmpty() {
        long[] testArr = new long[] {};
        Assert.assertEquals(0, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateMinTypeShortNonEmpty() {
        short[] testArr = new short[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        Assert.assertEquals(-6, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateMinTypeShortEmpty() {
        short[] testArr = new short[] {};
        Assert.assertEquals(0, FakeNumpy.min(testArr),0.001);
    }

    @Test
    public void validateSliceType() {
        Integer[] testArr = new Integer[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        Integer[] expectedResult = new Integer[] {-5,-4,-3};
        Assert.assertArrayEquals(expectedResult, FakeNumpy.slice(testArr,1,3));
    }

    @Test
    public void validateSliceTypeDouble() {
        double[] testArr = new double[] {-6.6,-5.5,-4.4,-3.3,-2.2,-1.1,0.1,0.0,1.1,1.1,2.2,3.3,4.4,5.5,6.6,6.0};
        double[] expectedResult = new double[] {-5.5,-4.4,-3.3};
        Assert.assertArrayEquals(expectedResult, FakeNumpy.slice(testArr,1,3),0.001);
    }

    @Test
    public void validateSliceTypeInteger() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        int[] expectedResult = new int[] {-5,-4,-3};
        Assert.assertArrayEquals(expectedResult, FakeNumpy.slice(testArr,1,3));
    }

    @Test
    public void validateSliceTypeFloat() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        float[] expectedResult = new float[] {-5.5f,-4.4f,-3.3f};
        Assert.assertArrayEquals(expectedResult, FakeNumpy.slice(testArr,1,3),0.001f);
    }

    @Test
    public void validateSliceTypeLong() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        long[] expectedResult = new long[] {-5L,-4L,-3L};
        Assert.assertArrayEquals(expectedResult, FakeNumpy.slice(testArr,1,3));
    }

    @Test
    public void validateSliceTypeShort() {
        short[] testArr = new short[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        short[] expectedResult = new short[] {-5,-4,-3};
        Assert.assertArrayEquals(expectedResult, FakeNumpy.slice(testArr,1,3));
    }

    @Test
    public void validateDoubleArrayMultiplyDoubleNonEmpty() {
        double[] testArr = new double[] {-6.6,-5.5,-4.4,-3.3,-2.2,-1.1,0.1,0.0,1.1,1.1,2.2,3.3,4.4,5.5,6.6,6.0};
        double[] expectedResult = new double[] {-13.2,-11.0,-8.8,-6.6,-4.4,-2.2,0.2,0.0,2.2,2.2,4.4,6.6,8.8,11.0,13.2,12.0};
        FakeNumpy.multiply(testArr,2.0);
        Assert.assertArrayEquals(expectedResult, testArr,0.001);
    }

    @Test
    public void validateDoubleArrayMultiplyDoubleEmpty() {
        double[] testArr = new double[] {};
        double[] expectedResult = new double[] {};
        FakeNumpy.multiply(testArr,2.0);
        Assert.assertArrayEquals(expectedResult, testArr,0.001);
    }

    @Test
    public void validateIntegerArrayMultiplyIntegerNonEmpty() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        int[] expectedResult = new int[] {-12,-10,-8,-6,-4,-2,0,0,2,2,4,6,8,10,12,12};
        FakeNumpy.multiply(testArr,2);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateIntegerArrayMultiplyIntegerEmpty() {
        int[] testArr = new int[] {};
        int[] expectedResult = new int[] {};
        FakeNumpy.multiply(testArr,2);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateFloatArrayMultiplyFloatNonEmpty() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        float[] expectedResult = new float[] {-13.2f,-11.0f,-8.8f,-6.6f,-4.4f,-2.2f,0.2f,0.0f,2.2f,2.2f,4.4f,6.6f,8.8f,11.0f,13.2f,12.0f};
        FakeNumpy.multiply(testArr,2.0f);
        Assert.assertArrayEquals(expectedResult, testArr,0.001f);
    }

    @Test
    public void validateFloatArrayMultiplyFloatEmpty() {
        float[] testArr = new float[] {};
        float[] expectedResult = new float[] {};
        FakeNumpy.multiply(testArr,2.0f);
        Assert.assertArrayEquals(expectedResult, testArr,0.001f);
    }

    @Test
    public void validateLongArrayMultiplyLongNonEmpty() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        long[] expectedResult = new long[] {-12L,-10L,-8L,-6L,-4L,-2L,0L,0L,2L,2L,4L,6L,8L,10L,12L,12L};
        FakeNumpy.multiply(testArr,2L);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateLongArrayMultiplyLongEmpty() {
        long[] testArr = new long[] {};
        long[] expectedResult = new long[] {};
        FakeNumpy.multiply(testArr,2L);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateIntegerArrayMultiplyDoubleNonEmpty() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        int[] expectedResult = new int[] {-15,-12,-10,-7,-5,-2,0,0,3,3,5,8,10,13,15,15};
        FakeNumpy.multiply(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateIntegerArrayMultiplyDoubleEmpty() {
        int[] testArr = new int[] {};
        int[] expectedResult = new int[] {};
        FakeNumpy.multiply(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateFloatArrayMultiplyDoubleNonEmpty() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        float[] expectedResult = new float[] {-16.5f,-13.75f,-11.0f,-8.25f,-5.5f,-2.75f,0.25f,0.0f,2.75f,2.75f,5.5f,8.25f,11.0f,13.75f,16.5f,15.0f};
        FakeNumpy.multiply(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr,0.001f);
    }

    @Test
    public void validateFloatArrayMultiplyDoubleEmpty() {
        float[] testArr = new float[] {};
        float[] expectedResult = new float[] {};
        FakeNumpy.multiply(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr,0.001f);
    }

    @Test
    public void validateLongArrayMultiplyDoubleNonEmpty() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        long[] expectedResult = new long[] {-15L,-12L,-10L,-7L,-5L,-2L,0L,0L,3L,3L,5L,8L,10L,13L,15L,15L};
        FakeNumpy.multiply(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateLongArrayMultiplyDoubleEmpty() {
        long[] testArr = new long[] {};
        long[] expectedResult = new long[] {};
        FakeNumpy.multiply(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateDoubleArrayDivideDoubleNonEmpty() {
        double[] testArr = new double[] {-6.6,-5.5,-4.4,-3.3,-2.2,-1.1,0.1,0.0,1.1,1.1,2.2,3.3,4.4,5.5,6.6,6.0};
        double[] expectedResult = new double[] {-3.3,-2.75,-2.2,-1.65,-1.1,-0.55,0.05,0.0,0.55,0.55,1.1,1.65,2.2,2.75,3.3,3.0};
        FakeNumpy.divide(testArr,2.0);
        Assert.assertArrayEquals(expectedResult, testArr,0.001);
    }

    @Test
    public void validateDoubleArrayDivideDoubleEmpty() {
        double[] testArr = new double[] {};
        double[] expectedResult = new double[] {};
        FakeNumpy.divide(testArr,2.0);
        Assert.assertArrayEquals(expectedResult, testArr,0.001);
    }

    @Test(expected = ArithmeticException.class)
    public void validateDoubleArrayDivideDoubleZero() {
        double[] testArr = new double[] {-6.6,-5.5,-4.4,-3.3,-2.2,-1.1,0.1,0.0,1.1,1.1,2.2,3.3,4.4,5.5,6.6,6.0};
        FakeNumpy.divide(testArr,0.0);
    }

    @Test
    public void validateIntegerArrayDivideIntegerNonEmpty() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        int[] expectedResult = new int[] {-3,-2,-2,-1,-1,0,0,0,1,1,1,2,2,3,3,3};
        FakeNumpy.divide(testArr,2);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateIntegerArrayDivideIntegerEmpty() {
        int[] testArr = new int[] {};
        int[] expectedResult = new int[] {};
        FakeNumpy.divide(testArr,2);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test(expected = ArithmeticException.class)
    public void validateIntegerArrayDivideIntegerZero() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        FakeNumpy.divide(testArr,0);
    }

    @Test
    public void validateFloatArrayDivideFloatNonEmpty() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        float[] expectedResult = new float[] {-3.3f,-2.75f,-2.2f,-1.65f,-1.1f,-0.55f,0.05f,0.0f,0.55f,0.55f,1.1f,1.65f,2.2f,2.75f,3.3f,3.0f};
        FakeNumpy.divide(testArr,2.0f);
        Assert.assertArrayEquals(expectedResult, testArr,0.001f);
    }

    @Test
    public void validateFloatArrayDivideFloatEmpty() {
        float[] testArr = new float[] {};
        float[] expectedResult = new float[] {};
        FakeNumpy.divide(testArr,2.0f);
        Assert.assertArrayEquals(expectedResult, testArr,0.001f);
    }

    @Test(expected = ArithmeticException.class)
    public void validateFloatArrayDivideFloatZero() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        FakeNumpy.divide(testArr,0.0f);
    }

    @Test
    public void validateLongArrayDivideLongNonEmpty() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        long[] expectedResult = new long[] {-3L,-2L,-2L,-1L,-1L,0L,0L,0L,1L,1L,1L,2L,2L,3L,3L,3L};
        FakeNumpy.divide(testArr,2L);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateLongArrayDivideLongEmpty() {
        long[] testArr = new long[] {};
        long[] expectedResult = new long[] {};
        FakeNumpy.divide(testArr,2L);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test(expected = ArithmeticException.class)
    public void validateLongArrayDivideLongZero() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        FakeNumpy.divide(testArr,0L);
    }

    @Test
    public void validateIntegerArrayDivideDoubleNonEmpty() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        int[] expectedResult = new int[] {-2,-2,-2,-1,-1,0,0,0,0,0,1,1,2,2,2,2};
        FakeNumpy.divide(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateIntegerArrayDivideDoubleEmpty() {
        int[] testArr = new int[] {};
        int[] expectedResult = new int[] {};
        FakeNumpy.divide(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test(expected = ArithmeticException.class)
    public void validateIntegerArrayDivideDoubleZero() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        FakeNumpy.divide(testArr,0.0);
    }

    @Test
    public void validateFloatArrayDivideDoubleNonEmpty() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        float[] expectedResult = new float[] {-2.64f,-2.2f,-1.76f,-1.32f,-0.88f,-0.44f,0.04f,0.0f,0.44f,0.44f,0.88f,1.32f,1.76f,2.2f,2.64f,2.4f};
        FakeNumpy.divide(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr,0.001f);
    }

    @Test
    public void validateFloatArrayDivideDoubleEmpty() {
        float[] testArr = new float[] {};
        float[] expectedResult = new float[] {};
        FakeNumpy.divide(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr,0.001f);
    }

    @Test(expected = ArithmeticException.class)
    public void validateFloatArrayDivideDoubleZero() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        FakeNumpy.divide(testArr,0.0);
    }

    @Test
    public void validateLongArrayDivideDoubleNonEmpty() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        long[] expectedResult = new long[] {-2L,-2L,-2L,-1L,-1L,0L,0L,0L,0L,0L,1L,1L,2L,2L,2L,2L};
        FakeNumpy.divide(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test
    public void validateLongArrayDivideDoubleEmpty() {
        long[] testArr = new long[] {};
        long[] expectedResult = new long[] {};
        FakeNumpy.divide(testArr,2.5);
        Assert.assertArrayEquals(expectedResult, testArr);
    }

    @Test(expected = ArithmeticException.class)
    public void validateLongArrayDivideDoubleZero() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        FakeNumpy.divide(testArr,0.0);
    }

    //------

    @Test
    public void validateAbsDoubleNonEmpty() {
        double[] testArr = new double[] {-6.6,-5.5,-4.4,-3.3,-2.2,-1.1,0.1,0.0,1.1,1.1,2.2,3.3,4.4,5.5,6.6,6.0};
        double[] expectedResult = new double[] {6.6,5.5,4.4,3.3,2.2,1.1,0.1,0.0,1.1,1.1,2.2,3.3,4.4,5.5,6.6,6.0};

        Assert.assertArrayEquals(expectedResult, FakeNumpy.abs(testArr),0.001);
    }

    @Test
    public void validateAbsDoubleEmpty() {
        double[] testArr = new double[] {};
        double[] expectedResult = new double[] {};

        Assert.assertArrayEquals(expectedResult, FakeNumpy.abs(testArr),0.001);
    }

    @Test
    public void validateAbsIntegerNonEmpty() {
        int[] testArr = new int[] {-6,-5,-4,-3,-2,-1,0,0,1,1,2,3,4,5,6,6};
        int[] expectedResult = new int[] {6,5,4,3,2,1,0,0,1,1,2,3,4,5,6,6};

        Assert.assertArrayEquals(expectedResult, FakeNumpy.abs(testArr));
    }

    @Test
    public void validateAbsIntegerEmpty() {
        int[] testArr = new int[] {};
        int[] expectedResult = new int[] {};

        Assert.assertArrayEquals(expectedResult, FakeNumpy.abs(testArr));
    }

    @Test
    public void validateAbsFloatNonEmpty() {
        float[] testArr = new float[] {-6.6f,-5.5f,-4.4f,-3.3f,-2.2f,-1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};
        float[] expectedResult = new float[] {6.6f,5.5f,4.4f,3.3f,2.2f,1.1f,0.1f,0.0f,1.1f,1.1f,2.2f,3.3f,4.4f,5.5f,6.6f,6.0f};

        Assert.assertArrayEquals(expectedResult, FakeNumpy.abs(testArr),0.001f);
    }

    @Test
    public void validateAbsFloatEmpty() {
        float[] testArr = new float[] {};
        float[] expectedResult = new float[] {};

        Assert.assertArrayEquals(expectedResult, FakeNumpy.abs(testArr),0.001f);
    }

    @Test
    public void validateAbsLongNonEmpty() {
        long[] testArr = new long[] {-6L,-5L,-4L,-3L,-2L,-1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};
        long[] expectedResult = new long[] {6L,5L,4L,3L,2L,1L,0L,0L,1L,1L,2L,3L,4L,5L,6L,6L};

        Assert.assertArrayEquals(expectedResult, FakeNumpy.abs(testArr));
    }

    @Test
    public void validateAbsLongEmpty() {
        long[] testArr = new long[] {};
        long[] expectedResult = new long[] {};

        Assert.assertArrayEquals(expectedResult, FakeNumpy.abs(testArr));
    }

    @Test
    public void validateCheckForDuplicatesTypeNonEmpty() {
        Double[] testArr1 = new Double[] {0.0,1.0,2.0,3.0,4.0,5.0,6.0,6.0};
        Double[] testArr2 = new Double[] {0.0,1.0,2.0,3.0,4.0,5.0,6.0};
        Integer[] testArr3 = new Integer[] {0,1,2,3,4,5,6,6};
        Integer[] testArr4 = new Integer[] {0,1,2,3,4,5,6};

        Assert.assertTrue(FakeNumpy.checkForDuplicates(testArr1));
        Assert.assertFalse(FakeNumpy.checkForDuplicates(testArr2));
        Assert.assertTrue(FakeNumpy.checkForDuplicates(testArr3));
        Assert.assertFalse(FakeNumpy.checkForDuplicates(testArr4));
    }

    @Test
    public void validateCheckForDuplicatesTypeAllDuplicates() {
        Double[] testArr1 = new Double[] {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
        Double[] testArr2 = new Double[] {-1.3,-1.3,-1.3,-1.3,-1.3,-1.3,-1.3};
        Integer[] testArr3 = new Integer[] {0,0,0,0,0,0,0,0};
        Integer[] testArr4 = new Integer[] {-1,-1,-1,-1,-1,-1,-1};

        Assert.assertTrue(FakeNumpy.checkForDuplicates(testArr1));
        Assert.assertTrue(FakeNumpy.checkForDuplicates(testArr2));
        Assert.assertTrue(FakeNumpy.checkForDuplicates(testArr3));
        Assert.assertTrue(FakeNumpy.checkForDuplicates(testArr4));
    }

    @Test
    public void validateCheckForDuplicatesTypeEmpty() {
        Double[] testArr1 = new Double[] {};
        Integer[] testArr2 = new Integer[] {};

        Assert.assertFalse(FakeNumpy.checkForDuplicates(testArr1));
        Assert.assertFalse(FakeNumpy.checkForDuplicates(testArr2));
    }

    //-----
    @Test
    public void validateRemoveDuplicatesTypeNonEmpty() {
        Double[] testArr1 = new Double[] {0.0,1.0,2.0,3.0,4.0,5.0,6.0,6.0};
        Double[] expectedResult1 = new Double[] {0.0,1.0,2.0,3.0,4.0,5.0,6.0};
        Integer[] testArr2 = new Integer[] {0,1,2,3,4,5,6,6};
        Integer[] expectedResult2 = new Integer[] {0,1,2,3,4,5,6};

        Assert.assertArrayEquals(expectedResult1, FakeNumpy.removeDuplicates(testArr1));
        Assert.assertArrayEquals(expectedResult2, FakeNumpy.removeDuplicates(testArr2));
    }

    @Test
    public void validateRemoveDuplicatesTypeAllDuplicates() {
        Double[] testArr1 = new Double[] {-0.1,-0.1,-0.1,-0.1,-0.1,-0.1,-0.1,-0.1};
        Double[] expectedResult1 = new Double[] {-0.1};
        Integer[] testArr2 = new Integer[] {0,0,0,0,0,0,0,0};
        Integer[] expectedResult2 = new Integer[] {0};

        Assert.assertArrayEquals(expectedResult1, FakeNumpy.removeDuplicates(testArr1));
        Assert.assertArrayEquals(expectedResult2, FakeNumpy.removeDuplicates(testArr2));
    }

    @Test
    public void validateRemoveDuplicatesTypeNoDuplicates() {
        Double[] testArr1 = new Double[] {-0.2,-0.1,0.0,0.2,0.3,0.4,0.5,0.6,0.7};
        Double[] expectedResult1 = new Double[] {-0.2,-0.1,0.0,0.2,0.3,0.4,0.5,0.6,0.7};
        Integer[] testArr2 = new Integer[] {-2,-1,0,1,2,3,4,5};
        Integer[] expectedResult2 = new Integer[] {-2,-1,0,1,2,3,4,5};

        Assert.assertArrayEquals(expectedResult1, FakeNumpy.removeDuplicates(testArr1));
        Assert.assertArrayEquals(expectedResult2, FakeNumpy.removeDuplicates(testArr2));
    }

    @Test
    public void validateRemoveDuplicatesTypeEmpty() {
        Double[] testArr1 = new Double[] {};
        Double[] expectedResult1 = new Double[] {};
        Integer[] testArr2 = new Integer[] {};
        Integer[] expectedResult2 = new Integer[] {};

        Assert.assertArrayEquals(expectedResult1, FakeNumpy.removeDuplicates(testArr1));
        Assert.assertArrayEquals(expectedResult2, FakeNumpy.removeDuplicates(testArr2));
    }

}
