package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.BlendOp;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechMask;

public class MaskOperator extends AbstractDrwOperator {

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
      RgbImage figImg = createMaskedFigure(g, mask, w, h, fig);
      g.drawRgbImage(figImg, x, y);
      figImg.dispose();
   }

   public void drawMask(GraphicsX g, int x, int y, ByteObject mask, char c, IMFont f) {
      RgbImage figImg = drc.getRgbImageFactory().getCharImage(ColorUtils.FULLY_OPAQUE_BLACK, c, f, ColorUtils.FULLY_OPAQUE_WHITE);
      RgbImage maskedImg = createMaskedFigure(g, mask, figImg);
      g.drawRgbImage(maskedImg, x, y);
      figImg.dispose();
   }

   public void drawMask(GraphicsX g, int x, int y, ByteObject mask, char c, IMFont f, int anchor) {
      RgbImage figImg = drc.getRgbImageFactory().getCharImage(ColorUtils.FULLY_OPAQUE_BLACK, c, f, ColorUtils.FULLY_OPAQUE_WHITE);
      RgbImage maskedImg = createMaskedFigure(g, mask, figImg);
      g.drawRgbImage(maskedImg, x, y, anchor);
      figImg.dispose();
   }

   /**
    * Create a mask
    * Example\
    * Input image is white pixel with the word "Hello" written in black
    * Draws that image at coordinate x,y with all pixel of maskColor changed to finalMaskColor
    * Other pixels are changed to fully transparent.
    * @param img
    * @return
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
      RgbImage figImg = createMaskedFigure(g, mask, wi, hi, fig);
      g.drawRgbImage(figImg, x, y);
      figImg.dispose();
   }

   /**
    * Create a black mask. Overrides figure color to black and remove gradients. 
    * <br>
    * Clone of a String fig keeps the Anchor and Scaler elements.
    * <br>
    * This method should not create any blending operation.
    * <br>
    * <br>
    * What happens when you clone a String figure with litteral as sub object?
    * @param mask
    * @param wi
    * @param hi
    * @param fig
    * @return {@link RgbImage} flushed.
    */
   public RgbImage createShapeMask(GraphicsX g, ByteObject mask, int wi, int hi, ByteObject fig) {
      ByteObject cf = fig.cloneCopyHeadRefParams();
      cf.setValue(ITechFigure.FIG__OFFSET_06_COLOR4, ColorUtils.FULLY_OPAQUE_BLACK, 4);
      cf.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_2GRADIENT, false);
      cf.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_6ANIMATED, false);
      cf.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_4MASK, false);
      cf.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_5FILTER, false);
      //draw the mask canvas and apply color filter. bg color is opaque white
      RgbImage maskImg = drc.getFigureOperator().getFigImagePrimitve(g, cf, wi, hi, true, -1);
      if (mask.hasFlag(ITechMask.MASK_OFFSET_1FLAG1, ITechMask.MASK_FLAG_1MASK_FILTER)) {
         ByteObject maskColorFilter = mask.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
         drc.getFilterOperator().applyColorFilter(maskColorFilter, maskImg);
         //image will be switch to RGB mode.
      }
      return maskImg;
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
   public RgbImage createMaskedFigure(GraphicsX g, ByteObject mask, int wi, int hi, ByteObject fig) {
      //SystemLog.printDraw(RgbCache.getCache().toString());

      //draw the mask canvas and apply color filter
      RgbImage maskImg = createShapeMask(g, mask, wi, hi, fig);
      return createMaskedFigure(g, mask, maskImg);
   }

   public RgbImage createMaskedFigure(GraphicsX g, ByteObject mask, RgbImage maskImg) {

      //SystemLog.printDraw(RgbImage.debugAlphas(maskImg.getRgbData(), maskImg.getWidth(), maskImg.getHeight()));

      //the alpha value for the bgpixel not hidden by mask color
      int shapeAlphaValue = mask.getValue(ITechMask.MASK_OFFSET_9ALPHA_SHAPE1, 1);
      int bgAlphaValue = mask.getValue(ITechMask.MASK_OFFSET_8ALPHA_BG1, 1);
      int bgColor = mask.get4(ITechMask.MASK_OFFSET_2COLOR_BG4);
      int shapeColor = mask.get4(ITechMask.MASK_OFFSET_4COLOR_SHAPE4);
      int midColor = mask.get4(ITechMask.MASK_OFFSET_4COLOR_SHAPE4);
      int bgBlend = mask.get1(ITechMask.MASK_OFFSET_5BLEND_BG1);
      int midBlend = mask.get1(ITechMask.MASK_OFFSET_6BLEND_MID1);
      int shapeBlend = mask.get1(ITechMask.MASK_OFFSET_7BLEND_SHAPE1);

      ByteObject bgFigure = mask.getSubFirst(IBOTypesDrw.TYPE_050_FIGURE);
      int dw = maskImg.getWidth();
      int dh = maskImg.getHeight();
      if (mask.hasFlag(ITechMask.MASK_OFFSET_1FLAG1, ITechMask.MASK_FLAG_6SIZE_MASK)) {
         dw = maskImg.getWidth();
         dh = maskImg.getHeight();
      }
      //backgroud image with GraphicsX in RGB_IMAGE mode
      RgbImage figBackgroundImg = drc.getFigureOperator().getFigImage(g, bgFigure, dw, dh, false, false, 0);
      //SystemLog.printDraw(figImg);
      //SystemLog.printDraw(maskImg);
      //we know by construction that m and n = 0 for both images
      int[] figData = figBackgroundImg.getRgbData();
      int[] maskData = maskImg.getRgbData();

      //SystemLog.printDraw(RgbImage.debugAlphas(maskImg.getRgbData(), maskImg.getWidth(), maskImg.getHeight()));
      //SystemLog.printDraw(RgbCache.getCache().toString());
      //mask blending: 3 cases Shape,Bg or Halo.
      //each pixel can be blended
      BlendOp shapeBlendOP = new BlendOp(drc, shapeBlend);
      BlendOp bgBlendOP = new BlendOp(drc, bgBlend);
      BlendOp midBlendOP = new BlendOp(drc, midBlend);

      for (int i = 0; i < figData.length; i++) {
         if (maskData[i] == ColorUtils.FULLY_OPAQUE_BLACK) {
            //we are in the shape area. how to blend bgPixel or shape pixel? either draw pixel or blend with shapecolor
            blendMaskPixel(figData, i, shapeBlendOP, shapeColor, shapeAlphaValue);
         } else if (maskData[i] == ColorUtils.FULLY_OPAQUE_WHITE) {
            //we are in the background area
            blendMaskPixel(figData, i, bgBlendOP, bgColor, bgAlphaValue);
         } else {
            //middle values between black and white with various alpha values
            //take the alpha value and apply it
            int maskAlpha = (maskData[i] >> 24) & 0xFF;
            //SystemLog.printDraw("maskAlpha = " + maskAlpha);
            blendMaskPixel(figData, i, midBlendOP, midColor, maskAlpha);
         }
      }
      maskImg.dispose();
      return figBackgroundImg;
   }

   /**
    * Blends the figData ith pixel with the given color which is given an alphaValue.
    * <br>
    * <br>
    * 
    * @param figData
    * @param i
    * @param blendOp 
    * @param shapeColor
    * @param alphaValue
    */
   void blendMaskPixel(int[] figData, int i, BlendOp blendOp, int shapeColor, int alphaValue) {
      //figData[i] = BlendOp.blendPixel(figData[i], (color & 0xFFFFFF) + (alphaValue << 24), blendOp);
      if (blendOp.getMode() == ITechMask.MASK_BLEND_0) {
         figData[i] = (figData[i] & 0xFFFFFF) + (alphaValue << 24);
      } else if (blendOp.getMode() == ITechMask.MASK_BLEND_1) {
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
      if (!figure.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_4MASK)) {
         figure.addSub(mask);
         figure.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_4MASK, true);
      }
   }

}
