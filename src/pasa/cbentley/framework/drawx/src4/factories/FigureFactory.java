/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.tech.ITechMergeMask;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.coredraw.src4.ctx.ToStringStaticCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;
import pasa.cbentley.framework.drawx.src4.ctx.BOModuleDrawx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigPixelStar;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeMaskFigure;
import pasa.cbentley.framework.drawx.src4.utils.ToStringStaticDraw;

/**
 * Create {@link ByteObject} of type {@link IBOTypesDrw#TYPE_050_FIGURE}
 * <br>
 * @author Charles Bentley
 *
 */
public class FigureFactory extends AbstractDrwFactory implements ITechMergeMask, ITechFigPixelStar, ITechFigure {

   public FigureFactory(DrwCtx drc) {
      super(drc);
   }

   /**
    * Merge mask for {@link ITechMergeMaskFigure}
    * @return
    */
   public ByteObject getMergeMaskFigure() {
      // TODO Auto-generated method stub
      return null;
   }

   public void addAnchor(ByteObject figure, ByteObject anchor) {
      if (figure == null || anchor == null)
         return;
      if (!figure.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_1_ANCHOR)) {
         figure.addByteObject(anchor);
         figure.setFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_1_ANCHOR, true);
      }
   }

   /**
    * If figure does not have a mask, add the mask
    * @param figure may be null
    * @param mask may be null
    */
   public void addMask(ByteObject figure, ByteObject mask) {
      if (figure == null || mask == null)
         return;
      if (!figure.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_4_MASK)) {
         figure.addSub(mask);
         figure.setFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_4_MASK, true);
      }
   }

   public void addTxtFXToStringFig(ByteObject strFix, ByteObject fx) {
      strFix.addByteObject(fx);
      strFix.setFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_5_EFFECT, true);
   }

   public ByteObject getBorder(ByteObject tblr, int color) {
      return getFigBorder(tblr, color);
   }

   public ByteObject getEllipse(int color) {
      return getFigEllipse(color);
   }

   /**
    * 4 figures opposed to each other.
    * If filter, draw one figure and then do 3 transformations
    * @param trig
    * @return
    */
   public ByteObject getFig4Trig(ByteObject trig) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_4TRIG_BASIC_SIZE);
      return p;
   }

   public ByteObject getFigArlequin(int pcolor, int scolor) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_ARLEQUIN_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_17_ARLEQUIN, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, pcolor, 4);
      p.setValue(FIG_ARLEQUIN_OFFSET_2COLOR4, scolor, 4);
      return p;
   }

   public ByteObject getFigRays(int type, int color, int[] colorSeries) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_RAYS_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_15_RAYS, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);

      return p;
   }

   /**
    * Arlequin can be defined with a proportion
    * @param pcolor
    * @param scolor
    * @param size
    * @return
    */
   public ByteObject getFigArlequin(int pcolor, int scolor, int size) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_ARLEQUIN_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_17_ARLEQUIN, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, pcolor, 4);
      p.setValue(FIG_ARLEQUIN_OFFSET_2COLOR4, scolor, 4);
      p.setValue(FIG_ARLEQUIN_OFFSET_3SIZE4, size, 4);
      return p;
   }

   public ByteObject getFigArrow() {
      // TODO Auto-generated method stub
      return null;
   }

   public ByteObject getFigBorder(ByteObject tblr, ByteObject rect) {
      return getFigBorder(tblr, rect, false);
   }

   /**
    * Border is just a rectangle
    * @param tblr
    * @param rect
    * @param outer
    * @return
    */
   public ByteObject getFigBorder(ByteObject tblr, ByteObject rect, boolean outer) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_BORDER_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_02_BORDER, 1);
      p.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_2EXTRA_BOUNDARY, outer);
      p.setFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_1OUTER, outer);
      p.setFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_8FIGURES, false);
      p.setFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_5FIGURE, true);
      if (tblr == null) {
         p.setFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_4COIN, false);
      }
      p.setByteObjects(new ByteObject[] { rect, tblr });
      return p;
   }

   /**
    * Creates a border with 4 coin figures and 4 rectangle figures
    * <br>
    * 
    * @param tblr TBLR pixe sizes
    * @param tblrRects 4 TBLR figures. Maybe null.
    * @param coins 4 TL/TR/BL/BR figures. Maybe null.
    * @param outer True when drawn outside the boundary.
    * @return
    */
   public ByteObject getFigBorder(ByteObject tblr, ByteObject tblrRects, ByteObject coins, boolean outer) {
      ByteObject[] figs = new ByteObject[] { tblrRects, tblrRects, tblrRects, tblrRects, coins, coins, coins, coins };
      ByteObject p = getFigBorder(tblr, figs, outer);
      if (coins != null) {
         p.setFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_4COIN, true);
      }
      return p;
   }

   public ByteObject getFigBorder(ByteObject tblr, ByteObject coinFig, ByteObject topFig, ByteObject botFig, ByteObject leftFig, ByteObject rightFig, boolean outer) {
      ByteObject[] figures = new ByteObject[] { topFig, botFig, leftFig, rightFig, coinFig, coinFig, coinFig, coinFig };
      return getFigBorder(tblr, figures, outer);
   }

   /**
    * A border figure with TBLR sizes and up to 8 figures.
    * For partial definitions, set it to null. 
    * @param tblr The TBLR used  by the Style Border Box model
    * @param figures 8 figures for the 8 spots 
    * 0 = top rect
    * 1 = bot rect
    * 2 = left rect
    * 3 = right rect
    * 4 = TL coin
    * 5 = TR coin
    * 6 = BL coin
    * 7 = BR coin
    * @param outer
    * @return
    */
   public ByteObject getFigBorder(ByteObject tblr, ByteObject[] figures, boolean outer) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_BORDER_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_02_BORDER, 1);
      p.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_2EXTRA_BOUNDARY, outer);
      p.setFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_1OUTER, outer);
      p.setFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_8FIGURES, true);
      p.setFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_4COIN, true);
      if (tblr == null) {
         p.setFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_4COIN, false);
      }
      if (figures.length != 8) {
         throw new IllegalArgumentException();
      }
      p.setByteObjects(figures);
      p.addByteObject(tblr);
      return p;
   }

   public ByteObject getFigBorder(ByteObject tblr, int color) {
      return getFigBorder(tblr, color, false);
   }

   /**
    * 
    * @param tblr
    * @param color just a color for the border
    * @return
    */
   public ByteObject getFigBorder(ByteObject tblr, int color, boolean outer) {
      ByteObject rect = getRect(color);
      return getFigBorder(tblr, rect, outer);
   }

   public ByteObject getFigBorder(ByteObject tblr, int arcw, int arch, int color, ByteObject grad) {
      ByteObject rect = getFigRect(color, arcw, arch, grad, null, null, null);
      return getFigBorder(tblr, rect);
   }

   /**
    * Border Figure 
    * @param size
    * @param rect
    * @return
    */
   public ByteObject getFigBorder(int size, ByteObject rect) {
      return getFigBorder(size, rect, false);
   }

   public ByteObject getFigBorder(int size, ByteObject rect, boolean outer) {
      ByteObject tblr = getTblrFactory().getTBLR(size);
      return getFigBorder(tblr, rect, outer);
   }

   public ByteObject getFigBorder(int size, int color) {
      return getFigBorder(size, color, false);
   }

   public ByteObject getFigBorder(int size, int color, boolean outer) {
      ByteObject rect = getRect(color);
      return getFigBorder(size, rect, outer);
   }

   public ByteObject getFigBorder(int size, int color, ByteObject grad) {
      //fill size so that the Opaque Flag is not set
      ByteObject rect = getFigRect(color, 0, 0, size, grad, null, null, null);
      return getFigBorder(size, rect);
   }

   public ByteObject getFigBorder(int size, int pcolor, int scolor, int sec) {
      return getFigBorder(size, 0, 0, pcolor, scolor, sec);
   }

   public ByteObject getFigBorder(int size, int arcw, int arch, int color, ByteObject grad) {
      ByteObject rect = getFigRect(color, arcw, arch, grad, null, null, null);
      return getFigBorder(size, rect);
   }

   /**
    * Simple border
    * @param outer
    * @param size
    * @param arcw
    * @param arch
    * @param color
    * @param scolor
    * @return
    */
   public ByteObject getFigBorder(int size, int arcw, int arch, int color, int scolor, int sec) {
      ByteObject grad = drc.getGradientFactory().getGradient(scolor, sec, ITechGradient.GRADIENT_TYPE_RECT_00_SQUARE);
      return getFigBorder(size, arcw, arch, color, grad);
   }

   /**
    * A cross hair may be explicit in size. in which case, w and h are just ignored
    * Draw from center x,y
    * @param color
    * @param sizeH
    * @param sizeV
    * @param lenH
    * @param lenV
    * @param spacingH
    * @param spacingV
    * @return
    */
   public ByteObject getFigCrossHair(int color, int sizeH, int sizeV, int lenH, int lenV, int spacingH, int spacingV) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_CROSS_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_20_CROSS, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      p.setValue(FIG_CROSS_OFFSET_6SPACINGH1, spacingH, 1);
      p.setValue(FIG_CROSS_OFFSET_7SPACINGV1, spacingV, 1);
      ByteObject sizeTBLR = getTblrFactory().getTBLR(sizeV, sizeV, sizeH, sizeH);
      ByteObject lenTBLR = getTblrFactory().getTBLR(lenV, lenV, lenH, lenH);
      return p;
   }

   /**
    * Gradient works ok only with 50s ratio
    * @param color
    * @param widthVerticalBar
    * @param heightHorizontalBar
    * @param positionVerticalBar
    * @param positionHorizontalBar
    * @param grad
    * @return
    */
   public ByteObject getFigCrossRatio(int color, int widthVerticalBar, int heightHorizontalBar, int positionVerticalBar, int positionHorizontalBar, ByteObject grad) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_CROSS_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_20_CROSS, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      p.setValue(FIG_CROSS_OFFSET_2HTHICK1, heightHorizontalBar, 1);
      p.setValue(FIG_CROSS_OFFSET_3VTHICK1, widthVerticalBar, 1);
      p.setValue(FIG_CROSS_OFFSET_4XOFFSET1, positionVerticalBar, 1);
      p.setValue(FIG_CROSS_OFFSET_5YOFFSET1, positionHorizontalBar, 1);
      p.setFlag(FIG_CROSS_OFFSET_1FLAG, FIG_CROSS_FLAG_8CROSS, true);
      setFigLinks(p, grad, null, null, null);
      return p;
   }

   public ByteObject getFigEllipse(int color) {
      return getFigEllipse(color, null, null, null, null);
   }

   public ByteObject getFigEllipse(int color, ByteObject grad) {
      return getFigEllipse(color, grad, null, null, null);
   }

   public ByteObject getFigEllipse(int color, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] sub) {
      return getFigEllipse(color, 0, grad, anchor, filter, sub);
   }

   public ByteObject getFigEllipse(int color, int fillSize, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] sub) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_ELLIPSE_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_07_ELLIPSE, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      if (fillSize < 0)
         fillSize = 0;
      p.setValue(FIG_ELLIPSE_OFFSET_03_SIZE_FILL1, fillSize, 1);

      p.setValue(FIG_ELLIPSE_OFFSET_05_ANGLE_START2, 0, 2);
      p.setValue(FIG_ELLIPSE_OFFSET_06_ANGLE_END2, 360, 2);

      setFigPerfFlag(color, grad, p);
      setFigLinks(p, grad, anchor, filter, sub);
      return p;
   }

   /**
    * Sets up FLAGP based on parameters.
    * <br>
    * <br>
    * <li> {@link ITechFigure#FIG_FLAGP_3OPAQUE}
    * <br>
    * @param color
    * @param grad
    * @param fig
    */
   public void setFigPerfFlag(int color, ByteObject grad, ByteObject fig) {
      if (grad != null) {
         if (!grad.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_4_USEALPHA)) {
            fig.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_3OPAQUE, true);
         }
      } else {
         if (((color >> 24) & 0xFF) == 255) {
            fig.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_3OPAQUE, true);
         }
      }
   }

   /**
    * Ellipse figure
    * @param color
    * @param scolor
    * @param sec
    * @param type
    * @return
    */
   public ByteObject getFigEllipse(int color, int scolor, int sec, int type) {
      ByteObject grad = drc.getGradientFactory().getGradient(scolor, sec, type);
      return getFigEllipse(color, grad, null, null, null);
   }

   /**
    * Default parameters
    * @param filter
    * @return
    */
   public ByteObject getFigFallenPixel(ByteObject filter) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_FALLING_PIXEL_BASIC_SIZE);
      return p;
   }

   /**
    * Grid with mutable seperation sizes and number
    * Will use int[] array from a cache manager to handle separation sizes.
    * Class gets a cache ID
    * @param hsize
    * @param vsize
    * @param color
    * @param sepSize
    * @return
    */
   public ByteObject getFigGrid(int hsize, int vsize, int hcolor, int vcolor) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_GRID_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_11_GRID, 1);
      p.setFlag(FIG_GRID_OFFSET_FLAG, FIG_GRID_FLAG_CACHE_SEP, true);
      p.setValue(FIG_GRID_OFFSET_HSIZE, hsize, 1);
      p.setValue(FIG_GRID_OFFSET_VSIZE, vsize, 1);
      p.setValue(FIG_GRID_OFFSET_VCOLOR, vcolor, 4);
      p.setValue(FIG_GRID_OFFSET_HCOLOR, hcolor, 4);
      //create a pointer for
      return p;
   }

   /**
    * 2 superlines definition
    * In a given xywh, draws as many lines as possible. then according to anchor
    * position grid in the area (by default draws it TOP/LEFT
    * Often used sub figure is the border
    * Intersections may draw a special color or cross hair
    * Starts drawing a vertical line after sepSize, then draws a line of size 
    * @param hsize 
    * @param size
    * @param color
    * @param sepSize
    * @param repeat
    * @param horiz
    * @return
    */
   public ByteObject getFigGrid(int hsize, int vsize, int hcolor, int vcolor, int hSepSize, int vSepSize) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_GRID_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_11_GRID, 1);
      p.setValue(FIG_GRID_OFFSET_HSIZE, hsize, 1);
      p.setValue(FIG_GRID_OFFSET_VSIZE, vsize, 1);
      p.setValue(FIG_GRID_OFFSET_VCOLOR, vcolor, 4);
      p.setValue(FIG_GRID_OFFSET_HCOLOR, hcolor, 4);
      p.setValue(FIG_GRID_OFFSET_HSEPSIZE, hSepSize, 2);
      p.setValue(FIG_GRID_OFFSET_VSEPSIZE, vSepSize, 2);

      return p;
   }

   /**
    * Create a line 
    * @param size
    * @param color
    * @param angle angle of the line from the x,y anchor
    * Anchor of X TOPLEFT
    * Anchor of Y
    * TOPLEFT - BOTTOMRIGHT = Diagonal
    * TOPLEFT-TOPRIGHT = Top Horizontal
    * CENTERTOP - CENTERBOT = Middle Vertical
    * CENTERLEFT- CENTERRIGHT = Middle Horizontal
    * in the implicit rectangle
    * @param anchor
    * @param numsub the number of sub figures
    * @return
    */
   public ByteObject getFigLine(int size, int color, boolean horiz, int anchor, int numsub) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_LINE_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_05_LINE, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      p.setValue(FIG_LINE_OFFSET_2SIZE1, size, 1);
      p.setFlag(FIG_LINE_OFFSET_1FLAG, FIG_LINE_FLAG_HORIZ, horiz);
      return p;
   }

   public ByteObject getFigLine(int[] colors, boolean horiz, int stickTLColor, int stickBRColor, int exSize, int exColor, boolean stickEx, int anchor, int numsub) {
      int psize = getFigSize(FIG_LINE_COLORED_SIZE, numsub);
      psize += colors.length * 4;
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, psize);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_05_LINE, 1);
      p.setFlag(FIG_LINE_OFFSET_1FLAG, FIG_LINE_FLAG_HORIZ, horiz);
      int flagTL = FIG_LINE_FLAGX_STICK_LEFT;
      int flagBR = FIG_LINE_FLAGX_STICK_RIGHT;
      if (horiz) {
         flagTL = FIG_LINE_FLAGX_STICK_TOP;
         flagBR = FIG_LINE_FLAGX_STICK_BOT;
      }
      if (stickTLColor != 0) {
         p.setFlag(FIG_LINE_OFFSET_1FLAG, flagTL, true);
      }
      if (stickBRColor != 0) {
         p.setFlag(FIG_LINE_OFFSET_1FLAG, flagBR, true);
      }
      if (exSize != 0) {
         p.setFlag(FIG_LINE_OFFSET_1FLAG, FIG_LINE_FLAG_EX, true);
         p.setFlag(FIG_LINE_OFFSET_1FLAG, FIG_LINE_FLAG_EX_STICK, true);
      }
      p.setValue(FIG_LINE_OFFSET_EX_SIZE, exSize, 1);
      p.setValue(FIG_LINE_OFFSET_EX_COLOR, exColor, 4);
      return p;
   }

   /**
    * A losange definition from a Triangle. 
    * <br>
    * <br>
    * The opposite direction is used for the other part
    * @param trig
    * @return
    */
   public ByteObject getFigLosange(ByteObject trig, boolean pap, boolean contour) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_LOSANGE_BASIC_SIZE);
      p.setFlag(FIG_LOSANGE_OFFSET_1FLAG, FIG_LOSANGE_FLAG_4NOED_PAPILLION, pap);
      p.addByteObject(trig);
      return p;
   }

   public ByteObject getFigLosange(ByteObject trig1, ByteObject trig2) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_LOSANGE_BASIC_SIZE);

      return p;
   }

   /**
    * Some flag draw a Noeud Pap
    * Many different types of gradient since we have 2 triangles
    * <br>
    * <br>
    * TODO add a losange proportion. 1 byte decide ratio for Top/Left, 1 byte for Bot/Right
    * @param color
    * @param overstep
    * @param horiz
    * @return
    */
   public ByteObject getFigLosange(int color, int overstep, boolean horiz, boolean pap, boolean contour, ByteObject grad) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_LOSANGE_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_06_LOSANGE, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      p.setValue(FIG_LOSANGE_OFFSET_2OVERSTEP2, overstep, 2);
      p.setFlag(FIG_LOSANGE_OFFSET_1FLAG, FIG_LOSANGE_FLAG_1HORIZ, horiz);
      p.setFlag(FIG_LOSANGE_OFFSET_1FLAG, FIG_LOSANGE_FLAG_2NEG_OVERSTEP, overstep < 0);
      p.setFlag(FIG_LOSANGE_OFFSET_1FLAG, FIG_LOSANGE_FLAG_3CONTOUR, contour);
      p.setFlag(FIG_LOSANGE_OFFSET_1FLAG, FIG_LOSANGE_FLAG_4NOED_PAPILLION, pap);
      setFigLinks(p, grad, null, null);
      return p;
   }

   /**
    * Fixed 0 seed for pixels
    * @param len
    * @param randomSize
    * @param randomColor
    * @param colors
    * @return
    */
   public ByteObject getFigPixels(int len, boolean randomSize, boolean randomColor, int[] colors) {
      return getFigPixels(len, randomSize, randomColor, colors, 0);
   }

   /**
    * 
    * @param len
    * @param randomSize
    * @param randomColor
    * @param colors will define the maximum number of different colors to be used
    * @param seed
    * @param numsub
    * @return
    */
   public ByteObject getFigPixels(int len, boolean randomSize, boolean randomColor, int[] colors, int seed) {
      int size = FIG_PIXEL_BASIC_SIZE;
      size += colors.length * 4;
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, size);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_09_PIXELS, 1);
      p.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_1RGB, true);
      p.setFlag(FIG_PIXEL_OFFSET_01_FLAG, FIG_PIXEL_FLAG_2_RANDOM_COLOR, randomColor);
      p.setFlag(FIG_PIXEL_OFFSET_01_FLAG, FIG_PIXEL_FLAG_1_RANDOM_SIZE, randomSize);
      p.setValue(FIG_PIXEL_OFFSET_07_LENGTH_H2, len, 2);
      p.setValue(FIG_PIXEL_OFFSET_03_SEED4, seed, 4);
      p.setDynOverWriteValues(FIG_PIXEL_OFFSET_04_COLORSX, colors, 4);
      return p;
   }

   /**
    * 
    * @param lenH
    * @param lenV vertical size
    * @param randomSize
    * @param randomColor
    * @param smooth smooth filter modifies adjacent pixels like the Scale Filter
    * @param colors
    * @param seed
    * @param numsub
    * @return
    */
   public ByteObject getFigPixels(int lenH, int lenV, boolean randomSize, boolean randomColor, boolean smooth, int[] colors, int seed) {
      int size = FIG_PIXEL_BASIC_SIZE;
      size += colors.length * 4;
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, size);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_09_PIXELS, 1);
      p.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_1RGB, true);
      p.setFlag(FIG_PIXEL_OFFSET_01_FLAG, FIG_PIXEL_FLAG_2_RANDOM_COLOR, randomColor);
      p.setFlag(FIG_PIXEL_OFFSET_01_FLAG, FIG_PIXEL_FLAG_1_RANDOM_SIZE, randomSize);
      p.setValue(FIG_PIXEL_OFFSET_07_LENGTH_H2, lenH, 2);
      p.setValue(FIG_PIXEL_OFFSET_3VLENGTH2, lenV, 2);
      p.setValue(FIG_PIXEL_OFFSET_03_SEED4, seed, 4);
      p.setDynOverWriteValues(FIG_PIXEL_OFFSET_04_COLORSX, colors, 4);
      return p;
   }

   public ByteObject getFigRect(int color) {
      return getFigRect(color, null, null, null, null);
   }

   public ByteObject getFigRect(int color, boolean doAlpha) {
      ByteObject p = getFigRect(color);
      setDoAlplay(p, doAlpha);
      return p;
   }

   public ByteObject getFigRect(int color, ByteObject grad, boolean isDoAlpha) {
      ByteObject figBg = getFigRect(color, grad, null, null, null);
      if (isDoAlpha) {
         setDoAlplay(figBg, true);
      }
      return figBg;
   }

   public ByteObject getFigRect(int color, ByteObject grad) {
      return getFigRect(color, grad, null, null, null);
   }

   public ByteObject getFigRect(int color, ByteObject grad, ByteObject anchor) {
      return getFigRect(color, grad, anchor, null, null);
   }

   public ByteObject getFigRect(int color, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] sub) {
      return getFigRect(color, -1, -1, grad, anchor, filter, sub);
   }

   /**
    * 
    * @param fillVert should the gradient be vertical?
    * @param pcolor
    * @param scolor
    * @param maxSec
    * @return
    */
   public ByteObject getFigRect(boolean fillVert, int pcolor, int scolor, int maxSec) {
      int type = ITechGradient.GRADIENT_TYPE_RECT_01_HORIZ;
      if (fillVert) {
         type = ITechGradient.GRADIENT_TYPE_RECT_02_VERT;
      }
      ByteObject grad = drc.getGradientFactory().getGradient(scolor, maxSec, type);
      return getFigRect(pcolor, grad, null);
   }

   public ByteObject getFigRect(int color, int arcw, int arch, ByteObject grad) {
      return getFigRect(color, arcw, arch, 0, grad, null, null, null);
   }

   /**
    * Filled Rectangle
    * @param color
    * @param arcw
    * @param arch
    * @param grad
    * @param anchor
    * @param filter
    * @param sub
    * @return
    */
   public ByteObject getFigRect(int color, int arcw, int arch, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] sub) {
      return getFigRect(color, arcw, arch, 0, grad, anchor, filter, sub);
   }

   public ByteObject getFigRect(int pcolor, int scolor, int maxSec, int gradType) {
      ByteObject grad = drc.getGradientFactory().getGradient(scolor, maxSec, gradType);
      return getFigRect(pcolor, grad, null, null, null);
   }

   /**
    * 
    * @param color
    * @param arcw
    * @param arch
    * @param fillSize <= 0 means a filled rectangle. A value of 1 means the figure drawing method will
    * call the drawRectangle primitive once. 5 means a 5 pixels rectangle 
    * @param grad
    * @param anchor
    * @param filter
    * @param sub
    * @return
    */
   public ByteObject getFigRect(int color, int arcw, int arch, int fillSize, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] sub) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_RECTANGLE_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_01_RECTANGLE, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      if (fillSize < 0)
         fillSize = 0;
      p.setValue(FIG_RECTANGLE_OFFSET_4SIZEF1, fillSize, 1);

      if (arcw != -1 || arch != -1) {
         p.setFlag(FIG_RECTANGLE_OFFSET_1FLAG, FIG_RECTANGLE_FLAG_1ROUND, true);
      }
      if (arcw != -1) {
         p.setFlag(FIG_RECTANGLE_OFFSET_1FLAG, FIG_RECTANGLE_FLAG_7ARCW1, true);
         p.setValue(FIG_RECTANGLE_OFFSET_2ARCW1, arcw, 1);
      }
      if (arch != -1) {
         p.setFlag(FIG_RECTANGLE_OFFSET_1FLAG, FIG_RECTANGLE_FLAG_8ARCH1, true);
         p.setValue(FIG_RECTANGLE_OFFSET_3ARCH1, arch, 1);
      }
      //TODO merge with genetic flag
      p.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_5IGNORE_ALPHA, true);
      if (arch <= 0 && arcw <= 0) {
         //when rounded edges, figure is not shape opaque.
         setFigPerfFlag(color, grad, p);
      }
      setFigLinks(p, grad, anchor, filter, sub);
      return p;

   }

   public void setFigASUBS(ByteObject fig, ByteObject anchor, ByteObject[] subs) {
      if (anchor != null) {

      }
      if (subs != null) {

      }
   }

   public void setFigLinks(ByteObject figure, ByteObject d1, ByteObject d2, ByteObject d3, ByteObject[] sub) {
      int l = 0;
      if (d1 != null) {
         l++;
      }
      if (d2 != null)
         l++;
      if (d3 != null)
         l++;
      if (sub != null)
         l += sub.length;
      ByteObject[] ps = new ByteObject[l];
      l = 0;
      if (d1 != null) {
         ps[l] = d1;
         l++;
      }
      if (d2 != null) {
         ps[l] = d2;
         l++;
      }
      if (d3 != null) {
         ps[l] = d3;
         l++;
      }
      if (sub != null) {
         for (int i = 0; i < sub.length; i++) {
            ps[l] = sub[i];
            l++;
         }
      }
      setFigLinks(figure, ps);
   }

   public void setFigLinks(ByteObject figure, ByteObject d1, ByteObject d2, ByteObject[] sub) {
      setFigLinks(figure, d1, d2, null, sub);
   }

   /**
    * Works for the following types:
    * <li> {@link IBOTypesDrw#TYPE_056_COLOR_FILTER}
    * <li> {@link IBOTypesDrw#TYPE_050_FIGURE}
    * <li> {@link IBOTypesDrw#TYPE_051_BOX}
    * <li> {@link IBOTypesDrw#TYPE_059_GRADIENT}
    * 
    * @param figure
    * @param links Cannot be reused
    */
   public void setFigLinks(ByteObject figure, ByteObject[] links) {
      if (links == null)
         return;
      figure.checkType(IBOTypesDrw.TYPE_050_FIGURE);
      for (int i = 0; i < links.length; i++) {
         int type = links[i].getType();
         int flag = 0;
         switch (type) {
            case IBOTypesDrw.TYPE_056_COLOR_FILTER:
               flag = FIG_FLAG_5_FILTER;
               break;
            case IBOTypesDrw.TYPE_050_FIGURE:
               flag = FIG_FLAG_7_SUB_FIGURE;
               break;
            case IBOTypesDrw.TYPE_058_MASK:
               flag = FIG_FLAG_4_MASK;
               break;
            case IBOTypesDrw.TYPE_051_BOX:
               flag = FIG_FLAG_1_ANCHOR;
               break;
            case IBOTypesBOC.TYPE_007_LIT_ARRAY_INT:
               flag = FIG_FLAG_3_COLOR_ARRAY;
               break;
            case IBOTypesDrw.TYPE_059_GRADIENT:
               flag = FIG_FLAG_2_GRADIENT;
               break;
            default:
               break;
         }
         figure.setFlag(FIG__OFFSET_02_FLAG, flag, true);
      }
      figure.setByteObjects(links);
   }

   /**
    * Updates the flag accordingly.
    * If there was already subfigures, new sub figures are appended.
    * @param figure
    * @param subFigures the array may be reused after this call. A copy is made anyways
    */
   public void setFigSubFigures(ByteObject figure, ByteObject[] subFigures) {
      figure.setFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_7_SUB_FIGURE, true);
      figure.addByteObject(subFigures);
   }

   /**
    * TODO
    * Rectangle figure whose borders are chopped away.
    * <br>
    * When all triangles of same size, implemented with a clipped losange.
    * @param color
    * @param grad
    * @return
    */
   public ByteObject getFigRectChopped(int color, ByteObject grad) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_RECTANGLE_BASIC_SIZE);

      return p;
   }

   public ByteObject getFigRectOpaque(int color) {
      return getRect((color & 0xFFFFFF) + (255 << 24));
   }

   public ByteObject getFigRectOpaque(int color, ByteObject grad) {
      return getFigRect((color & 0xFFFFFF) + (255 << 24), grad);
   }

   /**
    * Figure that will repeat a figure using the given full anchor
    * @param figure
    * @param anchor defines the size of the unit. align decides what do to with extra space in the
    * defining rectangle area
    * @return
    */
   public ByteObject getFigRepeater(ByteObject figure, ByteObject anchor) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_REPEATER_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_13_REPEATER, 1);
      p.setByteObjects(new ByteObject[] { figure, anchor });
      return p;
   }

   public ByteObject getFigRepeater(ByteObject figure, int w, int h) {
      ByteObject anchor = drc.getBoxFactory().getCenter(w, h);
      return getFigRepeater(figure, anchor);
   }

   /**
    * When mode is to 
    * @param size
    * @param numSubs
    * @return
    */
   private int getFigSize(int size, int numSubs) {
      size += FIG__BASIC_SIZE;
      if (numSubs != 0) {
         size += 1;
         size += (numSubs * 2);
      }
      return size;
   }

   public ByteObject getFigString(IMFont f, int color) {
      return getFigString(null, f.getFace(), f.getStyle(), f.getSize(), color, null, null, null);
   }

   /**
    * Simple text effect definition.
    * 
    * @param face
    * @param st
    * @param size
    * @param color
    * @return
    */
   public ByteObject getFigString(int face, int style, int size, int color) {
      return getFigString(null, face, style, size, color, null, null, null);
   }

   /**
    * Black string
    * @param pooled true if String to be merged in the repository
    * @param str
    * @param f
    * @return
    */
   public ByteObject getFigString(String str, IMFont f) {
      return getFigString(str, f.getFace(), f.getStyle(), f.getSize(), ColorUtils.FULLY_OPAQUE_BLACK, null, null, null, null);
   }

   /**
    * Basic string with no special text effects
    * @param str maybe null in which case Implicit definition
    * @param face
    * @param style
    * @param size
    * @param color
    * @return
    */
   public ByteObject getFigString(String str, int face, int style, int size, int color) {
      return getFigString(str, face, style, size, color, null, null, null, null);
   }

   /**
    * 
    * @param str null for
    * @param face -1 = undefined
    * @param style -1 = undefined
    * @param size
    * @param color
    * @param effects text effects order in array is order 
    * Descriptive text effect type is first
    * THen Mask
    * Last is Scale
    * This is in addition to the Filter of the Figure
    * @return
    */
   public ByteObject getFigString(String str, int face, int style, int size, int color, ByteObject effects, ByteObject mask, ByteObject scale) {
      return getFigString(str, face, style, size, color, effects, mask, scale, null);
   }

   /**
    * Create a transparent String definition
    * <br>
    * <br>
    * @param pooled
    * @param str
    * @param face {@link ITechFont#FACE_MONOSPACE}
    * @param style {@link ITechFont#STYLE_BOLD}
    * @param size {@link ITechFont#SIZE_3_MEDIUM}
    * @param color
    * @param c
    * @param effects effects to apply on the String
    * @param mask figure level mask. if any Fx has a Mask, this field will be ignored when figure is drawn.
    * @param scale scaler to use in order to fit String
    * @param anchor anchor is string in figure area when scaler is null.
    * @return
    */
   public ByteObject getFigString(String str, int face, int style, int size, int color, ByteObject effects, ByteObject mask, ByteObject scale, ByteObject anchor) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_STRING_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_10_STRING, 1);

      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      p.setValue(FIG_STRING_OFFSET_02_FACE1, face, 1);
      p.setValue(FIG_STRING_OFFSET_03_STYLE1, style, 1);
      p.setValue(FIG_STRING_OFFSET_04_SIZE1, size, 1);
      int num = 0;
      ByteObject raw = null;
      if (str != null) {
         p.setFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_6_EXPLICIT, true);
         if (str.length() == 1) {
            p.setFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_7_CHAR, true);
            p.setValue(FIG_STRING_OFFSET_05_CHAR2, str.charAt(0), 2);
         } else {
            p.setFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_4_RAW, true);
            raw = boc.getLitteralStringFactory().getLitteralString(str);
            num++;
         }
      }
      if (effects != null) {
         p.setFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_5_EFFECT, true);
         num++;
      }
      if (mask != null) {
         p.setFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_5_EFFECT, true);
         num++;
      }
      if (scale != null) {
         p.setFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_5_EFFECT, true);
         num++;
      }
      if (anchor != null) {
         p.setFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_1_ANCHOR, true);
         num++;
      }
      ByteObject[] pa = new ByteObject[num];
      int count = 0;
      if (raw != null) {
         pa[count] = raw;
         count++;
      }
      if (effects != null) {
         pa[count] = effects;
         count++;
      }
      if (mask != null) {
         pa[count] = mask;
         count++;
      }
      if (scale != null) {
         pa[count] = scale;
         count++;
      }
      if (anchor != null) {
         pa[count] = anchor;
         count++;
      }

      p.setByteObjects(pa);
      return p;
   }

   /**
    * 
    * @param str
    * @param f array with face/style/size
    * @param color
    * @param effects
    * @param mask
    * @param scale
    * @param anchor
    * @return
    */
   public ByteObject getFigString(String str, int[] f, int color, ByteObject effects, ByteObject mask, ByteObject scale, ByteObject anchor) {
      return getFigString(str, f[0], f[1], f[2], color, effects, mask, scale, anchor);
   }

   /**
    * Implicit String, Undefined color 
    * @param face
    * @param st
    * @param size
    * @return
    */
   public ByteObject getFigStringT(int face, int style, int size) {
      ByteObject p = getFigString(null, face, style, size, 0, null, null, null);
      ByteObject mm = drc.getMergeMaskFactory().getMergeMask(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_1);
      mm.setFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_2, true);
      mm.setFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_3, true);
      p.addByteObject(mm);
      return p;
   }

   /**
    * Create a color composer
    * @param mod
    * @param type
    * @param alpha
    * @return
    */
   public ByteObject getPxStar(int lenH, int lenV) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_063_PIX_STAR, PIXSTAR_BASIC_SIZE);
      p.set1(PIXSTAR_OFFSET_03_TOP_SIZE1, lenV);
      p.set1(PIXSTAR_OFFSET_04_BOT_SIZE1, lenV);
      p.set1(PIXSTAR_OFFSET_05_LEFT_SIZE1, lenH);
      p.set1(PIXSTAR_OFFSET_06_RIGHT_SIZE1, lenH);
      return p;
   }

   /**
    * A String Figure. Scaling will be used to fit the area given to the figures
    * String figure don't usually have their mask defined at the figure level
    * Transparent definition
    * @param str
    * @param txteffect
    * @return
    */
   public ByteObject getFigStringT(String str, ByteObject txteffect, ByteObject scale) {
      ByteObject p = getFigString(str, 0, 0, 0, 0, txteffect, null, scale, null);
      ByteObject mm = drc.getMergeMaskFactory().getMergeMask(MERGE_MASK_OFFSET_1FLAG1, FIG_STRING_FLAG_5_EFFECT);
      p.addByteObject(mm);
      return p;
   }

   /**
    * Transparent text definition with just a color.
    * @param color
    * @return
    */
   public ByteObject getFigStringTColor(int color) {
      return getFigStringTrans(FIG__OFFSET_06_COLOR4, 4, color, MERGE_MASK_OFFSET_5VALUES1, ITechMergeMaskFigure.MM_VALUES5_FLAG_2_COLOR);
   }

   /**
    * Figure with only its size defined.
    * <br>
    * <br>
    * 
    * @param size
    * @return
    */
   public ByteObject getFigStringTFontSize(int size) {
      return getFigStringTrans(FIG_STRING_OFFSET_04_SIZE1, 1, size, MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_3);
   }

   /**
    * All default values but the Bold value for the Font
    * @param text
    * @return
    */
   public ByteObject getFigStringTFontStyle(int style) {
      return getFigStringTrans(FIG_STRING_OFFSET_03_STYLE1, 1, style, MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_2);
   }

   /**
    * More at {@link MergeMaskOperator}.
    * <br>
    * <br>
    * @param offset
    * @param size
    * @param value
    * @param mergeOffset
    * @param mergeFlag
    * @return
    */
   public ByteObject getFigStringTrans(int offset, int size, int value, int mergeOffset, int mergeFlag) {
      ByteObject figure = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_STRING_BASIC_SIZE);
      figure.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_10_STRING, 1);
      figure.setValue(offset, value, size);
      drc.getMergeMaskFactory().setMergeMask(figure, mergeOffset, mergeFlag);
      return figure;
   }

   /**
    * 
    * @param size size of line
    * @param color
    * @param separation
    * @param repeat
    * @return
    */
   public ByteObject getFigSuperLines(int size, int color, int separation, int repeat, boolean horiz) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_SL_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_16_SUPERLINES, 1);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_1SIMPLE, true);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_2ANGLE, false);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_3HORIZ, horiz);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      p.setValue(FIG_SL_OFFSET_3REPEAT2, repeat, 2);
      p.setValue(FIG_SL_OFFSET_4SEPARATION2, separation, 2);
      p.setValue(FIG_SL_OFFSET_2LINE_SIZE1, size, 1);
      return p;
   }

   /**
    * Size of lines is defined by color array.
    * @param lineColors
    * @param separation
    * @param repeat
    * @param horiz
    * @return
    */
   public ByteObject getFigSuperLines(int[] lineColors, int separation, int repeat, boolean horiz) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_SL_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_16_SUPERLINES, 1);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_1SIMPLE, true);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_5EXPLICIT_COLORS, true);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_2ANGLE, false);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_3HORIZ, horiz);
      p.setValue(FIG_SL_OFFSET_3REPEAT2, repeat, 2);
      p.setValue(FIG_SL_OFFSET_4SEPARATION2, separation, 2);
      return p;
   }

   public ByteObject getFigSuperLines(int[] lineColors, int[] separations, boolean horiz) {
      int sizeDrw = FIG_SL_BASIC_SIZE;
      int maxSep = BitUtils.getMaxByteSize(separations);
      int sizeAdd = 4 * lineColors.length + (maxSep * separations.length) + 3 + 3;
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, sizeDrw + sizeAdd);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_16_SUPERLINES, 1);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_4EXPLICIT_SEP, true);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_5EXPLICIT_COLORS, true);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_2ANGLE, false);
      p.setFlag(FIG_SL_OFFSET_1FLAG, FIG_SL_FLAG_3HORIZ, horiz);
      p.setDynOverWriteValues(FIG_SL_OFFSET_4SEPARATION2, separations, maxSep);
      p.setDynOverWriteValues(FIG_SL_OFFSET_2LINE_SIZE1, lineColors, 4);
      return p;
   }

   /**
    * 
    * @param color
    * @param angle
    * @return
    */
   public ByteObject getFigTriangle(int color, int angle) {
      return getFigTriangle(color, angle, 0, null, null);
   }

   public ByteObject getFigTriangle(int angle, int color, ByteObject grad, ByteObject anchor, ByteObject[] subs) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_TRIANGLE_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_3_TRIANGLE, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      //custom values of triangle
      p.setValue(FIG_TRIANGLE_OFFSET_2ANGLE2, angle, 2);
      setFigLinks(p, grad, anchor, subs);
      return p;
   }

   /**
    * Basic Triangle with one color. no anchor
    * Default is intuitive
    * Top is 
    * @param angle
    * @param color
    * @param percent
    * @return
    */
   public ByteObject getFigTriangle(int color, int angle, int h) {
      return getFigTriangle(color, angle, h, null, null);
   }

   /**
    * 
    * @param color
    * @param angle when angle is 0 - 90 - 180 - 270, put to a single direction.
    * @param h as a percentage
    * @param isAngle
    * @param grad
    * @param anchor
    * @param filter
    * @param subs
    * @return
    */
   public ByteObject getFigTriangle(int color, int angle, int h, boolean isAngle, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] subs) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_TRIANGLE_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_3_TRIANGLE, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      p.setValue(FIG_TRIANGLE_OFFSET_2ANGLE2, angle, 2);
      p.setValue(FIG_TRIANGLE_OFFSET_3h4, h, 4);
      setFigLinks(p, grad, anchor, filter, subs);
      p.setFlag(FIG_TRIANGLE_OFFSET_1FLAG1, FIG_TRIANGLE_FLAG_2ANGLE, isAngle);
      return p;
   }

   /**
    * 
    * @param color
    * @param angle 0-360 degree angle
    * @param h
    * @param grad
    * @return
    */
   public ByteObject getFigTriangle(int color, int angle, int h, ByteObject grad) {
      return getFigTriangle(color, angle, h, grad, null, null, null);
   }

   /**
    * Create a triangle with an anchor
    * @param anchor 32 bits anchor
    * @param angle
    * @param color
    * @param h in percent of big H
    * @return
    */
   public ByteObject getFigTriangle(int color, int angle, int h, ByteObject grad, ByteObject anchor) {
      return getFigTriangle(color, angle, h, grad, anchor, null, null);
   }

   public ByteObject getFigTriangle(int color, int angle, int h, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] subs) {
      return getFigTriangle(color, angle, h, true, grad, anchor, filter, subs);
   }

   public ByteObject getFigTriangleDir(int color, int dir, int h, ByteObject grad) {
      return getFigTriangle(color, dir, h, false, grad, null, null, null);
   }

   public ByteObject getFigTriangleDir(int color, int dir, int h, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] subs) {
      return getFigTriangle(color, dir, h, false, grad, anchor, filter, subs);
   }

   public ByteObject getFigTriangleType(int color, int type) {
      return getFigTriangleType(color, type, null);
   }

   /**
    * 
    * @param color
    * @param type {@link C#TYPE_00TOP} etc.
    * @param grad
    * @return
    */
   public ByteObject getFigTriangleType(int color, int type, ByteObject grad) {
      return getFigTriangleType(color, type, grad, null);
   }

   /**
    *  {@link C#TYPE_00TOP}
    *  {@link C#TYPE_01BOTTOM}
    *  {@link C#TYPE_02LEFT}
    *  
    * @param color
    * @param type {@link C#TYPE_00TOP}
    * @param grad
    * @param anchor
    * @return
    */
   public ByteObject getFigTriangleType(int color, int type, ByteObject grad, ByteObject anchor) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_TRIANGLE_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_3_TRIANGLE, 1);
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      p.setValue(FIG_TRIANGLE_OFFSET_2ANGLE2, type, 2);
      setFigLinks(p, grad, anchor, null, null);
      return p;
   }

   public ByteObject getRect(int color) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_050_FIGURE, FIG_RECTANGLE_BASIC_SIZE);
      p.setValue(FIG__OFFSET_01_TYPE1, FIG_TYPE_01_RECTANGLE, 1);
      if (((color >> 24) & 0xFF) == 255) {
         p.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_3OPAQUE, true);
      }
      p.setValue(FIG__OFFSET_06_COLOR4, color, 4);
      return p;
   }

   public ByteObject getRect(int color, boolean alpha) {
      ByteObject p = getRect(color);
      setDoAlplay(p, alpha);
      return p;
   }

   public ByteObject getRect(int color, ByteObject gradient) {
      return getFigRect(color, gradient);
   }

   /**
    * Create a String figure definition
    * 
    * Face. One can use the framework fonts with logic sizes
    * <li> {@link IMFont#FACE_MONOSPACE}
    * <li> {@link IMFont#FACE_PROPORTIONAL}
    * <li> {@link IMFont#FACE_SYSTEM}
    * 
    * Size is
    * <li> {@link IMFont#SIZE_4_LARGE}
    * <li> {@link IMFont#SIZE_3_MEDIUM}
    * <li> {@link IMFont#SIZE_2_SMALL}
    * <li> {@link IMFont#SIZE_5_HUGE}
    * <li> {@link IMFont#SIZE_1_TINY}
    * 
    * @param mod
    * @param str can be null.
    * @param face
    * @param style
    * @param size
    * @param color
    * @param effects
    * @param mask
    * @param scale
    * @param anchor
    * @return
    */
   public ByteObject getString(String str, int face, int style, int size, int color, ByteObject effects, ByteObject mask, ByteObject scale, ByteObject anchor) {
      return getFigString(str, face, style, size, color, effects, mask, scale);
   }

   public ByteObject getTriangle(int color, int angle, int h) {
      return getFigTriangle(color, angle, h);
   }

   public void setDoAlplay(ByteObject p, boolean doAlpha) {
      if (doAlpha) {
         p.setFlag(FIG__OFFSET_02_FLAG, FIG_FLAGP_5IGNORE_ALPHA, false);
         p.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_3OPAQUE, false);
      } else {
         p.setFlag(FIG__OFFSET_02_FLAG, FIG_FLAGP_5IGNORE_ALPHA, true);
         p.setFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_3OPAQUE, true);
      }
   }

   public void toStringFigure1Line(ByteObject bo, Dctx sb) {
      sb.rootN(bo, "Figure");
      final int figType = bo.getValue(FIG__OFFSET_01_TYPE1, 1);
      switch (figType) {
         case FIG_TYPE_01_RECTANGLE:
            if (bo.hasFlag(FIG_RECTANGLE_OFFSET_1FLAG, FIG_RECTANGLE_FLAG_1ROUND)) {
               sb.append("Round ");
            }
            sb.append("Rectangle ");
            sb.append(toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)));
            sb.append(" arc=[");
            sb.append(bo.get1(FIG_RECTANGLE_OFFSET_2ARCW1));
            sb.append(',');
            sb.append(bo.get1(FIG_RECTANGLE_OFFSET_3ARCH1));
            sb.append(']');
            sb.append(" sizeFill=" + bo.get1(FIG_RECTANGLE_OFFSET_4SIZEF1));
            break;
         case FIG_TYPE_02_BORDER:
            sb.append("Border ");
            sb.append((toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))));
            if (bo.hasFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_1OUTER)) {
               sb.append(" Outer");
            }
            if (bo.hasFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_1OUTER)) {
               sb.append(" Outer");
            }

            sb.append(" Corners=" + (bo.hasFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_4COIN)) + " ");
            sb.append(" CornerShift=" + bo.get1(FIG_BORDER_OFFSET_2CORNER_SHIFT1));
            break;
         case FIG_TYPE_06_LOSANGE:
            sb.append("Losange = ");
            sb.append((toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))));
            break;
         case FIG_TYPE_13_REPEATER:
            sb.append("Repeater");
            sb.nl();
            sb.append(" color=" + (toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))) + " ");
            sb.nl();
            sb.append(" forceCopyArea=" + bo.hasFlag(FIG_REPEATER_OFFSET_1FLAG, FIG_REPEATER_FLAG_1FORCECOPYAREA));
            sb.append(" BgColor=" + bo.hasFlag(FIG_REPEATER_OFFSET_1FLAG, FIG_REPEATER_FLAG_2USE_BGCOLOR));
            break;
         case FIG_TYPE_17_ARLEQUIN:
            sb.append("Arlequin");
            sb.append("pcolor = " + toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)) + " ");
            sb.append("scolor = " + toStringColor(bo.get4(FIG_ARLEQUIN_OFFSET_2COLOR4)) + " ");
            break;
         case FIG_TYPE_3_TRIANGLE:
            sb.append("Triangle");
            sb.append((toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))));
            sb.append(" angle = " + (bo.getValue(FIG_TRIANGLE_OFFSET_2ANGLE2, 2)) + " ");
            int h = bo.getValue(FIG_TRIANGLE_OFFSET_3h4, 4);
            if (h != 0) {
               sb.append("h = " + h);
            }
            break;
         case FIG_TYPE_16_SUPERLINES:
            sb.append("SUPERLINES");
            sb.nl();
            sb.append("color = " + toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)));
            sb.append(" sepsize = " + (bo.getValue(FIG_SL_OFFSET_4SEPARATION2, 2)));
            sb.append(" repeat = " + (bo.getValue(FIG_SL_OFFSET_3REPEAT2, 2)));
            break;
         case FIG_TYPE_07_ELLIPSE:
            sb.append("Ellipse = ");
            sb.append((toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))));
            break;
         case FIG_TYPE_10_STRING:
            sb.append("String ");
            sb.append(toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)));
            if (bo.hasFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_6_EXPLICIT)) {
               if (bo.hasFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_7_CHAR)) {
                  sb.append("char=" + (char) bo.get2(FIG_STRING_OFFSET_05_CHAR2));
               } else if (bo.hasFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_4_RAW)) {
                  ByteObject raw = bo.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
                  sb.append("string = " + boc.getLitteralStringOperator().getLitteralString(raw));
               }
            }
            sb.append(" Font=");
            sb.append("[" + ToStringStaticCoreDraw.fontFace(bo.get1(FIG_STRING_OFFSET_02_FACE1)));
            sb.append("," + ToStringStaticCoreDraw.debugFontStyle(bo.get1(FIG_STRING_OFFSET_03_STYLE1)));
            sb.append("," + ToStringStaticCoreDraw.debugFontSize(bo.get1(FIG_STRING_OFFSET_04_SIZE1)));
            sb.append("]");
            break;
         case FIG_TYPE_09_PIXELS:
            sb.append("PIXELS");
            sb.nl();
            sb.append("color = " + toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)));
            sb.append(" len = " + (bo.getValue(FIG_PIXEL_OFFSET_07_LENGTH_H2, 2)));
            sb.append(" rndColor = " + (bo.hasFlag(FIG_PIXEL_OFFSET_01_FLAG, FIG_PIXEL_FLAG_2_RANDOM_COLOR)));
            sb.append(" rndLength = " + (bo.hasFlag(FIG_PIXEL_OFFSET_01_FLAG, FIG_PIXEL_FLAG_1_RANDOM_SIZE)));
            int[] colors = bo.getValues(FIG_PIXEL_OFFSET_04_COLORSX);
            sb.nl();
            sb.append(" colors ");
            for (int i = 0; i < colors.length; i++) {
               sb.append(" " + toStringColor(colors[i]));
            }
         default:
            sb.append("UNKNOWN FIG = " + figType);
            break;
      }
   }

   public void toStringFigure(ByteObject bo, Dctx sb) {
      sb.rootN(bo, "Figure");
      final int figType = bo.getValue(FIG__OFFSET_01_TYPE1, 1);
      switch (figType) {
         case FIG_TYPE_01_RECTANGLE:
            if (bo.hasFlag(FIG_RECTANGLE_OFFSET_1FLAG, FIG_RECTANGLE_FLAG_1ROUND)) {
               sb.append("Round ");
            }
            sb.append("Rectangle ");
            sb.append(toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)));
            sb.append(" arc=[");
            sb.append(bo.get1(FIG_RECTANGLE_OFFSET_2ARCW1));
            sb.append(',');
            sb.append(bo.get1(FIG_RECTANGLE_OFFSET_3ARCH1));
            sb.append(']');
            sb.append(" sizeFill=" + bo.get1(FIG_RECTANGLE_OFFSET_4SIZEF1));
            break;
         case FIG_TYPE_02_BORDER:
            sb.append("Border ");
            sb.append((toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))));
            if (bo.hasFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_1OUTER)) {
               sb.append(" Outer");
            }
            if (bo.hasFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_1OUTER)) {
               sb.append(" Outer");
            }

            sb.append(" Corners=" + (bo.hasFlag(FIG_BORDER_OFFSET_1FLAG, FIG_BORDER_FLAG_4COIN)) + " ");
            sb.append(" CornerShift=" + bo.get1(FIG_BORDER_OFFSET_2CORNER_SHIFT1));
            break;
         case FIG_TYPE_06_LOSANGE:
            sb.append("Losange = ");
            sb.append((toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))));
            break;
         case FIG_TYPE_13_REPEATER:
            sb.append("#Repeater");
            sb.nl();
            sb.append(" color=" + (toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))) + " ");
            sb.nl();
            sb.append(" forceCopyArea=" + bo.hasFlag(FIG_REPEATER_OFFSET_1FLAG, FIG_REPEATER_FLAG_1FORCECOPYAREA));
            sb.append(" BgColor=" + bo.hasFlag(FIG_REPEATER_OFFSET_1FLAG, FIG_REPEATER_FLAG_2USE_BGCOLOR));
            break;
         case FIG_TYPE_17_ARLEQUIN:
            sb.append("#Arlequin");
            sb.nl();
            sb.append("pcolor = " + toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)) + " ");
            sb.nl();
            sb.append("scolor = " + toStringColor(bo.get4(FIG_ARLEQUIN_OFFSET_2COLOR4)) + " ");
            break;
         case FIG_TYPE_3_TRIANGLE:
            sb.append("Triangle = ");
            sb.append((toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))));
            sb.append(" angle = " + (bo.getValue(FIG_TRIANGLE_OFFSET_2ANGLE2, 2)) + " ");
            int h = bo.getValue(FIG_TRIANGLE_OFFSET_3h4, 4);
            if (h != 0) {
               sb.append("h = " + h);
            }
            break;
         case FIG_TYPE_16_SUPERLINES:
            sb.append("SUPERLINES");
            sb.nl();
            sb.append("color = " + toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)));
            sb.append(" sepsize = " + (bo.getValue(FIG_SL_OFFSET_4SEPARATION2, 2)));
            sb.append(" repeat = " + (bo.getValue(FIG_SL_OFFSET_3REPEAT2, 2)));
            break;
         case FIG_TYPE_07_ELLIPSE:
            sb.append("Ellipse = ");
            sb.append((toStringColor(bo.get4(FIG__OFFSET_06_COLOR4))));
            break;
         case FIG_TYPE_10_STRING:
            sb.append("String ");
            sb.append(toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)));
            if (bo.hasFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_6_EXPLICIT)) {
               if (bo.hasFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_7_CHAR)) {
                  sb.append("char=" + (char) bo.get2(FIG_STRING_OFFSET_05_CHAR2));
               } else if (bo.hasFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_4_RAW)) {
                  ByteObject raw = bo.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
                  sb.append("string = " + boc.getLitteralStringOperator().getLitteralString(raw));
               }
            }
            sb.append(" Font=");

            sb.append("[" + ToStringStaticCoreDraw.fontFace(bo.get1(FIG_STRING_OFFSET_02_FACE1)));
            sb.append("," + ToStringStaticCoreDraw.debugFontStyle(bo.get1(FIG_STRING_OFFSET_03_STYLE1)));
            sb.append("," + ToStringStaticCoreDraw.debugFontSize(bo.get1(FIG_STRING_OFFSET_04_SIZE1)));
            sb.append("]");
            break;
         case FIG_TYPE_09_PIXELS:
            sb.append("PIXELS");
            sb.nl();
            sb.append("color = " + toStringColor(bo.get4(FIG__OFFSET_06_COLOR4)));
            sb.append(" len = " + (bo.getValue(FIG_PIXEL_OFFSET_07_LENGTH_H2, 2)));
            sb.append(" rndColor = " + (bo.hasFlag(FIG_PIXEL_OFFSET_01_FLAG, FIG_PIXEL_FLAG_2_RANDOM_COLOR)));
            sb.append(" rndLength = " + (bo.hasFlag(FIG_PIXEL_OFFSET_01_FLAG, FIG_PIXEL_FLAG_1_RANDOM_SIZE)));
            int[] colors = bo.getValues(FIG_PIXEL_OFFSET_04_COLORSX);
            sb.nl();
            sb.append(" colors ");
            for (int i = 0; i < colors.length; i++) {
               sb.append(" " + toStringColor(colors[i]));
            }
         default:
            sb.append("UNKNOWN FIG = " + figType);
            break;
      }
      if (bo.get1(FIG__OFFSET_02_FLAG) != 0) {
         sb.nl();
         sb.append("Flags_Basic:");
         sb.append(ToStringStaticDraw.debugFigFlag(bo));
      }
      if (bo.get1(FIG__OFFSET_03_FLAGP) != 0) {
         sb.nl();
         sb.append("Flags_Performance:");
         sb.append(ToStringStaticDraw.debugFigPerfFlag(bo));
      }
   }

   public String toStringColor(int color) {
      return boc.getUCtx().getColorU().toStringColor(color);
   }
}
