package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.utils.StringUtils;
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
   public static final int FIG_STRING_BASIC_SIZE                  = IBOFigure.FIG__BASIC_SIZE + 15;

   /**
    * The String figure is explicit. It defines its content completely.
    * 
    * <li> {@link IBOFigString#FIG_STRING_FLAGX_6_DEFINED_CHAR} 
    * <li> {@link IBOFigString#FIG_STRING_FLAGX_5_DEFINED_STRING}
    * 
    * 
    * Therefore it cannot/should be used in a String generic container.
    * 
    */
   public static final int FIG_STRING_FLAG_1_EXPLICIT             = 1 << 0;

   /**
    * When this flag is set, hidden chars from source text are shown with a visible artifact.
    * 
    * New lines from breaking are not shown.
    * 
    * Visible chars are configurable with {@link IConfigCoreDraw}
    */
   public static final int FIG_STRING_FLAG_2_SHOW_HIDDEN_CHARS    = 1 << 1;

   /**
    * When true, a "..." artifact is drawn where a line a trimmed because of the lack of space.
    */
   public static final int FIG_STRING_FLAG_3_TRIM_ARTIFACT        = 1 << 2;

   /**
    * Each Lines are drawn vertically next to each other
    * 
    * E L a
    * a l r
    * c n e
    * h e 
    */
   public static final int FIG_STRING_FLAG_4_VERTICAL             = 1 << 3;

   /**
    * 
    */
   public static final int FIG_STRING_FLAG_5_                     = 1 << 4;

   /**
    * 
    */
   public static final int FIG_STRING_FLAG_6_                     = 1 << 5;
   public static final int FIG_STRING_FLAG_7_                     = 1 << 6;
   public static final int FIG_STRING_FLAG_8_                     = 1 << 7;

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
   public static final int FIG_STRING_FLAGX_1_DEFINED_SCALER      = 1 << 0;

   /**
    * Set when an extra effect like 
    * <li>mask
    * <li>shadow
    * <li>vertical/diagonal text
    * 
    * Defined by {@link IBOFxStr}, {@link IBOTypesDrw#TYPE_070_TEXT_EFFECTS}
    * 
    */
   public static final int FIG_STRING_FLAGX_2_DEFINED_FX          = 1 << 1;

   /**
    */
   public static final int FIG_STRING_FLAGX_3_                    = 1 << 2;

   /**
    */
   public static final int FIG_STRING_FLAGX_4_                    = 1 << 3;

   /**
    * When true, a String BO is defined
    */
   public static final int FIG_STRING_FLAGX_5_DEFINED_STRING      = 1 << 4;

   /**
    * Figure is just one char defined at {@link IBOFigString#FIG_STRING_OFFSET_06_CHAR2}.
    * <br>
    * <br>
    * Text effect are applied
    */
   public static final int FIG_STRING_FLAGX_6_DEFINED_CHAR        = 1 << 5;

   /**
    */
   public static final int FIG_STRING_FLAGX_7_                    = 1 << 6;

   /**
    */
   public static final int FIG_STRING_FLAGX_8_                    = 1 << 7;

   public static final int FIG_STRING_OFFSET_01_FLAG              = IBOFigure.FIG__BASIC_SIZE;

   public static final int FIG_STRING_OFFSET_02_FLAGX             = IBOFigure.FIG__BASIC_SIZE + 1;

   /**
    * MM: flag 1 of offset 6.
    * <li>{@link ITechFont#FACE_MONOSPACE} 
    * <li>{@link ITechFont#FACE_PROPORTIONAL}
    * <li>{@link ITechFont#FACE_SYSTEM}
    * <br>
    * Bigger values means a custom font
    */
   public static final int FIG_STRING_OFFSET_03_FACE1             = IBOFigure.FIG__BASIC_SIZE + 2;

   /**
    * MM: flag 2 of offset 6.
    * <li>{@link ITechFont#STYLE_BOLD} 
    * <li>{@link ITechFont#STYLE_ITALIC}
    * <li>{@link ITechFont#STYLE_PLAIN}
    */
   public static final int FIG_STRING_OFFSET_04_STYLE1            = IBOFigure.FIG__BASIC_SIZE + 3;

   /**
    * The size of the font.
    * 
    * <li>{@link ITechFont#SIZE_0_DEFAULT} 
    * <li>{@link ITechFont#SIZE_1_TINY} 
    * <li>{@link ITechFont#SIZE_2_SMALL} 
    * <li>{@link ITechFont#SIZE_3_MEDIUM} 
    * <li>{@link ITechFont#SIZE_4_LARGE}
    * <li>{@link ITechFont#SIZE_5_HUGE}
    * 
    * <p>
    * When a value is different than those 5 values, it is used a such if the platform
    * has a integer font size granularity. 
    * 
    * Otherwise it is automatically moved to one of the following:
    * </p>
    * 
    * <li>Value of 0 is default.
    * <li>6 to to 8 = {@link ITechFont#SIZE_1_TINY}.
    * <li>9 to 11 = {@link ITechFont#SIZE_2_SMALL}.
    * <li>11 to 15 = {@link ITechFont#SIZE_3_MEDIUM}.
    * <li>16 to 23 = {@link ITechFont#SIZE_4_LARGE}.
    * <li>23 and above = {@link ITechFont#SIZE_5_HUGE}.
    * <p>
    * MergeMask: flag 3 of offset 6.
    * </p>
    */
   public static final int FIG_STRING_OFFSET_05_SIZE1             = IBOFigure.FIG__BASIC_SIZE + 4;

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
   public static final int FIG_STRING_OFFSET_06_CHAR2             = IBOFigure.FIG__BASIC_SIZE + 5;

   /**
    * <li> {@link ITechStringer#WORDWRAP_0_NONE}
    * <li> {@link ITechStringer#WORDWRAP_1_ANYWHERE}
    * <li> {@link ITechStringer#WORDWRAP_2_NICE_WORD}
    * <li> {@link ITechStringer#WORDWRAP_3_NICE_HYPHENATION}
    */
   public static final int FIG_STRING_OFFSET_07_WRAP_WIDTH1       = IBOFigure.FIG__BASIC_SIZE + 7;

   /**
    * Decides what to do on a constrained height dimension
    * <li> {@link ITechStringer#LINEWRAP_0_NONE}
    * <li> {@link ITechStringer#LINEWRAP_1_ANYWHERE}
    */
   public static final int FIG_STRING_OFFSET_08_WRAP_HEIGHT1      = IBOFigure.FIG__BASIC_SIZE + 8;

   /**
    * Decide the policy of dealing with spaces
    * <li> {@link ITechStringer#SPACETRIM_0_NONE} 
    * <li> {@link ITechStringer#SPACETRIM_1_NORMAL} 
    * <li> {@link ITechStringer#SPACETRIM_2_JUSTIFIED} 
    */
   public static final int FIG_STRING_OFFSET_09_SPACE_TRIM1       = IBOFigure.FIG__BASIC_SIZE + 9;

   /**
    * Ignores 0 and <0 and assumes 1 as default
    * 
    * When 2 or more, draw that many lines.
    * 
    * A trim artifacts might be drawn {@link IBOFigString#FIG_STRING_FLAG_3_TRIM_ARTIFACT}
    */
   public static final int FIG_STRING_OFFSET_10_MAXLINES1         = IBOFigure.FIG__BASIC_SIZE + 10;

   /**
    * Depends on the Tab manager value.
    * 
    * <p>
    * In Column mode tells the size in spaces for one column. Historically it is often 8 or 4
    * </p>
    */
   public static final int FIG_STRING_OFFSET_11_MANAGER_TAB_AUX1  = IBOFigure.FIG__BASIC_SIZE + 11;

   /**
    * How to manage special character  \t
    * <li> {@link ITechStringer#TAB_MANAGER_0_SINGLE_SPACE}
    * <li> {@link ITechStringer#TAB_MANAGER_1_ESCAPED}
    * <li> {@link ITechStringer#TAB_MANAGER_2_COLUMN}
    */
   public static final int FIG_STRING_OFFSET_13_MANAGER_TAB1      = IBOFigure.FIG__BASIC_SIZE + 12;

   /**
    * How to manage special character \n
    * <li> {@link ITechStringer#NEWLINE_MANAGER_0_IGNORE}
    * <li> {@link ITechStringer#NEWLINE_MANAGER_1_WORK}
    */
   public static final int FIG_STRING_OFFSET_14_MANAGER_NEWLINE1  = IBOFigure.FIG__BASIC_SIZE + 13;

   /**
    * How to manage special character \f -> {@link StringUtils#FORM_FEED_F}
    * <li> {@link ITechStringer#NEWLINE_MANAGER_0_IGNORE}
    * <li> {@link ITechStringer#NEWLINE_MANAGER_1_WORK}
    */
   public static final int FIG_STRING_OFFSET_15_MANAGER_FORMFEED1 = IBOFigure.FIG__BASIC_SIZE + 14;

}
