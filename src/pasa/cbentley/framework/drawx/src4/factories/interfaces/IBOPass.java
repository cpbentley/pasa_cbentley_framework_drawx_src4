/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;

public interface IBOPass extends IByteObject {


   public static final int PASS_BASIC_SIZE               = A_OBJECT_BASIC_SIZE + 3;

   public static final int PASS_FLAG_1_FULLY_TRANS_WHITE = 1 << 0;

   /**
    * Filter to apply before the scaling method<br>
    */
   public static final int PASS_FLAG_1_PRE_FILTER        = 1 << 0;

   /**
    * Filter to apply after the scaling method <br>
    * Positionned second if a pre filter is defined.
    */
   public static final int PASS_FLAG_2_POST_FILTER       = 1 << 1;

   public static final int PASS_OFFSET_01_FLAG1          = A_OBJECT_BASIC_SIZE;

   /**
    * {@link ITechPass#PASS_0_FIGURE}
    */
   public static final int PASS_OFFSET_02_TYPE1          = A_OBJECT_BASIC_SIZE + 1;

}
