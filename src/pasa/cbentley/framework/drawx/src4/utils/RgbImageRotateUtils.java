/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.utils;

import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

/**
 * 
 * Implementation is at {@link RgbImageRotateUtils#rotate(int[], int, int, int, int, int, double, double, int[], int, int)}
 * 
 * 
 * @author Charles Bentley
 *
 */
public class RgbImageRotateUtils {

   private DrwCtx drc;

   public RgbImageRotateUtils(DrwCtx drc) {
      this.drc = drc;
   }

   public final int getRotatedHeight(int degree, int width, int heigth, double degreeCos, double degreeSin) {
      if (degree == -90 || degree == 90 || degree == 270 || degree == -270) {
         return width;
      } else if (degree == 360 || degree == 180 || degree == 0) {
         return heigth;
      }
      long pointY1 = round(0 * degreeSin + 0 * degreeCos);
      long pointY2 = round(width * degreeSin + 0 * degreeCos);
      long pointY3 = round(0 * degreeSin + heigth * degreeCos);
      long pointY4 = round(width * degreeSin + heigth * degreeCos);
      long minY = pointY1;
      if (pointY2 < minY) {
         minY = pointY2;
      }
      if (pointY3 < minY) {
         minY = pointY3;
      }
      if (pointY4 < minY) {
         minY = pointY4;
      }

      long maxY = pointY1;
      if (pointY2 > maxY) {
         maxY = pointY2;
      }
      if (pointY3 > maxY) {
         maxY = pointY3;
      }
      if (pointY4 > maxY) {
         maxY = pointY4;
      }
      return (int) (maxY - minY);
   }

   public final int getRotatedWidth(int degree, int width, int heigth, double degreeCos, double degreeSin) {
      if (degree == -90 || degree == 90 || degree == 270 || degree == -270) {
         return heigth;
      } else if (degree == 360 || degree == 180 || degree == 0) {
         return width;
      }
      long pointX1 = 0; // MathUtil.round(0 * degreeCos - 0 * degreeSin);
      long pointX2 = round(width * degreeCos); //MathUtil.round(width * degreeCos - 0 *degreeSin);
      long pointX3 = round(-heigth * degreeSin); // MathUtil.round(0 *degreeCos - heigth *degreeSin);
      long pointX4 = round(width * degreeCos - heigth * degreeSin);
      long minX = pointX1;
      if (pointX2 < minX) {
         minX = pointX2;
      }
      if (pointX3 < minX) {
         minX = pointX3;
      }
      if (pointX4 < minX) {
         minX = pointX4;
      }
      long maxX = pointX1;
      if (pointX2 > maxX) {
         maxX = pointX2;
      }
      if (pointX3 > maxX) {
         maxX = pointX3;
      }
      if (pointX4 > maxX) {
         maxX = pointX4;
      }
      return (int) (maxX - minX);
   }

   /**
    * Rotate around the center
    * @param argbArray
    * @param width
    * @param height
    * @param degree
    * @param backgroundColor
    * @return
    */
   public final int[] rotate(int[] argbArray, int width, int height, int degree, int backgroundColor) {
      return rotate(argbArray, width, height, degree, width / 2, height / 2, backgroundColor);
   }

   /**
    * 
    * @param sourceRgbData
    * @param width
    * @param height
    * @param referenceX
    * @param referenceY
    * @param backgroundColor
    * @param degreeCos
    * @param degreeSin
    * @param rotatedRGB
    * @param rotatedWidth
    * @param rotatedHeight
    */
   public void rotate(int[] sourceRgbData, int width, int height, int referenceX, int referenceY, int backgroundColor, double degreeCos, double degreeSin, int[] rotatedRGB, int rotatedWidth, int rotatedHeight) {
      //3/01/2016.. it took me 4 hours to find this bug. you need to make the reference point from the rotated dimension.
      referenceX = rotatedWidth / 2;
      referenceY = rotatedHeight / 2;
      int halfOfWidth = width / 2;
      int halfOfHeigth = height / 2;
      int refX, refY, newX, newY, srcXY;
      for (int x = 0; x < rotatedWidth; x++) {
         for (int y = 0; y < rotatedHeight; y++) {
            refX = x - referenceX;
            refY = y - referenceY;
            newX = (int) (refX * degreeCos - refY * degreeSin);
            newY = (int) (refX * degreeSin + refY * degreeCos);
            newX += halfOfWidth;
            newY += (halfOfHeigth);
            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
               srcXY = newX + newY * width;
               rotatedRGB[x + y * rotatedWidth] = sourceRgbData[srcXY];
            } else {
               rotatedRGB[x + y * rotatedWidth] = backgroundColor;
            }
         }
      }
   }

   public final int[] rotate(int[] sourceRgbData, int width, int height, int degree, int referenceX, int referenceY, int backgroundColor) {
      double degreeCos = Math.cos(Math.PI * degree / 180);
      double degreeSin = Math.sin(Math.PI * degree / 180);
      int rotatedWidth = getRotatedWidth(degree, width, height, degreeCos, degreeSin);
      int rotatedHeight = getRotatedHeight(degree, width, height, degreeCos, degreeSin);
      int[] rotatedRgb = new int[rotatedHeight * rotatedWidth];
      rotate(sourceRgbData, width, height, referenceX, referenceY, backgroundColor, degreeCos, degreeSin, rotatedRgb, rotatedWidth, rotatedHeight);
      return rotatedRgb;
   }

   public IImage rotate(RgbImage image, int angle) {
      return rotate(image, angle, image.getWidth() / 2, image.getHeight() / 2);
   }

   public IImage rotate(RgbImage image, int angle, int bgColor) {
      return rotate(image, angle, image.getWidth() / 2, image.getHeight() / 2, bgColor);
   }

   public IImage rotate(RgbImage image, int angle, int referenceX, int referenceY) {
      return rotate(image, angle, referenceX, referenceY, 0x00FFFFFF);
   }

   public IImage rotate(RgbImage image, int angle, int referenceX, int referenceY, int bgColor) {
      int[] rgbData = image.getRgbData();
      int width = image.getWidth();
      int height = image.getHeight();
      double degreeCos = Math.cos(Math.PI * angle / 180);
      double degreeSin = Math.sin(Math.PI * angle / 180);
      int rotatedWidth = getRotatedWidth(angle, width, height, degreeCos, degreeSin);
      int rotatedHeight = getRotatedHeight(angle, width, height, degreeCos, degreeSin);
      int[] rotatedRgbData = new int[rotatedWidth * rotatedHeight];
      rotate(rgbData, width, height, referenceX, referenceY, bgColor, degreeCos, degreeSin, rotatedRgbData, rotatedWidth, rotatedHeight);
      
      return drc.getImageFactory().createRGBImage(rotatedRgbData, rotatedWidth, rotatedHeight, true);
   }

   public RgbImage rotateRGB(RgbImage image, int angle) {
      return rotateRGB(image, angle, image.getWidth() / 2, image.getHeight() / 2);
   }

   public RgbImage rotateRGB(RgbImage image, int angle, int bgColor) {
      return rotateRGB(image, angle, image.getWidth() / 2, image.getHeight() / 2, bgColor);
   }

   public RgbImage rotateRGB(RgbImage image, int angle, int referenceX, int referenceY) {
      return rotateRGB(image, angle, referenceX, referenceY, 0x00FFFFFF);
   }

   /**
    * Rotating a NULL image returns NULL
    * @param image
    * @param angle
    * @param referenceX
    * @param referenceY
    * @return
    */
   public RgbImage rotateRGB(RgbImage image, int angle, int referenceX, int referenceY, int bgColor) {
      if (image.isNullImage()) {
         return image;
      }
      int[] rgbData = image.getRgbData();
      int width = image.getWidth();
      int height = image.getHeight();
      double degreeCos = Math.cos(Math.PI * angle / 180);
      double degreeSin = Math.sin(Math.PI * angle / 180);
      int rotatedWidth = getRotatedWidth(angle, width, height, degreeCos, degreeSin);
      int rotatedHeight = getRotatedHeight(angle, width, height, degreeCos, degreeSin);
      int[] rotatedRgbData = new int[rotatedWidth * rotatedHeight];
      rotate(rgbData, width, height, referenceX, referenceY, bgColor, degreeCos, degreeSin, rotatedRgbData, rotatedWidth, rotatedHeight);
      return image.getRgbCache().createImage(rotatedRgbData, rotatedWidth, rotatedHeight);
   }

   private long round(double value) {
      if (value < 0) {
         return (long) (value - 0.5);
      } else {
         return (long) (value + 0.5);
      }
   }
}
