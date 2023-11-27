/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.functions.Function;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.BlendOp;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechFilter;
import pasa.cbentley.framework.drawx.src4.utils.DrawUtilz;
import pasa.cbentley.framework.drawx.src4.utils.TransformUtils;

public class FilterOperator extends AbstractDrwOperator implements ITechFilter {

   public FilterOperator(DrwCtx drc) {
      super(drc);
   }

   public void applyColorFilter(ByteObject filter, int[] rgb, int offset, int w, int h) {
      applyColorFilter(filter, rgb, 0, w, 0, 0, w, h);
   }

   public void applyColorFilter(ByteObject filter, int[] rgb, int offset, int scanlength, int m, int n, int w, int h) {
      if (filter == null)
         return;
      filter.checkType(IBOTypesDrw.TYPE_056_COLOR_FILTER);
      final int type = filter.getValue(FILTER_OFFSET_01_TYPE1, 1);
      switch (type) {
         case FILTER_TYPE_00_FUNCTION_ALL:
            Function fct = getFilterFactory().getFilterFunction(filter);
            filterRGB(rgb, offset, scanlength, m, n, w, h, fct);
            break;
         case FILTER_TYPE_01_GRAYSCALE:
            filterGrayScale(rgb, offset, scanlength, m, n, w, h);
            break;
         case FILTER_TYPE_02_BILINEAR:
            filterBiLinear(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         case FILTER_TYPE_03_ALPHA_TO_COLOR:
            filterAlphaToColor(filter, rgb, offset, scanlength, m, n, w, h);
            break;
         case FILTER_TYPE_04_SIMPLE_ALPHA:
            filterAlpha(filter, rgb, offset, scanlength, m, n, w, h);
            break;
         case FILTER_TYPE_05_REPEAT_PIXEL:
            filterRepeatPixels(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         case FILTER_TYPE_06_STEP_SMOOTH:
            filterSmootherBorder(rgb, offset, scanlength, m, n, w, h);
            break;
         case FILTER_TYPE_07_TBLR:
            filterTBLR(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         case FILTER_TYPE_08_TOUCHES:
            filterTouches(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         case FILTER_TYPE_09_STICK:
            filterStick(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         case FILTER_TYPE_10_SEPIA:
            filterSepia(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         case FILTER_TYPE_11_HORIZ_AVERAGE:
            filterHorizAverage(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         case FILTER_TYPE_12_HORIZ_AVERAGE_NEOM:
            filterHorizAverageNeom(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         case FILTER_TYPE_13_CHANNEL_MOD:
            filterChannelMOD(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         case FILTER_TYPE_14_BLEND_SELF:
            filterSelfBlend(rgb, offset, scanlength, m, n, w, h, filter);
            break;
         default:
            throw new IllegalArgumentException("Unknown Color Filter Type " + type);
      }
      //apply subfilters, linked together
      ByteObject subfilter = null;
      int i = 0;
      while ((subfilter = filter.getSubOrder(IBOTypesDrw.TYPE_056_COLOR_FILTER, i)) != null) {
         applyColorFilter(subfilter, rgb, offset, scanlength, m, n, w, h);
         i++;
      }
      //SystemLog.printDraw(RgbImage.debugAlphas(rgb, w, h));
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
      applyColorFilter(filter, ar, img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight());

      //	 SystemLog.printDraw(RgbImage.debugAlphas(ar, img.getWidth(), img.getHeight()));
      //	 if(ar != img.getRgbData()) {
      //	    SystemLog.printDraw("DIFFERENT");
      //	 }
      //	 SystemLog.printDraw(img);
      //	 SystemLog.printDraw(RgbImage.debugAlphas(img.getRgbData(), img.getWidth(), img.getHeight()));
      //		
      //SystemLog.printDraw(img.debugAlpha());

   }

   protected void filterAlpha(ByteObject filter, int[] rgb, int offset, int scanlength, int m, int n, int w, int h) {
      int val = filter.getValue(FILTER_OFFSET_04_FUNCTION2, 2);
      DrawUtilz.setAlpha(rgb, offset, scanlength, m, n, w, h, val);
   }

   protected void filterAlphaToColor(ByteObject filter, int[] rgb, int offset, int scanlength, int m, int n, int w, int h) {
      int alphaValue = filter.getValue(FILTER_OFFSET_04_FUNCTION2, 2);
      int color = filter.getValue(FILTER_OFFSET_05_COLOR4, 4);
      if (filter.hasFlag(FILTER_OFFSET_02_FLAG1, FILTER_FLAG_7_EXACT_MATCH)) {
         DrawUtilz.setAlphaToColorARGB(rgb, offset, scanlength, m, n, w, h, alphaValue, color);
      } else {
         DrawUtilz.setAlphaToColorRGB(rgb, offset, scanlength, m, n, w, h, alphaValue, color);
      }
   }

   public int filterAverage(int pixelA, int pixelB) {

      int aA = (pixelA >> 24) & 0xFF;
      int aB = (pixelB >> 24) & 0xFF;

      int redA = (pixelA >> 16) & 0xFF;
      int redB = (pixelB >> 16) & 0xFF;

      int greenA = pixelA >> 8 & 0xFF;
      int greenB = pixelB >> 8 & 0xFF;

      int blueA = pixelA & 0xFF;
      int blueB = pixelB & 0xFF;

      // Calculate new pixels colour and mask.
      int a = (aA + aB) / 2;
      int red = (redA + redB) / 2;
      int green = (greenA + greenB) / 2;
      int blue = (blueA + blueB) / 2;
      // Store pixel in output buffer and increment offset.
      return (a << 24) + (red << 16) + (green << 8) + blue;
   }

   public int filterBiLinear(int pixel, int pixelLeft, int pixelBot, int pixelBL, int xo, int yo) {
      int pixelA = pixel;
      int pixelB = pixelLeft;
      int pixelC = pixelBot;
      int pixelD = pixelBL;

      int weightD = xo * yo;
      int weightC = (yo << 8) - weightD;
      int weightB = (xo << 8) - weightD;
      int weightA = 0x10000 - weightB - weightC - weightD;

      int redA = (pixelA >> 16) & 0xFF;
      int redB = (pixelB >> 16) & 0xFF;
      int redC = (pixelC >> 16) & 0xFF;
      int redD = (pixelD >> 16) & 0xFF;
      int aA = (pixelA >> 24) & 0xFF;
      int aB = (pixelB >> 24) & 0xFF;
      int aC = (pixelC >> 24) & 0xFF;
      int aD = (pixelD >> 24) & 0xFF;

      int greenA = pixelA & 0x00FF00;
      int greenB = pixelB & 0x00FF00;
      int greenC = pixelC & 0x00FF00;
      int greenD = pixelD & 0x00FF00;

      int blueA = pixelA & 0x0000FF;
      int blueB = pixelB & 0x0000FF;
      int blueC = pixelC & 0x0000FF;
      int blueD = pixelD & 0x0000FF;

      // Calculate new pixels colour and mask.
      int a = 0x00FF0000 & (aA * weightA + aB * weightB + aC * weightC + aD * weightD);
      int red = 0x00FF0000 & (redA * weightA + redB * weightB + redC * weightC + redD * weightD);
      int green = 0xFF000000 & (greenA * weightA + greenB * weightB + greenC * weightC + greenD * weightD);
      int blue = 0x00FF0000 & (blueA * weightA + blueB * weightB + blueC * weightC + blueD * weightD);
      // Store pixel in output buffer and increment offset.
      return (a << 8) + red + (((green | blue) >> 16));
   }

   public void filterBiLinear(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      Function f = getFilterFactory().getFilterFunction(filter);
      int wM1 = w - 1;
      int hM1 = h - 1;
      int oldv = rgb[0];
      int newv = 0;
      int index = 0;
      //start with top left pixel

      // top right pixel

      // bot left pixel

      //bot right pixel

      //top line

      //bot line

      //left line

      //right line

      //body
      for (int j = 1; j < hM1; j++) {
         //first pixel of line
         index = offset + 1 + m + (scanlength * (n + j));
         oldv = rgb[index - 1];
         for (int i = 1; i < wM1; i++) {
            newv = rgb[index];
            if (oldv != newv) {
               //
               int pixel = rgb[index];
               int pixelLeft = rgb[index + 1];
               int pixelBot = rgb[index + scanlength];
               int pixelBL = rgb[index + scanlength + 1];
               int newWidth = wM1;
               int newHeight = hM1;
               int xe = i;
               int ye = j;
               int xo = (xe << 8) / newWidth;
               int yo = (ye << 8) / newHeight;
               rgb[index] = filterBiLinear(pixel, pixelLeft, pixelBot, pixelBL, xo, yo);
            }
            oldv = newv;
            index++;
         }
      }
   }

   public void filterBiLinear2(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      int wM1 = w / 2;
      int hM1 = h / 2;
      int oldv = rgb[0];
      int newv = 0;
      int index = 0;
      for (int j = 0; j < w; j++) {
         //first pixel of line
         index = offset + 1 + m + (scanlength * (n + j));
         oldv = rgb[index - 1];
         for (int i = 0; i < n; i++) {
            newv = rgb[index];
            if (oldv != newv) {
               //
               int pixel = rgb[index];
               int pixelLeft = rgb[index + 1];
               int pixelBot = rgb[index + scanlength];
               int pixelBL = rgb[index + scanlength + 1];
               int newWidth = wM1;
               int newHeight = hM1;
               int xe = i;
               int ye = j;
               int xo = (xe << 8) / newWidth;
               int yo = (ye << 8) / newHeight;
               rgb[index] = filterBiLinear(pixel, pixelLeft, pixelBot, pixelBL, xo, yo);
            }
            oldv = newv;
            index++;
         }
      }
   }

   /**
    * Modifies a channel
    * @param rgb
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param filter
    */
   public void filterChannelMOD(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      boolean pA = filter.hasFlag(FILTER_OFFSET_02_FLAG1, 1);
      boolean pRed = filter.hasFlag(FILTER_OFFSET_02_FLAG1, 2);
      boolean pGreen = filter.hasFlag(FILTER_OFFSET_02_FLAG1, 4);
      boolean pBlue = filter.hasFlag(FILTER_OFFSET_02_FLAG1, 8);
      int fct = filter.get2(FILTER_OFFSET_04_FUNCTION2);
      int fctV = filter.get1(FILTER_OFFSET_05_COLOR4);
      boolean isCapped = filter.hasFlag(FILTER_OFFSET_02_FLAG1, 16);
      for (int i = 0; i < h; i++) {
         int index = offset + m + (scanlength * (n + i));
         for (int j = 0; j < w; j++) {
            int val = rgb[index];
            int count = 0;
            int a = (val >> 24) & 0xFF;
            int red = (val >> 16) & 0xFF;
            int green = val >> 8 & 0xFF;
            int blue = val & 0xFF;
            if (pA) {
               if (fctV == 0) {
                  a = fct;
               } else if (fctV == 1) {
                  a = a + fct;
                  if (isCapped) {
                     if (a > 255) {
                        a = 255;
                     }
                  }
               } else if (fctV == 2) {
                  a = a - fct;
                  if (isCapped) {
                     if (a < 0) {
                        a = 0;
                     }
                  }
               }
            }

            val = (a << 24) + (red << 16) + (green << 8) + blue;

            rgb[index] = val;
            index++;
         }

      }

   }

   /**
    * Modifies each pixel based on a function each channel
    * The filter has a root color. if a pixel has its channel value, it recieves
    * an alpha value
    * @param rgb
    * @param w
    * @param h
    * @param filter
    */
   public void filteRGB(int[] rgb, Function fct) {
      filterRGB(rgb, 0, rgb.length, fct);
   }

   /**
    * Applies Function to all pixels except pixels of value mask.
    * For thos pixels, their alpha value is set to 0 (fully transparent)
    * @param rgb
    * @param offset
    * @param len
    * @param fct
    * @param mask
    */
   public void filteRGBRemoveMask(int[] rgb, int offset, int len, Function fct, int mask) {
      int end = offset + len;
      for (int i = offset; i < end; i++) {
         int pix = rgb[i];
         if (pix == mask) {
            rgb[i] = pix & 0xFFFFFF;
         } else {
            rgb[i] = fct.fx(pix);
         }
      }
   }

   /**
    * Applies Function to all pixels except pixels of value mask.
    * For thos pixels, their alpha value is set to 0 (fully transparent)
   */
   public void alphaFilteRGBRemoveMask(int[] rgb, Function fct, int mask) {
      filteRGBRemoveMask(rgb, 0, rgb.length, fct, mask);
   }

   public void filterGrayScale(int[] rgb, int offset, int scanlength, int m, int n, int w, int h) {
      for (int i = 0; i < w; i++) {
         int index = offset + m + (scanlength * n) + i;
         for (int j = 0; j < h; j++) {
            rgb[index] = ColorUtils.pixelToGrayScale(rgb[index]);
            index += scanlength;
         }
      }
   }

   public void filterHorizAverage(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      int[] workValues = new int[w];
      int amplitude = filter.get2(FILTER_OFFSET_04_FUNCTION2);
      //sanity check on amplitude value
      if (amplitude <= 0 || amplitude > w) {
         amplitude = 10;
      }
      for (int i = 0; i < h; i++) {
         int index = offset + m + (scanlength * (n + i));
         for (int j = 0; j < w; j++) {
            workValues[j] = rgb[index];
            index++;
         }
         index = offset + m + (scanlength * (n + i));
         for (int j = 0; j < w; j++) {
            int val = rgb[index];
            int count = 1;
            int aA = (val >> 24) & 0xFF;
            int redA = (val >> 16) & 0xFF;
            int greenA = val >> 8 & 0xFF;
            int blueA = val & 0xFF;

            for (int k = -amplitude; k <= amplitude; k++) {
               int hg = j + k;
               if (hg >= 0 && hg < w) {
                  int val1 = workValues[hg];
                  int aB = (val1 >> 24) & 0xFF;
                  int redB = (val1 >> 16) & 0xFF;
                  int greenB = val1 >> 8 & 0xFF;
                  int blueB = val1 & 0xFF;
                  aA += aB;
                  redA += redB;
                  greenA += greenB;
                  blueA += blueB;
                  count++;
               }

            }
            int a = (aA) / count;
            int red = (redA) / count;
            int green = (greenA) / count;
            int blue = (blueA) / count;
            // Store pixel in output buffer and increment offset.
            val = (a << 24) + (red << 16) + (green << 8) + blue;

            rgb[index] = val;
            index++;
         }
      }
   }

   /**
    * Just by setting count to 0 instead of 1.
    * @param rgb
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param filter
    */
   public void filterHorizAverageNeom(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      int[] workValues = new int[w];
      for (int i = 0; i < h; i++) {
         int index = offset + m + (scanlength * (n + i));
         for (int j = 0; j < w; j++) {
            workValues[j] = rgb[index];
            index++;
         }
         index = offset + m + (scanlength * (n + i));
         int amplitude = 3;
         for (int j = 0; j < w; j++) {
            int val = rgb[index];
            int count = 0;
            int aA = (val >> 24) & 0xFF;
            int redA = (val >> 16) & 0xFF;
            int greenA = val >> 8 & 0xFF;
            int blueA = val & 0xFF;

            for (int k = -amplitude; k <= amplitude; k++) {
               int hg = j + k;
               if (hg >= 0 && hg < w) {
                  int val1 = workValues[hg];
                  int aB = (val1 >> 24) & 0xFF;
                  int redB = (val1 >> 16) & 0xFF;
                  int greenB = val1 >> 8 & 0xFF;
                  int blueB = val1 & 0xFF;
                  aA += aB;
                  redA += redB;
                  greenA += greenB;
                  blueA += blueB;
                  count++;
               }

            }
            int a = (aA) / count;
            int red = (redA) / count;
            int green = (greenA) / count;
            int blue = (blueA) / count;
            // Store pixel in output buffer and increment offset.
            val = (a << 24) + (red << 16) + (green << 8) + blue;

            rgb[index] = val;
            index++;
         }
      }
   }

   public void filterRGB(int[] rgb, int offset, int len, Function fct) {
      int end = offset + len;
      for (int i = offset; i < end; i++) {
         rgb[i] = fct.fx(rgb[i]);
      }
   }

   /**
    * TODO insert a Progress feedback for big image modifitions made willingly by the user in the MyPixel Studio
    * @param rgb
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param fct
    */
   public void filterRGB(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, Function fct) {
      int index = 0;
      for (int i = 0; i < h; i++) {
         index = offset + m + (scanlength * (n + i));
         for (int j = 0; j < w; j++) {
            rgb[index] = fct.fx(rgb[index]);
            index++;
         }
      }
   }

   /**
    * Applies the function
    * @param img
    * @param fct
    */
   public void filterRGB(RgbImage img, Function fct) {
      if (img.isRegion()) {
         filterRGB(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), fct);
      } else {
         filterRGB(img.getRgbData(), img.getOffset(), img.getLength(), fct);
      }
   }

   /**
    * {@link ITechFilter#FILTER_TYPE_14_BLEND_SELF}.
    * <br>
    * Self blends with a slight offset
    * @param rgb
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param filter
    */
   public void filterSelfBlend(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {

      int index = 0;
      int blendop = filter.get1(FILTER_OFFSET_10_BLEND1);
      int blendAlpa = filter.get1(FILTER_OFFSET_11_BLEND_ALPHA1);
      if (filter.hasFlag(FILTER_OFFSET_02_FLAG1, FILTER_FLAG_2_BLENDER)) {
         ByteObject blender = filter.getSubFirst(IBOTypesDrw.TYPE_062_BLENDER);
         if (blender != null) {
            int transform = filter.get1(FILTER_OFFSET_08_EXTRA1);
            int wOffset = filter.get2(FILTER_OFFSET_12_W2);
            int hOffset = filter.get2(FILTER_OFFSET_13_H2);
            BlendOp bo = new BlendOp(drc, blendop, blendAlpa);
            int[] rgbTransformed = RgbImage.getRGBCheck(rgb, offset, scanlength, m, transform, w, h);
            rgbTransformed = TransformUtils.transform(rgb, w, h, transform);
            int count = 0;
            for (int i = 0; i < h; i++) {
               index = offset + m + (scanlength * (n + i));
               for (int j = 0; j < w; j++) {
                  rgb[index] = rgbTransformed[count];
                  count++;
                  index++;
               }
            }

         }
      }

   }

   /**
    * {@link ITechFilter#FILTER_TYPE_05_REPEAT_PIXEL}
    * <br>
    * 
    * @param rgb
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param filter
    */
   public void filterRepeatPixels(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      int index = 0;
      int oldv = 0;
      int blendop = filter.get2(FILTER_OFFSET_04_FUNCTION2);
      int trans = filter.get1(FILTER_OFFSET_08_EXTRA1);
      BlendOp bo = new BlendOp(drc, blendop);
      //iteration from top to bottom. each pixel is blended with the bottom
      int[] values = new int[rgb.length];
      int j = 0;
      index = offset + m + (scanlength * (n + j));
      for (int i = 0; i < w; i++) {
         int botPixel = rgb[index + scanlength];
         values[index] = bo.blendPixel(rgb[index], botPixel);
         index++;
      }
      for (j = 1; j < h - 1; j++) {
         //first pixel of line
         index = offset + m + (scanlength * (n + j));
         oldv = rgb[index];
         for (int i = 0; i < w; i++) {
            int topPixel = rgb[index - scanlength];
            int botPixel = rgb[index + scanlength];
            int newv = oldv;
            newv = bo.blendPixel(newv, topPixel);
            newv = bo.blendPixel(newv, botPixel);
            oldv = newv;
            values[index] = newv;
            index++;
         }
      }
      j = h - 1;
      index = offset + m + (scanlength * (n + j));
      for (int i = 0; i < w; i++) {
         int topPixel = rgb[index - scanlength];
         values[index] = bo.blendPixel(rgb[index], topPixel);
         index++;
      }
      for (j = 0; j < h; j++) {
         //first pixel of line
         index = offset + m + (scanlength * (n + j));
         for (int i = 0; i < w; i++) {
            rgb[index] = values[index];
            index++;
         }
      }

   }

   public void filterSepia(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      // Play around with this. 20 works well and was recommended
      // by another developer. 0 produces black/white image
      int sepiaDepth = 20;

      //value between 0 and 255;
      int sepiaIntensity = 30;

      for (int i = 0; i < h; i++) {
         int index = offset + m + (scanlength * (n + i));
         for (int j = 0; j < w; j++) {
            rgb[index] = ColorUtils.pixelToSepia(rgb[index], sepiaDepth, sepiaIntensity);
            index++;
         }
      }
   }

   public void filterSmootherBorder(int[] rgb, int w, int h) {
      int oldv = rgb[0];
      int newv = 0;
      int index = 0;
      for (int j = 0; j < h; j++) {
         for (int i = 1; i < w; i++) {
            newv = rgb[index];
            if (oldv != newv) {
               rgb[index - 1] = ColorUtils.smoothStep(oldv, 0.3, newv);
               rgb[index] = ColorUtils.smoothStep(newv, 0.3, oldv);
            }
            oldv = newv;
            index++;
         }
      }
      for (int j = 0; j < w; j++) {
         index = j;
         oldv = rgb[index];
         index += w;
         for (int i = 1; i < h; i++) {
            newv = rgb[index];
            if (oldv != newv) {
               //
               rgb[index - w] = ColorUtils.smoothStep(oldv, 0.4, newv);
               rgb[index] = ColorUtils.smoothStep(newv, 0.4, oldv);
            }
            oldv = newv;
            index += w;
         }
      }
   }

   /**
    * {@link ITechFilter#FILTER_TYPE_06_STEP_SMOOTH}
    * <br>
    * Applies the {@link DrawUtilz#smoothStep(int, double, int)} function
    * @param rgb
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    */
   public void filterSmootherBorder(int[] rgb, int offset, int scanlength, int m, int n, int w, int h) {
      int oldv = rgb[0];
      int newv = 0;
      int index = 0;
      for (int j = 0; j < h; j++) {
         index = offset + m + (scanlength * (n + j));
         oldv = rgb[index];
         index++;
         for (int i = 1; i < w; i++) {
            newv = rgb[index];
            if (oldv != newv) {
               //
               rgb[index - 1] = ColorUtils.smoothStep(oldv, 0.3, newv);
               rgb[index] = ColorUtils.smoothStep(newv, 0.3, oldv);
            }
            oldv = newv;
            index++;
         }
      }
      for (int j = 0; j < w; j++) {
         index = offset + m + j + (scanlength * n);
         oldv = rgb[index];
         index += scanlength;
         for (int i = 1; i < h; i++) {
            newv = rgb[index];
            if (oldv != newv) {
               //
               rgb[index - scanlength] = ColorUtils.smoothStep(oldv, 0.4, newv);
               rgb[index] = ColorUtils.smoothStep(newv, 0.4, oldv);
            }
            oldv = newv;
            index += scanlength;
         }
      }

   }

   /**
    * Start by a Top pixel and falls on a line
    * @param rgb
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param replace
    * @param f
    * @param T
    * @param B
    * @param L
    * @param R
    */
   public void filterStick(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, boolean replace, Function f, boolean T, boolean B, boolean L, boolean R) {
      if (rgb == null) {
         throw new NullPointerException("RGB array is null");
      }
      int index = 0;
      //for image border pixel
      if (T) {
         for (int i = 0; i < w; i++) {
            index = offset + m + (scanlength * n) + i;
            f.resetCounter();
            for (int j = 0; j < h; j++) {
               int val = f.fxa(rgb[index]);
               if (f.isAccepted()) {
                  if (j == 0) {
                     if (replace) {
                        rgb[index] = val;
                     }
                  } else {
                     rgb[index - scanlength] = val;
                  }
               }
               if (f.isFinished()) {
                  break;
               } else {
                  index += scanlength;
               }
            }
         }
      }
      if (B) {
         for (int i = 0; i < w; i++) {
            // index = offset + (w * h) - 1 - i; //version with n=0 m=0 scanlength=w
            index = offset + ((m + w) * (n + h)) - 1 - i;
            f.resetCounter();
            for (int j = 0; j < h; j++) {
               rgb[index] = f.fxa(rgb[index]);
               if (f.isFinished()) {
                  break;
               } else
                  index -= scanlength;
            }
         }
      }
      if (L) {
         for (int i = 0; i < h; i++) {
            // index = offset + i * w;
            index = offset + m + (scanlength * (n + i));
            f.resetCounter();
            for (int j = 0; j < w; j++) {
               rgb[index] = f.fxa(rgb[index]);
               if (f.isFinished()) {
                  break;
               } else
                  index++;
            }
         }
      }
      if (R) {
         for (int i = 0; i < h; i++) {
            //index = offset + (w * i) + w - 1;
            index = offset + m + w - 1 + (scanlength * (n + i));
            f.resetCounter();
            for (int j = 0; j < w; j++) {
               rgb[index] = f.fxa(rgb[index]);
               if (f.isFinished()) {
                  break;
               } else
                  index--;
            }
         }
      }
   }

   public void filterStick(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      Function f = getFilterFactory().getFilterFunction(filter);
      boolean replace = filter.hasFlag(FILTER_OFFSET_02_FLAG1, FILTER_FLAG_4_REPLACE);
      boolean T = filter.hasFlag(FILTER_OFFSET_03_FLAGP1, FILTER_FLAGP_1_TOP);
      boolean B = filter.hasFlag(FILTER_OFFSET_03_FLAGP1, FILTER_FLAGP_2_BOT);
      boolean L = filter.hasFlag(FILTER_OFFSET_03_FLAGP1, FILTER_FLAGP_3_LEFT);
      boolean R = filter.hasFlag(FILTER_OFFSET_03_FLAGP1, FILTER_FLAGP_4_RIGHT);
      filterStick(rgb, offset, scanlength, m, n, w, h, replace, f, T, B, L, R);
   }

   public void filterTBLR(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      Function fct = getFilterFactory().getFilterFunction(filter);
      if (fct == null) {
         throw new NullPointerException("Function TBLR is null");
      }
      boolean T = filter.hasFlag(FILTER_OFFSET_03_FLAGP1, FILTER_FLAGP_1_TOP);
      boolean B = filter.hasFlag(FILTER_OFFSET_03_FLAGP1, FILTER_FLAGP_2_BOT);
      boolean L = filter.hasFlag(FILTER_OFFSET_03_FLAGP1, FILTER_FLAGP_3_LEFT);
      boolean R = filter.hasFlag(FILTER_OFFSET_03_FLAGP1, FILTER_FLAGP_4_RIGHT);
      filterTBLR(rgb, offset, scanlength, m, n, w, h, fct, T, B, L, R);
   }

   public void filterTBLR(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, Function f, boolean T, boolean B, boolean L, boolean R) {
      if (rgb == null) {
         throw new NullPointerException("RGB array is null");
      }
      int index = 0;
      int add = 0;
      if (T) {
         for (int i = 0; i < w; i++) {
            index = offset + m + (scanlength * n) + i;
            f.resetCounter();
            for (int j = 0; j < h; j++) {
               rgb[index] = f.fxa(rgb[index]);
               //function count calls and rejections. 
               //we cannot count rejections outside of the function
               //therefore we have to check if sequence is finished
               if (f.isFinished()) {
                  break;
               } else {
                  index += scanlength;
               }
            }
         }
      }
      if (B) {
         for (int i = 0; i < w; i++) {
            // index = offset + (w * h) - 1 - i; //version with n=0 m=0 scanlength=w
            index = offset + ((m + w) * (n + h)) - 1 - i;
            f.resetCounter();
            for (int j = 0; j < h; j++) {
               rgb[index] = f.fxa(rgb[index]);
               if (f.isFinished()) {
                  break;
               } else
                  index -= scanlength;
            }
         }
      }
      if (L) {
         add = scanlength - w;
         for (int i = 0; i < h; i++) {
            index = offset + m + (scanlength * (n + i));
            // index = offset + i * w;
            f.resetCounter();
            for (int j = 0; j < w; j++) {
               rgb[index] = f.fxa(rgb[index]);
               if (f.isFinished()) {
                  break;
               } else
                  index++;
            }
         }
      }
      if (R) {
         for (int i = 0; i < h; i++) {
            //index = offset + (w * i) + w - 1;
            index = offset + m + w - 1 + (scanlength * (n + i));
            f.resetCounter();
            for (int j = 0; j < w; j++) {
               rgb[index] = f.fxa(rgb[index]);
               if (f.isFinished()) {
                  break;
               } else
                  index--;
            }
         }
      }
   }

   public void filterTBLR(RgbImage img, ByteObject filter) {
      if (img == null) {
         throw new NullPointerException("RgbImage is null");
      }
      //we have to manage the filter acceptor mask color here.? TODO 
      filterTBLR(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), filter);
   }

   /**
    * {@link ITechFilter#FILTER_TYPE_08_TOUCHES}
    * every pixel adjacent to maskColor will have its function called alpha value modified
    * based on a function
    * Mask color will not be modified
    * @param source
    * @param w
    * @param h
    * @param touchColor the touch color. a pixel adjacent to 2 touchColors will have a function call
    * f(pixel,2). touch colors are not processed
    * @param baseAlpha
    * @param alphaIncr
    * @param or48
    */
   public void filterTouches(int[] source, int offset, int w, int h, int touchColor, boolean or48, Function fct) {
      filterTouches(source, offset, w, 0, 0, w, h, touchColor, or48, fct);
      //	 int index = offset;
      //	 int alphaCount = 0;
      //	 int wM1 = w - 1;
      //	 int hM1 = h - 1;
      //	 //topleft pixel
      //	 int rgb = source[index];
      //	 if (rgb != touchColor) {
      //	    if (source[1] == touchColor)
      //		  alphaCount++;
      //	    if (source[w] == touchColor)
      //		  alphaCount++;
      //	    if (or48) {
      //		  if (source[index + w + 1] == touchColor)
      //			alphaCount++;
      //	    }
      //	    source[index] = fct.fx(source[index], alphaCount);
      //	    alphaCount = 0;
      //	 }
      //	 index++;
      //	 //first line
      //	 for (int i = 1; i < wM1; i++) {
      //	    rgb = source[index];
      //	    if (rgb != touchColor) {
      //		  if (source[index - 1] == touchColor)
      //			alphaCount++;
      //		  if (source[index + 1] == touchColor)
      //			alphaCount++;
      //		  if (source[index + w] == touchColor)
      //			alphaCount++;
      //		  if (or48) {
      //			if (source[index + w + 1] == touchColor)
      //			   alphaCount++;
      //			if (source[index + w - 1] == touchColor)
      //			   alphaCount++;
      //		  }
      //		  source[index] = fct.fx(source[index], alphaCount);
      //		  alphaCount = 0;
      //	    }
      //	    index++;
      //	 }
      //	 //left vertical line
      //	 index = offset + w;
      //	 for (int i = 1; i < hM1; i++) {
      //	    rgb = source[index];
      //	    if (rgb != touchColor) {
      //		  if (source[index + 1] == touchColor)
      //			alphaCount++;
      //		  if (source[index + w] == touchColor)
      //			alphaCount++;
      //		  if (source[index - w] == touchColor)
      //			alphaCount++;
      //		  if (or48) {
      //			if (source[index + w + 1] == touchColor)
      //			   alphaCount++;
      //			if (source[index - w + 1] == touchColor)
      //			   alphaCount++;
      //		  }
      //		  source[index] = fct.fx(source[index], alphaCount);
      //		  alphaCount = 0;
      //	    }
      //	    index += w;
      //	 }
      //
      //	 //right vertical line
      //	 index = offset + w + w - 1;
      //	 for (int i = 1; i < hM1; i++) {
      //	    rgb = source[index];
      //	    if (rgb != touchColor) {
      //		  if (source[index - 1] == touchColor)
      //			alphaCount++;
      //		  if (source[index + w] == touchColor)
      //			alphaCount++;
      //		  if (source[index - w] == touchColor)
      //			alphaCount++;
      //		  if (or48) {
      //			if (source[index + w - 1] == touchColor)
      //			   alphaCount++;
      //			if (source[index - w - 1] == touchColor)
      //			   alphaCount++;
      //		  }
      //		  source[index] = fct.fx(source[index], alphaCount);
      //		  alphaCount = 0;
      //	    }
      //	    index += w;
      //	 }
      //
      //	 //top right pixel
      //	 index = offset + w - 1;
      //	 rgb = source[index];
      //	 if (rgb != touchColor) {
      //	    if (source[index - 1] == touchColor)
      //		  alphaCount++;
      //	    if (source[index + w] == touchColor)
      //		  alphaCount++;
      //	    if (or48) {
      //		  if (source[index + w - 1] == touchColor)
      //			alphaCount++;
      //	    }
      //	    source[index] = fct.fx(source[index], alphaCount);
      //	    alphaCount = 0;
      //	 }
      //
      //	 //mainland
      //	 index = offset + w + 1;
      //	 //only TBLR
      //	 for (int i = 1; i < hM1; i++) {
      //	    for (int j = 1; j < wM1; j++) {
      //		  rgb = source[index];
      //		  if (rgb != touchColor) {
      //			if (source[index - 1] == touchColor)
      //			   alphaCount++;
      //			if (source[index + 1] == touchColor)
      //			   alphaCount++;
      //			if (source[index - w] == touchColor)
      //			   alphaCount++;
      //			if (source[index + w] == touchColor)
      //			   alphaCount++;
      //			if (or48) {
      //			   if (source[index + w + 1] == touchColor)
      //				 alphaCount++;
      //			   if (source[index + w - 1] == touchColor)
      //				 alphaCount++;
      //			   if (source[index - w + 1] == touchColor)
      //				 alphaCount++;
      //			   if (source[index - w - 1] == touchColor)
      //				 alphaCount++;
      //			}
      //			source[index] = fct.fx(source[index], alphaCount);
      //			alphaCount = 0;
      //		  }
      //		  index++;
      //	    }
      //	    index++;
      //	    index++;
      //	 }
      //
      //	 //bottom left pixel
      //	 index = offset + ((h - 1) * w);
      //	 rgb = source[index];
      //	 if (rgb != touchColor) {
      //	    if (source[index + 1] == touchColor)
      //		  alphaCount++;
      //	    if (source[index - w] == touchColor)
      //		  alphaCount++;
      //	    if (or48) {
      //		  if (source[index - w + 1] == touchColor)
      //			alphaCount++;
      //	    }
      //	    source[index] = fct.fx(source[index], alphaCount);
      //	    alphaCount = 0;
      //	 }
      //
      //	 //bottom right pixel
      //	 index = offset + (h * w) - 1;
      //	 rgb = source[index];
      //	 if (rgb != touchColor) {
      //	    if (source[index - 1] == touchColor)
      //		  alphaCount++;
      //	    if (source[index - w] == touchColor)
      //		  alphaCount++;
      //	    if (or48) {
      //		  if (source[index - w - 1] == touchColor)
      //			alphaCount++;
      //	    }
      //	    source[index] = fct.fx(source[index], alphaCount);
      //	    alphaCount = 0;
      //	 }
      //	 //bottom line
      //	 index = offset + ((h - 1) * w) + 1;
      //	 //last line
      //	 for (int i = 1; i < w - 1; i++) {
      //	    rgb = source[index];
      //	    if (rgb != touchColor) {
      //		  if (source[index - 1] == touchColor)
      //			alphaCount++;
      //		  if (source[index + 1] == touchColor)
      //			alphaCount++;
      //		  if (source[index - w] == touchColor)
      //			alphaCount++;
      //		  if (or48) {
      //			if (source[index - w + 1] == touchColor)
      //			   alphaCount++;
      //			if (source[index - w - 1] == touchColor)
      //			   alphaCount++;
      //		  }
      //		  source[index] = fct.fx(source[index], alphaCount);
      //		  alphaCount = 0;
      //	    }
      //	    index++;
      //	 }
   }

   public void filterTouches(int[] source, int offset, int scanlength, int m, int n, int w, int h, ByteObject filter) {
      Function fct = getFilterFactory().getFilterFunction(filter);
      int touchColor = filter.getValue(FILTER_OFFSET_05_COLOR4, 4);
      boolean or48 = filter.hasFlag(FILTER_OFFSET_02_FLAG1, FILTER_FLAG_5_OR48);
      filterTouches(source, offset, scanlength, m, n, w, h, touchColor, or48, fct);
      //SystemLog.printDraw(RgbImage.debugAlphas(source, w, h));

   }

   /**
    * {@link ITechFilter#FILTER_TYPE_08_TOUCHES}
    * @param source
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param touchColor
    * @param or48
    * @param fct
    */
   public void filterTouches(int[] source, int offset, int scanlength, int m, int n, int w, int h, int touchColor, boolean or48, Function fct) {
      int alphaCount = 0;
      int wM1 = w - 1;
      int hM1 = h - 1;
      int index = offset + m + (scanlength * n);
      //topleft pixel
      int rgb = source[index];
      if (rgb != touchColor) {
         if (source[1] == touchColor)
            alphaCount++;
         if (source[w] == touchColor)
            alphaCount++;
         if (or48) {
            if (source[index + w + 1] == touchColor)
               alphaCount++;
         }
         filterTouchFunction(source, index, alphaCount, fct);
         alphaCount = 0;
      }
      index++;
      //first line
      for (int i = 1; i < wM1; i++) {
         rgb = source[index];
         if (rgb != touchColor) {
            if (source[index - 1] == touchColor)
               alphaCount++;
            if (source[index + 1] == touchColor)
               alphaCount++;
            if (source[index + scanlength] == touchColor)
               alphaCount++;
            if (or48) {
               if (source[index + scanlength + 1] == touchColor)
                  alphaCount++;
               if (source[index + scanlength - 1] == touchColor)
                  alphaCount++;
            }
            filterTouchFunction(source, index, alphaCount, fct);
            alphaCount = 0;
         }
         index++;
      }

      //left vertical line
      // index = offset + w;
      index = offset + m + (n * scanlength) + scanlength;
      for (int i = 1; i < hM1; i++) {
         rgb = source[index];
         if (rgb != touchColor) {
            if (source[index + 1] == touchColor)
               alphaCount++;
            if (source[index + scanlength] == touchColor)
               alphaCount++;
            if (source[index - scanlength] == touchColor)
               alphaCount++;
            if (or48) {
               if (source[index + scanlength + 1] == touchColor)
                  alphaCount++;
               if (source[index - scanlength + 1] == touchColor)
                  alphaCount++;
            }
            filterTouchFunction(source, index, alphaCount, fct);
            alphaCount = 0;
         }
         index += scanlength;
      }

      //right vertical line
      index = offset + m + (n * scanlength) + scanlength + w - 1;
      for (int i = 1; i < hM1; i++) {
         rgb = source[index];
         if (rgb != touchColor) {
            if (source[index - 1] == touchColor)
               alphaCount++;
            if (source[index + scanlength] == touchColor)
               alphaCount++;
            if (source[index - scanlength] == touchColor)
               alphaCount++;
            if (or48) {
               if (source[index + scanlength - 1] == touchColor)
                  alphaCount++;
               if (source[index - scanlength - 1] == touchColor)
                  alphaCount++;
            }
            filterTouchFunction(source, index, alphaCount, fct);
            alphaCount = 0;
         }
         index += scanlength;
      }

      //top right pixel
      //index = offset + w - 1;
      index = offset + m + w - 1 + (n * scanlength);
      rgb = source[index];
      if (rgb != touchColor) {
         if (source[index - 1] == touchColor)
            alphaCount++;
         if (source[index + scanlength] == touchColor)
            alphaCount++;
         if (or48) {
            if (source[index + scanlength - 1] == touchColor)
               alphaCount++;
         }
         filterTouchFunction(source, index, alphaCount, fct);
         alphaCount = 0;
      }

      //mainland
      //index = offset + w + 1;
      //only TBLR
      for (int i = 1; i < hM1; i++) {
         index = offset + m + ((n + i) * scanlength) + 1;
         for (int j = 1; j < wM1; j++) {
            rgb = source[index];
            //SystemLog.printDraw("rgb=" + DrawUtilz.debugColor(rgb) + " touchColor=" + DrawUtilz.debugColor(touchColor));
            if (rgb != touchColor) {
               if (source[index - 1] == touchColor)
                  alphaCount++;
               if (source[index + 1] == touchColor)
                  alphaCount++;
               if (source[index - scanlength] == touchColor)
                  alphaCount++;
               if (source[index + scanlength] == touchColor)
                  alphaCount++;
               if (or48) {
                  if (source[index + scanlength + 1] == touchColor)
                     alphaCount++;
                  if (source[index + scanlength - 1] == touchColor)
                     alphaCount++;
                  if (source[index - scanlength + 1] == touchColor)
                     alphaCount++;
                  if (source[index - scanlength - 1] == touchColor)
                     alphaCount++;
               }
               filterTouchFunction(source, index, alphaCount, fct);
               alphaCount = 0;
            }
            index++;
         }
      }

      //bottom left pixel
      index = offset + m + ((n + h - 1) * scanlength);
      rgb = source[index];
      if (rgb != touchColor) {
         if (source[index + 1] == touchColor)
            alphaCount++;
         if (source[index - scanlength] == touchColor)
            alphaCount++;
         if (or48) {
            if (source[index - scanlength + 1] == touchColor)
               alphaCount++;
         }
         filterTouchFunction(source, index, alphaCount, fct);
         alphaCount = 0;
      }

      //bottom right pixel = last pixel
      //index = offset + (h * w) - 1;
      index = offset + m + w - 1 + ((n + h - 1) * scanlength);
      rgb = source[index];
      if (rgb != touchColor) {
         if (source[index - 1] == touchColor)
            alphaCount++;
         if (source[index - scanlength] == touchColor)
            alphaCount++;
         if (or48) {
            if (source[index - scanlength - 1] == touchColor)
               alphaCount++;
         }
         filterTouchFunction(source, index, alphaCount, fct);
         alphaCount = 0;
      }

      //bottom line last line
      //index = offset + ((h - 1) * w) + 1;
      index = offset + m + 1 + ((n + h - 1) * scanlength);
      for (int i = 1; i < w - 1; i++) {
         rgb = source[index];
         if (rgb != touchColor) {
            if (source[index - 1] == touchColor)
               alphaCount++;
            if (source[index + 1] == touchColor)
               alphaCount++;
            if (source[index - scanlength] == touchColor)
               alphaCount++;
            if (or48) {
               if (source[index - scanlength + 1] == touchColor)
                  alphaCount++;
               if (source[index - scanlength - 1] == touchColor)
                  alphaCount++;
            }
            filterTouchFunction(source, index, alphaCount, fct);
            alphaCount = 0;
         }
         index++;
      }
      //SystemLog.printDraw(RgbImage.debugAlphas(source, w, h));
   }

   /**
    * {@link ITechFilter#FILTER_TYPE_08_TOUCHES}
    * @param img
    * @param filter
    */
   public void filterTouches(RgbImage img, ByteObject filter) {
      //function for Touch filter is f(x=pixel,y=count)
      Function fct = getFilterFactory().getFilterFunction(filter);
      int touchColor = filter.getValue(FILTER_OFFSET_05_COLOR4, 4);
      boolean or48 = filter.hasFlag(FILTER_OFFSET_03_FLAGP1, FILTER_FLAGP_1_TOP);
      filterTouches(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), touchColor, or48, fct);
   }

   public void filterTouchFunction(int[] source, int index, int alphaCount, Function fct) {
      int pix = source[index];
      if (fct.accept(pix)) {
         //TODO check if newAlpha if bounded. else do it here
         int newAlpha = fct.fx(alphaCount);
         if (newAlpha < 0)
            newAlpha = 0;
         if (newAlpha > 255) {
            newAlpha = 255;
         }
         source[index] = (newAlpha << 24) + (pix & 0xFFFFFF);
      }
   }

}
