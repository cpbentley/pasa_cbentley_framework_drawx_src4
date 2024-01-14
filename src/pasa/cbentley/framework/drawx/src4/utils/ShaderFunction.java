/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.utils;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.byteobjects.src4.objects.color.ColorFunction;
import pasa.cbentley.byteobjects.src4.objects.function.Acceptor;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigPixelStar;

public class ShaderFunction extends ColorFunction {

   /**
    * For each pixel, apply a function. The function reads pixels and modifies them
    * based on a condition.
    * <br>
    * @param source
    * @param offset
    * @param scanlength
    * @param m
    * @param n
    * @param w
    * @param h
    * @param fct
    * @param p
    */
   public static void filterRGB(int[] source, int offset, int scanlength, int m, int n, int w, int h, ShaderFunction fct, ByteObject p) {
      int alphaCount = 0;
      int wM1 = w - 1;
      int hM1 = h - 1;
      //
      //put the final result here which will copied afterwards
      int[] res = new int[w * h];
      //counts the number of times, the pixel has been processed
      int[] processed = new int[w * h];
      int resCount = 0;
      int index = offset + m + (scanlength * n);
      ByteObject pixStar = p.getSubFirst(IBOTypesDrw.TYPE_063_PIX_STAR);
      int lenT = pixStar.get1(ITechFigPixelStar.PIXSTAR_OFFSET_03_TOP_SIZE1);
      int lenB = pixStar.get1(ITechFigPixelStar.PIXSTAR_OFFSET_04_BOT_SIZE1);
      int lenL = pixStar.get1(ITechFigPixelStar.PIXSTAR_OFFSET_05_LEFT_SIZE1);
      int lenR = pixStar.get1(ITechFigPixelStar.PIXSTAR_OFFSET_06_RIGHT_SIZE1);

      int[] starData = new int[(lenT + lenB + 1) * (lenL + lenR + 1)];
      //topleft pixel why starting with this one?
      int rgb = source[index];
      //check if we accept the index.
      Acceptor acc = fct.getAcceptor(1);
      //introduce a weight based on the distance
      if (fct.accept(rgb)) {
         //read the pixel shape around the pixel
         int starIndex = 0;
         for (int i = 0; i < lenB + 1; i++) {
            starIndex = offset + m + ((n + i) * scanlength) + 1;
            for (int j = 0; j < lenR + 1; j++) {
               //for all star values. do the star function. (e.g. counting the alpha
               int distancePix = j;
               fct.fxStar(rgb, distancePix, source[starIndex]); //increment counter if alpha or given color

               int proc = processed[resCount];
               if (acc.accept(proc)) {
                  starData[i] = source[starIndex];
                  res[resCount] = fct.fxCombine(rgb, source[starIndex], res[resCount]);
               }
               starIndex++;
               resCount++;
            }

         }
         if (!pixStar.hasFlag(ITechFigPixelStar.PIXSTAR_OFFSET_01_FLAG1, ITechFigPixelStar.PIXSTAR_FLAG_1_CENTER_OFF)) {
            res[resCount] = fct.fxStar(rgb, starData);
         }
      }
      index++;
      //first line
      for (int i = 1; i < wM1; i++) {
         rgb = source[index];
         index++;
      }

      //left vertical line
      // index = offset + w;
      index = offset + m + (n * scanlength) + scanlength;
      for (int i = 1; i < hM1; i++) {
         index += scanlength;
      }

      //right vertical line
      index = offset + m + (n * scanlength) + scanlength + w - 1;
      for (int i = 1; i < hM1; i++) {
         rgb = source[index];
         index += scanlength;
      }

      //top right pixel
      //index = offset + w - 1;
      index = offset + m + w - 1 + (n * scanlength);
      rgb = source[index];

      //mainland
      //index = offset + w + 1;
      //only TBLR
      for (int i = 1; i < hM1; i++) {
         index = offset + m + ((n + i) * scanlength) + 1;
         for (int j = 1; j < wM1; j++) {
            rgb = source[index];
            //SystemLog.printDraw("rgb=" + DrawUtilz.debugColor(rgb) + " touchColor=" + DrawUtilz.debugColor(touchColor));
            index++;
         }
      }

      //bottom left pixel
      index = offset + m + ((n + h - 1) * scanlength);
      rgb = source[index];

      //bottom right pixel = last pixel
      //index = offset + (h * w) - 1;
      index = offset + m + w - 1 + ((n + h - 1) * scanlength);
      rgb = source[index];

      //bottom line last line
      //index = offset + ((h - 1) * w) + 1;
      index = offset + m + 1 + ((n + h - 1) * scanlength);
      for (int i = 1; i < w - 1; i++) {
         rgb = source[index];
         index++;
      }
   }

   protected Acceptor[] accs;

   public ShaderFunction(DrwCtx drc, ByteObject def) {
      super(drc.getBOC(), def);
   }

   public int fxCombine(int original, int starOriginal, int starResult) {

      return 0;
   }

   private void fxStar(int rgb, int distancePix, int i) {
      // TODO Auto-generated method stub

   }

   private int fxStar(int rgb, int[] starData) {
      // TODO Auto-generated method stub
      return 0;
   }

   public Acceptor getAcceptor(int index) {
      return accs[index];
   }

}
