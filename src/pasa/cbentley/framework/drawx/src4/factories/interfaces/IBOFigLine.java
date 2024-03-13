package pasa.cbentley.framework.drawx.src4.factories.interfaces;

public interface IBOFigLine extends IBOFigure {

   public static final int FIG_LINE_BASIC_SIZE        = FIG__BASIC_SIZE + 2;

   public static final int FIG_LINE_COLORED_SIZE      = 0;

   /**
    * Draw Extremity
    */
   public static final int FIG_LINE_FLAG_EX           = 64;

   /**
    * Apply Stick after drawing extremity
    */
   public static final int FIG_LINE_FLAG_EX_STICK     = 128;

   public static final int FIG_LINE_FLAG_HORIZ        = 0;

   public static final int FIG_LINE_FLAGX_STICK_BOT   = 0;

   public static final int FIG_LINE_FLAGX_STICK_LEFT  = 0;

   public static final int FIG_LINE_FLAGX_STICK_RIGHT = 0;

   public static final int FIG_LINE_FLAGX_STICK_TOP   = 0;

   public static final int FIG_LINE_OFFSET_1FLAG      = FIG__BASIC_SIZE;

   public static final int FIG_LINE_OFFSET_2SIZE1     = FIG__BASIC_SIZE + 1;

   public static final int FIG_LINE_OFFSET_EX_COLOR   = 0;

   public static final int FIG_LINE_OFFSET_EX_SIZE    = 0;

}
