package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

/**
 * Style definition is composed of  3 logical levels : 
 * <li>content = describe how to render content.
 * <li>bg = describes the figures to draw before/below the content.
 * <li>fg = describes the figures to draw after/over the content.
 * <br>
 * <br>
 * 
 * <b>Content</b>:
 * <br>
 * Uses the Box model
 * <li>anchor 
 * <li>padding 
 * <li>border
 * <li>margin
 * <li>Content FX Effects : text effect for strings
 * <br>
 * <br>
 * 
 * <b>Background</b>:
 * <br>
 * 4 sheets of figures numbered from 1 to 4
 * <br>
 * <br>
 * <b>Foreground</b>:
 * <br>
 * 4 sheets of figures numbered from 1 to 4
 * <br>
 * <br>
 * 
 * <b>Filters</b>
 * <br>
 * Filters on the 3 Logical Layers:<br>
 * 5 types: 
 * <li>bg
 * <li>content
 * <li>fg
 * <li>bg + content
 * <li>bg + content + fg (same as filter on drawable) 
 * <br>
 * <br>
 * <b>Anchoring</b>:
 * <br>
 * For all BGs,FGs and content layer, can be anchored at 
 * <li>border
 * <li>margin
 * <li>content
 * <li>padding
 * <br>
 * <br>
 * <b>Structure</b>: <br>
 * Style components are stored in the ByteObject[] array consecutively or at a static pointer position.
 * <br>
 * 
 * <br>
 * <b>Examples of use</b>:
 * <br>
 * Selected style may typically 
 * <li>start an animation on a given bg layer
 * <li>modify the main txt effect colors etc.
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 */
public interface IStyle extends ITechByteObject {
   /**
    * Default anchoring for style layers.
    * <br>
    * That layers will draw starting at the border position
    */
   public static final int STYLE_ANC_0BORDER                = 0;

   /**
    * That layers will draw starting at the margin position
    */
   public static final int STYLE_ANC_1MARGIN                = 1;

   /**
    * That layers will draw starting at the content position
    */
   public static final int STYLE_ANC_2CONTENT               = 2;

   /**
    * That layers will draw starting at the padding position
    */
   public static final int STYLE_ANC_3PADDING               = 3;

   /**
    * 1 byte flagv
    * 1 byte flagg
    * 1 byte flagp
    * 1 byte flag perf
    * 1 byte
    * 
    */
   public static final int STYLE_BASIC_SIZE                 = A_OBJECT_BASIC_SIZE + 8;

   /**
    * Set if at least 1 Bg layer
    */
   public static final int STYLE_FLAG_PERF_1BG              = 1;

   /**
    * Set if at least 1 Fg layer
    */
   public static final int STYLE_FLAG_PERF_2FG              = 2;

   /**
    * Set if at least 1 style filter
    */
   public static final int STYLE_FLAG_PERF_3FILTERS         = 4;

   /**
    * Set if at least 1 animation
    */
   public static final int STYLE_FLAG_PERF_4ANIMS           = 8;

   /**
    * When the style has 24 sized array where each
    */
   public static final int STYLE_FLAG_PERF_5FULL            = 16;

   /**
    * When the style has at least one bg or fg figure that is extra boundary
    * {@link IDrw#FIG_FLAGP_2EXTRA_BOUNDARY}
    */
   public static final int STYLE_FLAG_PERF_6EXTRA_BOUNDARY  = 32;

   /**
    * Style elements are stored with a static pointer
    */
   public static final int STYLE_FLAG_PERF_8_STATIC_POINTER = 128;

   /**
    * Filter applied on bg layers together.
    */
   public static final int STYLE_FLAGF_1FILTER_BG           = 1;

   /**
    * Filter applied on content.
    * <br>
    * Uses a cache when possible.
    */
   public static final int STYLE_FLAGF_2FILTER_CONTENT      = 2;

   /**
    * Applies only if style has fg layers.
    */
   public static final int STYLE_FLAGF_3FILTER_FG           = 4;

   /**
    * Applied on background concatenated with content. 
    * <br>
    * <br>
    * Equal to {@link ByteObject#STYLE_FLAGF_5FILTER_ALL} when there are no FG layers.
    */
   public static final int STYLE_FLAGF_4FILTER_BG_CONTENT   = 8;

   /**
    * This filter is managed at the level of the drawable. <br>
    * An Image cache is created and the item is drawn on it. then filter is applied.
    * When using this filter, a full {@link RgbImage} cache is used.
    */
   public static final int STYLE_FLAGF_5FILTER_ALL          = 16;

   /**
    * Entry animation at the style level.
    * Animation at the layer figure level animates the figure,
    * Here, we animate the style definition, like modifying the
    * value of padding.
    */
   public static final int STYLE_FLAGF_6ANIM_ENTRY          = 32;

   /**
    * Main animation at the style level.
    */
   public static final int STYLE_FLAGF_7ANIM_MAIN           = 64;

   /**
    * Exit animation at the style level.
    */
   public static final int STYLE_FLAGF_8ANIM_EXIT           = 128;

   public static final int STYLE_FLAGG_1BG                  = 1;

   public static final int STYLE_FLAGG_2BG                  = 2;

   public static final int STYLE_FLAGG_3BG                  = 4;

   public static final int STYLE_FLAGG_4BG                  = 8;

   /**
    * First Fg layer
    */
   public static final int STYLE_FLAGG_5FG                  = 16;

   public static final int STYLE_FLAGG_6FG                  = 32;

   public static final int STYLE_FLAGG_7FG                  = 64;

   public static final int STYLE_FLAGG_8FG                  = 128;

   public static final int STYLE_FLAGV_1CONTENT             = 1;

   public static final int STYLE_FLAGV_2ANCHOR              = 2;

   public static final int STYLE_FLAGV_3PADDING             = 4;

   public static final int STYLE_FLAGV_4BORDER              = 8;

   public static final int STYLE_FLAGV_5MARGIN              = 16;

   public static final int STYLE_FLAGV_6ANIMATIONS          = 32;

   /**
    * Base flag
    */
   public static final int STYLE_OFFSET_1FLAGV              = A_OBJECT_BASIC_SIZE;

   /**
    * Flags for bg and fg layers
    */
   public static final int STYLE_OFFSET_2FLAGG              = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Flag for filters and animations
    */
   public static final int STYLE_OFFSET_3FLAGF              = A_OBJECT_BASIC_SIZE + 2;

   /**
    * For quickly know if there are any Bgs, Fgs, Filters, Anims
    */
   public static final int STYLE_OFFSET_4FLAG_PERF          = A_OBJECT_BASIC_SIZE + 3;

   /**
    * Define which area to be used for figures in the background layers.
    * 2 bits for each layer. 4 draw area positions <br>
    * <li>00 = at border (default)
    * <li>01 = at padding
    * <li>10 = at content
    * <li>11 = at margin
    */
   public static final int STYLE_OFFSET_5BG_POINTS1         = A_OBJECT_BASIC_SIZE + 4;

   /**
    * see STYLE_OFFSET_BG_POINTS1
    */
   public static final int STYLE_OFFSET_6FG_POINTS1         = A_OBJECT_BASIC_SIZE + 5;
}
