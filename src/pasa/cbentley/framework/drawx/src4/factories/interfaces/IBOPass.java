/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;

public interface IBOPass extends IByteObject {

   public static final int PASS_0_FIGURE           = 0;

   /**
    * Mosaic with different color scheme!
    * <li> this wouldn't be an image transform.
    */
   public static final int PASS_1_MOSAIC           = 1;

   /**
    * Skew box. but other types of skews are possible.
    * <li> root image of skew can be any image in the process. this allows a center skew box of zero size
    * but the skewed image src is different.
    * <li> 
    */
   public static final int PASS_2_SKEW             = 2;

   /**
    * Special type of scaling where the image is scaled down and transparent pixels.
    * 
    * Zoom is here?  take an area of and fit it to another dimension
    * 
    * Crop?
    * 
    * pad 
    * Resize the image to fill the given width & height while retaining original proportions.
    *    If proportions of the originals image do not match the required ones, 
    *    pad the image for reaching exactly the required size. 
    *    You can specify the 'gravity' to selecting which part of the original image 
    *    to use for filling the rectangle.
    */

   public static final int PASS_3_SCALE            = 3;

   /**
    * Rotates the image. Option to fit inside (using scaling) or crop
    */
   public static final int PASS_4_ROTATE           = 4;

   public static final int PASS_BASIC_SIZE         = A_OBJECT_BASIC_SIZE + 3;

   public static final int PASS_CK_MAX             = 4;

   /**
    * Filter to apply before the scaling method<br>
    */
   public static final int PASS_FLAG_1_PRE_FILTER  = 1;

   /**
    * Filter to apply after the scaling method <br>
    * Positionned second if a pre filter is defined.
    */
   public static final int PASS_FLAG_2_POST_FILTER = 2;

   public static final int PASS_OFFSET_01_FLAG1    = A_OBJECT_BASIC_SIZE;

   /**
    * {@link IBOPass#PASS_0_FIGURE}
    */
   public static final int PASS_OFFSET_02_TYPE1    = A_OBJECT_BASIC_SIZE + 1;

}
