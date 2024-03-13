package pasa.cbentley.framework.drawx.src4.utils;

import java.util.Random;

import pasa.cbentley.byteobjects.src4.objects.color.ColorIteratorFun;
import pasa.cbentley.core.src4.ctx.ObjectU;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.Geo2dUtils;
import pasa.cbentley.core.src4.utils.MathUtils;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;

/**
 * Representation of a triangle with base being the longest segment.
 * The head being the point not on the base
 * @author Charles Bentley
 *
 */
public class Trig extends ObjectU {

   /**
    * The value of 180 is equal to PI radians
    * 
    */
   public static final double ONE_RADIAN            = 180 / Math.PI;

   public static final double ONE_RADIAN_IN_DEGREES = 57.2958;

   static double area(int x1, int y1, int x2, int y2, int x3, int y3) {
      int rectArea = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
      return Math.abs(rectArea / 2.0);
   }

   static float findArea(float a, float b, float c) {
      // Length of sides must be positive and sum of any two sides 
      // must be smaller than third side. 
      if (a < 0 || b < 0 || c < 0 || (a + b <= c) || a + c <= b || b + c <= a) {
         System.out.println("Not a valid triangle");
      }
      float s = (a + b + c) / 2;
      return (float) Math.sqrt(s * (s - a) * (s - b) * (s - c));
   }

   public static Trig[] get4TrigFrom(UCtx uc, int x, int y, int w, int h, Random r) {
      int x1 = x + r.nextInt(w);
      int y1 = y + r.nextInt(h);
      Trig[] ar = new Trig[4];
      ar[0] = new Trig(uc, x1, y1, x, y, x + w, y);
      ar[1] = new Trig(uc, x1, y1, x, y, x, y + h);
      ar[2] = new Trig(uc, x1, y1, x, y + h, x + w, y + h);
      ar[3] = new Trig(uc, x1, y1, x + w, y + h, x + w, y);
      return ar;
   }

   /**
    * Tan alpha = opposite / adjacent
    * @param opposite 
    * @param adjacent
    * @return
    */
   public static double getAlpha(int opposite, int adjacent) {
      return MathUtils.aTan(opposite, adjacent);
   }

   /**
    * A function to check whether point P(x, y) lies inside the triangle formed by A(x1, y1), B(x2, y2) and C(x3, y3)
    * @param x1
    * @param y1
    * @param x2
    * @param y2
    * @param x3
    * @param y3
    * @param x
    * @param y
    * @return
    */
   static boolean isInside(int x1, int y1, int x2, int y2, int x3, int y3, int x, int y) {
      /* Calculate area of triangle ABC */
      double A = area(x1, y1, x2, y2, x3, y3);

      /* Calculate area of triangle PBC */
      double A1 = area(x, y, x2, y2, x3, y3);

      /* Calculate area of triangle PAC */
      double A2 = area(x1, y1, x, y, x3, y3);

      /* Calculate area of triangle PAB */
      double A3 = area(x1, y1, x2, y2, x, y);

      /* Check if sum of A1, A2 and A3 is same as A */
      return (A == A1 + A2 + A3);
   }

   /**
    * A point that has barycentric coordinates (0.5, 0.7, -0.2),
    * it means that the point is located at 0.5*P1 + 0.7*P2 - 0.2*P3
    * @param x
    * @param y
    * @return
    */
   public double[] getBaryCentricCoordinate(int x, int y) {
      // Calculate the barycentric coordinates
      // of point P with respect to triangle ABC
      double denominator = ((y2 - yHead) * (x1 - xHead) + (xHead - x2) * (y1 - yHead));
      double a = ((y2 - yHead) * (x - xHead) + (xHead - x2) * (y - yHead)) / denominator;
      double b = ((yHead - y1) * (x - xHead) + (x1 - xHead) * (y - yHead)) / denominator;
      double c = 1 - a - b;
      return new double[] { a, b, c };
   }

   private int   circumX;

   private int   circumY;

   private int   d1x;

   private int   d1y;

   private int   d2x;

   private int   d2y;

   private int   dBasex;

   private int   dBasey;

   private float distanceBase;

   private int   headXDistance1;

   private int   headXDistance2;

   private int   px;

   private int   py;

   private int   x1;

   private int   x2;

   private int   xbig;

   private int   xHead;

   private int   xsmall;

   private int   y1;

   private int   y2;

   private int   ybig;

   private int   yHead;

   private int   ysmall;

   private int   sideType;

   private int   angleType;

   public Trig(UCtx uc, int xHead, int yHead, int x1, int y1, int x2, int y2) {
      super(uc);

      headXDistance1 = xHead - x1;
      headXDistance2 = xHead - x2;

      this.xHead = xHead;
      this.yHead = yHead;
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      xsmall = Math.min(Math.min(xHead, x1), x2);
      xbig = Math.max(Math.max(xHead, x1), x2);
      ysmall = Math.min(Math.min(yHead, y1), y2);
      ybig = Math.max(Math.max(yHead, y1), y2);
   }

   public int[] abcLine1() {
      return geo().getABCLineFromPoints(xHead, yHead, x1, y1);
   }

   public int[] abcLine2() {
      return geo().getABCLineFromPoints(xHead, yHead, x2, y2);
   }

   /**
    * Line between P1 and P2
    * @return
    */
   public int[] abcLineBase() {
      return geo().getABCLineFromPoints(x2, y2, x1, y1);
   }

   public double area() {
      return area(x1, y1, x2, y2, xHead, yHead);
   }

   public float areaVertice() {
      float a = d1();
      float b = d2();
      float c = dBase();
      float p = (a + b + c);

      // area of the triangle
      float area = (float) Math.sqrt(p * (p - a) * (p - b) * (p - c));
      return area;
   }

   /**
    * It is formed using the intersection of three medians of the triangle. 
    * The centroid of a triangle always lies inside the triangle. 
    * The centroid of a triangle divides all three medians into a 2:1 ratio.
    * The centroid of a triangle is at two-thirds length from the vertex of a triangle and at one-third from the midpoint of the opposite side.
    * The centroid is also known as the center of the object or the center of gravity.
    * In geometry, if one assumes uniform mass density, then the barycenter or center of mass coincides with the centroid. 
    * @return
    */
   public float centroidX() {
      float x = (x1 + x2 + xHead) / 3;
      return x;
   }

   public float centroidY() {
      float y = (y1 + y2 + yHead) / 3;
      return y;
   }

   /**
    * The circumcenter of the triangle is point where all the perpendicular bisectors of the sides of the triangle intersect.
    * @return
    */
   public float circumCenterX() {
      if (circumX == 0) {
         computeCircumCenter();
      }
      return circumX;
   }

   /**
    * Triangle with one right angle i.e 90 degrees
    */
   public static final int ANGLE_RIGHT        = 0;

   /**
    * Triangle with only acute angles. All angles < 90
    */
   public static final int ANGLE_ACUTE        = 1;

   /**
    * Triangle with one obuts angle.. i.e. > 90
    */
   public static final int ANGLE_OBTUSE       = 2;

   public static final int SIDE_0_SCALENE     = 0;

   public static final int SIDE_1_ISOSCELES   = 1;

   public static final int SIDE_2_EQUILATERAL = 2;

   /**
    * 
    * @return
    */
   public int getAngleType() {
      computeTrigType();
      return angleType;
   }

   public void computeTrigType() {
      float d1 = d1Square();
      float d2 = d2Square();
      float dBase = dBaseSquare();
      this.sideType = getSideTYpe(d1, d2, dBase);

      //by construction we know that dBase is bigger than other 2 angles
      this.angleType = getAngleType(d1, d2, dBase);
   }

   public int getSideTYpe(float d1Square, float d2Square, float dBaseSquare) {
      if (d1Square == d2Square) {
         if (d1Square == dBaseSquare) {
            return SIDE_2_EQUILATERAL;
         } else {
            return SIDE_1_ISOSCELES;
         }
      }
      return SIDE_0_SCALENE;
   }

   public int getAngleType(float d1, float d2, float dBase) {
      //
      if (d1 == d2) {
         if (d1 == dBase) {
            return SIDE_2_EQUILATERAL;
         } else {
            return SIDE_1_ISOSCELES;
         }
      }
      return SIDE_0_SCALENE;
   }

   public int getSideType() {
      computeTrigType();
      return sideType;
   }

   /**
    * The circumcenter of the triangle is point where all the perpendicular bisectors of the sides of the triangle intersect.
    * @return
    */
   public int circumCenterY() {
      if (circumY == 0) {
         computeCircumCenter();
      }
      return circumY;
   }

   public void computeBarycentric() {

   }

   /**
    * <li>get abc lines of 2;
    * <li>
    */
   private void computeCircumCenter() {
      Geo2dUtils geo = uc.getGeo2dUtils();

      int[] abc = geo.getABCLineFromPoints(x1, y1, x2, y2);
      int[] efg = geo.getABCLineFromPoints(xHead, yHead, x2, y2);

      int[] per1 = geo.getPerpendicularBisector(x1, y1, x2, y2, abc);
      int[] per2 = geo.getPerpendicularBisector(xHead, yHead, x2, y2, efg);

      int[] inter = geo.getIntersectionLinePoint(per1, per2);
      circumX = inter[0];
      circumY = inter[1];
   }

   public void computeInCenterD1x() {
      int[] res = new int[2];
      int[] abc = abcLine1();
      int cx = (int) inCenterX();
      int cy = (int) inCenterY();
      geo().getLineIntersectionPerpendicularPoint(abc, cx, cy, res);
      d1x = res[0];
      d1y = res[1];
   }

   public void computeInCenterD2x() {
      int[] res = new int[2];
      int[] abc = abcLine2();
      int cx = (int) inCenterX();
      int cy = (int) inCenterY();
      geo().getLineIntersectionPerpendicularPoint(abc, cx, cy, res);
      d2x = res[0];
      d2y = res[1];
   }

   public void computeInCenterDBasex() {
      int[] res = new int[2];
      int[] abc = abcLineBase();
      int cx = (int) inCenterX();
      int cy = (int) inCenterY();
      geo().getLineIntersectionPerpendicularPoint(abc, cx, cy, res);
      dBasex = res[0];
      dBasey = res[1];
   }

   /**
    * When base is sitting flat below.. d1 is the left vertice
    * @return
    */
   public float d1() {
      return Geo2dUtils.getDistance(xHead, yHead, x1, y1);
   }

   /**
    * Less expensive 
    * @return
    */
   public float d1Square() {
      return Geo2dUtils.getDistanceSquare(xHead, yHead, x1, y1);
   }

   /**
    * When base is sitting flat below.. d2 is the right vertice
    * @return
    */
   public float d2() {
      return Geo2dUtils.getDistance(xHead, yHead, x2, y2);
   }

   public float d2Square() {
      return Geo2dUtils.getDistanceSquare(xHead, yHead, x2, y2);
   }

   public float dBaseSquare() {
      return Geo2dUtils.getDistanceSquare(x1, y1, x2, y2);
   }

   public float dBase() {
      return Geo2dUtils.getDistance(x1, y1, x2, y2);
   }

   public void draw(GraphicsX g, ColorIteratorFun cif) {
      cif.iterateColor(g);
      g.fillTriangle(x1, y1, x2, y2, xHead, yHead);
   }

   public Geo2dUtils geo() {
      return uc.getGeo2dUtils();
   }

   public int getDiffX() {
      int xdiff = xbig - xsmall;
      return xdiff;
   }

   public int getDiffY() {
      int ydiff = ybig - ysmall;
      return ydiff;
   }

   public int getHeadProjectionX() {
      return 0;
   }

   public int getHeadProjectionY() {
      return 0;

   }

   public int getSmallX() {
      return xsmall;
   }

   public int getSmallY() {
      return ysmall;
   }

   public Trig[] getTrigsFromP(int px, int py) {
      Trig[] ar = new Trig[3];
      ar[0] = new Trig(uc, px, py, x1, y1, x2, y2);
      ar[1] = new Trig(uc, px, py, xHead, yHead, x2, y2);
      ar[2] = new Trig(uc, px, py, x1, y1, xHead, yHead);
      return ar;
   }

   public int headDistanceX1() {
      return xHead - x1;
   }

   /**
    * Intersection point on the d1 and perpendicular going through inCenter
    * @return
    */
   public float inCenterD1X() {
      if (d1x == 0) {
         computeInCenterD1x();
      }
      return d1x;
   }

   public float inCenterD1Y() {
      if (d1y == 0) {
         computeInCenterD1x();
      }
      return d1y;
   }

   public float inCenterD2X() {
      if (d2x == 0) {
         computeInCenterD2x();
      }
      return d2x;
   }

   public float inCenterD2Y() {
      if (d2y == 0) {
         computeInCenterD2x();
      }
      return d2y;
   }

   public float inCenterDBaseX() {
      if (dBasex == 0) {
         computeInCenterDBasex();
      }
      return dBasex;
   }

   public float inCenterDBaseY() {
      if (dBasey == 0) {
         computeInCenterDBasex();
      }
      return dBasey;
   }

   public float inCenterRadius() {
      // semi-perimeter of the circle
      float p = perimeter() / 2;

      // area of the triangle
      float area = (float) area();
      //float area = (float) areaVertice();

      // Radius of the incircle
      float radius = area / p;

      // Return the radius
      return radius;
   }

   public float inCenterX() {
      float c = dBase();
      float b = d1();
      float a = d2();
      float x = (a * x1 + b * x2 + c * xHead) / (a + b + c);
      return x;
   }

   public float inCenterY() {
      float c = dBase(); //must match 
      float b = d1();
      float a = d2();
      float y = (a * y1 + b * y2 + c * yHead) / (a + b + c);
      return y;
   }

   public float orthoCenterX() {
      float centroidX = centroidX();
      float circunCenterX = circumCenterX();
      float ocX = (3 * centroidX - 2 * circunCenterX);
      return ocX;
   }

   public float orthoCenterY() {
      float centroidY = centroidY();
      float circunCenterY = circumCenterY();
      float ocY = (3 * centroidY - 2 * circunCenterY);
      return ocY;
   }

   public float perimeter() {
      return d1() + d2() + dBase();
   }

   public int px() {
      return px;
   }

   public int py() {
      return py;
   }

   public void randomPointInside(Random r) {
      int xdiff = getDiffX();
      px = xsmall + r.nextInt(xdiff);
      int ydiff = getDiffY();
      py = ysmall + r.nextInt(ydiff);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, Trig.class, 205);
      toStringPrivate(dc);
      super.toString(dc.sup());

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, Trig.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {
      dc.append(" head=");
      dc.append(xHead);
      dc.append(',');
      dc.append(yHead);
      dc.append(" 1=");
      dc.append(x1);
      dc.append(',');
      dc.append(y1);
      dc.append(" 2=");
      dc.append(x2);
      dc.append(',');
      dc.append(y2);

      dc.append("Aread=");
      dc.append(area());
   }

   //#enddebug

}
