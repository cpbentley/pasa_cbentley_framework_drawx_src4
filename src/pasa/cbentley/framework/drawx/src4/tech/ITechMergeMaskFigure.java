/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;

public interface ITechMergeMaskFigure extends ITechByteObject {
   /**
    * Sizer of a merge mask for a string figure
    */
   public static final int MM_FIG_SIZE                   = 2;

   public static final int MM_INDEX_VALUES5_FLAG_2_COLOR = 1;

   public static final int MM_VALUES5_FLAG_2_COLOR       = 1 << 1;

}
