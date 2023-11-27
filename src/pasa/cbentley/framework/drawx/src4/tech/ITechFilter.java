/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;
import pasa.cbentley.framework.drawx.src4.factories.FilterOperator;

public interface ITechFilter extends ITechByteObject {

   /**
    * 1 byte type
    * 1 byte flag
    * 1 byte flagp
    * 2 bytes for value/function id
    * 4 bytes color
    */
   public final static int FILTER_BASIC_SIZE                 = A_OBJECT_BASIC_SIZE + 22;

   /**
    * when applying a filter to a primitive figure, the default color will be white
    * However if the figure has white, the filter must use a different color defined
    * in the mask color
    */
   public static final int FILTER_FLAG_1_BG_COLOR            = 1;

   public static final int FILTER_FLAG_2_BLENDER                     = 2;

   public static final int FILTER_FLAG_3                     = 4;

   public static final int FILTER_FLAG_4_REPLACE             = 8;

   /**
    * Switch for TouchFilter
    */
   public static final int FILTER_FLAG_5_OR48                = 16;

   /**
    * If set, Function is linked with an ID.
    * <br>
    * this allows to inject a Interface
    */
   public static final int FILTER_FLAG_6_FUNCTION_ID         = 32;

   public static final int FILTER_FLAG_7_EXACT_MATCH         = 64;

   public static final int FILTER_FLAG_8                     = 128;

   /**
    * used as o48 for Touch filters
    */
   public static final int FILTER_FLAGP_1_TOP                = 1;

   public static final int FILTER_FLAGP_2_BOT                = 2;

   public static final int FILTER_FLAGP_3_LEFT               = 4;

   public static final int FILTER_FLAGP_4_RIGHT              = 8;

   public static final int FILTER_FLAGP_5_CENTER             = 16;

   public static final int FILTER_ID_0_NONE                  = 0;

   public static final int FILTER_ID_1_PRE                   = 1;

   public static final int FILTER_ID_2_POST                  = 2;

   /**
    * The type of alpha filter
    * <li>{@link ITechFilter#FILTER_TYPE_00_FUNCTION_ALL}
    * <li>{@link ITechFilter#FILTER_TYPE_01_GRAYSCALE}
    * <li>{@link ITechFilter#FILTER_TYPE_02_BILINEAR}
    */
   public static final int FILTER_OFFSET_01_TYPE1            = A_OBJECT_BASIC_SIZE;

   public static final int FILTER_OFFSET_02_FLAG1            = A_OBJECT_BASIC_SIZE + 1;

   /**
    * TBLR flags
    * <li> {@link ITechFilter#FILTER_TYPE_14_BLEND_SELF} for blending pixels
    */
   public static final int FILTER_OFFSET_03_FLAGP1           = A_OBJECT_BASIC_SIZE + 2;

   /**
    * Function ID or Pointer to ByteObject function definition
    * For Alpha, Alpha value is here
    */
   public static final int FILTER_OFFSET_04_FUNCTION2        = A_OBJECT_BASIC_SIZE + 3;

   /**
    * TouchColor/Alpha Destination Color
    */
   public static final int FILTER_OFFSET_05_COLOR4           = A_OBJECT_BASIC_SIZE + 5;

   /**
    * Color to be used when filling the empty and that will be
    */
   public static final int FILTER_OFFSET_06_MASK_BG_COLOR4   = A_OBJECT_BASIC_SIZE + 9;

   /**
    * <li> {@link ITechFilter#FILTER_ID_0_NONE}
    * <li> {@link ITechFilter#FILTER_ID_1_PRE}
    * <li> {@link ITechFilter#FILTER_ID_2_POST}
    */
   public static final int FILTER_OFFSET_07_ID1              = A_OBJECT_BASIC_SIZE + 13;

   /**
    * <li> {@link ITechFilter#FILTER_TYPE_14_BLEND_SELF} for transformation
    */
   public static final int FILTER_OFFSET_08_EXTRA1           = A_OBJECT_BASIC_SIZE + 14;

   public static final int FILTER_OFFSET_10_BLEND1           = A_OBJECT_BASIC_SIZE + 15;

   public static final int FILTER_OFFSET_11_BLEND_ALPHA1     = A_OBJECT_BASIC_SIZE + 16;

   public static final int FILTER_OFFSET_12_W2               = A_OBJECT_BASIC_SIZE + 17;

   public static final int FILTER_OFFSET_13_H2               = A_OBJECT_BASIC_SIZE + 19;

   /**
    * 
    */
   public static final int FILTER_TOUCH_BASIC_SIZE           = FILTER_BASIC_SIZE;

   /**
    * Simple Filter function applies to all pixels. 
    * F(pixelRGB) => alpha
    */
   public static final int FILTER_TYPE_00_FUNCTION_ALL       = 0;

   /**
    * Put pixels into their shade of grey
    */
   public static final int FILTER_TYPE_01_GRAYSCALE          = 1;

   /**
    * {@link FilterOperator#filterBiLinear2(int[], int, int, int, int, int, int, ByteObject)}
    */
   public static final int FILTER_TYPE_02_BILINEAR           = 2;

   public static final int FILTER_TYPE_03_ALPHA_TO_COLOR     = 3;

   /**
    * 
    */
   public static final int FILTER_TYPE_04_SIMPLE_ALPHA       = 4;

   /**
    * TODO Color repeater blender.
    * Iterate over each pixels. When a pixel is accepted with the {@link IAcceptor} of the filter,
    * (The pixel channels match the given points within a tolerance. 128 +- 2
    * <li>{@link ITechFilter#FILTER_OFFSET_04_FUNCTION2} is blendop
    * Takes existing color pixel and repeat them in the neighbourhood
    * <br>
    * <br>
    * {@link ITechFilter#FILTER_OFFSET_05_COLOR4}
    * 
    * It happens for only a given color = taken from random pixel
    * For all pixels, but a repeatability function controls the probability a pixel is repeated.
    * <li>An {@link IAcceptor} can be used to filter acceptable pixels to be processed
    */
   public static final int FILTER_TYPE_05_REPEAT_PIXEL       = 5;

   /**
    * 
    */
   public static final int FILTER_TYPE_06_STEP_SMOOTH        = 6;

   /**
    * Function applies from Top,Bottom, Left and Right 
    * of image.
    * Penetration size. applies to mask color or not
    * threshold or additive
    */
   public static final int FILTER_TYPE_07_TBLR               = 7;

   /**
    * Iterates over each pixel. When pixel different from touch color is found,
    * <br>
    * The filter counts the number of touch color pixels in the 4/8 {@link ITechFilter#FILTER_FLAG_5_OR48} adjacent pixels
    * <br>
    * Then the function f(pixel,countColor) 
    * <br>
    * {@link ITechFilter#FILTER_OFFSET_05_COLOR4} is the touch color.
    * <br>
    * Alpha values are set for pixels touching other
    * F(pixel + 4neighboursRGB) => alpha
    * F(pixel + 8neighboursRGB) => alpha
    * <br>
    * <br>
    * The filter iterates over each pixel, The function looks for the count of touch colors
    * <br>when the RGB value match, the filter function is applied
    * to the adjacent pixels
    * <br>
    * the touch color. a pixel adjacent to 2 touchColors will have a function call
    * f(pixel,2). touch colors are not processed
    * <br>
    * This function is used to generate an anti-alias around a String figure
    */
   public static final int FILTER_TYPE_08_TOUCHES            = 8;

   /**
    * Does a similar end result as Touch filter. But here the intuition
    * is a falling pixel sticking as soon as it meets a condition.
    * Usually, once a non mask color is met.
    * The color of the sticking pixel is predefined or it may take a blend of the color of the surronding pixels
    */
   public static final int FILTER_TYPE_09_STICK              = 9;

   public static final int FILTER_TYPE_10_SEPIA              = 10;

   /**
    * Horizontal average.. takes pixels around a radius and average values, including
    * alpha. write those values in a new array. meaning computations don't affect
    */
   public static final int FILTER_TYPE_11_HORIZ_AVERAGE      = 11;

   public static final int FILTER_TYPE_12_HORIZ_AVERAGE_NEOM = 12;

   public static final int FILTER_TYPE_13_CHANNEL_MOD        = 13;

   /**
    * 
    * Blend itself with a transformation
    * <br>
    * <li>{@link ITechFilter#FILTER_OFFSET_04_FUNCTION2} is blendop
    * <li>{@link ITechFilter#FILTER_OFFSET_08_EXTRA1} is the trans
    */
   public static final int FILTER_TYPE_14_BLEND_SELF         = 14;

   public static final int FILTER_TYPE_CK_MAX                = 14;
}
