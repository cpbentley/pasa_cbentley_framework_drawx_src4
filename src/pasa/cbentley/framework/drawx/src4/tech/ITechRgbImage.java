/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbCache;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

public interface ITechRgbImage {

   /**
    * Set when RgbImage is a region of another RgbImage.
    * <br>
    * ?means M and N?
    */
   int FLAG_01_REGION                = 1 << 0;
   /**
    * Flag set to an Image when it is the source used by other images
    * linked with a transformation
    */
   int FLAG_02_ROOT_LINK             = 1 << 1;
   /**
    * Flag set to an Image with a LinkingImage
    * A linking image will inherit all modifications to the root image
    * and vice versa
    */
   int FLAG_03_LINKING               = 1 << 2;
   /**
    * Image may not be modified. Therefore, Rgb Mode is not accepted 
    */
   int FLAG_04_IMMUTABLE             = 1 << 3;
   /**
    * RgbImage keeps track if it has transparent pixels
    * If not set, nothing can be known.
    * if flag is set to true, this means all the pixels of the the image are opaque.
    */
   int FLAG_05_IGNORE_ALPHA          = 1 << 4;
   /**
    * Set when a class wants to have exclusive access to the int[] pixel data for modification and drawing
    * 
    */
   int FLAG_06_WRITE_LOCK            = 1 << 5;
   int FLAG_07_READ_LOCK             = 1 << 6;
   /**
    * Flag for ephemeral images
    */
   int FLAG_08_DISPOSE_AFTER_DRAW    = 1 << 7;
   /**
    * Flag set when soft gc cannot clear this memory area
    * By default all images data can be reclaimed. This is especailly true of cache image.
    * Setting this flag should be an exception as it pins the image data in memory
    */
   int FLAG_09_USED                  = 1 << 8;
   /**
    * Flag set by the garbage collector when the image data have been set to null.
    * <br>
    * If sourceLocator image, data is reloaded from disk. 
    * For cache images, cache content must be rebuilt externally
    * For other images, set the Flag USED to prevent data erasure.
    * <br>
    * No Data means Width and Height are maybe known! The raw data has been purged
    */
   int FLAG_10_NO_DATA               = 1 << 9;
   /**
    * Set when the Image is a result of a cloneage
    */
   int FLAG_11_CLONED                = 1 << 10;
   /**
    * Set when image instance may be reused.
    * 
    */
   int FLAG_12_DISPOSED              = 1 << 11;
   /**
    * Flag for RgbMode
    */
   int FLAG_13_RGB                   = 1 << 12;
   /**
    * Flag set by Originator of the {@link RgbImage} to tell the user of the {@link RgbImage},
    * that it must be updated.
    */
   int FLAG_14_MODIFIED              = 1 << 13;
   int FLAG_15_KNOWN_ALPHA           = 1 << 14;
   /**
    * Set when the image is full of bgcolor
    */
   int FLAG_16_VIRGIN                = 1 << 15;
   /**
    * Set when {@link RgbImage} is able to rebuilt itself.
    */
   int FLAG_17_LOCATOR               = 1 << 16;
   /**
    * When image has index =0, M =0, N=0
    */
   int FLAG_18_OWN_RGB_ARRAY         = 1 << 17;
   /**
    * Flag enables a {@link RgbImage} to distinguish blend calls.
    */
   int FLAG_19_BLENDING              = 1 << 18;
   /**
    * {@link RgbImage} is flagged as reloadable.
    * <br>
    * Merge with MemAgent framework is reloading/soft reference
    */
   int FLAG_20_RELOADABLE            = 1 << 19;
   int FLAG_21                       = 1 << 20;
   /**
    * RgbImage will not be drawn by {@link GraphicsX}
    */
   int FLAG_22_NOT_DRAWABLE          = 1 << 21;
   int FLAG_23                       = 1 << 22;
   int FLAG_24_MASKED                = 1 << 23;
   /**
    * Data, width and height are in memory.
    * <br>
    * Set by the {@link RgbCache}
    */
   int FLAG_25_LOADED_FULLY          = 1 << 24;
   int MEM_STATE_0_FULLY_LOADED      = 0;
   /**
    * when gc has removed the data
    */
   int MEM_STATE_1_WH_LOADED_NO_DATA = 1;
   int MEM_STATE_2_NOTHING           = 2;
   int TRANS_MAX_CK                  = 7;
   int TYPE_0_NONE                   = 0;
   int TYPE_1_SHARED                 = 1;
   int TYPE_2_INDIRECT               = 2;

}
