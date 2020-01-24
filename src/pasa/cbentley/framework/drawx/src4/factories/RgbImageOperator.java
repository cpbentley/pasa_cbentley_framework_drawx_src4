package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.utils.DrawUtilz;
import pasa.cbentley.framework.drawx.src4.utils.RgbUtils;

public class RgbImageOperator extends AbstractDrwOperator {

   public RgbImageOperator(DrwCtx drc) {
      super(drc);
   }

   public RgbImage skewImage(RgbImage img, ByteObject tech, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
      SkewImplementationJava s = new SkewImplementationJava(drc, img, tech);
      return s.setCorners(x0, y0, x1, y1, x2, y2, x3, y3);
   }

   public RgbImage scaleRgbImage(RgbImage rgbImage, int newWidth, int newHeight, ByteObject scaler) {
      return drc.getScaleOperator().scaleRgbImage(rgbImage, newWidth, newHeight, scaler);
   }

   /**
    * Returns the smallest rectangle removing external blocks of color
    * @param img
    * @param color
    * @return
    */
   public RgbImage crop(RgbImage img, int color) {
      int[] rgb = img.getRgbData();
      int w = img.getWidth();
      int h = img.getHeight();
      int[] vals = DrawUtilz.cropTBLRDistances(rgb, w, h, color);
      int minLeftCount = vals[2];
      int minRightCount = vals[3];
      int minTopCount = vals[0];
      int minBotCount = vals[1];
      int newW = w - minLeftCount - minRightCount;
      int newH = h - minTopCount - minBotCount;
      RgbImage nimg = img.getRgbCache().create(newW, newH);
      GraphicsX g = nimg.getGraphicsX();
      g.drawRgbImage(img, minLeftCount, minTopCount, newW, newH, IImage.TRANSFORM_0_NONE, 0, 0);
      return nimg;
   }

   public void setAlpha(RgbImage img, int alpha) {
      DrawUtilz.setAlpha(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), alpha);
   }

   public void setAlphaToColorARGB(RgbImage img, int alpha, int color) {
      DrawUtilz.setAlphaToColorARGB(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), alpha, color);
   }

   /**
    * All pixels whose opaque channels match the opaque channels of parameter color will have their alpha value modified
    * @param img
    * @param alpha
    * @param color
    */
   public void setAlphaToColorRGB(RgbImage img, int alpha, int color) {
      DrawUtilz.setAlphaToColorRGB(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), alpha, color);
   }

   /**
    * Empty array if all rgb value equal color
    * @param rgb
    * @param pad the number of pixels of trim color to keep
    * @param color the useless color to be trimmed
    * @return null if all image is of color and padding is 0
    */
   public RgbImage trim(RgbImage img, int pad, int color) {
      int[] rgb = img.getRgbData();
      int w = img.getWidth();
      int h = img.getHeight();
      //tblr visit
      int topCount = RgbUtils.getDepthTop(rgb, w, h, color);
      int botCount = RgbUtils.getDepthBot(rgb, w, h, color);
      int rightCount = RgbUtils.getDepthRight(rgb, w, h, color);
      int leftCount = RgbUtils.getDepthLeft(rgb, w, h, color);

      botCount -= pad;
      if (botCount < 0)
         botCount = 0;
      topCount -= pad;
      if (topCount < 0)
         topCount = 0;
      rightCount -= pad;
      if (rightCount < 0)
         rightCount = 0;
      leftCount -= pad;
      if (leftCount < 0)
         leftCount = 0;

      //now takes the portion of image
      int newW = w - leftCount - rightCount;
      int newH = h - topCount - botCount;
      if (newH < 0 || newW < 0)
         return null;
      //SystemLog.printDraw("topCount=" + topCount + " botCount=" + botCount + " leftCount=" + leftCount + " rightCount=" + rightCount);
      RgbImage rgbi = img.getRgbCache().createImage(newW, newH);
      int[] ar = rgbi.getRgbData();
      int offset = 0;
      int m = leftCount;
      int n = topCount;
      int destIndex = 0;
      for (int j = 0; j < newH; j++) {
         int start = offset + m + (w * (n + j));
         for (int i = 0; i < newW; i++) {
            ar[destIndex] = rgb[start];
            start++;
            destIndex++;
         }
      }
      return rgbi;
   }

}
