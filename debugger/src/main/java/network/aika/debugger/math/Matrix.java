package network.aika.debugger.math;



import static java.lang.Math.*;
import static network.aika.debugger.math.MatrixUtls.*;
import static network.aika.debugger.math.Vector.length;
import static network.aika.utils.Utils.TOLERANCE;

public class Matrix implements Value<Matrix> {

    double[][] value;

    public Matrix(double[][] value) {
        this.value = value;
    }

    @Override
    public Matrix add(Matrix x) {
        double[][] result = new double[value.length][value.length];

        for(int i = 0; i < value.length; i++)
            for(int j = 0; j < value.length; j++)
                result[i][j] = value[i][j] + x.value[i][j];

        return new Matrix(result);
    }

    @Override
    public Matrix sub(Matrix x) {
        double[][] result = new double[value.length][value.length];

        for(int i = 0; i < value.length; i++)
            for(int j = 0; j < value.length; j++)
                result[i][j] = value[i][j] - x.value[i][j];

        return new Matrix(result);
    }

    @Override
    public Matrix scale(double s) {
        double[][] result = new double[4][4];

        for(int i = 0; i < 3; i++)
            result[i][i] = s;

        result[3][3] = 1.0;

        return mul(new Matrix(result));
    }

    @Override
    public Matrix mul(Matrix x) {
        return new Matrix(multiply(value, x.value));
    }

    public Vector mulVec(Vector x) {
        return new Vector(multiply(value, x.value));
    }

    public static double[][] negate(double[][] x) {
        double[][] result = new double[x.length][x.length];

        for(int i = 0; i < x.length; i++)
            for(int j = 0; j < x.length; j++)
                result[i][j] = -x[i][j];
        return result;
    }

    public Matrix perspective(double fovy, double aspect, double zNear, double zFar, boolean zZeroToOne) {
        Matrix dest = new Matrix(new double[4][4]);

        double h = Math.tan(fovy * 0.5D);
        double rm00 = 1.0D / (h * aspect);
        double rm11 = 1.0D / h;
        boolean farInf = zFar > 0.0D && Double.isInfinite(zFar);
        boolean nearInf = zNear > 0.0D && Double.isInfinite(zNear);
        double rm22;
        double rm32;
        double e;
        if (farInf) {
            e = 1.0E-6D;
            rm22 = e - 1.0D;
            rm32 = (e - (zZeroToOne ? 1.0D : 2.0D)) * zNear;
        } else if (nearInf) {
            e = 1.0E-6D;
            rm22 = (zZeroToOne ? 0.0D : 1.0D) - e;
            rm32 = ((zZeroToOne ? 1.0D : 2.0D) - e) * zFar;
        } else {
            rm22 = (zZeroToOne ? zFar : zFar + zNear) / (zNear - zFar);
            rm32 = (zZeroToOne ? zFar : zFar + zFar) * zNear / (zNear - zFar);
        }

        e = this.value[2][0] * rm22 - this.value[3][0];
        double nm21 = this.value[2][1] * rm22 - this.value[3][1];
        double nm22 = this.value[2][2] * rm22 - this.value[3][2];
        double nm23 = this.value[2][3] * rm22 - this.value[3][3];
        dest.value[0][0] = this.value[0][0] * rm00;
        dest.value[0][1] = this.value[0][1] * rm00;
        dest.value[0][2] = this.value[0][2] * rm00;
        dest.value[0][3] = this.value[0][3] * rm00;
        dest.value[1][0] = this.value[1][0] * rm11;
        dest.value[1][1] = this.value[1][1] * rm11;
        dest.value[1][2] = this.value[1][2] * rm11;
        dest.value[1][3] = this.value[1][3] * rm11;
        dest.value[3][0] = this.value[2][0] * rm32;
        dest.value[3][1] = this.value[2][1] * rm32;
        dest.value[3][2] = this.value[2][2] * rm32;
        dest.value[3][3] = this.value[2][3] * rm32;
        dest.value[2][0] = e;
        dest.value[2][1] = nm21;
        dest.value[2][2] = nm22;
        dest.value[2][3] = nm23;
        return dest;
    }

    public static Matrix identity(int dim) {
        double[][] m = new double[dim][dim];

        for(int i = 0; i < dim; i++)
            m[i][i] = 1;

        return new Matrix(m);
    }

    public static Matrix getWorldMatrix(Vector offset, Vector rotation, double scale) {
        return identity(4)
                .rotateX(rotation.value[0])
                .rotateY(rotation.value[1])
                .rotateZ(rotation.value[2])
                .scale(scale)
                .translate(offset);
    }

    public static Matrix getWorldMatrix(Vector offset, double scale) {
        return identity(4)
                .scale(scale)
                .translate(offset);
    }


    public static Matrix getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        Matrix projectionMatrix = identity(4);
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar, false);
        return projectionMatrix;
    }

    public Matrix translate(Vector offset) {
        double[][] y = MatrixUtls.copy(value);
        for(int i = 0; i < 3; i++)
            y[i][3] = offset.value[i];

        return new Matrix(y);
    }

    public Matrix translate(double... offset) {
        double[][] y = MatrixUtls.copy(value);
        for(int i = 0; i < offset.length; i++)
            y[i][3] = offset[i];

        return new Matrix(y);
    }

    public Matrix rotateX(double rad) {
        return mul(getRotateXMatrix(rad));
    }

    public Matrix rotateY(double rad) {
        return mul(getRotateYMatrix(rad));
    }

    public Matrix rotateZ(double rad) {
        return mul(getRotateZMatrix(rad));
    }

    public double getRotationX() {
        return atan2(-value[1][2], value[2][2]);
    }

    public double getRotationY() {
        double cosY = sqrt(1 - value[0][2]);
        return atan2(value[0][2], cosY);
    }


    public Vector getRotation() {
        double[] rot = new double[3];

        rot[0] = -atan2(-value[1][2], value[2][2]);
        double cosYangle = sqrt(pow(value[0][0], 2) + pow(value[0][1], 2));
        rot[1] = -atan2(value[0][2], cosYangle);
        double sinXangle = sin(-rot[0]);
        double cosXangle = cos(-rot[0]);
        rot[2] = -atan2(cosXangle * value[1][0] + sinXangle * value[2][0], cosXangle * value[1][1] + sinXangle * value[2][1]);

        return new Vector(rot);
    }

    public Vector getScale() {
        double[] scale = new double[] {
                length(getColumn(0, 3)),
                length(getColumn(1, 3)),
                length(getColumn(2, 3))
        };
        return new Vector(scale);
    }

    public Matrix getNormRotationMatrix() {
        return scale(1 / getRadius());
    }

    public double getRadius() {
        return length(
                getColumn(0, 3)
        );
    }

    public Matrix copy() {
        return new Matrix(MatrixUtls.copy(value));
    }

    public Matrix transpose() {
        return new Matrix(MatrixUtls.transpose(value));
    }

    public double[] getColumn(int column, int size) {
        return MatrixUtls.getColumn(value, column, size);
    }
    public Vector getColumnVec(int column, int size) {
        return new Vector(MatrixUtls.getColumn(value, column, size));
    }

    public Matrix inverseTransform() {
        return new Matrix(MatrixUtls.inverseTransform(value));
    }

    @Override
    public boolean belowTolerance() {
        for(int i = 0; i < value.length; i++)
            for(int j = 0; j < value.length; j++)
            if(Math.abs(value[i][j]) >= TOLERANCE)
                return false;

        return true;
    }

    @Override
    public Matrix getUninitialized() {
        return new Matrix(new double[value.length][value.length]);
    }

    @Override
    public Matrix negate() {
        return new Matrix(negate(value));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append("\n");
        for(int i = 0; i < value.length; i++) {
            sb.append('[');
            for(int j = 0; j < value.length; j++) {
                sb.append(value[i][j]);
                if (j != value.length - 1)
                    sb.append(",");
            }
            sb.append(']');
            sb.append("\n");
        }
        sb.append(']');
        return sb.toString();
    }

    public double[][] getValue() {
        return value;
    }
}
