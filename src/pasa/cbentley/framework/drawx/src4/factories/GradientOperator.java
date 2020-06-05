/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.extra.MergeMaskFactory;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;

public class GradientOperator extends AbstractDrwOperator implements ITechFigure, ITechGradient {

   public GradientOperator(DrwCtx drc) {
      super(drc);
   }

   public static void setGradientFct(ByteObject grad, ByteObject fct) {
      grad.setFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_2_EXTERNAL_FUNCTION, true);
      grad.addByteObject(fct);
   }

   public static void addArtifact(ByteObject grad, ByteObject artifac) {
      if (grad == null || artifac == null)
         return;
      grad.addSub(artifac);
      grad.setFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_7_ARTIFACTS, true);
   }

   public void setGradientOffset(ByteObject gradient, int offset) {
      gradient.set2(GRADIENT_OFFSET_08_OFFSET2, 2);
      gradient.setFlag(GRADIENT_OFFSET_09_FLAGX1, GRADIENT_FLAGX_6_OFFSET, true);
   }
   /**
    * Merge 2 gradients when a figure with a gradient is merged with another figure with a gradient.
    * <br>
    * <br>
    * 
    * @param root != null
    * @param merge != null
    * @return
    */
   public ByteObject mergeGradient(ByteObject root, ByteObject merge) {
      MergeMaskFactory mmf = boc.getMergeMaskFactory();
      int scolor = root.get4(ITechGradient.GRADIENT_OFFSET_04_COLOR4);
      int sec = root.get1(ITechGradient.GRADIENT_OFFSET_05_SEC1);
      int type = root.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      ByteObject tcolor = root.getSubFirst(IBOTypesBOC.TYPE_002_LIT_INT);
      //get merge mask from incomplete gradient
      ByteObject mergeMask = merge.getSubFirst(IBOTypesBOC.TYPE_011_MERGE_MASK);
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_4)) {
         scolor = merge.get4(ITechGradient.GRADIENT_OFFSET_04_COLOR4);
      }
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_5)) {
         sec = merge.get1(ITechGradient.GRADIENT_OFFSET_05_SEC1);
      }
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_6)) {
         type = merge.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      }
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_1FLAG1, ITechGradient.GRADIENT_FLAG_3_THIRD_COLOR)) {
         tcolor = merge.getSubFirst(IBOTypesBOC.TYPE_002_LIT_INT);
      }

      int mainFlag = mmf.mergeFlag(root, merge, mergeMask, ITechGradient.GRADIENT_OFFSET_01_FLAG, MERGE_MASK_OFFSET_1FLAG1);
      int exludeFlags = mmf.mergeFlag(root, merge, mergeMask, ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, MERGE_MASK_OFFSET_2FLAG1);
      int channelFlags = mmf.mergeFlag(root, merge, mergeMask, ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, MERGE_MASK_OFFSET_3FLAG1);

      ByteObject newGrad = drc.getGradientFactory().getGradient(scolor, sec, type, mainFlag, exludeFlags, channelFlags, tcolor);
      return newGrad;
   }

   public static int getRectGradSize(int width, int height, int arcw, int arch, int type) {
      int size = 0; //number of pixel steps
      switch (type) {
         case ITechGradient.GRADIENT_TYPE_RECT_00_SQUARE:
            size = Math.min(height, width) / 2;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_02_VERT:
            size = height;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_01_HORIZ:
            size = width;
            size -= arch;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_07_L_TOP:
            size = width / 2;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_08_L_BOT:
            size = width / 2;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_09_L_LEFT:
            size = height / 2;
            size -= arcw;
            break;
         case ITechGradient.GRADIENT_TYPE_RECT_10_L_RIGHT:
            size = height / 2;
            size -= arcw;
            break;
         default:
            if (arcw == 0 && arch == 0) {
               size = Math.min(height, width);
            } else {
               size = Math.min(height, width) / 2;
            }
            break;
      }
      return size;
   }

   public static int getEllipseGradSize(int w, int h, ByteObject grad) {
      final int type = grad.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      switch (type) {
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_00_NORMAL:
            return Math.min(h, w) / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_01_HORIZ:
            return h / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_02_VERT:
            return w / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_03_TOP_FLAMME:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_04_BOT_FLAMME:
            return w / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_05_LEFT_FLAMME:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_06_RIGHT_FLAMME:
            return h / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_11_WATER_DROP_TOP:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_12_WATER_DROP_BOT:
            return w / 2;
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_13_WATER_DROP_LEFT:
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_14_WATER_DROP_RIGHT:
            return h / 2;
      }
      return Math.min(h, w);
   }

   public static int getGradientColor(int primaryColor, int secondaryColor, int step, int end, double maxSecondary) {
      // Break the primary color into red, green, and blue.
      int pr = (primaryColor & 0x00FF0000) >> 16;
      int pg = (primaryColor & 0x0000FF00) >> 8;
      int pb = (primaryColor & 0x000000FF);

      // Break the secondary color into red, green, and blue.
      int sr = (secondaryColor & 0x00FF0000) >> 16;
      int sg = (secondaryColor & 0x0000FF00) >> 8;
      int sb = (secondaryColor & 0x000000FF);
      double p = (double) step / (double) end;
      double v = Math.abs(maxSecondary - p);
      double v2 = 1.0 - v;

      int red = (int) (pr * v + sr * v2);
      int green = (int) (pg * v + sg * v2);
      int blue = (int) (pb * v + sb * v2);
      return ColorUtils.getRGBInt(red, green, blue);

   }
}
