package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMerge;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * 
 * @author Charles Bentley
 *
 */
public interface IBOFigBorder extends IBOFigure {

   /**
    * 1 byte for border flag
    * 1 byte for size, 
    * 2 bytes for arcw and arch
    * 4 bytes for secondary color
    * 1 byte gradient position
    */
   public static final int FIG_BORDER_BASIC_SIZE             = FIG__BASIC_SIZE + 5;

   /**
    * When flag is set, it draws the border around the provided figure boundary.
    */
   public static final int FIG_BORDER_FLAG_1_OUTER           = 1 << 0;

   /**
    * Draws border using filled primitives
    */
   public static final int FIG_BORDER_FLAG_2_FILLED          = 1 << 1;

   /**
    * Are coins defined are drawn when {@link ITechFigure#DIM_MASTER_0_NONE}
    * 
    * is for {@link IBOFigBorder#FIG_BORDER_OFFSET_4_DIM_MASTER1}
    */
   public static final int FIG_BORDER_FLAG_4_COIN            = 1 << 3;

   /**
    * Set to true when border has at least one Rectangle figure
    */
   public static final int FIG_BORDER_FLAG_5_FIGURE          = 1 << 4;

   public static final int FIG_BORDER_FLAG_6_SHIFT_COINS     = 1 << 5;

   /**
    * Flag set when every area is drawn using a Rectangle depending on .
    * {@link IBOFigBorder#FIG_BORDER_OFFSET_4_DIM_MASTER1}.
    * 
    * The first 4 to 8 figures are used. If 2, the figures are cycled.
    * 
    * <li>4 coins
    * <li>4 rectangles for the TBLR
    */
   public static final int FIG_BORDER_FLAG_8_FIGURES         = 1 << 7;

   public static final int FIG_BORDER_OFFSET_1_FLAG          = FIG__BASIC_SIZE;

   /** 
    * The pixel shift applied at the 4 corners.
    * 
    * Reduces the size of the visible border.
    * 
    * Size of border depends on a ByteObject TBLR.
    * 
    * <p>
    * {@link IBOMerge#MERGE_MASK_OFFSET_02_FLAGX1}
    * </p>
    */
   public static final int FIG_BORDER_OFFSET_2_CORNER_SHIFT1 = FIG__BASIC_SIZE + 1;

   /**
    * <li>  {@link ITechFigure#STROKE_0_SOLID}
    * <li>  {@link ITechFigure#STROKE_1_SIMPLE_DOTS}
    */
   public static final int FIG_BORDER_OFFSET_3_STROKE_STYLE1 = FIG__BASIC_SIZE + 2;

   /**
    * <p>
    * {@link IBOMerge#MERGE_MASK_OFFSET_02_FLAGX1}
    * </p>
    */
   public static final int FIG_BORDER_OFFSET_4_DIM_MASTER1   = FIG__BASIC_SIZE + 3;

}
