/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.framework.drawx.src4.tech.ITechSkew;

public interface IBOPassSkewer extends IBOPass {

   public static final int SKEWER_BASE_OFFSET                   = IBOPass.PASS_BASIC_SIZE;

   public static final int SKEWER_BASIC_SIZE                    = IBOPass.PASS_BASIC_SIZE + 19;

   public static final int SKEWER_FLAG_1_HIDE_CENTER            = 1;

   public static final int SKEWER_FLAG_2_HIDE_TOP               = 1 << 1;

   public static final int SKEWER_FLAG_3_HIDE_BOT               = 1 << 2;

   public static final int SKEWER_FLAG_4_HIDE_LEFT              = 1 << 3;

   public static final int SKEWER_FLAG_5_HIDE_RIGHT             = 1 << 4;

   public static final int SKEWER_FLAG_6_NO_TRANSFORMS          = 1 << 5;

   public static final int SKEWER_FLAG_7_SYMMETRIC              = 1 << 6;

   /**
    * Should the extra w and h fit the figure. Default is crop.
    */
   public static final int SKEWER_FLAG_8_EXTRA_FIT              = 1 << 7;

   public static final int SKEWER_OFFSET_01_FLAG1               = SKEWER_BASE_OFFSET;

   /**
    * <li> {@link ITechSkew#SKEW_EDGE_0_ZERO}
    * <li> {@link ITechSkew#SKEW_EDGE_1_CLAMP}
    * 
    */
   public static final int SKEWER_OFFSET_02_EDGE_TYPE1          = SKEWER_BASE_OFFSET + 1;

   /**
    * <li> {@link ITechSkew#SKEW_TYPE_0_NEAREST_NEIGHBOUR}
    * <li> {@link ITechSkew#SKEW_TYPE_1_BILINEAR}
    */
   public static final int SKEWER_OFFSET_03_INTERPOLATION_TYPE1 = SKEWER_BASE_OFFSET + 2;

   /**
    * 
    */
   public static final int SKEWER_OFFSET_04_EXTRA_X2            = SKEWER_BASE_OFFSET + 3;

   /**
    * 
    */
   public static final int SKEWER_OFFSET_05_EXTRA_Y2            = SKEWER_BASE_OFFSET + 5;

   /**
    * 
    */
   public static final int SKEWER_OFFSET_06_FUZZY_BORDERSIZE2   = SKEWER_BASE_OFFSET + 7;

   public static final int SKEWER_OFFSET_07_CENTER_TRANS1       = SKEWER_BASE_OFFSET + 9;

   public static final int SKEWER_OFFSET_08_COLOR_MOD4          = SKEWER_BASE_OFFSET + 10;

   /**
    * Blend operation for the 4 skewed TBLR parts.
    * <br>
    * When not zero, this forces
    */
   public static final int SKEWER_OFFSET_09_BLENDER1            = SKEWER_BASE_OFFSET + 14;

   /** 
    * first byte are for the draw first (0,1,2,3)
    * second byte are for the next
    * last byte is for the last
    * <br>
    * <li>0 = TBLR order
    * <li>1 = TRBL
    * <li>2 = TLBR
    * <li>3 = LRTB
    * <br>
    * 
    */
   public static final int SKEWER_OFFSET_10_DRAW_ORDER3         = SKEWER_BASE_OFFSET + 15;

   public static final int SKEWER_OFFSET_11_EDGE_ALPHA1         = SKEWER_BASE_OFFSET + 18;

}
