/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import java.util.Random;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.objects.color.BlendOp;
import pasa.cbentley.byteobjects.src4.objects.color.ColorIterator;
import pasa.cbentley.byteobjects.src4.objects.color.GradientOperator;
import pasa.cbentley.byteobjects.src4.objects.color.IBOFilter;
import pasa.cbentley.byteobjects.src4.objects.color.IBOGradient;
import pasa.cbentley.byteobjects.src4.objects.color.ITechGradient;
import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMerge;
import pasa.cbentley.byteobjects.src4.objects.pointer.MergeFactory;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.drawx.src4.ctx.BOModuleDrawx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.factories.drawer.DrawerString;
import pasa.cbentley.framework.drawx.src4.factories.drawer.DrawerTriangle;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOArtifact;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOBox;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigArlequin;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigBorder;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigCardsCPCTrefle;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigCross;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigEllipse;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigGrid;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigLine;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigPixels;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigRectangle;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigRepeater;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigSuperLines;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigTesson;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigTriangle;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.ITechArtifact;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeFigureRect;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeFigureString;
import pasa.cbentley.framework.drawx.src4.tech.ITechRgbImage;
import pasa.cbentley.framework.drawx.src4.utils.AnchorUtils;
import pasa.cbentley.layouter.src4.ctx.IBOTypesLayout;
import pasa.cbentley.layouter.src4.ctx.LayouterCtx;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;
import pasa.cbentley.layouter.src4.tech.IBOTblr;
import pasa.cbentley.layouter.src4.tech.ITechLayout;

/**
 * Lambda expressions for figures.
 * <br>
 * {@link ByteObject} declaratively configures the drawing of the figures.
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class FigureOperator extends AbstractDrwOperator implements ITechMergeFigureString, ITechMergeFigure, ITechFigure, IBOBox, IBOTblr, IBOFigBorder, IBOFigure, IBOFigRectangle, IBOFigString {

   protected DrawerTriangle drawerTriangle;

   protected DrawerString   stringDrawer;

   public FigureOperator(DrwCtx drc) {
      super(drc);
      drawerTriangle = new DrawerTriangle(drc);
      stringDrawer = new DrawerString(drc);
   }

   public void addArtifact(ByteObject fig, ByteObject artifac) {
      if (fig == null || artifac == null)
         return;
      fig.checkType(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
      artifac.checkType(IBOTypesDrawX.TYPE_DRWX_02_FIG_ARTIFACT);
      fig.addSub(artifac);
      fig.setFlag(IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_8_ARTIFACT, true);
   }

   public void addFilter(ByteObject figure, ByteObject filter) {
      if (filter != null) {
         figure.addSub(filter);
         figure.setFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_5_FILTER, true);
      }
   }

   /**
    * Takes the figure and clone with with the direction provided
    * 
    * <li> {@link C#DIR_0_TOP}
    * <li> {@link C#DIR_1_BOTTOM}
    * <li> {@link C#DIR_2_LEFT}
    * <li> {@link C#DIR_3_RIGHT}
    * <li> {@link C#DIR_4_TopLeft}
    * <li> {@link C#DIR_5_TopRight}
    * <li> {@link C#DIR_6_BotLeft}
    * <li> {@link C#DIR_7_BotRight}
    * @param fig
    * @param dir
    * @return
    */
   public ByteObject cloneFigDirectionanl(ByteObject fig, int dir) {
      int type = fig.get1(FIG__OFFSET_01_TYPE1);
      ByteObject clone = fig.cloneCopyHeadRefParams();
      switch (type) {
         case ITechFigure.FIG_TYPE_03_TRIANGLE:
            clone.set1(IBOFigTriangle.FIG_TRIANGLE_OFFSET_02_TYPE1, ITechFigure.FIG_TRIANGLE_TYPE_1_DIRECTIONAL);
            clone.set2(IBOFigTriangle.FIG_TRIANGLE_OFFSET_03_ANGLE2, dir);
            clone.setValue4Bits1(FIG__OFFSET_05_DIR1, dir);
            break;
         case ITechFigure.FIG_TYPE_01_RECTANGLE:

         default:
            break;
      }
      return clone;
   }

   int decodeHeight(int index, ByteObject p, int w, int h) {
      int v = p.get4(index);
      v = drc.getLAC().getLayoutOperator().decode(v, w, h, ITechLayout.CTX_2_HEIGHT);
      return v;
   }

   int decodeWidth(int index, ByteObject p, int w, int h) {
      int v = p.get4(index);
      v = drc.getLAC().getLayoutOperator().decode(v, w, h, ITechLayout.CTX_1_WIDTH);
      return v;
   }

   public void drawArc(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      ByteObject grad = p.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      int color = p.get4(FIG__OFFSET_06_COLOR4);
      int angle = 0;
      g.setColor(color);
   }

   public void drawArrow(GraphicsX g, int dir, int x, int y, int base, int H, int h, int length, int thick, int headColor, int bodyColor, ByteObject transFunct) {
      if (transFunct != null) {
         if (transFunct.getType() != IBOTypesBOC.TYPE_040_COLOR_FILTER)
            throw new IllegalArgumentException();

      }
   }

   public void drawArrow(GraphicsX g, int[] p) {

   }

   public void drawArrowLeft(GraphicsX g, int[] p) {

   }

   public void drawArrowTriangle(GraphicsX g, int[] p) {

   }

   /**
    * Called after the gradient step has been drawn.
    * 
    * @param g
    * @param color
    * @param x 
    * @param y y baseline
    * @param width
    * @param height
    * @param art
    * @param r
    */
   public void drawArtifactRectHoriz(GraphicsX g, int color, int x, int y, int width, int height, ByteObject art, int arcw, int arch) {
      boolean isRndSpace = art.hasFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_3_RANDOM_SPACING);
      boolean isRndW = art.hasFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_1_RANDOM_W);
      boolean isRndH = art.hasFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_2_RANDOM_H);
      boolean isRndFig = art.hasFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_4_RANDOM_FIG);
      int type = art.get1(IBOArtifact.ARTIFACT_OFFSET_05_TYPE1);

      int artW = art.get1(IBOArtifact.ARTIFACT_OFFSET_02_W1);
      int artH = art.get1(IBOArtifact.ARTIFACT_OFFSET_03_H1);
      int artFig = art.get1(IBOArtifact.ARTIFACT_OFFSET_06_FIG1);
      int aspace = art.get1(IBOArtifact.ARTIFACT_OFFSET_04_SPACING_CAP1);
      if (aspace <= 0)
         aspace = 1;
      g.setColor(color);
      int dw = artW;
      int dh = artH;
      int dFig = artFig;
      int dSpace = aspace;
      int dx = x;
      int xCount = arcw;
      int yCount = arch;
      Random r = drc.getRandom();
      long seed = art.get4(IBOArtifact.ARTIFACT_OFFSET_07_SEED4);
      if (seed != 0) {
         r = drc.getRandom(seed);
      }
      int endX = x + width;
     
      switch (type) {
         case ITechArtifact.ART_TYPE_RECT_0_TRIG_BORDER:
            g.fillTriangle(x, y, x, y - artH, x + artW, y);
            g.fillTriangle(x + width, y, x + width, y - artH, x + width - artW, y);
            break;
         case ITechArtifact.ART_TYPE_RECT_1_TRIG_BORDER:
            while (dx < endX) {
               if (isRndW) {
                  dw = r.nextInt(artW) + 1;
               }
               if (isRndH) {
                  dh = r.nextInt(artH) + 1;
               }
               if (isRndFig) {
                  dFig = r.nextInt(artFig);
               }
               if (isRndSpace) {
                  dSpace = r.nextInt(aspace) + 1;
               }
               if (dFig == 0) {
                  g.fillRect(dx, y - dh, dw, dh);
                  dx += dw;
               } else {
                  g.fillTriangle(x + xCount, y, x + dw, y - dh, x + 2 * dw, y);
               }
               dx += dSpace;
            }
            break;
         case ITechArtifact.ART_TYPE_RECT_2_TRIG_BORDER:
            while (xCount + (2 * dw) < width) {
               if (isRndW) {
                  dw = r.nextInt(artW) + 1;
               }
               if (isRndH) {
                  dh = r.nextInt(artH) + 1;
               }
               g.fillTriangle(x + xCount, y, x + xCount + dw, y - dh, x + xCount + 2 * dw, y);
               //at least
               xCount += 2 * dw;
               if (isRndSpace) {
                  xCount += r.nextInt(aspace) + 1;
               } else {
                  xCount += aspace;
               }
            }

         default:
            break;
      }

   }

   public void drawBorder(GraphicsX g, int x, int y, int w, int h, int size, int color) {
      drawBorder(g, x, y, w, h, size, size, size, size, color, color, color, color);
   }

   /**
    * Draw a border full inside the dimension
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param leftSize
    * @param topSize
    * @param rightSize
    * @param bottomSize
    * @param leftColor
    * @param topColor
    * @param rightColor
    * @param bottomColor
    */
   public void drawBorder(GraphicsX g, int x, int y, int w, int h, int leftSize, int topSize, int rightSize, int bottomSize, int leftColor, int topColor, int rightColor, int bottomColor) {
      //top Horizontal
      if (topSize != 0) {
         g.setColor(topColor);
         g.fillRect(x, y, w, topSize);
      }
      //left vertical
      if (leftSize != 0) {
         g.setColor(leftColor);
         g.fillRect(x, y, leftSize, h);
      }
      //bottom horizontal
      if (bottomSize != 0) {
         g.setColor(bottomColor);
         g.fillRect(x, y + h - bottomSize, w, bottomSize);
      }
      //right vertical
      if (rightSize != 0) {
         g.setColor(rightColor);
         g.fillRect(x + w - rightSize, y, rightSize, h);
      }
   }

   public void drawCoeur(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int color = p.get4(FIG__OFFSET_06_COLOR4);
      int color2 = p.get4(FIG__OFFSET_06_COLOR4);
      int w2 = w / 2;
      int w3_demi = w - w2;
      int def = h / 3;
      int p1 = 28; // ck.getValue(ClassKey.ROOT_28, 1, h, "Upper Part Size Pixels", 28, false, true, false);
      boolean isHalf = true;
      if (isHalf) {
         p1 = h / 2;
      }
      int p2 = h - p1;

      int rh1 = 2 * p1;
      int flattener = 0; // ck.getValue(ClassKey.ROOT_46, -p1, p1, "Flattener/Reduce UpperPart Size", 0, false, true, false);

      int rayonp = 43; //ck.getValue(ClassKey.ROOT_X, 0, 100, "Rayon", 43, false, true, false);

      int ymod = 2 * flattener;

      rh1 = rh1 - ymod;

      int rh = 2 * p2;
      g.setColor(color);
      boolean isReal = true;
      boolean isTwoColors = true;
      boolean c = true;
      boolean isDrawArc = true;
      boolean isRayonMatch = true;
      if (isReal) {
         // draw a diamond
         int halfH = p1;
         int halfW = p2;
         g.fillTriangle(x, y + halfH, x + w2, y, x + w, y + halfH);
         g.fillTriangle(x, y + halfH, x + w2, y + h, x + w, y + halfH);
         // draw circles in the middle
         int cx = x + w / 4;
         int cy = y + p1 / 2;
         double hyp = halfH * halfH + halfW * halfW;
         int rayon = (int) (Math.sqrt(hyp) / (double) 2);
         if (isRayonMatch) {
            rayon = rayonp;
         }
         int fx = cx - rayon;
         int fy = cy - rayon;
         int fw = 2 * rayon;
         int fh = 2 * rayon;
         if (isTwoColors) {
            g.setColor(color2);
         }
         if (isDrawArc) {
            g.fillArc(fx, fy, fw, fh, 0, 360);
            fx = x + w - w / 4 - rayon;
            g.fillArc(fx, fy, fw, fh, 0, 360);
         }
      } else {
         g.fillArc(x, y + flattener, w2, rh1, 0, 180);
         g.fillArc(x + w2 - 1, y + flattener, w3_demi, rh1, 0, 180);

         if (isTwoColors) {
            int ys = y + p1;
            g.fillTriangle(x, ys, x + w2, y + h, x + w2, ys);
            g.fillTriangle(x + w - 1, ys, x + w2, y + h, x + w2, ys);

         } else {
            g.fillArc(x, y + p1 - p2, w - 1, rh, 180, 90);
            g.fillArc(x, y + p1 - p2, w - 1, rh, 270, 90);
         }
      }
   }

   /**
    * Primitive for fil de fer cross drawing
    * Fill is different. Gradient is even harder
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawCross(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int color = p.get4(FIG__OFFSET_06_COLOR4);
      g.setColor(color);
      ByteObject grad = p.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      if (g.hasGradient() && grad != null) {
         int gradSize = Math.min(w, h);
         ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
         while (ci.iteratePixelCount(g) != -1) {
            drawCrossFill(g, x, y, w, h, p);
            x++;
            y++;
            w -= 2;
            h -= 2;
         }
      } else {
         drawCrossFill(g, x, y, w, h, p);
      }
   }

   /**
    * Draw cross as percentages.
    * <br>
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawCrossAsLine(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int positionVerticalBar = p.get1(IBOFigCross.FIG_CROSS_OFFSET_4XOFFSET1);
      int positionHorizontalBar = p.get1(IBOFigCross.FIG_CROSS_OFFSET_5YOFFSET1);
      int heightHorizontalBar = p.get1(IBOFigCross.FIG_CROSS_OFFSET_2HTHICK1);
      int widthVerticalBar = p.get1(IBOFigCross.FIG_CROSS_OFFSET_3VTHICK1);
      int ch = h * heightHorizontalBar / 100;
      int cw = w * widthVerticalBar / 100;
      int h1 = (h - ch) * positionHorizontalBar / 100;
      int h2 = (h - ch - h1);
      int w1 = (w - cw) * positionVerticalBar / 100;
      int w2 = (w - cw - w1);
      int dx = x + ((w - cw) / 2);
      int dy = y;
      int ex = dx + cw;
      int ey = dy;
      g.drawLine(dx, dy, ex, ey);
      dx = ex;
      dy = ey + h1;
      g.drawLine(ex, ey, dx, dy);
      ey = dy;
      ex = dx + w2;
      g.drawLine(dx, dy, ex, ey);
      dx = ex;
      dy = ey + ch;
      g.drawLine(ex, ey, dx, dy);
      ex = dx - w2;
      ey = dy;
      g.drawLine(dx, dy, ex, ey);
      dx = ex;
      dy = ey + h2;
      g.drawLine(ex, ey, dx, dy);
      ex = dx - cw;
      ey = dy;
      g.drawLine(dx, dy, ex, ey);
      dx = ex;
      dy = ey - h2;
      g.drawLine(ex, ey, dx, dy);
      ex = dx - w1;
      ey = dy;
      g.drawLine(dx, dy, ex, ey);
      dx = ex;
      dy = ey - ch;
      g.drawLine(ex, ey, dx, dy);
      ex = dx + w1;
      ey = dy;
      g.drawLine(dx, dy, ex, ey);
      dx = ex;
      dy = ey - h1;
      g.drawLine(ex, ey, dx, dy);
   }

   /**
    * Draws 2 rectangles
    * <br>
    * 
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawCrossFill(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int heightHorizontalBar = p.get1(IBOFigCross.FIG_CROSS_OFFSET_2HTHICK1);
      int widthVerticalBar = p.get1(IBOFigCross.FIG_CROSS_OFFSET_3VTHICK1);
      int crossHheight = h * heightHorizontalBar / 100;
      int crossVwidth = w * widthVerticalBar / 100;

      int dx = x + ((w - crossVwidth) / 2);
      int dy = y;
      g.fillRect(dx, dy, crossVwidth, h);

      dx = x;
      dy = y + ((h - crossHheight) / 2);
      g.fillRect(dx, dy, w, crossHheight);

   }

   public int getEllipseGradSize(int w, int h, ByteObject grad) {
      final int type = grad.get1(IBOGradient.GRADIENT_OFFSET_07_TYPE1);
      switch (type) {
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_00_NORMAL:
            return Math.min(h, w) / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_01_HORIZ:
            return h / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_02_VERT:
            return w / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_03_TOP_FLAMME:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_04_BOT_FLAMME:
            return w / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_05_LEFT_FLAMME:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_06_RIGHT_FLAMME:
            return h / 2;

         case ITechGradient.GRADIENT_TYPE_ELLIPSE_07_CLOCHE_TOP:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_08_CLOCHE_BOT:
            return h;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_09_CLOCHE_LEFT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_10_CLOCHE_RIGHT:
            return w;

         case ITechGradient.GRADIENT_TYPE_ELLIPSE_11_WATER_DROP_TOP:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_12_WATER_DROP_BOT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_31_WATER_DROP_TOP:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_32_WATER_DROP_BOT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_35_WATER_DROP_TOP:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_36_WATER_DROP_BOT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_40_DROP_H_CENTER:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_41_WATER_DROP_TOP:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_42_WATER_DROP_BOT:
            return w / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_13_WATER_DROP_LEFT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_14_WATER_DROP_RIGHT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_33_WATER_DROP_LEFT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_34_WATER_DROP_RIGHT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_37_WATER_DROP_LEFT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_38_WATER_DROP_RIGHT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_39_DROP_V_CENTER:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_43_WATER_DROP_LEFT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_44_WATER_DROP_RIGHT:
            return h / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_20_T:
            return 360;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_45_OBUS_TOP:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_46_OBUS_BOT:
            return Math.min(h, w) / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_47_OBUS_LEFT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_48_OBUS_RIGHT:
            return Math.min(h, w) / 2;
      }
      return Math.min(h, w) / 2;
   }

   public void drawEllipseGradient(GraphicsX g, int x, int y, int w, int h, ByteObject p, int color, ByteObject grad) {
      final int type = grad.get1(IBOGradient.GRADIENT_OFFSET_07_TYPE1);
      int gradSize = getEllipseGradSize(w, h, grad);
      int count = 0;
      int start = p.get2(IBOFigEllipse.FIG_ELLIPSE_OFFSET_05_ANGLE_START2);
      int amplitude = p.get2(IBOFigEllipse.FIG_ELLIPSE_OFFSET_06_ANGLE_END2);
      int slip = p.get2(IBOFigEllipse.FIG_ELLIPSE_OFFSET_07_ANGLE_SLIP2);
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      fillArea(g, x, y, w, h, grad, ci);

      int[] types = new int[] { type };
      if (grad.hasFlag(IBOGradient.GRADIENT_OFFSET_04_FLAGX1, IBOGradient.GRADIENT_FLAGX_8_MANY_TYPES)) {
         ByteObject ar = grad.getSubAtIndex(type);
         types = boc.getLitteralIntOperator().getLitteralArray(ar);
      }
      int amp = amplitude;
      int angleChange = 360 / gradSize;
      boolean v = true;
      while ((count = ci.iteratePixelCount(g)) != -1) {
         v = !v;
         int count2 = count * 2;
         for (int i = 0; i < types.length; i++) {
            int mtype = types[i];
            switch (mtype) {
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_00_NORMAL:
                  g.fiArc(x + count, y + count, w - count2, h - count2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_01_HORIZ:
                  g.fiArc(x, y + count, w, h - count2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_02_VERT:
                  g.fiArc(x + count, y, w - count2, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_03_TOP_FLAMME:
                  g.fiArc(x + count, y, w - count2, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_04_BOT_FLAMME:
                  g.fiArc(x + count, y + count, w - count2, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_05_LEFT_FLAMME:
                  g.fiArc(x, y + count, w - count, h - count2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_06_RIGHT_FLAMME:
                  g.fiArc(x + count, y + count, w - count, h - count2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_07_CLOCHE_TOP:
                  g.fiArc(x, y, w, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_08_CLOCHE_BOT:
                  g.fiArc(x, y + count, w, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_09_CLOCHE_LEFT:
                  g.fiArc(x + count, y, w - count, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_10_CLOCHE_RIGHT:
                  g.fiArc(x, y, w - count, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_11_WATER_DROP_TOP:
                  g.fiArc(x + count, y - count, w - count2, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_12_WATER_DROP_BOT:
                  g.fiArc(x + count, y + count, w - count2, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_13_WATER_DROP_LEFT:
                  g.fiArc(x - count, y + count, w, h - count2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_14_WATER_DROP_RIGHT:
                  g.fiArc(x + count, y + count, w, h - count2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_15_TOP_LEFT_BUBBLE:
                  g.fiArc(x, y, w - count, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_16_TOP_RIGHT_BUBBLE:
                  g.fiArc(x + count, y, w - count, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_17_BOT_LEFT_BUBBLE:
                  g.fiArc(x, y + count, w - count, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_18_BOT_RIGHT_BUBBLE:
                  g.fiArc(x + count, y + count, w - count, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_19_TEST:
                  g.fiArc(x + count, y + count, w - count, h - count, start - count, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_20_T:
                  g.fiArc(x, y, w, h, start + count, 360 - count);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_21_T:
                  if (count == 0) {
                     amp = amplitude;
                  } else {
                     if (v) {
                        amp = -180;
                     } else {
                        amp = 180;
                     }
                  }
                  g.fiArc(x + count, y + count, w - count2, h - count2, start, amp);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_22_T:

                  g.fiArc(x + count, y + count, w - count2, h - count2, start + count * angleChange, 360 - count * angleChange);
                  break;

               case ITechGradient.GRADIENT_TYPE_ELLIPSE_31_WATER_DROP_TOP:
                  g.fiArc(x + count, y - count, w - count2, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_32_WATER_DROP_BOT:
                  g.fiArc(x + count, y + count2, w - count2, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_33_WATER_DROP_LEFT:
                  g.fiArc(x - count, y + count, w - count, h - count2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_34_WATER_DROP_RIGHT:
                  g.fiArc(x + count2, y + count, w - count, h - count2, start, amplitude);
                  break;

               case ITechGradient.GRADIENT_TYPE_ELLIPSE_39_DROP_V_CENTER:
                  g.fiArc(x + count, y + h / 4 - count / 2, w - count2, h / 2 + count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_40_DROP_H_CENTER:
                  g.fiArc(x + w / 4 - count / 2, y + count, w / 2 + count, h - count2, start, amplitude);
                  break;

               case ITechGradient.GRADIENT_TYPE_ELLIPSE_35_WATER_DROP_TOP:
                  g.fiArc(x + count, y + h / 4 - count / 2, w - count2, ((3 * h) / 4) + 1 - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_36_WATER_DROP_BOT:
                  g.fiArc(x + count, y + h / 4 - count / 2, w - count2, ((3 * h) / 4) + 1 - count / 2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_37_WATER_DROP_LEFT:
                  g.fiArc(x + count, y + h / 4 - count / 2, w - count2, ((3 * h) / 4) + 1, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_38_WATER_DROP_RIGHT:
                  g.fiArc(x + count, y + h / 4 - count / 2, w - count2, ((3 * h) / 4) + count / 2, start, amplitude);
                  break;

               case ITechGradient.GRADIENT_TYPE_ELLIPSE_41_WATER_DROP_TOP:
                  g.fiArc(x + w / 2 - count, y, w / 2 - count, h - count2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_42_WATER_DROP_BOT:
                  g.fiArc(x + w / 4 + count / 2, y + h / 2 - count, w / 2 - count, h / 2 + count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_43_WATER_DROP_LEFT:
                  g.fiArc(x + count / 2, y + h / 2 - count, w - count, h / 2 + count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_44_WATER_DROP_RIGHT:
                  g.fiArc(x + count, y + h / 2 - count, w - count2, h / 2 + count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_45_OBUS_TOP:
                  g.fiArc(x, y, w, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_46_OBUS_BOT:
                  g.fiArc(x, y + count, w, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_47_OBUS_LEFT:
                  g.fiArc(x + count, y, w - count, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_48_OBUS_RIGHT:
                  g.fiArc(x, y, w - count, h, start, amplitude);
                  break;
               default:
                  break;
            }
         }
         start += slip;
      }
   }

   void drawFigArlequin(GraphicsX g, int x, int y, int w, int h, ByteObject p) {

      if (p.hasFlag(IBOFigArlequin.FIG_ARLEQUIN_OFFSET_1_FLAG, IBOFigArlequin.FIG_ARLEQUIN_FLAG_1_FILL_BG)) {
         int pcolor = p.get4(FIG__OFFSET_06_COLOR4);
         g.setColor(pcolor);
         g.fillRect(x, y, w, h);
      }
      int sizeW = p.get4(IBOFigArlequin.FIG_ARLEQUIN_OFFSET_3_SIZE_W4);
      int sizeH = p.get4(IBOFigArlequin.FIG_ARLEQUIN_OFFSET_4_SIZE_H4);

      int numFigures = p.get2(IBOFigArlequin.FIG_LAYOUT_OFFSET_3_NUM_FIGURES2);
      int indexStart = p.get2(IBOFigArlequin.FIG_LAYOUT_OFFSET_5_INDEX_START2);

      ByteObject[] figures = p.getSubsRange(indexStart, numFigures);
      int sepSizeW = decodeWidth(IBOFigArlequin.FIG_ARLEQUIN_OFFSET_5_SIZE_SEP_W4, p, w, h);
      int sepSizeH = decodeHeight(IBOFigArlequin.FIG_ARLEQUIN_OFFSET_6_SIZE_SEP_H4, p, w, h);

      int numW = 0;
      int numH = 0;

      if (sizeW == 0) {
         sizeW = w / numFigures;
         numW = 2;
      } else {
         if (p.hasFlag(IBOFigArlequin.FIG_ARLEQUIN_OFFSET_1_FLAG, IBOFigArlequin.FIG_ARLEQUIN_FLAG_2_SIZEW_IS_NUM)) {
            numW = sizeW;
            int sepSizes = sepSizeW * (numW - 1);
            sizeW = (w - sepSizes) / (numW);
         } else {
            sizeW = drc.getLAC().getLayoutOperator().decode(sizeW, w, h, ITechLayout.CTX_1_WIDTH);
            numW = w / (sizeW + sepSizeW);
         }
      }
      if (sizeH == 0) {
         sizeH = h / numFigures;
         numH = 2;
      } else {
         if (p.hasFlag(IBOFigArlequin.FIG_ARLEQUIN_OFFSET_1_FLAG, IBOFigArlequin.FIG_ARLEQUIN_FLAG_3_SIZEH_IS_NUM)) {
            numH = sizeH;
            int sepSizes = sepSizeH * (numH - 1);
            sizeH = (h - sepSizes) / (numH);
         } else {
            sizeH = drc.getLAC().getLayoutOperator().decode(sizeH, w, h, ITechLayout.CTX_2_HEIGHT);
            numH = h / (sizeH + sepSizeH);
         }
      }

      int typeW = p.get1(IBOFigArlequin.FIG_ARLEQUIN_OFFSET_7_EXTRA_W_TYPE1);
      int typeH = p.get1(IBOFigArlequin.FIG_ARLEQUIN_OFFSET_8_EXTRA_H_TYPE1);

      int dx = x;
      int dy = y;
      int diffW = w - (sizeW * numW) - (sepSizeW * (numW - 1));
      int diffH = h - (sizeH * numH) - (sepSizeH * (numH - 1));
      //3 cases of extra distribution
      int offsetH = 0;

      for (int i = 0; i < numH; i++) {
         int dh = sizeH;
         int offsetW = offsetH; //start of the row is changing every row
         ByteObject figure = figures[offsetW];
         if (typeH == ITechFigure.EXTRA_1_TOP_LEFT) {
            if (i == 0) {
               //last row
               dh += diffH;
            }
         } else if (typeH == ITechFigure.EXTRA_2_BOT_RITE) {
            if (i == numH - 1) {
               //last row
               dh += diffH;
            }
         } else {
            //center
            if (i == 0) {
               dh += (diffH / 2);
            } else if (i == numH - 1) {
               dh += (diffH - (diffH / 2));
            }
         }
         for (int j = 0; j < numW; j++) {
            int dw = sizeW;
            if (typeW == ITechFigure.EXTRA_1_TOP_LEFT) {
               if (j == 0) {
                  //last row
                  dw += diffW;
               }
            } else if (typeW == ITechFigure.EXTRA_2_BOT_RITE) {
               if (j == numW - 1) {
                  //last row
                  dw += diffW;
               }
            } else {
               //center
               if (j == 0) {
                  dw += (diffW / 2);
               } else if (j == numW - 1) {
                  dw += (diffW - (diffW / 2));
               }
            }
            if (figure != null) {
               paintFigureSwitch(g, dx, dy, dw, dh, figure);
            }
            dx += dw;
            dx += sepSizeW;
            offsetW = (offsetW + 1) % numFigures;
            figure = figures[offsetW];

         }
         dx = x;
         dy += dh;
         dy += sepSizeH;
         offsetH = (offsetH + 1) % numFigures;

      }
   }

   /**
    * Generic method that infers the type of border to draw based on the drawing parameters
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawFigBorder(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      //read the Top Bottom Left Right size values
      ByteObject tblr = p.getSubFirst(IBOTypesLayout.FTYPE_2_TBLR);
      if (tblr == null) {
         throw new NullPointerException("TBLR Border Size is null");
      }
      boolean isFigures = p.hasFlag(FIG_BORDER_OFFSET_1_FLAG, FIG_BORDER_FLAG_8_FIGURES);
      if (isFigures) {
         drawFigBorderRects(g, x, y, w, h, p, tblr);
         return;
      }
      int dx = x;
      int dy = y;
      int cornerShift = p.get1(FIG_BORDER_OFFSET_2_CORNER_SHIFT1);
      boolean isDotted = p.get1(FIG_BORDER_OFFSET_3_STROKE_STYLE1) == 1;
      if (isDotted) {
         g.setStrokeStyle(GraphicsX.STROKE_1_DOTTED);
      }
      //main rectangle that we need in all configurations
      ByteObject rect = p.getSubOrder(IBOTypesDrawX.TYPE_DRWX_00_FIGURE, 0);
      LayoutOperator layoutOperator = getLayoutOperator();
      if (tblr.hasFlag(TBLR_OFFSET_01_FLAG, TBLR_FLAG_4_SAME_VALUE) && p.hasFlag(FIG_BORDER_OFFSET_1_FLAG, FIG_BORDER_FLAG_5_FIGURE)) {
         int size = layoutOperator.getTBLRValue(tblr, C.POS_0_TOP, x, y, w, h);
         if (size == 0)
            return;
         if (p.hasFlag(FIG_BORDER_OFFSET_1_FLAG, FIG_BORDER_FLAG_1_OUTER)) {
            //outer
            dx -= size;
            dy -= size;
            w += (2 * size);
            h += (2 * size);
         }
         if (cornerShift == 0) {
            //rectangle
            if (rect == null) {
               throw new NullPointerException("Rectangle Definition for Border is null");
            }
            if (p.hasFlag(FIG_BORDER_OFFSET_1_FLAG, FIG_BORDER_FLAG_2_FILLED)) {
               //TODO move this to the option for round rectangle
               //or the gradient because we want the same color as the first one
               int color = rect.get4(FIG__OFFSET_06_COLOR4);
               g.setColor(color);
               g.fillRect(dx, dy, w, h);
               rect.set1(FIG_RECTANGLE_OFFSET_5_SIZE_GRAD1, size);
            } else {
               rect.set1(FIG_RECTANGLE_OFFSET_4_SIZE_FILL1, size);
            }
            paintFigureSwitch(g, dx, dy, w, h, rect);
         } else {
            if (cornerShift >= size) {
               return;
            }
            int pixelSize = size - cornerShift;
            //draw 4 lines
            ByteObject grad = rect.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
            int color = rect.get4(FIG__OFFSET_06_COLOR4);
            if (g.hasGradient() && grad != null) {
               int gradientSize = pixelSize / 2;
               ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradientSize);
               int count = 0;
               int dw = w;
               int dh = h;
               int dpixel = pixelSize;
               while ((count = ci.iteratePixelStep(g)) != -1) {
                  dx += count;
                  dy += count;
                  dw -= 2 * count;
                  dh -= 2 * count;
                  dpixel -= 2 * count;
                  g.fillRect(dx, dy + cornerShift, dw, dpixel); //top
                  g.fillRect(dx, dy + dh - cornerShift - dpixel, dw, dpixel);
                  g.fillRect(dx + cornerShift, dy, dpixel, dh);
                  g.fillRect(dx + dw - cornerShift - dpixel, dy, dpixel, dh);
               }
            } else {
               g.setColor(color);
               g.fillRect(x, y + cornerShift, w, pixelSize);
               g.fillRect(x, y + h - cornerShift - pixelSize, w, pixelSize);
               g.fillRect(x + cornerShift, y, pixelSize, h);
               g.fillRect(x + w - cornerShift - pixelSize, y, pixelSize, h);
            }
         }
      } else {
         int[] values = layoutOperator.getTBLRValues(tblr, x, y, w, h);
         int sizeTop = values[0];
         int sizeBot = values[1];
         int sizeLeft = values[2];
         int sizeRight = values[3];
         if (p.hasFlag(FIG_BORDER_OFFSET_1_FLAG, FIG_BORDER_FLAG_1_OUTER)) {
            //outer
            dx -= sizeLeft;
            dy -= sizeTop;
            w += (sizeLeft + sizeRight);
            h += (sizeTop + sizeBot);
         }
         if (p.hasFlag(FIG_BORDER_OFFSET_1_FLAG, FIG_BORDER_FLAG_8_FIGURES)) {
            //top
            int ch = h - sizeTop - sizeBot;
            int cw = w - sizeLeft - sizeRight;
            ByteObject figTop = p.getSubAtIndex(0);
            ByteObject figBot = p.getSubAtIndex(1);
            ByteObject figLeft = p.getSubAtIndex(2);
            ByteObject figRight = p.getSubAtIndex(3);

            paintFigure(g, dx + sizeLeft, dy, cw, sizeTop, figTop);
            paintFigure(g, dx + sizeLeft, dy + h - sizeBot, cw, sizeBot, figBot); //bottom
            paintFigure(g, dx, dy + sizeTop, sizeLeft, ch, figLeft); // left
            paintFigure(g, dx + w - sizeRight, dy + sizeTop, sizeRight, ch, figRight); // left
            if (p.hasFlag(FIG_BORDER_OFFSET_1_FLAG, FIG_BORDER_FLAG_4_COIN)) {
               ByteObject figTL = p.getSubAtIndex(4);
               ByteObject figTR = p.getSubAtIndex(5);
               ByteObject figBL = p.getSubAtIndex(6);
               ByteObject figBR = p.getSubAtIndex(7);

               paintFigure(g, dx, dy, sizeLeft, sizeTop, figTL); // TL
               paintFigure(g, dx + w - sizeRight, dy, sizeRight, sizeTop, figTR); // TR
               paintFigure(g, dx, dy + h - sizeBot, sizeLeft, sizeBot, figBL); // BL
               paintFigure(g, dx + w - sizeRight, dy + h - sizeBot, sizeRight, sizeBot, figBR); // BR
            }
         } else {
            ByteObject[] figs = p.getSubs(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
            int count = 0;
            ByteObject figTop = figs[(count++) % figs.length];
            ByteObject figBot = figs[(count++) % figs.length];
            ByteObject figLeft = figs[(count++) % figs.length];
            ByteObject figRight = figs[(count++) % figs.length];
            int master = 0;
            int ch = h - sizeTop - sizeBot;
            int cw = w - sizeLeft - sizeRight;

            if (master == DIM_MASTER_0_NONE) {
               paintFigure(g, dx + sizeLeft, dy, cw, sizeTop, figTop);
               paintFigure(g, dx + sizeLeft, dy + h - sizeBot, cw, sizeBot, figBot); //bottom
               paintFigure(g, dx, dy + sizeTop, sizeLeft, ch, figLeft); // left
               paintFigure(g, dx + w - sizeRight, dy + sizeTop, sizeRight, ch, figRight); // left
            } else if (master == DIM_MASTER_1_HORI) {
               paintFigure(g, dx, dy, w, sizeTop, figTop);
               paintFigure(g, dx, dy + h - sizeBot, w, sizeBot, figBot);

               paintFigure(g, dx, dy + sizeTop, sizeLeft, ch, figLeft); // left
               paintFigure(g, dx + w - sizeRight, dy + sizeTop, sizeRight, ch, figRight); // left
            } else {
               paintFigure(g, dx + sizeLeft, dy, cw, sizeTop, figTop);
               paintFigure(g, dx + sizeLeft, dy + h - sizeBot, cw, sizeBot, figBot); //bottom

               paintFigure(g, dx, dy, sizeLeft, ch, figLeft); // left
               paintFigure(g, dx + w - sizeRight, dy, sizeRight, ch, figRight); // left

            }
            throw new IllegalArgumentException();
         }
      }
      if (isDotted) {
         g.setStrokeStyle(GraphicsX.STROKE_0_FILL);
      }
   }

   protected void drawFigBorderRects(GraphicsX g, int x, int y, int w, int h, ByteObject p, ByteObject tblr) {
      LayoutOperator layoutOperator = getLayoutOperator();
      int[] values = layoutOperator.getTBLRValues(tblr, x, y, w, h);
      int cornerShift = p.get1(FIG_BORDER_OFFSET_2_CORNER_SHIFT1);
      int sizeTop = values[0];
      int sizeBot = values[1];
      int sizeLeft = values[2];
      int sizeRight = values[3];
      if (p.hasFlag(FIG_BORDER_OFFSET_1_FLAG, FIG_BORDER_FLAG_1_OUTER)) {
         //outer
         x -= sizeLeft;
         y -= sizeTop;
         w += (sizeLeft + sizeRight);
         h += (sizeTop + sizeBot);
      }
      ByteObject[] figs = p.getSubs(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
      int count = 0;
      int numFigures = figs.length;
      ByteObject figTop = figs[(count++) % numFigures];
      ByteObject figBot = figs[(count++) % numFigures];
      ByteObject figLeft = figs[(count++) % numFigures];
      ByteObject figRight = figs[(count++) % numFigures];
      int master = p.get1(FIG_BORDER_OFFSET_4_DIM_MASTER1);

      if (master == DIM_MASTER_0_NONE) {
         int wHoriz = w - sizeLeft - sizeRight;
         int xTop = x + sizeLeft;
         int yTop = y + cornerShift;
         paintFigure(g, xTop, yTop, wHoriz, sizeTop, figTop); //top
         int xBot = x + sizeLeft;
         int yBot = y + h - sizeBot - cornerShift;
         paintFigure(g, xBot, yBot, wHoriz, sizeBot, figBot); //bottom

         int hVerti = h - sizeTop - sizeBot;
         int xLeft = x + cornerShift;
         int yLeft = y + sizeTop;
         paintFigure(g, xLeft, yLeft, sizeLeft, hVerti, figLeft); // left
         int xRight = x + w - sizeRight - cornerShift;
         int yRight = y + sizeTop;
         paintFigure(g, xRight, yRight, sizeRight, hVerti, figRight); // right

      } else if (master == DIM_MASTER_1_HORI) {
         paintFigure(g, x, y + cornerShift, w, sizeTop, figTop);
         paintFigure(g, x, y + h - sizeBot - cornerShift, w, sizeBot, figBot);

         int hVerti = h - sizeTop - sizeBot;
         paintFigure(g, x + cornerShift, y + sizeTop, sizeLeft, hVerti, figLeft); // left
         paintFigure(g, x + w - sizeRight - cornerShift, y + sizeTop, sizeRight, hVerti, figRight); // left
      } else if (master == DIM_MASTER_2_VERT) {
         int wHoriz = w - sizeLeft - sizeRight;
         paintFigure(g, x + sizeLeft, y + cornerShift, wHoriz, sizeTop, figTop);
         paintFigure(g, x + sizeLeft, y + h - sizeBot - cornerShift, wHoriz, sizeBot, figBot); //bottom

         paintFigure(g, x + cornerShift, y, sizeLeft, h, figLeft); // left
         paintFigure(g, x + w - sizeRight - cornerShift, y, sizeRight, h, figRight); // rite

      }

      if (p.hasFlag(FIG_BORDER_OFFSET_1_FLAG, FIG_BORDER_FLAG_4_COIN)) {
         ByteObject figTopLeft = figs[(count++) % numFigures];
         ByteObject figTopRite = figs[(count++) % numFigures];
         ByteObject figBL = figs[(count++) % numFigures];
         ByteObject figBR = figs[(count++) % numFigures];

         paintFigure(g, x, y, sizeLeft, sizeTop, figTopLeft); // TopLeft
         paintFigure(g, x + w - sizeRight, y, sizeRight, sizeTop, figTopRite); // TR
         paintFigure(g, x, y + h - sizeBot, sizeLeft, sizeBot, figBL); // BL
         paintFigure(g, x + w - sizeRight, y + h - sizeBot, sizeRight, sizeBot, figBR); // BR
      }
   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawFigEllipse(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int color = p.get4(FIG__OFFSET_06_COLOR4);
      int border = p.get1(IBOFigEllipse.FIG_ELLIPSE_OFFSET_03_SIZE_FILL1);
      if (border != 0) {
         //we must draw using a mask
         ByteObject borderSizer = p.getSubFirst(IBOTypesLayout.FTYPE_3_SIZER);
      }
      boolean isArc = p.hasFlag(IBOFigEllipse.FIG_ELLIPSE_OFFSET_01_FLAG1, IBOFigEllipse.FIG_ELLIPSE_FLAG_3_FIL_DE_FER);
      g.setFillMode(!isArc);
      ByteObject grad = p.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      if (g.hasGradient() && grad != null) {
         drawEllipseGradient(g, x, y, w, h, p, color, grad);
      } else {
         drawFigEllipse(g, x, y, w, h, color, p);
      }
   }

   public void drawFigEllipse(GraphicsX g, int x, int y, int w, int h, int color, ByteObject p) {
      int start = p.get2(IBOFigEllipse.FIG_ELLIPSE_OFFSET_05_ANGLE_START2);
      int amplitude = p.get2(IBOFigEllipse.FIG_ELLIPSE_OFFSET_06_ANGLE_END2);

      g.setColor(color);
      g.fiArc(x, y, w, h, start, amplitude);
   }

   private void drawFigGrid(GraphicsX g, int x, int y, int w, int h, ByteObject p) {

      boolean isGrill = true;
      LayoutOperator layOp = drc.getLAC().getLayoutOperator();
      int squareSizeW = layOp.codedSizeDecodeW(p, IBOFigGrid.FIG_GRID_OFFSET_05_VSIZE4, w, h);
      int squareSizeH = layOp.codedSizeDecodeH(p, IBOFigGrid.FIG_GRID_OFFSET_04_HSIZE4, w, h);

      int lineSizeV = layOp.codedSizeDecodeH(p, IBOFigGrid.FIG_GRID_OFFSET_06_HSEPSIZE4, w, h);
      int lineSizeH = layOp.codedSizeDecodeW(p, IBOFigGrid.FIG_GRID_OFFSET_07_VSEPSIZE4, w, h);

      int colorv = p.get4(IBOFigGrid.FIG_GRID_OFFSET_03_VCOLOR4);
      int colorh = p.get4(IBOFigGrid.FIG_GRID_OFFSET_02_HCOLOR4);
      int colorm = p.get4(IBOFigGrid.FIG__OFFSET_06_COLOR4);

      g.setColor(colorm);
      g.fillRect(x, y, w, h);

      g.setColor(colorh);

      int vcount = 0;
      int end = y + h;
      int dy = y;

      while (dy < end) {
         if (!(isGrill && vcount == 0)) {
            for (int j = 0; j < lineSizeV; j++) {
               g.drawLine(x, dy, x + w - 1, dy);
               dy++;
            }
         }
         dy += squareSizeH;
         vcount++;
      }
      int hcount = 0;
      end = x + w;
      int dx = x;
      //
      g.setColor(colorv);
      while (dx < end) {
         if (!(isGrill && hcount == 0)) {
            for (int j = 0; j < lineSizeH; j++) {
               g.drawLine(dx, y, dx, y + h - 1);
               dx++;
            }
         }
         dx += squareSizeW;
         hcount++;
      }
   }

   /**
    * Grid of vertical and horizontal lines.
    * + definition of Grid figure artifacts
    * @param g
    * @param vLines
    * @param vSizes
    * @param vFirst
    * @param vLast
    * @param hLines
    * @param hSize
    * @param hFirst
    * @param hLast
    */
   public void drawFigLines(GraphicsX g, int x, int y, ByteObject vLines, int[] vSizes, int vFirst, int vLast, ByteObject hLines, int[] hSize, int hFirst, int hLast) {

   }

   void drawFigLosange(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      drawerTriangle.drawFigLosange(g, x, y, w, h, p);
   }

   /**
    * Draw its rectangle along any gradient and rounded border
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawFigRectangle(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      //4 cases. opaque rectangle,
      int color = p.get4(FIG__OFFSET_06_COLOR4);
      boolean hasGradient = p.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_2_GRADIENT);
      ByteObject gradient = null;

      int arcw = p.get1(FIG_RECTANGLE_OFFSET_2_SIZE_ARCW1);
      LayoutOperator layOp = drc.getLAC().getLayoutOperator();
      if (p.hasFlag(FIG_RECTANGLE_OFFSET_1_FLAG, FIG_RECTANGLE_FLAG_5_ARCW_SIZER)) {
         ByteObject sizer = p.getSubAtIndex(arcw);
         arcw = layOp.getPixelSizeW(sizer, w, h);
      }
      int arch = p.get1(FIG_RECTANGLE_OFFSET_3_SIZE_ARCH1);
      if (p.hasFlag(FIG_RECTANGLE_OFFSET_1_FLAG, FIG_RECTANGLE_FLAG_6_ARCH_SIZER)) {
         ByteObject sizer = p.getSubAtIndex(arch);
         arch = layOp.getPixelSizeH(sizer, w, h);
      }
      int sizeFill = p.get1(FIG_RECTANGLE_OFFSET_4_SIZE_FILL1);
      if (p.hasFlag(FIG_RECTANGLE_OFFSET_1_FLAG, FIG_RECTANGLE_FLAG_7_FILL_SIZER)) {
         ByteObject sizer = p.getSubAtIndex(arcw);
         sizeFill = layOp.getPixelSize(sizer, w, h);
      }

      if (hasGradient) {
         gradient = p.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      }
      if (g.hasGradient() && gradient != null) {
         drawRectangleGradient(g, x, y, w, h, p, color, arcw, arch, sizeFill, gradient);
      } else {
         drawRectangleSingleColor(g, x, y, w, h, p, color, arcw, arch, sizeFill);
      }
   }

   public void drawFigRectangleShape(GraphicsX g, int x, int y, int w, int h, int color, int size, int arcw, int arch) {
      g.setColor(color);
      for (int i = 0; i < size; i++) {
         g.drawRoundRect(x, y, w, h, arcw, arch);
         x++;
         y++;
         w -= 2;
         h -= 2;
      }
   }

   /**
    * Draw figure using a repeater pattern
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   void drawFigRepeater(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      //first check if figure is opaque
      //when opaque just use the copy method
      int bgColor = p.get4(FIG__OFFSET_06_COLOR4);
      ByteObject figure = p.getSubFirst(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
      ByteObject anchor = p.getSubFirst(IBOTypesDrawX.TYPE_DRWX_03_BOX);
      if (figure == null)
         throw new IllegalArgumentException();
      int fx = x;
      int fy = y;
      int fw = drc.getBoxFactory().computeSizeW(anchor, w, h);
      int fh = drc.getBoxFactory().computeSizeH(anchor, w, h);
      int ha = anchor.get1(BOX_OFFSET_02_HORIZ_ALIGN4);
      int va = anchor.get1(BOX_OFFSET_03_VERTICAL_ALIGN4);
      if (fw <= 0)
         fw = w;
      if (fh <= 0)
         fh = h;
      int dw = w % fw;
      int dh = h % fh;
      if (ha == ITechAnchor.ALIGN_4_RIGHT) {
         fx += dw;
      } else if (ha == ITechAnchor.ALIGN_6_CENTER) {
         fx += dw / 2; //align cente
      } //align left does not change fx
      if (va == ITechAnchor.ALIGN_2_BOTTOM) {
         fy += dh;
      } else if (va == ITechAnchor.ALIGN_6_CENTER) {
         fy += dh / 2; //align center
      }
      int numX = w / fw;
      int numY = h / fh;

      //#debug
      g.toDLog().pDraw("numX=" + numX + " numY=" + numY + " fw=" + fw + " fh=" + fh, p, FigureOperator.class, "drawFigRepeater");

      if (p.hasFlag(IBOFigRepeater.FIG_REPEATER_OFFSET_1_FLAG, IBOFigRepeater.FIG_REPEATER_FLAG_1_FORCECOPYAREA) || figure.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAGP_3_OPAQUE)) {
         if (p.hasFlag(IBOFigRepeater.FIG_REPEATER_OFFSET_1_FLAG, IBOFigRepeater.FIG_REPEATER_FLAG_2_USE_BGCOLOR)) {
            //fill bg background color
         }
         paintFigure(g, fx, fy, fw, fh, figure);
         int x_dest = fx + fw;
         for (int i = 0; i < numX; i++) {
            //g.copyArea(fx, fy, fw, fh, x_dest, fy, IDrw.ANCHOR);
            x_dest += fw;
         }
         int y_dest = fy;
         for (int i = 0; i < numY; i++) {
            //g.copyArea(fx, fy, fw * numX, fh, fx, y_dest, IDrw.ANCHOR);
            y_dest += fh;
         }

      } else {
         //create an image
         int color = 0;
         if (p.hasFlag(IBOFigRepeater.FIG_REPEATER_OFFSET_1_FLAG, IBOFigRepeater.FIG_REPEATER_FLAG_2_USE_BGCOLOR)) {
            //fill bg background color
            color = bgColor;
         }
         RgbImage img = getFigImage(figure, fw, fh, false, false, color);
         int y_dest = fy;
         for (int i = 0; i < numY; i++) {
            int x_dest = fx;
            for (int j = 0; j < numX; j++) {
               g.drawRgbImage(img, x_dest, y_dest, ANCHOR);
               x_dest += fw;
            }
            y_dest += fh;
         }
         img.dispose();
      }
   }

   void drawFigTesson(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int pcolor = p.get4(FIG__OFFSET_06_COLOR4);

      int maxSizeTesson = p.get1(IBOFigTesson.FIG_TESSON_OFFSET_4_SIZE_MAX1);
      int minSize = 20;
      maxSizeTesson = 40;
      boolean useSeed = p.hasFlag(IBOFigTesson.FIG_TESSON_OFFSET_1_FLAG, IBOFigTesson.FIG_TESSON_FLAG_2_USE_SEED);
      Random r = null;
      if (useSeed) {
         long seed = p.getLong(IBOFigTesson.FIG_TESSON_OFFSET_5_SEED8);
         r = drc.getUC().getRandom(seed);
      } else {
         r = drc.getRandom();
      }
      TessonUtilz.drawFigTessonTrigInCircle(g, x, y, w, h, p, r, drc.getBOC());

   }

   /**
    * Draws a color over the referential rectangle execpt over the Window rectangle
    * @param g
    * @param x referential x coordinate
    * @param y referential y coordinate
    * @param w referential width
    * @param h referendial height
    * @param color the color of the areas
    * @param wx window x coordinate
    * @param wy window y coordinate
    * @param ww window's width
    * @param wh window's height
    */
   public void drawGlassWindow(GraphicsX g, int x, int y, int w, int h, int color, int wx, int wy, int ww, int wh) {
      int hx = wx;
      int hy = wy;
      int hw = ww;
      int hh = wh;
      int[] dim = new int[4];
      drc.getUC().getGeo2dUtils().getIntersection(x, y, w, h, hx, hy, hw, hh, dim);
      hx = dim[0];
      hy = dim[1];
      hw = dim[2];
      hh = dim[3];
      //find the 4 rectangles
      int x0 = x;
      int y0 = y;
      int w0 = w;
      int h0 = hy - y;
      int x1 = x;
      int y1 = y + h0;
      int w1 = hx - x;
      int h1 = hh;
      int x2 = w1 + hw;
      int y2 = y1;
      int w2 = w - hw - w1;
      int h2 = h1;
      int x3 = 0;
      int y3 = y + h0 + hh;
      int w3 = w;
      int h3 = h - hh - h0;
      RgbImage img = drc.getCache().create(w, h, color);
      //top image
      img.changeDimension(w0, h0);
      img.draw(g, x0, y0);
      //left
      img.changeDimension(w1, h1);
      img.draw(g, x1, y1);
      //right
      img.changeDimension(w2, h2);
      img.draw(g, x2, y2);
      //bottom
      img.changeDimension(w3, h3);
      img.draw(g, x3, y3);
      img.dispose();

   }

   /**
     * Line pixels start at x,y and run to w,h
     * w may be ignored in which case we have a vertical line 
     * h may be ignored => horizontal line
     * The line may actually be a figure with a size anchor
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param p
     */
   void drawLine(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int linesize = p.getValue(IBOFigLine.FIG_LINE_OFFSET_2SIZE1, 1);
      int color = p.get4(FIG__OFFSET_06_COLOR4);
      boolean horiz = p.hasFlag(IBOFigLine.FIG_LINE_OFFSET_1FLAG, IBOFigLine.FIG_LINE_FLAG_HORIZ);
      g.setColor(color);
      if (horiz) {
         for (int j = 0; j < linesize; j++) {
            g.drawLine(x, y, x + w - 1, y);
            y++;
         }
      } else {
         for (int j = 0; j < linesize; j++) {
            g.drawLine(x, y, x, y + h - 1);
            x++;
         }
      }
   }

   public void drawLine(GraphicsX g, int x, int y, int dir, int len, int size) {
      int xlen = (dir == 0) ? len : 0;
      int ylen = (dir == 0) ? 0 : len;
      for (int i = 0; i < size; i++) {
         g.drawLine(x, y, x + xlen, y + ylen);
         if (dir == 0)
            y++;
         else
            x++;
      }
   }

   public void drawMArrow(GraphicsX g, int dir, int headColor, int x, int y, int base, int H, int h, int bodyColor, int length, int size, int borderSize, int borderColor) {

   }

   /**
    * Draws Pixel Figure.
    * When Figure has a non default blending move (OVER).
    * <br>
    * <br>
    * Why don't you draw with {@link IGraphics#drawRGB(int[], int, int, int, int, int, int, boolean)} ?
    */
   public void drawPixels(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      //TODO the buffer does not get the BlendOp correctly
      RgbImage data = null;
      //or create a new empty image with the background color
      if (p.hasFlag(IBOFigPixels.FIG_PIXEL_OFFSET_01_FLAG, IBOFigPixels.FIG_PIXEL_FLAG_3_NEW_IMAGE)) {
         int color = p.get4(IBOFigPixels.FIG_PIXEL_OFFSET_04_COLOR_EXTRA4);
         data = drc.getCache().createRGB(w, h, color);
      } else {
         //either reads existing data 
         data = g.getBufferRegion(x, y, w, h, true);
         drawPixels(data.getRgbData(), data.getOffset(), data.getScanLength(), data.getM(), data.getN(), w, h, p);
      }
      g.drawRgbImage(data, x, y); //why drawing again if we take a buffer?
      data.dispose();
   }

   /**
    * Many rules for updating existing pixels
    * @param rgb
    * @param offset
    * @param scan
    * @param m
    * @param n
    * @param w
    * @param h
    * @param p
    * @param blendMode the mode for blending the pixel with existing pixel in rgb array.
    */
   public void drawPixels(int[] rgb, int offset, int scan, int m, int n, int w, int h, ByteObject p) {
      int lengthHoriz = p.getValue(IBOFigPixels.FIG_PIXEL_OFFSET_07_LENGTH_H2, 2);
      int lengthVert = p.getValue(IBOFigPixels.FIG_PIXEL_OFFSET_08_LENGTH_V2, 2);
      if (lengthHoriz <= 0)
         lengthHoriz = 1;
      if (lengthVert <= 0)
         lengthVert = 1;
      int blendMode = p.get1(IBOFigPixels.FIG_PIXEL_OFFSET_05_BLENDERX1);

      boolean randomLength = p.hasFlag(IBOFigPixels.FIG_PIXEL_OFFSET_01_FLAG, IBOFigPixels.FIG_PIXEL_FLAG_1_RANDOM_SIZE);
      boolean randomColor = p.hasFlag(IBOFigPixels.FIG_PIXEL_OFFSET_01_FLAG, IBOFigPixels.FIG_PIXEL_FLAG_2_RANDOM_COLOR);
      //or use gradient
      ByteObject grad = p.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      int[] colors = p.getValues(IBOFigPixels.FIG_PIXEL_OFFSET_04_COLORSX);
      int colorIndex = 0;
      int maxc = colors.length;
      int add = lengthHoriz;
      int baseColor = p.get4(FIG__OFFSET_06_COLOR4);
      int gradSize = p.get1(IBOFigPixels.FIG_PIXEL_OFFSET_09_GRAD_SIZE1);

      ColorIterator ci = new ColorIterator(boc, colors);
      if (grad != null) {
         ColorIterator cigrad = drc.getColorFunctionFactory().getColorIterator(baseColor, grad, gradSize);
         //rules for mixing 2 color iterator
         ci.mix(cigrad);
      }

      //define the randomness of the iteration.
      int seed = p.get4(IBOFigPixels.FIG_PIXEL_OFFSET_03_SEED4);
      Random r = drc.getUC().getRandom(seed);
      //SystemLog.pDraw("#DrwParamFig#drawPixels m=" + m + " n=" + n + " w=" + w + " h=" + h + " scan=" + scan + " offset=" + offset);
      BlendOp bop = new BlendOp(boc, blendMode);

      //for all lines
      for (int j = 0; j < h; j++) {
         int start = offset + m + (scan * (n + j));
         add = lengthHoriz;

         for (int i = 0; i < w;) {
            int color = 0;
            if (randomColor) {
               color = ci.iterateRandom(r);
            } else {
               color = ci.iterateColor();
            }
            if (randomLength) {
               add = r.nextInt(lengthHoriz + 1);
               if (add == 0)
                  add = 1;
            }
            int nadd = w - i;
            add = Math.min(add, nadd);
            for (int k = 0; k < add; k++) {
               //blending mode is a replace
               rgb[start] = bop.blendPixel(rgb[start], color);
               start++;
               i++;
            }
         }
      }
      //custom randomizer color filter that adds pixels based on existing
      int tag = colors[0];
      for (int i = 0; i < rgb.length - w; i++) {
         if (rgb[i] == tag && r.nextInt(2) == 0) {
            rgb[i + w] = tag;
         }
      }
      //DrwParam filter = DrwParamFilter.getFilterSmoothStep();
      //DrwParamFilter.applyColorFilter(filter, rgb, 0, w, h);

   }

   /**
    * How to manage the cache? look at DrwParam
    * Pixels according to colors stored in the Figure Definition
    * Or Based on a Function
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawPixelsLegacy(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      //before creating an array (expensive)
      //check if ByteObject p has a link to a free to use cached array?
      int[] rgb = new int[w * h];
      int index = 0;
      int length = p.getValue(IBOFigPixels.FIG_PIXEL_OFFSET_07_LENGTH_H2, 2);
      if (length <= 0)
         length = 1;
      boolean randomLength = p.hasFlag(IBOFigPixels.FIG_PIXEL_OFFSET_01_FLAG, IBOFigPixels.FIG_PIXEL_FLAG_1_RANDOM_SIZE);
      boolean randomColor = p.hasFlag(IBOFigPixels.FIG_PIXEL_OFFSET_01_FLAG, IBOFigPixels.FIG_PIXEL_FLAG_2_RANDOM_COLOR);
      int[] colors = p.getValues(IBOFigPixels.FIG_PIXEL_OFFSET_04_COLORSX);
      int colorIndex = 0;
      int maxc = colors.length;
      if (maxc == 0) {
         throw new IllegalArgumentException();
      }
      int add = length;
      boolean exit = false;
      int total = h * w;
      int seed = p.get4(IBOFigPixels.FIG_PIXEL_OFFSET_03_SEED4);
      Random r = drc.getUC().getRandom(seed);
      while (index < total) {
         int color = 0;
         if (randomColor) {
            color = colors[r.nextInt(maxc)];
         } else {
            colorIndex = ((colorIndex + 1) % maxc) + 1;
            color = colors[r.nextInt(colorIndex)];
         }
         if (randomLength) {
            add = r.nextInt(length + 1);
            if (add == 0)
               add = 1;
         }
         for (int k = 0; k < add; k++) {
            rgb[index] = color;
            index++;
            if (index >= rgb.length) {
               index = 0;
               exit = true;
            }
         }
         if (exit)
            break;
      }
      g.drawRGB(rgb, 0, w, x, y, w, h, true);

   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param width
    * @param height
    * @param sizeBorder if different than zero number of rectangle lines to be drawn.
    * @param arcw
    * @param arch
    * @param primaryColor
    * @param grad
    */
   public void drawRectangle(GraphicsX g, int x, int y, int width, int height, ByteObject rect, int sizeBorder, int arcw, int arch, int primaryColor, ByteObject grad) {
      if (sizeBorder != 0) {
         drawRectangleGradientBorder(g, x, y, width - 1, height - 1, rect, arcw, arch, primaryColor, sizeBorder, grad);
      } else {
         drawRectangleGradient(g, x, y, width, height, rect, arcw, arch, primaryColor, grad);
      }
   }

   public void drawRectangleGradient(GraphicsX g, int x, int y, int width, int height, ByteObject rect, int color, int arcw, int arch, int fill, ByteObject grad) {
      if (fill != 0) {
         drawRectangleGradientBorder(g, x, y, width - 1, height - 1, rect, arcw, arch, color, fill, grad);
      } else {
         drawRectangleGradient(g, x, y, width, height, rect, arcw, arch, color, grad);
      }
   }

   public int getRectGradSize(int width, int height, int arcw, int arch, int type) {
      int size = 0; //number of pixel steps
      switch (type) {
         case ITechGradient.GRADIENT_TYPE_RECT_00_SQUARE:
            size = Math.min(height, width) / 2;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_02_VERT:
            size = height;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_01_HORIZ:
            size = width;
            size -= arch;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_03_TOPLEFT:
         case ITechGradient.GRADIENT_TYPE_RECT_04_TOPRIGHT:
         case ITechGradient.GRADIENT_TYPE_RECT_05_BOTLEFT:
         case ITechGradient.GRADIENT_TYPE_RECT_06_BOTRIGHT:
            size = Math.min(height, width);
            size -= Math.max(arch, arcw);
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_07_L_TOP:
            size = width / 2;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_08_L_BOT:
            size = width / 2;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_09_L_LEFT:
            size = height / 2;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_10_L_RIGHT:
            size = height / 2;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_13_L_THIN_LEFT:
            size = height / 2;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_12_L_THIN_BOT:
            size = height / 2;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_15_PIC_TOP_LEFT:
         case ITechGradient.GRADIENT_TYPE_RECT_16_PIC_TOP_MID:
         case ITechGradient.GRADIENT_TYPE_RECT_17_PIC_TOP_RIGHT:
         case ITechGradient.GRADIENT_TYPE_RECT_18_PIC_MID_RIGHT:
         case ITechGradient.GRADIENT_TYPE_RECT_19_PIC_BOT_RIGHT:
         case ITechGradient.GRADIENT_TYPE_RECT_20_PIC_BOT_MID:
         case ITechGradient.GRADIENT_TYPE_RECT_21_PIC_BOT_LEFT:
         case ITechGradient.GRADIENT_TYPE_RECT_22_PIC_MID_LEFT:
            if (arcw == 0 && arch == 0) {
               size = Math.min(height, width) / 2;
            } else {
               size = Math.min(height - arch, width - arcw) / 2;
            }
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_27_:
            size = 8;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_28_:
            size = Math.min(height, width);
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_29_TRIG_:
         case ITechGradient.GRADIENT_TYPE_RECT_30_TRIG:
         case ITechGradient.GRADIENT_TYPE_RECT_31_:
         case ITechGradient.GRADIENT_TYPE_RECT_32_:
         case ITechGradient.GRADIENT_TYPE_RECT_33_:
         case ITechGradient.GRADIENT_TYPE_RECT_34_:
         case ITechGradient.GRADIENT_TYPE_RECT_35_:
         case ITechGradient.GRADIENT_TYPE_RECT_36_:
         case ITechGradient.GRADIENT_TYPE_RECT_37_:
         case ITechGradient.GRADIENT_TYPE_RECT_38_:
            size = Math.min(height, width);
            break;
         default:
            if (arcw == 0 && arch == 0) {
               size = Math.min(height, width) / 2;
            } else {
               size = Math.min(height, width) / 2;
            }
            break;
      }
      return size;
   }

   public void drawRectangleGradient(GraphicsX g, int x, int y, int w, int h, ByteObject rect, int arcw, int arch, int color, ByteObject grad) {
      int gradSize = rect.get1(FIG_RECTANGLE_OFFSET_5_SIZE_GRAD1);
      if (gradSize == 0) {
         int type = grad.get1(IBOGradient.GRADIENT_OFFSET_07_TYPE1);
         gradSize = getRectGradSize(w, h, arcw, arch, type); //number of iteration
      }
      ByteObject artifact = null;
      if (rect.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_8_ARTIFACT)) {
         artifact = rect.getSubFirst(IBOTypesDrawX.TYPE_DRWX_02_FIG_ARTIFACT);
      }
      drawRectangleGradientSwitch(g, x, y, w, h, artifact, arcw, arch, color, gradSize, grad);
   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param arcw
    * @param arch
    * @param color primary color
    * @param end
    * @param grad non null definition of a color gradient.
    */
   public void drawRectangleGradientSwitch(GraphicsX g, int x, int y, int w, int h, ByteObject artifact, int arcw, int arch, int color, int gradSize, ByteObject grad) {
      int type = grad.get1(IBOGradient.GRADIENT_OFFSET_07_TYPE1);

      //figure overrides gradient artifact
      if (artifact == null) {
         if (grad.hasFlag(IBOGradient.GRADIENT_OFFSET_01_FLAG, IBOGradient.GRADIENT_FLAG_7_ARTIFACTS)) {
            artifact = grad.getSubFirst(IBOTypesDrawX.TYPE_DRWX_02_FIG_ARTIFACT);
         }
      }
      if (artifact != null) {
         g.clipSet(x, y, w, h);
      }
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      fillArea(g, x, y, w, h, grad, ci);
      while ((count = ci.iteratePixelCount(g)) != -1) {
         int countX2 = count * 2;
         int countD2 = count / 2;
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_RECT_00_SQUARE:
               g.fillRoundRect(x + count, y + count, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_01_HORIZ:
               //g.drawLine(x + count, y, x + count, y + height - 1);
               g.fillRoundRect(x + count, y, w - count, h, arcw, arch);
               if (artifact != null) {
                  drawArtifactRectHoriz(g, ci.currentColor, x + count, y, w - count, h, artifact, arcw, arch);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_02_VERT:
               //g.drawLine(x, y + count, x + width - 1, y + count);
               g.fillRoundRect(x, y + count, w, h - count, arcw, arch);
               if (artifact != null) {
                  drawArtifactRectHoriz(g, ci.currentColor, x, y + count, w, h - count, artifact, arcw, arch);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_03_TOPLEFT:
               g.fillRoundRect(x, y, w - count, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_04_TOPRIGHT:
               g.fillRoundRect(x + count, y, w - count, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_05_BOTLEFT:
               g.fillRoundRect(x, y + count, w - count, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_06_BOTRIGHT:
               g.fillRoundRect(x + count, y + count, w - count, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_07_L_TOP:
               g.fillRoundRect(x + count, y, w - countX2, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_08_L_BOT:
               g.fillRoundRect(x + count, y + count, w - countX2, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_09_L_LEFT:
               g.fillRoundRect(x, y + count, w - count, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_10_L_RIGHT:
               g.fillRoundRect(x + count, y + count, w - count, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_11_L_THIN_TOP:
               g.fillRoundRect(x + countD2, y, w - count, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_12_L_THIN_BOT:
               g.fillRoundRect(x + countD2, y + count, w - count, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_13_L_THIN_LEFT:
               g.fillRoundRect(x, y + countD2, w - count, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_14_L_THIN_RIGHT:
               g.fillRoundRect(x + count, y + countD2, w - count, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_15_PIC_TOP_LEFT:
               g.fillRoundRect(x + countD2, y + countD2, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_16_PIC_TOP_MID:
               g.fillRoundRect(x + count, y + countD2, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_17_PIC_TOP_RIGHT:
               g.fillRoundRect(x + count + countD2, y + countD2, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_18_PIC_MID_RIGHT:
               g.fillRoundRect(x + count + countD2, y + count, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_19_PIC_BOT_RIGHT:
               g.fillRoundRect(x + countD2 + count, y + countD2 + count, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_20_PIC_BOT_MID:
               g.fillRoundRect(x + count, y + count + countD2, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_21_PIC_BOT_LEFT:
               g.fillRoundRect(x + countD2, y + count + countD2, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_22_PIC_MID_LEFT:
               g.fillRoundRect(x + countD2, y + count, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_23_WIN_TOP:
               g.fillRoundRect(x + countD2, y + countD2, w - count, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_24_WIN_BOT:
               g.fillRoundRect(x + countD2, y + count + countD2, w - count, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_25_WIN_LEFT:
               g.fillRoundRect(x + countD2, y + countD2, w - countX2, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_26_WIN_RIGHT:
               g.fillRoundRect(x + count + countD2, y + countD2, w - countX2, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_27_:
               int countm = count % 4;
               int extra = 8;
               int countd4 = (count / 4) * extra;
               int countd2x = (count / 2) * extra;

               int baseC = countd4 * extra;
               int baseCReduced = countd4 + extra;
               int areaReducer = countd4 * extra;
               int areaReducerEx = areaReducer + extra;
               int countReduced2 = areaReducer + extra + extra;

               if (countm == 0) {
                  g.fillRoundRect(x + baseC, y + baseC, w - areaReducer, h - areaReducer, arcw, arch);
               } else if (countm == 1) {
                  g.fillRoundRect(x + baseC, y + baseC, w - areaReducerEx, h - areaReducer, arcw, arch);
               } else if (countm == 2) {
                  g.fillRoundRect(x + baseC, y + baseC, w - areaReducerEx, h - areaReducerEx, arcw, arch);
               } else {
                  g.fillRoundRect(x + baseCReduced, y + baseC, w - countReduced2, h - areaReducerEx, arcw, arch);
               }
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_28_:
               int x1 = x + w / 2;
               int y1 = y + h / 2;

               int x2 = x + count;
               int y2 = y;
               int x3 = x + count + 1;
               int y3 = y;
               g.fillTriangle(x1, y1, x2, y2, x3, y3);

               x2 = x + w;
               x3 = x + w;
               y2 = y + h - count;
               y3 = y + h - count - 1;
               g.fillTriangle(x1, y1, x2, y2, x3, y3);

               y2 = y + h;
               y3 = y + h;
               x2 = x + count;
               x3 = x + count + 1;
               g.fillTriangle(x1, y1, x2, y2, x3, y3);

               x2 = x;
               x3 = x;
               y2 = y + h - count;
               y3 = y + h - count - 1;
               g.fillTriangle(x1, y1, x2, y2, x3, y3);

               break;
            case ITechGradient.GRADIENT_TYPE_RECT_29_TRIG_:
               x1 = x;
               y1 = y;
               x2 = x + w - count;
               y2 = y;
               x3 = x;
               y3 = y + h - count;
               g.fillTriangle(x1, y1, x2, y2, x3, y3);
               x1 = x + w;
               y1 = y + h;
               x2 = x + w - count;
               y2 = y;
               x3 = x;
               y3 = y + h - count;
               g.fillTriangle(x1, y1, x2, y2, x3, y3);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_30_TRIG:
            case ITechGradient.GRADIENT_TYPE_RECT_31_:
            case ITechGradient.GRADIENT_TYPE_RECT_32_:
            case ITechGradient.GRADIENT_TYPE_RECT_33_:
            case ITechGradient.GRADIENT_TYPE_RECT_34_:
            case ITechGradient.GRADIENT_TYPE_RECT_35_:
            case ITechGradient.GRADIENT_TYPE_RECT_36_:
            case ITechGradient.GRADIENT_TYPE_RECT_37_:
            case ITechGradient.GRADIENT_TYPE_RECT_38_:
               drawRectangleGradientTrig(g, x, y, w, h, arcw, arch, color, gradSize, count, type);
               break;
            default:
               throw new IllegalArgumentException();
         }
      }
      if(artifact != null) {
         g.clipReset();
      }
   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param width
    * @param height
    * @param arcw
    * @param arch
    * @param color
    * @param gradSize
    * @param grad
    */
   public void drawRectangleGradientBorder(GraphicsX g, int x, int y, int width, int height, ByteObject rect, int arcw, int arch, int color, int gradSize, ByteObject grad) {
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      while ((count = ci.iteratePixelCount(g)) != -1) {
         int countX2 = count * 2;
         g.drawRoundRect(x + count, y + count, width - countX2, height - countX2, arcw, arch);
      }
   }

   public void drawRectangleGradientTrig(GraphicsX g, int x, int y, int w, int h, int arcw, int arch, int color, int gradSize, int count, int type) {
      int x1 = x;
      int y1 = y;
      int x2 = 0;
      int y2 = y;
      int x3 = x;
      int y3 = 0;

      switch (type) {
         case ITechGradient.GRADIENT_TYPE_RECT_30_TRIG:
            x1 = x + w;
            y1 = y;
            x2 = x + w;
            y2 = y + h;
            x3 = x + count;
            y3 = y + count;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
            x1 = x;
            y1 = y + h;
            x2 = x + w;
            y2 = y + h;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);

            break;
         case ITechGradient.GRADIENT_TYPE_RECT_31_:
            x1 = x + count;
            y1 = y;
            x2 = x + w;
            y2 = y;
            x3 = x + w - count;
            y3 = y + h - count;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_32_:
            x1 = x;
            y1 = y;
            x2 = x;
            y2 = y + h;
            x3 = x + w - count;
            y3 = y + h - count;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
            x1 = x;
            y1 = y;
            x2 = x + w;
            y2 = y;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_33_:
            x1 = x + w;
            y1 = y;

            x2 = x;
            y2 = y + count;

            x3 = x + w - count;
            y3 = y + h;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_34_:
            x1 = x;
            y1 = y;

            x2 = x + count;
            y2 = y + h;

            x3 = x + w;
            y3 = y + count;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);

            break;
         case ITechGradient.GRADIENT_TYPE_RECT_35_:
            x1 = x + w;
            y1 = y + h;

            x2 = x + w - count;
            y2 = y;

            x3 = x;
            y3 = y + h - count;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_36_:
            x1 = x;
            y1 = y + h;

            x2 = x + count;
            y2 = y;

            x3 = x + w;
            y3 = y + h - count;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_37_:
            x1 = x;
            y1 = y;
            x2 = x + w - count;
            y2 = y;
            x3 = x;
            y3 = y + h - count;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
            x1 = x + w;
            y1 = y + h;
            x2 = x + w;
            y2 = y + count;
            x3 = x + count;
            y3 = y + h;
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
            break;
         default:
            break;
      }

   }

   private void drawRectangleSingleColor(GraphicsX g, int x, int y, int w, int h, ByteObject p, int color, int arcw, int arch, int sizeFill) {
      if (sizeFill != 0) {
         //draw method =>  it with d
         if (p.hasFlag(FIG_RECTANGLE_OFFSET_1_FLAG, FIG_RECTANGLE_FLAG_2_ROUND_INSIDE)) {
            //color = 0xFFFFFF;
            g.setColor(color);
            g.fillRoundRect(x, y, w, h, arcw, arch);
            ByteObject colori = p.getSubFirst(IBOTypesDrawX.TYPE_002_LIT_INT);
            if (colori == null) {
               g.setColor(0xFFFFFF);
            } else {
               int color_1 = boc.getLitteralIntOperator().getIntValueFromBO(p);
               g.setColor(color_1);
               //SystemLog.printBridge("#DrwParamFig#drawFigRectangle " + DrawUtilz.debugColor(color) + " " + DrawUtilz.debugColor(color_1) + " sizeFill=" + sizeFill);
            }
            g.fillRoundRect(x + sizeFill, y + sizeFill, w - sizeFill * 2, h - sizeFill * 2, arcw, arch);
         } else {
            drawFigRectangleShape(g, x, y, w - 1, h - 1, color, sizeFill, arcw, arch);
         }
      } else {
         if (p.hasFlag(FIG_RECTANGLE_OFFSET_1_FLAG, FIG_RECTANGLE_FLAG_1_ROUND)) {
            g.setColor(color);
            g.fillRoundRect(x, y, w, h, arcw, arch);
         } else {
            boolean ignoreAlpha = p.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAGP_5_IGNORE_ALPHA);
            if (ignoreAlpha) {
               color = (255 << 24) + (color & 0xFFFFFF);
            }
            //fill method. supports alpha. must be disabled
            drawRectangleSingleColor(g, x, y, w, h, color);
         }
      }
   }

   /**
    * Draws a sharp edged rectangular area with a color. alpha channel of color is implemented
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param color
    */
   public void drawRectangleSingleColor(GraphicsX g, int x, int y, int w, int h, int color) {
      int alpha = (color >> 24) & 0xFF;
      if (alpha == 0 || alpha == 255) {
         //we use the graphics alpha settings.
         g.setColor(color);
         g.fillRect(x, y, w, h);
      } else {
         //partially transparent => 
         if (g.isVirgin()) {
            //to prevent the use of a secondary buffer, when the g has an RGB array
            // with empty background color spacein the cases of empty graphicsx in the area
            //check if buffer has a RGB array with background pixels
            RgbImage ri = g.getBufferRegion(x, y, w, h, true);
            ri.fill(color);
         } else {
            //creates a RgbImage in rgb mode filled with that color.
            RgbImage mg = drc.getCache().create(w, h, color, true);
            //draw is asap.
            mg.draw(g, x, y);
            //dispose image for security
            mg.dispose();
         }
      }
   }

   public void drawShapeRectangle(GraphicsX g, int x, int y, int w, int h, int color) {
      g.setColor(color);
      g.fillRect(x, y, w, h);
   }

   public void drawShapeTriangle(GraphicsX g, int x, int y, int w, int h, int color) {
      g.setColor(color);

   }

   /**
    * Draws the super line figure
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   public void drawSuperLines(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int sepsize = p.getValue(IBOFigSuperLines.FIG_SL_OFFSET_4SEPARATION2, 2);
      int linesize = p.getValue(IBOFigSuperLines.FIG_SL_OFFSET_2LINE_SIZE1, 1);
      int color = p.get4(FIG__OFFSET_06_COLOR4);
      boolean horiz = p.hasFlag(IBOFigSuperLines.FIG_SL_OFFSET_1FLAG, IBOFigSuperLines.FIG_SL_FLAG_3HORIZ);
      g.setColor(color);
      int numLines = p.getValue(IBOFigSuperLines.FIG_SL_OFFSET_3REPEAT2, 2);
      boolean isIFirst = p.hasFlag(IBOFigSuperLines.FIG_SL_OFFSET_1FLAG, IBOFigSuperLines.FIG_SL_FLAG_7IGNORE_FIRST);
      boolean isILast = p.hasFlag(IBOFigSuperLines.FIG_SL_OFFSET_1FLAG, IBOFigSuperLines.FIG_SL_FLAG_8IGNORE_LAST);
      ByteObject grad = p.getSubFirst(IBOTypesBOC.TYPE_038_GRADIENT);
      int count = 0;
      if (horiz) {
         if (p.hasFlag(IBOFigSuperLines.FIG_SL_OFFSET_1FLAG, IBOFigSuperLines.FIG_SL_FLAG_6FILL)) {
            int end = y + h;
            while (y < end) {
               if (!(isIFirst && count == 0)) {
                  for (int j = 0; j < linesize; j++) {
                     g.drawLine(x, y, x + w - 1, y);
                     y++;
                  }
               }
               y += sepsize;
               count++;
            }
         } else {
            for (int i = 0; i < numLines; i++) {
               for (int j = 0; j < linesize; j++) {
                  g.drawLine(x, y, x + w - 1, y);
                  y++;
               }
               y += sepsize;
            }
         }
      } else {
         if (p.hasFlag(IBOFigSuperLines.FIG_SL_OFFSET_1FLAG, IBOFigSuperLines.FIG_SL_FLAG_6FILL)) {
            int end = x + w;
            while (x < end) {
               if (!(isIFirst && count == 0)) {
                  for (int j = 0; j < linesize; j++) {
                     g.drawLine(x, y, x, y + h - 1);
                     x++;
                  }
               }
               x += sepsize;
               count++;
            }
         } else {
            for (int i = 0; i < numLines; i++) {
               for (int j = 0; j < linesize; j++) {
                  g.drawLine(x, y, x, y + h - 1);
                  x++;
               }
               x += sepsize;
            }
         }
      }
   }

   public void drawTrefle(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int color = p.get4(FIG__OFFSET_06_COLOR4);
      //2 bytes sizer
      int base = p.get2(IBOFigCardsCPCTrefle.FIG_TREFLE_OFFSET_2_BASE2);
      if (base == 0) {
         base = w / 4;
      }
      int leafSize = p.get2(IBOFigCardsCPCTrefle.FIG_TREFLE_OFFSET_3_LEAF2);
      if (leafSize == 0) {
         leafSize = w / 2;
      }
      int w2 = w / 2;
      int h2 = h / 2;
      int sizeBase = base;
      int rayon = leafSize / 2;
      int midX = x + w2;
      int midY = y + h2;
      int bot = y + h;
      g.setColor(color);
      int x1 = midX - sizeBase / 2;
      g.fillTriangle(x1, bot, midX, midY - rayon, x1 + sizeBase, bot);

      g.fillArc(midX - leafSize, midY - rayon, leafSize, leafSize, 0, 360);
      g.fillArc(midX, midY - rayon, leafSize, leafSize, 0, 360);

      g.fillArc(midX - rayon, midY - leafSize, leafSize, leafSize, 0, 360);
   }

   public void fillArea(GraphicsX g, int x, int y, int w, int h, ByteObject grad, ColorIterator ci) {
      if (grad.hasFlag(IBOGradient.GRADIENT_OFFSET_04_FLAGX1, IBOGradient.GRADIENT_FLAGX_1_FILL_AREA)) {
         g.setColor(ci.getCurrentColor());
         g.fillRect(x, y, w, h);
      }
   }

   public DrawerString getDrawerString() {
      return stringDrawer;
   }

   public DrawerTriangle getDrawerTriangle() {
      return drawerTriangle;
   }

   /**
    * Gets an image of the figure parameters in the rectangle [w,h] <br>
    * For performance reasons, one might want to draw figure of primitives using given pseudo background color
    * 
    * For non RGB figure {@link IBOFigure#FIG_FLAGP_1_RGB}, image background is 
    * <br>either black transparent
    * <br> white opaque
    * <br> given color
    * <br>
    * For figures with flag {@link IBOFigure#FIG_FLAGP_1_RGB}, background is transparent black
    * 
    * if just switch is true, the figure is drawn without the controls of filters, mask etc.
    * @param fig
    * @param w
    * @param h
    * @param justSwitch draw just the figure without the control code of Filters/Mask
    * @param whiteopaque ignored if figure is full RGB. else if true, create a primitive only mutable image
    * @return image is in Rgb mode with black transparent pixels where the figure has not drawn
    */
   public RgbImage getFigImage(ByteObject fig, int w, int h, boolean justSwitch, boolean whiteopaque, int bgColor) {
      int mode = GraphicsX.MODE_1_IMAGE;
      RgbImage figImg = null;
      if (fig.hasFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_1_RGB)) {
         figImg = drc.getCache().create(w, h, bgColor);
      } else {
         if (whiteopaque) {
            figImg = drc.getCache().createPrimitiveRgb(w, h, bgColor);
         } else {
            figImg = drc.getCache().createImage(w, h, bgColor);
         }
      }
      //SystemLog.printDraw(figImg.debugFull("\n"));
      //get the graphics object of the image to draw the figure on it
      GraphicsX figGraphics = figImg.getGraphicsX(mode);
      if (justSwitch) {
         paintFigureSwitch(figGraphics, 0, 0, w, h, fig);
      } else {
         paintFigure(figGraphics, 0, 0, w, h, fig);
         //figGraphics.flush();
      }
      figImg.disposeGraphics();
      return figImg;
   }

   public RgbImage getFigImageMutable(ByteObject fig, int w, int h, int color) {
      return getFigImage(fig, w, h, false, true, color);
   }

   /**
    * Returns the image drawn on a white background
    * @param fig
    * @param w
    * @param h
    * @return
    */
   public RgbImage getFigImageNonNull(ByteObject fig, int w, int h) {
      return getFigImageNonNull(fig, w, h, false, true, 0);
   }

   public RgbImage getFigImageNonNull(ByteObject fig, int w, int h, boolean justSwitch, boolean whiteopaque, int bgColor) {
      if (w <= 0 || h <= 0) {
         return drc.getCache().NULL_IMAGE;
      }
      return getFigImage(fig, w, h, justSwitch, whiteopaque, bgColor);
   }

   /**
    * Force the drawing of the figure on an {@link IImage} layer.
    * @param fig
    * @param w
    * @param h
    * @param justSwitch
    * @param bgColor
    * @return
    */
   public RgbImage getFigImagePrimitve(ByteObject fig, int w, int h, boolean justSwitch, int bgColor) {
      RgbImage figImg = drc.getCache().createPrimitiveRgb(w, h, bgColor);
      GraphicsX figGraphics = figImg.getGraphicsX(GraphicsX.MODE_1_IMAGE);
      if (justSwitch) {
         paintFigureSwitch(figGraphics, 0, 0, w, h, fig);
      } else {
         paintFigure(figGraphics, 0, 0, w, h, fig);
      }
      figImg.disposeGraphics();
      return figImg;
   }

   /**
    * Draw figure and its artifacts (mark, filters,etc) on fully transparent black background.
    * @param fig
    * @param w
    * @param h
    * @return
    */
   public RgbImage getFigImageTrans(ByteObject fig, int w, int h) {
      return getFigImage(fig, w, h, false, false, 0);
   }

   public int getMergeColor(ByteObject root, ByteObject merge, ByteObject mm) {
      int rcolor = root.get4(FIG__OFFSET_06_COLOR4);
      if (mm.hasFlag(MERGE_MASK_OFFSET_05_VALUES1, MERGE_MASK_FLAG5_2)) {
         rcolor = merge.get4(FIG__OFFSET_06_COLOR4);
      }
      return rcolor;
   }

   /**
    * Tells if that Figure ByteObject will draw translucent data
    * @param p
    * @return
    */
   public boolean hasFigTransparentPixels(ByteObject p) {
      p.checkType(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
      return false;
   }

   /**
    * Merges the two figures definition into a new Definition.
    * <br>
    * <br>
    * A change of type implies a completely different figure
    * <br>
    * <br>
    * Action will clone root figure and create a new one by applying a function on a pointer.
    * <br>
    * 
    * @param root FIGURE TYPE
    * @param merge FIGURE TYPE
    * @return
    */
   public ByteObject merge_Figure(ByteObject root, ByteObject merge) {
      if (root == null) {
         return merge;
      }
      if (merge == null) {
         return root;
      }
      if (merge.getType() != IBOTypesDrawX.TYPE_DRWX_00_FIGURE) {
         throw new IllegalArgumentException();
      }

      MergeFactory mm = boc.getMergeFactory();
      ByteObject mergeMask = merge.getSubFirst(IBOTypesBOC.TYPE_011_MERGE_MASK);
      boolean b = merge.hasFlag(FIG__OFFSET_07_FLAGZ1, FIG_FLAGZ_2_MERGE_TRANS);
      if (mergeMask == null) {
         //figure is opaque
         return merge;
      }
      int typeRoot = root.get1(FIG__OFFSET_01_TYPE1);
      int typeMerge = merge.get1(FIG__OFFSET_01_TYPE1);

      //3 cases : sames type, different types, merge type is transparent when merge flag is true

      //when types are different, merge is returned with transparent figure fields coming from root

      //when merge type is unknown, merge figure values are transfered to root

      //when same type, regular merge

      ByteObject newFigure = null;

      int mainFigureFlag = mm.mergeFlag(root, merge, mergeMask, FIG__OFFSET_02_FLAG, MERGE_MASK_OFFSET_01_FLAG1);
      int figurePerfFlags = mm.mergeFlag(root, merge, mergeMask, FIG__OFFSET_03_FLAGP, MERGE_MASK_OFFSET_02_FLAGX1);

      if (merge.hasFlag(FIG__OFFSET_07_FLAGZ1, FIG_FLAGZ_4_LAYOUT)) {
         //we have to get those layout figures first and merge them
      }

      //types. figure type can be transparent when we don't know the root type.
      //figure type is by default opaque and transparent when flag is set
      if (mergeMask.hasFlag(MERGE_OFFSET_FIG_TYPE, MERGE_FLAG_FIG_TYPE)) {
         typeRoot = merge.get1(FIG__OFFSET_01_TYPE1);
         //we clone root and set what is opaque
         //when this happens, the figure does a reverse stamping by only taking the main color and figure
         //attributes (Filter,Gradient,Mask)
         newFigure = (ByteObject) root.clone();
      } else {
         if (typeRoot == typeMerge) {
            switch (typeRoot) {
               case ITechFigure.FIG_TYPE_10_STRING:
                  newFigure = mergeFigString(root, merge, mergeMask);
                  break;
               case ITechFigure.FIG_TYPE_01_RECTANGLE:
                  newFigure = mergeFigRectangle(root, merge, mergeMask);
                  break;
               case ITechFigure.FIG_TYPE_02_BORDER:
                  newFigure = mergeFigBorder(root, merge, mergeMask);
                  break;
               default:
                  //if figure not defined in this module, asks bo manager for subfigure merging
                  throw new RuntimeException("Not implemented Merge Method for Figure " + ToStringStaticDrawx.toStringFigType(typeRoot));
            }
            mergeFigureRegular(newFigure, root, merge, mergeMask);
         } else {
            //types are different merge is returned with transparent figure fields coming from root
            newFigure = (ByteObject) merge.clone();
            //root replaces merge on purpose. we keep using the merge mask from merge
            mergeFigureRegular(newFigure, root, root, mergeMask);
         }
      }

      if (mergeMask.hasFlag(MERGE_OFFSET_FIG_COLOR, MERGE_FLAG_FIG_COLOR)) {
         merge.set4To(newFigure, FIG__OFFSET_06_COLOR4);
      } else {
         root.set4To(newFigure, FIG__OFFSET_06_COLOR4);
      }
      
      
      return newFigure;
   }

   /**
    * Replace or keep or what ? nothing yet
    * @param root
    * @param merge
    * @return
    */
   public ByteObject merge_Figure_Struct(ByteObject root, ByteObject merge) {
      if (root == null) {
         return merge;
      }
      if (merge == null) {
         return root;
      }
      if (merge.getType() != IBOTypesDrawX.TYPE_DRWX_01_FIG_SUB_STRUCT) {
         throw new IllegalArgumentException();
      }
      return merge;

   }

   public ByteObject mergeFigBorder(ByteObject root, ByteObject merge, ByteObject mm) {

      int arcw = root.get1(FIG_RECTANGLE_OFFSET_2_SIZE_ARCW1);
      int arch = root.get1(FIG_RECTANGLE_OFFSET_3_SIZE_ARCH1);
      int size = root.get1(FIG_RECTANGLE_OFFSET_4_SIZE_FILL1);
      if (mm.hasFlag(MERGE_MASK_OFFSET_06_VALUES1, MERGE_MASK_FLAG5_1)) {
         arcw = merge.get1(FIG_RECTANGLE_OFFSET_2_SIZE_ARCW1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_06_VALUES1, MERGE_MASK_FLAG5_2)) {
         arch = merge.get1(FIG_RECTANGLE_OFFSET_3_SIZE_ARCH1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_06_VALUES1, MERGE_MASK_FLAG5_3)) {
         size = merge.get1(FIG_RECTANGLE_OFFSET_4_SIZE_FILL1);
      }
      return drc.getFigureFactory().getFigRect(0, arcw, arch, size, null, null, null, null);

   }

   /**
    * Specific to {@link IBOFigRectangle} values
    * @param root
    * @param merge
    * @param mm
    * @return
    */
   public ByteObject mergeFigRectangle(ByteObject root, ByteObject merge, ByteObject mm) {
      ByteObject figRectResult = drc.getFigureFactory().getFigRectMerge();
      ByteObject[] ar = new ByteObject[4];
      int[] arC = new int[2];
      mergeSizerIndexFlagged(figRectResult, root, mm, ITechMergeFigureRect.MERGE_OFFSET_SIZE_ARCW, ITechMergeFigureRect.MERGE_FLAG_SIZE_ARCW, merge, ar, arC, FIG_RECTANGLE_OFFSET_2_SIZE_ARCW1, FIG_RECTANGLE_OFFSET_1_FLAG, FIG_RECTANGLE_FLAG_5_ARCW_SIZER);
      mergeSizerIndexFlagged(figRectResult, root, mm, ITechMergeFigureRect.MERGE_OFFSET_SIZE_ARCH, ITechMergeFigureRect.MERGE_FLAG_SIZE_ARCH, merge, ar, arC, FIG_RECTANGLE_OFFSET_3_SIZE_ARCH1, FIG_RECTANGLE_OFFSET_1_FLAG, FIG_RECTANGLE_FLAG_6_ARCH_SIZER);
      mergeSizerIndexFlagged(figRectResult, root, mm, ITechMergeFigureRect.MERGE_OFFSET_SIZE_FILL, ITechMergeFigureRect.MERGE_FLAG_SIZE_FILL, merge, ar, arC, FIG_RECTANGLE_OFFSET_4_SIZE_FILL1, FIG_RECTANGLE_OFFSET_1_FLAG, FIG_RECTANGLE_FLAG_7_FILL_SIZER);
      mergeSizerIndexFlagged(figRectResult, root, mm, ITechMergeFigureRect.MERGE_OFFSET_SIZE_GRAD, ITechMergeFigureRect.MERGE_FLAG_SIZE_GRAD, merge, ar, arC, FIG_RECTANGLE_OFFSET_5_SIZE_GRAD1, FIG_RECTANGLE_OFFSET_1_FLAG, FIG_RECTANGLE_FLAG_8_GRAD_SIZER);

      figRectResult.setByteObjectsTrimmed(ar);
      return figRectResult;
   }

   /**
    * 
    * @param root
    * @param merge object on top being inprinted on root
    * @param mergeMask the {@link IBOMerge} definition.
    * @return
    */
   public ByteObject mergeFigString(ByteObject root, ByteObject merge, ByteObject mergeMask) {
      int rface = root.get1(FIG_STRING_OFFSET_03_FACE1);
      int rstyle = root.get1(FIG_STRING_OFFSET_04_STYLE1);
      int rsize = root.get1(FIG_STRING_OFFSET_05_SIZE1);

      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_06_VALUES1, MERGE_MASK_FLAG6_1)) {
         rface = merge.get1(FIG_STRING_OFFSET_03_FACE1);
      }
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_06_VALUES1, MERGE_MASK_FLAG6_2)) {
         rstyle = merge.get1(FIG_STRING_OFFSET_04_STYLE1);
      }
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_06_VALUES1, MERGE_MASK_FLAG6_3)) {
         rsize = merge.get1(FIG_STRING_OFFSET_05_SIZE1);
      }

      String str = null;
      if (root.hasFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_1_EXPLICIT)) {
         ByteObject raw = root.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
         str = boc.getLitteralStringOperator().getLitteralString(raw);
      }
      if (merge.hasFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_1_EXPLICIT)) {
         ByteObject raw = merge.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
         str = boc.getLitteralStringOperator().getLitteralString(raw);
      }

      ByteObject effects = drc.getStrAuxOperator().getSub(root, IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_4_FX);
      ByteObject mask = root.getSubFirst(IBOTypesDrawX.TYPE_DRWX_06_MASK);
      ByteObject scale = root.getSubFirst(IBOTypesDrawX.TYPE_DRWX_05_SCALE);
      if (root.hasFlag(FIG_STRING_OFFSET_02_FLAGX, FIG_STRING_FLAGX_2_DEFINED_FX)) {

      }
      int rcolor = getMergeColor(root, merge, mergeMask);
      return drc.getFigureFactory().getFigString(str, rface, rstyle, rsize, rcolor, effects, mask, scale);

   }

   private void mergeFigureFlagged(ByteObject newFigure, ByteObject root, ByteObject merge, ByteObject mergeMask, ByteObject[] ar, int[] arC, int type, int offsetFlag, int flag) {
      ByteObject gradRoot = root.getSubFirst(type);
      ByteObject gradMerge = null;
      if (merge.hasFlag(offsetFlag, flag)) {
         gradMerge = merge.getSubFirst(type);
      }
      BOModuleDrawx moduleDrawx = drc.getModuleDrawx();
      ByteObject gradResult = moduleDrawx.mergeCheck(gradRoot, gradMerge);
      if (gradResult != null) {
         newFigure.setFlag(offsetFlag, flag, true);
         int arIndex = arC[0]++;
         ar[arIndex] = gradResult;
      }
   }

   private void mergeFigureRegular(ByteObject newFigure, ByteObject root, ByteObject merge, ByteObject mergeMask) {
      if (mergeMask.hasFlag(MERGE_OFFSET_FIG_COLOR, MERGE_FLAG_FIG_COLOR)) {
         merge.set4To(newFigure, FIG__OFFSET_06_COLOR4);
      }

      ByteObject[] ar = new ByteObject[7];
      int[] arC = new int[2];

      mergeFigureFlagged(newFigure, root, merge, mergeMask, ar, arC, IBOTypesDrawX.TYPE_DRWX_03_BOX, FIG__OFFSET_02_FLAG, FIG_FLAG_1_BOX);
      mergeFigureFlagged(newFigure, root, merge, mergeMask, ar, arC, IBOTypesBOC.TYPE_038_GRADIENT, FIG__OFFSET_02_FLAG, FIG_FLAG_2_GRADIENT);
      mergeFigureFlagged(newFigure, root, merge, mergeMask, ar, arC, IBOTypesBOC.TYPE_007_LIT_ARRAY_INT, FIG__OFFSET_02_FLAG, FIG_FLAG_3_COLOR_ARRAY);
      mergeFigureFlagged(newFigure, root, merge, mergeMask, ar, arC, IBOTypesDrawX.TYPE_DRWX_06_MASK, FIG__OFFSET_02_FLAG, FIG_FLAG_4_MASK);
      mergeFigureFlagged(newFigure, root, merge, mergeMask, ar, arC, IBOTypesBOC.TYPE_040_COLOR_FILTER, FIG__OFFSET_02_FLAG, FIG_FLAG_5_FILTER);
      mergeFigureFlagged(newFigure, root, merge, mergeMask, ar, arC, IBOTypesBOC.TYPE_030_ANIM, FIG__OFFSET_02_FLAG, FIG_FLAG_6_ANIMATED);
      mergeFigureFlagged(newFigure, root, merge, mergeMask, ar, arC, IBOTypesDrawX.TYPE_DRWX_01_FIG_SUB_STRUCT, FIG__OFFSET_02_FLAG, FIG_FLAG_7_SUB_FIGURE);

      newFigure.addByteObjectsNullsTrimmed(ar);
   }

   public void mergeSizerIndexFlagged(ByteObject result, ByteObject root, ByteObject mm, int mmOffset, int mmFlag, ByteObject merge, ByteObject[] ar, int[] arC, int offsetValue, int offsetFlag, int flag) {
      ByteObject sizerMerged = null;
      ByteObject sizerMerge = null;
      ByteObject sizerRoot = null;
      if (root.hasFlag(offsetFlag, flag)) { //we have sizer
         int valueRoot = root.get1(offsetValue);
         sizerRoot = root.getSubAtIndex(valueRoot);
      }
      if (mm.hasFlag(mmOffset, mmFlag)) {
         int valueMerge = merge.get1(offsetValue);

         if (merge.hasFlag(offsetFlag, flag)) {
            sizerMerge = merge.getSubAtIndex(valueMerge);
         } else {
            //transfer the raw value
            result.set1(offsetValue, valueMerge);
         }
      }
      sizerMerged = drc.getLAC().getLayoutOperator().mergeSizer(sizerRoot, sizerMerge);
      if (sizerMerged != null) {
         int arIndex = arC[0]++;
         ar[arIndex] = sizerMerged;
         result.set1(offsetValue, arIndex);
      }
   }

   /**
    * Paint the figure defined by ByteObject in the area defined by rectangle x,y w,h. <br>
    * <br>
    * <b>Options</b>: <br>
    * <li>caching. when a figure is drawn, a signature is recorded. When a similar figure is drawn
    * in the draw cycle, the figure is flagged. During next cycle, the flagged figures are cached.
    * <li> However caching requires to deal with background transparent pixels.
    * <li>Figure transform relationships: all {@link IImage#TRANSFORM_1_FLIP_H_MIRROR_ROT180}
    * <li>Also link to Gradient. Gradient position 0 and 100 depending on Direction gives a give Transform.
    * <li>timing. Additional option that asks for a specific time threshold to be taken in order
    * for a figure to be cache elligible.
    * <li>+timing per primitive calls.
    * 
    * <br><br>
    * <b>Implementation</b>: <br>
    * The method behaves like a fillRect call, drawn pixels are fully enclosed in xywh. <br>
    * <br>
    * <b>Example</b>: a rectangle mesh figure will call drawRect(x,y,w-1,h-1);
    * <br><br>
    * @param g Graphical context of the draw.
    * @param x left coordinate
    * @param y top coordinate
    * @param w the number of pixels on which the figure may be painted horizontally
    * @param h the number of pixles on which the figure may be painted vertically
    * @param p The figure definition
   
    */
   public void paintFigure(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      //#debug
      g.toDLog().pDraw("area x=" + x + " y=" + y + "  w=" + w + ", h=" + h, p, FigureOperator.class, "paintFigure@line884", LVL_04_FINER, true);

      if (p == null || w <= 0 || h <= 0) {
         //there is nothing to draw
         return;
      }
      p.checkType(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
      if (p.hasFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_8_POSTPONE)) {
         g.postpone(x, y, w, h, p);
         return;
      }
      ByteObject filter = null;
      if (p.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_5_FILTER)) {
         filter = p.getSubOrder(IBOTypesBOC.TYPE_040_COLOR_FILTER, 0);
      }
      ByteObject mask = null;
      RgbImage rgbMask = null;
      RgbImageOperator rgbImageOperator = drc.getRgbImageOperator();
      if (p.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_4_MASK)) {
         mask = p.getSubFirst(IBOTypesDrawX.TYPE_DRWX_06_MASK);
         if (mask == null) {
            throw new IllegalArgumentException("Mask is null");
         }
         rgbMask = drc.getMaskOperator().createMaskedFigure(mask, w, h, p);
         if (filter != null) {
            rgbImageOperator.applyColorFilter(filter, rgbMask);
         }
         g.drawRgbImage(rgbMask, x, y);
         rgbMask.dispose();
      } else {
         if (filter != null) {
            //At best,  GraphicsX is Rgb Virgin and Figure is Rgb -> 0 buffer is created. Only SRC Blending is possible
            //At middle GraphicsX is Primitive and Figure is Rgb -> 1 buffer is created for figure: RGB
            //At worst, Figure is Primitive  -> 2 buffers are created. One for Image and one for Rgb filter : RGB_IMAGE
            boolean rgb = p.hasFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_1_RGB);
            GraphicsX gi = null;
            RgbImage buffer = null;
            if (rgb) {
               buffer = g.getVirginBuffer(x, y, w, h);
               if (buffer == null) {
                  buffer = drc.getCache().createRGB(w, h, 0);
                  gi = buffer.getGraphicsX(GraphicsX.MODE_2_RGB_IMAGE);
               } else {
                  gi = buffer.getGraphicsX(GraphicsX.MODE_3_RGB);
               }
            } else {
               buffer = drc.getCache().createRGB(w, h, 0);
               gi = buffer.getGraphicsX(GraphicsX.MODE_2_RGB_IMAGE);
            }
            //#debug
            gi.toStringSetName("FilterLayer");
            //SystemLog.pDraw(gi);
            //SystemLog.pDraw(buffer);
            paintFigureSwitch(gi, 0, 0, w, h, p);
            //SystemLog.pDraw(buffer.debugColors());
            //check if filter need extra
            if (filter.hasFlag(IBOFilter.FILTER_OFFSET_02_FLAG1, IBOFilter.FILTER_FLAG_8_EXTRA_BO)) {
               ByteObject figureExtra = filter.getSubFirst(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
               if (figureExtra != null) {
                  //we send a copy of the rgb on the same area

                  RgbImage buffere = drc.getCache().createRGB(w, h, 0);
                  GraphicsX gie = buffere.getGraphicsX(GraphicsX.MODE_2_RGB_IMAGE);
                  paintFigureSwitch(gie, 0, 0, w, h, figureExtra);

                  rgbImageOperator.applyColorFilter(filter, buffer, buffere);

               }
            } else {
               //the color Filter will query the background color of the RgbImage for applying any filter
               rgbImageOperator.applyColorFilter(filter, buffer);
            }
            //SystemLog.pDraw(buffer);
            //TODO white is kept. make pseudo alpha for mutable white image
            //SystemLog.pDraw(buffer.debugAlpha());
            buffer.setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, false);
            g.drawRgbImage(buffer, x, y); //blending buffer with the input Graphics
            buffer.dispose(); //tell the Application Memory Manager that this image is no longer used
            //pure primitive figure
            //however with pure primitive figure, if we have a color rgb filter we must
            //use

            //by default background color is opaque White
            //mutable image does not support transparency
            //RgbImage img = RgbImage.create(w, h);
            //GraphicsX gi = img.getGraphicsX();
            //paintFigurePrimitive(gi, 0, 0, w, h, p);
            //one way trip to the rgb mode and then we draw on the graphics
            //Mutable image pixels are always opaque
            //how to draw a line on a RgbImage in rgb mode with transparent pixels?
            //1:implement line drawing on Rgb int[] array
            //2: Convert to Primitive while keep old array,and a carefully choosen
            //pseudo transparent color. then draw the line
            //convert back to Rgb where each pixels is put back to its transparent value
            //by matching each other with the old array. the color of the line are left untouched
            //RgbImage rgbi = RgbImage.create(img);
            //DrwParam.applyColorFilter(filter, rgbi);
            //remove pseudo transparent color or keep it?
            //DrawUtilz.setAlpha(rgbi.getRgbData(), 0, rgbi.getRgbData().length, DrawUtilz.FULLY_OPAQUE_WHITE, 0);
            //rgbi.draw(g, x, y);
            //rgbi.dispose();
         } else {
            //pure primitive drawing
            paintFigureSwitch(g, x, y, w, h, p);
         }
      }
   }

   /**
    * Check if figure is directional. If it is not, ignore direction directive.
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param fig
    * @param dir0top
    */
   public void paintFigureDir(GraphicsX g, int x, int y, int w, int h, ByteObject fig, int dir0top) {
      // TODO Auto-generated method stub

   }

   void paintFigureSwitch(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      final int type = p.getValue(FIG__OFFSET_01_TYPE1, 1);
      switch (type) {
         case ITechFigure.FIG_TYPE_01_RECTANGLE:
            //no trans filter
            drawFigRectangle(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_03_TRIANGLE:
            drawerTriangle.drawFigTriangle(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_05_LINE:
            drawLine(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_02_BORDER:
            drawFigBorder(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_04_CHAR:
            stringDrawer.drawFigChar(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_06_LOSANGE:
            drawFigLosange(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_20_CROSS:
            drawCross(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_17_ARLEQUIN:
            drawFigArlequin(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_11_GRID:
            drawFigGrid(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_35_TESSON:
            drawFigTesson(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_12_ARROW:
            break;
         case ITechFigure.FIG_TYPE_08_GERMANCROSS:
            break;
         case ITechFigure.FIG_TYPE_07_ELLIPSE:
            drawFigEllipse(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_13_REPEATER:
            drawFigRepeater(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_16_SUPERLINES:
            drawSuperLines(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_09_PIXELS:
            drawPixels(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_10_STRING:
            stringDrawer.drawFigString(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_33_TREFLE:
            drawTrefle(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_30_COEUR:
            drawCoeur(g, x, y, w, h, p);
            break;
         default:
            throw new IllegalArgumentException("Unknown figure type " + type);
      }
      paintFigureSwitchSubFigures(g, x, y, w, h, p);
   }

   /**
    * Checks for sub figures and draw them sub figures are drawn according to the 
    * {@link LayouterCtx} of this {@link DrwCtx}
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param p
    */
   private void paintFigureSwitchSubFigures(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      if (p.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_7_SUB_FIGURE)) {
         //find sub figures which are sized
         ByteObject subs = p.getSubFirst(IBOTypesDrawX.TYPE_DRWX_01_FIG_SUB_STRUCT);
         ByteObject[] subFigures = subs.getSubs(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
         for (int i = 0; i < subFigures.length; i++) {
            ByteObject fig = subFigures[i];
            ByteObject anchorBox = fig.getSubFirst(IBOTypesDrawX.TYPE_DRWX_03_BOX);

            if (fig.getType() == IBOTypesDrawX.TYPE_DRWX_00_FIGURE && anchorBox != null) {
               int ha = anchorBox.get4(BOX_OFFSET_02_HORIZ_ALIGN4);
               int va = anchorBox.get4(BOX_OFFSET_03_VERTICAL_ALIGN4);

               BoxFactory boxFactory = drc.getBoxFactory();

               int figW = boxFactory.computeSizeW(anchorBox, w, h);
               int figH = boxFactory.computeSizeH(anchorBox, w, h);
               int figX = AnchorUtils.getXAlign(ha, x, w, figW);
               int figY = AnchorUtils.getYAlign(va, y, h, figH);
               paintFigure(g, figX, figY, figW, figH, fig);
            } else {
               throw new IllegalArgumentException();
            }
         }
      }
   }

   public void setFigAnchor(ByteObject fig, ByteObject anchor) {
      fig.setFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_1_BOX, true);
      fig.addSub(anchor);
   }

   /**
    * Adds an {@link ByteObject} animation to the Figure.
    * <br>
    * <br>
    * @param fig
    * @param anim Animation must be implicit
    */
   public void setFigAnimation(ByteObject fig, ByteObject anim) {
      fig.checkType(IBOTypesDrawX.TYPE_DRWX_00_FIGURE);
      anim.checkType(IBOTypesBOC.TYPE_030_ANIM);
      if (fig.hasFlag(IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_6_ANIMATED)) {
         //check the animations in the array remove
      } else {
         fig.setFlag(IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_6_ANIMATED, true);
      }
      fig.addByteObject(anim);
   }

   public void setFilter(ByteObject figure, ByteObject filter) {
      if (filter != null) {
         figure.addByteObjectUniqueType(filter);
         figure.setFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_5_FILTER, true);
      }
   }
}
