/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import java.util.Random;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.extra.MergeMaskFactory;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.drawx.src4.color.ColorIterator;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.BlendOp;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.factories.drawer.DrawerTriangle;
import pasa.cbentley.framework.drawx.src4.factories.drawer.DrawerString;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;
import pasa.cbentley.framework.drawx.src4.tech.ITechArtifact;
import pasa.cbentley.framework.drawx.src4.tech.ITechBox;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeMaskFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechRgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechTblr;
import pasa.cbentley.framework.drawx.src4.utils.AnchorUtils;
import pasa.cbentley.framework.drawx.src4.utils.ToStringStaticDraw;
import pasa.cbentley.layouter.src4.ctx.LayouterCtx;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;
import pasa.cbentley.layouter.src4.interfaces.IBOTypesLayout;
import pasa.cbentley.layouter.src4.tech.ITechLayout;

/**
 * Lambda expressions for figures.
 * <br>
 * {@link ByteObject} declaratively configures the drawing of the figures.
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class FigureOperator extends AbstractDrwOperator implements ITechBox {

   protected DrawerString   stringDrawer;

   protected DrawerTriangle drawerTriangle;

   public FigureOperator(DrwCtx drc) {
      super(drc);
      drawerTriangle = new DrawerTriangle(drc);
      stringDrawer = new DrawerString(drc);
   }

   public DrawerTriangle getDrawerTriangle() {
      return drawerTriangle;
   }

   public DrawerString getDrawerString() {
      return stringDrawer;
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
   public ByteObject mergeFigure(ByteObject root, ByteObject merge) {
      MergeMaskFactory mm = boc.getMergeMaskFactory();
      if (merge.getType() == IBOTypesDrw.TYPE_050_FIGURE) {
         ByteObject mergeMask = merge.getSubFirst(IBOTypesBOC.TYPE_011_MERGE_MASK);
         if(mergeMask == null) {
            //figure is opaque
            return merge;
         }
         int fig = root.get1(ITechFigure.FIG__OFFSET_01_TYPE1);
         int rcolor = root.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
         int mainFigureFlag = mm.mergeFlag(root, merge, mergeMask, ITechFigure.FIG__OFFSET_02_FLAG, MERGE_MASK_OFFSET_1FLAG1);
         int figurePerfFlags = mm.mergeFlag(root, merge, mergeMask, ITechFigure.FIG__OFFSET_03_FLAGP, MERGE_MASK_OFFSET_2FLAG1);

         if (mergeMask.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_1)) {
            fig = merge.get1(ITechFigure.FIG__OFFSET_01_TYPE1);
            //when this happens, the figure does a reverse stamping by only taking the main color and figure
            //attributes (Filter,Gradient,Mask)
            ByteObject newFigure = (ByteObject) merge.clone();
            newFigure.setValue(ITechFigure.FIG__OFFSET_06_COLOR4, rcolor, 4);
            newFigure.setValue(ITechFigure.FIG__OFFSET_02_FLAG, mainFigureFlag, 1);
            newFigure.setValue(ITechFigure.FIG__OFFSET_03_FLAGP, figurePerfFlags, 1);
            return newFigure;
         }
         if (mergeMask.hasFlag(MERGE_MASK_OFFSET_5VALUES1, ITechMergeMaskFigure.MM_VALUES5_FLAG_2_COLOR)) {
            rcolor = merge.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
         }
         ByteObject newFigure = null;
         switch (fig) {
            case ITechFigure.FIG_TYPE_10_STRING:
               newFigure = mergeFigString(root, merge, mergeMask);
               break;
            case ITechFigure.FIG_TYPE_01_RECTANGLE:
               newFigure = mergeFigRectangle(root, merge, mergeMask);
               break;
            default:
               throw new RuntimeException("Not implemented Merge Method for Figure " + ToStringStaticDraw.debugFigType(fig));
         }
         ByteObject grad = root.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
         //TODO when merging figure has a gradient. what happens if root figure also has a gradient? override or merge gradients?
         if (merge.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_2_GRADIENT)) {
            grad = merge.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
         }
         //same for filters?
         ByteObject filter = root.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
         if (merge.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_5_FILTER)) {
            filter = merge.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
         }
         drc.getFigureFactory().setFigLinks(newFigure, grad, filter, null);
         newFigure.setValue(ITechFigure.FIG__OFFSET_02_FLAG, mainFigureFlag, 1);
         newFigure.setValue(ITechFigure.FIG__OFFSET_03_FLAGP, figurePerfFlags, 1);
         return newFigure;
      } else {
         throw new IllegalArgumentException();
      }
   }

   /**
    * non null
    * @param root
    * @param merge
    * @param mm
    * @return
    */
   public ByteObject mergeFigRectangle(ByteObject root, ByteObject merge, ByteObject mm) {
      int arcw = root.get1(ITechFigure.FIG_RECTANGLE_OFFSET_2ARCW1);
      int arch = root.get1(ITechFigure.FIG_RECTANGLE_OFFSET_3ARCH1);
      int size = root.get1(ITechFigure.FIG_RECTANGLE_OFFSET_4SIZEF1);
      if (mm.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG5_1)) {
         arcw = merge.get1(ITechFigure.FIG_RECTANGLE_OFFSET_2ARCW1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG5_2)) {
         arch = merge.get1(ITechFigure.FIG_RECTANGLE_OFFSET_3ARCH1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG5_3)) {
         size = merge.get1(ITechFigure.FIG_RECTANGLE_OFFSET_4SIZEF1);
      }
      return drc.getFigureFactory().getFigRect(0, arcw, arch, size, null, null, null, null);

   }

   /**
    * 
    * @param root
    * @param merge object on top being inprinted on root
    * @param mergeMask the {@link IMergeMask} definition.
    * @return
    */
   public ByteObject mergeFigString(ByteObject root, ByteObject merge, ByteObject mergeMask) {
      int rface = root.get1(ITechFigure.FIG_STRING_OFFSET_02_FACE1);
      int rstyle = root.get1(ITechFigure.FIG_STRING_OFFSET_03_STYLE1);
      int rsize = root.get1(ITechFigure.FIG_STRING_OFFSET_04_SIZE1);

      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_1)) {
         rface = merge.get1(ITechFigure.FIG_STRING_OFFSET_02_FACE1);
      }
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_2)) {
         rstyle = merge.get1(ITechFigure.FIG_STRING_OFFSET_03_STYLE1);
      }
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_3)) {
         rsize = merge.get1(ITechFigure.FIG_STRING_OFFSET_04_SIZE1);
      }

      String str = null;
      if (root.hasFlag(ITechFigure.FIG_STRING_OFFSET_01_FLAG, ITechFigure.FIG_STRING_FLAG_6_EXPLICIT)) {
         ByteObject raw = root.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
         str = boc.getLitteralStringOperator().getLitteralString(raw);
      }
      if (merge.hasFlag(ITechFigure.FIG_STRING_OFFSET_01_FLAG, ITechFigure.FIG_STRING_FLAG_6_EXPLICIT)) {
         ByteObject raw = merge.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
         str = boc.getLitteralStringOperator().getLitteralString(raw);
      }

      ByteObject effects = root.getSubFirst(IBOTypesDrw.TYPE_070_TEXT_EFFECTS);
      ByteObject mask = root.getSubFirst(IBOTypesDrw.TYPE_058_MASK);
      ByteObject scale = root.getSubFirst(IBOTypesDrw.TYPE_055_SCALE);
      if (root.hasFlag(ITechFigure.FIG_STRING_OFFSET_01_FLAG, ITechFigure.FIG_STRING_FLAG_5_EFFECT)) {

      }
      int rcolor = getMergeColor(root, merge, mergeMask);
      return drc.getFigureFactory().getFigString(str, rface, rstyle, rsize, rcolor, effects, mask, scale);

   }

   public int getMergeColor(ByteObject root, ByteObject merge, ByteObject mm) {
      int rcolor = root.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_2)) {
         rcolor = merge.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      }
      return rcolor;
   }

   private final String STRING = "sada";

   public ByteObject cloneFigDirectionanl(ByteObject fig, int dir) {
      int type = fig.get1(ITechFigure.FIG__OFFSET_01_TYPE1);
      ByteObject clone = fig.cloneCopyHeadRefParams();
      switch (type) {
         case ITechFigure.FIG_TYPE_3_TRIANGLE:
            clone.set1(ITechFigure.FIG_TRIANGLE_OFFSET_2ANGLE2, dir);
            clone.setFlag(ITechFigure.FIG_TRIANGLE_OFFSET_1FLAG1, ITechFigure.FIG_TRIANGLE_FLAG_2ANGLE, false);
            clone.setValue4Bits1(ITechFigure.FIG__OFFSET_05_DIR1, dir);
            break;
         case ITechFigure.FIG_TYPE_01_RECTANGLE:

         default:
            break;
      }
      return clone;
   }

   void drawFigArlequin(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int pcolor = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      int scolor = p.get4(ITechFigure.FIG_ARLEQUIN_OFFSET_2COLOR4);
      int size = p.get4(ITechFigure.FIG_ARLEQUIN_OFFSET_3SIZE4);
      if (p.hasFlag(ITechFigure.FIG__OFFSET_03_FLAGP, ITechFigure.FIG_FLAGP_5IGNORE_ALPHA)) {
         pcolor = ColorUtils.setOpaque(pcolor);
         scolor = ColorUtils.setOpaque(scolor);
      }
      if (size <= 0) {
         int wa = w / 2;
         int ha = h / 2;

         int y2 = y + ha;
         int x2 = x + wa;
         g.setColor(pcolor);
         g.fillRect(x, y, wa, ha);
         g.fillRect(x2, y2, w - wa, h - ha);
         g.setColor(scolor);
         g.fillRect(x2, y, w - wa, ha);
         g.fillRect(x, y2, wa, h - ha);
      } else {
         int color = pcolor;
         int rootX = x;
         int rootY = y;
         int maxY = y + h;
         int maxX = x + w;
         boolean isStartP = true;
         boolean isP = true;
         while (y < maxY) {
            while (x < maxX) {
               if (isP) {
                  color = pcolor;
               } else {
                  color = scolor;
               }
               isP = !isP;
               g.setColor(color);
               g.fillRect(x, y, size, size);
               x += size;
            }
            if (isStartP) {
               isStartP = false;
               isP = false;
            } else {
               isStartP = true;
               isP = true;
            }
            y += size;
            x = rootX;
         }
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
      ByteObject tblr = p.getSubFirst(IBOTypesDrw.TYPE_060_TBLR);
      if (tblr == null) {
         throw new NullPointerException("TBLR Border Size is null");
      }
      int dx = x;
      int dy = y;
      int cornerShift = p.get1(ITechFigure.FIG_BORDER_OFFSET_2CORNER_SHIFT1);
      boolean isDotted = p.get1(ITechFigure.FIG_BORDER_OFFSET_3STROKE_STYLE1) == 1;
      if (isDotted) {
         g.setStrokeStyle(GraphicsX.STROKE_1_DOTTED);
      }
      if (tblr.hasFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_4_SAME_VALUE) && p.hasFlag(ITechFigure.FIG_BORDER_OFFSET_1FLAG, ITechFigure.FIG_BORDER_FLAG_5FIGURE)) {

         int size = getTblrFactory().getTBLRValue(tblr, C.POS_0_TOP);
         if (size == 0)
            return;
         if (p.hasFlag(ITechFigure.FIG_BORDER_OFFSET_1FLAG, ITechFigure.FIG_BORDER_FLAG_1OUTER)) {
            //outer
            dx -= size;
            dy -= size;
            w += (2 * size);
            h += (2 * size);
         }
         if (cornerShift == 0) {
            //rectangle
            ByteObject rect = p.getSubOrder(IBOTypesDrw.TYPE_050_FIGURE, 0);
            if (rect == null) {
               throw new NullPointerException("Rectangle Definition for Border is null");
            }
            rect.setValue(ITechFigure.FIG_RECTANGLE_OFFSET_4SIZEF1, size, 1);
            paintFigureSwitch(g, dx, dy, w, h, rect);
         } else {
            if (cornerShift >= size) {
               return;
            }
            int pixelSize = size - cornerShift;
            //draw 4 lines
            ByteObject rect = p.getSubOrder(IBOTypesDrw.TYPE_050_FIGURE, 0);
            ByteObject grad = rect.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
            int color = rect.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
            if (g.hasGradient() && grad != null) {
               ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, pixelSize);
               int count = 0;
               int dw = w;
               int dh = h;
               while ((count = ci.iteratePixelStep(g)) != -1) {
                  dx += count;
                  dy += count;
                  g.fillRect(x, dy + cornerShift, dw, pixelSize);
                  g.fillRect(x, dy + dh - cornerShift - pixelSize, dw, pixelSize);
                  g.fillRect(dx + cornerShift, y, pixelSize, dh);
                  g.fillRect(dx + dw - cornerShift - pixelSize, y, pixelSize, dh);
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
         int sizeTop = getTblrFactory().getTBLRValue(tblr, C.POS_0_TOP);
         int sizeLeft = getTblrFactory().getTBLRValue(tblr, C.POS_2_LEFT);
         int sizeRight = getTblrFactory().getTBLRValue(tblr, C.POS_3_RIGHT);
         int sizeBot = getTblrFactory().getTBLRValue(tblr, C.POS_1_BOT);
         if (p.hasFlag(ITechFigure.FIG_BORDER_OFFSET_1FLAG, ITechFigure.FIG_BORDER_FLAG_1OUTER)) {
            //outer
            dx -= sizeLeft;
            dy -= sizeTop;
            w += (sizeLeft + sizeRight);
            h += (sizeTop + sizeBot);
         }
         if (p.hasFlag(ITechFigure.FIG_BORDER_OFFSET_1FLAG, ITechFigure.FIG_BORDER_FLAG_8FIGURES)) {
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
            if (p.hasFlag(ITechFigure.FIG_BORDER_OFFSET_1FLAG, ITechFigure.FIG_BORDER_FLAG_4COIN)) {
               ByteObject figTL = p.getSubAtIndex(4);
               ByteObject figTR = p.getSubAtIndex(5);
               ByteObject figBL = p.getSubAtIndex(6);
               ByteObject figBR = p.getSubAtIndex(7);

               paintFigure(g, dx, dy, sizeLeft, sizeTop, figTL); // TL
               paintFigure(g, dx + w - sizeRight, dy, sizeRight, sizeTop, figTR); // TR
               paintFigure(g, dx, dy + h - sizeBot, sizeLeft, sizeBot, figBL); // BL
               paintFigure(g, dx + w - sizeRight, dy + h - sizeBot, sizeRight, sizeBot, figBR); // BR
            }
         }
      }
      if (isDotted) {
         g.setStrokeStyle(GraphicsX.STROKE_0_FILL);
      }
   }

   void drawFigLosange(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      drawerTriangle.drawFigLosange(g, x, y, w, h, p);
   }

   public void drawShapeRectangle(GraphicsX g, int x, int y, int w, int h, int color) {
      g.setColor(color);
      g.fillRect(x, y, w, h);
   }

   public void drawShapeTriangle(GraphicsX g, int x, int y, int w, int h, int color) {
      g.setColor(color);

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
      int color = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      boolean grad = p.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_2_GRADIENT);
      ByteObject gradient = null;
      int arcw = p.getValue(ITechFigure.FIG_RECTANGLE_OFFSET_2ARCW1, 1);
      int arch = p.getValue(ITechFigure.FIG_RECTANGLE_OFFSET_3ARCH1, 1);
      int sizeFill = p.getValue(ITechFigure.FIG_RECTANGLE_OFFSET_4SIZEF1, 1);
      if (grad) {
         gradient = p.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
      }
      if (g.hasGradient() && gradient != null) {
         drawRectangle(g, x, y, w, h, sizeFill, arcw, arch, color, gradient);
      } else {
         if (sizeFill != 0) {
            //draw method =>  it with d
            if (p.hasFlag(ITechFigure.FIG_RECTANGLE_OFFSET_1FLAG, ITechFigure.FIG_RECTANGLE_FLAG_2ROUND_INSIDE)) {
               //color = 0xFFFFFF;
               g.setColor(color);
               g.fillRoundRect(x, y, w, h, arcw, arch);
               ByteObject colori = p.getSubFirst(IBOTypesDrw.TYPE_002_LIT_INT);
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
            if (p.hasFlag(ITechFigure.FIG_RECTANGLE_OFFSET_1FLAG, ITechFigure.FIG_RECTANGLE_FLAG_1ROUND)) {
               g.setColor(color);
               g.fillRoundRect(x, y, w, h, arcw, arch);
            } else {
               boolean ignoreAlpha = p.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAGP_5IGNORE_ALPHA);
               if (ignoreAlpha) {
                  color = (255 << 24) + (color & 0xFFFFFF);
               }
               //fill method. supports alpha. must be disabled
               drawRectangleSingleColor(g, x, y, w, h, color);
            }
         }
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
      int bgColor = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      ByteObject figure = p.getSubFirst(IBOTypesDrw.TYPE_050_FIGURE);
      ByteObject anchor = p.getSubFirst(IBOTypesDrw.TYPE_051_BOX);
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

      if (p.hasFlag(ITechFigure.FIG_REPEATER_OFFSET_1FLAG, ITechFigure.FIG_REPEATER_FLAG_1FORCECOPYAREA) || figure.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAGP_3OPAQUE)) {
         if (p.hasFlag(ITechFigure.FIG_REPEATER_OFFSET_1FLAG, ITechFigure.FIG_REPEATER_FLAG_2USE_BGCOLOR)) {
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
         if (p.hasFlag(ITechFigure.FIG_REPEATER_OFFSET_1FLAG, ITechFigure.FIG_REPEATER_FLAG_2USE_BGCOLOR)) {
            //fill bg background color
            color = bgColor;
         }
         RgbImage img = getFigImage(g, figure, fw, fh, false, false, color);
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

   /**
    * Gets an image of the figure parameters in the rectangle [w,h] <br>
    * For performance reasons, one might want to draw figure of primitives using given pseudo background color
    * 
    * For non RGB figure {@link ITechFigure#FIG_FLAGP_1RGB}, image background is 
    * <br>either black transparent
    * <br> white opaque
    * <br> given color
    * <br>
    * For figures with flag {@link ITechFigure#FIG_FLAGP_1RGB}, background is transparent black
    * 
    * if just switch is true, the figure is drawn without the controls of filters, mask etc.
    * @param fig
    * @param w
    * @param h
    * @param justSwitch draw just the figure without the control code of Filters/Mask
    * @param whiteopaque ignored if figure is full RGB. else if true, create a primitive only mutable image
    * @return image is in Rgb mode with black transparent pixels where the figure has not drawn
    */
   public RgbImage getFigImage(GraphicsX g, ByteObject fig, int w, int h, boolean justSwitch, boolean whiteopaque, int bgColor) {
      int mode = GraphicsX.MODE_1_IMAGE;
      RgbImage figImg = null;
      if (fig.hasFlag(ITechFigure.FIG__OFFSET_03_FLAGP, ITechFigure.FIG_FLAGP_1RGB)) {
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

   public RgbImage getFigImageMutable(GraphicsX g, ByteObject fig, int w, int h, int color) {
      return getFigImage(g, fig, w, h, false, true, color);
   }

   /**
    * Returns the image drawn on a white background
    * @param fig
    * @param w
    * @param h
    * @return
    */
   public RgbImage getFigImageNonNull(GraphicsX g, ByteObject fig, int w, int h) {
      return getFigImageNonNull(g, fig, w, h, false, true, 0);
   }

   public RgbImage getFigImageNonNull(GraphicsX g, ByteObject fig, int w, int h, boolean justSwitch, boolean whiteopaque, int bgColor) {
      if (w <= 0 || h <= 0) {
         return drc.getCache().NULL_IMAGE;
      }
      return getFigImage(g, fig, w, h, justSwitch, whiteopaque, bgColor);
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
   public RgbImage getFigImagePrimitve(GraphicsX g, ByteObject fig, int w, int h, boolean justSwitch, int bgColor) {
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
   public RgbImage getFigImageTrans(GraphicsX g, ByteObject fig, int w, int h) {
      return getFigImage(g, fig, w, h, false, false, 0);
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
      int linesize = p.getValue(ITechFigure.FIG_LINE_OFFSET_2SIZE1, 1);
      int color = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      boolean horiz = p.hasFlag(ITechFigure.FIG_LINE_OFFSET_1FLAG, ITechFigure.FIG_LINE_FLAG_HORIZ);
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
      if (p.hasFlag(ITechFigure.FIG_PIXEL_OFFSET_01_FLAG, ITechFigure.FIG_PIXEL_FLAG_3_NEW_IMAGE)) {
         int color = p.get4(ITechFigure.FIG_PIXEL_OFFSET_04_COLOR_EXTRA4);
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
      int lengthHoriz = p.getValue(ITechFigure.FIG_PIXEL_OFFSET_07_LENGTH_H2, 2);
      int lengthVert = p.getValue(ITechFigure.FIG_PIXEL_OFFSET_08_LENGTH_V2, 2);
      if (lengthHoriz <= 0)
         lengthHoriz = 1;
      if (lengthVert <= 0)
         lengthVert = 1;
      int blendMode = p.get1(ITechFigure.FIG_PIXEL_OFFSET_05_BLENDERX1);

      boolean randomLength = p.hasFlag(ITechFigure.FIG_PIXEL_OFFSET_01_FLAG, ITechFigure.FIG_PIXEL_FLAG_1_RANDOM_SIZE);
      boolean randomColor = p.hasFlag(ITechFigure.FIG_PIXEL_OFFSET_01_FLAG, ITechFigure.FIG_PIXEL_FLAG_2_RANDOM_COLOR);
      //or use gradient
      ByteObject grad = p.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
      int[] colors = p.getValues(ITechFigure.FIG_PIXEL_OFFSET_04_COLORSX);
      int colorIndex = 0;
      int maxc = colors.length;
      int add = lengthHoriz;
      int baseColor = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      int gradSize = p.get1(ITechFigure.FIG_PIXEL_OFFSET_09_GRAD_SIZE1);

      ColorIterator ci = new ColorIterator(drc, colors);
      if (grad != null) {
         ColorIterator cigrad = drc.getColorFunctionFactory().getColorIterator(baseColor, grad, gradSize);
         //rules for mixing 2 color iterator
         ci.mix(cigrad);
      }

      //define the randomness of the iteration.
      int seed = p.get4(ITechFigure.FIG_PIXEL_OFFSET_03_SEED4);
      Random r = drc.getUCtx().getRandom(seed);
      //SystemLog.pDraw("#DrwParamFig#drawPixels m=" + m + " n=" + n + " w=" + w + " h=" + h + " scan=" + scan + " offset=" + offset);
      BlendOp bop = new BlendOp(drc, blendMode);

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
      int length = p.getValue(ITechFigure.FIG_PIXEL_OFFSET_07_LENGTH_H2, 2);
      if (length <= 0)
         length = 1;
      boolean randomLength = p.hasFlag(ITechFigure.FIG_PIXEL_OFFSET_01_FLAG, ITechFigure.FIG_PIXEL_FLAG_1_RANDOM_SIZE);
      boolean randomColor = p.hasFlag(ITechFigure.FIG_PIXEL_OFFSET_01_FLAG, ITechFigure.FIG_PIXEL_FLAG_2_RANDOM_COLOR);
      int[] colors = p.getValues(ITechFigure.FIG_PIXEL_OFFSET_04_COLORSX);
      int colorIndex = 0;
      int maxc = colors.length;
      if (maxc == 0) {
         throw new IllegalArgumentException();
      }
      int add = length;
      boolean exit = false;
      int total = h * w;
      int seed = p.get4(ITechFigure.FIG_PIXEL_OFFSET_03_SEED4);
      Random r = drc.getUCtx().getRandom(seed);
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

   public void setFigAnchor(ByteObject fig, ByteObject anchor) {
      fig.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_1_ANCHOR, true);
      fig.addSub(anchor);
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
      p.checkType(IBOTypesDrw.TYPE_050_FIGURE);
      if (p.hasFlag(ITechFigure.FIG__OFFSET_03_FLAGP, ITechFigure.FIG_FLAGP_8POSTPONE)) {
         g.postpone(x, y, w, h, p);
         return;
      }
      ByteObject filter = null;
      if (p.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_5_FILTER)) {
         filter = p.getSubOrder(IBOTypesDrw.TYPE_056_COLOR_FILTER, 0);
      }
      ByteObject mask = null;
      RgbImage rgbMask = null;
      if (p.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_4_MASK)) {
         mask = p.getSubFirst(IBOTypesDrw.TYPE_058_MASK);
         if (mask == null) {
            throw new IllegalArgumentException("Mask is null");
         }
         rgbMask = drc.getMaskOperator().createMaskedFigure(g, mask, w, h, p);
         if (filter != null) {
            drc.getFilterOperator().applyColorFilter(filter, rgbMask);
         }
         g.drawRgbImage(rgbMask, x, y);
         rgbMask.dispose();
      } else {
         if (filter != null) {
            //At best,  GraphicsX is Rgb Virgin and Figure is Rgb -> 0 buffer is created. Only SRC Blending is possible
            //At middle GraphicsX is Primitive and Figure is Rgb -> 1 buffer is created for figure: RGB
            //At worst, Figure is Primitive  -> 2 buffers are created. One for Image and one for Rgb filter : RGB_IMAGE
            boolean rgb = p.hasFlag(ITechFigure.FIG__OFFSET_03_FLAGP, ITechFigure.FIG_FLAGP_1RGB);
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
            gi.setDebugName("FilterLayer");
            //SystemLog.pDraw(gi);
            //SystemLog.pDraw(buffer);
            paintFigureSwitch(gi, 0, 0, w, h, p);
            //SystemLog.pDraw(buffer.debugColors());
            //the color Filter will query the background color of the RgbImage for applying any filter
            drc.getFilterOperator().applyColorFilter(filter, buffer);
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

   void paintFigureSwitch(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      final int type = p.getValue(ITechFigure.FIG__OFFSET_01_TYPE1, 1);
      switch (type) {
         case ITechFigure.FIG_TYPE_01_RECTANGLE:
            //no trans filter
            drawFigRectangle(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_3_TRIANGLE:
            drawerTriangle.drawFigTriangle(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_05_LINE:
            drawLine(g, x, y, w, h, p);
            break;
         case ITechFigure.FIG_TYPE_02_BORDER:
            drawFigBorder(g, x, y, w, h, p);
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
         case ITechFigure.FIG_TYPE_12_ARROW:
            break;
         case ITechFigure.FIG_TYPE_08_GERMANCROSS:
            break;
         case ITechFigure.FIG_TYPE_07_ELLIPSE:
            drawEllipse(g, x, y, w, h, p);
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
         default:
            throw new IllegalArgumentException("Unknown figure type " + type);
      }
      paintFigureSwitchSubFigures(g, x, type, w, h, p);
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
      if (p.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_7_SUB_FIGURE)) {
         //find sub figures which are sized
         int index = getSubFiguresDrwIndex(p);
         ByteObject[] params = p.getSubs();
         for (int i = index; i < params.length; i++) {

            ByteObject fig = params[i];
            ByteObject anchorBox = fig.getSubFirst(IBOTypesDrw.TYPE_051_BOX);

            LayoutOperator sizer = drc.getSizer();
            int figW = sizer.codedSizeDecode(anchorBox, BOX_OFFSET_04_WIDTH4, w, h, ITechLayout.CTX_1_WIDTH);
            int figH = sizer.codedSizeDecode(anchorBox, BOX_OFFSET_05_HEIGHT4, w, h, ITechLayout.CTX_2_HEIGHT);

            fig.getSubFirst(IBOTypesDrw.TYPE_051_BOX);

            if (fig.getType() == IBOTypesDrw.TYPE_050_FIGURE && anchorBox != null) {
               int ha = anchorBox.get4(BOX_OFFSET_02_HORIZ_ALIGN4);
               int va = anchorBox.get4(BOX_OFFSET_03_VERTICAL_ALIGN4);
               int fw = drc.getBoxFactory().computeSizeW(anchorBox, w, h);
               int fh = drc.getBoxFactory().computeSizeH(anchorBox, w, h);
               int fx = AnchorUtils.getXAlign(ha, x, w, fw);
               int fy = AnchorUtils.getYAlign(va, y, h, fh);
               paintFigure(g, fx, fy, fw, fh, fig);
            }
         }
      }
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
      int sepsize = p.getValue(ITechFigure.FIG_SL_OFFSET_4SEPARATION2, 2);
      int linesize = p.getValue(ITechFigure.FIG_SL_OFFSET_2LINE_SIZE1, 1);
      int color = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      boolean horiz = p.hasFlag(ITechFigure.FIG_SL_OFFSET_1FLAG, ITechFigure.FIG_SL_FLAG_3HORIZ);
      g.setColor(color);
      int numLines = p.getValue(ITechFigure.FIG_SL_OFFSET_3REPEAT2, 2);
      boolean isIFirst = p.hasFlag(ITechFigure.FIG_SL_OFFSET_1FLAG, ITechFigure.FIG_SL_FLAG_7IGNORE_FIRST);
      boolean isILast = p.hasFlag(ITechFigure.FIG_SL_OFFSET_1FLAG, ITechFigure.FIG_SL_FLAG_8IGNORE_LAST);
      ByteObject grad = p.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
      int count = 0;
      if (horiz) {
         if (p.hasFlag(ITechFigure.FIG_SL_OFFSET_1FLAG, ITechFigure.FIG_SL_FLAG_6FILL)) {
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
         if (p.hasFlag(ITechFigure.FIG_SL_OFFSET_1FLAG, ITechFigure.FIG_SL_FLAG_6FILL)) {
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

   /**
    * The index at which ByteObject of type FIGURE may found in the ByteObject array.
    * @param p
    * @return
    */
   public int getSubFiguresDrwIndex(ByteObject p) {
      int index = 0;
      if (p.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_5_FILTER)) {
         index++;
      }
      if (p.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_6_ANIMATED)) {
         index++;
      }
      return index;
   }

   /**
    * Tells if that Figure ByteObject will draw translucent data
    * @param p
    * @return
    */
   public boolean hasFigTransparentPixels(ByteObject p) {
      p.checkType(IBOTypesDrw.TYPE_050_FIGURE);
      return false;
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

   public void drawArc(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      ByteObject grad = p.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
      int color = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      int angle = 0;
      g.setColor(color);
   }

   public void drawArrow(GraphicsX g, int dir, int x, int y, int base, int H, int h, int length, int thick, int headColor, int bodyColor, ByteObject transFunct) {
      if (transFunct != null) {
         if (transFunct.getType() != IBOTypesDrw.TYPE_056_COLOR_FILTER)
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
   public void drawArtifactsHoriz(GraphicsX g, int color, int x, int y, int width, ByteObject art, Random r) {
      boolean isRndSpace = art.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_3RANDOM_SPACING);
      boolean isRndW = art.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_1RANDOM_W);
      boolean isRndH = art.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_2RANDOM_H);
      int type = art.get1(ITechArtifact.ARTIFACT_OFFSET_5TYPE1);

      int artW = art.get1(ITechArtifact.ARTIFACT_OFFSET_2W1);
      int artH = art.get1(ITechArtifact.ARTIFACT_OFFSET_3H1);
      int aspace = art.get1(ITechArtifact.ARTIFACT_OFFSET_4SPACING_CAP1);
      if (aspace <= 0)
         aspace = 1;
      g.setColor(color);
      int dw = artW;
      int dh = artH;
      int xCount = 0;
      switch (type) {
         case 0:
            g.fillTriangle(x, y, x, y - artH, x + artW, y);
            g.fillTriangle(x + width, y, x + width, y - artH, x + width - artW, y);
            break;
         case 1:
            while (xCount < width) {
               if (isRndW) {
                  dw = r.nextInt(artW) + 1;
               }
               if (isRndH) {
                  dh = r.nextInt(artH) + 1;
               }
               //g.setColor(gh.colorPrevious);
               int rnd = r.nextInt(2);
               if (rnd == 0) {
                  g.fillRect(x + xCount, y - dh, dw, dh);
               } else {
                  g.fillTriangle(x + xCount, y, x + dw, y - dh, x + 2 * dw, y);
               }
               if (isRndSpace) {
                  xCount += r.nextInt(aspace) + 1;
               } else {
                  xCount += aspace;
               }
            }
            break;
         case 2:
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
      int color = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      g.setColor(color);
      ByteObject grad = p.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
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
      int positionVerticalBar = p.get1(ITechFigure.FIG_CROSS_OFFSET_4XOFFSET1);
      int positionHorizontalBar = p.get1(ITechFigure.FIG_CROSS_OFFSET_5YOFFSET1);
      int heightHorizontalBar = p.get1(ITechFigure.FIG_CROSS_OFFSET_2HTHICK1);
      int widthVerticalBar = p.get1(ITechFigure.FIG_CROSS_OFFSET_3VTHICK1);
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
      int heightHorizontalBar = p.get1(ITechFigure.FIG_CROSS_OFFSET_2HTHICK1);
      int widthVerticalBar = p.get1(ITechFigure.FIG_CROSS_OFFSET_3VTHICK1);
      int crossHheight = h * heightHorizontalBar / 100;
      int crossVwidth = w * widthVerticalBar / 100;

      int dx = x + ((w - crossVwidth) / 2);
      int dy = y;
      g.fillRect(dx, dy, crossVwidth, h);

      dx = x;
      dy = y + ((h - crossHheight) / 2);
      g.fillRect(dx, dy, w, crossHheight);

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
   public void drawEllipse(GraphicsX g, int x, int y, int w, int h, ByteObject p) {
      int color = p.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      int border = p.get1(ITechFigure.FIG_ELLIPSE_OFFSET_03_SIZE_FILL1);
      if (border != 0) {
         //we must draw using a mask
         ByteObject borderSizer = p.getSubFirst(IBOTypesLayout.FTYPE_3_SIZER);
      }
      boolean isArc = p.hasFlag(ITechFigure.FIG_ELLIPSE_OFFSET_01_FLAG1, ITechFigure.FIG_ELLIPSE_FLAG_3_FIL_DE_FER);
      g.setFillMode(!isArc);
      ByteObject grad = p.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
      if (g.hasGradient() && grad != null) {
         drawEllipseGradient(g, x, y, w, h, p, color, grad);
      } else {
         drawEllipse(g, x, y, w, h, color, p);
      }
   }

   public void drawEllipse(GraphicsX g, int x, int y, int w, int h, int color, ByteObject p) {
      int start = p.get2(ITechFigure.FIG_ELLIPSE_OFFSET_05_ANGLE_START2);
      int amplitude = p.get2(ITechFigure.FIG_ELLIPSE_OFFSET_06_ANGLE_END2);

      g.setColor(color);
      g.fiArc(x, y, w, h, start, amplitude);
   }

   public void drawEllipseGradient(GraphicsX g, int x, int y, int w, int h, ByteObject p, int color, ByteObject grad) {
      final int type = grad.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      int gradSize = GradientOperator.getEllipseGradSize(w, h, grad);
      int count = 0;
      int start = p.get2(ITechFigure.FIG_ELLIPSE_OFFSET_05_ANGLE_START2);
      int amplitude = p.get2(ITechFigure.FIG_ELLIPSE_OFFSET_06_ANGLE_END2);
      int slip = p.get2(ITechFigure.FIG_ELLIPSE_OFFSET_07_ANGLE_SLIP2);
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      if (p.hasFlag(ITechFigure.FIG_ELLIPSE_OFFSET_01_FLAG1, ITechFigure.FIG_ELLIPSE_FLAG_4_RECTANGLE_FILL)) {
         g.setColor(ci.getCurrentColor());
         g.fillRect(x, y, w, h);
      }
      int[] types = new int[] { type };
      if (grad.hasFlag(ITechGradient.GRADIENT_OFFSET_09_FLAGX1, ITechGradient.GRADIENT_FLAGX_8_MANY_TYPES)) {
         ByteObject ar = grad.getSubAtIndex(type);
         types = boc.getLitteralIntOperator().getLitteralArray(ar);
      }
      while ((count = ci.iteratePixelCount(g)) != -1) {
         int countX2 = count * 2;
         for (int i = 0; i < types.length; i++) {
            int mtype = types[i];
            switch (mtype) {
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_00_NORMAL:
                  g.fiArc(x + count, y + count, w - countX2, h - countX2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_01_HORIZ:
                  g.fiArc(x, y + count, w, h - countX2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_02_VERT:
                  g.fiArc(x + count, y, w - countX2, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_03_TOP_FLAMME:
                  g.fiArc(x + count, y, w - countX2, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_04_BOT_FLAMME:
                  g.fiArc(x + count, y + count, w - countX2, h - count, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_05_LEFT_FLAMME:
                  g.fiArc(x, y + count, w - count, h - countX2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_06_RIGHT_FLAMME:
                  g.fiArc(x + count, y + count, w - count, h - countX2, start, amplitude);
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
                  g.fiArc(x + count, y - count, w - countX2, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_12_WATER_DROP_BOT:
                  g.fiArc(x + count, y + count, w - countX2, h, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_13_WATER_DROP_LEFT:
                  g.fiArc(x - count, y + count, w, h - countX2, start, amplitude);
                  break;
               case ITechGradient.GRADIENT_TYPE_ELLIPSE_14_WATER_DROP_RIGHT:
                  g.fiArc(x + count, y + count, w, h - countX2, start, amplitude);
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
               default:
                  break;
            }
         }
         start += slip;
      }
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
      drc.getUCtx().getGeo2dUtils().getIntersection(x, y, w, h, hx, hy, hw, hh, dim);
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
   public void drawRectangle(GraphicsX g, int x, int y, int width, int height, int sizeBorder, int arcw, int arch, int primaryColor, ByteObject grad) {
      if (sizeBorder != 0) {
         drawRectangleGradientBorder(g, x, y, width - 1, height - 1, arcw, arch, primaryColor, sizeBorder, grad);
      } else {
         drawRectangleGradient(g, x, y, width, height, arcw, arch, primaryColor, grad);
      }
   }

   public void drawRectangleGradient(GraphicsX g, int x, int y, int w, int h, int arcw, int arch, int color, ByteObject grad) {
      int type = grad.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      int gradSize = GradientOperator.getRectGradSize(w, h, arcw, arch, type); //number of iteration
      drawRectangleGradient(g, x, y, w, h, arcw, arch, color, gradSize, grad);
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
   public void drawRectangleGradient(GraphicsX g, int x, int y, int w, int h, int arcw, int arch, int color, int gradSize, ByteObject grad) {
      int type = grad.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      if (grad.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_7_ARTIFACTS)) {
         //artifact definition
         ByteObject art = grad.getSubFirst(IBOTypesDrw.TYPE_052_ARTIFACT);
         drawRectangleGradientArt(g, x, y, w, h, arcw, arch, color, gradSize, grad, art);
         return;
      }
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      while ((count = ci.iteratePixelCount(g)) != -1) {
         int countX2 = count * 2;
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_RECT_00_SQUARE:
               g.fillRoundRect(x + count, y + count, w - countX2, h - countX2, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_02_VERT:
               //g.drawLine(x, y + count, x + width - 1, y + count);
               g.fillRoundRect(x, y + count, w, h - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_01_HORIZ:
               //g.drawLine(x + count, y, x + count, y + height - 1);
               g.fillRoundRect(x + count, y, w - count, h, arcw, arch);
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

            default:
               break;
         }
      }

   }

   public void drawRectangleGradientArt(GraphicsX g, int x, int y, int width, int height, int arcw, int arch, int color, int gradSize, ByteObject grad, ByteObject art) {
      int type = grad.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      int aw = art.get1(ITechArtifact.ARTIFACT_OFFSET_2W1);
      int ah = art.get1(ITechArtifact.ARTIFACT_OFFSET_3H1);
      int aspace = art.get1(ITechArtifact.ARTIFACT_OFFSET_4SPACING_CAP1);
      if (aspace <= 0)
         aspace = 1;
      boolean isRndSpace = art.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_3RANDOM_SPACING);
      boolean isRndW = art.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_1RANDOM_W);
      boolean isRndH = art.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_2RANDOM_H);
      Random r = drc.getRandom();
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      while ((count = ci.iteratePixelCount(g)) != -1) {
         int countX2 = count * 2;
         switch (type) {
            case ITechGradient.GRADIENT_TYPE_RECT_00_SQUARE:
               g.fillRoundRect(x + count, y + count, width - countX2, height - countX2, arcw, arch);
               drawArtifactsHoriz(g, ci.currentColor, x, y + count, width, art, r);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_01_HORIZ:
               //g.drawLine(x + count, y, x + count, y + height - 1);
               g.fillRoundRect(x + count, y, width - count, height, arcw, arch);
               drawArtifactsHoriz(g, ci.currentColor, x, y + count, width, art, r);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_02_VERT:
               //g.drawLine(x, y + count, x + width - 1, y + count);
               int yc = y + count;
               g.fillRoundRect(x, y + count, width, height - count, arcw, arch);
               if (count == 0)
                  break;
               drawArtifactsHoriz(g, ci.currentColor, x, y + count, width, art, r);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_04_TOPRIGHT:
               g.fillRoundRect(x + count, y, width - count, height - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_06_BOTRIGHT:
               g.fillRoundRect(x + count, y + count, width - count, height - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_05_BOTLEFT:
               g.fillRoundRect(x, y + count, width - count, height - count, arcw, arch);
               break;
            case ITechGradient.GRADIENT_TYPE_RECT_03_TOPLEFT:
               g.fillRoundRect(x, y, width - count, height - count, arcw, arch);
               break;
            default:
               break;
         }
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
   public void drawRectangleGradientBorder(GraphicsX g, int x, int y, int width, int height, int arcw, int arch, int color, int gradSize, ByteObject grad) {
      int count = 0;
      ColorIterator ci = drc.getColorFunctionFactory().getColorIterator(color, grad, gradSize);
      while ((count = ci.iteratePixelCount(g)) != -1) {
         int countX2 = count * 2;
         g.drawRoundRect(x + count, y + count, width - countX2, height - countX2, arcw, arch);
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
      if (alpha == 0) {
         //fully transparent so we don't draw anything :)
         return;
      } else if (alpha == 255) {
         //fully opaque so use fast primitive drawing
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
}
