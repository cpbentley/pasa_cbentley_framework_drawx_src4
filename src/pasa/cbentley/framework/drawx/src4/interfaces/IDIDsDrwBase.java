/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.interfaces;

import pasa.cbentley.byteobjects.src4.ctx.IDebugIDsBOC;
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
public interface IDIDsDrwBase extends IDebugIDsBOC {
   
   public static final int DID__MODULE_LENGTH           = 120;

   public static final int DID__MODULE_OFFSET           = 4500;

   public static final int DID_01_GRAD_RECT             = DID__MODULE_OFFSET + 1;

   public static final int DID_02_GRAD_TRIG             = DID__MODULE_OFFSET +2;

   public static final int DID_03_IMAGE_TRANSFORM       = DID__MODULE_OFFSET +3;

   public static final int DID_04_GRAD_ELLIPSE          = DID__MODULE_OFFSET +4;

   public static final int DID_05_MASK_BLEND            = DID__MODULE_OFFSET +5;

   public static final int DID_06_MASK_PRESET           = DID__MODULE_OFFSET +6;

   public static final int DID_07_DRAWABLE_IMPLICIT_W_H = DID__MODULE_OFFSET +7;

   public static final int DID_08_DIAG_DIR              = DID__MODULE_OFFSET +8;

   public static final int DID_09_BLEND_OP              = DID__MODULE_OFFSET +9;

   public static final int DID_10_TRANSFORMATION        = DID__MODULE_OFFSET +10;

   public static final int DID_11_INTERPOLATION         = DID__MODULE_OFFSET +11;

   public static final int DID_12_SKEW_EDGE_TYPES       = DID__MODULE_OFFSET +12;

   public static final int DID_13_RND_COLORS            = DID__MODULE_OFFSET +13;

   public static final int DID_14_PASS                  = DID__MODULE_OFFSET +14;

   public static final int DID_15_FILTER_TYPE           = DID__MODULE_OFFSET +15;

   public static final int DID_16_GRAD_PREDEFINES       = DID__MODULE_OFFSET +16;

   public static final int DID_17_BLEND_OP_ALPHA        = DID__MODULE_OFFSET +17;

   public static final int DID_18_BLEND_OP_DUFF         = DID__MODULE_OFFSET +18;

   public static final int DID_19_BLEND_OPACITY         = DID__MODULE_OFFSET +19;

   public static final int DID_NUMBER                   = DID__MODULE_OFFSET +16;

}
