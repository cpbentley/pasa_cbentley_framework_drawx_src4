package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;

public interface IBOFigPixels extends IBOFigure {

   /**
    * 1 byte for flag
    * 2 bytes for len
    * 4 bytes for seed
    * 3 bytes for colors header
    */
   public static final int FIG_PIXEL_BASIC_SIZE             = IBOFigure.FIG__BASIC_SIZE + 10;

   public static final int FIG_PIXEL_FLAG_1_RANDOM_SIZE     = 1;

   public static final int FIG_PIXEL_FLAG_2_RANDOM_COLOR    = 2;

   /**
    * Instead of reading {@link GraphicsX#getBufferRegion(int, int, int, int)}
    */
   public static final int FIG_PIXEL_FLAG_3_NEW_IMAGE       = 4;

   public static final int FIG_PIXEL_OFFSET_01_FLAG         = IBOFigure.FIG__BASIC_SIZE;

   /**
    * Seed for computing pixels
    */
   public static final int FIG_PIXEL_OFFSET_03_SEED4        = IBOFigure.FIG__BASIC_SIZE + 3;

   public static final int FIG_PIXEL_OFFSET_04_COLOR_EXTRA4 = IBOFigure.FIG__BASIC_SIZE + 7;

   public static final int FIG_PIXEL_OFFSET_04_COLORSX      = IBOFigure.FIG__BASIC_SIZE + 7;

   /**
    * 
    */
   public static final int FIG_PIXEL_OFFSET_05_BLENDERX1    = IBOFigure.FIG__BASIC_SIZE + 7;

   public static final int FIG_PIXEL_OFFSET_07_LENGTH_H2    = IBOFigure.FIG__BASIC_SIZE + 8;

   public static final int FIG_PIXEL_OFFSET_08_LENGTH_V2    = IBOFigure.FIG__BASIC_SIZE + 10;

   public static final int FIG_PIXEL_OFFSET_09_GRAD_SIZE1   = IBOFigure.FIG__BASIC_SIZE + 12;

   public static final int FIG_PIXEL_OFFSET_3VLENGTH2       = 0;

}
