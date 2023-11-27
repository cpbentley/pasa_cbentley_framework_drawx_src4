/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;
import pasa.cbentley.framework.drawx.src4.engine.BlendOp;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

public interface ITechBlend extends ITechByteObject {

   /**
    * All alphas are treated equally in the blender
    * and are merged using the OVER function
    */
   public static final int ALPHA_0_OVER                   = 0;

   /**
    * The alphas are merged using the same function as the RGB components
    */
   public static final int ALPHA_1_MERGE                  = 1;

   /**
    * The merge ignores alpha values.
    * <br>
    * Return pixel's alpha is always 255.
    */
   public static final int ALPHA_2_255                    = 2;

   /**
    * Inverse function on the alpha
    */
   public static final int ALPHA_4_INVERSE                = 4;

   /**
    * The maximum alpha value between base and blend is taken
    */
   public static final int ALPHA_5_MAX                    = 5;

   /**
    * Takes the average of RGB values. Ignores 
    * 
    * light is open, dark is 
    */
   public static final int ALPHA_5_RGB_AVERAGE            = 8;

   /**
    * Takes the average of RGB values and invert it. Ignores 
    * 
    * light is transparent, dark is opaque 
    */
   public static final int ALPHA_5_RGB_AVERAGE_INVERSE    = 9;

   /**
    * The minimum alpha value between base and blend is taken
    */
   public static final int ALPHA_6_MIN                    = 6;

   public static final int ALPHA_CK_MAX                   = 0;

   public static final int BLEND_BASIC_SIZE               = A_OBJECT_BASIC_SIZE + 8;

   public static final int BLEND_FLAG_1_PORTER_ALPHA      = 1 << 0;

   public static final int BLEND_FLAG_2_SWAP_ALPHA        = 1 << 1;

   public static final int BLEND_FLAG_3_IGNORE_ALPHA      = 1 << 2;

   public static final int BLEND_FLAG_4_IGNORE_RED        = 1 << 3;

   public static final int BLEND_FLAG_5_IGNORE_GREEN      = 1 << 4;

   public static final int BLEND_FLAG_6_IGNORE_BLUE       = 1 << 5;

   /**
    * Operator to see if there is a composition to be made.
    * <br>
    * <li> {@link ITechBlend#OP_00_SRC_OVER}
    * <li> {@link ITechBlend#OP_01_SRC}
    * <li> {@link ITechBlend#OP_02_SRC_IN}
    * <li> {@link ITechBlend#OP_03_SRC_OUT}
    * <li> {@link ITechBlend#OP_04_SRC_ATOP}
    * <li> {@link ITechBlend#OP_05_DST_OVER}
    * <li> {@link ITechBlend#OP_06_DST}
    * <li> {@link ITechBlend#OP_07_DST_IN}
    * <li> {@link ITechBlend#OP_08_DST_OUT}
    * <li> {@link ITechBlend#OP_09_DST_ATOP}
    * <li> {@link ITechBlend#OP_10_XOR}
    * <br>
    * <br>
    * In some cases, there won't be composition.
    * <br>
    * The background of an {@link RgbImage} is deemed outside. By defaut 0.
    * <br>
    * One pixel value for destination is deemed empty
    * another pixel (mostly the same as destination) value for src is deemed empty. 
    * Composition occurs only if both pixels are not empty.
    */
   public static final int BLEND_OFFSET_01_DUFF_OP1       = A_OBJECT_BASIC_SIZE;

   /**
    * The alpha/opacity of the composition
    */
   public static final int BLEND_OFFSET_02_ALPHA1         = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Composition between 2 pixels.
    * {@link ITechBlend#BLENDING_00_OVER}
    * {@link ITechBlend#BLENDING_01_SRC}
    * {@link ITechBlend#BLENDING_02_DARKEN}
    * {@link ITechBlend#BLENDING_03_LIGHTEN}
    * {@link ITechBlend#BLENDING_04_MERGE_ARGB}
    */
   public static final int BLEND_OFFSET_03_TYPE2          = A_OBJECT_BASIC_SIZE + 2;

   /**
    * 
    * <li> {@link ITechBlend#OPACITY_00_SRC}
    * <li> {@link ITechBlend#OPACITY_01_MIN_OVERIDE_SRC}
    * 
    */
   public static final int BLEND_OFFSET_04_OPACITY_OP1    = A_OBJECT_BASIC_SIZE + 4;

   /**
    * Value between 0 and 255. Final opacity for the src layer compositing over the destination layer
    */
   public static final int BLEND_OFFSET_05_OPACITY1       = A_OBJECT_BASIC_SIZE + 5;

   /**
    * Little operator defining how channels are merged back
    */
   public static final int BLEND_OFFSET_06_COMPO_OP1      = A_OBJECT_BASIC_SIZE + 6;

   public static final int BLEND_OFFSET_07_FLAG1          = A_OBJECT_BASIC_SIZE + 7;

   /**
    * Blend Opaque pixels replace the base, semi-transparent pixels are merged.
    * <li>{@link BlendOp#blendPixel(int, int, int)}.
    * <li>Default behavior.
    * <li>Ignore Alpha mode since it defines its own mode by nature.
    */
   public static final int BLENDING_00_OVER               = 0;

   /**
    * Blend pixels replace destination.
    * <br>
    * <br>
    * Except for background color/ pseudo T color.
    * <br>
    * This is the behavior of opaque replacement except that it also applies to alpha value
    * 
    * Using this blending operator can be redundant with the porter-duff op
    */
   public static final int BLENDING_01_SRC                = 1;

   /**
    * Looks at each channel and selects the dest o
    * Base Pixels lighter than the blend color are replaced.<br>
    * Base pixel darker than the blend color do no change.
    * <br>
    * <br>
    * The darkest pixel of either the blend layer or the composition is used.
    * <br>
    * In effect takes the minimum of each RGB components in base and blend pixels.
    */
   public static final int BLENDING_02_DARKEN             = 30;

   /**
    * Base Pixels darker than the blend color are replaced.<br>
    * Base pixel lighter than the blend color do no change.
    * <br>
    * <br>
    * The lightest pixel of either the blend layer or the composition is used.
    * <br>
    * In effect takes the maximum of each RGB components in base and blend pixels.
    */
   public static final int BLENDING_03_LIGHTEN            = 31;

   /**
    * Take RGB channels and merge them. Used by competitive layers that both win.
    */
   public static final int BLENDING_04_MERGE_ARGB         = 4;

   /**
    * Only paints on the transparent part of the layer. = transhape?
    */
   public static final int BLENDING_05_BEHIND             = 5;

   /**
    * Inverse the value of each color channel
    */
   public static final int BLENDING_07_INVERSE            = 7;

   /**
    * Takes random pixels from both layers. With high opacity, most pixels are taken from the top layer.
    * With low opacity most pixels are taken from the bottom layer.
    */
   public static final int BLENDING_08_DISSOLVE           = 8;

   /**
    * 
    */
   public static final int BLENDING_09_INVERSE            = 9;

   /**
    * hue of blend color
    */
   public static final int BLENDING_10_HUE                = 10;

   /**
    * luminance of base color
    * hue and saturation of blend color
    */
   public static final int BLENDING_11_HUE_SAT            = 11;

   /**
    * saturation of base color
    * hue and saturation of blend color
    */
   public static final int BLENDING_12_HUE_LUM            = 12;

   /**
    * luminance and hue of base color
    * saturation of blend color
    */
   public static final int BLENDING_13_SATURATION         = 13;

   /**
    * saturation and lum of blend color
    */
   public static final int BLENDING_14_SAT_LUM            = 14;

   /**
    * luminance of blend color
    */
   public static final int BLENDING_15_LUMINANCE          = 15;

   /**
    * Each channel multiplies base color by blend color. Always darker color.
    * <br>
    * Multiplying any color with black produces black. <br>
    * Multiplying any color with white leaves color unchanged. <br>
    * 
    * 
    */
   public static final int BLENDING_16_MULTIPLY_BURN      = 16;

   /**
    * Looks each channel in base color to reflect the blend color by increasing the contrast.
    * <br>
    * Blending with white produces no change.
    */
   public static final int BLENDING_17_COLOR_BURN         = 17;

   /**
    * Looks each channel in base color to reflect the blend color by decreasing the brightness.
    * <br>
    * Blending with white produces no change.
    */
   public static final int BLENDING_18_LINEAR_BURN        = 18;

   public static final int BLENDING_19_SCREEN_DODGE       = 19;

   /**
    * Each channel and brightens the base color to reflect the blend color by decreasing the contrast.
    * <br>
    * Blending with black produces no change.
    */
   public static final int BLENDING_20_COLOR_DODGE        = 20;

   /**
    * Each channel and brightens the base color to reflect the blend color by increasing the brightness.
    * <br>
    * Blending with black produces no change.
    */
   public static final int BLENDING_21_LINEAR_DODGE       = 21;

   public static final int BLENDING_22_HARD_MIX           = 22;

   public static final int BLENDING_23_ADDITION           = 23;

   public static final int BLENDING_24_DIFFERENCE         = 24;

   /**
    * Each channel and multiplies the inverse of the blend and base colors. always a lighter color.
    * <br>
    * any color with black is unchanged. <br>
    * any color with white produces white.<br>
    * <br>
    * Effect of
    */
   public static final int BLENDING_25_DIVIDE             = 25;

   public static final int BLENDING_255_INVALID           = 255;

   public static final int BLENDING_26_                   = 26;

   /**
    * TODO Similar to difference. take the inverse of the difference.
    *  Instead of making colors darker, it will make them brighter.
    */
   public static final int BLENDING_27_EXCLUSION_NEGATION = 27;

   public static final int BLENDING_28_                   = 28;

   public static final int BLENDING_29_PIN_LIGHT          = 29;

   public static final int BLENDING_CK_MAX                = 31;

   /**
    * The source is composited over the destination. 
    * 
    * RGB components blending can be parametrized with
    * <li> {@link ITechBlend#BLENDING_00_OVER}
    * <li> {@link ITechBlend#BLENDING_01_SRC}
    * <li> {@link ITechBlend#BLENDING_02_DARKEN}
    * <li> {@link ITechBlend#BLENDING_10_HUE}
    * <li> {@link ITechBlend#BLENDING_11_HUE_SAT}
    * <li> {@link ITechBlend#BLENDING_12_HUE_LUM}
    * <li> {@link ITechBlend#BLENDING_19_SCREEN_DODGE}
    * 
    * <br>
    * <br>
    * The alpha blending is decided by
    * <li> {@link ITechBlend#ALPHA_0_OVER}
    * <li> {@link ITechBlend#ALPHA_2_255}
    * <li> {@link ITechBlend#ALPHA_1_MERGE}
    * <li> {@link ITechBlend#ALPHA_4_INVERSE}
    */
   public static final int OP_00_SRC_OVER                 = 0;

   /**
    * The source is copied to the destination (Porter-Duff Source rule). The destination is not used as input. 
    * 
    * Return the orange triangle and nothing else.
    * 
    * Inverse of {@link ITechBlend#OP_06_DST}
    */
   public static final int OP_01_SRC                      = 1;

   /**
    * The part of the source lying inside of the destination replaces the destination.
    * 
    * "The orange triangle colors in orange the opaque pixels of the letters."
    * 
    * What happens for semi tranparent pixels [0 < alpha values < 255] ?
    * <br>
    * But you still have the Opacity operator applied
    * <li> {@link ITechBlend#OPACITY_00_SRC}
    * <li> {@link ITechBlend#OPACITY_01_MIN_OVERIDE_SRC}
    * <li> {@link ITechBlend#OPACITY_02_MAX_OVERIDE_SRC}
    * <li> {@link ITechBlend#OPACITY_03_OVERIDE_SRC}
    */
   public static final int OP_02_SRC_IN                   = 2;

   /**
    * The part of the source lying outside of the destination replaces the destination.
    * <br>
    * "The orange triangle colors in orange the transparent pixels. Letter shape is now transparent"
    * 
    * Erase where there are destination pixels. Erasing is proportional to Opacity.
    * <br>
    * Destination pixels outside Source are kept. Ie empty src pixels don't replace. Should they?
    */
   public static final int OP_03_SRC_OUT                  = 3;

   /**
    * The part of the source lying inside of the destination is composited onto the destination.
    * 
    * If a destination pixel is empty.. the source is not composited at all!
    * 
    * The orange triangle is ARGB composited only where opaque letters pixel are located.
    */
   public static final int OP_04_SRC_ATOP                 = 4;

   /**
    * The destination is composited over the source and the result replaces the destination.
    * 
    * The blue letter is RGB composited over the orange triangle.
    * 
    */
   public static final int OP_05_DST_OVER                 = 5;

   /**
    * The destination is left untouched. The source is not used.
    * 
    * The blue letter is returned and the orange triangle is not used.
    * 
    * Inverse of {@link ITechBlend#OP_01_SRC}
    */
   public static final int OP_06_DST                      = 6;

   /**
    * The part of the destination lying inside of the source replaces the destination.
    * <br>
    * or 'mask' the background with source)
    */
   public static final int OP_07_DST_IN                   = 7;

   /**
    * The part of the destination lying outside of the source replaces the destination.
    * <br.
    * Erase where there are src pixels. Erasing is proportional to Opacity.
    */
   public static final int OP_08_DST_OUT                  = 8;

   /**
    * The part of the destination lying inside of the source is composited over the source and replaces the destination
    */
   public static final int OP_09_DST_ATOP                 = 9;

   /**
    * The part of the source that lies outside of the destination 
    * is combined with the part of the destination that lies outside of the source 
    */
   public static final int OP_10_XOR                      = 10;

   /**
    * Both the color and the alpha of the destination are cleared (Porter-Duff Clear rule). 
    * Neither the source nor the destination is used as input. 
    */
   public static final int OP_11_CLEAR                    = 11;

   public static final int OP_CK_MAX                      = 11;

   /**
    * Don't touch alpha values
    */
   public static final int OPACITY_00_SRC                 = 0;

   public static final int OPACITY_01_MIN_OVERIDE_SRC     = 1;

   public static final int OPACITY_02_MAX_OVERIDE_SRC     = 2;

   public static final int OPACITY_03_OVERIDE_SRC         = 3;

   public static final int OPACITY_CK_MAX                 = 3;

}
