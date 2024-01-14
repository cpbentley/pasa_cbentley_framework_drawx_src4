/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.byteobjects.src4.objects.color.BlendOp;
import pasa.cbentley.byteobjects.src4.objects.color.ITechBlend;

public interface ITechMask extends IByteObject {

   /**
    * 1 byte for flag
    * 4 bytes for bg color
    * 4 bytes for mid color
    * 4 bytes for shape color
    * 3 bytes for 3 blends
    * 1 byte for alpha bg
    * 1 byte for alpha shape
    */
   public static final int MASK_BASIC_SIZE                  = A_OBJECT_BASIC_SIZE + 18;

   /**
    * Operator takes the bg figure pixel and applies the alpha
    */
   public static final int MASK_BLEND_0                     = 0;

   /**
    * Operator takes the shape color and applies the alpha
    */
   public static final int MASK_BLEND_1                     = 1;

   /**
    * For any value above, use the {@link BlendOp} switch.
    */
   public static final int MASK_BLEND_2                     = 2;

   /**
    * Is there a color filter to be applied on the mask layer, 
    * before the fondu
    */
   public static final int MASK_FLAG_1_MASK_FILTER          = 1 << 0;

   /**
    * When set, the mask has a background figure defined.
    * Otherwise, a fully opaque black background is used
    */
   public static final int MASK_FLAG_2_BG_FIGURE            = 1 << 1;

   /**
    * is bg color defined
    */
   public static final int MASK_FLAG_5_BG_COLOR             = 1 << 4;

   /**
    * Function to be applied to all pixels masked with the white mask color
    */
   public static final int MASK_FLAG_5_FUNCTION_MASK_COLOR  = 1 << 4;

   /**
    * Function to be applied to all pixels masked with the shape color
    */
   public static final int MASK_FLAG_6_FUNCTION_SHAPE_COLOR = 1 << 5;

   /**
    * Background figure is drawn at the scale of masked image.
    * Most used for Strings.
    */
   public static final int MASK_FLAG_6_SIZE_MASK            = 1 << 5;

   public static final int MASK_FLAG_7_BG_FONDU             = 1 << 6;

   public static final int MASK_FLAG_8_SHAPE_FONDU          = 1 << 7;

   public static final int MASK_OFFSET_1_FLAG1              = A_OBJECT_BASIC_SIZE;

   /**
    * Color of the mask. Usually fully opaque and should match the background color of
    * the drawable drawing the mask. 
    * When a shape is applied on a mask, the shape has WHITE pixels which is by default convention,
    * the background.
    * White pixels are not used per say. The mask background pixel  
    */
   public static final int MASK_OFFSET_2_COLOR_BG4          = A_OBJECT_BASIC_SIZE + 1;

   /**
    * When a shape is applied on a mask, the shape has a boundary between BLACK(shape) and WHITE(bg) pixels.
    */
   public static final int MASK_OFFSET_3_COLOR_MID4         = A_OBJECT_BASIC_SIZE + 5;

   /**
    * Color of the shape drawn on the mask layer (string or other figure).
    * <br>
    * This value is <b>opaque black</b> by default.
    * <br>
    * <br>
    * When the mask layer shape is implicit, uses this color for drawing the mask shape
    * <br>
    * <br>
    * 
    */
   public static final int MASK_OFFSET_4_COLOR_SHAPE4       = A_OBJECT_BASIC_SIZE + 9;

   /**
    * Blend operator for the non shape/background pixels in the mask.
    * <br>
    * It will blend the {@link ITechMask#MASK_OFFSET_2_COLOR_BG4} over the background figure of the mask.
    * <br>
    * It defaults to {@link ITechMask#MASK_BLEND_0}
    * <li> {@link ITechBlend#BLENDING_00_OVER} replaced by  {@link ITechMask#MASK_BLEND_0}
    * <li> {@link ITechBlend#BLENDING_01_SRC} replaced by  {@link ITechMask#MASK_BLEND_1}
    * <li> {@link ITechBlend#BLENDING_02_DARKEN}
    * <li> {@link ITechBlend#BLENDING_03_LIGHTEN}
    */
   public static final int MASK_OFFSET_5_BLEND_BG1          = A_OBJECT_BASIC_SIZE + 13;

   /**
    * When the black and white mask is produced, a {@link IBOTypesDrw#TYPE_056_COLOR_FILTER} maybe applied
    * which modifies the alpha values of white and/or black pixels.
    * <br>
    * <br>
    * This field is the blend operator for those pixels.
    */
   public static final int MASK_OFFSET_6_BLEND_MID1         = A_OBJECT_BASIC_SIZE + 14;

   /**
    * The pixels of the shape blends with the bg pixel of the background figure.
    * 
    */
   public static final int MASK_OFFSET_7_BLEND_SHAPE1       = A_OBJECT_BASIC_SIZE + 15;

   /**
    * Alpha value of pixels that are not the shape.
    * <br>
    * <br>
    * In a normal mask this value is 0 so as to show only the pixels of shape.
    */
   public static final int MASK_OFFSET_8_ALPHA_BG1          = A_OBJECT_BASIC_SIZE + 16;

   /**
    * Alpha value of pixels of shape color.
    * <br>
    * <br>
    * That is when you draw a String, 255 will show a fully opaque string of characters.
    */
   public static final int MASK_OFFSET_9_ALPHA_SHAPE1       = A_OBJECT_BASIC_SIZE + 17;

   public static final int MASK_PRESET_0HAL0                = 0;

   public static final int MASK_PRESET_1HAL0                = 1;

   public static final int MASK_PRESET_2HAL0                = 2;

   public static final int MASK_PRESET_3HAL0                = 3;

}
