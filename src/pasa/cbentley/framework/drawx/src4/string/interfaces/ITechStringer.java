/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.core.src4.text.TextModel;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.drawx.src4.string.LineStringer;
import pasa.cbentley.framework.drawx.src4.string.StringFx;
import pasa.cbentley.framework.drawx.src4.string.StringFxLeaf;
import pasa.cbentley.framework.drawx.src4.string.StringMetrics;
import pasa.cbentley.framework.drawx.src4.string.Stringer;

/**
 * 
 * @author Charles Bentley
 *
 */
public interface ITechStringer {

   /**
    * 
    */
   public static final int BREAK_EXTRA_SIZE                     = 2;

   public static final int BREAK_FLAG_1_NO_BREAKS               = 1;

   /**
    * The size of the header.. one slot for number of ints
    */
   public static final int BREAK_HEADER_SIZE                    = 1;

   /**
    * Forces a break when seeing a new line in some break methods
    */
   public static final int BREAK_OPTION_1_NEW_LINE              = 1;

   /**
    * Trailer. 1 slot for the flag
    */
   public static final int BREAK_TRAILER_SIZE                   = 1;

   /**
    * 1 for startoffset
    * 2 for num of chars
    * 3 for computed width of the whole line if any
    */
   public static final int BREAK_WINDOW_SIZE                    = 3;

   /**
    * Helper flag telling us that the {@link StringFx} does not change
    * height between characters.
    */
   public static final int FX_FLAG_01_SAME_HEIGHTS              = 1 << 0;

   /**
    * 
    */
   public static final int FX_FLAG_02_HAS_LINE_VISUAL_ARTIFACTS = 1 << 1;

   /**
    * {@link Stringer} has word scoped fx.
    * 
    * {@link Stringer} has to keep track of words
    */
   public static final int FX_FLAG_03_HAS_WORD_VISUALS          = 1 << 2;

   /**
    * Underline
    */
   public static final int FX_FLAG_04_HAS_CHAR_VISUALS          = 1 << 3;

   public static final int FX_FLAG_05_UNSTABLE_COLOR            = 1 << 4;

   /**
    * The source char[] array cannot be written outside [offset-offset+len]
    * 
    * By default the char[] array is assumed to be protected when set with
    * {@link Stringer#setStringFig(ByteObject, char[], int, int)S}
    */
   public static final int FX_FLAG_06_PROTECTED_CHARS           = 1 << 5;

   /**
    * 
    */
   public static final int FX_FLAG_07_UNSTABLE_FONT             = 1 << 6;

   /**
    *
    */
   public static final int FX_MASKDRAW_TYPE_0_NONE              = 0;

   /**
    *
    */
   public static final int FX_MASKDRAW_TYPE_1_CHAR              = 1;

   /**
    *
    */
   public static final int FX_MASKDRAW_TYPE_2_WORD              = 2;

   /**
    *
    */
   public static final int FX_MASKDRAW_TYPE_3_LINE              = 3;

   /**
    *
    */
   public static final int FX_MASKDRAW_TYPE_4_SENTENCE          = 4;

   public static final int FX_PATTERN_0_NONE                    = 0;

   public static final int FX_PATTERN_1_ALL_INSTANCES           = 1;

   /**
    * Default applies the FX to the interval on a line.
    * Fx applies independantly on each line.
    */
   public static final int FX_SCOPE_0_TEXT                      = 0;

   /**
    * The CHAR level is the first one. It defines how characters are drawn relative to each other.
    * <br>
    * <br>
    * 
    * Random styling: each char has a random font from pooling defined
    * <br>
    * <br>
    * Effect to be create for each characters.
    * <br>
    * <br>
    * <li> individual char level Mask Figure
    */
   public static final int FX_SCOPE_1_CHAR                      = 1;

   /**
    * At the LINE level we find the following options : 
    * <br>
    * <li>The Relative function positioning.
    * 	Default is x unchanged and y increment by Font/Line Height
    * 	Function similar to the Relative Function Position of characters 
    * 	but applied to lines
    * <li>RFP has an impact the width and height consumed by the text effect
    * <li>One Word Per Line
    * <li>x-Line Level Mask Figure (apply mask underyling figure to X lines at a time
    * <br>
    * <br>
    * TODO how do model alternative line background styling ?
    * index based function .. style applicator ?
    */
   public static final int FX_SCOPE_4_LINE                      = 4;

   /**
    * FX is applied to string 'word'.
    * What happens when the word is cut with -? Right now words are not cut.
    * 
    * <p>
    * Competing Word and Line Fx ?
    * Examples. Word Mask vs Line Mask Smaller Scope wins in practice.
    * But if Word Mask fx applies to the first word of a line, the line mask is applied to the whole line including the first word
    * Line Mask might make the space glow in a certain way! so Line Mask does not lose completely
    * </p>
    */
   public static final int FX_SCOPE_2_WORD                      = 2;

   /**
    * Each text block separated with an empty line
    */
   public static final int FX_SCOPE_3_PARA                      = 3;

   /**
    * Break text into sentences. The start of a sentence is always a abcABC character?
    */
   public static final int FX_SCOPE_5_FRAZ                      = 5;

   /**
    * Only applies to word separators as defined in {@link TextModel}.
    * usually whitespace and ,
    */
   public static final int FX_SCOPE_6_SEPARATORS                = 6;

   /**
    * Drawing the characters of the Interval don't require the {@link StringMetrics}
    */
   public static final int FX_STRUCT_TYPE_0_BASIC_HORIZONTAL    = 0;

   /**
    * Custom x variables.. but if Font changes, the Anchor become a factor
    * 
    * Mode forces the draw using specific x values
    * 
    * <li>{@link LineStringer#getX()}
    * <li>{@link StringMetrics#getCharX(int)}
    * MID Y for 
    */
   public static final int FX_STRUCT_TYPE_1_METRICS_X           = 1;

   /**
    * Only spaces have special
    */
   public static final int FX_STRUCT_TYPE_2_METRICS_SPACE_X     = 1;

   /**
   * Font
   */
   public static final int FX_STRUCT_TYPE_2_METRICS_XY          = 2;

   public static final int LINEWRAP_0_NONE                      = 0;

   /**
    * Draws lines fitting the height provided
    */
   public static final int LINEWRAP_1_ANYWHERE                  = 1;

   public static final int MAX_STYLE_LAYERS                     = 4;

   /**
    * No spaces are ever trimmed
    */
   public static final int SPACETRIM_0_NONE                     = 0;

   /**
    * When breaking strings into lines, remove leading and trailing leading white space
    * characters so that a line never starts/ends with a whitespace character.
    * 
    * <p>
    * What about leading spaces on the first line ? Removed as well.
    * </p>
    */
   public static final int SPACETRIM_1_NORMAL                   = 1;

   /**
    * For big strings on several lines, adds white spaces to that words finish at the end of the line.
    * 
    * <p>
    * Usually used in combination with hyphenated word wrap.
    * </p>
    * See {@link ITechStringer#WORDWRAP_3_NICE_HYPHENATION} 
    * on {@link IBOStrAuxFormat#STR_FORMAT_OFFSET_02_WRAP_WIDTH1}
    * <p>
    * This mode force the use of char individual width as spaces will gain extra pixels to exactly match
    * the pixel width given to the line
    * </p>
    * 
    * <p>
    * What about the last line of a paragraph ?
    * </p>
    */
   public static final int SPACETRIM_2_JUSTIFIED                = 2;

   /**
    * \f is ignored completely. 
    * 
    */
   public static final int SPECIALS_FORMFEED_0_IGNORED          = 0;

   public static final int SPECIALS_FORMFEED_1_SPACE_SPECIAL    = 1;

   public static final int SPECIALS_FORMFEED_2_JAVA_ESCAPED     = 2;

   public static final int SPECIALS_FORMFEED_3_NEW_PAGE         = 3;

   /**
    * Does not create newlines at \n and \r. 
    * 
    * It is only shown if show hidden character option is enabled
    * <p>
    * {@link IBOStrAuxSpecialCharDirective#AUX_CHARS_OFFSET_02_NEWLINE1}
    * </p>
    * Does not even look for trim.
    * Fastest algo but no artifacts.
    * <li> For single line trim. use {@link ITechStringer#WORDWRAP_0_NONE} with 
    */
   public static final int SPECIALS_NEWLINE_0_IGNORED           = 0;

   /**
    * Replaces the newline \n and \r character with a space or the special char
    * 
    */
   public static final int SPECIALS_NEWLINE_1_SPACE_SPECIAL     = 1;

   /**
    * 
    */
   public static final int SPECIALS_NEWLINE_2_JAVA_ESCAPED      = 2;

   /**
    * Create newlines at \n and \r\n, or trims when not enough space due to {@link Stringer#setBreakWidth(int)}
    * <p>
    * The trim trigger is because of {@link IBOStrAuxFormat#STR_FORMAT_OFFSET_05_MAXLINES1} set to 1 or more.
    * This forces the algo to stop at 1 line and trim it. Double dot artifact if
    * {@link IBOStrAuxFormat#STR_FORMAT_FLAG_3_TRIM_ARTIFACT} is set to true
    * </p>
    * <p>
    * {@link IBOStrAuxSpecialCharDirective#AUX_CHARS_OFFSET_02_NEWLINE1}
    * </p>
    */
   public static final int SPECIALS_NEWLINE_3_WORK              = 3;

   /**
    * 
    */
   public static final int SPECIALS_NEWLINE_4_WORK_SHOW         = 4;

   public static final int SPECIALS_TAB_0_IGNORED               = 0;

   /**
    * Tab is replaced with a single space
    * 
    * When {@link IBOFigString#FIG_STRING_FLAG_2_SHOW_HIDDEN_CHARS}, the space
    * is replaced with tab hidden char mapping.
    */
   public static final int SPECIALS_TAB_1_SPACE_SPECIAL         = 1;

   /**
    * Tab is Java represented with \t.
    * 
    * Compatible 
    */
   public static final int SPECIALS_TAB_2_JAVA_ESCAPED          = 2;

   /**
    * Notepad behaviour. Does not force columns.
    * <li>Display Tab as one big blank character. Selection shows one
    * <li>Depending on tab offset in the line, forward next char to start
    * at a multiple of Tab aux on the pixel value.
    * <li> In prop fonts, once the tab portion gets over the pixel value, 
    * In a line with 5m, tab executes.. you need 10 dots for the tab to execute
    */
   public static final int SPECIALS_TAB_3_NOTEPAD               = 3;

   /**
    * Depending on tab size, insert spaces to reach a multiple of Tabsize of characters count.
    */
   public static final int SPECIALS_TAB_4_ECLIPSE               = 4;

   /**
    * This field decides the space size of a tab column {@link IBOStrAuxSpecialCharDirective#AUX_CHARS_OFFSET_05_MANAGER_TAB_AUX1}
    * or
    * The first line decides the width of the tab columns
    * 
    * For next lines, if a char goes over the tab col width, it is moved over to the next tab column.
    * 
    * In this mode, new lines are accepted, creating empty column.
    * However word wrap is forcibly set to {@link ITechStringer#WORDWRAP_0_NONE}.
    * 
    * 
    */
   public static final int SPECIALS_TAB_5_COLUMN                = 5;

   /**
    * Set when
    */
   public static final int STATE_01_CHAR_EFFECTS                = 1;

   /**
    * Set when all characters widths have been computed.
    */
   public static final int STATE_02_CHAR_WIDTHS                 = 1 << 1;

   public static final int STATE_03_CHECK_CLIP                  = 1 << 2;

   /**
    * Modifies the char array and reduces the length
    */
   public static final int STATE_04_TRIMMED                     = 1 << 3;

   /**
    * 
    */
   public static final int STATE_05_STR_WIDTH                   = 1 << 4;

   /**
    * Set when characters coordinates have been computed.
    * <br>
    * <br>
    * 
    */
   public static final int STATE_06_CHAR_POSITIONS              = 1 << 5;

   /**
    * When the string had to be broken into several pieces.
    */
   public static final int STATE_07_BROKEN                      = 1 << 6;

   /**
    * Set when at least one Static style is impacting at least one character.
    */
   public static final int STATE_08_ACTIVE_STYLE                = 1 << 7;

   /**
    * 
    */
   public static final int STATE_09_EDITING                     = 1 << 8;

   /**
    * Set when at least one dynamic style is impacting at least one character.
    */
   public static final int STATE_10_ACTIVE_DYNAMIC_STYLE        = 1 << 9;

   public static final int STATE_11_DIFFERENT_FONTS             = 1 << 10;

   public static final int STATE_12_                            = 1 << 11;

   /**
    * Set when there is a FX component to the String. anything other than basic color will set this flag
    * 
    */
   public static final int STATE_13_FX                          = 1 << 12;

   /**
    * Set when characters are set horizontally as provided by {@link GraphicsX#drawChars(char[],public static final int ,public static final int ,public static final int ,public static final int ,public static final int )} method.
    * <br>
    * <br>
    * Colors could change though. in which case {@link ITechStringer#TYPE_1_SINGLE_LINE_FX}
    * <br>
    * <br>
    * 
    */
   public static final int STATE_14_BASIC_POSITIONING           = 1 << 13;

   /**
    * At least one {@link StringFx} defines a bg or a style hosted in a {@link FigDrawable}.
    * <br>
    * <br>
    * 
    */
   public static final int STATE_15_BG_DEFINED                  = 1 << 14;

   public static final int STATE_16_STATIC_INDEX_FX             = 1 << 15;

   /**
    * Set when Fx has been computed and {@link StringFxLeaf} synchronized
    */
   public static final int STATE_17_COMPUTED_FX                 = 1 << 16;

   /**
    * Every characters is drawn using the same width. This means same font size
    * and font type. and no text effect changing the size of the string subset.
    * 
    * <p>
    * Beware that some characters are zero width.. 
    * <li> {@link StringUtils#NEW_LINE}
    * <li> {@link StringUtils#TAB}
    * <li> {@link StringUtils#NEW_LINE}
    * </p>
    */
   public static final int STATE_18_FULL_MONOSPACE              = 1 << 17;

   /**
    * True when Stringer has been FXed
    */
   public static final int STATE_19_FX_SETUP                    = 1 << 18;

   /**
    * True if chars,lines sizes and positions have been computed relative to their
    * 
    * When true, {@link ITechStringer#STATE_19_FX_SETUP} must also be true
    */
   public static final int STATE_20_METERED_FULL                = 1 << 19;

   /**
    * There are at least one zero width char
    */
   public static final int STATE_21_ZERO_WIDTH_CHARS            = 1 << 20;

   public static final int STATE_22_                            = 1 << 21;

   public static final int STATE_23_                            = 1 << 22;

   public static final int STATE_24_                            = 1 << 23;

   public static final int STATE_25_                            = 1 << 24;

   public static final int STATE_26_MODEL_SEPARATORS_FX         = 1 << 25;

   public static final int STATE_27_MODEL_SPACE_FX              = 1 << 26;

   public static final int STATE_28_MODEL_SENTENCE_FX           = 1 << 27;

   /**
    * This enable word indexing and looking for space and punctuation.
    * <br>
    * <br>
    * 
    */
   public static final int STATE_29_MODEL_WORD_FX               = 1 << 28;

   /**
    * Set when the char array cannot be written to. Any edition must create a new array.
    * 
    */
   public static final int STATE_30_PROTECTED                   = 1 << 29;

   public static final int STATE_31_                            = 1 << 30;

   public static final int STATE_32_                            = 1 << 31;

   /**
    * Does not do any word wrap. 
    * <p>
    * If {@link Stringer} has a break width, it generates a trim visual cue at the end of the broken line
    * </p>
    * 
    * <p>
    * Value is set here -> {@link IBOStrAuxFormat#STR_FORMAT_OFFSET_02_WRAP_WIDTH1}.
    * </p>
    * 
    */
   public static final int WORDWRAP_0_NONE                      = 0;

   /**
    * Cuts anywhere in a word without any artifacts
    */
   public static final int WORDWRAP_1_ANYWHERE                  = 1;

   /**
    * Tries to cut at words, sending them to next line.. if word too big, cut at size
    */
   public static final int WORDWRAP_2_NICE_WORD                 = 2;

   /**
    * Complex wrap using a - cutting at specific places in a word apple - > ap-ple
    * 
    * <p>
    * Usually in combination with space trim {@link ITechStringer#SPACETRIM_2_JUSTIFIED}
    * 
    * </p>
    */
   public static final int WORDWRAP_3_NICE_HYPHENATION          = 3;

}
