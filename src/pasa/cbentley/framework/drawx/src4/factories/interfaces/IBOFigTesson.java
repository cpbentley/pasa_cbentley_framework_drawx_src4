package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;

/**
 * Draws a multitude of triangles with varying colors
 * @author Charles Bentley
 *
 */
public interface IBOFigTesson extends IBOFigure {

   public static final int FIG_TESSON_BASIC_SIZE         = FIG__BASIC_SIZE + 30;

   /**
    * a {@link IBOTypes}  colors decides which color to use next from current color   
    */
   public static final int FIG_TESSON_FLAG_1_RND_COLORS  = 1 << 0;

   public static final int FIG_TESSON_FLAG_2_USE_SEED    = 1 << 1;

   public static final int FIG_TESSON_FLAG_3_            = 1 << 2;

   public static final int FIG_TESSON_FLAG_4_            = 1 << 3;

   public static final int FIG_TESSON_FLAG_5_            = 1 << 4;

   public static final int FIG_TESSON_FLAG_6_            = 1 << 5;

   public static final int FIG_TESSON_FLAG_7_            = 1 << 6;

   public static final int FIG_TESSON_FLAG_8_            = 1 << 7;

   public static final int FIG_TESSON_OFFSET_1_FLAG      = FIG__BASIC_SIZE;

   public static final int FIG_TESSON_OFFSET_2_COLOR4    = FIG__BASIC_SIZE + 1;

   public static final int FIG_TESSON_OFFSET_3_SIZE4     = FIG__BASIC_SIZE + 5;

   public static final int FIG_TESSON_OFFSET_4_SIZE_MAX1 = FIG__BASIC_SIZE + 9;

   public static final int FIG_TESSON_OFFSET_5_SEED8     = FIG__BASIC_SIZE + 10;

}
