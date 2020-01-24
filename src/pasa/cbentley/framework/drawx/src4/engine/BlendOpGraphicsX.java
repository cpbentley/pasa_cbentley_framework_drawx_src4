package pasa.cbentley.framework.drawx.src4.engine;
//package mordan.draw;
//
//import mordan.device.DeviceDriver;
//import mordan.universal.utils.ColorConstants;
//import mordan.universal.utils.StringBuilder;
//
///**
// * Used only for swtiching SRC and OVER modes
// * @author Charles-Philip Bentley
// *
// */
//class BlendOpGraphicsX extends BlendOp {
//
//   /**
//    * Blends using simple {@link BlendOp#blendPixel(int, int)}.
//    */
//   public static final int INTERNAL_01_OVER                = 1;
//
//   /**
//    * Same as OVER, but changes the alpha with blending alpha value.
//    */
//   public static final int INTERNAL_02_OVER_ALPHA          = 2;
//
//   /**
//    * Do blending if source pixel is accepted/rejected by a function.
//    * <br> a given mask color.
//    */
//   public static final int INTERNAL_03_OVER_EX_COLOR       = 3;
//
//   /**
//    * Do blending if source/blend pixel does not have a given alpha value.
//    * <br>
//    * TODO. use acceptor function.
//    */
//   public static final int INTERNAL_04_OVER_EX_COLOR_ALPHA = 4;
//
//   /**
//    */
//   public static final int INTERNAL_05_SRC                 = 5;
//
//   public static final int INTERNAL_06_SRC_ALPHA           = 6;
//
//   public static final int INTERNAL_07_SRC_EX_COLOR        = 7;
//
//   public static final int INTERNAL_08_SRC_ALPHA_EX_COLOR  = 8;
//
//   public static final int INTERNAL_09_SRC_SEMI_ALPHA      = 9;
//
//   public static String debugBlendModeInternal(int mode) {
//      switch (mode) {
//         case INTERNAL_01_OVER:
//            return "OVER";
//         case INTERNAL_02_OVER_ALPHA:
//            return "OVER_ALPHA";
//         case INTERNAL_03_OVER_EX_COLOR:
//            return "OVER_EX_COLOR";
//         case INTERNAL_04_OVER_EX_COLOR_ALPHA:
//            return "OVER_EX_COLOR_ALPHA";
//         case INTERNAL_05_SRC:
//            return "SRC";
//         case INTERNAL_06_SRC_ALPHA:
//            return "SRC_ALPHA";
//         case INTERNAL_08_SRC_ALPHA_EX_COLOR:
//            return "SRC_ALPHA_EX_COLOR";
//         case INTERNAL_07_SRC_EX_COLOR:
//            return "SRC_EX_COLOR";
//         case INTERNAL_09_SRC_SEMI_ALPHA:
//            return "SRC_SEMI_ALPHA";
//         default:
//            return "Unknown " + mode;
//      }
//   }
//
//   private int     alpha;
//
//   private boolean applyAlpha   = false;
//
//   private boolean applyExclude;
//
//   /**
//    * Used by {@link GraphicsX} virtual colors purple and cyan.
//    */
//   private int     excludeColor;
//
//   /**
//    * Tells whether to use Acceptor/Alpha
//    */
//   private int     internalMode = 0;
//
//   public BlendOpGraphicsX(DeviceDriver dd, int mode) {
//      super(dd, mode);
//      updateInternalMode();
//   }
//
//   /**
//    * Constructor used by {@link GraphicsX} virtual layer blenders.
//    * <br>
//    * <br>
//    * In their cases, white/cyan is never blended as such since it is actually a fully transparent pixel.
//    * <br>
//    * @param mode
//    * @param excludeColor
//    */
//   public BlendOpGraphicsX(DeviceDriver dd, int mode, int excludeColor) {
//      super(dd, mode);
//      this.excludeColor = excludeColor;
//      applyExclude = true;
//      updateInternalMode();
//   }
//
//   /**
//    * override and switch on internal mode
//    */
//   public int blendPixel(int baseIndex, int blendIndex) {
//      int newPixel = 0;
//      switch (internalMode) {
//         case INTERNAL_01_OVER:
//            newPixel = BlendOp.mergePixelsOver(baseIndex, blendIndex);
//            break;
//         case INTERNAL_02_OVER_ALPHA:
//            //apply alpha to blend pixel
//            newPixel = updateAlpha(blendIndex, alpha);
//            baseIndex = BlendOp.mergePixelsOver(baseIndex, newPixel);
//            break;
//         case INTERNAL_03_OVER_EX_COLOR:
//            newPixel = blendIndex;
//            if (excludeColor != newPixel) {
//               baseIndex = BlendOp.mergePixelsOver(baseIndex, newPixel);
//            }
//            break;
//         case INTERNAL_04_OVER_EX_COLOR_ALPHA:
//            newPixel = blendIndex;
//            if (excludeColor != newPixel) {
//               newPixel = updateAlpha(newPixel, alpha);
//               baseIndex = BlendOp.mergePixelsOver(baseIndex, newPixel);
//            }
//            break;
//         case INTERNAL_05_SRC:
//            baseIndex = blendIndex;
//            break;
//         case INTERNAL_07_SRC_EX_COLOR:
//            newPixel = blendIndex;
//            if (excludeColor != newPixel) {
//               baseIndex = blendIndex;
//            }
//            break;
//         case INTERNAL_06_SRC_ALPHA:
//            newPixel = updateAlpha(blendIndex, alpha);
//            baseIndex = newPixel;
//            break;
//         case INTERNAL_08_SRC_ALPHA_EX_COLOR:
//            newPixel = blendIndex;
//            if (excludeColor != newPixel) {
//               newPixel = updateAlpha(newPixel, alpha);
//               baseIndex = newPixel;
//            }
//            break;
//         case INTERNAL_09_SRC_SEMI_ALPHA:
//            newPixel = blendIndex;
//            int alpha = (newPixel >> 24 & 0xFF);
//            if (alpha != 0 && alpha != 255) {
//               baseIndex = newPixel;
//            }
//            break;
//         default:
//            break;
//      }
//      return newPixel;
//   }
//
//   /**
//    * Changes the blending mode without changing the exclude color.
//    * @param mode
//    */
//   public void newMode(int mode) {
//      this.mode = mode;
//      updateInternalMode();
//   }
//
//   public void setAlpha(boolean applyAlpha, int alpha) {
//      this.applyAlpha = applyAlpha;
//      this.alpha = alpha;
//      updateInternalMode();
//   }
//
//   public void setExcludeColor(boolean applyExclude, int color) {
//      this.applyExclude = applyExclude;
//      this.excludeColor = color;
//      updateInternalMode();
//   }
//
//   public String toString(String nl) {
//      StringBuilder sb = StringBuilder.getMainSmall();
//      sb.append("#BlendOpGraphicsX");
//      sb.append(debugBlend(internalMode));
//      sb.append(' ');
//      sb.append(ColorConstants.debugColorStr(excludeColor));
//      sb.append(" alpha=" + alpha);
//      
//      return sb.toString();
//   }
//
//   private int updateAlpha(int newPixel, int alpha) {
//      newPixel = ((alpha << 24) + (newPixel & 0xFFFFFF));
//      return newPixel;
//   }
//
//   /**
//    * For speed we have a internal mode for each operation types.
//    * Internal mode for {@link GraphicsX}.
//    */
//   private void updateInternalMode() {
//      if (mode == BLENDING_00_OVER) {
//         if (applyAlpha && alpha != 255) {
//            if (applyExclude) {
//               internalMode = INTERNAL_04_OVER_EX_COLOR_ALPHA;
//            } else {
//               internalMode = INTERNAL_02_OVER_ALPHA;
//            }
//         } else {
//            if (applyExclude) {
//               internalMode = INTERNAL_03_OVER_EX_COLOR;
//            } else {
//               internalMode = INTERNAL_01_OVER;
//            }
//         }
//      } else if (mode == BLENDING_01_SRC) {
//         if (applyAlpha && alpha != 255) {
//            if (applyExclude) {
//               internalMode = INTERNAL_08_SRC_ALPHA_EX_COLOR;
//            } else {
//               internalMode = INTERNAL_06_SRC_ALPHA;
//            }
//         } else {
//            if (applyExclude) {
//               internalMode = INTERNAL_07_SRC_EX_COLOR;
//            } else {
//               internalMode = INTERNAL_05_SRC;
//            }
//         }
//      } else {
//         throw new IllegalStateException("Wrong mode");
//      }
//   }
//}
