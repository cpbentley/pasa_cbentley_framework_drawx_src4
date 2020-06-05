/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.base;

import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;

public class TransformUtilz {

   /**
    * Returns a new array of the transformed 
    * @param rgb that array is left untouched
    * @param w
    * @param h
    * @param transform
    * @return
    */
   public static int[] transform(int[] rgb, int w, int h, int transform) {
      if (transform == 0) {
         return rgb;
      }
      int[] data = new int[rgb.length];
      System.arraycopy(rgb, 0, data, 0, rgb.length);
      switch (transform) {
         case IImage.TRANSFORM_1_FLIP_H_MIRROR_ROT180:
            flipMirrorHorizAxis(data, w, h);
            break;
         case IImage.TRANSFORM_2_FLIP_V_MIRROR:
            flipMirrorVerticalAxis(data, w, h);
            break;
         case IImage.TRANSFORM_3_ROT_180:
            rotate180(data, w, h);
            break;
         case IImage.TRANSFORM_4_MIRROR_ROT270:
            flipVrotate270(rgb, data, w, h);
            break;
         case IImage.TRANSFORM_5_ROT_90:
            //rotate90(rgb, data, w, h);
            flipVrotate270(rgb, data, w, h);
            flipMirrorVerticalAxis(data, h, w);
            break;
         case IImage.TRANSFORM_6_ROT_270:
            flipMirrorVerticalAxis(data, w, h);
            flipVrotate270(rgb, data, w, h);
            flipMirrorHorizAxis(data, h, w);
            break;
         case IImage.TRANSFORM_7_MIRROR_ROT90:
            flipMirrorVerticalAxis(data, h, w);
            flipVrotate270(rgb, data, w, h);
            rotate180(data, h, w);
            break;
         default:
            break;
      }
      return data;
   }

   /**
    * cos270=0
    * Sin270=-1
    * @param src
    * @param rgb destination buffer
    * @param w
    * @param h
    */
   public static void flipVrotate270(int[] src, int[] data, int w, int h) {
      int rotatedWidth = h;
      int rotatedHeight = w;
      int refX = 0;
      int refY = 0;
      int referenceX = 0;
      int referenceY = 0;
      for (int x = 0; x < rotatedWidth; x++) {
         for (int y = 0; y < rotatedHeight; y++) {
            refX = x - referenceX;
            refY = y - referenceY;
            int newX = refY; //(int) (refX * degreeCos + refY * degreeSin);
            int newY = refX; //(int) (refY * degreeCos - refX * degreeSin);
            //newX += halfOfWidth;
            //newY += halfOfHeigth;
            int sumXY = newX + newY * w;
            data[x + y * rotatedWidth] = src[sumXY];
         }
      }
   }

   /**
    * Rotate 180 is a flip Horizontal followed by a flip vertical
    * @param rgb
    * @param w
    * @param h
    */
   public static void rotate180(int[] rgb, int w, int h) {
      int indexH = 0;
      int sizeH = (int) h / 2;
      int mod = h % 2;
      for (int row = 0; row < sizeH; row++) {
         int index = indexH;
         for (int col = 0; col < w; col++) {
            int index2 = w * (h - row - 1) + w - col - 1;
            //swap
            int temp = rgb[index];
            rgb[index] = rgb[index2];
            rgb[index2] = temp;
            index++;
         }
         indexH += w;
      }
      //for the last row.. do a V swap
      if (mod != 0) {
         int row = sizeH;
         int w2 = w / 2;
         int index = row * w;
         for (int col = 0; col < w2; col++) {
            int index2 = (row * w) + w - col - 1;
            int temp = rgb[index];
            rgb[index] = rgb[index2];
            rgb[index2] = temp;
            index++;
         }
      }
   }

   /**
    * 
    * @param rgb
    * @param w
    * @param h
    */
   public static void flipMirrorHorizAxis(int[] rgb, int w, int h) {
      flipMirrorVerticalAxis(rgb, w, h);
      rotate180(rgb, w, h);
   }

   /**
    * 
    * @param rgb
    * @param w
    * @param h
    */
   public static void flipMirrorVerticalAxis(int[] rgb, int w, int h) {
      int w2 = (int) Math.ceil(w / 2);
      int indexH = 0;
      for (int i = 0; i < h; i++) {
         int index = indexH;
         for (int j = 0; j < w2; j++) {
            int index1 = index;
            int index2 = (i * w) + w - j - 1;
            int v1 = rgb[index1];
            int v2 = rgb[index2];
            rgb[index1] = v2;
            rgb[index2] = v1;
            index++;
         }
         indexH += w;
      }
   }

   public static boolean isTransformSwap(int trans) {
      switch (trans) {
         case IImage.TRANSFORM_5_ROT_90:
         case IImage.TRANSFORM_6_ROT_270:
         case IImage.TRANSFORM_4_MIRROR_ROT270:
         case IImage.TRANSFORM_7_MIRROR_ROT90:
            return true;
         default:
            return false;
      }
   }
}
