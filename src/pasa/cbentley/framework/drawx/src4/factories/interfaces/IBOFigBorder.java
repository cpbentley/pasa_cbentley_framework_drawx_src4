package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

public interface IBOFigBorder extends IBOFigure {

   /**
    * 1 byte for border flag
    * 1 byte for size, 
    * 2 bytes for arcw and arch
    * 4 bytes for secondary color
    * 1 byte gradient position
    */
   public static final int FIG_BORDER_BASIC_SIZE             = IBOFigure.FIG__BASIC_SIZE + 3;

   /**
    * When flag is set, it draws the border around the provided figure boundary.
    */
   public static final int FIG_BORDER_FLAG_1_OUTER           = 1 << 0;

   /**
    * Draws border using filled primitives
    */
   public static final int FIG_BORDER_FLAG_2_FILLED          = 1 << 1;

   /**
    * Are coins defined
    */
   public static final int FIG_BORDER_FLAG_4_COIN            = 1 << 3;

   /**
    * Set to true when border has a Rectangle figure
    */
   public static final int FIG_BORDER_FLAG_5_FIGURE          = 1 << 4;

   /**
    * Flag set when the 8 first ByteObject are the 8 figures to be drawn.
    * <li>4 coins
    * <li>4 rectangles for the TBLR
    */
   public static final int FIG_BORDER_FLAG_8_FIGURES         = 1 << 7;

   public static final int FIG_BORDER_OFFSET_1_FLAG          = IBOFigure.FIG__BASIC_SIZE;

   /** 
    * The pixel shift applied at the 4 corners.<br>
    * Size of border depends on a ByteObject TBLR.
    * Shift reduce that value
    */
   public static final int FIG_BORDER_OFFSET_2_CORNER_SHIFT1 = IBOFigure.FIG__BASIC_SIZE + 1;

   /**
    * <li>  {@link ITechFigure#STROKE_0_SOLID}
    * <li>  {@link ITechFigure#STROKE_1_SIMPLE_DOTS}
    */
   public static final int FIG_BORDER_OFFSET_3_STROKE_STYLE1 = IBOFigure.FIG__BASIC_SIZE + 2;

}
