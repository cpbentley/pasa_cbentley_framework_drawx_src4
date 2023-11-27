package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.string.IBOFxStr;
import pasa.cbentley.framework.drawx.src4.string.StringFx;
import pasa.cbentley.framework.drawx.src4.string.Stringer;

public interface IBOFigString extends ITechFigure {

   /**
    * Minimum for drawing a string
    * 1 existence flag
    * 3 bytes for font
    * 2 bytes for char
    */
   public static final int FIG_STRING_BASIC_SIZE       = ITechFigure.FIG__BASIC_SIZE + 6;

   /**
    * Scaling switch. <br>
    * <li>scaler type : 
    * <li>scale id (linear,bilinear
    * 
    * We have a scaler object {@link IBOScaler#SCALE_OFFSET_02_FIT_TYPE1}.
    * 
    * {@link ITechFigure#FIG_FLAG_1_ANCHOR} is ignored if scaled.. otherwise anchor.
    * 
    * when no anchor, centered by default
    */
   public static final int FIG_STRING_FLAG_1_SCALING   = 1 << 0;

   /**
    * Set when an extra effect like 
    * <li>mask
    * <li>shadow
    * <li>vertical/diagonal text
    * 
    * Defined by {@link IBOFxStr}, {@link IBOTypesDrw#TYPE_070_TEXT_EFFECTS}
    * 
    * Will require a {@link Stringer} with {@link StringFx} to be drawn.
    * 
    */
   public static final int FIG_STRING_FLAG_5_EFFECT    = 1 << 4;

   /**
    * Set if a RawType string is present.
    * Must check for {@link IBOFigString#FIG_STRING_FLAG_7_CHAR} or  {@link IBOFigString#FIG_STRING_FLAG_8_RAW}
    * <br>
    * <br>
    * 
    */
   public static final int FIG_STRING_FLAG_6_EXPLICIT  = 1 << 5;

   /**
    * Figure is just one char defined at {@link IBOFigString#FIG_STRING_OFFSET_05_CHAR2}.
    * <br>
    * <br>
    * Text effect are applied
    */
   public static final int FIG_STRING_FLAG_7_CHAR      = 1 << 6;

   /**
    * When true, a String BO is defined
    */
   public static final int FIG_STRING_FLAG_8_RAW       = 1 << 3;

   public static final int FIG_STRING_OFFSET_01_FLAG   = ITechFigure.FIG__BASIC_SIZE;

   /**
    * MM: flag 1 of offset 6.
    * <li>{@link ITechFont#FACE_MONOSPACE} 
    * <li>{@link ITechFont#FACE_PROPORTIONAL}
    * <li>{@link ITechFont#FACE_SYSTEM}
    * <br>
    * Bigger values means a custom font
    */
   public static final int FIG_STRING_OFFSET_02_FACE1  = ITechFigure.FIG__BASIC_SIZE + 1;

   /**
    * MM: flag 2 of offset 6.
    * <li>{@link ITechFont#STYLE_BOLD} 
    * <li>{@link ITechFont#STYLE_ITALIC}
    * <li>{@link ITechFont#STYLE_PLAIN}
    */
   public static final int FIG_STRING_OFFSET_03_STYLE1 = ITechFigure.FIG__BASIC_SIZE + 2;

   /**
    * MM: flag 3 of offset 6.
    * <br>
    * <br>
    * <li>{@link ITechFont#SIZE_0_DEFAULT} 16
    * <li>{@link ITechFont#SIZE_1_TINY} 16
    * <li>{@link ITechFont#SIZE_2_SMALL} 16
    * <li>{@link ITechFont#SIZE_3_MEDIUM} 0
    * <li>{@link ITechFont#SIZE_4_LARGE} 8
    * <li>{@link ITechFont#SIZE_5_HUGE} 8
    * <br>
    * <br>
    * When a value is different than those, it is used a such if the platform
    * has a integer font size granularity. Otherwise it is automatically moved to one
    * of those 3 choices.
    * <li> Values of 16 and above are large.
    * <li>Values of 0, 9 to 15 are medium
    * <li>Values of 1 to to 8 are small
    * <br>
    * <br>
    * 
    */
   public static final int FIG_STRING_OFFSET_04_SIZE1  = ITechFigure.FIG__BASIC_SIZE + 3;

   /**
    * Character Unicode. <br>
    * Often used with a scaler to draw big numbers. Centered and scaled on the drawing area.
    * 
    * Smallest unit
    * 
    * <b>Possible styling characteristics</b>
    * <li> Font Face
    * <li> Font Size
    * <li> Font Style
    * <li> Font color
    * <li> Mask
    * <li> Background figure
    * <li> Extra TBLR
    * <li> Shadow
    * <li> Blend layer
    * <li> Rotation
    */
   public static final int FIG_STRING_OFFSET_05_CHAR2  = ITechFigure.FIG__BASIC_SIZE + 4;

}
