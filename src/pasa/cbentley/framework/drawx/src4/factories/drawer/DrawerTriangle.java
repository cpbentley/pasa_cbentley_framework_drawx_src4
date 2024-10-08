/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories.drawer;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.objects.color.ColorIterator;
import pasa.cbentley.byteobjects.src4.objects.color.IBOGradient;
import pasa.cbentley.byteobjects.src4.objects.color.ITechGradient;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.core.src4.utils.MathUtils;
import pasa.cbentley.core.src4.utils.RgbUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigLosange;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigTriangle;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.layouter.src4.tech.ITechLayout;

public class DrawerTriangle extends ObjectDrw implements ITechFigure, IBOFigTriangle {

   public DrawerTriangle(DrwCtx drc) {
      super(drc);
   }

   /**
    * A Losange is always only just 2 Triangles.
    * <br>
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawFigLosange(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int type = p.get1(IBOFigLosange.FIG_LOSANGE_OFFSET_4_TYPE1);

      if (type == ITechFigure.FIG_LOSANGE_TYPE_0_COLOR) {
         ByteObject grad = p.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);

         int color = p.get4(IBOFigure.FIG__OFFSET_06_COLOR4);
         //when fill is different than zero, draw on rgbimage then mask
         int fill = p.get2(IBOFigLosange.FIG_LOSANGE_OFFSET_3_FILL2);
         int overStep = p.get2(IBOFigLosange.FIG_LOSANGE_OFFSET_2_OVERSTEP2);

         if (grad == null) {
            g.setColor(color);
            drawShapeLosange(g, x, y, w, h, p, overStep);
         } else {
            double beta = MathUtils.aTan((double) w, (double) h);
            double alpha = MathUtils.aTan((double) h, (double) w);
            double cosbeta = Math.cos(beta);
            double cosalpha = Math.cos(alpha);
            int typeGrad = grad.get1(IBOGradient.GRADIENT_OFFSET_07_TYPE1);
            int gradSize = getLosangeGradSize(grad, typeGrad, w, h);
            int val = 0;
            ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
            int count = 0;
            while ((count = ci.iterateTotalSize(g)) != -1) {
               int countTan = (int) (Math.tan(beta) * (double) (count));
               int countAlpha = (int) (cosalpha * (double) (count));
               int countAlphaD = (int) ((double) (count) / cosalpha);
               int countBetaD = (int) ((double) (count) / cosbeta);
               int countBeta = (int) (cosbeta * (double) (count));
               int d2 = countTan / 2;
               //#debug
               toDLog().pFlow("count=" + count + " countAlpha=" + countAlpha + " countAlphaD=" + countAlphaD + " countBeta=" + countBeta + " countBetaD=" + countBetaD + " countTan=" + countTan, null, DrawerTriangle.class, "drawFigLosange", LVL_05_FINE, true);
               int c2 = count << 1;
               switch (typeGrad) {
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_0_SQUARE:
                     drawShapeLosange(g, x + count, y + count, w - c2, h - c2, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_1_FULLVERTICAL:
                     drawShapeLosange(g, x + count, y, w - c2, h, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_2_FULLHORIZ:
                     drawShapeLosange(g, x, y + count, w, h - c2, p, overStep);
                     break;

                  case ITechGradient.GRADIENT_TYPE_LOSANGE_3_FULLDIAGDOWN:
                     drawShapeLosange(g, x + w / 2 - countTan, y + countBeta, (int) (w / 2 + countAlphaD), h - countBeta * 2, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_4_FULLDIAGUP:
                     drawShapeLosange(g, x + countAlphaD, y + countBetaD, w - c2, h - c2, p, overStep);
                     break;

                  case ITechGradient.GRADIENT_TYPE_LOSANGE_5_TOP:
                     drawShapeLosange(g, x + count, y, w - c2, h - count, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_6_BOT:
                     drawShapeLosange(g, x + count, y + count, w - c2, h - count, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_7_LEFT:
                     drawShapeLosange(g, x, y + count, w - c2, h - c2, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_8_RIGHT:
                     drawShapeLosange(g, x + count, y + count, w - count, h - c2, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_01_SQUARE:
                     drawShapeLosange(g, x + count, y, w - count, h, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_02_SQUARE:
                     drawShapeLosange(g, x + (int) (count * cosbeta), y + (int) (count * cosalpha), w - c2, h - c2, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_03_SQUARE:
                     drawShapeLosange(g, x + w / 2 - count, y + count, (int) (w / 2 + (cosalpha * count)), h - c2, p, overStep);
                     break;
                  case ITechGradient.GRADIENT_TYPE_LOSANGE_04_SQUARE:
                     drawShapeLosange(g, x + count / 2, y + count, w - count, h - c2, p, overStep);
                     break;
                  default:
                     break;
               }
            }
         }
      } else if (type == ITechFigure.FIG_LOSANGE_TYPE_1_TRIANGLE) {
         drawFigLosangeAs1Triangle(g, x, y, w, h, p);
      } else if (type == ITechFigure.FIG_LOSANGE_TYPE_2_TRIANGLES) {
         drawFigLosangeAs2Triangles(p);
      } else if (type == ITechFigure.FIG_LOSANGE_TYPE_3_ANGLES) {
         drawFigLosangeAsAngles(g, x, y, w, h, p);
      }
   }

   private void drawFigLosangeAs1Triangle(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      ByteObject trig = p.getSubFirst(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
      trig = trig.cloneCopyHeadRefParams();
      trig.setFlag(IBOFigTriangle.FIG_TRIANGLE_OFFSET_01_FLAG1, IBOFigTriangle.FIG_TRIANGLE_OFFSET_01_FLAG1, false);
      if (p.hasFlag(IBOFigLosange.FIG_LOSANGE_OFFSET_1_FLAG, IBOFigLosange.FIG_LOSANGE_FLAG_1_HORIZ)) {
         int w1 = w / 2;
         int w2 = w - w1;
         trig.setValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, C.TYPE_02_LEFT, 2);
         drawFigTriangle(g, x, y, w1, h, trig);
         trig.setValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, C.TYPE_03_RIGHT, 2);
         drawFigTriangle(g, x + w1, y, w2, h, trig);
      } else {
         int h1 = h / 2;
         int h2 = h - h1;
         trig.setValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, C.TYPE_00_TOP, 2);
         drawFigTriangle(g, x, y + h1, w, h1, trig);
         trig.setValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, C.TYPE_01_BOTTOM, 2);
         drawFigTriangle(g, x + h1, y, w, h2, trig);
      }
   }

   private void drawFigLosangeAs2Triangles(ByteObject p) {
      ByteObject trig1 = p.getSubOrder(IBOTypesDrawX.TYPE_DRWX_00_FIGURE, 0);
      ByteObject trig2 = p.getSubOrder(IBOTypesDrawX.TYPE_DRWX_00_FIGURE, 1);
   }

   private void drawFigLosangeAsAngles(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      ByteObject trig = p.getSubFirst(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
      int angle = trig.get2(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2);
      int h1 = h / 2;
      int h2 = h - h1;
      int w1 = w / 2;
      int w2 = w - w1;
      switch (angle) {
         case C.TYPE_00_TOP:
            drawFigTriangle(g, x, y + h1, w, h1, trig);
            trig.setValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, C.TYPE_01_BOTTOM, 2);
            drawFigTriangle(g, x + h1, y, w, h2, trig);
            break;
         case C.TYPE_02_LEFT:
            drawFigTriangle(g, x, y, w1, h, trig);
            trig.setValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, C.TYPE_03_RIGHT, 2);
            drawFigTriangle(g, x + w1, y, w2, h, trig);
            break;
         case C.TYPE_11_MID_BotRight:
            drawFigTriangle(g, x, y, w, h1, trig);
            trig.setValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, C.TYPE_08_MID_TopLeft, 2);
            drawFigTriangle(g, x + h1, y, w, h2, trig);
            break;

         default:
            break;
      }
      trig.setValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, angle, 2);
   }

   /**
    * Entry point for drawing a Triangle.
    * <br>
    * 
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawFigTriangle(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int type = p.get1(FIG_TRIANGLE_OFFSET_02_TYPE1);
      switch (type) {
         case FIG_TRIANGLE_TYPE_0_DEGREE_360:
            drawTriangleComplexAngle(g, x, y, w, h, p);
            break;
         case FIG_TRIANGLE_TYPE_1_DIRECTIONAL:
            drawTriangleDirectional(g, x, y, w, h, p);
            break;
         case FIG_TRIANGLE_TYPE_2_ANCHORS:
            drawTriangleAnchors(g, x, y, w, h, p);
            break;
         default:
            throw new IllegalArgumentException();
      }
   }

   private void drawShapeLosange(GraphicsX g, int x, int y, int w, int h, ByteObject p, int ostep) {
      int px = w / 2;
      int py = h / 2;

      if (p.hasFlag(IBOFigLosange.FIG_LOSANGE_OFFSET_1_FLAG, IBOFigLosange.FIG_LOSANGE_FLAG_1_HORIZ)) {
         if (p.hasFlag(IBOFigLosange.FIG_LOSANGE_OFFSET_1_FLAG, IBOFigLosange.FIG_LOSANGE_FLAG_4_NOED_PAPILLION)) {
            g.fillTriangle(x, y, x + px + ostep, y + py, x, y + h);
            g.fillTriangle(x + w, y, x + px - ostep, y + py, x + w, y + h);
         } else {
            if (p.hasFlag(IBOFigLosange.FIG_LOSANGE_OFFSET_1_FLAG, IBOFigLosange.FIG_LOSANGE_FLAG_3_CONTOUR)) {
               g.fillTriangle(x, y, x + px + ostep, y, x, y + py);
               //bot left
               g.fillTriangle(x, y + py, x + px + ostep, y + h, x, y + h);
               //top rigth
               g.fillTriangle(x + w, y, x + px - ostep, y, x + w, y + py);
               //bot right
               g.fillTriangle(x + w, y + h, x + px - ostep, y + h, x + w, y + py);
            } else {
               g.fillTriangle(x, y + py, x + px + ostep, y, x + px + ostep, y + h);
               g.fillTriangle(x + px - ostep, y + h, x + px - ostep, y, x + w, y + py);
            }
         }
      } else {
         if (p.hasFlag(IBOFigLosange.FIG_LOSANGE_OFFSET_1_FLAG, IBOFigLosange.FIG_LOSANGE_FLAG_4_NOED_PAPILLION)) {
            g.fillTriangle(x, y, x + px, y + py + ostep, x + w, y);
            g.fillTriangle(x, y + h, x + px, y + py - ostep, x + w, y + h);
         } else {
            if (p.hasFlag(IBOFigLosange.FIG_LOSANGE_OFFSET_1_FLAG, IBOFigLosange.FIG_LOSANGE_FLAG_3_CONTOUR)) {
               g.fillTriangle(x, y, x + px, y, x, y + py + ostep);
               //bot left
               g.fillTriangle(x, y + py - ostep, x + px, y + h, x, y + h);
               //top rigth
               g.fillTriangle(x + w, y, x + px, y, x + w, y + py + ostep);
               //bot right
               g.fillTriangle(x + w, y + h, x + px, y + h, x + w, y + py - ostep);
            } else {
               g.fillTriangle(x + px, y, x + w, y + py + ostep, x, y + py + ostep);
               g.fillTriangle(x + px, y + h, x, y + py - ostep, x + w, y + py - ostep);
            }
         }
      }
   }

   public void drawTransIsoTriangle(GraphicsX g, int dir, int color, int x, int y, int base, int H, int tsize) {
      drawTransIsoTriangle(g, dir, color, x, y, base, H, tsize, ColorUtils.FULLY_TRANSPARENT_WHITE, true);
   }

   public void drawTransIsoTriangle(GraphicsX g, int dir, int color, int x, int y, int base, int H, int tsize, int bgColor, boolean borderOnBg) {
      int h = H;
      int w = 2 * base;
      if (dir == C.ANGLE_LEFT_180 || dir == C.ANGLE_RIGHT_0) {
         w = H;
         h = 2 * base;
      }
      RgbImage img = drc.getCache().create(w, h);
      GraphicsX gr = img.getGraphicsX();
      if (dir == C.ANGLE_UP_90)
         drawTriangleIso(gr, dir, color, base, 0, base, H);
      else if (dir == C.ANGLE_DOWN_270)
         drawTriangleIso(gr, dir, color, base, H, base, H);
      else if (dir == C.ANGLE_LEFT_180)
         drawTriangleIso(gr, dir, color, 0, base, base, H);
      else if (dir == C.ANGLE_RIGHT_0)
         drawTriangleIso(gr, dir, color, H, base, base, H);

      int[] rgb = img.getRgbData();
      RgbUtils.setAlphaToColorRGB(rgb, -1, 0);
      int maskColor = ColorUtils.FULLY_TRANSPARENT_WHITE;
      RgbUtils.setAlphaGradient(rgb, img.getWidth(), img.getHeight(), maskColor, tsize, borderOnBg);
      if (dir == C.ANGLE_UP_90)
         g.drawRGB(rgb, 0, w, x - base, y, w, h, true);
      else if (dir == C.ANGLE_DOWN_270)
         g.drawRGB(rgb, 0, w, x - base, y - H, w, h, true);
      else if (dir == C.ANGLE_LEFT_180)
         g.drawRGB(rgb, 0, w, x, y - base, w, h, true);
      else if (dir == C.ANGLE_RIGHT_0)
         g.drawRGB(rgb, 0, w, x - H, y - base, w, h, true);

   }

   public void drawTriangleAnchors(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      ByteObject grad = p.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      int x1 = p.get1(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2);
      int y1 = p.get1(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2 + 1);
      int x2 = p.get1(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2 + 2);
      int y2 = p.get1(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2 + 3);
      int x3 = p.get1(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2 + 4);
      int y3 = p.get1(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2 + 5);
      int color = p.get4(IBOFigure.FIG__OFFSET_06_COLOR4);
      if (g.hasGradient() && grad != null) {
         int gradSize = Math.min(w / 2, h / 2);
         int count = 0;
         ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
         while ((count = ci.iteratePixelCount(g)) != -1) {
            drawTriangleAnchors(g, x, y, w, h, x1, y1, x2, y2, x3, y3);
            x++;
            y++;
            w -= 2;
            h -= 2;
         }
      } else {
         g.setColor(color);
         drawTriangleAnchors(g, x, y, w, h, x1, y1, x2, y2, x3, y3);
      }

   }

   public void drawTriangleAnchors(GraphicsX g, int x, int y, int w, int h, int x1, int y1, int x2, int y2, int x3, int y3) {

      int xc = (x + w) / 2;
      int yc = (y + h) / 2;

      int dx1 = get_0_200Ratio(x1, x, w, xc);
      int dy1 = get_0_200Ratio(y1, y, h, yc);

      int dx2 = get_0_200Ratio(x2, x, w, xc);
      int dy2 = get_0_200Ratio(y2, y, h, yc);

      int dx3 = get_0_200Ratio(x3, x, w, xc);
      int dy3 = get_0_200Ratio(y3, y, h, yc);

      g.fillTriangle(dx1, dy1, dx2, dy2, dx3, dy3);
   }

   public void drawTriangleAnchorsGradient(GraphicsX g, int x, int y, int w, int h, int color, int angle, ByteObject grad) {

   }

   /**
    * Complext with no little h
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawTriangleComplexAngle(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int color = p.getValue(IBOFigure.FIG__OFFSET_06_COLOR4, 4);
      double angle = p.getValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, 2);
      //adjust for weird triangle draw implementation
      angle += 90;
      double rad = Math.toRadians(angle);
      int diff = Math.abs(w - h);
      double len1 = h * Math.abs(Math.sin(rad)) + w * Math.abs(Math.cos(rad));
      double len2 = w * Math.abs(Math.sin(rad)) + h * Math.abs(Math.cos(rad));

      int base = w / 2;
      int H = h;
      int h2 = H / 2;
      double cosBase = (Math.cos(rad) * base);
      double cosH = (Math.cos(rad) * H);
      double sinBase = (Math.sin(rad) * base);
      double sinH = (Math.sin(rad) * H);
      double cosAsinH = Math.cos(rad) * sinH;
      double cosAsinBase = Math.cos(rad) * sinBase;
      double sinAcosH = Math.sin(rad) * cosH;
      double sinAcosBase = Math.sin(rad) * cosBase;
      double i = angle;
      int ex = (int) (base * Math.sin(Math.toRadians(i)));
      int ey = (int) (h2 * Math.cos(Math.toRadians(i)));

      int dx = x + (int) (cosAsinBase + sinAcosH);
      int dy = y + (int) (cosAsinH + sinAcosBase);
      dx = x + base + ex;
      dy = y + h2 + ey;
      //SystemLog.printDraw(angle);
      //match coordinate to fit into the dimension
      int oh = h2;
      int obase = base;
      base = (int) ((oh * Math.abs(Math.sin(rad))) + obase * Math.abs(Math.cos(rad)));
      h = (int) (oh * Math.abs(Math.cos(rad)) + obase * Math.abs(Math.sin(rad)));
      int minus = (int) (Math.abs(Math.cos(rad)) * Math.abs(Math.sin(rad)) * diff);
      base -= minus;
      h -= minus;

      //decode the small H value
      int littleH = drc.getSizer().codedSizeDecode(p, IBOFigTriangle.FIG_TRIANGLE_OFFSET_04_h4, w, h, ITechLayout.CTX_2_HEIGHT);

      drawTriangleIso(g, (int) angle - 90, color, dx, dy, base, h * 2, littleH);
   }

   public void drawTriangleDirectional(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      if (g.hasGradient() && p.hasFlag(IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_2_GRADIENT)) {
         int trigType = p.get2(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2);
         if (trigType < 4) {
            drawTriangleGradient_0_3(g, x, y, w, h, p);
         } else if (trigType < 8) {
            drawTriangleGradient_4_7(g, x, y, w, h, p);
         } else if (trigType < 12) {
            drawTriangleGradient_8_11(g, x, y, w, h, p);
         } else if (trigType < 16) {
            drawTriangleGradientOther(g, x, y, w, h, p);
         }
      } else {
         drawTriangleSimple(g, x, y, w, h, p);
      }
   }

   /**
    * If Gradient is null
    * GradientHelper will be called once TODO
    * @param g
    * @param x
    * @param y
    * @param dir
    * @param base
    * @param H
    * @param color
    * @param grad
    */
   private void drawTriangleGradient_0_3(GraphicsX g, int x, int y, int w, int h, ByteObject fig) {
      int color = fig.getValue(IBOFigure.FIG__OFFSET_06_COLOR4, 4);
      ByteObject grad = fig.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      int type = grad.get1(IBOGradient.GRADIENT_OFFSET_07_TYPE1);
      int trigType = fig.get2(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2);
      boolean isWire = grad.hasFlag(IBOGradient.GRADIENT_OFFSET_04_FLAGX1, IBOGradient.GRADIENT_FLAGX_5_WIRE);
      int gradSize = getTrigGradSize(grad, type, w, h, trigType);
      double alpha = MathUtils.aTan((double) w / 2, (double) h / 2);
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      drc.getFigureOperator().fillArea(g, x, y, w, h, grad, ci);
      
      int previousC = 0;
      int dx = x;
      int dy = y;
      int w2 = w / 2;
      int h2 = h / 2;
      while ((count = ci.iteratePixelCount(g)) != -1) {
         //SystemLog.printDraw(i + " dir="+dir + " type="+type + " color="+debugColor(g.getColor()));
         int count2 = count * 2;
         int countDiv2 = count / 2;
         int d = (int) (Math.tan(alpha) * (double) (count));
         int d2 = d / 2;
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_TRIG_00_TENT:
               //inverse normal top
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + d2, y + count, w - d, h - count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM) {
                  drawTriangleSwitch(g, x + d2, y, w - d, h - count, trigType);
               } else if (trigType == C.TYPE_02_LEFT) {
                  drawTriangleSwitch(g, x + count, y + d2, w - count, h - d, trigType);
               } else {
                  drawTriangleSwitch(g, x, y + d2, w - count, h - d, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_01_TENT_JESUS:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + d, y + count, w - (2 * d), h - count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x + d, y, w - (2 * d), h - count, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x + count, y + d, w - count, h - (2 * d), trigType);
               else
                  drawTriangleSwitch(g, x, y + d, w - count, h - (2 * d), trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_02_TOP_JESUS:
               if (trigType == C.TYPE_00_TOP || trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x + count, y, w - count2, h, trigType);
               else
                  drawTriangleSwitch(g, x, y + count, w, h - count2, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_03_TUNNEL:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + d, y, w - (2 * d), h - count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x + d, y + count, w - (2 * d), h - count, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x, y + d, w - count, h - (2 * d), trigType);
               else
                  drawTriangleSwitch(g, x + count, y + d, w - count, h - (2 * d), trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_04_FULL:
               if (w > h) {
                  int hcount = count2 + count;
                  int hcount2 = hcount / 2;
                  drawTriangleSwitch(g, x + hcount2, y + count, w - hcount, h - count2, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + count, w - count2, h - count2, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_05_OPAQUEBASE:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x, y + count, w, h - count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x, y, w, h - count, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x + count, y, w - count, h, trigType);
               else
                  drawTriangleSwitch(g, x, y, w - count, h, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_06_OPAQUE_CENTER:
               drawTriangleSwitch(g, x + d2, y + d2, w - d, h - d, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_07_ARROW:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + w2 - d, y + count, 2 * d, count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x + w2 - d, y + h - 2 * count, 2 * d, count, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x + count, y + h2 - count, count, 2 * d, trigType);
               else
                  drawTriangleSwitch(g, x + w - 2 * count, y + h2 - count, count, 2 * d, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_08_NORMAL:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + d / 2, y, w - d, h - count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM) {
                  drawTriangleSwitch(g, x + d2, y + count, w - d, h - count, trigType);
               } else if (trigType == C.TYPE_02_LEFT) {
                  drawTriangleSwitch(g, x, y + d2, w - count, h - d, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + d2, w - count, h - d, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_09_FAT_HALO:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x, y + 2 * count, w, h2 - count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x, y + h2 - count, w, h2 - count, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x + 2 * count, y, w2 - count, h, trigType);
               else
                  drawTriangleSwitch(g, x + w2 - count, y, w2 - count, h, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_10_SWIPE:
               if (trigType == C.TYPE_00_TOP) {
                  if (isWire) {
                     //TODO stroke style
                     g.drawLine(x + count, y + h, x + w / 2, y);
                  } else {
                     g.fillTriangle(x + count, y + h, x + previousC, y + h, x + w2, y);
                  }
               } else if (trigType == C.TYPE_01_BOTTOM) {
                  g.fillTriangle(x + count, y, x + previousC, y, x + w2, y + h);
               } else if (trigType == C.TYPE_02_LEFT) {
                  g.fillTriangle(x + w, y + count, x + w, y + previousC, x, y + h2);
               } else {
                  g.fillTriangle(x, y + count, x, y + previousC, x + w, y + h2);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_11_FAT_STRIKE:
               //g.fillTriangle(x + count, y + h, x + previousC, y, x + w2, y + h);
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x, y, w, h2 + count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x, y + h2 - count, w, h2 + count, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x, y, w2 + count, h, trigType);
               else
                  drawTriangleSwitch(g, x + w2 - count, y, w2 + count, h, trigType);

               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_12_POINTY:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + (w - count) / 4, y + count, w2 + countDiv2, h - count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x + (w - count) / 4, y, w2 + countDiv2, h - count, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x + count, y + (h - count) / 4, w - count, h2 + countDiv2, trigType);
               else
                  drawTriangleSwitch(g, x, y + (h - count) / 4, w - count, h2 + countDiv2, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_13_FAT_ETHER_BASE:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x, y + h - count2, w, count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x, y + count, w, count, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x + w - count2, y, count, h, trigType);
               else
                  drawTriangleSwitch(g, x + count, y, count, h, trigType);

               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_14_TRY:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + count, y + count2, w - count, h - countDiv2, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x + count, y + count2, w - count, h - countDiv2, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x + count2, y + count, w - countDiv2, h - count, trigType);
               else
                  drawTriangleSwitch(g, x + count2, y + count2, w - countDiv2, h - countDiv2, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_15_POINTY_LYS:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + (w - count) / 4, y + count2, w2 + countDiv2, h - count2, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x + (w - count) / 4, y, w2 + countDiv2, h - count2, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x + count2, y + (h - count) / 4, w - count2, h2 + countDiv2, trigType);
               else
                  drawTriangleSwitch(g, x, y + (h - count) / 4, w - count2, h2 + countDiv2, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_16_TENT_SMALL:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + d2, y + count, w - d, h - count, trigType);
               else if (trigType == C.TYPE_01_BOTTOM) {
                  drawTriangleSwitch(g, x + d2, y, w - d, h - count, trigType);
               } else if (trigType == C.TYPE_02_LEFT) {
                  drawTriangleSwitch(g, x + count, y + d2, w - count, h - d, trigType);
               } else {
                  drawTriangleSwitch(g, x, y + d2, w - count, h - d, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_17_POINTY_DODGE:
               if (trigType == C.TYPE_00_TOP)
                  drawTriangleSwitch(g, x + (w - count) / 3, y + count2, w2 + countDiv2, h - count2, trigType);
               else if (trigType == C.TYPE_01_BOTTOM)
                  drawTriangleSwitch(g, x + (w - count) / 2, y, w2 + countDiv2, h - count2, trigType);
               else if (trigType == C.TYPE_02_LEFT)
                  drawTriangleSwitch(g, x + count2, y + (h - count) / 4, w - count2, h2 + countDiv2, trigType);
               else
                  drawTriangleSwitch(g, x + w2 - count, y + (h - count) / 6, w2 - count2, h2 + countDiv2 / 2, trigType);
               break;
            default:
               break;
         }
         previousC = count;
      }
   }

   /**
    * Gradient routines for corner triangles.
    * 
    * <li> {@link C#TYPE_04_TopLeft}
    * <li> {@link C#TYPE_05_TopRight}
    * <li> {@link C#TYPE_06_BotLeft}
    * <li> {@link C#TYPE_07_BotRight}
    * <br>
    * <br>
    * Support
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param fig
    */
   private void drawTriangleGradient_4_7(GraphicsX g, int x, int y, int w, int h, ByteObject fig) {
      int color = fig.getValue(IBOFigure.FIG__OFFSET_06_COLOR4, 4);
      ByteObject grad = fig.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      int type = grad.get1(IBOGradient.GRADIENT_OFFSET_07_TYPE1);
      int trigType = fig.get2(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2);
      int gradSize = getTrigGradSize_4_7(grad, type, w, h, trigType);
      double alpha = MathUtils.aTan((double) w / 2, (double) h / 2);
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      drc.getFigureOperator().fillArea(g, x, y, w, h, grad, ci);
      
      while ((count = ci.iteratePixelCount(g)) != -1) {
         //SystemLog.printDraw(i + " dir="+dir + " type="+type + " color="+debugColor(g.getColor()));
         int count2 = count * 2;
         int d = (int) (Math.tan(alpha) * (double) (count));
         int d2 = d / 2;
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_TRIG_00_TENT:
               //inverse normal top
               if (trigType == C.TYPE_04_TopLeft)
                  drawTriangleSwitch(g, x, y, w - count, h - count, trigType);
               else if (trigType == C.TYPE_05_TopRight) {
                  drawTriangleSwitch(g, x + count, y, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_06_BotLeft) {
                  drawTriangleSwitch(g, x, y + count, w - count, h - count, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_01_TENT_JESUS:
               if (trigType == C.TYPE_04_TopLeft)
                  drawTriangleSwitch(g, x, y, w, h - count, trigType);
               else if (trigType == C.TYPE_05_TopRight) {
                  drawTriangleSwitch(g, x, y, w, h - count, trigType);
               } else if (trigType == C.TYPE_06_BotLeft) {
                  drawTriangleSwitch(g, x, y + count, w, h - count, trigType);
               } else {
                  drawTriangleSwitch(g, x, y + count, w, h - count, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_02_TOP_JESUS:
               //inverse normal top
               if (trigType == C.TYPE_04_TopLeft)
                  drawTriangleSwitch(g, x, y, w - count, h, trigType);
               else if (trigType == C.TYPE_05_TopRight) {
                  drawTriangleSwitch(g, x + count, y, w - count, h, trigType);
               } else if (trigType == C.TYPE_06_BotLeft) {
                  drawTriangleSwitch(g, x, y, w - count, h, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y, w - count, h, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_03_TUNNEL:
               //inverse normal top
               if (trigType == C.TYPE_04_TopLeft)
                  drawTriangleSwitch(g, x, y + count, w - count, h - count, trigType);
               else if (trigType == C.TYPE_05_TopRight) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_06_BotLeft) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - 2 * count, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + 2 * count, w - count, h - count, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_04_FULL:
               //inverse normal top
               if (trigType == C.TYPE_04_TopLeft)
                  drawTriangleSwitch(g, x + count, y, w - count, h - count, trigType);
               else if (trigType == C.TYPE_05_TopRight) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_06_BotLeft) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - 2 * count, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + 2 * count, w - count, h - count, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_10_SWIPE:
               //in this case each count is the number of degrees from base angle
               if (trigType == C.TYPE_04_TopLeft) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_05_TopRight) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_06_BotLeft) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - 2 * count, trigType);
               } else {
                  //draw mini trig along 
                  drawTriangleSwitch(g, x + count, y + 2 * count, w - count, h - count, trigType);
               }
               break;
         }
      }
   }

   /**
    * Middle triangle only supports
    * 
    * <li> {@link C#TYPE_08_MID_TopLeft}
    * <li> {@link C#TYPE_09_MID_TopRight}
    * <li> {@link C#TYPE_10_MID_BotLeft}
    * <li> {@link C#TYPE_11_MID_BotRight}
    * 
    * <br>
    * <br>
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param fig
    */
   private void drawTriangleGradient_8_11(GraphicsX g, int x, int y, int w, int h, ByteObject fig) {
      int color = fig.getValue(IBOFigure.FIG__OFFSET_06_COLOR4, 4);
      ByteObject grad = fig.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      int type = grad.get1(IBOGradient.GRADIENT_OFFSET_07_TYPE1);
      int trigType = fig.get2(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2);
      int gradSize = getTrigGradSize(grad, type, w, h, trigType);
      double alpha = MathUtils.aTan((double) w / 2, (double) h / 2);
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      drc.getFigureOperator().fillArea(g, x, y, w, h, grad, ci);
      
      while ((count = ci.iteratePixelCount(g)) != -1) {
         //SystemLog.printDraw(i + " dir="+dir + " type="+type + " color="+debugColor(g.getColor()));
         int count2 = count * 2;
         int d = (int) (Math.tan(alpha) * (double) (count));
         int d2 = d / 2;
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_TRIG_04_FULL:
               if (w > h) {
                  int hcount = count2 + count;
                  int xc = hcount / 2;
                  drawTriangleSwitch(g, x + xc, y + count, w - hcount, h - count2, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + count, w - count2, h - count2, trigType);
               }
               break;
         }
      }
   }

   /**
    * Gradient routines for corner triangles.
    * 
    * <li> {@link C#TYPE_12_TopLeftDiagBot}
    * <li> {@link C#TYPE_13_TopLeftDiagRight}
    * <li> {@link C#TYPE_14_TopRightDiagBot}
    * <li> {@link C#TYPE_15_TopRightDiagLeft}
    * <br>
    * <br>
    * Support
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param fig
    */
   private void drawTriangleGradientOther(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      drawTriangleGradient_0_3(g, x, y, w, h, p);
   }

   /**
    * Draws an isocele triangle whose point is at x,y. <br>
    * 
    * @param g
    * @param angle angle is not normalized. should be done externally
    * @param x
    * @param y
    * @param base half the base
    * @param H distance between x,y and the baseline.
    */
   public void drawTriangleIso(GraphicsX g, int angle, int x, int y, int base, int H) {
      if (angle == C.ANGLE_UP_90)
         g.fillTriangle(x, y, x + base, y + H, x - base, y + H);
      else if (angle == C.ANGLE_DOWN_270)
         g.fillTriangle(x, y, x + base, y - H, x - base, y - H);
      else if (angle == C.ANGLE_LEFT_180)
         g.fillTriangle(x, y, x + H, y - base, x + H, y + base);
      else if (angle == C.ANGLE_RIGHT_0)
         g.fillTriangle(x, y, x - H, y - base, x - H, y + base);
      else {
         //special angle specified
         double rad = Math.toRadians(angle);
         double rad9 = Math.toRadians(90 - angle);
         double a = x - Math.cos(rad) * H;
         double b = y + Math.sin(rad) * H;
         int m = (int) (a + Math.cos(rad9) * base);
         int n = (int) (b + Math.sin(rad9) * base);
         int o = (int) (a - Math.sin(rad) * base);
         int p = (int) (b - Math.cos(rad) * base);
         g.fillTriangle(x, y, m, n, o, p);

      }
   }

   /**
    * 
    * @param g
    * @param color
    * @param x the x coordinate of unique corner
    * @param y the y coorindate of unique corner
    * @param base half of the base size
    * @param H height of the ISO triangle
    */
   public void drawTriangleIso(GraphicsX g, int dir, int color, int x, int y, int base, int H) {
      g.setColor(color);
      drawTriangleIso(g, dir, x, y, base, H);
   }

   /**
    * 
    * @param g
    * @param angle
    * @param color
    * @param x
    * @param y
    * @param base
    * @param H
    * @param h number of pixels eaten
    */
   public void drawTriangleIso(GraphicsX g, int angle, int color, int x, int y, int base, int H, int h) {
      g.setColor(color);
      if (h == 0) {
         drawTriangleIso(g, angle, x, y, base, H);
         return;
      }
      if (angle == C.ANGLE_UP_90) {
         g.fillTriangle(x, y, x + base, y + H, x, y + H - h);
         g.fillTriangle(x, y, x - base, y + H, x, y + H - h);
      } else if (angle == C.ANGLE_DOWN_270) {
         g.fillTriangle(x, y, x + base, y - H, x, y - H + h);
         g.fillTriangle(x, y, x - base, y - H, x, y - H + h);
      } else if (angle == C.ANGLE_LEFT_180) {
         g.fillTriangle(x, y, x + H, y - base, x + H - h, y);
         g.fillTriangle(x, y, x + H, y + base, x + H - h, y);
      } else if (angle == C.ANGLE_RIGHT_0) {
         g.fillTriangle(x, y, x - H, y - base, x - H + h, y);
         g.fillTriangle(x, y, x - H, y + base, x - H + h, y);
      } else {
         //special angle specified
         double rad = Math.toRadians(angle);
         double rad9 = Math.toRadians(90 - angle);
         double a = x - Math.cos(rad) * H;
         double b = y + Math.sin(rad) * H;
         int m = (int) (a + Math.cos(rad9) * base);
         int n = (int) (b + Math.sin(rad9) * base);
         int o = (int) (a - Math.sin(rad) * base);
         int p = (int) (b - Math.cos(rad) * base);
         int hx = x - (int) (Math.cos(rad) * (H - h));
         int hy = y + (int) (Math.sin(rad) * (H - h));
         g.fillTriangle(x, y, m, n, hx, hy);
         g.fillTriangle(x, y, o, p, hx, hy);
      }
   }

   public void drawTriangleLittleH(GraphicsX g, int x, int y, int w, int h, ByteObject p) {

   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param trig
    */
   public void drawTriangleSimple(GraphicsX g, int x, int y, int w, int h, ByteObject trig) {
      int color = trig.getValue(IBOFigure.FIG__OFFSET_06_COLOR4, 4);
      int type = trig.getValue(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, 2);
      g.setColor(color);
      drawTriangleSwitch(g, x, y, w, h, type);
   }

   private void drawTriangleSimpleAngleGrad(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      //	 int color = p.getValue(DrwParam.FIG_OFFSET_4COLOR4, 4);
      //	 int a = p.getValue(DrwParam.FIG_TRIANGLE_OFFSET_2ANGLE2, 2);
      //	 ByteObject grad = p.getSub(DrwParam.TYPE_GRADIENT);
      //	 if (a == C.ANGLE_UP) {
      //	    drawTriangleSimpleGradient(g, x + w / 2, y, a, w / 2, h, color, grad);
      //	 } else if (a == C.ANGLE_DOWN) {
      //	    drawTriangleSimpleGradient(g, x + w / 2, y + h, a, w / 2, h, color, grad);
      //	 } else if (a == C.ANGLE_LEFT) {
      //	    drawTriangleSimpleGradient(g, x, y + h / 2, a, h / 2, w, color, grad);
      //	 } else if (a == C.ANGLE_RIGHT) {
      //	    drawTriangleSimpleGradient(g, x + w, y + h / 2, a, h / 2, w, color, grad);
      //	 }
   }

   private void drawTriangleSwitch(GraphicsX g, int x, int y, int w, int h, int type) {
      if (w < 0)
         w = 0;
      if (h < 0)
         h = 0;
      int w2 = w / 2;
      int h2 = h / 2;
      switch (type) {
         case C.TYPE_00_TOP:
            g.fillTriangle(x, y + h, x + w2, y, x + w, y + h);
            break;
         case C.TYPE_01_BOTTOM:
            g.fillTriangle(x, y, x + w2, y + h, x + w, y);
            break;
         case C.TYPE_02_LEFT:
            g.fillTriangle(x + w, y, x, y + h2, x + w, y + h);
            break;
         case C.TYPE_03_RIGHT:
            g.fillTriangle(x, y, x + w, y + h2, x, y + h);
            break;
         case C.TYPE_04_TopLeft:
            g.fillTriangle(x, y, x + w, y, x, y + h);
            break;
         case C.TYPE_05_TopRight:
            g.fillTriangle(x, y, x + w, y, x + w, y + h);
            break;
         case C.TYPE_06_BotLeft:
            g.fillTriangle(x, y, x + w + 1, y + h, x, y + h);
            break;
         case C.TYPE_07_BotRight:
            g.fillTriangle(x + w, y - 1, x + w, y + h, x - 1, y + h);
            break;
         case C.TYPE_08_MID_TopLeft:
            g.fillTriangle(x, y, x + w, y + h2, x + w2, y + h);
            break;
         case C.TYPE_09_MID_TopRight:
            g.fillTriangle(x + w, y, x + w2, y + h, x, y + h2);
            break;
         case C.TYPE_10_MID_BotLeft:
            g.fillTriangle(x, y + h, x + w2, y, x + w, y + h2);
            break;
         case C.TYPE_11_MID_BotRight:
            g.fillTriangle(x + w, y + h, x, y + h2, x + w2, y);
            break;
         case C.TYPE_12_TopLeftDiagBot:
            g.fillTriangle(x, y, x + w, y + h, x + w2, y + h);
            break;
         case C.TYPE_13_TopLeftDiagRight:
            g.fillTriangle(x, y, x + w, y + h, x + w, y + h2);
            break;
         case C.TYPE_14_TopRightDiagBot:
            g.fillTriangle(x + w, y, x, y + h, x + w2, y + h);
            break;
         case C.TYPE_15_TopRightDiagLeft:
            g.fillTriangle(x + w, y, x, y + h, x, y + h2);
            break;
         case C.TYPE_16_BotLeftDiagTop:
            g.fillTriangle(x, y + h, x + w, y, x + w2, y);
            break;
         case C.TYPE_17_BotLeftDiagRight:
            g.fillTriangle(x, y + h, x + w, y, x + w, y + h2);
            break;
         case C.TYPE_18_BotRightDiagTop:
            g.fillTriangle(x + w, y + h, x, y, x + w2, y);
            break;
         case C.TYPE_19_BotRightDiagLeft:
            g.fillTriangle(x + w, y + h, x, y, x, y + h2);
            break;

         default:
            break;
      }
   }

   public int get_0_200Ratio(int ratio, int coord, int size, int middle) {
      if (ratio == 0) {
         return coord;
      } else if (ratio == 100) {
         return coord + middle;
      } else if (ratio == 200) {
         return coord + size;
      } else {
         float diff = coord - 100;
         float rate = 100f / diff;
         if (diff < 0) {
            float res = (float) middle * rate;
            return coord + (int) res;
         } else {
            float res = (float) middle * rate;
            return coord + middle + (int) res;
         }
      }
   }

   private int getLosangeGradSize(ByteObject grad, int type, int w, int h) {
      int sizeType = grad.get1(IBOGradient.GRADIENT_OFFSET_13_GRADSIZE_TYPE1);
      if (sizeType == ITechGradient.GRADSIZE_TYPE_00_DEFAULT) {
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_LOSANGE_0_SQUARE:
               return Math.max(w, h) / 2;
            case ITechGradient.GRADIENT_TYPE_LOSANGE_01_SQUARE:
               return Math.min(w, h) / 2;
            case ITechGradient.GRADIENT_TYPE_LOSANGE_02_SQUARE:
               return Math.max(w, h) / 4;
            case ITechGradient.GRADIENT_TYPE_LOSANGE_03_SQUARE:
               return Math.min(w, h);
            case ITechGradient.GRADIENT_TYPE_LOSANGE_1_FULLVERTICAL:
               return h / 2;
            case ITechGradient.GRADIENT_TYPE_LOSANGE_2_FULLHORIZ:
               return w / 2;
            case ITechGradient.GRADIENT_TYPE_LOSANGE_3_FULLDIAGDOWN:
            case ITechGradient.GRADIENT_TYPE_LOSANGE_4_FULLDIAGUP:
            case ITechGradient.GRADIENT_TYPE_LOSANGE_7_LEFT:
            case ITechGradient.GRADIENT_TYPE_LOSANGE_8_RIGHT:
            case ITechGradient.GRADIENT_TYPE_LOSANGE_5_TOP:
            case ITechGradient.GRADIENT_TYPE_LOSANGE_6_BOT:
               return Math.min(w, h) / 2;
            default:
               break;
         }
         return Math.min(w, h);
      } else {
         return drc.getBOC().getGradientOperator().getGradSize(sizeType, w, h, grad);
      }
   }

   /**
    * 
    * @param grad
    * @param type
    * @param w
    * @param h
    * @param trigType
    * @return
    */
   private int getTrigGradSize(ByteObject grad, int type, int w, int h, int trigType) {
      int size = 0;
      if (grad == null) {
         size = 1;
      } else {
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_TRIG_00_TENT:
               if (trigType == C.TYPE_00_TOP || trigType == C.TYPE_01_BOTTOM)
                  size = h;
               else
                  size = w;
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_04_FULL:
               size = Math.min(w / 2, h / 2);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_08_NORMAL:
               size = h;
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_10_SWIPE:
               if (trigType == C.TYPE_00_TOP || trigType == C.TYPE_01_BOTTOM)
                  size = w;
               else
                  size = h;
               break;
            default:
               size = w / 2;
               if (h / 2 < w / 2) {
                  size = h / 2;
               }
         }
      }
      return size;
   }

   private int getTrigGradSize_4_7(ByteObject grad, int type, int w, int h, int trigType) {
      int size = 0;
      if (grad == null) {
         size = 1;
      } else {
         switch (type) {
            default:
               size = Math.min(w, h);
         }
      }
      return size;
   }

}
