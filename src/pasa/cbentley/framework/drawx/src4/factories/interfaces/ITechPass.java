package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.core.src4.interfaces.ITech;

public interface ITechPass extends ITech {

   int PASS_0_FIGURE                 = 0;
   /**
    * Mosaic with different color scheme!
    * <li> this wouldn't be an image transform.
    */
   int PASS_1_MOSAIC                 = 1;
   /**
    * Skew box. but other types of skews are possible.
    * <li> root image of skew can be any image in the process. this allows a center skew box of zero size
    * but the skewed image src is different.
    * <li> 
    */
   int PASS_2_SKEW                   = 2;
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
   
   int PASS_3_SCALE                  = 3;
   /**
    * Rotates the image. Option to fit inside (using scaling) or crop
    */
   int PASS_4_ROTATE                 = 4;
   int PASS_CK_MAX                   = 4;

}
