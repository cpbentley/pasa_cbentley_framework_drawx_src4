package pasa.cbentley.framework.drawx.src4.string;

public interface IBOFxStrLine extends IBOFxStr {

   public static final int FXLINE_BASIC_SIZE               = FX_BASIC_SIZE + 4;

   /**
    * Is there a figure to draw between chars
    * <br>
    * <br>
    * When this flag is set the last figure in sub parameters is that figure.
    * <br>
    * <br>
    * POssibly the first one if there are 2, is the bg figure.
    */
   public static final int FXLINE_FLAG_1_INTERCHAR_FIG     = 1;

   /**
    * Simple color gradient over the characters in a line
    * <br>
    * <br>
    * When this flag is set, a {@link IDrwTypes#TYPE_059_GRADIENT} is in the sub parameters.
    * 
    */
   public static final int FXLINE_FLAG_2_GRADIENT          = 1 << 1;

   /**
    * Is there a XF.
    * <br>
    * <br>
    * A Function is defined to 
    */
   public static final int FXLINE_FLAG_5_DEFINED_XF        = 1 << 4;

   /**
    * Is a Y shift defined?
    */
   public static final int FXLINE_FLAG_6_DEFINED_YF        = 1 << 5;

   /**
    * A function decides how to position characters on a line.
    * <br>
    * <br>
    * By default, input is character width and returns this value.
    * <br>
    * <br>
    * It is a relative function. Relative? Well, it is or can be absolute, but 
    * in this case, the character index must be given to the function.
    * <br>
    * <br>
    * 
    */
   public static final int FXLINE_FLAG_7_FUNCTION_XF       = 1 << 6;

   /**
    * A second function define the y.
    * <br>
    * <br>
    * By default input is current y position and returns y.
    */
   public static final int FXLINE_FLAG_8_FUNCTION_Y        = 1 << 7;

   /**
    * 
    */
   public static final int FXLINE_OFFSET_01_FLAG           = 0;

   /**
    * Additional x offset between chars in a line of text
    * <br>
    * signed
    * <br>
    * 0 -> vertical
    */
   public static final int FXLINE_OFFSET_02_CHAR_X_OFFSET1 = A_OBJECT_BASIC_SIZE + 2;

   /**
    * Additional y offset between chars in a line
    * 0 -> horizontal
    */
   public static final int FXLINE_OFFSET_03_CHAR_Y_OFFSET1 = A_OBJECT_BASIC_SIZE + 3;
}
