package pasa.cbentley.framework.drawx.src4.factories.interfaces;

public interface IBOFigRectangle extends IBOFigure {

   /**
    * uses all default Figure fields
    * 1 byte for flag
    * 1 byte for arcw
    * 1 byte for arch
    * 1 byte for fillSize
    */
   public static final int FIG_RECTANGLE_BASIC_SIZE          = FIG__BASIC_SIZE + 5;

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
   public static final int FIG_RECTANGLE_FLAG_5_ARCW1        = 1 << 4;

   public static final int FIG_RECTANGLE_FLAG_6_ARCH1        = 1 << 5;

   public static final int FIG_RECTANGLE_FLAG_7_ARCW1        = 1 << 6;

   public static final int FIG_RECTANGLE_FLAG_8_ARCH1        = 1 << 7;

   public static final int FIG_RECTANGLE_OFFSET_1_FLAG       = FIG__BASIC_SIZE;

   /**
    * Contains the arc width of the rectangle.
    * 
    */
   public static final int FIG_RECTANGLE_OFFSET_2_ARCW1      = FIG__BASIC_SIZE + 1;

   /**
    */
   public static final int FIG_RECTANGLE_OFFSET_3_ARCH1      = FIG__BASIC_SIZE + 2;

   /**
    * When different from 0, draws "Border" rectangle.
    */
   public static final int FIG_RECTANGLE_OFFSET_4_SIZE_FILL1    = FIG__BASIC_SIZE + 3;

   /**
    * Another size influencer for smaller gradients
    */
   public static final int FIG_RECTANGLE_OFFSET_5_SIZE_G1    = FIG__BASIC_SIZE + 4;

}
