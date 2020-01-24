package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.BlendOp;

public interface ITechMask extends ITechByteObject {

   /**
    * 1 byte for flag
    * 4 bytes for bg color
    * 4 bytes for mid color
    * 4 bytes for shape color
    * 3 bytes for 3 blends
    * 1 byte for alpha bg
    * 1 byte for alpha shape
    */
   int MASK_BASIC_SIZE                      = A_OBJECT_BASIC_SIZE + 18;
   /**
    * Default behaviour takes the
    */
   int MASK_BLEND_0                         = 0;
   int MASK_BLEND_1                         = 1;
   /**
    * For any value above, use the {@link BlendOp} switch.
    */
   int MASK_BLEND_2                         = 2;
   /**
    * Is there a color filter to be applied on the mask layer, 
    * before the fondu
    */
   int MASK_FLAG_1MASK_FILTER               = 1;
   /**
    * When set, the mask has a background figure defined
    */
   int MASK_FLAG_2BG_FIGURE                 = 2;
   /**
    * is bg color defined
    */
   int MASK_FLAG_5BG_COLOR                  = 16;
   /**
    * Function to be applied to all pixels masked with the white mask color
    */
   int MASK_FLAG_5FUNCTION_MASK_COLOR       = 16;
   /**
    * Function to be applied to all pixels masked with the shape color
    */
   int MASK_FLAG_6FUNCTION_SHAPE_COLOR      = 32;
   /**
    * Background figure is drawn at the scale of masked image.
    * Most used for Strings.
    */
   int MASK_FLAG_6SIZE_MASK                 = 32;
   int MASK_FLAG_7BG_FONDU                  = 64;
   int MASK_FLAG_8SHAPE_FONDU               = 128;
   int MASK_OFFSET_1FLAG1                   = A_OBJECT_BASIC_SIZE;
   /**
    * Color of the mask. Usually fully opaque and should match the background color of
    * the drawable drawing the mask. 
    */
   int MASK_OFFSET_2COLOR_BG4               = A_OBJECT_BASIC_SIZE + 1;
   /**
    * 
    */
   int MASK_OFFSET_3COLOR_MID4              = A_OBJECT_BASIC_SIZE + 5;
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
   int MASK_OFFSET_4COLOR_SHAPE4            = A_OBJECT_BASIC_SIZE + 9;
   /**
    * Blend operator for the non shape pixels in the mask.
    * <br>
    * <br>
    * It defaults to {@link ITechMask#MASK_BLEND_0}
    */
   int MASK_OFFSET_5BLEND_BG1               = A_OBJECT_BASIC_SIZE + 13;
   /**
    * When the black and white mask is produced, a {@link IBOTypesDrw#TYPE_056_COLOR_FILTER} maybe applied
    * which modifies the alpha values of white and/or black pixels.
    * <br>
    * <br>
    * This field is the blend operator for those pixels.
    */
   int MASK_OFFSET_6BLEND_MID1              = A_OBJECT_BASIC_SIZE + 14;
   /**
    * The pixels of the shape blends with the bg pixel of the background figure.
    * 
    */
   int MASK_OFFSET_7BLEND_SHAPE1            = A_OBJECT_BASIC_SIZE + 15;
   /**
    * Alpha value of pixels that are not the shape.
    * <br>
    * <br>
    * In a normal mask this value is 0 so as to show only the pixels of shape.
    */
   int MASK_OFFSET_8ALPHA_BG1               = A_OBJECT_BASIC_SIZE + 16;
   /**
    * Alpha value of pixels of shape color.
    * <br>
    * <br>
    * That is when you draw a String, 255 will show a fully opaque string of characters.
    */
   int MASK_OFFSET_9ALPHA_SHAPE1            = A_OBJECT_BASIC_SIZE + 17;
   int MASK_PRESET_0HAL0                    = 0;
   int MASK_PRESET_1HAL0                    = 1;
   int MASK_PRESET_2HAL0                    = 2;
   int MASK_PRESET_3HAL0                    = 3;

}
