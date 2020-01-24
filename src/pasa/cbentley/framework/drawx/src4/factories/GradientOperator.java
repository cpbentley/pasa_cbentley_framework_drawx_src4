package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;

public class GradientOperator  extends AbstractDrwOperator implements ITechFigure {

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
