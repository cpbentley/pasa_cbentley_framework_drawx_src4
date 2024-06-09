/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;

/**
 * {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_0_FX}
 * 
 * @author Charles-Philip Bentley
 *
 */
public interface IBOStrAuxFx extends IBOStrAux {

   /**
    * 1 byte for flag
    */
   public static final int STR_AUX_FX_BASIC_SIZE              = STR_AUX_SIZE + 1;

   /**
    */
   public static final int STR_AUX_FX_FLAG_1_SPECIFIC_SWITCH  = 1;

   /**
    * 
    */
   public static final int STR_AUX_FX_FLAG_2_ROOT             = 1 << 1;

   /**
    * 
    */
   public static final int STR_AUX_FX_FLAG_3_                 = 1 << 2;

   /**
    * 
    */
   public static final int STR_AUX_FX_FLAG_4_EXTRA_SPACE_TBLR = 1 << 3;

   public static final int STR_AUX_FX_FLAG_5_                 = 1 << 4;

   public static final int STR_AUX_FX_FLAG_6_                 = 1 << 5;

   public static final int STR_AUX_FX_FLAG_7_                 = 1 << 6;

   public static final int STR_AUX_FX_FLAG_8_                 = 1 << 7;

   public static final int STR_AUX_OFFSET_1_FLAG              = STR_AUX_SIZE;
}
