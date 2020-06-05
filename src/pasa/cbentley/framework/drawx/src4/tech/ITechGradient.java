/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.functions.Function;
import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;
import pasa.cbentley.framework.drawx.src4.color.GradientFunction;

/**
 * Specification of the Gradient architecture.
 * <br>
 * <br>
 * A {@link GradientFunction} creates the colors from the root color.
 * <br>
 * <br>
 * Relation to {@link ITechFigure#FIG__OFFSET_05_DIR1} ?
 * <br>
 * <br>
 * 
 * @author Charles-Philip Bentley
 *
 */
public interface ITechGradient extends ITechByteObject {

   public static final int GRADIENT_PRE_0_NONE                       = 0;

   public static final int GRADIENT_PRE_1_0                          = 1;

   public static final int GRADIENT_PRE_2_50                         = 2;

   public static final int GRADIENT_PRE_3_100                        = 3;

   /**
    * 1 byte for flag
    * 1 byte for type
    * 1 byte for sec
    * 4 bytes for color
    * 1 byte for Flaz
    * 1 byte for flag channel
    * 1 byte for flagK
    * 1 byte for stepping
    * 1 byte for directional data
    */
   public static final int GRADIENT_BASIC_SIZE                       = A_OBJECT_BASIC_SIZE + 17;

   /**
    * Flags for easily switch between two opposing types. <br>
    * <li>horiz and vertical
    * <br>
    * <br>
    * Simple way of using directional gradient
    */
   public static final int GRADIENT_FLAG_1_SWITCH_2TYPES             = 1;

   /**
    * A {@link Function} will generate the colors based on computed gradient size.
    * <br>
    * 
    * Gradient function is defined by a ByteObject definition.
    */
   public static final int GRADIENT_FLAG_2_EXTERNAL_FUNCTION         = 2;

   /**
    * Use the 3rd color defined
    */
   public static final int GRADIENT_FLAG_3_THIRD_COLOR               = 4;

   /**
    * When this flag is set, the alpha value of the gradient colors are used
    */
   public static final int GRADIENT_FLAG_4_USEALPHA                  = 8;

   /**
    * Set when an array of colors {@link IBOTypesBOC#TYPE_007_LIT_ARRAY_INT} is set to the Gradient
    * definition.
    * <br>
    * <br>
    * 
    */
   public static final int GRADIENT_FLAG_5_INT_ARRAY                 = 16;

   /**
    * Use wire method instead of fill
    */
   public static final int GRADIENT_FLAGX_5_WIRE                     = 16;

   /**
    * Set when color.
    * a 50 position does a loop already. why using this flag?
    * When using more than 2 colors or a function like darken/lighten,
    * once black/white reached, it goes back to original color, loops until gradient size is reached.
    * <br>
    * <br>
    * For {@link ITechGradient#GRADIENT_FLAG_3_THIRD_COLOR} and {@link ITechGradient#GRADIENT_FLAG_5_INT_ARRAY},
    * the last color will be the starting color.
    */
   public static final int GRADIENT_FLAG_6_LOOP                      = 32;

   /**
    * Set when there is an artifact definition. Not all gradient types support artifact support
    * <br>
    */
   public static final int GRADIENT_FLAG_7_ARTIFACTS                 = 64;

   /**
    * Swaps primary and secondary colors.
    */
   public static final int GRADIENT_FLAG_8_REVERSE                   = 128;

   /**
    * 
    */
   public static final int GRADIENT_FLAGC_1_CH_A                     = 1;

   /**
    * If set, gradient does not impact red channel
    */
   public static final int GRADIENT_FLAGC_2_CH_R                     = 2;

   public static final int GRADIENT_FLAGC_3_CH_G                     = 4;

   public static final int GRADIENT_FLAGC_4_CH_B                     = 8;

   public static final int GRADIENT_FLAGC_5_CHX_A                    = 16;

   public static final int GRADIENT_FLAGC_6_CHX_R                    = 32;

   public static final int GRADIENT_FLAGC_7_CHX_G                    = 64;

   public static final int GRADIENT_FLAGC_8_CHX_B                    = 128;

   /**
    * Primary color starts at 0.
    * Secondary color start at Sec
    */
   public static final int GRADIENT_FLAGK_1_FULL_LEFT                = 1;

   /**
    * black to white, then white till the end
    * black to Sec then black to white
    * reverse (swaps primary and secondary color)
    * white to black, then black till the end
    */
   public static final int GRADIENT_FLAGK_2_FULL_RIGHT               = 2;

   /**
    * Fine grain control of the colors in the gradient at the first part. <br>
    * When this flag is set, the first part of the gradient exclude the primary color from the gradient.<br>
    * The first gradient color is the second color that would have been used. <br>
    * White to Black = First color is white little more black.
    */
   public static final int GRADIENT_FLAGK_3_PART1_EXCLUDE_LEFT       = 4;

   /**
    * Exclude secondary color in first part
    */
   public static final int GRADIENT_FLAGK_4_PART1_EXCLUDE_RIGHT      = 8;

   /**
    * Exlucde the secondary color in the second part
    */
   public static final int GRADIENT_FLAGK_5_PART2_EXCLUDE_LEFT       = 16;

   /**
    * Excludes the last color in the second part.
    * <br>
    * Usually the primary color. Tertiary if there is one
    */
   public static final int GRADIENT_FLAGK_6_PART2_EXCLUDE_RIGHT      = 32;

   public static final int GRADIENT_FLAGX_1_CHAIN                    = 1;

   public static final int GRADIENT_FLAGX_2_CHAIN                    = 2;

   /**
    * 
    */
   public static final int GRADIENT_FLAGX_3_RAW                      = 1 << 2;

   /**
    * <li>{@link ITechGradient#GRADIENT_FLAG_1_SWITCH_2TYPES}
    * <li>{@link ITechGradient#GRADIENT_FLAG_3_THIRD_COLOR}
    * <li>{@link ITechGradient#GRADIENT_FLAG_4_USEALPHA}
    * <li>{@link ITechGradient#GRADIENT_FLAG_8_REVERSE}
    * 
    */
   public static final int GRADIENT_OFFSET_01_FLAG                   = A_OBJECT_BASIC_SIZE;

   /**
    * Flags for Full Left/Right and Excludes
    */
   public static final int GRADIENT_OFFSET_02_FLAGK_EXCLUDE          = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Flags for excluding channels from the gradient. 
    * <br>
    * By default no channel is excluded. So even alpha channel will be gradiented. when
    * {@link ITechGradient#GRADIENT_FLAG_4_USEALPHA} is set to true.
    */
   public static final int GRADIENT_OFFSET_03_FLAGC_CHANNELS         = A_OBJECT_BASIC_SIZE + 2;

   /**
    * Secondary color to which the Gradient code will tend.
    * <br>
    * Input Color from Figure will enable to compute Gradient function.
    */
   public static final int GRADIENT_OFFSET_04_COLOR4                 = A_OBJECT_BASIC_SIZE + 3;

   /**
    * value between 0 and 100 <br>
    * at 0 gradient color starts at secondary and goes to primary.<br>
    * at 100 gradient color starts at primary and goes to secondary.<br>
    * For values in between, separates the area of a Grad<br>
    * Value is the percentage of length for the first gradient. The inverse is left for the <br>
    * second gradient<br>
    * 
    */
   public static final int GRADIENT_OFFSET_05_SEC1                   = A_OBJECT_BASIC_SIZE + 7;

   /**
    * External value type given by the Figure's code
    * <br>
    * For example in a rectangle, we have square type of gradient, or vertical or horizontal.
    */
   public static final int GRADIENT_OFFSET_06_TYPE1                  = A_OBJECT_BASIC_SIZE + 8;

   /**
    * Depending on the context:
    * 
    * <li>with input dimension: Number of pixels in each gradient step.
    * <li>without input dimension = Number of steps
    * <br>
    * <br>
    * In the first case, stepping can be made random from an array of choice
    */
   public static final int GRADIENT_OFFSET_07_STEP1                  = A_OBJECT_BASIC_SIZE + 9;

   /**
    * Offset at which to start the gradient. last color will be offset-1 treating the color array
    * as circular.
    * 
    * Flag controlled with {@link ITechGradient#GRADIENT_FLAGX_6_OFFSET}
    */
   public static final int GRADIENT_OFFSET_08_OFFSET2                = A_OBJECT_BASIC_SIZE + 10;

   public static final int GRADIENT_OFFSET_09_FLAGX1                 = A_OBJECT_BASIC_SIZE + 12;

   /**
    * Grad Size explicitely defined. Using a {@link ISizer}
    */
   public static final int GRADIENT_OFFSET_10_GRADSIZE2              = A_OBJECT_BASIC_SIZE + 13;

   /**
    * Override the primary grad size for computing color gradient.
    * <br> Reuse those values according to
    * <li>{@link IFunction#FUN_COUNTER_OP_0_ASC}
    * <li>{@link IFunction#FUN_COUNTER_OP_3_UP_DOWN}
    * 
    */
   public static final int GRADIENT_OFFSET_11_FAKE_SIZE2             = A_OBJECT_BASIC_SIZE + 15;


   public static final int GRADIENT_TYPE_ELLIPSE_00_NORMAL           = 0;

   public static final int GRADIENT_TYPE_ELLIPSE_01_HORIZ            = 1;

   public static final int GRADIENT_TYPE_ELLIPSE_02_VERT             = 2;

   public static final int GRADIENT_TYPE_ELLIPSE_03_TOP_FLAMME       = 3;

   public static final int GRADIENT_TYPE_ELLIPSE_04_BOT_FLAMME       = 4;

   public static final int GRADIENT_TYPE_ELLIPSE_05_LEFT_FLAMME      = 5;

   public static final int GRADIENT_TYPE_ELLIPSE_06_RIGHT_FLAMME     = 6;

   public static final int GRADIENT_TYPE_ELLIPSE_07_CLOCHE_TOP       = 7;

   public static final int GRADIENT_TYPE_ELLIPSE_08_CLOCHE_BOT       = 8;

   public static final int GRADIENT_TYPE_ELLIPSE_09_CLOCHE_LEFT      = 9;

   public static final int GRADIENT_TYPE_ELLIPSE_10_CLOCHE_RIGHT     = 10;

   public static final int GRADIENT_TYPE_ELLIPSE_11_WATER_DROP_TOP   = 11;

   public static final int GRADIENT_TYPE_ELLIPSE_12_WATER_DROP_BOT   = 12;

   public static final int GRADIENT_TYPE_ELLIPSE_13_WATER_DROP_LEFT  = 13;

   public static final int GRADIENT_TYPE_ELLIPSE_14_WATER_DROP_RIGHT = 14;

   public static final int GRADIENT_TYPE_ELLIPSE_15_TOP_LEFT_BUBBLE  = 15;

   public static final int GRADIENT_TYPE_ELLIPSE_16_TOP_RIGHT_BUBBLE = 16;

   public static final int GRADIENT_TYPE_ELLIPSE_17_BOT_LEFT_BUBBLE  = 17;

   public static final int GRADIENT_TYPE_ELLIPSE_18_BOT_RIGHT_BUBBLE = 18;

   public static final int GRADIENT_TYPE_ELLIPSE_MAX_CK              = 18;

   public static final int GRADIENT_TYPE_ELLIPSE_MAX_MODULO          = 19;

   public static final int GRADIENT_TYPE_LOSANGE_0_SQUARE            = 0;

   public static final int GRADIENT_TYPE_LOSANGE_1_FULLVERTICAL      = 1;

   public static final int GRADIENT_TYPE_LOSANGE_2_FULLHORIZ         = 2;

   public static final int GRADIENT_TYPE_LOSANGE_3_FULLDIAGDOWN      = 3;

   public static final int GRADIENT_TYPE_LOSANGE_4_FULLDIAGUP        = 4;

   /**
    * Gradient on the Left Triangle
    */
   public static final int GRADIENT_TYPE_LOSANGE_5_LEFT              = 5;

   public static final int GRADIENT_TYPE_LOSANGE_6_RIGHT             = 6;

   public static final int GRADIENT_TYPE_LOSANGE_7_TOP               = 7;

   public static final int GRADIENT_TYPE_LOSANGE_8_BOT               = 8;

   public static final int GRADIENT_TYPE_LOSANGE_MAX_CK              = 8;

   public static final int GRADIENT_TYPE_LOSANGE_MAX_MODULO          = 9;

   /**
    * No Direction
    */
   public static final int GRADIENT_TYPE_RECT_00_SQUARE              = 0;

   /**
    * Vectorial direction Left-Right. To be used in horizontal figure
    */
   public static final int GRADIENT_TYPE_RECT_01_HORIZ               = 1;

   /**
    * Vectorial direction Top-Down. To be used in vertical figure
    */
   public static final int GRADIENT_TYPE_RECT_02_VERT                = 2;

   /**
    * Double directional figure
    */
   public static final int GRADIENT_TYPE_RECT_03_TOPLEFT             = 3;

   public static final int GRADIENT_TYPE_RECT_04_TOPRIGHT            = 4;

   public static final int GRADIENT_TYPE_RECT_05_BOTLEFT             = 5;

   public static final int GRADIENT_TYPE_RECT_06_BOTRIGHT            = 6;

   public static final int GRADIENT_TYPE_RECT_07_L_TOP               = 7;

   public static final int GRADIENT_TYPE_RECT_08_L_BOT               = 8;

   public static final int GRADIENT_TYPE_RECT_09_L_LEFT              = 9;

   public static final int GRADIENT_TYPE_RECT_10_L_RIGHT             = 10;

   public static final int GRADIENT_TYPE_RECT_11_TRIG_TOP_LEFT       = 11;

   public static final int GRADIENT_TYPE_RECT_12_TRIG_BOT_LEFT       = 12;

   public static final int GRADIENT_TYPE_RECT_MAX_CK                 = 10;

   public static final int GRADIENT_TYPE_RECT_MAX_MODULO             = 11;

   /**
    * The normal gradient
    */
   public static final int GRADIENT_TYPE_TRIG_00_TENT                = 0;

   /**
    * Variation of Silex that draws a silex shape when H is bigger than 2 * base.
    * Goes outside
    */
   public static final int GRADIENT_TYPE_TRIG_01_TENT_JESUS          = 1;

   /**
    * Just the base is diminishing
    */
   public static final int GRADIENT_TYPE_TRIG_02_TOP_JESUS           = 2;

   public static final int GRADIENT_TYPE_TRIG_03_TUNNEL              = 3;

   /**
    * Builds a gradient from top and base
    */
   public static final int GRADIENT_TYPE_TRIG_04_FULL                = 4;

   public static final int GRADIENT_TYPE_TRIG_05_OPAQUEBASE          = 5;

   public static final int GRADIENT_TYPE_TRIG_06_OPAQUE_CENTER       = 6;

   public static final int GRADIENT_TYPE_TRIG_07_ARROW               = 7;

   public static final int GRADIENT_TYPE_TRIG_08_NORMAL              = 8;

   public static final int GRADIENT_TYPE_TRIG_09_HALO                = 9;

   /**
    * draws mini triangles
    */
   public static final int GRADIENT_TYPE_TRIG_10_SWIPE               = 10;

   public static final int GRADIENT_TYPE_TRIG_MAX_CK                 = 9;

   public static final int GRADIENT_TYPE_TRIG_MAX_MODULO             = 7;

   /**
    * enables override with {@link ITechGradient#GRADIENT_OFFSET_10_GRADSIZE2}
    * 
    */
   public static final int GRADIENT_FLAGX_7_GRADSIZE                 = 1 << 6;

   public static final int GRADIENT_FLAGX_8_MANY_TYPES               = 1 << 7;

   /**
    * enables override with {@link ITechGradient#GRADIENT_OFFSET_08_OFFSET2}
    */
   public static final int GRADIENT_FLAGX_6_OFFSET                   = 1 << 5;

}
