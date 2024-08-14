package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * Arranges Figures in arlequin fashion
 * @author Charles Bentley
 *
 */
public interface IBOFigArlequin extends IBOFigLayout {

   public static final int FIG_ARLEQUIN_BASIC_SIZE             = FIG_LAYOUT_BASIC_SIZE + 23;

   /**
    * When true fills the area with Figure color
    */
   public static final int FIG_ARLEQUIN_FLAG_1_FILL_BG         = 1 << 0;

   /**
    * When true, FIG_ARLEQUIN_OFFSET_3_SIZE_W4 is number of elements on a row
    */
   public static final int FIG_ARLEQUIN_FLAG_2_SIZEW_IS_NUM    = 1 << 1;

   /**
    */
   public static final int FIG_ARLEQUIN_FLAG_3_SIZEH_IS_NUM    = 1 << 2;

   public static final int FIG_ARLEQUIN_FLAG_4_                = 1 << 3;

   public static final int FIG_ARLEQUIN_FLAG_5_                = 1 << 4;

   public static final int FIG_ARLEQUIN_OFFSET_1_FLAG          = FIG_LAYOUT_BASIC_SIZE;

   public static final int FIG_ARLEQUIN_OFFSET_2_COLOR4        = FIG_LAYOUT_BASIC_SIZE + 1;

   /**
    * When size does not fit area. flag tells what should be done
    */
   public static final int FIG_ARLEQUIN_OFFSET_3_SIZE_W4       = FIG_LAYOUT_BASIC_SIZE + 5;

   public static final int FIG_ARLEQUIN_OFFSET_4_SIZE_H4       = FIG_LAYOUT_BASIC_SIZE + 9;

   public static final int FIG_ARLEQUIN_OFFSET_5_SIZE_SEP_W4   = FIG_LAYOUT_BASIC_SIZE + 13;

   public static final int FIG_ARLEQUIN_OFFSET_6_SIZE_SEP_H4   = FIG_LAYOUT_BASIC_SIZE + 17;

   /**
    * directive on how to deal with extra space.
    * centers
    * draws biggers figures on the edges
    * clip
    * <li> {@link ITechFigure#EXTRA_0_CENTER} 
    * <li> {@link ITechFigure#EXTRA_1_TOP_LEFT} 
    * <li> {@link ITechFigure#EXTRA_2_BOT_RITE} 
    */
   public static final int FIG_ARLEQUIN_OFFSET_7_EXTRA_W_TYPE1 = FIG_LAYOUT_BASIC_SIZE + 21;

   public static final int FIG_ARLEQUIN_OFFSET_8_EXTRA_H_TYPE1 = FIG_LAYOUT_BASIC_SIZE + 22;

}
