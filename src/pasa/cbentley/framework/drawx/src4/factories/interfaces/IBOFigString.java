package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.coredraw.src4.ctx.IConfigCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStr;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * A mix of {@link ITechFont} and {@link ITechStringer}
 * 
 * @author Charles Bentley
 *
 */
public interface IBOFigString extends ITechFigure {

   /**
    * Minimum for drawing a string
    * 1 existence flag
    * 3 bytes for font
    * 2 bytes for char
    */
   public static final int FIG_STRING_BASIC_SIZE               = IBOFigure.FIG__BASIC_SIZE + 12;

   /**
    * Scaling flag telling a {@link IBOTypesDrw#TYPE_055_SCALE} {@link ByteObject} is in the params
    * 
    * <li>scaler fit type ->  {@link IBOScaler#SCALE_OFFSET_02_FIT_TYPE1} 
    * <li>scale type id -> (linear,bilinear {@link IBOScaler#SCALE_OFFSET_03_ID1}
    * 
    * <p>
    * {@link IBOFigure#FIG_FLAG_1_ANCHOR} is ignored if scaled.. otherwise anchor.
    * when no anchor, centered by default
    * 
    * </p>
    * 
    */
   public static final int FIG_STRING_FLAG_1_SCALING           = 1 << 0;

   /**
    * When this flag is set, hidden chars from source text are shown with a visible artifact.
    * 
    * New lines from breaking are not shown.
    * 
    * Visible chars are configurable with {@link IConfigCoreDraw}
    */
   public static final int FIG_STRING_FLAG_2_SHOW_HIDDEN_CHARS = 1 << 1;

   /**
    * When true, a "..." artifact is drawn where a line a trimmed because of the lack of space.
    */
   public static final int FIG_STRING_FLAG_3_TRIM_ARTIFACT     = 1 << 2;

   /**
    * Each Lines are drawn vertically next to each other
    * 
    * E L a
    * a l r
    * c n e
    * h e 
    */
   public static final int FIG_STRING_FLAG_4_VERTICAL          = 1 << 3;

   /**
    * Set when an extra effect like 
    * <li>mask
    * <li>shadow
    * <li>vertical/diagonal text
    * 
    * Defined by {@link IBOFxStr}, {@link IBOTypesDrw#TYPE_070_TEXT_EFFECTS}
    * 
    */
   public static final int FIG_STRING_FLAG_5_EFFECT            = 1 << 4;

   /**
    * Set if a RawType string is present.
    * Must check for {@link IBOFigString#FIG_STRING_FLAG_7_CHAR} or  {@link IBOFigString#FIG_STRING_FLAG_8_RAW}
    * <br>
    * <br>
    * 
    */
   public static final int FIG_STRING_FLAG_6_EXPLICIT          = 1 << 5;

   /**
    * Figure is just one char defined at {@link IBOFigString#FIG_STRING_OFFSET_12_CHAR2}.
    * <br>
    * <br>
    * Text effect are applied
    */
   public static final int FIG_STRING_FLAG_7_CHAR              = 1 << 6;

   /**
    * When true, a String BO is defined
    */
   public static final int FIG_STRING_FLAG_8_RAW               = 1 << 7;

   public static final int FIG_STRING_FLAGX_1_                 = 1 << 0;

   /**
    */
   public static final int FIG_STRING_FLAGX_2_                 = 1 << 1;

   /**
    */
   public static final int FIG_STRING_FLAGX_3_                 = 1 << 2;

   /**
    */
   public static final int FIG_STRING_FLAGX_4_                 = 1 << 3;

   /**
    * 
    */
   public static final int FIG_STRING_FLAGX_5_                 = 1 << 4;

   /**
    * 
    */
   public static final int FIG_STRING_FLAGX_6_                 = 1 << 5;

   /**
    */
   public static final int FIG_STRING_FLAGX_7_                 = 1 << 6;

   /**
    */
   public static final int FIG_STRING_FLAGX_8_                 = 1 << 7;

   public static final int FIG_STRING_OFFSET_01_FLAG           = IBOFigure.FIG__BASIC_SIZE;

   public static final int FIG_STRING_OFFSET_02_FLAGX          = IBOFigure.FIG__BASIC_SIZE + 1;

   /**
    * MM: flag 1 of offset 6.
    * <li>{@link ITechFont#FACE_MONOSPACE} 
    * <li>{@link ITechFont#FACE_PROPORTIONAL}
    * <li>{@link ITechFont#FACE_SYSTEM}
    * <br>
    * Bigger values means a custom font
    */
   public static final int FIG_STRING_OFFSET_03_FACE1          = IBOFigure.FIG__BASIC_SIZE + 2;

   /**
    * MM: flag 2 of offset 6.
    * <li>{@link ITechFont#STYLE_BOLD} 
    * <li>{@link ITechFont#STYLE_ITALIC}
    * <li>{@link ITechFont#STYLE_PLAIN}
    */
   public static final int FIG_STRING_OFFSET_04_STYLE1         = IBOFigure.FIG__BASIC_SIZE + 3;

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
   public static final int FIG_STRING_OFFSET_05_SIZE1          = IBOFigure.FIG__BASIC_SIZE + 4;

   /**
    * <li> {@link ITechStringer#NEWLINE_MANAGER_0_IGNORE}
    * <li> {@link ITechStringer#NEWLINE_MANAGER_1_WORK}
    */
   public static final int FIG_STRING_OFFSET_06_NEWLINE1       = IBOFigure.FIG__BASIC_SIZE + 5;

   /**
    * <li> {@link ITechStringer#WORDWRAP_0_NONE}
    * <li> {@link ITechStringer#WORDWRAP_1_ANYWHERE}
    * <li> {@link ITechStringer#WORDWRAP_2_NICE_WORD}
    * <li> {@link ITechStringer#WORDWRAP_3_NICE_HYPHENATION}
    */
   public static final int FIG_STRING_OFFSET_07_WORDWRAP1      = IBOFigure.FIG__BASIC_SIZE + 6;

   /**
    * Ignores 0 and <0 and assumes 1 as default
    * 
    * When 2 or more, draw that many lines.
    * 
    * A trim artifacts might be drawn {@link IBOFigString#FIG_STRING_FLAG_3_TRIM_ARTIFACT}
    */
   public static final int FIG_STRING_OFFSET_08_MAXLINES1      = IBOFigure.FIG__BASIC_SIZE + 7;

   /**
    * Decide the policy of dealing with spaces
    * <li> {@link ITechStringer#SPACETRIM_0_NONE} 
    * <li> {@link ITechStringer#SPACETRIM_1_NORMAL} 
    * <li> {@link ITechStringer#SPACETRIM_2_JUSTIFIED} 
    */
   public static final int FIG_STRING_OFFSET_09_SPACE_TRIM1    = IBOFigure.FIG__BASIC_SIZE + 8;

   /**
    * <li> {@link ITechStringer#TAB_MANAGER_0_SINGLE_SPACE}
    * <li> {@link ITechStringer#TAB_MANAGER_1_ESCAPED}
    * <li> {@link ITechStringer#TAB_MANAGER_2_COLUMN}
    */
   public static final int FIG_STRING_OFFSET_10_TAB_MANAGER1   = IBOFigure.FIG__BASIC_SIZE + 9;

   /**
    * Depends on the Tab manager value.
    * 
    * <p>
    * In Column mode tells the size in spaces for one column. Historically it is often 8 or 4
    * </p>
    */
   public static final int FIG_STRING_OFFSET_11_TAB_AUX1       = IBOFigure.FIG__BASIC_SIZE + 10;

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
   public static final int FIG_STRING_OFFSET_12_CHAR2          = IBOFigure.FIG__BASIC_SIZE + 11;

}
