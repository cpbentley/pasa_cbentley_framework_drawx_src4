package pasa.cbentley.framework.drawx.src4.factories.drawer;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.core.src4.utils.MathUtils;
import pasa.cbentley.framework.drawx.src4.color.ColorIterator;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;
import pasa.cbentley.framework.drawx.src4.utils.DrawUtilz;
import pasa.cbentley.layouter.src4.tech.ITechLayout;

public class DrawerTriangle {

   private DrwCtx drc;

   public DrawerTriangle(DrwCtx drc) {
      this.drc = drc;
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
               if (trigType == C.TYPE_00TOP || trigType == C.TYPE_01BOTTOM)
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
               if (trigType == C.TYPE_00TOP || trigType == C.TYPE_01BOTTOM)
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

   /**
    * Gradient routines for corner triangles.
    * 
    * <li> {@link C#TYPE_04TopLeft}
    * <li> {@link C#TYPE_05TopRight}
    * <li> {@link C#TYPE_06BotLeft}
    * <li> {@link C#TYPE_07BotRight}
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
      int color = fig.getValue(ITechFigure.FIG__OFFSET_06_COLOR4, 4);
      ByteObject grad = fig.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
      int type = grad.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      int trigType = fig.get2(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2);
      int gradSize = getTrigGradSize_4_7(grad, type, w, h, trigType);
      double alpha = MathUtils.aTan((double) w / 2, (double) h / 2);
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      while ((count = ci.iteratePixelCount(g)) != -1) {
         //SystemLog.printDraw(i + " dir="+dir + " type="+type + " color="+debugColor(g.getColor()));
         int count2 = count * 2;
         int d = (int) (Math.tan(alpha) * (double) (count));
         int d2 = d / 2;
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_TRIG_00_TENT:
               //inverse normal top
               if (trigType == C.TYPE_04TopLeft)
                  drawTriangleSwitch(g, x, y, w - count, h - count, trigType);
               else if (trigType == C.TYPE_05TopRight) {
                  drawTriangleSwitch(g, x + count, y, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_06BotLeft) {
                  drawTriangleSwitch(g, x, y + count, w - count, h - count, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_01_TENT_JESUS:
               if (trigType == C.TYPE_04TopLeft)
                  drawTriangleSwitch(g, x, y, w, h - count, trigType);
               else if (trigType == C.TYPE_05TopRight) {
                  drawTriangleSwitch(g, x, y, w, h - count, trigType);
               } else if (trigType == C.TYPE_06BotLeft) {
                  drawTriangleSwitch(g, x, y + count, w, h - count, trigType);
               } else {
                  drawTriangleSwitch(g, x, y + count, w, h - count, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_02_TOP_JESUS:
               //inverse normal top
               if (trigType == C.TYPE_04TopLeft)
                  drawTriangleSwitch(g, x, y, w - count, h, trigType);
               else if (trigType == C.TYPE_05TopRight) {
                  drawTriangleSwitch(g, x + count, y, w - count, h, trigType);
               } else if (trigType == C.TYPE_06BotLeft) {
                  drawTriangleSwitch(g, x, y, w - count, h, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y, w - count, h, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_03_TUNNEL:
               //inverse normal top
               if (trigType == C.TYPE_04TopLeft)
                  drawTriangleSwitch(g, x, y + count, w - count, h - count, trigType);
               else if (trigType == C.TYPE_05TopRight) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_06BotLeft) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - 2 * count, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + 2 * count, w - count, h - count, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_04_FULL:
               //inverse normal top
               if (trigType == C.TYPE_04TopLeft)
                  drawTriangleSwitch(g, x + count, y, w - count, h - count, trigType);
               else if (trigType == C.TYPE_05TopRight) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_06BotLeft) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - 2 * count, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + 2 * count, w - count, h - count, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_10_SWIPE:
               //in this case each count is the number of degrees from base angle
               if (trigType == C.TYPE_04TopLeft) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_05TopRight) {
                  drawTriangleSwitch(g, x + count, y + count, w - count, h - count, trigType);
               } else if (trigType == C.TYPE_06BotLeft) {
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
    * <li> {@link C#TYPE_08MID_TopLeft}
    * <li> {@link C#TYPE_09MID_TopRight}
    * <li> {@link C#TYPE_10MID_BotLeft}
    * <li> {@link C#TYPE_11MID_BotRight}
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
      int color = fig.getValue(ITechFigure.FIG__OFFSET_06_COLOR4, 4);
      ByteObject grad = fig.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
      int type = grad.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      int trigType = fig.get2(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2);
      int gradSize = getTrigGradSize(grad, type, w, h, trigType);
      double alpha = MathUtils.aTan((double) w / 2, (double) h / 2);
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
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
      int color = fig.getValue(ITechFigure.FIG__OFFSET_06_COLOR4, 4);
      ByteObject grad = fig.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
      int type = grad.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      int trigType = fig.get2(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2);
      boolean isWire = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_09_FLAGX1, ITechGradient.GRADIENT_FLAGX_5_WIRE);
      int gradSize = getTrigGradSize(grad, type, w, h, trigType);
      double alpha = MathUtils.aTan((double) w / 2, (double) h / 2);
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      int previousC = 0;
      int dx = x;
      int dy = y;
      while ((count = ci.iteratePixelCount(g)) != -1) {
         //SystemLog.printDraw(i + " dir="+dir + " type="+type + " color="+debugColor(g.getColor()));
         int count2 = count * 2;
         int d = (int) (Math.tan(alpha) * (double) (count));
         int d2 = d / 2;
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_TRIG_00_TENT:
               //inverse normal top
               if (trigType == C.TYPE_00TOP)
                  drawTriangleSwitch(g, x + d2, y + count, w - d, h - count, trigType);
               else if (trigType == C.TYPE_01BOTTOM) {
                  drawTriangleSwitch(g, x + d2, y, w - d, h - count, trigType);
               } else if (trigType == C.TYPE_02LEFT) {
                  drawTriangleSwitch(g, x + count, y + d2, w - count, h - d, trigType);
               } else {
                  drawTriangleSwitch(g, x, y + d2, w - count, h - d, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_01_TENT_JESUS:
               if (trigType == C.TYPE_00TOP)
                  drawTriangleSwitch(g, x + d, y + count, w - (2 * d), h - count, trigType);
               else if (trigType == C.TYPE_01BOTTOM)
                  drawTriangleSwitch(g, x + d, y, w - (2 * d), h - count, trigType);
               else if (trigType == C.TYPE_02LEFT)
                  drawTriangleSwitch(g, x + count, y + d, w - count, h - (2 * d), trigType);
               else
                  drawTriangleSwitch(g, x, y + d, w - count, h - (2 * d), trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_02_TOP_JESUS:
               if (trigType == C.TYPE_00TOP || trigType == C.TYPE_01BOTTOM)
                  drawTriangleSwitch(g, x + count, y, w - count2, h, trigType);
               else
                  drawTriangleSwitch(g, x, y + count, w, h - count2, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_03_TUNNEL:
               if (trigType == C.TYPE_00TOP)
                  drawTriangleSwitch(g, x + d, y, w - (2 * d), h - count, trigType);
               else if (trigType == C.TYPE_01BOTTOM)
                  drawTriangleSwitch(g, x + d, y + count, w - (2 * d), h - count, trigType);
               else if (trigType == C.TYPE_02LEFT)
                  drawTriangleSwitch(g, x, y + d, w - count, h - (2 * d), trigType);
               else
                  drawTriangleSwitch(g, x + count, y + d, w - count, h - (2 * d), trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_04_FULL:
               if (w > h) {
                  int hcount = count2 + count;
                  int xc = hcount / 2;
                  drawTriangleSwitch(g, x + xc, y + count, w - hcount, h - count2, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + count, w - count2, h - count2, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_08_NORMAL:
               if (trigType == C.TYPE_00TOP)
                  drawTriangleSwitch(g, x + d / 2, y, w - d, h - count, trigType);
               else if (trigType == C.TYPE_01BOTTOM) {
                  drawTriangleSwitch(g, x + d / 2, y + count, w - d, h - count, trigType);
               } else if (trigType == C.TYPE_02LEFT) {
                  drawTriangleSwitch(g, x, y + d2, w - count, h - d, trigType);
               } else {
                  drawTriangleSwitch(g, x + count, y + d2, w - count, h - d, trigType);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_05_OPAQUEBASE:
               drawTriangleSwitch(g, x, y + count, w, h - count, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_06_OPAQUE_CENTER:
               drawTriangleSwitch(g, x + d2, y + d2, w - d, h - d, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_07_ARROW:
               drawTriangleSwitch(g, x + (w / 2) - d, y + count, 2 * d, count, trigType);
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_09_HALO:
               break;
            case ITechGradient.GRADIENT_TYPE_TRIG_10_SWIPE:
               if (trigType == C.TYPE_00TOP) {
                  if (isWire) {
                     //TODO stroke style
                     g.drawLine(x + count, y + h, x + w / 2, y);
                  } else {
                     g.fillTriangle(x + count, y + h, x + previousC, y + h, x + w / 2, y);
                  }
               } else if (trigType == C.TYPE_01BOTTOM) {
                  g.fillTriangle(dx, y, dx + count - previousC, y, x + w / 2, y + h);
               } else if (trigType == C.TYPE_02LEFT) {
                  g.fillTriangle(x + w, dy, x + w, dy + count - previousC, x, y + h / 2);
               } else {
               }
               break;
            default:
               break;
         }
         previousC = count;
      }
   }

   public void drawTriangleAnchors(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      ByteObject grad = p.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
      int angle = p.get2(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2);
      int color = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      if (g.hasGradient() && grad != null) {
         int gradSize = Math.min(w / 2, h / 2);
         int count = 0;
         ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
         while ((count = ci.iteratePixelCount(g)) != -1) {
            drawTriangleAnchors(g, x, y, w, h, angle);
            x++;
            y++;
            w -= 2;
            h -= 2;
         }
      } else {
         g.setColor(color);
         drawTriangleAnchors(g, x, y, w, h, angle);
      }

   }

   public void drawTriangleAnchorsGradient(GraphicsX g, int x, int y, int w, int h, int color, int angle, ByteObject grad) {

   }

   public void drawTriangleAnchors(GraphicsX g, int x, int y, int w, int h, int angle) {
      int ha1 = (angle >> 0) & 0x3;
      int va1 = (angle >> 2) & 0x3;
      int ha2 = (angle >> 4) & 0x3;
      int va2 = (angle >> 6) & 0x3;
      int ha3 = (angle >> 8) & 0x3;
      int va3 = (angle >> 10) & 0x3;
      int dx1 = (ha1 == ITechFigure.ALIGN_2BITS_0CENTER) ? x + w / 2 : (ha1 == ITechFigure.ALIGN_2BITS_1LEFT) ? x : x + w;
      int dy1 = (va1 == ITechFigure.ALIGN_2BITS_0CENTER) ? y + h / 2 : (va1 == ITechFigure.ALIGN_2BITS_1LEFT) ? y : y + h;
      int dx2 = (ha2 == ITechFigure.ALIGN_2BITS_0CENTER) ? x + w / 2 : (ha2 == ITechFigure.ALIGN_2BITS_1LEFT) ? x : x + w;
      int dy2 = (va2 == ITechFigure.ALIGN_2BITS_0CENTER) ? y + h / 2 : (va2 == ITechFigure.ALIGN_2BITS_1LEFT) ? y : y + h;
      int dx3 = (ha3 == ITechFigure.ALIGN_2BITS_0CENTER) ? x + w / 2 : (ha3 == ITechFigure.ALIGN_2BITS_1LEFT) ? x : x + w;
      int dy3 = (va3 == ITechFigure.ALIGN_2BITS_0CENTER) ? y + h / 2 : (va3 == ITechFigure.ALIGN_2BITS_1LEFT) ? y : y + h;
      g.fillTriangle(dx1, dy1, dx2, dy2, dx3, dy3);
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
      DrawUtilz.setAlphaToColorRGB(rgb, -1, 0);
      int maskColor = ColorUtils.FULLY_TRANSPARENT_WHITE;
      DrawUtilz.setAlphaGradient(rgb, img.getWidth(), img.getHeight(), maskColor, tsize, borderOnBg);
      if (dir == C.ANGLE_UP_90)
         g.drawRGB(rgb, 0, w, x - base, y, w, h, true);
      else if (dir == C.ANGLE_DOWN_270)
         g.drawRGB(rgb, 0, w, x - base, y - H, w, h, true);
      else if (dir == C.ANGLE_LEFT_180)
         g.drawRGB(rgb, 0, w, x, y - base, w, h, true);
      else if (dir == C.ANGLE_RIGHT_0)
         g.drawRGB(rgb, 0, w, x - H, y - base, w, h, true);

   }

   public void drawTransIsoTriangle(GraphicsX g, int dir, int color, int x, int y, int base, int H, int tsize) {
      drawTransIsoTriangle(g, dir, color, x, y, base, H, tsize, ColorUtils.FULLY_TRANSPARENT_WHITE, true);
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
      int type = p.get1(ITechFigure.FIG_LOSANGE_OFFSET_4TYPE1);
      if (type == 0) {
         ByteObject grad = p.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);

         int color = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
         //when fill is different than zero, draw on rgbimage then mask
         int fill = p.get2(ITechFigure.FIG_LOSANGE_OFFSET_3FILL2);
         int overStep = p.get2(ITechFigure.FIG_LOSANGE_OFFSET_2OVERSTEP2);

         if (grad == null) {
            g.setColor(color);
            drawShapeLosange(g, x, y, w, h, p, overStep);
         } else {
            int stepNum = getLosangeGradSize(grad, type, w, h);
            int val = 0;
            ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, stepNum);
            int count = 0;
            while ((count = ci.iteratePixelCount(g)) != -1) {
               int c2 = count << 1;
               drawShapeLosange(g, x + count, y + count, w - c2, h - c2, p, overStep);
            }
         }
      } else if (type == 1) {
         ByteObject trig = p.getSubFirst(IBOTypesDrw.TYPE_050_FIGURE);
         if (p.hasFlag(ITechFigure.FIG_LOSANGE_OFFSET_1FLAG, ITechFigure.FIG_LOSANGE_FLAG_1HORIZ)) {
            int w1 = w / 2;
            int w2 = w - w1;
            trig.setValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, C.TYPE_02LEFT, 2);
            drawFigTriangle(g, x, y, w1, h, trig);
            trig.setValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, C.TYPE_03RIGHT, 2);
            drawFigTriangle(g, x + w1, y, w2, h, trig);
         } else {
            int h1 = h / 2;
            int h2 = h - h1;
            trig.setValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, C.TYPE_00TOP, 2);
            drawFigTriangle(g, x, y + h1, w, h1, trig);
            trig.setValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, C.TYPE_01BOTTOM, 2);
            drawFigTriangle(g, x + h1, y, w, h2, trig);
         }
      } else if (type == 2) {
         ByteObject trig1 = p.getSubOrder(IBOTypesDrw.TYPE_050_FIGURE, 0);
         ByteObject trig2 = p.getSubOrder(IBOTypesDrw.TYPE_050_FIGURE, 1);

      } else if (type == 3) {
         ByteObject trig = p.getSubFirst(IBOTypesDrw.TYPE_050_FIGURE);
         int angle = trig.get2(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2);
         int h1 = h / 2;
         int h2 = h - h1;
         int w1 = w / 2;
         int w2 = w - w1;
         switch (angle) {
            case C.TYPE_00TOP:
               drawFigTriangle(g, x, y + h1, w, h1, trig);
               trig.setValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, C.TYPE_01BOTTOM, 2);
               drawFigTriangle(g, x + h1, y, w, h2, trig);
               break;
            case C.TYPE_02LEFT:
               drawFigTriangle(g, x, y, w1, h, trig);
               trig.setValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, C.TYPE_03RIGHT, 2);
               drawFigTriangle(g, x + w1, y, w2, h, trig);
               break;
            case C.TYPE_11MID_BotRight:
               drawFigTriangle(g, x, y, w, h1, trig);
               trig.setValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, C.TYPE_08MID_TopLeft, 2);
               drawFigTriangle(g, x + h1, y, w, h2, trig);
               break;

            default:
               break;
         }
         trig.setValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, angle, 2);
      }
   }

   private int getLosangeGradSize(ByteObject grad, int type, int w, int h) {
      return Math.min(w, h);
   }

   private void drawShapeLosange(GraphicsX g, int x, int y, int w, int h, ByteObject p, int ostep) {
      int px = w / 2;
      int py = h / 2;

      if (p.hasFlag(ITechFigure.FIG_LOSANGE_OFFSET_1FLAG, ITechFigure.FIG_LOSANGE_FLAG_1HORIZ)) {
         if (p.hasFlag(ITechFigure.FIG_LOSANGE_OFFSET_1FLAG, ITechFigure.FIG_LOSANGE_FLAG_4NOED_PAPILLION)) {
            g.fillTriangle(x, y, x + px + ostep, y + py, x, y + h);
            g.fillTriangle(x + w, y, x + px - ostep, y + py, x + w, y + h);
         } else {
            if (p.hasFlag(ITechFigure.FIG_LOSANGE_OFFSET_1FLAG, ITechFigure.FIG_LOSANGE_FLAG_3CONTOUR)) {
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
         if (p.hasFlag(ITechFigure.FIG_LOSANGE_OFFSET_1FLAG, ITechFigure.FIG_LOSANGE_FLAG_4NOED_PAPILLION)) {
            g.fillTriangle(x, y, x + px, y + py + ostep, x + w, y);
            g.fillTriangle(x, y + h, x + px, y + py - ostep, x + w, y + h);
         } else {
            if (p.hasFlag(ITechFigure.FIG_LOSANGE_OFFSET_1FLAG, ITechFigure.FIG_LOSANGE_FLAG_3CONTOUR)) {
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
      int color = p.getValue(ITechFigure.FIG__OFFSET_06_COLOR4, 4);
      double angle = p.getValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, 2);
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
      int littleH = drc.getSizer().codedSizeDecode(p, ITechFigure.FIG_TRIANGLE_OFFSET_3h4, w, h, ITechLayout.CTX_2_HEIGHT);

      drawTriangleIso(g, (int) angle - 90, color, dx, dy, base, h * 2, littleH);
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

   public void drawTriangleDirectional(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      if (g.hasGradient() && p.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_2GRADIENT)) {
         int trigType = p.get2(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2);
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
    * Gradient routines for corner triangles.
    * 
    * <li> {@link C#TYPE_12TopLeftDiagBot}
    * <li> {@link C#TYPE_13TopLeftDiagRight}
    * <li> {@link C#TYPE_14TopRightDiagBot}
    * <li> {@link C#TYPE_15TopRightDiagLeft}
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
      int color = trig.getValue(ITechFigure.FIG__OFFSET_06_COLOR4, 4);
      int type = trig.getValue(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, 2);
      g.setColor(color);
      drawTriangleSwitch(g, x, y, w, h, type);
   }

   private void drawTriangleSwitch(GraphicsX g, int x, int y, int w, int h, int type) {
      if (w < 0)
         w = 0;
      if (h < 0)
         h = 0;
      int w2 = w / 2;
      int h2 = h / 2;
      switch (type) {
         case C.TYPE_00TOP:
            g.fillTriangle(x, y + h, x + w2, y, x + w, y + h);
            break;
         case C.TYPE_01BOTTOM:
            g.fillTriangle(x, y, x + w2, y + h, x + w, y);
            break;
         case C.TYPE_02LEFT:
            g.fillTriangle(x + w, y, x, y + h2, x + w, y + h);
            break;
         case C.TYPE_03RIGHT:
            g.fillTriangle(x, y, x + w, y + h2, x, y + h);
            break;
         case C.TYPE_04TopLeft:
            g.fillTriangle(x, y, x + w, y, x, y + h);
            break;
         case C.TYPE_05TopRight:
            g.fillTriangle(x, y, x + w, y, x + w, y + h);
            break;
         case C.TYPE_06BotLeft:
            g.fillTriangle(x, y, x + w + 1, y + h, x, y + h);
            break;
         case C.TYPE_07BotRight:
            g.fillTriangle(x + w, y - 1, x + w, y + h, x - 1, y + h);
            break;
         case C.TYPE_08MID_TopLeft:
            g.fillTriangle(x, y, x + w, y + h2, x + w2, y + h);
            break;
         case C.TYPE_09MID_TopRight:
            g.fillTriangle(x + w, y, x + w2, y + h, x, y + h2);
            break;
         case C.TYPE_10MID_BotLeft:
            g.fillTriangle(x, y + h, x + w2, y, x + w, y + h2);
            break;
         case C.TYPE_11MID_BotRight:
            g.fillTriangle(x + w, y + h, x, y + h2, x + w2, y);
            break;
         case C.TYPE_12TopLeftDiagBot:
            g.fillTriangle(x, y, x + w, y + h, x + w2, y + h);
            break;
         case C.TYPE_13TopLeftDiagRight:
            g.fillTriangle(x, y, x + w, y + h, x + w, y + h2);
            break;
         case C.TYPE_14TopRightDiagBot:
            g.fillTriangle(x + w, y, x, y + h, x + w2, y + h);
            break;
         case C.TYPE_15TopRightDiagLeft:
            g.fillTriangle(x + w, y, x, y + h, x, y + h2);
            break;
         case C.TYPE_16BotLeftDiagTop:
            g.fillTriangle(x, y + h, x + w, y, x + w2, y);
            break;
         case C.TYPE_17BotLeftDiagRight:
            g.fillTriangle(x, y + h, x + w, y, x + w, y + h2);
            break;
         case C.TYPE_18BotRightDiagTop:
            g.fillTriangle(x + w, y + h, x, y, x + w2, y);
            break;
         case C.TYPE_19BotRightDiagLeft:
            g.fillTriangle(x + w, y + h, x, y, x, y + h2);
            break;

         default:
            break;
      }
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
      if (p.hasFlag(ITechFigure.FIG_TRIANGLE_OFFSET_1FLAG1, ITechFigure.FIG_TRIANGLE_FLAG_2ANGLE)) {
         drawTriangleComplexAngle(g, x, y, w, h, p);
      } else {
         if (p.hasFlag(ITechFigure.FIG_TRIANGLE_OFFSET_1FLAG1, ITechFigure.FIG_TRIANGLE_FLAG_3ANCHOR_POINTS)) {
            drawTriangleAnchors(g, x, y, w, h, p);
         } else {
            drawTriangleDirectional(g, x, y, w, h, p);
         }
      }
   }

}
