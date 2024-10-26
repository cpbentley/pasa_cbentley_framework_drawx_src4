/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.core.src4.ctx.ICtx;
import pasa.cbentley.core.src4.ctx.IToStringFlagsUC;
import pasa.cbentley.core.src4.interfaces.IToStringFlags;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

/**
 * Flags that modifies how ToString methods include some information for {@link DrwCtx} classes.
 * 
 * @author Charles Bentley
 *
 */
public interface IToStringFlagsDrw extends IToStringFlags {

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

   public static final int D_FLAG_28_IGNORE_FONT      = 1 << 27;

   public static final int D_FLAG_27_CACHE            = 1 << 26;

   /**
    * {@link Dctx#setFlagToString(ICtx, int, boolean)}
    */
   public static final int DATA_FLAG_20_HIDE_CACHE    = 19 << 1;

   /**
    * Set it to prevent {@link GraphicsX} debug data in {@link RgbImage}
    */
   public static final int DATA_FLAG_22_HIDE_GRAPHICS = 21 << 1;

}
