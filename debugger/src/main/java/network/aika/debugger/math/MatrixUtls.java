package network.aika.debugger.math;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MatrixUtls {


    public static double[][] transpose(double[][] a) {
        int m = a.length;
        int n = a[0].length;
        double[][] b = new double[n][m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                b[j][i] = a[i][j];
        return b;
    }


    public static double[] getColumn(double[][] a, int column, int size) {
        int m = a.length;
        int n = a[0].length;
        double[] b = new double[size];
        for (int i = 0; i < Math.min(m, size); i++)
            b[i] = a[i][column];
        return b;
    }

    public static double[][] getSubMatrix(double[][] a, int ms, int ns) {
        int m = a.length;
        int n = a[0].length;
        double[][] b = new double[ms][ns];
        for (int i = 0; i < Math.min(m, ms); i++)
            for (int j = 0; j < Math.min(n, ns); j++)
                b[i][j] = a[i][j];
        return b;
    }

    public static double[][] insert(double[][] a, double[][] b, int posM, int posN) {
        for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[i].length; j++)
                a[i + posM][j + posN] = b[i][j];
        return a;
    }

    public static double[][] insert(double[][] a, double[] b, int column) {
        for (int i = 0; i < b.length; i++)
            a[i][column] = b[i];
        return a;
    }


    public static double[][] inverseTransform(double[][] a) {
        RealMatrix pInverse = new LUDecomposition(
                MatrixUtils.createRealMatrix(a)
        )
                .getSolver()
                .getInverse();

        return pInverse.getData();
    }



    public static Matrix getRotateXMatrix(double rad) {
        double sin = sin(rad);
        double cos = cos(rad);

        return new Matrix(new double[][] {
                {1,    0,   0, 0},
                {0,  cos, sin, 0},
                {0, -sin, cos, 0},
                {0,    0,   0, 1}
        });
    }

    public static Matrix getRotateYMatrix(double rad) {
        double sin = sin(rad);
        double cos = cos(rad);

        return new Matrix(new double[][] {
                {cos, 0, -sin, 0},
                {0,   1,    0, 0},
                {sin, 0,  cos, 0},
                {0,   0,    0, 1}
        });
    }

    public static Matrix getRotateZMatrix(double rad) {
        double sin = sin(rad);
        double cos = cos(rad);

        return new Matrix(new double[][] {
                {cos, -sin, 0, 0},
                {sin,  cos, 0, 0},
                {0  ,    0, 1, 0},
                {0  ,    0, 0, 1}
        });
    }



    public static double[][] multiply(double[][] a, double[][] b) {
        int m1 = a.length;
        int n1 = a[0].length;
        int m2 = b.length;
        int n2 = b[0].length;
        if (n1 != m2) throw new RuntimeException("Illegal matrix dimensions.");
        double[][] c = new double[m1][n2];
        for (int i = 0; i < m1; i++)
            for (int j = 0; j < n2; j++)
                for (int k = 0; k < n1; k++)
                    c[i][j] += a[i][k] * b[k][j];
        return c;
    }

    // matrix-vector multiplication (y = A * x)
    public static double[] multiply(double[][] a, double[] x) {
        int m = a.length;
        int n = a[0].length;
        if (x.length != n) throw new RuntimeException("Illegal matrix dimensions.");
        double[] y = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                y[i] += a[i][j] * x[j];
        return y;
    }

    public static double[][] copy(double[][] x) {
        int m = x.length;
        int n = x[0].length;

        double[][] y = new double[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                y[i][j] = x[i][j];

        return y;
    }

}
