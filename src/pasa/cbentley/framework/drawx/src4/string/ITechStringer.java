/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

/**
 * 
 * @author Charles Bentley
 *
 */
public interface ITechStringer {

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

   public static final int BREAK_EXTRA_SIZE                     = 2;

   /**
    * Drawing the characters of the Interval don't require the {@link StringMetrics}
    */
   public static final int FX_STRUCT_TYPE_0_BASIC               = 0;

   /**
    * Custom x variables.. but if Font changes, the Anchor become a factor
    * 
    * MID Y for 
    */
   public static final int FX_STRUCT_TYPE_1_METRICS_X           = 1;

   /**
   * Font
   */
   public static final int FX_STRUCT_TYPE_2_METRICS_XY          = 2;

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

   /**
    * 
    */
   public static final int BREAK_HEADER_SIZE                    = 1;

   public static final int BREAK_TRAILER_SIZE                   = 1;

   public static final int BREAK_WINDOW_SIZE                    = 3;

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
    * This enable word indexing and looking for space and punctuation.
    * <br>
    * <br>
    * 
    */
   public static final int STATE_09_WORD_FX                     = 1 << 8;

   /**
    * Set when at least one dynamic style is impacting at least one character.
    */
   public static final int STATE_10_ACTIVE_DYNAMIC_STYLE        = 1 << 9;

   public static final int STATE_11_DIFFERENT_FONTS             = 1 << 10;

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
    * Single line no breaks, no fx.
    * <br>
    * <br>
    * This is the straight forward line drawing. Are {@link StringMetrics} fully computed?
    * <br>
    * We don't really need them until editmodule requires specific char positions
    * <br>
    * This will be used temporarily for the line shape mask.
    * <br>
    * <br>
    * 
    */
   public static final int TYPE_0_SINGLE_LINE                   = 0;

   /**
    * There is a {@link StringFx} but no breaks
    */
   public static final int TYPE_1_SINGLE_LINE_FX                = 1;

   /**
    * Fx with Image/Figures
    */
   public static final int TYPE_2_SINGLE_LINE_FX_IMG            = 1;

   /**
    * Rectangular area breaks the text no special effect.
    * <br>
    * <br>
    * Natural \n
    * \t Tabs to spaces
    * 
    */
   public static final int TYPE_2_BREAKS                        = 2;

   /**
    * Fx and breaks
    */
   public static final int TYPE_3_BREAKS_FX                     = 3;

   public static final int TYPE_7_LINE_BREAKS_WORD_BREAKS_FX    = 7;

}
