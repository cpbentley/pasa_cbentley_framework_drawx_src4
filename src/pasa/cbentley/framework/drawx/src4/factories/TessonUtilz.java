package pasa.cbentley.framework.drawx.src4.factories;

import java.util.Random;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.BOCtx;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.objects.color.ColorFunction;
import pasa.cbentley.byteobjects.src4.objects.color.ColorIteratorFun;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.structs.BufferObject;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.core.src4.utils.Geo2dUtils;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigTesson;
import pasa.cbentley.framework.drawx.src4.utils.Trig;

public class TessonUtilz {

   public static void drawFigTessonNearestHigh(GraphicsX g, int x, int y, int w, int h, ByteObject p, Random r, BOCtx boc) {
      int maxSizeTesson = p.get1(IBOFigTesson.FIG_TESSON_OFFSET_4_SIZE_MAX1);
      int minSize = 20;
      maxSizeTesson = 40;
      int colorStart = p.get4(IBOFigTesson.FIG__OFFSET_06_COLOR4);
      ByteObject fdefinition = p.getSubFirst(IBOTypesBOC.TYPE_021_FUNCTION);
      ColorFunction cf = boc.getColorFunctionFactory().createColorFunction(fdefinition);
      cf.setRandom(r);

      ColorIteratorFun cif = new ColorIteratorFun(boc, cf, colorStart);

      int dw = minSize + r.nextInt(maxSizeTesson);
      int dh = minSize + r.nextInt(maxSizeTesson);

      int dx1 = x;
      int dy1 = y;
      int totalW = 0;
      boolean isContinueW = true;
      while (isContinueW) {
         int colW = minSize + r.nextInt(maxSizeTesson);
         if (totalW + colW > w) {
            colW = w - totalW;
            isContinueW = false;
         }
         int dy2 = dy1;
         int dx2 = dx1 + colW;
         cif.iterateColor(g);
         g.fillTriangle(dw, dh, dx1, dy1, dx2, dy2);

         totalW += colW;
         dx1 += colW;
      }
   }

   public static IntBuffer getBufDistruted(Random r, UCtx uc, int min, int max, int size) {
      IntBuffer ibws = new IntBuffer(uc);
      ibws.addInt(0);
      int totalW = 0;
      int wDiff = 0;
      do {
         int colW = min + r.nextInt(max);
         totalW += colW;
         wDiff = totalW - size;
         if (wDiff > 0) {
            colW = colW - wDiff;
            totalW = size;
         }
         ibws.addInt(totalW);
      } while (wDiff < 0);
      return ibws;
   }

   public static void drawFigTessonSpiral2(GraphicsX g, int x, int y, int w, int h, ByteObject p, Random r, BOCtx boc) {

   }

   public static void drawFigTessonTrigInCircle(GraphicsX g, int x, int y, int w, int h, ByteObject p, Random r, BOCtx boc) {
      int maxSizeTesson = p.get1(IBOFigTesson.FIG_TESSON_OFFSET_4_SIZE_MAX1);
      int minSize = 2700;
      maxSizeTesson = 80;

      int colorStart = p.get4(IBOFigTesson.FIG__OFFSET_06_COLOR4);
      ByteObject fdefinition = p.getSubFirst(IBOTypesBOC.TYPE_021_FUNCTION);
      ColorFunction cf = boc.getColorFunctionFactory().createColorFunction(fdefinition);
      cf.setRandom(r);

      ColorIteratorFun cif = new ColorIteratorFun(boc, cf, colorStart);

      cif.iterateColor(g);
      g.fillRect(x, y, w, h);

      UCtx uc = boc.getUC();

      Trig[] t4s = Trig.get4TrigFrom(uc, x, y, w, h, r);

      Geo2dUtils geo = uc.getGeo2dUtils();
      for (int i = 0; i < t4s.length; i++) {
         Trig tr = t4s[i];
         tr.draw(g, cif);


    
         int px = (int) tr.inCenterX();
         int py = (int) tr.inCenterY();
         float radius = tr.inCenterRadius();
         int cx = (int) (px - radius);
         int cy = (int) (py - radius);
         int cw = (int) (radius * 2);
         cif.iterateColor(g);
         g.fillArc(cx, cy, cw, cw, 0, 360);

         int x1 = (int) tr.inCenterD1X();
         int y1 = (int) tr.inCenterD1Y();
         int x2 = (int) tr.inCenterD2X();
         int y2 = (int) tr.inCenterD2Y();
         int x3 = (int) tr.inCenterDBaseX();
         int y3 = (int) tr.inCenterDBaseY();

      
         
         cif.iterateColor(g);
         g.fillTriangle(x1, y1, x2, y2, x3, y3);

         cif.iterateColor(g);
         g.drawLine(px, py, x1, y1);
         g.drawLine(px, py, x2, y2);
         g.drawLine(px, py, x3, y3);
         
//         int[] abcLine1 = tr.abcLine1();
//         int[] abcLine2 = tr.abcLine2();
//         int[] pi = geo.getIntersectionLinePoint(abcLine1, abcLine2);
//         cif.iterateColor(g);
//         g.drawLine(px, py, pi[0], pi[1]);
      }
   }

   public static void drawFigTessonTrigCentroid(GraphicsX g, int x, int y, int w, int h, ByteObject p, Random r, BOCtx boc) {
      int maxSizeTesson = p.get1(IBOFigTesson.FIG_TESSON_OFFSET_4_SIZE_MAX1);
      int minSize = 2700;
      maxSizeTesson = 80;

      int colorStart = p.get4(IBOFigTesson.FIG__OFFSET_06_COLOR4);
      ByteObject fdefinition = p.getSubFirst(IBOTypesBOC.TYPE_021_FUNCTION);
      ColorFunction cf = boc.getColorFunctionFactory().createColorFunction(fdefinition);
      cf.setRandom(r);

      ColorIteratorFun cif = new ColorIteratorFun(boc, cf, colorStart);

      cif.iterateColor(g);
      g.fillRect(x, y, w, h);

      UCtx uc = boc.getUC();

      //one point in rectangle defined 4 triangles

      Trig[] t4s = Trig.get4TrigFrom(uc, x, y, w, h, r);

      BufferObject trigsFinal = new BufferObject(uc);
      BufferObject trigsToProcessed = new BufferObject(uc);
      trigsToProcessed.add(t4s);
      Trig t = null;
      while ((t = (Trig) trigsToProcessed.removeFirst()) != null) {
         //#debug
         uc.toDLog().pDraw("Area=" + t.area(), t, TessonUtilz.class, "drawFigTessonSpiral", ITechLvl.LVL_05_FINE, true);
         if (t.area() > minSize) {
            int cx = (int) t.centroidX();
            int cy = (int) t.centroidY();
            Trig[] ts = t.getTrigsFromP(cx, cy);
            trigsToProcessed.add(ts);
         } else {
            //add it to final buffer
            trigsFinal.add(t);
         }
      }

      for (int i = 0; i < trigsFinal.getSize(); i++) {
         Trig tr = (Trig) trigsFinal.get(i);
         tr.draw(g, cif);
      }
   }

   public static void drawFigSierpinksy(GraphicsX g, int x, int y, int w, int h, ByteObject p, Random r, BOCtx boc) {

      int maxSizeTesson = p.get1(IBOFigTesson.FIG_TESSON_OFFSET_4_SIZE_MAX1);

      int colorStart = p.get4(IBOFigTesson.FIG__OFFSET_06_COLOR4);
      ByteObject fdefinition = p.getSubFirst(IBOTypesBOC.TYPE_021_FUNCTION);
      ColorFunction cf = boc.getColorFunctionFactory().createColorFunction(fdefinition);
      cf.setRandom(r);

      ColorIteratorFun cif = new ColorIteratorFun(boc, cf, colorStart);

      int minSize = 8;
      int xA = x;
      int yA = y + h; // (bas-gauche)
      int xB = x + w;
      int yB = y + h; // (bas-droite)
      int xC = x + w / 2;
      int yC = y;
      int[] xs = { xA, xB, xC };
      int[] ys = { yA, yB, yC };

      drawSierpinskiTriangle(g, xs, ys, w / 2, minSize, cif); // démarrer la récursion

   }

   public static void processTrigs(BufferObject trigsFinal, BufferObject trigsToProcessed, Trig[] trigs, int minSize, Random r) {
      for (int i = 0; i < trigs.length; i++) {
         Trig t = trigs[i];
         if (t.area() > minSize) {
            int cx = (int) t.centroidX();
            int cy = (int) t.centroidY();
            Trig[] ts = t.getTrigsFromP(cx, cy);
            trigsToProcessed.add(ts);
         } else {
            //add it to final buffer
            trigsFinal.add(t);
         }
      }
   }

   private static void drawTriangle(GraphicsX g, int[] x, int[] y, ColorIteratorFun cif) {
      cif.iterateColor(g);
      g.fillTriangle(x[0], y[0], x[1], y[1], x[2], y[2]);
   }

   private static void drawSierpinskiTriangle(GraphicsX g, int[] x, int[] y, int d, int minSize, ColorIteratorFun cif) {
      if (d < minSize) {
         drawTriangle(g, x, y, cif);
      } else {
         drawTriangle(g, x, y, cif);
         // milieux des côtés du triangle:
         int xMc = (x[0] + x[1]) / 2, yMc = (y[0] + y[1]) / 2;
         int xMb = (x[0] + x[2]) / 2, yMb = (y[0] + y[2]) / 2;
         int xMa = (x[1] + x[2]) / 2, yMa = (y[1] + y[2]) / 2;

         int[] xNouveau1 = { x[0], xMc, xMb };
         int[] yNouveau1 = { y[0], yMc, yMb };
         drawSierpinskiTriangle(g, xNouveau1, yNouveau1, d / 2, minSize, cif);

         int[] xNouveau2 = { x[1], xMc, xMa };
         int[] yNouveau2 = { y[1], yMc, yMa };
         drawSierpinskiTriangle(g, xNouveau2, yNouveau2, d / 2, minSize, cif);

         int[] xNouveau3 = { x[2], xMb, xMa };
         int[] yNouveau3 = { y[2], yMb, yMa };
         drawSierpinskiTriangle(g, xNouveau3, yNouveau3, d / 2, minSize, cif);
      }
   }

   public static void drawFigTessonOneCenter(GraphicsX g, int x, int y, int w, int h, ByteObject p, Random r, BOCtx boc) {
      int maxSizeTesson = p.get1(IBOFigTesson.FIG_TESSON_OFFSET_4_SIZE_MAX1);
      int minSize = 10;
      maxSizeTesson = 80;

      int colorStart = p.get4(IBOFigTesson.FIG__OFFSET_06_COLOR4);
      ByteObject fdefinition = p.getSubFirst(IBOTypesBOC.TYPE_021_FUNCTION);
      ColorFunction cf = boc.getColorFunctionFactory().createColorFunction(fdefinition);
      cf.setRandom(r);

      ColorIteratorFun cif = new ColorIteratorFun(boc, cf, colorStart);

      cif.iterateColor(g);
      g.fillRect(x, y, w, h);

      UCtx uc = boc.getUC();

      IntBuffer ibws1 = getBufDistruted(r, uc, minSize, maxSizeTesson, w);
      IntBuffer ibws2 = getBufDistruted(r, uc, minSize, maxSizeTesson, w);
      IntBuffer ibhs1 = getBufDistruted(r, uc, minSize, maxSizeTesson, h);
      IntBuffer ibhs2 = getBufDistruted(r, uc, minSize, maxSizeTesson, h);

      int x1 = x + r.nextInt(w);
      int y1 = y + r.nextInt(h);
      drawX(ibws1, x, y, x1, y1, cif, g);
      drawX(ibws2, x, y + h, x1, y1, cif, g);

      drawY(ibhs1, x, y, x1, y1, cif, g);
      drawY(ibhs2, x + w, y, x1, y1, cif, g);

   }

   public static void drawX(IntBuffer work, int x, int y, int x1, int y1, ColorIteratorFun cif, GraphicsX g) {
      for (int j = 0; j < work.getSize() - 1; j++) {
         int x2 = x + work.get(j);
         int x3 = x + work.get(j + 1);
         cif.iterateColor(g);
         g.fillTriangle(x1, y1, x2, y, x3, y);
      }
   }

   public static void drawY(IntBuffer work, int x, int y, int x1, int y1, ColorIteratorFun cif, GraphicsX g) {
      for (int j = 0; j < work.getSize() - 1; j++) {
         int y2 = y + work.get(j);
         int y3 = y + work.get(j + 1);
         cif.iterateColor(g);
         g.fillTriangle(x1, y1, x, y2, x, y3);
      }
   }

   public static void drawFigTessonV(GraphicsX g, int x, int y, int w, int h, ByteObject p, Random r, BOCtx boc) {
      int maxSizeTesson = p.get1(IBOFigTesson.FIG_TESSON_OFFSET_4_SIZE_MAX1);
      int minSize = 20;
      maxSizeTesson = 40;
      int colorStart = p.get4(IBOFigTesson.FIG__OFFSET_06_COLOR4);
      ByteObject fdefinition = p.getSubFirst(IBOTypesBOC.TYPE_021_FUNCTION);
      ColorFunction cf = boc.getColorFunctionFactory().createColorFunction(fdefinition);
      cf.setRandom(r);

      ColorIteratorFun cif = new ColorIteratorFun(boc, cf, colorStart);
      int dx = x;
      int totalW = 0;
      boolean isContinueW = true;
      while (isContinueW) {

         int colW = minSize + r.nextInt(maxSizeTesson);
         if (totalW + colW > w) {
            colW = w - totalW;
            isContinueW = false;
         }

         boolean isContinueH = true;
         int dy = y;
         int totalH = 0;
         while (isContinueH) {
            int rowH = minSize + r.nextInt(maxSizeTesson);
            if (totalH + rowH > h) {
               rowH = h - totalH;
               isContinueH = false;
            }
            totalH += rowH;
            cif.iterateColor(g);
            g.fillRect(dx, dy, colW, rowH);

            int first = r.nextInt(2);
            if (first == 0) {
               cif.iterateColor(g);
               g.fillTriangle(dx, dy, dx + colW, dy, dx, dy + rowH);

               cif.iterateColor(g);
               g.fillTriangle(dx, dy, dx + colW, dy + rowH, dx, dy + rowH);
            } else {
               cif.iterateColor(g);
               g.fillTriangle(dx, dy, dx + colW, dy + rowH, dx, dy + rowH);

               cif.iterateColor(g);
               g.fillTriangle(dx, dy, dx + colW, dy, dx, dy + rowH);
            }

            dy += rowH;
         }
         totalW += colW;
         dx += colW;
      }
   }
}
