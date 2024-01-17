package pasa.cbentley.framework.drawx.src4.factories.interfaces;

public interface IBOFigSuperLines extends IBOFigure {

   /**
    * 1 byte flag
    * 2 bytes repeat
    * 2 bytes  separation
    * 1 byte line thickness
    * 
    */
   public static final int FIG_SL_BASIC_SIZE            = FIG__BASIC_SIZE + 6;

   public static final int FIG_SL_FLAG_1SIMPLE          = 1;

   public static final int FIG_SL_FLAG_2ANGLE           = 2;

   public static final int FIG_SL_FLAG_3HORIZ           = 4;

   /**
    * Tells we have explicit seperation values
    */
   public static final int FIG_SL_FLAG_4EXPLICIT_SEP    = 8;

   public static final int FIG_SL_FLAG_5EXPLICIT_COLORS = 16;

   public static final int FIG_SL_FLAG_6FILL            = 32;

   public static final int FIG_SL_FLAG_7IGNORE_FIRST    = 64;

   public static final int FIG_SL_FLAG_8IGNORE_LAST     = 128;

   public static final int FIG_SL_OFFSET_1FLAG          = FIG__BASIC_SIZE;

   public static final int FIG_SL_OFFSET_2LINE_SIZE1    = FIG__BASIC_SIZE + 1;

   /**
    * Maximum number of line repeat. Ignored if ask to do a fill.
    */
   public static final int FIG_SL_OFFSET_3REPEAT2       = FIG__BASIC_SIZE + 2;

   /**
    * Pixels separating 2 lines
    */
   public static final int FIG_SL_OFFSET_4SEPARATION2   = FIG__BASIC_SIZE + 4;

}
