package pasa.cbentley.framework.drawx.src4.utils;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.framework.drawx.src4.tech.ITechBox;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

public class AnchorUtils {

   /**
    * Align X using {@link ByteObject} 
    * <br>
    * <br>
    * @param anchor when null returns x
    * @param x
    * @param w
    * @param ow 
    * @return
    */
   public static int getXAlign(ByteObject anchor, int x, int w, int ow) {
      if (anchor == null)
         return x;
      int ha = anchor.getValue(ITechBox.BOX_OFFSET_02_HORIZ_ALIGN4, 1);
      return getXAlign(ha, x, w, ow);
   }

   /**
    * New X value
    * <br>
    * <br>
    * @param ha horizontal alignment of object
    * @param x x coordinate of area root
    * @param totalwidth width of area in which object is being drawn
    * @param objectwidth width in pixels for our object
    * @return x coordinate, in referential containing area
    */
   public static int getXAlign(int ha, int x, int totalwidth, int objectwidth) {
      int val = 0;
      if (ha == ITechFigure.ALIGN_CENTER) {
         //center
         val = (totalwidth - objectwidth) / 2;
      } else if (ha == ITechFigure.ALIGN_RIGHT) {
         val = (totalwidth - objectwidth);
      }
      return x + val;
   }

   /**
    * Same as other method but reads VA value for you
    * @param anchor
    * @param y
    * @param h
    * @param oh
    * @return
    */
   public static int getYAlign(ByteObject anchor, int y, int h, int oh) {
      if (anchor == null)
         return y;
      int va = anchor.getValue(ITechBox.BOX_OFFSET_03_VERTICAL_ALIGN4, 1);
      return getYAlign(va, y, h, oh);
   }

   /**
    * Align TOP, CENTER, or BOTTOM
    * @param va
    * @param y
    * @param totalheight
    * @param objectheight
    * @return
    */
   public static int getYAlign(int va, int y, int totalheight, int objectheight) {
      int val = 0;
      if (va == ITechFigure.ALIGN_CENTER) {
         //center
         val = (totalheight - objectheight) / 2;
      } else if (va == ITechFigure.ALIGN_BOTTOM) {
         val += totalheight - objectheight;
      }
      return y + val;
   }

}
