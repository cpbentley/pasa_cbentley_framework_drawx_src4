/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.utils;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOAnchor;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;

/**
 * 
 * @author Charles Bentley
 *
 */
public class AnchorUtils {

   /**
    * Align X using {@link ByteObject} 
    * <br>
    * <br>
    * @param anchor when null returns x
    * @param areaX
    * @param areaW
    * @param objectW 
    * @return
    */
   public static int getXAlign(ByteObject anchor, int areaX, int areaW, int objectW) {
      if (anchor == null)
         return areaX;
      int ha = anchor.getValue(IBOAnchor.ANCHOR_OFFSET_02_HORIZ_ALIGN1, 1);
      return getXAlign(ha, areaX, areaW, objectW);
   }

   /**
    * New X value
    * <br>
    * <br>
    * @param ha horizontal alignment of object
    * @param areaX x coordinate of area root
    * @param areaW width of area in which object is being drawn
    * @param objectW width in pixels for our object
    * @return x coordinate, in referential containing area
    */
   public static int getXAlign(int ha, int areaX, int areaW, int objectW) {
      int val = 0;
      if (ha == ITechAnchor.ALIGN_6_CENTER) {
         //center
         val = (areaW - objectW) / 2;
      } else if (ha == ITechAnchor.ALIGN_4_RIGHT) {
         val = (areaW - objectW);
      }
      return areaX + val;
   }

   /**
    * Same as other method but reads VA value for you
    * @param anchor
    * @param areaY
    * @param areaH
    * @param objectH
    * @return
    */
   public static int getYAlign(ByteObject anchor, int areaY, int areaH, int objectH) {
      if (anchor == null)
         return areaY;
      int va = anchor.getValue(IBOAnchor.ANCHOR_OFFSET_03_VERTICAL_ALIGN1, 1);
      return getYAlign(va, areaY, areaH, objectH);
   }

   /**
    * Align TOP, CENTER, or BOTTOM
    * @param va
    * @param areaY
    * @param areaH
    * @param objectH
    * @return
    */
   public static int getYAlign(int va, int areaY, int areaH, int objectH) {
      int val = 0;
      if (va == ITechAnchor.ALIGN_6_CENTER) {
         //center
         val = (areaH - objectH) / 2;
      } else if (va == ITechAnchor.ALIGN_2_BOTTOM) {
         val += areaH - objectH;
      }
      return areaY + val;
   }

}
