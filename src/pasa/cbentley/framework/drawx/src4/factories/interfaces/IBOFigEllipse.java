package pasa.cbentley.framework.drawx.src4.factories.interfaces;

public interface IBOFigEllipse extends IBOFigure {

   public static final int FIG_ELLIPSE_BASIC_SIZE             = IBOFigure.FIG__BASIC_SIZE + 10;

   /**
    * The border is drawn using the mask method instead of drawing ellipses of decreasing size
    */
   public static final int FIG_ELLIPSE_FLAG_1_BORDER_MASK1    = 1;

   public static final int FIG_ELLIPSE_FLAG_2_RNDCOLOR        = 1 << 1;

   /**
    * draw method instead of fill for Gradients.
    */
   public static final int FIG_ELLIPSE_FLAG_3_FIL_DE_FER      = 1 << 2;

   /**
    */
   public static final int FIG_ELLIPSE_FLAG_4_                = 1 << 3;

   public static final int FIG_ELLIPSE_FLAG_5_SLIP_FUNCTION   = 1 << 4;

   public static final int FIG_ELLIPSE_OFFSET_01_FLAG1        = IBOFigure.FIG__BASIC_SIZE;

   public static final int FIG_ELLIPSE_OFFSET_02_TYPE1        = IBOFigure.FIG__BASIC_SIZE + 1;

   /**
    * Size of the fill.
    * <br>
    * Transparent size inside
    */
   public static final int FIG_ELLIPSE_OFFSET_03_SIZE_FILL1   = IBOFigure.FIG__BASIC_SIZE + 2;

   /**
    * Increment value when drawing an Ellipse with Rayons or drawing with subfigures as pencil
    */
   public static final int FIG_ELLIPSE_OFFSET_04_INCR1        = IBOFigure.FIG__BASIC_SIZE + 3;

   /**
    * 
    */
   public static final int FIG_ELLIPSE_OFFSET_05_ANGLE_START2 = IBOFigure.FIG__BASIC_SIZE + 4;

   /**
    * end of angle in the fillArc method
    */
   public static final int FIG_ELLIPSE_OFFSET_06_ANGLE_END2   = IBOFigure.FIG__BASIC_SIZE + 6;

   /**
    * Angle slip 
    */
   public static final int FIG_ELLIPSE_OFFSET_07_ANGLE_SLIP2  = IBOFigure.FIG__BASIC_SIZE + 8;

}
