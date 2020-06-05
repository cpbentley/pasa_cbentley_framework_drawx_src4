/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.utils;

public class RgbUtils {

   /**
    * Reads each line from y decreaseing, x increasing
    * return the number of lines that don't have color in it
    * @param rgb
    * @param w
    * @param h
    * @param color
    * @return
    */
   public static int getDepthBot(int[] rgb, int w, int h, int color) {
      int index = 0;
      int count = 0;
      for (int i = 0; i < h; i++) {
         //start at the first pixel of last line
         index = ((h - i - 1) * w);
         for (int j = 0; j < w; j++) {
            if (rgb[index] != color) {
               return count;
            }
            index++;
         }
         count++;
      }
      return w;
   }

   /**
    * Coming from the left, number of lines with no pixel of color
    * @param rgb
    * @param w
    * @param h
    * @param color
    * @return
    */
   public static int getDepthLeft(int[] rgb, int w, int h, int color) {
      int index = 0;
      int count = 0;
      for (int i = 0; i < w; i++) {
         //start at the first pixel
         index = i;
         for (int j = 0; j < h; j++) {
            if (rgb[index] != color) {
               return count;
            }
            index += w;
         }
         count++;
      }
      return w;
   }

   public static int getDepthRight(int[] rgb, int w, int h, int color) {
      int index = 0;
      int count = 0;
      for (int i = 0; i < w; i++) {
         //starts at last pixel of first line
         index = w - i - 1;
         for (int j = 0; j < h; j++) {
            if (rgb[index] != color) {
               return count;
            }
            index += w;
         }
         count++;
      }
      return h;
   }

   public static int getDepthTop(int[] rgb, int w, int h, int color) {
      int index = 0;
      int count = 0;
      for (int j = 0; j < h; j++) {
         for (int i = 0; i < w; i++) {
            if (rgb[index] != color) {
               return count;
            }
            index++;
         }
         count++;
      }
      return h;
   }

}
