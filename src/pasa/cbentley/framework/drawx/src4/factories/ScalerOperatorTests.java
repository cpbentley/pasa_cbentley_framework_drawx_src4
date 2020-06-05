/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImageFactory;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;

public class ScalerOperatorTests extends AbstractDrwOperator {

   public ScalerOperatorTests(DrwCtx drc) {
      super(drc);
   }

   /**
    * Old Simple Fast Scale
    * @param scaleFactor percent
    * @param width
    * @param height
    * @param rgbData same size
    * @param scaledRgbData same size
    */
   public void scale(int scaleFactor, int width, int height, int[] rgbData, int[] scaledRgbData) {
      if (scaleFactor < 100) {
         int xStart = ((width * 100) - (width * scaleFactor)) / 200;
         int yStart = ((height * 100) - (height * scaleFactor)) / 200;
         for (int y = yStart; y < height - yStart; y++) {
            for (int x = xStart; x < width - xStart; x++) {
               int xTarget = (x * scaleFactor) / 100 + xStart;
               int yTarget = (y * scaleFactor) / 100 + yStart;
               scaledRgbData[(yTarget * width) + xTarget] = rgbData[(y * width) + x];
            }
         }
      } else {
         int xStart = (width - width * 100 / scaleFactor) / 2;
         int yStart = ((height - height * 100 / scaleFactor) / 2) * width;
         for (int y = 0; y < height; y++) {
            int c1 = y * width;
            int c2 = yStart + (y * 100 / scaleFactor) * width;
            for (int x = 0; x < width; x++) {
               scaledRgbData[c1 + x] = rgbData[c2 + xStart + x * 100 / scaleFactor];
            }
         }
      }
   }

   /**
    * http://forums.sun.com/thread.jspa?threadID=5345210&tstart=0
    * Modified by Mordan to support alpha
    * @param srcImage
    * @param newWidth
    * @param newHeight
    * @return
    */
   public final IImage resampleImage(IImage srcImage, int newWidth, int newHeight, boolean purplish, boolean redcountour) {

      int srcWidth = srcImage.getWidth();
      int srcHeight = srcImage.getHeight();
      int srcLength = srcWidth * srcHeight;
      int srcMax = srcLength - 1;

      int[] srcInput = new int[srcLength];
      srcImage.getRGB(srcInput, 0, srcWidth, 0, 0, srcWidth, srcHeight);

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

      //#debug
      String msg = "xDelta=" + xDelta + " xError=" + xError + " yDelta=" + yDelta + " yError=" + yError;
      //#debug
      toDLog().pDraw(msg, this, ScalerOperatorTests.class, "resampleImage", ITechLvl.LVL_05_FINE, true);

      // Whole pile of non array variables for the loop.
      int pixelA, pixelB, pixelC, pixelD;
      int ppixelA = 0, ppixelB = 0, ppixelC = 0, ppixelD = 0;

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

            // Isolate colour channels.
            if (redcountour) {
               redA = pixelA >> 16;
               redB = pixelB >> 16;
               redC = pixelC >> 16;
               redD = pixelD >> 16;
            } else {
               redA = (pixelA >> 16) & 0xFF;
               redB = (pixelB >> 16) & 0xFF;
               redC = (pixelC >> 16) & 0xFF;
               redD = (pixelD >> 16) & 0xFF;
            }
            aA = (pixelA >> 24) & 0xFF;
            aB = (pixelB >> 24) & 0xFF;
            aC = (pixelC >> 24) & 0xFF;
            aD = (pixelD >> 24) & 0xFF;

            if (purplish) {
               //greens uncomment for a purplish tone to the image
               greenA = (pixelA >> 8) & 0xFF;
               greenB = (pixelB >> 8) & 0xFF;
               greenC = (pixelC >> 8) & 0xFF;
               greenD = (pixelD >> 8) & 0xFF;
               //blues
               blueA = pixelA & 0xFF;
               blueB = pixelB & 0xFF;
               blueC = pixelC & 0xFF;
               blueD = pixelD & 0xFF;
            } else {
               greenA = pixelA & 0x00FF00;
               greenB = pixelB & 0x00FF00;
               greenC = pixelC & 0x00FF00;
               greenD = pixelD & 0x00FF00;
               blueA = pixelA & 0x0000FF;
               blueB = pixelB & 0x0000FF;
               blueC = pixelC & 0x0000FF;
               blueD = pixelD & 0x0000FF;
            }
            // Calculate new pixels colour and mask.
            a = 0x00FF0000 & (aA * weightA + aB * weightB + aC * weightC + aD * weightD);
            red = 0x00FF0000 & (redA * weightA + redB * weightB + redC * weightC + redD * weightD);
            green = 0xFF000000 & (greenA * weightA + greenB * weightB + greenC * weightC + greenD * weightD);
            blue = 0x00FF0000 & (blueA * weightA + blueB * weightB + blueC * weightC + blueD * weightD);
            // Store pixel in output buffer and increment offset.
            destOutput[destOffset++] = (a << 8) + red + (((green | blue) >> 16));
            //rawOutput[outOffset++] = (alphaA << 24) + red + (((green | blue) >> 16));

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
      return getImgFont().createRGBImage(destOutput, newWidth, newHeight, true);
   }

   public IImageFactory getImgFont() {
      return drc.getImageFactory();
   }
   
   public final IImage resampleImage4(IImage orgImage, int newWidth, int newHeight) {

      int orgWidth = orgImage.getWidth();
      int orgHeight = orgImage.getHeight();
      int orgLength = orgWidth * orgHeight;
      int orgMax = orgLength - 1;

      int[] rawInput = new int[orgLength];
      orgImage.getRGB(rawInput, 0, orgWidth, 0, 0, orgWidth, orgHeight);

      int newLength = newWidth * newHeight;

      int[] rawOutput = new int[newLength];

      int yd = (orgHeight / newHeight) * orgWidth;
      int yr = orgHeight % newHeight;
      //0 for up scale
      int xd = orgWidth / newWidth;
      // orgWidth for up scale
      int xr = orgWidth % newWidth;
      
      //#debug
      String msg = "yd=" + yd + " yr=" + yr + " xd=" + xd + " xr=" + xr;
      //#debug
      toDLog().pDraw(msg, this, ScalerOperatorTests.class, "resampleImage4", ITechLvl.LVL_05_FINE, true);

      

      int outOffset = 0;
      //start at zero
      int inOffset = 0;
      int px = 0;
      int py = 0;
      int maxPX = newWidth / xr;
      int maxPY = newHeight / yr;
      int ye = 0;
      int inLineCount = 0;
      for (int y = 0; y < newHeight; y++) {
         //new line
         int xe = 0;
         for (int x = 0; x < newWidth; x++) {
            int max = (inLineCount + 1) * orgWidth;
            int min = inLineCount * orgWidth;
            // Set source pixels.
            int p = rawInput[inOffset];

            //we are not in the first line
            int pT = inOffset >= orgWidth ? rawInput[inOffset - orgWidth] : p;
            //we are not on the last pixel of line
            int pR = inOffset + 1 < max ? rawInput[inOffset + 1] : p;
            //we are not on the first pixel of line
            int pL = inOffset - 1 >= min ? rawInput[inOffset - 1] : p;
            //we are not in the last line
            int pB = inOffset + orgWidth < orgMax ? rawInput[inOffset + orgWidth] : p;

            int val = getNewPixelC(maxPX, maxPY, px, py, p, pT, pR, pB, pL);
            rawOutput[outOffset] = val;

            outOffset++;
            // Increment input by x delta. (0 for up scale)

            xe += xr;

            if (xe >= newWidth) {
               xe = 0;
               px = 0;
               inOffset++;
            } else {
               px++;
            }
         }

         // Increment input by y delta.
         inOffset += yd;

         // Correct if we have a roll over error.
         ye += yr;
         if (ye >= newHeight) {
            ye = 0;
            py = 0;
            inLineCount++;
         } else {
            py++;
         }
         inOffset = inLineCount * orgWidth;
      }
      return getImgFont().createRGBImage(rawOutput, newWidth, newHeight, false);
   }

   public final IImage resampleImageOri(IImage orgImage, int newWidth, int newHeight) {

      int orgWidth = orgImage.getWidth();
      int orgHeight = orgImage.getHeight();
      int orgLength = orgWidth * orgHeight;
      int orgMax = orgLength - 1;

      int[] rawInput = new int[orgLength];
      orgImage.getRGB(rawInput, 0, orgWidth, 0, 0, orgWidth, orgHeight);

      int newLength = newWidth * newHeight;

      int[] rawOutput = new int[newLength];

      int yd = (orgHeight / newHeight - 1) * orgWidth;
      int yr = orgHeight % newHeight;
      int xd = orgWidth / newWidth;
      int xr = orgWidth % newWidth;
      int outOffset = 0;
      //start at zero
      int inOffset = 0;

      // Whole pile of non array variables for the loop.
      int pixelA, pixelB, pixelC, pixelD;
      int xo, yo;
      int weightA, weightB, weightC, weightD;
      int redA, redB, redC, redD;
      int greenA, greenB, greenC, greenD;
      int blueA, blueB, blueC, blueD;
      int red, green, blue;

      for (int y = newHeight, ye = 0; y > 0; y--) {
         for (int x = newWidth, xe = 0; x > 0; x--) {

            // Set source pixels.
            pixelA = inOffset;
            pixelB = pixelA + 1; //pixel right adjacent
            pixelC = pixelA + orgWidth; //pixel adjacent below
            pixelD = pixelC + 1; //pixel bottom left

            // Get pixel values from array for speed, avoiding overflow.
            pixelA = rawInput[pixelA];
            pixelB = pixelB > orgMax ? pixelA : rawInput[pixelB];
            pixelC = pixelC > orgMax ? pixelA : rawInput[pixelC];
            pixelD = pixelD > orgMax ? pixelB : rawInput[pixelD];

            // Calculate pixel weights from error values xe & ye.
            xo = (xe << 8) / newWidth;
            yo = (ye << 8) / newHeight;
            weightD = xo * yo;
            weightC = (yo << 8) - weightD;
            weightB = (xo << 8) - weightD;
            weightA = 0x10000 - weightB - weightC - weightD;

            // Isolate colour channels.
            redA = pixelA >> 16;
            redB = pixelB >> 16;
            redC = pixelC >> 16;
            redD = pixelD >> 16;
            greenA = pixelA & 0x00FF00;
            greenB = pixelB & 0x00FF00;
            greenC = pixelC & 0x00FF00;
            greenD = pixelD & 0x00FF00;
            blueA = pixelA & 0x0000FF;
            blueB = pixelB & 0x0000FF;
            blueC = pixelC & 0x0000FF;
            blueD = pixelD & 0x0000FF;

            // Calculate new pixels colour and mask.
            red = 0x00FF0000 & (redA * weightA + redB * weightB + redC * weightC + redD * weightD);
            green = 0xFF000000 & (greenA * weightA + greenB * weightB + greenC * weightC + greenD * weightD);
            blue = 0x00FF0000 & (blueA * weightA + blueB * weightB + blueC * weightC + blueD * weightD);

            // Store pixel in output buffer and increment offset.
            rawOutput[outOffset++] = red + (((green | blue) >> 16));

            // Increment input by x delta.
            inOffset += xd;

            // Correct if we have a roll over error.
            xe += xr;
            if (xe >= newWidth) {
               xe -= newWidth;
               inOffset++;
            }
         }

         // Increment input by y delta.
         inOffset += yd;

         // Correct if we have a roll over error.
         ye += yr;
         if (ye >= newHeight) {
            ye -= newHeight;
            inOffset += orgWidth;
         }
      }
      return getImgFont().createRGBImage(rawOutput, newWidth, newHeight, false);
   }

   public final int[] resampleImageOri(int[] rawInput, int orgWidth, int orgHeight, int newWidth, int newHeight) {
      int newLength = newWidth * newHeight;
      int orgLength = orgWidth * orgHeight;
      int orgMax = orgLength - 1;

      int[] rawOutput = new int[newLength];

      int yd = (orgHeight / newHeight - 1) * orgWidth;
      int yr = orgHeight % newHeight;
      int xd = orgWidth / newWidth;
      int xr = orgWidth % newWidth;
      int outOffset = 0;
      int inOffset = 0;

      // Whole pile of non array variables for the loop.
      int pixelA, pixelB, pixelC, pixelD;
      int xo, yo;
      int weightA, weightB, weightC, weightD;
      int redA, redB, redC, redD;
      int greenA, greenB, greenC, greenD;
      int blueA, blueB, blueC, blueD;
      int red, green, blue;

      for (int y = newHeight, ye = 0; y > 0; y--) {
         for (int x = newWidth, xe = 0; x > 0; x--) {

            // Set source pixels.
            pixelA = inOffset;
            pixelB = pixelA + 1;
            pixelC = pixelA + orgWidth;
            pixelD = pixelC + 1;

            // Get pixel values from array for speed, avoiding overflow.
            pixelA = rawInput[pixelA];
            pixelB = pixelB > orgMax ? pixelA : rawInput[pixelB];
            pixelC = pixelC > orgMax ? pixelA : rawInput[pixelC];
            pixelD = pixelD > orgMax ? pixelB : rawInput[pixelD];

            // Calculate pixel weights from error values xe & ye.
            xo = (xe << 8) / newWidth;
            yo = (ye << 8) / newHeight;
            weightD = xo * yo;
            weightC = (yo << 8) - weightD;
            weightB = (xo << 8) - weightD;
            weightA = 0x10000 - weightB - weightC - weightD;

            // Isolate colour channels.
            redA = pixelA >> 16;
            redB = pixelB >> 16;
            redC = pixelC >> 16;
            redD = pixelD >> 16;
            greenA = pixelA & 0x00FF00;
            greenB = pixelB & 0x00FF00;
            greenC = pixelC & 0x00FF00;
            greenD = pixelD & 0x00FF00;
            blueA = pixelA & 0x0000FF;
            blueB = pixelB & 0x0000FF;
            blueC = pixelC & 0x0000FF;
            blueD = pixelD & 0x0000FF;

            // Calculate new pixels colour and mask.
            red = 0x00FF0000 & (redA * weightA + redB * weightB + redC * weightC + redD * weightD);
            green = 0xFF000000 & (greenA * weightA + greenB * weightB + greenC * weightC + greenD * weightD);
            blue = 0x00FF0000 & (blueA * weightA + blueB * weightB + blueC * weightC + blueD * weightD);

            // Store pixel in output buffer and increment offset.
            rawOutput[outOffset++] = red + (((green | blue) >> 16));

            // Increment input by x delta.
            inOffset += xd;

            // Correct if we have a roll over error.
            xe += xr;
            if (xe >= newWidth) {
               xe -= newWidth;
               inOffset++;
            }
         }

         // Increment input by y delta.
         inOffset += yd;

         // Correct if we have a roll over error.
         ye += yr;
         if (ye >= newHeight) {
            ye -= newHeight;
            inOffset += orgWidth;
         }
      }
      return rawOutput;
   }

   public final IImage resampleImageOriGood(IImage orgImage, int newWidth, int newHeight) {
      int orgWidth = orgImage.getWidth();
      int orgHeight = orgImage.getHeight();
      int orgLength = orgWidth * orgHeight;
      int orgMax = orgLength - 1;
      int pixelA, pixelB, pixelC, pixelD;
      int[] rawInput = new int[orgLength];
      orgImage.getRGB(rawInput, 0, orgWidth, 0, 0, orgWidth, orgHeight);

      int newLength = newWidth * newHeight;

      int[] rawOutput = new int[newLength];

      int yd = (orgHeight / newHeight) * orgWidth;
      int yr = orgHeight % newHeight;
      //0 for up scale
      int xd = orgWidth / newWidth;
      // orgWidth for up scale
      int xr = orgWidth % newWidth;

      //#debug
      String msg = "yd=" + yd + " yr=" + yr + " xd=" + xd + " xr=" + xr;
      //#debug
      toDLog().pDraw(msg, this, ScalerOperatorTests.class, "resampleImageOriGood", ITechLvl.LVL_05_FINE, true);

      

      int outOffset = 0;
      //start at zero
      int inOffset = 0;

      int ye = 0;
      int inLineCount = 0;
      for (int y = 0; y < newHeight; y++) {
         //new line
         int xe = 0;
         for (int x = 0; x < newWidth; x++) {
            int max = (inLineCount + 1) * orgWidth;
            int min = inLineCount * orgWidth;

            // Set source pixels.
            pixelA = inOffset;
            pixelB = pixelA + 1 < max ? pixelA + 1 : pixelA; //pixel right adjacent
            pixelC = pixelA + orgWidth < orgMax ? pixelA + orgWidth : pixelA; //pixel adjacent below
            pixelD = pixelA + orgWidth + 1 < orgMax ? pixelA + orgWidth + 1 : pixelA; //pixel bottom left

            // Get pixel values from array for speed, avoiding overflow.
            pixelA = rawInput[pixelA];
            pixelB = rawInput[pixelB];
            pixelC = rawInput[pixelC];
            pixelD = rawInput[pixelD];

            int val = getNewPixelOri(newWidth, newHeight, xe, ye, pixelA, pixelB, pixelC, pixelD);
            rawOutput[outOffset] = val;

            outOffset++;
            // Increment input by x delta. (0 for up scale)
            inOffset += xd;

            xe += xr;

            if (xe >= newWidth) {
               xe -= newWidth;
               inOffset++;
            }
         }

         // Increment input by y delta.
         inOffset += yd;

         // Correct if we have a roll over error.
         ye += yr;
         if (ye >= newHeight) {
            ye -= newHeight;
            inLineCount++;
         }
         inOffset = inLineCount * orgWidth;
      }
      return getImgFont().createRGBImage(rawOutput, newWidth, newHeight, false);
   }

   public IImage scaleImage(IImage original, int newWidth, int newHeight) {
      int[] rawInput = new int[original.getHeight() * original.getWidth()];
      original.getRGB(rawInput, 0, original.getWidth(), 0, 0, original.getWidth(), original.getHeight());

      int[] rawOutput = new int[newWidth * newHeight];

      // YD compensates for the x loop by subtracting the width back out
      int YD = (original.getHeight() / newHeight) * original.getWidth() - original.getWidth();
      int YR = original.getHeight() % newHeight;
      int XD = original.getWidth() / newWidth;
      int XR = original.getWidth() % newWidth;
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
            inOffset += original.getWidth();
         }
      }
      return getImgFont().createRGBImage(rawOutput, newWidth, newHeight, false);
   }

   /**
    * 
    * @param newWidth
    * @param newHeight
    * @param xe horizontal pixels distance from  input pixel
    * The most xe is big the most pL is irrelevant and pR becomes relevant
    * @param ye
    * @param p current input pixel
    * @param pT
    * @param pR
    * @param pB
    * @param pL
    * @return
    */
   public int getNewPixelC(int maxPX, int maxPY, int xe, int ye, int p, int pT, int pR, int pB, int pL) {
      // Calculate pixel weights from error values xe & ye.
      int xo = ((maxPX - xe) << 8);
      int yo = ((maxPY - ye) << 8);
      //ero
      int weightD = 0;
      //	 int wB = (yo << 8) - weightD;
      //	 int wT = (yo << 8) - weightD;
      //	 int wR = (xo << 8) - weightD;
      //	 int wL = (xo << 8) - weightD;
      //	 int wP = 0x10000 - wR - wB -wT - wL - weightD;
      if (p == pT && p == pR && p == pL && p == pB) {
         return p;
      }
      int pMin = (maxPX - xe) * (maxPY - ye);
      double wMod = 0.4 / (xe + 1);
      double hMod = 0.4 / (ye + 1);
      //when xe is 0 and ye is 0
      //more xe is big, more wP is small, and even more wL
      double wB = 0.4 - hMod;
      double wT = 0.4 - wB;
      //right weight is strongest when xe = MaxPX
      double wR = 0.4 - wMod;
      double wL = 0.4 - wR;
      double wP = 1 - wB - wT - wR - wL;

      // Isolate colour channels.
      int redA = (p >> 16) & 0xFF;
      int redR = (pR >> 16) & 0xFF;
      int redB = (pB >> 16) & 0xFF;
      int redL = (pL >> 16) & 0xFF;
      int redT = (pT >> 16) & 0xFF;

      int greenA = (p >> 8) & 0xFF;
      int greenR = (pR >> 8) & 0xFF;
      int greenB = (pB >> 8) & 0xFF;
      int greenL = (pL >> 8) & 0xFF;
      int greenT = (pT >> 8) & 0xFF;

      int blueA = p & 0xFF;
      int blueR = pR & 0xFF;
      int blueB = pB & 0xFF;
      int blueL = pL & 0xFF;
      int blueT = pT & 0xFF;

      int mask = 0xFF;
      // Calculate new pixels colour and mask.
      int red = mask & (int) ((redA * wP + redR * wR + redB * wB + redL * wL + redT * wT));
      int green = mask & (int) ((greenA * wP + greenR * wR + greenB * wB + greenL * wL + greenT * wT));
      int blue = mask & (int) ((blueA * wP + blueR * wR + blueB * wB + blueL * wL + blueT * blueT));
      return ColorUtils.getRGBInt(red, green, blue);
   }

   public int getNewPixelC2(int newWidth, int newHeight, int xe, int ye, int p, int pT, int pR, int pB, int pL) {
      // Calculate pixel weights from error values xe & ye.
      int xo = (xe << 8) / newWidth;
      int yo = (ye << 8) / newHeight;
      int weightD = xo * yo;
      int wB = (yo << 8) - weightD;
      int wT = (yo << 8) - weightD;
      int wR = (xo << 8) - weightD;
      int wL = (xo << 8) - weightD;
      int wP = 0x10000 - wR - wB - weightD;

      // Isolate colour channels.
      int redA = p >> 16;
      int redR = pR >> 16;
      int redB = pB >> 16;
      int redL = pL >> 16;
      int redT = pT >> 16;

      int greenA = p & 0x00FF00;
      int greenR = pR & 0x00FF00;
      int greenB = pB & 0x00FF00;
      int greenL = pL & 0x00FF00;
      int greenT = pT & 0x00FF00;

      int blueA = p & 0x0000FF;
      int blueR = pR & 0x0000FF;
      int blueB = pB & 0x0000FF;
      int blueL = pL & 0x0000FF;
      int blueT = pT & 0x0000FF;

      // Calculate new pixels colour and mask.
      int red = 0x00FF0000 & (redA * wP + redR * wR + redB * wB + redL * wL + redT * wT);
      int green = 0xFF000000 & (greenA * wP + greenR * wR + greenB * wB + greenL * wL + greenT * wT);
      int blue = 0x00FF0000 & (blueA * wP + blueR * wR + blueB * wB + blueL * wL + blueT * blueT);
      return red + (((green | blue) >> 16));
   }

   public int getNewPixelCXOYO(int maxPX, int maxPY, int xe, int ye, int p, int pT, int pR, int pB, int pL) {
      // Calculate pixel weights from error values xe & ye.
      int xo = ((maxPX - xe) << 8);
      int yo = ((maxPY - ye) << 8);
      //ero
      int weightD = 0;
      int wB = (yo << 8) - weightD;
      int wT = (yo << 8) - weightD;
      int wR = (xo << 8) - weightD;
      int wL = (xo << 8) - weightD;
      int wP = 0x10000 - wR - wB - wT - wL - weightD;

      //	 int wB = 1;
      //	 int wT = 1;
      //	 int wR = 1;
      //	 int wL = 1;
      //	 int wP = 1;

      // Isolate colour channels.
      int redA = (p >> 16) & 0xFF;
      int redR = (pR >> 16) & 0xFF;
      int redB = (pB >> 16) & 0xFF;
      int redL = (pL >> 16) & 0xFF;
      int redT = (pT >> 16) & 0xFF;

      int greenA = (p >> 8) & 0xFF;
      int greenR = (pR >> 8) & 0xFF;
      int greenB = (pB >> 8) & 0xFF;
      int greenL = (pL >> 8) & 0xFF;
      int greenT = (pT >> 8) & 0xFF;

      int blueA = p & 0xFF;
      int blueR = pR & 0xFF;
      int blueB = pB & 0xFF;
      int blueL = pL & 0xFF;
      int blueT = pT & 0xFF;

      int mask = 0xFF0000;
      int div = 1;
      // Calculate new pixels colour and mask.
      int red = mask & ((redA * wP + redR * wR + redB * wB + redL * wL + redT * wT) / div);
      int green = mask & ((greenA * wP + greenR * wR + greenB * wB + greenL * wL + greenT * wT) / div);
      int blue = mask & ((blueA * wP + blueR * wR + blueB * wB + blueL * wL + blueT * blueT) / div);
      return ColorUtils.getRGBInt(red, green >> 8, blue >> 16);
   }

   public int getNewPixelOri(int newWidth, int newHeight, int xe, int ye, int pixelA, int pixelB, int pixelC, int pixelD) {
      // Calculate pixel weights from error values xe & ye.
      int xo = (xe << 8) / newWidth;
      int yo = (ye << 8) / newHeight;
      int weightD = xo * yo;
      int weightC = (yo << 8) - weightD;
      int weightB = (xo << 8) - weightD;
      int weightA = 0x10000 - weightB - weightC - weightD;

      // Isolate colour channels.
      int redA = pixelA >> 16;
      int redB = pixelB >> 16;
      int redC = pixelC >> 16;
      int redD = pixelD >> 16;
      int greenA = pixelA & 0x00FF00;
      int greenB = pixelB & 0x00FF00;
      int greenC = pixelC & 0x00FF00;
      int greenD = pixelD & 0x00FF00;
      int blueA = pixelA & 0x0000FF;
      int blueB = pixelB & 0x0000FF;
      int blueC = pixelC & 0x0000FF;
      int blueD = pixelD & 0x0000FF;

      // Calculate new pixels colour and mask.
      int red = 0x00FF0000 & (redA * weightA + redB * weightB + redC * weightC + redD * weightD);
      int green = 0xFF000000 & (greenA * weightA + greenB * weightB + greenC * weightC + greenD * weightD);
      int blue = 0x00FF0000 & (blueA * weightA + blueB * weightB + blueC * weightC + blueD * weightD);

      // Store pixel in output buffer and increment offset.
      return red + (((green | blue) >> 16));

   }

}
