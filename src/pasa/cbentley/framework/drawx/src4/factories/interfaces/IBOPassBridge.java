/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories.interfaces;

/**
 * 
 * @author Charles Bentley
 *
 */
public interface IBOPassBridge extends IBOPass {

   public static final int PBRIDGE_BASIC_SIZE               = A_OBJECT_BASIC_SIZE + 3;

   public static final int PBRIDGE_FLAG_1_FULLY_TRANS_WHITE = 1 << 0;

   public static final int PBRIDGE_OFFSET_01_FLAG1          = A_OBJECT_BASIC_SIZE;

   /**
    * <li>{@link ITechPass#PASS_0_FIGURE}
    * <li>{@link ITechPass#PASS_1_MOSAIC}
    * <li>{@link ITechPass#PASS_2_SKEW}
    * <li>{@link ITechPass#PASS_3_SCALE}
    * <li>{@link ITechPass#PASS_4_ROTATE}
    */
   public static final int PASS_OFFSET_02_TYPE1             = A_OBJECT_BASIC_SIZE + 1;

}
