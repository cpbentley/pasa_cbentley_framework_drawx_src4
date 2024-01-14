/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.objects.color.FilterOperator;
import pasa.cbentley.byteobjects.src4.objects.color.IBOFilter;
import pasa.cbentley.byteobjects.src4.objects.color.ITechFilter;
import pasa.cbentley.byteobjects.src4.objects.function.Function;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.core.src4.utils.RgbUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

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

   public void filterTBLR(RgbImage img, ByteObject filter) {
      if (img == null) {
         throw new NullPointerException("RgbImage is null");
      }
      //we have to manage the filter acceptor mask color here.? TODO 
      drc.getFilterOperator().filterTBLR(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), filter);
   }

   /**
    * Applies the function
    * @param img
    * @param fct
    */
   public void filterRGB(RgbImage img, Function fct) {
      FilterOperator filterOperator = drc.getFilterOperator();
      if (img.isRegion()) {
         filterOperator.filterRGB(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), fct);
      } else {
         filterOperator.filterRGB(img.getRgbData(), img.getOffset(), img.getLength(), fct);
      }
   }

   /**
    * {@link ITechFilter#FILTER_TYPE_08_TOUCHES}
    * @param img
    * @param filter
    */
   public void filterTouches(RgbImage img, ByteObject filter) {
      //function for Touch filter is f(x=pixel,y=count)
      FilterOperator filterOperator = drc.getFilterOperator();
      Function fct = getFilterFactory().getFilterFunction(filter);
      int touchColor = filter.getValue(IBOFilter.FILTER_OFFSET_05_COLOR4, 4);
      boolean or48 = filter.hasFlag(IBOFilter.FILTER_OFFSET_03_FLAGP1, IBOFilter.FILTER_FLAGP_1_TOP);
      filterOperator.filterTouches(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), touchColor, or48, fct);
   }

   /**
    * Applies the filter on the RgbImage. Returns immediately if filter is null
    * @param filter
    * @param img It may be in Primitive Mode
    * POST: Output mode of Image is Rgb.
    */
   public void applyColorFilter(ByteObject filter, RgbImage img) {
      if (filter == null)
         return;
      int[] ar = img.getRgbData();
      drc.getFilterOperator().applyColorFilter(filter, ar, img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight());

      //     SystemLog.printDraw(RgbImage.debugAlphas(ar, img.getWidth(), img.getHeight()));
      //     if(ar != img.getRgbData()) {
      //        SystemLog.printDraw("DIFFERENT");
      //     }
      //     SystemLog.printDraw(img);
      //     SystemLog.printDraw(RgbImage.debugAlphas(img.getRgbData(), img.getWidth(), img.getHeight()));
      //        
      //SystemLog.printDraw(img.debugAlpha());

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
      int[] vals = RgbUtils.cropTBLRDistances(rgb, w, h, color);
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
      RgbUtils.setAlpha(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), alpha);
   }

   public void setAlphaToColorARGB(RgbImage img, int alpha, int color) {
      RgbUtils.setAlphaToColorARGB(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), alpha, color);
   }

   /**
    * All pixels whose opaque channels match the opaque channels of parameter color will have their alpha value modified
    * @param img
    * @param alpha
    * @param color
    */
   public void setAlphaToColorRGB(RgbImage img, int alpha, int color) {
      RgbUtils.setAlphaToColorRGB(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), alpha, color);
   }

   public void apply(RgbImage img, int numBits, int size, int[] rgbs, int[] indexes, int offset, int len, int width) {
      int iterateSize = offset + len;
      for (int i = offset; i < iterateSize; i++) {
         int pixelIndex = indexes[i];
         int pixelData = rgbs[pixelIndex];

      }
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
