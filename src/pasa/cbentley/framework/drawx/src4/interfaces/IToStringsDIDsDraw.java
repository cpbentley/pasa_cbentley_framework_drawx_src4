/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.interfaces;

import pasa.cbentley.byteobjects.src4.ctx.IToStringsDIDsBoc;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * Debug IDs for {@link ITechFigure}
 * <br>
 * Did starts. How to use
 * <br>
 * Extends as tag, the {@link IDIDsRootBase} which specifies the behavior of DIDS.
 * <br>
 * <br>
 * 
 * @author Charles-Philip
 *
 */
public interface IToStringsDIDsDraw extends IToStringsDIDsBoc {

   public static final int A_DID_OFFSET_A_DRAW          = 1000;

   public static final int A_DID_OFFSET_Z_DRAW          = 1200;

   public static final int DID_01_                      = A_DID_OFFSET_A_DRAW + 1;

   public static final int DID_02_                      = A_DID_OFFSET_A_DRAW + 2;

   public static final int DID_03_                      = A_DID_OFFSET_A_DRAW + 3;

   public static final int DID_04_                      = A_DID_OFFSET_A_DRAW + 4;

   public static final int DID_05_MASK_BLEND            = A_DID_OFFSET_A_DRAW + 5;

   public static final int DID_06_MASK_PRESET           = A_DID_OFFSET_A_DRAW + 6;

   public static final int DID_07_DRAWABLE_IMPLICIT_W_H = A_DID_OFFSET_A_DRAW + 7;

   public static final int DID_09_                      = A_DID_OFFSET_A_DRAW + 9;

   public static final int DID_10_                      = A_DID_OFFSET_A_DRAW + 10;

   public static final int DID_11_INTERPOLATION         = A_DID_OFFSET_A_DRAW + 11;

   public static final int DID_12_SKEW_EDGE_TYPES       = A_DID_OFFSET_A_DRAW + 12;

   public static final int DID_13_                      = A_DID_OFFSET_A_DRAW + 13;

   public static final int DID_14_PASS                  = A_DID_OFFSET_A_DRAW + 14;

   public static final int DID_15_                      = A_DID_OFFSET_A_DRAW + 15;

   public static final int DID_16_                      = A_DID_OFFSET_A_DRAW + 16;

   public static final int DID_17_BLEND_OP_ALPHA        = A_DID_OFFSET_A_DRAW + 17;

   public static final int DID_18_BLEND_OP_DUFF         = A_DID_OFFSET_A_DRAW + 18;

   public static final int DID_19_BLEND_OPACITY         = A_DID_OFFSET_A_DRAW + 19;

   public static final int DID_NUMBER                   = A_DID_OFFSET_A_DRAW + 16;

}
