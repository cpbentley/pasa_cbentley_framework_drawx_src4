/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

public interface ITechFigPixelStar extends ITechFigure {
   
   public static final int PIXSTAR_BASIC_SIZE                = A_OBJECT_BASIC_SIZE + 6;

   public static final int PIXSTAR_FLAG_1_CENTER_OFF         = 1;

   /**
    * When set inter pixels are one.
    */
   public static final int PIXSTAR_FLAG_1_MIDDLE_ON          = 1;

   public static final int PIXSTAR_FLAGP_1_TOP_OFF           = 1;

   public static final int PIXSTAR_FLAGP_2_BOT_OFF           = 1 << 1;

   public static final int PIXSTAR_FLAGP_3_LEFT_OFF          = 1 << 2;

   public static final int PIXSTAR_FLAGP_4_RIGHT_OFF         = 1 << 3;

   public static final int PIXSTAR_OFFSET_01_FLAG1           = A_OBJECT_BASIC_SIZE;

   public static final int PIXSTAR_OFFSET_02_FLAGP1          = A_OBJECT_BASIC_SIZE + 1;

   public static final int PIXSTAR_OFFSET_03_TOP_SIZE1       = A_OBJECT_BASIC_SIZE + 2;

   public static final int PIXSTAR_OFFSET_04_BOT_SIZE1       = A_OBJECT_BASIC_SIZE + 3;

   public static final int PIXSTAR_OFFSET_05_LEFT_SIZE1      = A_OBJECT_BASIC_SIZE + 4;

   public static final int PIXSTAR_OFFSET_06_RIGHT_SIZE1     = A_OBJECT_BASIC_SIZE + 5;

   public static final int PIXSTAR_OFFSET_07_TOP_LEFT_SIZE1  = A_OBJECT_BASIC_SIZE + 6;

   public static final int PIXSTAR_OFFSET_08_TOP_RIGHT_SIZE1 = A_OBJECT_BASIC_SIZE + 7;

   public static final int PIXSTAR_OFFSET_09_BOT_LEFT_SIZE1  = A_OBJECT_BASIC_SIZE + 8;

   public static final int PIXSTAR_OFFSET_10_BOT_RIGHT_SIZE1 = A_OBJECT_BASIC_SIZE + 9;
}
