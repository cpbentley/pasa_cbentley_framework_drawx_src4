/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories.interfaces;

public interface IBOMosaic extends IBOPass {

   public static final int PMOSAIC_BASE_OFFSET               = IBOPass.PASS_BASIC_SIZE;

   public static final int PMOSAIC_BASIC_SIZE                = IBOPass.PASS_BASIC_SIZE + 11;

   /**
    * When set, the default trans scheme is used.
    * Only valid for certain schemes.
    * <br>
    * Always a flip
    */
   public static final int PMOSAIC_FLAG_1_TRANSFORMATION     = 1;

   /**
    * Use different sources
    */
   public static final int PMOSAIC_FLAG_2_DIFF_SOURCES       = 2;

   public static final int PMOSAIC_OFFSET_01_FLAG1           = PMOSAIC_BASE_OFFSET;

   /**
    * 4 or 9 squares
    */
   public static final int PMOSAIC_OFFSET_02_TYPE1           = PMOSAIC_BASE_OFFSET + 1;

   /**
    * Root position of original image. 
    * <li>0-1-2-3 for a 4 squares
    * <li>0-9
    */
   public static final int PMOSAIC_OFFSET_03_ROOT1           = PMOSAIC_BASE_OFFSET + 2;

   /**
    * Overlay : number of pixels. Negavtive of positive
    */
   public static final int PMOSAIC_OFFSET_04_OVERLAY_W2      = PMOSAIC_BASE_OFFSET + 3;

   public static final int PMOSAIC_OFFSET_05_OVERLAY_H2      = PMOSAIC_BASE_OFFSET + 5;

   /**
    * When there is an overlay, specific blender
    */
   public static final int PMOSAIC_OFFSET_06_BLEND_OVERLAY1  = PMOSAIC_BASE_OFFSET + 7;

   /**
    * The number of mosaic horizontally
    */
   public static final int PMOSAIC_OFFSET_07_NUM_W1          = PMOSAIC_BASE_OFFSET + 8;

   public static final int PMOSAIC_OFFSET_08_NUM_H1          = PMOSAIC_BASE_OFFSET + 9;

   /**
    * Transformation to be used
    */
   public static final int PMOSAIC_OFFSET_09_TRANSFORMANION1 = PMOSAIC_BASE_OFFSET + 10;

   public static final int PMOSAIC_TYPE_0_GENERIC_WH         = 0;

   /**
    * 
    */
   public static final int PMOSAIC_TYPE_1_SQUARE4            = 1;

   public static final int PMOSAIC_TYPE_2_SQUARE9            = 2;

}
