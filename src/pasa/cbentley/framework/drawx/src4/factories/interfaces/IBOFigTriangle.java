package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

public interface IBOFigTriangle extends ITechFigure {

   public static final int FIG_4TRIG_BASIC_SIZE              = 0;

   /**
    * 1 byte flag
    */
   public static final int FIG_TRIANGLE_BASIC_SIZE           = IBOFigure.FIG__BASIC_SIZE + 8;

   /**
    * Anchor Points
    */
   public static final int FIG_TRIANGLE_FLAG_1_ANCHOR_POINTS = 1 << 0;

   /**
    * When set, the value {@link IBOFigTriangle#FIG_TRIANGLE_OFFSET_03_ANGLE2} is a int based 360 degree angle.
    */
   public static final int FIG_TRIANGLE_FLAG_2_ANGLE360      = 1 << 1;

   /**
   
    */
   public static final int FIG_TRIANGLE_FLAG_3_ANGLE_RATIO   = 1 << 2;

   /**
    * 
    */
   public static final int FIG_TRIANGLE_FLAG_4_ANGLE_RAD     = 1 << 3;

   public static final int FIG_TRIANGLE_OFFSET_01_FLAG1      = IBOFigure.FIG__BASIC_SIZE;

   /**
    * Type of Angle interpretation
    * <li> {@link ITechFigure#FIG_TRIANGLE_TYPE_0_DEGREE_360}
    * <li> {@link ITechFigure#FIG_TRIANGLE_TYPE_1_DIRECTIONAL}
    * <li> {@link ITechFigure#FIG_TRIANGLE_TYPE_2_ANCHORS}
    */
   public static final int FIG_TRIANGLE_OFFSET_02_TYPE1      = IBOFigure.FIG__BASIC_SIZE + 1;

   /**
    * Intepretation of this value depends on coding type {@link IBOFigTriangle#FIG_TRIANGLE_OFFSET_02_TYPE1}
    * 
    * When true -> value is a 0-360 angle
    * <br>
    * When all those flags are false ->
    * <li> {@link C#TYPE_00_TOP}
    * <li> {@link C#TYPE_01_BOTTOM}
    * <li> {@link C#TYPE_02_LEFT}
    * <li> {@link C#TYPE_03_RIGHT}
    * <li> {@link C#TYPE_04_TopLeft}
    * <li> {@link C#TYPE_05_TopRight}
    * <li> {@link C#TYPE_06_BotLeft}
    * <li> {@link C#TYPE_07_BotRight}
    * <li> {@link C#TYPE_08_MID_TopLeft}
    * <li> {@link C#TYPE_09_MID_TopRight}
    * <li> {@link C#TYPE_10_MID_BotLeft}
    * <li> {@link C#TYPE_11_MID_BotRight}
    * <li> ..
    * 
    */
   public static final int FIG_TRIANGLE_OFFSET_03_ANGLE2     = IBOFigure.FIG__BASIC_SIZE + 2;

   /**
    * Value that will be read according to the {@link ISizer} definition.
    * 4 bytes size (percent size)
    * <br>
    * H Size
    */
   public static final int FIG_TRIANGLE_OFFSET_04_h4         = IBOFigure.FIG__BASIC_SIZE + 4;

}
