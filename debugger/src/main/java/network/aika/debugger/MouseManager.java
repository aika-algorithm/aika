/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.aika.debugger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * @author Lukas Molzberger
 */
public class MouseManager implements MouseInputListener, MouseWheelListener {

    private static final Logger log = LoggerFactory.getLogger(MouseManager.class);

    protected GraphicElement curElement;
    protected GraphicElement lastClickedElement;


    private AbstractViewManager viewManager;
    private MouseEvent lastMouseDragEvent;


    public MouseManager(AbstractViewManager viewManager) {
        this.viewManager = viewManager;
    }



    protected void mouseButtonPressOnElement(GraphicElement element, MouseEvent event) {
        lastClickedElement = element;

    }

    protected void elementMoving(GraphicElement element, MouseEvent event) {

    }

    protected void mouseButtonReleaseOffElement(GraphicElement element, MouseEvent event) {

    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {
       //this.curElement = view.findGraphicElementAt(this.types, event.getX(), event.getY());

        double x = event.getX();
        double y = event.getY();

        if (this.curElement != null) {
            this.mouseButtonPressOnElement(this.curElement, event);
        }
    }

    public void mouseDragged(MouseEvent event) {
        if (this.curElement != null) {
            this.elementMoving(this.curElement, event);
        } else {
            if (lastMouseDragEvent != null) {
 //               dragGraphMouseMoved(event, lastMouseDragEvent, (DefaultCamera2D) view.getCamera());
            }
            lastMouseDragEvent = event;
        }
    }

    public void mouseReleased(MouseEvent event) {
        lastMouseDragEvent = null;

        if (this.curElement != null) {
            this.mouseButtonReleaseOffElement(this.curElement, event);
            this.curElement = null;
        }
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseMoved(MouseEvent event) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {

    }
}
