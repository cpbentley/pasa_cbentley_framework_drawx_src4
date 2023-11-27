/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.framework.drawx.src4.factories.ScaleOperator;

public interface ITechScaler extends ITechPass {

   /**
    * algo for scaling is linear
    */
   public static final int SCALER_ID_0_LINEAR        = 0;

   /**
    * algo for scaling is bi linear
    * {@link ScaleOperator#getScaledBiLinear(pasa.cbentley.framework.drawx.src4.engine.RgbImage, int, int)}
    */
   public static final int SCALER_ID_1_BI_LINEAR     = 1;

   /**
    * algo for scaling is bi cubic
    * {@link ScaleOperator#getScaledBiCubic(pasa.cbentley.framework.drawx.src4.engine.RgbImage, int, int)}
    */
   public static final int SCALER_ID_2_BI_CUBIC      = 2;

   public static final int SCALER_ID_MAX_CK          = 2;

   /**
    * Scaler does nothing
    */
   public static final int SCALER_TYPE_0_FIT_NONE    = 0;

   /**
    * Scales to fit the width and the height.
    * <br>
    * Proportions are lost.
    */
   public static final int SCALER_TYPE_1_FIT_BOTH    = 1;

   /**
    * Scales the figure to fit the width, the height
    */
   public static final int SCALER_TYPE_2_FIT_W       = 2;

   public static final int SCALER_TYPE_3_FIT_H       = 3;

   /**
    * Grows the image proportionally. Math.min(W,H)
    */
   public static final int SCALER_TYPE_4_FIT_FIRST   = 4;

   /**
    * 
    */
   public static final int SCALER_TYPE_5_FIT_LAST    = 5;

   public static final int SCALER_TYPE_MAX_CK        = 5;

}
