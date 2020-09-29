package com.SCHSRobotics.HAL9001.util.math.geometry;

import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.math.FakeNumpy;
import com.SCHSRobotics.HAL9001.util.math.HALMathUtil;

import org.jetbrains.annotations.NotNull;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.util.Arrays;

public class Matrix {

    private final double[][] vals;

    public Matrix(double[]... vals) {
        if (vals != null) {
            this.vals = vals.clone();
        } else {
            this.vals = new double[1][1];
        }
    }

    private Matrix(Matrix matrix) {
        vals = new double[matrix.getNumRows()][matrix.getNumCols()];
        for (int i = 0; i < matrix.getNumRows(); i++) {
            System.arraycopy(matrix.vals[i], 0, vals[i], 0, vals[i].length);
        }
    }

    public static Matrix identityMatrix(int rows, int cols) {
        ExceptionChecker.assertTrue(rows > 1, new RuntimeException());
        ExceptionChecker.assertTrue(cols > 1, new RuntimeException());

        Mat identityMatrixMat = Mat.eye(new Size(rows, cols), CvType.CV_64F);
        Matrix identityMatrix = new Matrix();
        identityMatrix.matToVals(identityMatrixMat);
        return identityMatrix;
    }

    public static Matrix zeroMatrix(int rows, int cols) {
        ExceptionChecker.assertTrue(rows > 0, new RuntimeException());
        ExceptionChecker.assertTrue(cols > 0, new RuntimeException());

        Mat zeroMatrixMat = Mat.zeros(new Size(rows, cols), CvType.CV_64F);
        Matrix zeroMatrix = new Matrix();
        zeroMatrix.matToVals(zeroMatrixMat);
        return zeroMatrix;
    }

    public static Matrix onesMatrix(int rows, int cols) {
        ExceptionChecker.assertTrue(rows > 0, new RuntimeException());
        ExceptionChecker.assertTrue(cols > 0, new RuntimeException());

        Mat onesMatrixMat = Mat.ones(new Size(rows, cols), CvType.CV_64F);
        Matrix onesMatrix = new Matrix();
        onesMatrix.matToVals(onesMatrixMat);
        return onesMatrix;
    }

    public void set(int row, int col, double value) {
        vals[row][col] = value;
    }

    public double get(int row, int col) {
        return vals[row][col];
    }

    public double[] getRow(int row) {
        return vals[row].clone();
    }

    public double[] getCol(int col) {
        double[] colArray = new double[getNumRows()];
        for (int i = 0; i < getNumRows(); i++) {
            colArray[i] = getRow(i)[col];
        }
        return colArray;
    }

    public void setRow(int rowNum, double[] row) {
        ExceptionChecker.assertTrue(row.length == getNumCols(), new RuntimeException());
        vals[rowNum] = row;
    }

    public void setCol(int colNum, double[] col) {
        for (int i = 0; i < getNumRows(); i++) {
            set(i, colNum, col[i]);
        }
    }

    public int getNumRows() {
        return vals.length;
    }

    public int getNumCols() {
        if (this.getNumRows() == 0) {
            return 0;
        }
        return vals[0].length;
    }

    public boolean isSquare() {
        return this.getNumRows() == this.getNumCols();
    }

    public boolean isSymmetric() {
        return this.clone().transpose().equals(this);
    }

    public boolean isPositiveDefinite() {
        return false;
    }

    public Matrix transpose() {
        matToVals(valsToMat(CvType.CV_64F).t());
        return this;
    }

    public double trace() {
        Mat mat = valsToMat(CvType.CV_64F);
        Scalar trace = Core.trace(mat);
        mat.release();
        return trace.val[0];
    }

    public InverseMethod getOptimalInverseMethod() {
        if (this.isPositiveDefinite()) {
            return InverseMethod.CHOLESKY;
        } else if (this.isSymmetric()) {
            return InverseMethod.EIGEN;
        } else if (this.isSquare()) {
            return InverseMethod.LU;
        } else {
            return InverseMethod.SVD;
        }
    }

    public Matrix invert(InverseMethod inverseMethod) {
        switch (inverseMethod) {
            case LU:
                ExceptionChecker.assertTrue(isSquare(), new RuntimeException("LU decomposition method cannot be used on non-square matrices, use SVD instead to get a pseudo-inverse."));
                break;
            case EIGEN:
                ExceptionChecker.assertTrue(isSquare(), new RuntimeException("LU decomposition method cannot be used on non-square matrices, use SVD instead to get a pseudo-inverse."));
                ExceptionChecker.assertTrue(isSymmetric(), new RuntimeException("Eigen decomposition method cannot be used on non-symmetric matrices."));
                break;
            case CHOLESKY:
                ExceptionChecker.assertTrue(isSquare(), new RuntimeException("LU decomposition method cannot be used on non-square matrices, use SVD instead to get a pseudo-inverse."));
                ExceptionChecker.assertTrue(isSymmetric(), new RuntimeException("Eigen decomposition method cannot be used on non-symmetric matrices."));
                ExceptionChecker.assertTrue(isPositiveDefinite(), new RuntimeException("Cholesky decomposition method cannot be used on non-positive-definite matrices."));
                break;
        }

        matToVals(valsToMat(CvType.CV_64F).inv(inverseMethod.method));
        return this;
    }

    public Matrix invert() {
        return invert(InverseMethod.LU);
    }

    public Matrix multiply(Matrix matrix) {
        Mat thisMat = valsToMat(CvType.CV_64F);
        Mat thatMat = matrix.valsToMat(CvType.CV_64F);
        Mat emptyMat = new Mat();
        Mat dst = new Mat();
        Core.gemm(thisMat, thatMat, 1, emptyMat, 0, dst, 0);
        thisMat.release();
        thatMat.release();
        emptyMat.release();
        matToVals(dst);
        return this;
    }

    public double determinant() {
        ExceptionChecker.assertTrue(isSquare(), new RuntimeException("Matrix is not square, cannot take determinant."));
        Mat thisMat = valsToMat(CvType.CV_64F);
        double det = Core.determinant(thisMat);
        thisMat.release();
        return det;
    }

    public Matrix multiply(double value) {
        for (int i = 0; i < vals.length; i++) {
            for (int j = 0; j < vals[0].length; j++) {
                vals[i][j] *= value;
            }
        }
        return this;
    }

    public Matrix divide(double value) {
        ExceptionChecker.assertFalse(value == 0, new ArithmeticException("Divide by zero error."));
        return multiply(1 / value);
    }

    public Matrix mask(Matrix mask) {
        ExceptionChecker.assertTrue(this.getNumRows() == mask.getNumRows(), new RuntimeException());
        ExceptionChecker.assertTrue(this.getNumCols() == mask.getNumCols(), new RuntimeException());

        for (int i = 0; i < mask.vals.length; i++) {
            for (int j = 0; j < mask.vals[0].length; j++) {
                if (mask.vals[i][j] <= 0) {
                    this.vals[i][j] = 0;
                }
            }
        }

        return this;
    }

    public Matrix rref() {
        ref();

        int currentColNum = getNumCols() - 1;
        for (int currentRowNum = getNumRows() - 1; currentRowNum > 0; currentRowNum--) {
            double[] currentRowArray = getRow(currentRowNum);
            double val = currentRowArray[currentColNum];
            if (val == 0) {
                continue;
            }
            FakeNumpy.divide(currentRowArray, val);
            setRow(currentRowNum, currentRowArray);
            for (int row = 0; row < getNumRows(); row++) {
                if (row != currentRowNum) {
                    double[] rowArray = getRow(row);
                    double[] multipliedCurrentRow = currentRowArray.clone();
                    FakeNumpy.multiply(multipliedCurrentRow, rowArray[currentColNum]);
                    setRow(row, FakeNumpy.subtract(rowArray, multipliedCurrentRow));
                }
            }
            currentColNum--;
            currentColNum = HALMathUtil.mod(currentColNum, getNumCols());
        }
        return this;
    }

    public Matrix ref() {
        int currentColNum = 0;
        for (int currentRowNum = 0; currentRowNum < getNumRows(); currentRowNum++) {
            boolean anyNonZero = false;
            double[] currentRowArray = new double[1];
            for (int row = 0; row < getNumRows(); row++) {
                currentRowArray = getRow(row);
                double val = currentRowArray[currentColNum];
                boolean correctZeroPrefix = true;

                for (int i = 0; i < currentColNum; i++) correctZeroPrefix &= get(row, i) == 0;

                if (val != 0 && correctZeroPrefix) {
                    anyNonZero = true;
                    currentRowNum = row;
                    break;
                }
            }
            if (!anyNonZero) continue;
            FakeNumpy.divide(currentRowArray, currentRowArray[currentColNum]);

            for (int row = 0; row < getNumRows(); row++) {
                if (row != currentRowNum) {
                    double[] rowArray = getRow(row);
                    double[] multipliedCurrentRow = currentRowArray.clone();
                    FakeNumpy.multiply(multipliedCurrentRow, rowArray[currentColNum]);
                    setRow(row, FakeNumpy.subtract(rowArray, multipliedCurrentRow));
                }
            }
            currentColNum++;
        }
        return this;
    }

    public int rank() {
        Matrix thisCopy = this.clone();
        thisCopy.rref();

        int rank = 0;
        for (double[] row : thisCopy.vals) {
            boolean isNonZeroRow = false;
            for (double val : row) isNonZeroRow |= val != 0;
            if (isNonZeroRow) rank++;
        }

        return rank;
    }

    public int nullity() {
        return getNumCols() - rank();
    }

    public boolean isZeroMatrix() {
        boolean isZeroMatrix = true;
        for (double[] row : vals) {
            for (double val : row) {
                isZeroMatrix &= val == 0;
            }
        }
        return isZeroMatrix;
    }

    public boolean isIdentityMatrix() {
        if (!isSquare()) {
            return false;
        }

        boolean isIdentity = true;
        for (int i = 0; i < vals.length; i++) {
            for (int j = 0; j < vals[0].length; j++) {
                if (i == j) {
                    isIdentity &= vals[i][j] == 1;
                } else {
                    isIdentity &= vals[i][j] == 0;
                }
            }
        }
        return isIdentity;
    }

    private Mat valsToMat(int type) {
        Mat mat = new Mat(this.getNumRows(), this.getNumCols(), type);
        for (int i = 0; i < vals.length; i++) {
            mat.put(i, 0, vals[i]);
        }
        return mat;
    }

    private void matToVals(Mat mat) {
        for (int i = 0; i < mat.height(); i++) {
            mat.get(i, 0, vals[i]);
            FakeNumpy.floatingPointFix(vals[i]);
        }
        mat.release();
    }

    @NotNull
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (double[] row : vals) {
            output.append(Arrays.toString(row));
            output.append('\n');
        }
        output.deleteCharAt(output.length() - 1);
        return output.toString();
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        for (int row = 0; row < this.getNumRows(); row++) {
            for (int col = 0; col < this.getNumCols(); col++) {
                long bits = Double.doubleToLongBits(this.get(row, col));
                int hash = (int) (bits ^ (bits >>> 32));
                hashCode = 37 * hashCode + hash;
            }
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Matrix) {
            Matrix matrix = (Matrix) obj;
            if (this.getNumRows() != matrix.getNumRows() || this.getNumCols() != matrix.getNumCols()) {
                return false;
            }

            boolean equal = true;
            for (int row = 0; row < this.getNumRows(); row++) {
                for (int col = 0; col < this.getNumCols(); col++) {
                    equal &= matrix.get(row, col) == this.get(row, col);
                }
            }
            return equal;
        }
        return false;
    }

    @Override
    public Matrix clone() {
        return new Matrix(this);
    }

    public enum InverseMethod {
        LU(Core.DECOMP_LU), //Gaussian elimination w/ optimal pivot
        SVD(Core.DECOMP_SVD), //Singular value decomposition
        EIGEN(Core.DECOMP_EIG), //Eigenvalue decomposition, matrix must be symmetrical
        CHOLESKY(Core.DECOMP_CHOLESKY); //Cholesky factorization, matrix must be symmetrical and positively defined.

        private final int method;

        InverseMethod(int method) {
            this.method = method;
        }
    }
}