// Line.java
// Class Linea represents a line with two endpoints.
package it.uniba.di.sms.laricchia.cannon;

import android.graphics.Point;

public class Linea {
   public Point start; // starting Point
   public Point end; // ending Point

   // il costruttore di default inizializza Points a (0, 0)
   public Linea() {
      start = new Point(0, 0); // start Point
      end = new Point(0, 0); // end Point
   } // end method Linea
} // end class Linea