/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMerge;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOAnchor;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.tech.ITechStyle;
import pasa.cbentley.layouter.src4.tech.IBOTblr;

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
public interface IBOStyle extends IByteObject {
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
    * Flag set when there is a {@link IBOFigString}
    */
   public static final int STYLE_FLAG_A_1_CONTENT           = 1 << 0;

   /**
    * Flag set when there is a {@link IBOAnchor}
    */
   public static final int STYLE_FLAG_A_2_ANCHOR            = 1 << 1;

   /**
    * Flag set when there is a {@link IBOTblr} for padding
    */
   public static final int STYLE_FLAG_A_3_PADDING           = 1 << 2;

   /**
    * Flag set when there is a {@link IBOTblr} for border
    */
   public static final int STYLE_FLAG_A_4_BORDER            = 1 << 3;

   /**
    * Flag set when there is a {@link IBOTblr} for margin
    */
   public static final int STYLE_FLAG_A_5_MARGIN            = 1 << 4;

   public static final int STYLE_FLAG_B_1_BG                = 1 << 0;

   public static final int STYLE_FLAG_B_2_BG                = 1 << 1;

   public static final int STYLE_FLAG_B_3_BG                = 1 << 2;

   public static final int STYLE_FLAG_B_4_BG                = 1 << 3;

   /**
    * First Fg layer
    */
   public static final int STYLE_FLAG_B_5_FG                = 1 << 4;

   public static final int STYLE_FLAG_B_6_FG                = 1 << 5;

   public static final int STYLE_FLAG_B_7_FG                = 1 << 6;

   public static final int STYLE_FLAG_B_8_FG                = 1 << 7;

   /**
    * Entry animation at the style level.
    * Animation at the layer figure level animates the figure,
    * Here, we animate the style definition, like modifying the
    * value of padding.
    */
   public static final int STYLE_FLAG_C_6_ANIM_ENTRY        = 1 << 5;

   /**
    * Main animation at the style level.
    */
   public static final int STYLE_FLAG_C_7_ANIM_MAIN         = 1 << 6;

   /**
    * Exit animation at the style level.
    */
   public static final int STYLE_FLAG_C_8_ANIM_EXIT         = 1 << 7;

   /**
    * Filter applied on bg layers together.
    */
   public static final int STYLE_FLAG_F_1_FILTER_BG         = 1 << 0;

   /**
    * Filter applied on content.
    * <br>
    * Uses a cache when possible.
    */
   public static final int STYLE_FLAG_F_2_FILTER_CONTENT    = 1 << 1;

   /**
    * Applies only if style has fg layers.
    */
   public static final int STYLE_FLAG_F_3_FILTER_FG         = 1 << 2;

   /**
    * Applied on background concatenated with content. 
    * 
    * Equal to {@link ByteObject#STYLE_FLAG_F_5_FILTER_ALL} when there are no FG layers.
    */
   public static final int STYLE_FLAG_F_4_FILTER_BG_CONTENT = 1 << 3;

   /**
    * This filter is managed at the level of the drawable. <br>
    * An Image cache is created and the item is drawn on it. then filter is applied.
    * When using this filter, a full {@link RgbImage} cache is used.
    */
   public static final int STYLE_FLAG_F_5_FILTER_ALL        = 1 << 4;

   /**
    * Set if at least 1 Bg layer
    */
   public static final int STYLE_FLAG_PERF_1_BG             = 1 << 0;

   /**
    * Set if at least 1 Fg layer
    */
   public static final int STYLE_FLAG_PERF_2_FG             = 1 << 1;

   /**
    * Set if at least 1 style filter
    */
   public static final int STYLE_FLAG_PERF_3_FILTERS        = 1 << 2;

   /**
    * Set if at least 1 animation
    */
   public static final int STYLE_FLAG_PERF_4_ANIMS          = 1 << 3;

   /**
    * When the style has 24 sized array where each
    */
   public static final int STYLE_FLAG_PERF_5_FULL           = 1 << 4;

   /**
    * When the style has at least one bg or fg figure that is extra boundary
    * {@link IDrw#FIG_FLAGP_2_EXTRA_BOUNDARY}
    */
   public static final int STYLE_FLAG_PERF_6_EXTRA_BOUNDARY = 1 << 5;

   /**
    * Style elements are stored with a static pointer
    */
   public static final int STYLE_FLAG_PERF_8_STATIC_POINTER = 1 << 7;

   /**
    * Flags the style definition as being transparent, null values being transparent
    * 
    * How to tell if transparent definition acutally wants to delete a field ?
    * 
    * Implementation is done by {@link StyleOperator#mergeStyle(ByteObject, ByteObject)}
    * 
    * {@link IBOMerge}. It is used to forward nulls to the root. Presence.
    * 
    * by default
    * 
    * The merging of styles
    * <li> Every style element is merged with each other
    * <li> 
    */
   public static final int STYLE_FLAG_X_1_INCOMPLETE        = 1 << 0;

   /**
    * Set when the style is a result of a merge method
    */
   public static final int STYLE_FLAG_X_2_MERGED            = 1 << 1;

   /**
    * When merge changes the structure
    */
   public static final int STYLE_FLAG_X_3_MERGED_STRUCT     = 1 << 2;

   public static final int STYLE_FLAG_X_4_MERGED_ANIM       = 1 << 3;

   /**
    * Flags flagging existence of the following {@link ByteObject}s 
    * 
    * <li> {@link IBOStyle#STYLE_FLAG_A_1_CONTENT}
    * <li> {@link IBOStyle#STYLE_FLAG_A_2_ANCHOR}
    * <li> {@link IBOStyle#STYLE_FLAG_A_3_PADDING}
    * <li> {@link IBOStyle#STYLE_FLAG_A_4_BORDER}
    * <li> {@link IBOStyle#STYLE_FLAG_A_5_MARGIN}
    */
   public static final int STYLE_OFFSET_1_FLAG_A            = A_OBJECT_BASIC_SIZE;

   /**
    * Flags for bg and fg layers
    * 
    *  <li> {@link IBOStyle#STYLE_FLAG_B_1_BG}
    *  <li> {@link IBOStyle#STYLE_FLAG_B_2_BG}
    *  <li> {@link IBOStyle#STYLE_FLAG_B_3_BG}
    *  <li> {@link IBOStyle#STYLE_FLAG_B_4_BG}
    *  <li> {@link IBOStyle#STYLE_FLAG_B_5_FG}
    *  <li> {@link IBOStyle#STYLE_FLAG_B_6_FG}
    *  <li> {@link IBOStyle#STYLE_FLAG_B_7_FG}
    *  <li> {@link IBOStyle#STYLE_FLAG_B_8_FG}
    */
   public static final int STYLE_OFFSET_2_FLAG_B            = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Flags for animations
    *  
    *  <li> {@link IBOStyle#STYLE_FLAG_C_6_ANIM_ENTRY}
    *  <li> {@link IBOStyle#STYLE_FLAG_C_7_ANIM_MAIN}
    *  <li> {@link IBOStyle#STYLE_FLAG_C_8_ANIM_EXIT}
    */
   public static final int STYLE_OFFSET_3_FLAG_C            = A_OBJECT_BASIC_SIZE + 2;

   /**
    * Flag for filters 
    * 
    *  <li> {@link IBOStyle#STYLE_FLAG_F_1_FILTER_BG}
    *  <li> {@link IBOStyle#STYLE_FLAG_F_2_FILTER_CONTENT}
    *  <li> {@link IBOStyle#STYLE_FLAG_F_3_FILTER_FG}
    *  <li> {@link IBOStyle#STYLE_FLAG_F_4_FILTER_BG_CONTENT}
    *  <li> {@link IBOStyle#STYLE_FLAG_F_5_FILTER_ALL}
    */
   public static final int STYLE_OFFSET_4_FLAG_F            = A_OBJECT_BASIC_SIZE + 3;

   /**
    * 
    */
   public static final int STYLE_OFFSET_5_FLAG_X            = A_OBJECT_BASIC_SIZE + 4;

   /**
    * For quickly know if there are any Bgs, Fgs, Filters, Anims
    * 
    *  <li> {@link IBOStyle#STYLE_FLAG_PERF_1_BG}
    *  <li> {@link IBOStyle#STYLE_FLAG_PERF_2_FG}
    *  <li> {@link IBOStyle#STYLE_FLAG_PERF_3_FILTERS}
    *  <li> {@link IBOStyle#STYLE_FLAG_PERF_4_ANIMS}
    *  <li> {@link IBOStyle#STYLE_FLAG_PERF_5_FULL}
    *  <li> {@link IBOStyle#STYLE_FLAG_PERF_6_EXTRA_BOUNDARY}
    *  <li> {@link IBOStyle#STYLE_FLAG_PERF_8_STATIC_POINTER}
    */
   public static final int STYLE_OFFSET_6_FLAG_PERF         = A_OBJECT_BASIC_SIZE + 5;

   /**
    * Define which area to be used for figures in the background layers.
    * 2 bits for each layer. 4 draw area positions <br>
    * <li>00 = at border (default)
    * <li>01 = at padding
    * <li>10 = at content
    * <li>11 = at margin
    * 
    * Also related to
    * <li>{@link ITechStyle#STYLE_ANC_0_BORDER}
    * <li>{@link ITechStyle#STYLE_ANC_1_MARGIN}
    * <li>{@link ITechStyle#STYLE_ANC_2_CONTENT}
    * <li>{@link ITechStyle#STYLE_ANC_3_PADDING}
    */
   public static final int STYLE_OFFSET_7_BG_POINTS1        = A_OBJECT_BASIC_SIZE + 6;

   /**
    * @see STYLE_OFFSET_BG_POINTS1 but for foreground layers
    */
   public static final int STYLE_OFFSET_8_FG_POINTS1        = A_OBJECT_BASIC_SIZE + 7;
}
