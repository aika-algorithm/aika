package network.aika.debugger;

import network.aika.debugger.math.Matrix;
import network.aika.debugger.math.Vector;

import java.awt.*;


public class CanvasImpl implements Canvas {

    private Matrix transform;

    public CanvasImpl(Matrix transform) {
        this.transform = transform;
    }

    @Override
    public void drawLine(Graphics g, Vector from, Vector to, Color c) {
        g.setColor(c);

        Vector from2d = transform.mulVec(from.extendTo4d());// projectionMatrix.mul(from.extendTo4d());
        Vector to2d = transform.mulVec(to.extendTo4d()); //projectionMatrix.mul(to.extendTo4d());

        g.drawLine(
                (int) Math.round(from2d.value[0]),
                (int) Math.round(from2d.value[1]),
                (int) Math.round(to2d.value[0]),
                (int) Math.round(to2d.value[1])
        );
    }


    @Override
    public void drawRectangle(Graphics g, Vector from, Vector to, Color color) {
        Vector a = new Vector(from.value[0], 0, from.value[2]);
        Vector b = new Vector(from.value[0], 0, to.value[2]);
        Vector c = new Vector(to.value[0], 0, to.value[2]);
        Vector d = new Vector(to.value[0], 0, from.value[2]);

        Vector a2d = transform.mulVec(a.extendTo4d());
        Vector b2d = transform.mulVec(b.extendTo4d());
        Vector c2d = transform.mulVec(c.extendTo4d());
        Vector d2d = transform.mulVec(d.extendTo4d());

        g.setColor(color);
        g.fillPolygon(
                new int[] {
                        (int) Math.round(a2d.value[0]),
                        (int) Math.round(b2d.value[0]),
                        (int) Math.round(c2d.value[0]),
                        (int) Math.round(d2d.value[0])
                },
                new int[] {
                        (int) Math.round(a2d.value[1]),
                        (int) Math.round(b2d.value[1]),
                        (int) Math.round(c2d.value[1]),
                        (int) Math.round(d2d.value[1]),
                },
                4
        );
    }

    @Override
    public void drawCircle(Graphics g, Vector pos, double radius, Color c) {
        Vector outerPos = pos.add(new Vector(radius, 0, 0));

        Vector pos2d = transform.mulVec(pos.extendTo4d());
        Vector outerPos2d = transform.mulVec(outerPos.extendTo4d());

        double radius2D = outerPos2d.sub(pos2d).length();

        g.setColor(c);
        g.drawOval(
                (int) (Math.round(pos2d.value[0]) - radius2D),
                (int) (Math.round(pos2d.value[1]) - radius2D),
                (int) (2.0 * radius2D),
                (int) (2.0 * radius2D)
        );
    }

    @Override
    public void drawDot(Graphics g, Vector pos, Color c) {
        Vector pos2d = transform.mulVec(pos.extendTo4d());

        g.setColor(c);
        g.drawOval(
                (int) Math.round(pos2d.value[0]),
                (int) Math.round(pos2d.value[1]),
                3,
                3
        );
    }

    @Override
    public void drawText(Graphics g, Vector pos, String txt, Color c) {
        Vector pos2d = transform.mulVec(pos.extendTo4d());

        g.setColor(c);
        g.drawString(
                txt,
                (int) Math.round(pos2d.value[0]),
                (int) Math.round(pos2d.value[1])
        );
    }

    @Override
    public Canvas subCanvas(Matrix transform) {
        return new CanvasImpl(
                this.transform.mul(transform)
        );
    }
}
