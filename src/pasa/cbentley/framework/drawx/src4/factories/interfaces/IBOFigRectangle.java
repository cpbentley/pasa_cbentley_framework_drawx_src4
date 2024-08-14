package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.layouter.src4.tech.IBOSizer;

/**
 * 
 * @author Charles Bentley
 *
 */
public interface IBOFigRectangle extends IBOFigure {

   /**
    * uses all default Figure fields
    * 1 byte for flag
    * 1 byte for arcw
    * 1 byte for arch
    * 1 byte for fillSize
    */
   public static final int FIG_RECTANGLE_BASIC_SIZE          = FIG__BASIC_SIZE + 5;

   /**
    * 
    */
   public static final int FIG_RECTANGLE_FLAG_1_ROUND        = 1 << 0;

   /**
    * Master flag that enables arcw and arch computations.
    * 
    * <p>
    * One embedded, values will be hardcoded to a low value, therefore using 1 byte.
    * For better system, they will use a Sizer. no middle ground with codedSize on 4 bytes
    * </p>
    * 
    * <p>
    * Most of the time, it will be a function of the width and height of the rectangle.
    * </p>
    */
   public static final int FIG_RECTANGLE_FLAG_2_ROUND_INSIDE = 1 << 1;

   /**
    * 
    */
   public static final int FIG_RECTANGLE_FLAG_3_             = 1 << 2;

   /**
    * 
    */
   public static final int FIG_RECTANGLE_FLAG_4_             = 1 << 3;

   /**
    * {@link IBOFigRectangle#FIG_RECTANGLE_OFFSET_2_SIZE_ARCW1} is a pointer towards an {@link IBOSizer}
    */
   public static final int FIG_RECTANGLE_FLAG_5_ARCW_SIZER   = 1 << 4;

   /**
    * {@link IBOFigRectangle#FIG_RECTANGLE_OFFSET_3_SIZE_ARCH1} is a pointer towards an {@link IBOSizer}
    */
   public static final int FIG_RECTANGLE_FLAG_6_ARCH_SIZER   = 1 << 5;

   /**
    * {@link IBOFigRectangle#FIG_RECTANGLE_OFFSET_4_SIZE_FILL1} is a pointer towards an {@link IBOSizer}
    */
   public static final int FIG_RECTANGLE_FLAG_7_FILL_SIZER   = 1 << 6;

   /**
    * {@link IBOFigRectangle#FIG_RECTANGLE_OFFSET_5_SIZE_GRAD1} is a pointer towards an {@link IBOSizer}
    */
   public static final int FIG_RECTANGLE_FLAG_8_GRAD_SIZER   = 1 << 7;

   public static final int FIG_RECTANGLE_OFFSET_1_FLAG       = FIG__BASIC_SIZE;

   /**
    * Contains the arc width of the rectangle.
    * 
    * <p>
    * Controlled by {@link IBOFigRectangle#FIG_RECTANGLE_FLAG_5_ARCW_SIZER} for sizer definition
    * </p>
    */
   public static final int FIG_RECTANGLE_OFFSET_2_SIZE_ARCW1 = FIG__BASIC_SIZE + 1;

   /**
    * Contains the arc height of the rectangle.
    * 
    * <p>
    * Controlled by {@link IBOFigRectangle#FIG_RECTANGLE_FLAG_6_ARCH_SIZER} for sizer definition
    * </p>
    */
   public static final int FIG_RECTANGLE_OFFSET_3_SIZE_ARCH1 = FIG__BASIC_SIZE + 2;

   /**
    * When different from 0, draws "Border" rectangle.
    * 
    * <p>
    * Controlled by {@link IBOFigRectangle#FIG_RECTANGLE_FLAG_7_FILL_SIZER} for sizer definition
    * </p>
    */
   public static final int FIG_RECTANGLE_OFFSET_4_SIZE_FILL1 = FIG__BASIC_SIZE + 3;

   /**
    * When different than 0, overrides the Gradient size
    * 
    * <p>
    * Controlled by {@link IBOFigRectangle#FIG_RECTANGLE_FLAG_8_GRAD_SIZER} for sizer definition
    * </p>
    */
   public static final int FIG_RECTANGLE_OFFSET_5_SIZE_GRAD1 = FIG__BASIC_SIZE + 4;

}
