/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.BOCtx;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.byteobjects.src4.objects.color.BlendOp;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.core.src4.utils.interfaces.IColors;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechGraphicsX;
import pasa.cbentley.framework.drawx.src4.tech.ITechMask;
import pasa.cbentley.framework.drawx.src4.utils.AnchorUtils;

/**
 * 
 * @author Charles Bentley
 *
 */
public class MaskOperator extends AbstractDrwOperator implements ITechMask, IColors {

   public MaskOperator(DrwCtx drc) {
      super(drc);
   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param mask
    * @param fig 
    * @param w Ignored when figure has size anchor
    * @param h Ignored when figure has size anchor
    */
   public void drawMask(GraphicsX g, int x, int y, ByteObject mask, ByteObject fig, int w, int h) {
      RgbImage figImg = createMaskedFigure(mask, w, h, fig);
      g.drawRgbImage(figImg, x, y);
      figImg.dispose();
   }

   public void drawMask(GraphicsX g, int x, int y, ByteObject mask, char c, IMFont f) {
      int bgColor = FULLY_OPAQUE_WHITE;
      RgbImage figImg = drc.getRgbImageFactory().getCharImage(FULLY_OPAQUE_BLACK, c, f, bgColor);
      RgbImage maskedImg = createMaskedFigure(mask, figImg);
      g.drawRgbImage(maskedImg, x, y);
      figImg.dispose();
   }

   public void drawMask(GraphicsX g, int x, int y, ByteObject mask, char[] chars, int offset, int len, IMFont f, int w, int h) {
      int bgColor = FULLY_OPAQUE_WHITE;
      RgbImage figImg = drc.getRgbImageFactory().getCharsImage(FULLY_OPAQUE_BLACK, chars, offset,len , f, bgColor, w, h);
      RgbImage maskedImg = createMaskedFigure(mask, figImg);
      g.drawRgbImage(maskedImg, x, y);
      figImg.dispose();
   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param mask
    * @param c
    * @param f
    * @param anchor {@link ITechGraphicsX#ANCHOR}
    */
   public void drawMask(GraphicsX g, int x, int y, ByteObject mask, char c, IMFont f, int anchor) {
      RgbImage figImg = drc.getRgbImageFactory().getCharImage(FULLY_OPAQUE_BLACK, c, f, FULLY_OPAQUE_WHITE);
      RgbImage maskedImg = createMaskedFigure(mask, figImg);
      g.drawRgbImage(maskedImg, x, y, anchor);
      figImg.dispose();
   }

   /**
    * Merges two mask definitions.
    * <br>
    * <br>
    * @param root
    * @param merge
    * @return
    */
   public ByteObject mergeMask(ByteObject root, ByteObject merge) {
      int maskBgColor = root.get4(MASK_OFFSET_2_COLOR_BG4);
      int maskMidColor = root.get4(MASK_OFFSET_3_COLOR_MID4);
      int maskShapeColor = root.get4(MASK_OFFSET_4_COLOR_SHAPE4);
      int blendBg = root.get1(MASK_OFFSET_5_BLEND_BG1);
      int blendMid = root.get1(MASK_OFFSET_6_BLEND_MID1);
      int blendShape = root.get1(MASK_OFFSET_7_BLEND_SHAPE1);
      int bgAlpha = root.get1(MASK_OFFSET_8_ALPHA_BG1);
      int shapeAlpha = root.get1(MASK_OFFSET_9_ALPHA_SHAPE1);

      ByteObject mm = merge.getSubFirst(IBOTypesBOC.TYPE_011_MERGE_MASK);

      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_2)) {
         maskBgColor = merge.get4(MASK_OFFSET_2_COLOR_BG4);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_3)) {
         maskMidColor = merge.get4(MASK_OFFSET_3_COLOR_MID4);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_4)) {
         maskShapeColor = merge.get4(MASK_OFFSET_4_COLOR_SHAPE4);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_5)) {
         blendBg = merge.get4(MASK_OFFSET_5_BLEND_BG1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_6)) {
         blendMid = merge.get4(MASK_OFFSET_6_BLEND_MID1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_7)) {
         blendShape = merge.get4(MASK_OFFSET_7_BLEND_SHAPE1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_8)) {
         bgAlpha = merge.get4(MASK_OFFSET_8_ALPHA_BG1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_1)) {
         shapeAlpha = merge.get4(MASK_OFFSET_9_ALPHA_SHAPE1);
      }
      ByteObject maskFilter = root.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
      if (merge.hasFlag(MASK_OFFSET_1_FLAG1, MASK_FLAG_1_MASK_FILTER)) {
         maskFilter = merge.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
      }
      ByteObject bgFigure = root.getSubFirst(IBOTypesDrw.TYPE_050_FIGURE);
      if (merge.hasFlag(MASK_OFFSET_1_FLAG1, MASK_FLAG_2_BG_FIGURE)) {
         bgFigure = merge.getSubFirst(IBOTypesDrw.TYPE_050_FIGURE);
      }
      ByteObject newMask = drc.getMaskFactory().getMask(maskBgColor, maskMidColor, maskShapeColor, bgAlpha, shapeAlpha, blendBg, blendMid, blendShape, maskFilter, bgFigure);
      return newMask;
   }

   /**
    * Create a mask
    * Example\
    * Input image is white pixel with the word "Hello" written in black
    * Draws that image at coordinate x,y with all pixel of maskColor changed to finalMaskColor
    * Other pixels are changed to fully transparent.
    * @param img
    * @return
    * @deprecated
    */
   public IImage getImageMask(IImage srcImg, int maskColor, int finalMaskColor) {
      int[] rgb = drc.getRgbCache().getImageData(srcImg);
      for (int i = 0; i < rgb.length; i++) {
         if (rgb[i] == maskColor) {
            rgb[i] = finalMaskColor;
         } else {
            //sets it to fully transparent
            rgb[i] = rgb[i] & 0x00FFFFFF;
         }
      }
      return drc.getImageFactory().createRGBImage(rgb, srcImg.getWidth(), srcImg.getHeight(), true);
   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param mask
    * @param str
    * @param f
    */
   public void drawMask(GraphicsX g, int x, int y, ByteObject mask, String str, IMFont f) {
      ByteObject fig = drc.getFigureFactory().getFigString(str, f);
      int wi = f.stringWidth(str);
      int hi = f.getHeight();
      RgbImage figImg = createMaskedFigure(mask, wi, hi, fig);
      g.drawRgbImage(figImg, x, y);
      figImg.dispose();
   }

   public RgbImage createShapeMask(ByteObject mask, int wi, int hi, ByteObject fig, int percent) {
      RgbImage img = createShapeMask(mask, wi, hi, fig, percent, drc.getAnchorFactory().getCenterCenter());
      return img;
   }

   /**
    * Draws the image as a percentagen over the given area
    * @param mask
    * @param wi
    * @param hi
    * @param fig
    * @param percent
    * @return
    */
   public RgbImage createShapeMask(ByteObject mask, int wi, int hi, ByteObject fig, int percent, ByteObject anchor) {
      RgbImage figImg = drc.getCache().createPrimitiveRgb(wi, hi, -1);
      GraphicsX figGraphics = figImg.getGraphicsX(GraphicsX.MODE_1_IMAGE);
      fig = getClonedFigureForMask(fig);
      if (percent == 100) {
         drc.getFigureOperator().paintFigureSwitch(figGraphics, 0, 0, wi, hi, fig);
      } else {
         int wFig = (int) ((float) wi / 100f * (float) percent);
         int hFig = (int) ((float) hi / 100f * (float) percent);
         int x = AnchorUtils.getXAlign(anchor, 0, wi, wFig);
         int y = AnchorUtils.getYAlign(anchor, 0, hi, hFig);
         drc.getFigureOperator().paintFigureSwitch(figGraphics, x, y, wFig, hFig, fig);
      }
      figImg.disposeGraphics();

      if (mask.hasFlag(MASK_OFFSET_1_FLAG1, MASK_FLAG_1_MASK_FILTER)) {
         ByteObject maskColorFilter = mask.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
         drc.getRgbImageOperator().applyColorFilter(maskColorFilter, figImg);
         //image will be switch to RGB mode.
      }
      return figImg;
   }

   public ByteObject getClonedFigureForMask(ByteObject fig) {
      ByteObject figureCloned = fig.cloneCopyHeadRefParams();
      figureCloned.setValue(ITechFigure.FIG__OFFSET_06_COLOR4, FULLY_OPAQUE_BLACK, 4);
      figureCloned.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_2_GRADIENT, false);
      figureCloned.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_6_ANIMATED, false);
      figureCloned.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_4_MASK, false);
      figureCloned.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_5_FILTER, false);

      return figureCloned;
   }

   /**
    * Create a black mask on a white background from the figure. 
    * 
    * Overrides figure color to black and remove gradients. 
    * <br>
    * If the Mask has a filter, it is applied on the Black/White image.
    * <br>
    * What happens when you clone a String figure with litteral as sub object?
    * @param mask
    * @param wi
    * @param hi
    * @param fig
    * @return {@link RgbImage} flushed.
    */
   public RgbImage createShapeMask(ByteObject mask, int wi, int hi, ByteObject fig) {
      return createShapeMask(mask, wi, hi, fig, 100, null);
   }

   /**
    * Get an image mask for the shape drawn by the figure. 
    * <br>
    * 
    * <li>get shape in black and white
    * <li>draw background figure
    * <li>blend pixels from bg figure with mask colors.
    * <br>
    * <br>
    * 
    * Draw the figure in white on a black background or in black on a white background
    * <br>
    * For this purpose the {@link ByteObject} figure is cloned and color set to white/black.
    * <br>
    * @param mask mask definition with background image and filter to apply on figure
    * @param wi
    * @param hi
    * @param fig Figure that will paint its shape in a given color.
    * @return
    */
   public RgbImage createMaskedFigure(ByteObject mask, int wi, int hi, ByteObject fig) {
      //draw the mask canvas and apply color filter
      RgbImage maskImg = createShapeMask(mask, wi, hi, fig);
      return createMaskedFigure(mask, maskImg);
   }

   /**
    * 
    * Size of returned image is same as {@link RgbImage}, unless
    * the mask defines a sizer {@link ITechMask#MASK_FLAG_6_SIZE_MASK}
    * 
    * <br>
    * <br>
    * Mask image: 
    * <li>fully opaque black pixels are SHAPE
    * <li>fully opaque white pixels are background
    * <li>anything else is considered halo
    * 
    * @param mask Describes what to do with RgbImage
    * @param maskImg provides the template for which to accept the pixel
    * @return a new {@link RgbImage}
    */
   public RgbImage createMaskedFigure(ByteObject mask, RgbImage maskImg) {

      //the alpha value for the bgpixel not hidden by mask color
      int alphaValueShape = mask.getValue(MASK_OFFSET_9_ALPHA_SHAPE1, 1);
      int alphaValueBg = mask.getValue(MASK_OFFSET_8_ALPHA_BG1, 1);
      int colorBg = mask.get4(MASK_OFFSET_2_COLOR_BG4);
      int colorMid = mask.get4(MASK_OFFSET_3_COLOR_MID4);
      int colorShape = mask.get4(MASK_OFFSET_4_COLOR_SHAPE4);
      int blendBg = mask.get1(MASK_OFFSET_5_BLEND_BG1);
      int blendMid = mask.get1(MASK_OFFSET_6_BLEND_MID1);
      int blendShape = mask.get1(MASK_OFFSET_7_BLEND_SHAPE1);

      int dw = maskImg.getWidth();
      int dh = maskImg.getHeight();
      if (mask.hasFlag(MASK_OFFSET_1_FLAG1, MASK_FLAG_6_SIZE_MASK)) {
         dw = maskImg.getWidth();
         dh = maskImg.getHeight();
      }

      int[] figData = null;
      RgbImage figBackgroundImg;
      if (mask.hasFlag(MASK_OFFSET_1_FLAG1, MASK_FLAG_2_BG_FIGURE)) {
         ByteObject bgFigure = mask.getSubFirst(IBOTypesDrw.TYPE_050_FIGURE);
         //#debug      
         drc.toStringCheckNull(bgFigure);

         //background image with GraphicsX in RGB_IMAGE mode
         boolean justSwitch = false;
         boolean whiteopaque = false;
         figBackgroundImg = drc.getFigureOperator().getFigImage(bgFigure, dw, dh, justSwitch, whiteopaque, 0);
      } else {
         figBackgroundImg = drc.getCache().create(dw, dh, colorBg);
      }
      figData = figBackgroundImg.getRgbData();

      //mask pixels.. used to contruct the final image
      //we know by construction that m and n = 0 for both images
      int[] maskData = maskImg.getRgbData();

      //mask blending: 3 cases Shape,Bg or Halo.
      //each pixel can be blended
      BOCtx boc = drc.getBOC();
      BlendOp blendOPShape = new BlendOp(boc, blendShape);
      BlendOp blendOPBg = new BlendOp(boc, blendBg);
      BlendOp blendOPMid = new BlendOp(boc, blendMid);

      for (int i = 0; i < figData.length; i++) {
         if (maskData[i] == FULLY_OPAQUE_BLACK) {
            //we are in the shape area. how to blend bgPixel or shape pixel? either draw pixel or blend with shapecolor
            blendMaskPixel(figData, i, blendOPShape, colorShape, alphaValueShape);
         } else if (maskData[i] == FULLY_OPAQUE_WHITE) {
            //we are in the background area
            blendMaskPixel(figData, i, blendOPBg, colorBg, alphaValueBg);
         } else {
            //middle values between black and white with various alpha values
            //take the alpha value and apply it
            int maskAlpha = (maskData[i] >> 24) & 0xFF;
            //SystemLog.printDraw("maskAlpha = " + maskAlpha);
            blendMaskPixel(figData, i, blendOPMid, colorMid, maskAlpha);
         }
      }
      return figBackgroundImg;
   }

   /**
    * Blends the figData ith pixel with the given color which is given an alphaValue.
    * <br>
    * <br>
    * Shape color
    * <li> {@link ITechMask#MASK_OFFSET_2_COLOR_BG4}
    * <li> {@link ITechMask#MASK_OFFSET_3_COLOR_MID4}
    * <li> {@link ITechMask#MASK_OFFSET_4_COLOR_SHAPE4}
    * 
    * @param figData
    * @param i
    * @param blendOp 
    * @param shapeColor the color of the shape as defined by {@link ITechMask#MASK_OFFSET_2_COLOR_BG4}
    * @param alphaValue
    */
   void blendMaskPixel(int[] figData, int i, BlendOp blendOp, int shapeColor, int alphaValue) {
      //figData[i] = BlendOp.blendPixel(figData[i], (color & 0xFFFFFF) + (alphaValue << 24), blendOp);
      if (blendOp.getMode() == MASK_BLEND_0) {
         figData[i] = (figData[i] & 0xFFFFFF) + (alphaValue << 24);
      } else if (blendOp.getMode() == MASK_BLEND_1) {
         figData[i] = (shapeColor & 0xFFFFFF) + (alphaValue << 24);
      } else {
         figData[i] = blendOp.blendPixel(figData[i], (shapeColor & 0xFFFFFF) + (alphaValue << 24));
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
      if (!figure.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_4_MASK)) {
         figure.addSub(mask);
         figure.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_4_MASK, true);
      }
   }

}
