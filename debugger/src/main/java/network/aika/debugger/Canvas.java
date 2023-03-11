package network.aika.debugger;

import network.aika.debugger.math.Matrix;
import network.aika.debugger.math.Vector;

import java.awt.*;

public interface Canvas {


    void drawLine(Graphics g, Vector from, Vector to, Color c);

    void drawRectangle(Graphics g, Vector from, Vector to, Color c);

    void drawCircle(Graphics g, Vector pos, double radius, Color c);

    void drawDot(Graphics g, Vector pos, Color c);

    void drawText(Graphics g, Vector pos, String txt, Color c);

    Canvas subCanvas(Matrix transform);
}
