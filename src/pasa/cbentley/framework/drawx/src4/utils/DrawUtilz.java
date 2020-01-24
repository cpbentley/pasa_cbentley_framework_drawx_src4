package pasa.cbentley.framework.drawx.src4.utils;

import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.drawx.src4.interfaces.IAccept;

public class DrawUtilz {
//
//   public static final int    DIR_BOT                 = 2;
//
//   public static final int    DIR_LEFT                = 1;
//
//   public static final int    DIR_RIGHT               = 3;
//
//   public static final int    DIR_TOP                 = 0;


   /**
    * every pixel adjacent to maskColor will have its alpha value modified
    * based on a function
    * Mask color will not be modified
    * @param source
    * @param w
    * @param h
    * @param maskColor
    * @param baseAlpha
    * @param alphaIncr
    * @param or48
    */
   public static void alphaTouches(int[] source, int w, int h, int maskColor, int fid, int baseAlpha, int alphaIncr, boolean or48) {
      int index = 0;
      int alphaCount = 0;
      int wM1 = w - 1;
      int hM1 = h - 1;
      //topleft pixel
      int rgb = source[index];
      if (rgb != maskColor) {
         if (source[1] == maskColor)
            alphaCount++;
         if (source[w] == maskColor)
            alphaCount++;
         if (or48) {
            if (source[index + w + 1] == maskColor)
               alphaCount++;
         }
         source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
         source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
         alphaCount = 0;
      }
      index++;
      //first line
      for (int i = 1; i < wM1; i++) {
         rgb = source[index];
         if (rgb != maskColor) {
            if (source[index - 1] == maskColor)
               alphaCount++;
            if (source[index + 1] == maskColor)
               alphaCount++;
            if (source[index + w] == maskColor)
               alphaCount++;
            if (or48) {
               if (source[index + w + 1] == maskColor)
                  alphaCount++;
               if (source[index + w - 1] == maskColor)
                  alphaCount++;
            }
            source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
            alphaCount = 0;
         }
         index++;
      }
      //left vertical line
      index = w;
      for (int i = 1; i < hM1; i++) {
         rgb = source[index];
         if (rgb != maskColor) {
            if (source[index + 1] == maskColor)
               alphaCount++;
            if (source[index + w] == maskColor)
               alphaCount++;
            if (source[index - w] == maskColor)
               alphaCount++;
            if (or48) {
               if (source[index + w + 1] == maskColor)
                  alphaCount++;
               if (source[index - w + 1] == maskColor)
                  alphaCount++;
            }
            source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
            alphaCount = 0;
         }
         index += w;
      }

      //right vertical line
      index = w + w - 1;
      for (int i = 1; i < hM1; i++) {
         rgb = source[index];
         if (rgb != maskColor) {
            if (source[index - 1] == maskColor)
               alphaCount++;
            if (source[index + w] == maskColor)
               alphaCount++;
            if (source[index - w] == maskColor)
               alphaCount++;
            if (or48) {
               if (source[index + w - 1] == maskColor)
                  alphaCount++;
               if (source[index - w - 1] == maskColor)
                  alphaCount++;
            }
            source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
            alphaCount = 0;
         }
         index += w;
      }

      //top right pixel
      index = w - 1;
      rgb = source[index];
      if (rgb != maskColor) {
         if (source[index - 1] == maskColor)
            alphaCount++;
         if (source[index + w] == maskColor)
            alphaCount++;
         if (or48) {
            if (source[index + w - 1] == maskColor)
               alphaCount++;
         }
         source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
         alphaCount = 0;
      }

      //mainland
      index = w + 1;
      //only TBLR
      for (int i = 1; i < hM1; i++) {
         for (int j = 1; j < wM1; j++) {
            rgb = source[index];
            alphaCount = 0;
            if (rgb != maskColor) {
               if (source[index - 1] == maskColor)
                  alphaCount++;
               if (source[index + 1] == maskColor)
                  alphaCount++;
               if (source[index - w] == maskColor)
                  alphaCount++;
               if (source[index + w] == maskColor)
                  alphaCount++;
               if (or48) {
                  if (source[index + w + 1] == maskColor)
                     alphaCount++;
                  if (source[index + w - 1] == maskColor)
                     alphaCount++;
                  if (source[index - w + 1] == maskColor)
                     alphaCount++;
                  if (source[index - w - 1] == maskColor)
                     alphaCount++;
               }
               source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
            }
            index++;
         }
         index++;
         index++;
      }

      //bottom left pixel
      index = ((h - 1) * w);
      rgb = source[index];
      if (rgb != maskColor) {
         if (source[index + 1] == maskColor)
            alphaCount++;
         if (source[index - w] == maskColor)
            alphaCount++;
         if (or48) {
            if (source[index - w + 1] == maskColor)
               alphaCount++;
         }
         source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
         alphaCount = 0;
      }

      //bottom right pixel
      index = (h * w) - 1;
      rgb = source[index];
      if (rgb != maskColor) {
         if (source[index - 1] == maskColor)
            alphaCount++;
         if (source[index - w] == maskColor)
            alphaCount++;
         if (or48) {
            if (source[index - w - 1] == maskColor)
               alphaCount++;
         }
         source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
         alphaCount = 0;
      }
      //bottom line
      index = ((h - 1) * w) + 1;
      //last line
      for (int i = 1; i < w - 1; i++) {
         rgb = source[index];
         if (rgb != maskColor) {
            if (source[index - 1] == maskColor)
               alphaCount++;
            if (source[index + 1] == maskColor)
               alphaCount++;
            if (source[index - w] == maskColor)
               alphaCount++;
            if (or48) {
               if (source[index - w + 1] == maskColor)
                  alphaCount++;
               if (source[index - w - 1] == maskColor)
                  alphaCount++;
            }
            source[index] = ColorUtils.setAlpha(source[index], baseAlpha + alphaCount * alphaIncr);
            alphaCount = 0;
         }
         index++;
      }
   }

   /**
    * Returns an TBLR account of the crop
    * @param rgb
    * @param w
    * @param h
    * @param color
    * @return
    */
   public static int[] cropTBLRDistances(int[] rgb, int w, int h, int color) {
      int index = 0;
      int minLeftCount = w;
      int minRightCount = w;
      int minTopCount = h;
      int minBotCount = h;
      int localCount = 0;
      //top
      for (int i = 0; i < w; i++) {
         index = i;
         localCount = 0;
         for (int j = 0; j < h; j++) {
            if (rgb[index] != color) {
               if (localCount < minTopCount) {
                  minTopCount = localCount;
               }
               break;
            }
            index += w;
            localCount++;
         }
         if (minTopCount == 0) {
            //no point to continue. crop distance is zero
            break;
         }
      }
      //bottom
      for (int i = 0; i < w; i++) {
         index = w * h - 1 - i;
         localCount = 0;
         for (int j = 0; j < h; j++) {
            if (rgb[index] != color) {
               if (localCount < minBotCount)
                  minBotCount = localCount;
               break;
            }
            index -= w;
            localCount++;
         }
         if (minBotCount == 0)
            break;
      }
      //left
      for (int i = 0; i < h; i++) {
         localCount = 0;
         index = (i * w);
         for (int j = 0; j < w; j++) {
            if (rgb[index] != color) {
               if (localCount < minLeftCount)
                  minLeftCount = localCount;
               break;
            }
            index++;
            localCount++;
         }
      }
      //right
      for (int i = 0; i < h; i++) {
         localCount = 0;
         index = (i * w) + w - 1;
         for (int j = w - 1; j >= 0; j--) {
            if (rgb[index] != color) {
               if (localCount < minRightCount)
                  minRightCount = localCount;
               break;
            }
            localCount++;
            index--;
         }
      }
      int[] vals = new int[] { minTopCount, minBotCount, minLeftCount, minRightCount };
      return vals;
   }

   public static int[] crop(int[] rgb, int w, int h, int dx, int dy, int dw, int dh) {
      if (dx < 0)
         dx = 0;
      if (dy < 0)
         dy = 0;
      if (dx + dw > w) {
         dw = w - dx;
      }
      if (dy + dh > h) {
         dh = h - dy;
      }
      int[] nrgb = new int[dw * dh];
      int index = 0;
      int sindex = 0;
      for (int i = 0; i < dh; i++) {
         sindex = (dy + i) * w + dx;
         for (int j = 0; j < dw; j++) {
            nrgb[index] = rgb[sindex];
            index++;
            sindex++;
         }
      }
      return nrgb;
   }

   /**
    * Take the color at x,y and all adjacent pixel of the same color are replaced
    * with color
    * @param rgb
    * @param x
    * @param y
    * @param color
    */
   public static void fillColor(int[] rgb, int w, int h, int x, int y, int color) {

   }

   public static void getTBLRPixels(int[] rgb, int w, int h, int depth, IntBuffer buff) {
      getTBLRPixels(rgb, w, h, depth, C.POS_0_TOP, buff);
      getTBLRPixels(rgb, w, h, depth, C.POS_1_BOT, buff);
      getTBLRPixels(rgb, w, h, depth, C.POS_2_LEFT, buff);
      getTBLRPixels(rgb, w, h, depth, C.POS_3_RIGHT, buff);
   }

   /**
    * 
    * @param rgb
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param filter
    * @return number of pixels added.
    */
   public static void getTBLRPixels(int[] rgb, int w, int h, int depth, int dir, IntBuffer buff) {
      int index = 0;
      switch (dir) {
         case C.POS_0_TOP:
            for (int j = 0; j < depth; j++) {
               for (int i = 0; i < w; i++) {
                  int pix = rgb[index];
                  if (!buff.contains(pix)) {
                     buff.addInt(pix);
                  }
                  index++;
               }
            }
            break;
         case C.POS_1_BOT:
            for (int j = h - 1; j > h - 1 - depth; j--) {
               index = j * w;
               for (int i = 0; i < w; i++) {
                  int pix = rgb[index];
                  if (!buff.contains(pix)) {
                     buff.addInt(pix);
                  }
                  index++;
               }
            }
            break;
         case C.POS_2_LEFT:
            for (int i = 0; i < w; i++) {
               for (int j = 0; j < h; j++) {

               }
            }
            break;
         case C.POS_3_RIGHT:
            for (int i = 0; i < w; i++) {
               for (int j = 0; j < h; j++) {

               }
            }
            break;
         default:
            break;
      }
   }

   public static boolean gradientLoopBody(int[] rgb, int index, int maskColor, int[] count, int size, int alphaStep) {
      int alpha = count[2];
      if (rgb[index] != maskColor) {
         if (count[0] > size) {
            return true;
         } else {
            if (alpha > 255) {
               return true;
            }
            int pa = (rgb[index] >> 24) & 0xFF;
            if (pa > alpha)
               rgb[index] = ColorUtils.setAlpha(rgb[index], alpha);
            count[0]++;
            count[2] += alphaStep;
         }
      } else {
         if (count[1] == 1) {
            //count mask
            count[0]++;
            count[2] += alphaStep;
         }
      }
      return false;
   }

   /**
    * Sets all pixel with alpha value
    * @param rgb
    * @param alpha
    */
   public static void setAlpha(int[] rgb, int alpha) {
      setAlpha(rgb, 0, rgb.length, alpha);
   }

   /**
    * Set alpha to all pixels
    * @param rgb
    * @param offset
    * @param len
    * @param alpha
    */
   public static void setAlpha(int[] rgb, int offset, int len, int alpha) {
      int end = offset + len;
      for (int i = offset; i < end; i++) {
         int opaque = rgb[i] & 0xFFFFFF;
         int a = (alpha << 24);
         rgb[i] = opaque + a;
      }
   }

   /**
    * Any color with identical rgb channels will have it alpha value set to alpha
    * @param rgb
    * @param offset
    * @param len
    * @param color
    * @param alpha
    */
   public static void setAlpha(int[] rgb, int offset, int len, int color, int alpha) {
      int opaqueColor = color & 0xFFFFFF;
      int end = offset + len;
      for (int i = offset; i < end; i++) {
         int opaque = rgb[i] & 0xFFFFFF;
         if (opaque == opaqueColor) {
            rgb[i] = opaque + (alpha << 24);
         }
      }
   }

   public static void setAlpha(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, int alpha) {
      int index = 0;
      for (int i = 0; i < h; i++) {
         index = offset + m + (scanlength * (n + i));
         for (int j = 0; j < w; j++) {
            int opaque = rgb[index] & 0xFFFFFF;
            int a = (alpha << 24);
            rgb[index] = opaque + a;
            index++;
         }
      }
   }

   public static void setAlphaGradient(int[] rgb, int w, int h, int maskColor, int size) {
      setAlphaGradient(rgb, w, h, maskColor, size, true);
   }

   /**
    * Alpha gradient at the 4 cardinal directions.
    * @param rgb
    * @param w
    * @param h
    * @param maskColor
    * @param size
    * @param doMask if true count mask pixel in the transparent brush count
    */
   public static void setAlphaGradient(int[] rgb, int w, int h, int maskColor, int size, boolean doMask) {
      setAlphaGradient(rgb, w, h, maskColor, size, doMask, C.DIR_0TOP);
      setAlphaGradient(rgb, w, h, maskColor, size, doMask, C.DIR_1BOTTOM);
      setAlphaGradient(rgb, w, h, maskColor, size, doMask, C.DIR_2LEFT);
      setAlphaGradient(rgb, w, h, maskColor, size, doMask, C.DIR_3RIGHT);
   }

   /**
    * 
    * @param rgb
    * @param w
    * @param h
    * @param maskColor
    * @param size
    * @param doMask mask pixel count in the size count
    * @param dir
    */
   public static void setAlphaGradient(int[] rgb, int w, int h, int maskColor, int size, boolean doMask, int dir) {
      int index = 0;
      int alphaStep = 255 / size;
      //[0] = count
      //[1] = 1 only starts counting when first non mask color
      //[2] = alpha count
      int[] count = new int[3];
      if (doMask)
         count[1] = 1;
      boolean doBreak = false;
      if (dir == C.DIR_0TOP) {
         for (int i = 0; i < w; i++) {
            index = i;
            count[0] = 0;
            count[2] = 0;
            for (int j = 0; j < h; j++) {
               doBreak = gradientLoopBody(rgb, index, maskColor, count, size, alphaStep);
               if (doBreak)
                  break;
               index += w;
            }
         }
      } else if (dir == C.DIR_2LEFT) {
         for (int i = 0; i < h; i++) {
            index = i * w;
            count[0] = 0;
            count[2] = 0;
            for (int j = 0; j < w; j++) {
               doBreak = gradientLoopBody(rgb, index, maskColor, count, size, alphaStep);
               if (doBreak)
                  break;
               index++;
            }
         }
      } else if (dir == C.DIR_3RIGHT) {
         for (int i = 0; i < h; i++) {
            index = (i * w) + w - 1;
            count[0] = 0;
            count[2] = 0;
            for (int j = w - 1; j >= 0; j--) {
               doBreak = gradientLoopBody(rgb, index, maskColor, count, size, alphaStep);
               if (doBreak)
                  break;
               index--;
            }
         }
      } else if (dir == C.DIR_1BOTTOM) {
         for (int i = 0; i < w; i++) {
            index = w * h - 1 - i;
            count[0] = 0;
            count[2] = 0;
            for (int j = 0; j < h; j++) {
               doBreak = gradientLoopBody(rgb, index, maskColor, count, size, alphaStep);
               if (doBreak)
                  break;
               index -= w;
            }
         }
      }
   }

   public static void setAlphaToColorARGB(int color, int[] rgb, int offset, int len, int alpha) {
      int end = offset + len;
      for (int i = offset; i < end; i++) {
         if (rgb[i] == color) {
            rgb[i] = (rgb[i] & 0xFFFFFF) + (alpha << 24);
         }
      }
   }

   public static void setAlphaToColorARGB(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, int alpha, int color) {
      int index = 0;
      for (int i = 0; i < h; i++) {
         index = offset + m + (scanlength * (n + i));
         for (int j = 0; j < w; j++) {
            if (rgb[index] == color) {
               rgb[index] = (rgb[index] & 0xFFFFFF) + (alpha << 24);
            }
            index++;
         }
      }
   }

   public static void setAlphaToColorRGB(int color, int[] rgb, int offset, int len, int alpha) {
      int opaqueColor = color & 0xFFFFFF;
      int end = offset + len;
      for (int i = offset; i < end; i++) {
         int opaque = rgb[i] & 0xFFFFFF;
         if (opaque == opaqueColor) {
            rgb[i] = opaque + (alpha << 24);
         }
      }
   }

   /**
    * For equality purposes, alpha values are ignored
    * @param rgb
    * @param color alpha value is ignored
    * @param alpha
    */
   public static void setAlphaToColorRGB(int[] rgb, int color, int alpha) {
      setAlphaToColorRGB(color, rgb, 0, rgb.length, alpha);
   }

   /**
    * All pixels whose opaque channels match the opaque channels of parameter color will have their alpha value modified
    * @param rgb
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param alpha
    * @param color
    */
   public static void setAlphaToColorRGB(int[] rgb, int offset, int scanlength, int m, int n, int w, int h, int alpha, int color) {
      int opaqueColor = color & 0xFFFFFF;
      int index = 0;
      for (int i = 0; i < h; i++) {
         index = offset + m + (scanlength * (n + i));
         for (int j = 0; j < w; j++) {
            int opaque = rgb[index] & 0xFFFFFF;
            if (opaque == opaqueColor) {
               rgb[index] = opaque + (alpha << 24);
            }
            index++;
         }
      }
   }


   public static void stickPixel(int[] rgb, int w, int h, int maskColor, int stickColor) {
      stickPixel(rgb, w, h, maskColor, stickColor, C.POS_0_TOP);
      stickPixel(rgb, w, h, maskColor, stickColor, C.POS_1_BOT);
      stickPixel(rgb, w, h, maskColor, stickColor, C.POS_2_LEFT);
      stickPixel(rgb, w, h, maskColor, stickColor, C.POS_3_RIGHT);
   }

   /**
    * Gets the crop as a region rectangle (x,y,w,h);
    * @param rgb
    * @param w
    * @param h
    * @param maskColor
    * @param stickColor
    * @param dir
    * @return
    */
   public static int[] cropGetFrame(int[] rgb, int w, int h, int color) {
      int[] vals = cropTBLRDistances(rgb, w, h, color);
      int minLeftCount = vals[2];
      int minRightCount = vals[3];
      int minTopCount = vals[0];
      int minBotCount = vals[1];
      int newX = minLeftCount;
      int newY = minTopCount;
      int newW = w - newX - minRightCount;
      int newH = h - newY - minBotCount;
      vals[0] = newX;
      vals[1] = newY;
      vals[2] = newW;
      vals[3] = newH;
      return vals;
   }

   public static int computeOverlay(int[] rgbLeft, int wl, int hl, int color, int[] rgbRight, int wR, int hR) {
      int overlay = 0;
      //compute
      int[] vLeft = new int[hl];
      //compute on the right for the left image
      int index = wl - 1;
      int hCount = 0;
      for (int i = 0; i < hl; i++) {
         index = (i * wl) + wl - 1;
         for (int j = wl - 1; j >= 0; j--) {
            if (rgbLeft[index] != color) {
               vLeft[i] = hCount;
               hCount = 0;
               break;
            }
            hCount++;
            index--;
         }
      }
      int[] vRight = new int[hR];
      for (int i = 0; i < hR; i++) {
         index = i * wR;
         for (int j = 0; j < wR; j++) {
            if (rgbRight[index] != color) {
               vRight[i] = hCount;
               hCount = 0;
               break;
            }
            hCount++;
            index++;
         }
      }
      int min = wl;
      int vMin = Math.min(hl, hR);
      for (int i = 0; i < vMin; i++) {
         if (vLeft[i] + vRight[i] < min) {
            min = vLeft[i] + vRight[i];
         }
      }
      overlay = min;
      return overlay;
   }

   public static void eraseFromOuside(int[] rgb, int w, int h, int maskColor, int stickColor) {
      eraseFromOuside(rgb, w, h, maskColor, stickColor, C.POS_0_TOP);
      eraseFromOuside(rgb, w, h, maskColor, stickColor, C.POS_1_BOT);
      eraseFromOuside(rgb, w, h, maskColor, stickColor, C.POS_2_LEFT);
      eraseFromOuside(rgb, w, h, maskColor, stickColor, C.POS_3_RIGHT);
   }

   public static void eraseFromOuside(int[] rgb, int w, int h, IAccept ac, int stickColor) {
      eraseFromOuside(rgb, w, h, ac, stickColor, C.POS_0_TOP);
      eraseFromOuside(rgb, w, h, ac, stickColor, C.POS_1_BOT);
      eraseFromOuside(rgb, w, h, ac, stickColor, C.POS_2_LEFT);
      eraseFromOuside(rgb, w, h, ac, stickColor, C.POS_3_RIGHT);
   }

   public static void eraseFromOuside(int[] rgb, int w, int h, IAccept ac, int stickColor, int dir) {
      int index = 0;
      switch (dir) {
         case C.POS_0_TOP:
            for (int i = 0; i < w; i++) {
               index = i;
               for (int j = 0; j < h; j++) {
                  if (ac.acceptPixel(rgb[index])) {
                     //replace and continue down
                     rgb[index] = stickColor;
                  } else {
                     break;
                  }
                  index += w;
               }
            }
            break;
         case C.POS_1_BOT:
            for (int i = 0; i < w; i++) {
               index = w * h - 1 - i;
               for (int j = 0; j < h; j++) {
                  if (ac.acceptPixel(rgb[index])) {
                     rgb[index] = stickColor;
                  } else {
                     break;
                  }
                  index -= w;
               }
            }
            break;
         case C.POS_2_LEFT:
            for (int i = 0; i < h; i++) {
               index = i * w;
               for (int j = 0; j < w; j++) {
                  if (ac.acceptPixel(rgb[index])) {
                     rgb[index] = stickColor;
                  } else {
                     break;
                  }
                  index++;
               }
            }
            break;
         case C.POS_3_RIGHT:
            for (int i = 0; i < h; i++) {
               index = (i * w) + w - 1;
               for (int j = w - 1; j >= 0; j--) {
                  if (ac.acceptPixel(rgb[index])) {
                     rgb[index] = stickColor;
                  } else {
                     break;
                  }
                  index--;
               }
            }
            break;
         default:
            break;
      }
   }

   /**
    * 
    * @param rgb
    * @param w
    * @param h
    * @param maskColor
    * @param stickColor
    * @param dir
    */
   public static void eraseFromOuside(int[] rgb, int w, int h, int maskColor, int stickColor, int dir) {
      int index = 0;
      switch (dir) {
         case C.POS_0_TOP:
            for (int i = 0; i < w; i++) {
               index = i;
               for (int j = 0; j < h; j++) {
                  if (rgb[index] == maskColor) {
                     //replace and continue down
                     rgb[index] = stickColor;
                  } else {
                     break;
                  }
                  index += w;
               }
            }
            break;
         case C.POS_1_BOT:
            for (int i = 0; i < w; i++) {
               index = w * h - 1 - i;
               for (int j = 0; j < h; j++) {
                  if (rgb[index] == maskColor) {
                     rgb[index] = stickColor;
                  } else {
                     break;
                  }
                  index -= w;
               }
            }
            break;
         case C.POS_2_LEFT:
            for (int i = 0; i < h; i++) {
               index = i * w;
               for (int j = 0; j < w; j++) {
                  if (rgb[index] == maskColor) {
                     rgb[index] = stickColor;
                  } else {
                     break;
                  }
                  index++;
               }
            }
            break;
         case C.POS_3_RIGHT:
            for (int i = 0; i < h; i++) {
               index = (i * w) + w - 1;
               for (int j = w - 1; j >= 0; j--) {
                  if (rgb[index] == maskColor) {
                     rgb[index] = stickColor;
                  } else {
                     break;
                  }
                  index--;
               }
            }
            break;
         default:
            break;
      }
   }

   /**
    * Replace the first adjacent maskColor pixel to a non maskColor pixel by
    * stickColor
    * @param rgb
    * @param w
    * @param h
    * @param maskColor
    * @param stickColor
    * @param pos C.POS_X
    */
   public static void stickPixel(int[] rgb, int w, int h, int maskColor, int stickColor, int dir) {
      int index = 0;
      //offset to reach the previous pixel in the current line
      int mod = -1;
      boolean replace = true;
      switch (dir) {
         case C.POS_0_TOP:
            mod = -w;
            for (int i = 0; i < w; i++) {
               index = i;
               for (int j = 0; j < h; j++) {
                  if (rgb[index] != maskColor) {
                     if (j != 0) {
                        rgb[index + mod] = stickColor;
                     } else {
                        //if option, replace pixel with stickColor
                        if (replace)
                           rgb[index] = stickColor;
                     }
                     break;
                  }
                  index += w;
               }
            }
            break;
         case C.POS_1_BOT:
            mod = w;
            for (int i = 0; i < w; i++) {
               index = w * h - 1 - i;
               for (int j = 0; j < h; j++) {
                  if (rgb[index] != maskColor) {
                     if (j != 0) {
                        rgb[index + mod] = stickColor;
                     } else {
                        //if option, replace pixel with stickColor
                        if (replace)
                           rgb[index] = stickColor;
                     }
                     break;
                  }
                  index -= w;
               }
            }
            break;
         case C.POS_2_LEFT:
            mod = -1;
            for (int i = 0; i < h; i++) {
               index = i * w;
               for (int j = 0; j < w; j++) {
                  if (rgb[index] != maskColor) {
                     if (j != 0) {
                        rgb[index + mod] = stickColor;
                     } else {
                        //if option, replace pixel with stickColor
                        if (replace)
                           rgb[index] = stickColor;
                     }
                     break;
                  }
                  index++;
               }
            }
            break;
         case C.POS_3_RIGHT:
            mod = 1;
            for (int i = 0; i < h; i++) {
               index = (i * w) + w - 1;
               for (int j = w - 1; j >= 0; j--) {
                  if (rgb[index] != maskColor) {
                     if (j != w - 1) {
                        rgb[index + mod] = stickColor;
                     } else {
                        //if option, replace pixel with stickColor
                        if (replace)
                           rgb[index] = stickColor;
                     }
                     break;
                  }
                  index--;
               }
            }
            break;
         default:
            break;
      }
   }

   public static void stickPixel(int[] rgb, int w, int h, int maskColor, int stickColorTB, int stickColorLR, boolean startTB) {
      if (startTB) {
         stickPixel(rgb, w, h, maskColor, stickColorTB, C.POS_0_TOP);
         stickPixel(rgb, w, h, maskColor, stickColorTB, C.POS_1_BOT);
         stickPixel(rgb, w, h, maskColor, stickColorLR, C.POS_2_LEFT);
         stickPixel(rgb, w, h, maskColor, stickColorLR, C.POS_3_RIGHT);
      } else {
         stickPixel(rgb, w, h, maskColor, stickColorLR, C.POS_2_LEFT);
         stickPixel(rgb, w, h, maskColor, stickColorLR, C.POS_3_RIGHT);
         stickPixel(rgb, w, h, maskColor, stickColorTB, C.POS_0_TOP);
         stickPixel(rgb, w, h, maskColor, stickColorTB, C.POS_1_BOT);
      }
   }
}
