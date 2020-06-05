/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.core.src4.ctx.IFlagsToString;

public interface IFlagsToStringDrw extends IFlagsToString {

   /**
    * Shows all the style data
    */
   public static final int D_FLAG_01_STYLE            = 1 << 0;

   public static final int D_FLAG_03_STRINGER         = 1 << 2;

   /**
    * When flag is set, ignore GraphicsX data
    */
   public static final int D_FLAG_26_GRAPHCISX        = 1 << 25;

   public static final int D_FLAG_25_IGNORE_IGRAPHICS = 1 << 24;

   public static final int D_FLAG_27_CACHE            = 1 << 26;

}
