package pasa.cbentley.framework.drawx.src4.string.interfaces;

public interface IBOFxStrChar extends IBOFxStr {

   /**
    * 
    */
   public static final int FXCHAR_BASIC_SIZE           = FX_BASIC_SIZE + 4;

   /**
    * Flags specific to the {@link IBOFxStr#FX_SCOPE_1_CHAR}.
    * 
    */
   public static final int FXCHAR_OFFSET_01_FLAG       = FX_BASIC_SIZE;

   /**
    * Contains an array of char for which to apply this char scoped style
    */
   public static final int FXCHAR_FLAG_1_CHARS_BASED   = 1 << 0;

   /**
    * Type of decoration around the character.
    */
   public static final int FXCHAR_OFFSET_02_TYPE_DECO1 = FX_BASIC_SIZE + 1;
}
