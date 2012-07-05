//------------------------------------------------------------------------------
//
// This software is provided "AS IS".  360Commerce MAKES NO
// REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NON-INFRINGEMENT. 360Commerce shall not be liable for
// any damages suffered as a result of using, modifying or distributing this
// software or its derivatives. Permission to use, copy, modify, and distribute
// the software and its documentation for any purpose is hereby granted.
//
// DrawPanel.java - A component of the Signature Capture panel
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

    public class DrawPanel extends JPanel implements MouseMotionListener
    {

		private static final long serialVersionUID = -7018107363017640128L;
		
		int x1;
        int y1;
        int x2;
        int y2;
        int [] xArray;
        int [] yArray;
        int i = 0;
        int j,k;
        int minx = 0xffff;
        int miny = 0xffff;
        int maxx = 0;
        int maxy = 0;

        Dimension preferredSize = new Dimension(320,240);

        public DrawPanel()
        {
            super();
            setBackground(new Color(0.98f, 0.97f, 0.85f));
            addMouseMotionListener(this);
            clear();
        }

        public void clear(){
            i = 0;
            xArray = new int[1000];
            yArray = new int[1000];
            xArray[i] = 0xffff;
            yArray[i] = 0xffff;
            i++;
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
            j = i - 1;
            if(xArray[j] != 0xffff || yArray[j] != 0xffff){
                xArray[i] = 0xffff;
                yArray[i] = 0xffff;
                i++;
            }
        }

        public void mouseDragged(MouseEvent e) {
            xArray[i] = e.getX();
            yArray[i] = e.getY();
//System.out.println("x = " + xArray[i] + " y = " + yArray[i]);
            i++;
            if(i == 1000){i = 1;}
            repaint();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);  //paint background
            for(k = 1; k < i; k++){
                x1 = xArray[k-1];
                y1 = yArray[k-1];
                x2 = xArray[k];
                y2 = yArray[k];
                if(x1 == 0xffff && y1 == 0xffff || x2 == 0xffff && y2 == 0xffff){
                }else{
                    x1 -= minx;
                    y1 -= miny;
                    x2 -= minx;
                    y2 -= miny;
//System.out.println("drawing x1="+x1+" y1="+y1+" x2="+x2+" y2="+y2);
                    g.drawLine(x1,y1,x2,y2);
                }
            }
        }

        public Dimension getPreferredSize() {
            return preferredSize;
        }

        public Point[] getPoints(){
            Point[] points = new Point[i-1];
            for(j = 0; j < i-1; j++){
                points[j] = new Point(xArray[j+1],yArray[j+1]);
            }
            return points;
        }

        public void setPoints(Point[] points){
            clear();
            Point p;
     //       i = 0;
            for(j = 0; j < points.length; j++){
                p = points[j];
//System.out.println("x = " + p.x + " y = " + p.y);
                if(p != null)
                {
                xArray[i] = new Double(p.getX()).intValue();
                yArray[i] = new Double(p.getY()).intValue();
                    
                if( p.x < minx){ minx = p.x; }
                if( p.y < miny){ miny = p.y; }
                if(p.x != 0xffff && p.x > maxx){ maxx = p.x; }
                if(p.x != 0xffff && p.y > maxy){ maxy = p.y; }
                i++;
                }
            }
        }


    }

