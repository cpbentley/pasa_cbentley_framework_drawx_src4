/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.BlendOp;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechFilter;
import pasa.cbentley.framework.drawx.src4.tech.ITechMosaic;
import pasa.cbentley.framework.drawx.src4.tech.ITechPass;
import pasa.cbentley.framework.drawx.src4.tech.ITechSkewer;

public class PassDrawOperator extends AbstractDrwOperator {

   public PassDrawOperator(DrwCtx drc) {
      super(drc);
   }

   /**
    * Draws the {@link RgbImage} in a 4 mosaic tile.
    * @param g
    * @param ri
    * @param x
    * @param y
    * @param trans
    * @param root
    */
   public void drawMosaic4(GraphicsX g, RgbImage ri, int x, int y, ByteObject mosaic) {
      int ANCHOR = GraphicsX.ANCHOR;
      int rw = ri.getWidth();
      int rh = ri.getHeight();
      RgbImage topLeft = ri;
      RgbImage topRight = ri;
      RgbImage botLeft = ri;
      RgbImage botRight = ri;
      boolean trans = mosaic.hasFlag(ITechMosaic.PMOSAIC_OFFSET_01_FLAG1, ITechMosaic.PMOSAIC_FLAG_1_TRANSFORMATION);
      if (trans) {
         int root = mosaic.get1(ITechMosaic.PMOSAIC_OFFSET_03_ROOT1);
         int[] transs = new int[4];
         if (root == 0) {
            transs[0] = IImage.TRANSFORM_0_NONE;
            transs[1] = IImage.TRANSFORM_2_FLIP_V_MIRROR;
            transs[2] = IImage.TRANSFORM_1_FLIP_H_MIRROR_ROT180;
            transs[3] = IImage.TRANSFORM_3_ROT_180;
         } else if (root == 1) {
            transs[0] = IImage.TRANSFORM_2_FLIP_V_MIRROR;
            transs[1] = IImage.TRANSFORM_0_NONE;
            transs[2] = IImage.TRANSFORM_3_ROT_180;
            transs[3] = IImage.TRANSFORM_1_FLIP_H_MIRROR_ROT180;
         }
         topLeft = ri.getTransform(transs[0]);
         topRight = ri.getTransform(transs[1]);
         botLeft = ri.getTransform(transs[2]);
         botRight = ri.getTransform(transs[3]);

      }
      int overlayW = mosaic.get2(ITechMosaic.PMOSAIC_OFFSET_04_OVERLAY_W2);
      int overlayH = mosaic.get2(ITechMosaic.PMOSAIC_OFFSET_05_OVERLAY_H2);
      int blend = mosaic.get1(ITechMosaic.PMOSAIC_OFFSET_06_BLEND_OVERLAY1);
      //the blending should only apply to non empty area
      BlendOp bo = g.getBlendOp();
      g.setBlendingModeRGB(blend);
      int dw = rw - overlayW;
      int dh = rh - overlayH;
      g.drawImage(topLeft, x, y, ANCHOR, IImage.TRANSFORM_0_NONE);
      g.drawImage(topRight, x + dw, y, ANCHOR, IImage.TRANSFORM_0_NONE);
      g.drawImage(botLeft, x, y + dh, ANCHOR, IImage.TRANSFORM_0_NONE);
      g.drawImage(botRight, x + dw, y + dh, ANCHOR, IImage.TRANSFORM_0_NONE);
      g.setBlendOp(bo);

   }

   public void drawMosaicGen(GraphicsX g, RgbImage[] ris, int x, int y, ByteObject mosaic) {
      int ANCHOR = GraphicsX.ANCHOR;
      RgbImage ri = ris[0];
      int imgIndex = 0;
      int rw = ri.getWidth();
      int rh = ri.getHeight();
      boolean trans = mosaic.hasFlag(ITechMosaic.PMOSAIC_OFFSET_01_FLAG1, ITechMosaic.PMOSAIC_FLAG_1_TRANSFORMATION);
      int overlayW = mosaic.get2(ITechMosaic.PMOSAIC_OFFSET_04_OVERLAY_W2);
      int overlayH = mosaic.get2(ITechMosaic.PMOSAIC_OFFSET_05_OVERLAY_H2);
      int nw = mosaic.get1(ITechMosaic.PMOSAIC_OFFSET_07_NUM_W1);
      int nh = mosaic.get1(ITechMosaic.PMOSAIC_OFFSET_08_NUM_H1);
      boolean isdiff = mosaic.hasFlag(ITechMosaic.PMOSAIC_OFFSET_01_FLAG1, ITechMosaic.PMOSAIC_FLAG_2_DIFF_SOURCES);
      int blend = mosaic.get1(ITechMosaic.PMOSAIC_OFFSET_06_BLEND_OVERLAY1);
      BlendOp bo = g.getBlendOp();
      g.setBlendingModeRGB(blend);
      int dw = rw - 2 * overlayW;
      int dh = rh - 2 * overlayH;
      int mtrans = IImage.TRANSFORM_0_NONE;
      int dx = x;
      int dy = y;
      applyPreFilter(mosaic, ri);
      for (int i = 0; i < nh; i++) {
         for (int j = 0; j < nw; j++) {
            g.drawImage(ris[imgIndex], dx, dy, ANCHOR, mtrans);
            if (isdiff) {
               imgIndex = (imgIndex + 1) % ris.length;
            }
            if (trans) {
               if (mtrans == IImage.TRANSFORM_0_NONE) {
                  mtrans = IImage.TRANSFORM_2_FLIP_V_MIRROR;
               } else if (mtrans == IImage.TRANSFORM_2_FLIP_V_MIRROR) {
                  mtrans = IImage.TRANSFORM_0_NONE;
               } else if (mtrans == IImage.TRANSFORM_1_FLIP_H_MIRROR_ROT180) {
                  mtrans = IImage.TRANSFORM_3_ROT_180;
               } else if (mtrans == IImage.TRANSFORM_3_ROT_180) {
                  mtrans = IImage.TRANSFORM_1_FLIP_H_MIRROR_ROT180;
               }
            }
            dx += dw;
         }
         if (trans) {
            if (mtrans == IImage.TRANSFORM_0_NONE || mtrans == IImage.TRANSFORM_2_FLIP_V_MIRROR) {
               mtrans = IImage.TRANSFORM_1_FLIP_H_MIRROR_ROT180;
            } else {
               mtrans = IImage.TRANSFORM_0_NONE;
            }
         }
         dx = x;
         dy += dh;
      }

      g.setBlendOp(bo);

   }

   protected void applyPreFilter(ByteObject pass, RgbImage ri) {
      if (pass.hasFlag(ITechPass.PASS_OFFSET_01_FLAG1, ITechPass.PASS_FLAG_1_PRE_FILTER)) {
         ByteObject filter = pass.getSubValueMatch(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_OFFSET_07_ID1, 1, ITechFilter.FILTER_ID_1_PRE);
         drc.getFilterOperator().applyColorFilter(filter, ri);
      }
   }

   public void drawMosaic(GraphicsX g, RgbImage[] ri, int x, int y) {
      drawMosaic(g, ri, x, y, drc.getScalerFactory().getMosaic(ITechMosaic.PMOSAIC_TYPE_1_SQUARE4, true));
   }

   /**
    * 
    * @param g
    * @param ri
    * @param x
    * @param y
    * @param mos
    */
   public void drawMosaic(GraphicsX g, RgbImage[] ris, int x, int y, ByteObject mos) {
      RgbImage ri = ris[0];
      int type = mos.get1(ITechMosaic.PMOSAIC_OFFSET_02_TYPE1);
      if (type == ITechMosaic.PMOSAIC_TYPE_1_SQUARE4) {
         drawMosaic4(g, ri, x, y, mos);
      } else if (type == ITechMosaic.PMOSAIC_TYPE_2_SQUARE9) {
         drawMosaic9(g, ri, x, y, mos);
      } else {
         drawMosaicGen(g, ris, x, type, mos);
      }
   }

   public void drawMosaic9(GraphicsX g, RgbImage ri, int x, int y, ByteObject mos) {
      int ANCHOR = GraphicsX.ANCHOR;
      int rw = ri.getWidth();
      int rh = ri.getHeight();
      RgbImage topLeft = ri.getTransform(IImage.TRANSFORM_3_ROT_180);
      RgbImage topCenter = ri.getTransform(IImage.TRANSFORM_1_FLIP_H_MIRROR_ROT180); //flip h + rot 180
      RgbImage topRight = ri.getTransform(IImage.TRANSFORM_3_ROT_180);

      RgbImage centerleft = ri.getTransform(IImage.TRANSFORM_2_FLIP_V_MIRROR);
      RgbImage center = ri;
      RgbImage centerRight = ri.getTransform(IImage.TRANSFORM_2_FLIP_V_MIRROR);

      RgbImage botLeft = ri.getTransform(IImage.TRANSFORM_3_ROT_180);
      RgbImage botCenter = ri.getTransform(IImage.TRANSFORM_2_FLIP_V_MIRROR);
      RgbImage botRight = ri.getTransform(IImage.TRANSFORM_3_ROT_180);

      g.drawImage(topLeft, x, y, ANCHOR, IImage.TRANSFORM_0_NONE);
      g.drawImage(topCenter, x + rw, y, ANCHOR, IImage.TRANSFORM_0_NONE);
      g.drawImage(topRight, x + 2 * rw, y, ANCHOR, IImage.TRANSFORM_0_NONE);

      g.drawImage(centerleft, x, y + rh, ANCHOR, IImage.TRANSFORM_0_NONE);
      g.drawImage(center, x + rw, y + rh, ANCHOR, IImage.TRANSFORM_0_NONE);
      g.drawImage(centerRight, x + 2 * rw, y + rh, ANCHOR, IImage.TRANSFORM_0_NONE);

      g.drawImage(botLeft, x, y + 2 * rh, ANCHOR, IImage.TRANSFORM_0_NONE);
      g.drawImage(botCenter, x + rw, y + 2 * rh, ANCHOR, IImage.TRANSFORM_3_ROT_180);
      g.drawImage(botRight, x + 2 * rw, y + 2 * rh, ANCHOR, IImage.TRANSFORM_0_NONE);
   }

   public void drawSkewBox(GraphicsX g, RgbImage ri, int x, int y, int w, int h, ByteObject skewer) {
      int ANCHOR = GraphicsX.ANCHOR;
      int rh = ri.getHeight();
      int rw = ri.getWidth();

      if (rh > h || rw > w) {
         g.drawImage(ri, x, y, ANCHOR);
         return;
      }
      applyPreFilter(skewer, ri);

      int op = skewer.get1(ITechSkewer.SKEWER_OFFSET_09_BLENDER1);
      GraphicsX gt = g;
      RgbImage rm = null;
      if (op != 0) {
         rm = ri.getRgbCache().createPrimitiveRgb(w, h, ColorUtils.FULLY_TRANSPARENT_BLACK);
         g = rm.getGraphicsX();
         g.setBlendingModeRGB(op);
      }
      int drawOrder = skewer.get1(ITechSkewer.SKEWER_OFFSET_10_DRAW_ORDER3);

      RgbImage riLeft = ri;
      RgbImage riFloor = ri;
      if (skewer.hasFlag(ITechSkewer.SKEWER_OFFSET_01_FLAG1, ITechSkewer.SKEWER_FLAG_6_NO_TRANSFORMS)) {

      } else {
         riLeft = ri.getTransform(IImage.TRANSFORM_2_FLIP_V_MIRROR);
         riFloor = ri.getTransform(IImage.TRANSFORM_1_FLIP_H_MIRROR_ROT180);
      }

      //when true, there is an issue when w or h is uneven. one pixel is left. so code has to
      boolean isSymmetric = skewer.hasFlag(ITechSkewer.SKEWER_OFFSET_01_FLAG1, ITechSkewer.SKEWER_FLAG_7_SYMMETRIC);
      //we must find bet and H based on image with 
      int extraX = skewer.get2(ITechSkewer.SKEWER_OFFSET_04_EXTRA_X2);
      int extraY = skewer.get2(ITechSkewer.SKEWER_OFFSET_05_EXTRA_Y2);

      int xSkewLeft = (w - rw) / 2;
      int ySkewTop = (h - rh) / 2;
      int xS = w - rw - 2 * xSkewLeft;
      int yS = h - rh - 2 * ySkewTop;
      int xSkewRight = xSkewLeft + xS;
      int ySkewBot = ySkewTop + yS;

      //decide on the order to draw the 

      /////////////LEFT
      //topleft
      int x0 = 0;
      int y0 = -ySkewTop - extraY;
      //topright
      int x1 = xSkewLeft;
      int y1 = 0;
      //bot left
      int x3 = 0;
      int y3 = rh + ySkewBot + extraY;
      //bot right
      int x2 = xSkewLeft;
      int y2 = rh;
      //left
      RgbImage skewedLeft = riLeft.skew(x0, y0, x1, y1, x2, y2, x3, y3, skewer);
      if (!skewer.hasFlag(ITechSkewer.SKEWER_OFFSET_01_FLAG1, ITechSkewer.SKEWER_FLAG_4_HIDE_LEFT)) {
         //g.toLog().printBusiness("#drawSkewBox " + skewedLeft.toString());
         //skewedLeft.setFlag(RgbImage.FLAG_05_IGNORE_ALPHA, true);
         g.drawImage(skewedLeft, x, y - extraY, ANCHOR, IImage.TRANSFORM_0_NONE);
         //right
      }

      ///////////////// RIGHT
      //topleft
      x0 = 0;
      y0 = 0;
      //topright
      x1 = xSkewRight;
      y1 = -ySkewTop - extraY;
      //bot left
      x3 = 0;
      y3 = rh;
      //bot right
      x2 = xSkewRight;
      y2 = rh + ySkewBot + extraY;
      if (!skewer.hasFlag(ITechSkewer.SKEWER_OFFSET_01_FLAG1, ITechSkewer.SKEWER_FLAG_5_HIDE_RIGHT)) {
         RgbImage skewedRight = riLeft.skew(x0, y0, x1, y1, x2, y2, x3, y3, skewer);
         if (isSymmetric) {
            RgbImage riSkewedTopForRight = skewedLeft.getTransform(IImage.TRANSFORM_2_FLIP_V_MIRROR);
            g.drawImage(riSkewedTopForRight, x + rw + xSkewLeft, y - extraY, ANCHOR, IImage.TRANSFORM_0_NONE);
         } else {
            g.drawImage(skewedRight, x + rw + xSkewLeft, y - extraY, ANCHOR, IImage.TRANSFORM_0_NONE);
         }
      }

      /////TOP
      //top left
      x0 = -xSkewLeft - extraX;
      y0 = 0;
      //top right
      x1 = rw + xSkewRight + extraX;
      y1 = 0;
      //lower right
      x2 = rw;
      y2 = ySkewTop;
      //lower left
      x3 = 0;
      y3 = ySkewTop;

      RgbImage riSkewedTopp = riFloor.skew(x0, y0, x1, y1, x2, y2, x3, y3, skewer);
      if (!skewer.hasFlag(ITechSkewer.SKEWER_OFFSET_01_FLAG1, ITechSkewer.SKEWER_FLAG_2_HIDE_TOP)) {
         //top
         //RgbImage riSkewedFloor = riSkewedTop;
         g.drawImage(riSkewedTopp, x - extraX, y, ANCHOR, IImage.TRANSFORM_0_NONE);
      }

      ////////////BOT
      x0 = 0;
      y0 = 0;
      //uppwer right
      x1 = rw;
      y1 = 0;
      //lower right
      x2 = rw + xSkewRight + extraX;
      y2 = ySkewBot;

      //lower left
      x3 = -xSkewLeft - extraX;
      y3 = ySkewBot;
      //bottom
      if (!skewer.hasFlag(ITechSkewer.SKEWER_OFFSET_01_FLAG1, ITechSkewer.SKEWER_FLAG_3_HIDE_BOT)) {
         if (isSymmetric) {
            RgbImage riSkewedTopForBot = riSkewedTopp.getTransform(IImage.TRANSFORM_3_ROT_180);
            g.drawImage(riSkewedTopForBot, x - extraX, y + rh + ySkewTop, ANCHOR);
         } else {
            RgbImage riSkewedFloor = riFloor.skew(x0, y0, x1, y1, x2, y2, x3, y3, skewer);
            g.drawImage(riSkewedFloor, x - extraX, y + rh + ySkewTop, ANCHOR);
         }
      }
      if (!skewer.hasFlag(ITechSkewer.SKEWER_OFFSET_01_FLAG1, ITechSkewer.SKEWER_FLAG_1_HIDE_CENTER)) {
         //draw final image
         int centerTrans = skewer.get1(ITechSkewer.SKEWER_OFFSET_07_CENTER_TRANS1);
         g.drawImage(ri, x + xSkewLeft, y + ySkewTop, ANCHOR, centerTrans);

      }
   }

}
