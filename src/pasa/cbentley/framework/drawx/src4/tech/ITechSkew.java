/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

public interface ITechSkew {

   /**
    * When the input pixel is on the border of the image,
    * return 0 (Fully transparent black)
    */
   public static final int SKEW_EDGE_0_ZERO              = 0;

   /**
    * 
    */
   public static final int SKEW_EDGE_1_CLAMP             = 1;

   public static final int SKEW_EDGE_2_WRAP              = 2;

   public static final int SKEW_EDGE_3_FULLY_TRANS_PIXEL = 3;

   public static final int SKEW_EDGE_4_PIXEL             = 4;

   public static final int SKEW_EDGE_5_WHITE             = 5;

   public static final int SKEW_EDGE_CK_MAX              = 5;

   /**
    * Inverse the distance weight
    */
   public static final int SKEW_FLAG_1_INVERSE_DISTANCE  = 0;

   public static final int SKEW_TYPE_0_NEAREST_NEIGHBOUR = 0;

   public static final int SKEW_TYPE_1_BILINEAR          = 1;

   public static final int SKEW_TYPE_2_BILINEAR_4SPLIT   = 2;

   public static final int SKEW_TYPE_3_BICUBIC           = 3;

   public static final int SKEW_TYPE_CK_MAX              = 2;

}
