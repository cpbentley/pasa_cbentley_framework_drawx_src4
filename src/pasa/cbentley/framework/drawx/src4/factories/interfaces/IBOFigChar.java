package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStr;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * Type is {@link ITechFigure#FIG_TYPE_04_CHAR}
 *  
 * @author Charles Bentley
 *
 */
public interface IBOFigChar extends IBOFigure {

   /**
    * 1 byte for border flag
    * 1 byte for size, 
    * 2 bytes for arcw and arch
    * 4 bytes for secondary color
    * 1 byte gradient position
    */
   public static final int FIG_CHAR_BASIC_SIZE             = FIG__BASIC_SIZE + 3;

   /**
    * When flag is set, it defines its own {@link IBOFxStr}.
    * 
    * Otherwise figure engine will use the default one.
    */
   public static final int FIG_CHAR_FLAG_1_DEFINED_FX                = 1 << 0;

   /**
    * Draws border using filled primitives
    */
   public static final int FIG_CHAR_FLAG_2_                = 1 << 1;

   /**
    * Are coins defined
    */
   public static final int FIG_CHAR_FLAG_4_                = 1 << 3;

   /**
    * Set to true when border has a Rectangle figure
    */
   public static final int FIG_CHAR_FLAG_5_                = 1 << 4;

   /**
    * Flag set when the 8 first ByteObject are the 8 figures to be drawn.
    * <li>4 coins
    * <li>4 rectangles for the TBLR
    */
   public static final int FIG_CHAR_FLAG_8_                = 1 << 7;

   public static final int FIG_CHAR_OFFSET_1_FLAG          = FIG__BASIC_SIZE;

   /** 
    * The pixel shift applied at the 4 corners.<br>
    * Size of border depends on a ByteObject TBLR.
    * Shift reduce that value
    */
   public static final int FIG_CHAR_OFFSET_2_UTF16_VALUE2  = FIG__BASIC_SIZE + 1;

}
