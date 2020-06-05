/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.interfaces;

import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

public interface IDLogDraw {

   public static final int DATA_FLAG_21_MANY_COLORS = 20 << 1;

   public static final int DATA_FLAG_20_HIDE_CACHE       = 19 << 1;

   /**
    * Set it to prevent {@link GraphicsX} debug data in {@link RgbImage}
    */
   public static final int DATA_FLAG_22_HIDE_GRAPHICS    = 21 << 1;
}
