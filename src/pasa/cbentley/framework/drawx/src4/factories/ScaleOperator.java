/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.tech.IBOScaler;
import pasa.cbentley.framework.drawx.src4.tech.ITechPass;
import pasa.cbentley.framework.drawx.src4.tech.ITechScaler;

/**
 * Scaling done by the Bentley framework.
 * <br>
 * Ask the Host, if no support for scaling
 * @author Charles Bentley
 *
 */
public class ScaleOperator extends AbstractDrwOperator implements ITechScaler {

   public ScaleOperator(DrwCtx drc) {
      super(drc);
   }

   /**
    * Takes an {@link RgbImage} and create an unrelated int[] array that represents the image scaled to new width and height.
    * <br>
    * <br>
    * @param img
    * @param newWidth
    * @param newHeight
    * @return
    */
   public int[] scaleRGB(RgbImage img, int newWidth, int newHeight) {
      if (img.isNullImage()) {
         throw new NullPointerException();
      }
      int w = img.getWidth();
      int h = img.getHeight();
      int[] rawInput = img.getRgbData();
      int[] rawOutput = drc.getMem().createIntArray(newWidth * newHeight);
      // YD compensates for the x loop by subtracting the width back out
      int YD = ((h / newHeight) * w) - w;
      int YR = h % newHeight;
      int XD = w / newWidth;
      int XR = w % newWidth;
      int outOffset = 0;
      int inOffset = 0;

      for (int y = newHeight, YE = 0; y > 0; y--) {
         for (int x = newWidth, XE = 0; x > 0; x--) {
            rawOutput[outOffset++] = rawInput[inOffset];
            inOffset += XD;
            XE += XR;
            if (XE >= newWidth) {
               XE -= newWidth;
               inOffset++;
            }
         }
         inOffset += YD;
         YE += YR;
         if (YE >= newHeight) {
            YE -= newHeight;
            inOffset += w;
         }
      }
      return rawOutput;
   }

   /**
    * Entry point for scaling an RgbImage. The image is not modified
    * @param rgb RgbImage that is scaled and modified internally
    * @param newWidth
    * @param newHeight
    * @param scaler when null,image is not scaled
    */
   public RgbImage scaleRgbImage(RgbImage rgb, int newWidth, int newHeight, ByteObject scaler) {
      if (scaler == null || rgb.isNullImage()) {
         return rgb;
      }
      int[] nr = null;
      int num = 0;
      if (scaler.hasFlag(ITechPass.PASS_OFFSET_01_FLAG1, ITechPass.PASS_FLAG_1_PRE_FILTER)) {
         ByteObject preFilter = scaler.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
         drc.getRgbImageOperator().applyColorFilter(preFilter, rgb);
         num++;
      }
      int type = scaler.get1(IBOScaler.SCALE_OFFSET_02_FIT_TYPE1);
      switch (type) {
         case SCALER_TYPE_0_FIT_NONE:
            newWidth = rgb.getWidth();
            newHeight = rgb.getHeight();
            break;
         case SCALER_TYPE_1_FIT_BOTH:
            break;
         case SCALER_TYPE_2_FIT_W:
            newHeight = (int) ((double) rgb.getHeight() * (double) newWidth / (double) rgb.getWidth());
            break;
         case SCALER_TYPE_3_FIT_H:
            newWidth = (int) ((double) rgb.getWidth() * (double) newHeight / (double) rgb.getHeight());
            break;
         case SCALER_TYPE_4_FIT_FIRST:
            //
            break;
         case SCALER_TYPE_5_FIT_LAST:
            break;
         default:
            break;
      }
      int id = scaler.get1(IBOScaler.SCALE_OFFSET_03_ID1);

      switch (id) {
         case SCALER_ID_0_LINEAR:
            nr = scaleRGB(rgb, newWidth, newHeight);
            break;
         case SCALER_ID_1_BI_LINEAR:
            nr = scaleBiLinear(rgb, newWidth, newHeight);
            break;
         case SCALER_ID_2_BI_CUBIC:
            nr = scaleBiCubic(rgb, newWidth, newHeight);
            break;
         default:
            break;
      }
      if (scaler.hasFlag(ITechPass.PASS_OFFSET_01_FLAG1, ITechPass.PASS_FLAG_2_POST_FILTER)) {
         ByteObject postFilter = scaler.getSubOrder(IBOTypesDrw.TYPE_056_COLOR_FILTER, num);
         drc.getFilterOperator().applyColorFilter(postFilter, nr, 0, newWidth, newHeight);
      }
      return rgb.getRgbCache().createImage(nr, newWidth, newHeight);
   }

   public int[] scaleBiCubic(RgbImage rgb, int newWidth, int newHeight) {
      return scaleBiLinear(rgb, newWidth, newHeight);
   }

   public class CubicInterpolator {
      public double getValue(double[] p, double x) {
         return p[1] + 0.5 * x * (p[2] - p[0] + x * (2.0 * p[0] - 5.0 * p[1] + 4.0 * p[2] - p[3] + x * (3.0 * (p[1] - p[2]) + p[3] - p[0])));
      }
   }

   public class BicubicInterpolator extends CubicInterpolator {
      private double[] arr = new double[4];

      public double getValue(double[][] p, double x, double y) {
         arr[0] = getValue(p[0], y);
         arr[1] = getValue(p[1], y);
         arr[2] = getValue(p[2], y);
         arr[3] = getValue(p[3], y);
         return getValue(arr, x);
      }
   }

   public class TricubicInterpolator extends BicubicInterpolator {
      private double[] arr = new double[4];

      public double getValue(double[][][] p, double x, double y, double z) {
         arr[0] = getValue(p[0], y, z);
         arr[1] = getValue(p[1], y, z);
         arr[2] = getValue(p[2], y, z);
         arr[3] = getValue(p[3], y, z);
         return getValue(arr, x);
      }
   }

   public RgbImage getScaledBiCubic(RgbImage rgb, int newWidth, int newHeight) {
      int[] data = scaleBiCubic(rgb, newWidth, newHeight);
      return drc.getCache().createImage(data, newWidth, newHeight);
   }

   public RgbImage getScaledBiLinear(RgbImage rgb, int newWidth, int newHeight) {
      int[] data = scaleBiLinear(rgb, newWidth, newHeight);
      return drc.getCache().createImage(data, newWidth, newHeight);
   }

   public int[] scaleBiLinear(RgbImage rgb, int newWidth, int newHeight) {
      int[] srcInput = rgb.getRgbData();
      int srcWidth = rgb.getWidth();
      int srcHeight = rgb.getHeight();
      int srcLength = srcWidth * srcHeight;
      int srcMax = srcLength - 1;

      int destLength = newWidth * newHeight;

      int[] destOutput = new int[destLength];

      //y delta
      int yDelta = (srcHeight / newHeight - 1) * srcWidth;
      //y error
      int yError = srcHeight % newHeight;
      //value that will increment srcOffset inside the double loop
      int xDelta = srcWidth / newWidth;
      //x error
      int xError = srcWidth % newWidth;
      int destOffset = 0;
      int srcOffset = 0;

      //SystemLog.printDraw("xDelta=" + xDelta + " xError=" + xError + " yDelta=" + yDelta + " yError=" + yError);

      // Whole pile of non array variables for the loop.
      int pixelA, pixelB, pixelC, pixelD;

      //int ppixelA = 0, ppixelB = 0, ppixelC = 0, ppixelD = 0;

      int xo, yo;
      int weightA, weightB, weightC, weightD;
      int redA, redB, redC, redD;
      int greenA, greenB, greenC, greenD;
      int blueA, blueB, blueC, blueD;
      int red, green, blue;
      int aA, aB, aC, aD;
      int a;
      //for all lines of new image starting from bottom
      for (int y = newHeight, ye = 0; y > 0; y--) {
         //for such a line, starting from the right
         for (int x = newWidth, xe = 0; x > 0; x--) {

            // Set source pixels.
            pixelA = srcOffset;
            pixelB = pixelA + 1;
            pixelC = pixelA + srcWidth;
            pixelD = pixelC + 1;

            // Get pixel values from array for speed, avoiding overflow.
            pixelA = srcInput[pixelA];
            pixelB = pixelB > srcMax ? pixelA : srcInput[pixelB];
            pixelC = pixelC > srcMax ? pixelA : srcInput[pixelC];
            pixelD = pixelD > srcMax ? pixelB : srcInput[pixelD];

            // Calculate pixel weights from error values xe & ye.
            xo = (xe << 8) / newWidth;
            yo = (ye << 8) / newHeight;
            weightD = xo * yo;
            weightC = (yo << 8) - weightD;
            weightB = (xo << 8) - weightD;
            weightA = 0x10000 - weightB - weightC - weightD;

            redA = (pixelA >> 16) & 0xFF;
            redB = (pixelB >> 16) & 0xFF;
            redC = (pixelC >> 16) & 0xFF;
            redD = (pixelD >> 16) & 0xFF;
            aA = (pixelA >> 24) & 0xFF;
            aB = (pixelB >> 24) & 0xFF;
            aC = (pixelC >> 24) & 0xFF;
            aD = (pixelD >> 24) & 0xFF;

            greenA = pixelA & 0x00FF00;
            greenB = pixelB & 0x00FF00;
            greenC = pixelC & 0x00FF00;
            greenD = pixelD & 0x00FF00;

            blueA = pixelA & 0x0000FF;
            blueB = pixelB & 0x0000FF;
            blueC = pixelC & 0x0000FF;
            blueD = pixelD & 0x0000FF;

            // Calculate new pixels colour and mask.
            a = 0x00FF0000 & (aA * weightA + aB * weightB + aC * weightC + aD * weightD);
            red = 0x00FF0000 & (redA * weightA + redB * weightB + redC * weightC + redD * weightD);
            green = 0xFF000000 & (greenA * weightA + greenB * weightB + greenC * weightC + greenD * weightD);
            blue = 0x00FF0000 & (blueA * weightA + blueB * weightB + blueC * weightC + blueD * weightD);
            // Store pixel in output buffer and increment offset.
            destOutput[destOffset++] = (a << 8) + red + (((green | blue) >> 16));

            // Increment input by x delta.
            srcOffset += xDelta;

            // Correct if we have a roll over error.
            xe += xError;
            if (xe >= newWidth) {
               xe -= newWidth;
               srcOffset++;
            }
         }

         // Increment input by y delta.
         srcOffset += yDelta;

         // Correct if we have a roll over error.
         ye += yError;
         if (ye >= newHeight) {
            ye -= newHeight;
            srcOffset += srcWidth;
         }
      }
      return destOutput;
   }

}
