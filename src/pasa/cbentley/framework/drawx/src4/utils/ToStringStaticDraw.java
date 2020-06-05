/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.utils;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.interfaces.ITechTransform;
import pasa.cbentley.core.src4.logging.ToStringStaticBase;
import pasa.cbentley.framework.coredraw.src4.ctx.ToStringStaticCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.style.ITechStyle;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;
import pasa.cbentley.framework.drawx.src4.tech.ITechBlend;
import pasa.cbentley.framework.drawx.src4.tech.ITechColorFunction;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;
import pasa.cbentley.framework.drawx.src4.tech.ITechMask;
import pasa.cbentley.framework.drawx.src4.tech.ITechPass;
import pasa.cbentley.framework.drawx.src4.tech.ITechScaler;
import pasa.cbentley.framework.drawx.src4.tech.ITechSkew;

/**
 * Centralizes all debugging of ByteObject String and Constants in universal.utils project.
 * <br>
 * <br>
 * For other classes, use their own Debug centralizer per module.
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class ToStringStaticDraw extends ToStringStaticBase {

   public static String debugAlign(int a) {
      switch (a) {
         case ITechAnchor.ALIGN_1_TOP:
            return "Top";
         case ITechAnchor.ALIGN_2_BOTTOM:
            return "Bottom";
         case ITechAnchor.ALIGN_5_FILL:
            return "Fill";
         case ITechAnchor.ALIGN_6_CENTER:
            return "Center";
         case ITechAnchor.ALIGN_4_RIGHT:
            return "Right";
         case ITechAnchor.ALIGN_3_LEFT:
            return "Left";
         default:
            return "Unknown Align " + a;
      }
   }

   public static String debugPass(int p) {
      switch (p) {
         case ITechPass.PASS_0_FIGURE:
            return "Figure";
         case ITechPass.PASS_1_MOSAIC:
            return "Mosaic";
         case ITechPass.PASS_2_SKEW:
            return "Skew";
         case ITechPass.PASS_3_SCALE:
            return "Scale";
         case ITechPass.PASS_4_ROTATE:
            return "Rotate";
         default:
            return "UnknownPass " + p;
      }
   }

   public static String debugStyleAnchor(int i) {
      switch (i) {
         case ITechStyle.STYLE_ANC_0_BORDER:
            return "AT_BORDER";
         case ITechStyle.STYLE_ANC_1_MARGIN:
            return "AT_MARGIN";
         case ITechStyle.STYLE_ANC_2_CONTENT:
            return "AT_CONTENT";
         case ITechStyle.STYLE_ANC_3_PADDING:
            return "AT_PADDING";
         default:
            return "INVALID_ERROR";
      }
   }

   public static String debugColoRnd(int t) {
      switch (t) {
         case ITechColorFunction.RND_COLORS_TYPE_0_RND_32BITS:
            return "Random 32bits";
         case ITechColorFunction.RND_COLORS_TYPE_1_CHANNEL:
            return "Channel";
         case ITechColorFunction.RND_COLORS_TYPE_2_CHANNEL_SLOPE:
            return "Channel Slope";
         case ITechColorFunction.RND_COLORS_TYPE_3_CHANNEL_MOD:
            return "Channel Mod";
         case ITechColorFunction.RND_COLORS_TYPE_4_GRAYSCALE:
            return "Grayscale";
         case ITechColorFunction.RND_COLORS_TYPE_5_FIXED_BW_ROOT:
            return "Fixed BW Root";
         case ITechColorFunction.RND_COLORS_TYPE_6_PRE_SET:
            return "Preset";
         case ITechColorFunction.RND_COLORS_TYPE_7_BLEND_VARIATION:
            return "Color Blend Variation";
         case ITechColorFunction.RND_COLORS_TYPE_8_FIXEDEXTREMES:
            return "Fixed Extremes";

         default:
            return "Unknown Rnd Colors Type " + t;
      }
   }

   public static String debugDiagDir(int type) {
      switch (type) {
         case C.DIAG_DIR_0TOP_LEFT:
            return "TopLeft";
         case C.DIAG_DIR_1TOP_RIGHT:
            return "TopRight";
         case C.DIAG_DIR_2BOT_LEFT:
            return "BotLeft";
         case C.DIAG_DIR_3BOT_RIGHT:
            return "BotRight";
         default:
            return "UnknownDiagDir";
      }
   }

   public static String debugDrwType(final int type) {
      switch (type) {
         case IBOTypesDrw.TYPE_050_FIGURE:
            return "FIGURE";
         case IBOTypesDrw.TYPE_051_BOX:
            return "ANCHOR";
         case IBOTypesDrw.TYPE_058_MASK:
            return "MASK";
         case IBOTypesDrw.TYPE_059_GRADIENT:
            return "GRADIENT";
         case IBOTypesDrw.TYPE_060_TBLR:
            return "TBLR";
         case IBOTypesDrw.TYPE_070_TEXT_EFFECTS:
            return "TEXT_EFFECTS";
         case IBOTypesDrw.TYPE_056_COLOR_FILTER:
            return "COLOR_FILTER";
         default:
            return "UNKNOWN " + type;
      }
   }

   public static String debugFigType(final int type) {
      switch (type) {
         case ITechFigure.FIG_TYPE_01_RECTANGLE:
            return "RECTANGLE";
         case ITechFigure.FIG_TYPE_17_ARLEQUIN:
            return "Arlequin";
         case ITechFigure.FIG_TYPE_02_BORDER:
            return "Border";
         case ITechFigure.FIG_TYPE_07_ELLIPSE:
            return "Ellipse";
         case ITechFigure.FIG_TYPE_10_STRING:
            return "String";
         case ITechFigure.FIG_TYPE_3_TRIANGLE:
            return "Triangle";
         case ITechFigure.FIG_TYPE_12_ARROW:
            return "ARROW";
         case ITechFigure.FIG_TYPE_09_PIXELS:
            return "PIXELS";
         default:
            return "UNKNOWN FIGURE NAME " + type;
      }
   }

   public static String toStringGradPre(int val) {
      switch (val) {
         case ITechGradient.GRADIENT_PRE_0_NONE:
            return "CK";
         case ITechGradient.GRADIENT_PRE_1_0:
            return "0";
         case ITechGradient.GRADIENT_PRE_2_50:
            return "50 Middle";
         case ITechGradient.GRADIENT_PRE_3_100:
            return "100";
         default:
            return "Unknown Value Type " + val;
      }
   }

   /**
    * 
    * @param p
    * @param offset
    * @param flag
    * @param str
    * @param tru
    * @param fal
    */
   public static void debugFlagTrueOrFalse(ByteObject p, int offset, int flag, String str, StringBBuilder tru, StringBBuilder fal) {
      if (p.hasFlag(offset, flag)) {
         tru.append(str);
         tru.append(" ");
      } else {
         fal.append(str);
         fal.append(" ");
      }
   }

   public static String debugFont(IMFont f) {
      String s = "#Font " + ToStringStaticCoreDraw.fontFace(f.getFace()) + " " + ToStringStaticCoreDraw.debugFontStyle(f.getStyle()) + " " + ToStringStaticCoreDraw.debugFontSize(f.getSize());
      return s;
   }

   public static String debugFontBrackets(IMFont f) {
      if (f == null) {
         return "NULL FONT";
      }
      String s = "[" + ToStringStaticCoreDraw.fontFace(f.getFace()) + " " + ToStringStaticCoreDraw.debugFontStyle(f.getStyle()) + " " + ToStringStaticCoreDraw.debugFontSize(f.getSize()) + "]";
      return s;
   }

   public static String toStringColor(int c) {
      return "(" + ((c >> 24) & 0xFF) + "," + ((c >> 16) & 0xFF) + "," + ((c >> 8) & 0xFF) + "," + (c & 0xFF) + ")";
   }

   public static String debugStrGradEllipse(int value) {
      switch (value) {
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_00_NORMAL:
            return "Normal";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_01_HORIZ:
            return "Horizontal";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_02_VERT:
            return "Vertical";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_03_TOP_FLAMME:
            return "Top Flamme";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_04_BOT_FLAMME:
            return "Bot Flamme";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_05_LEFT_FLAMME:
            return "Left Flame";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_06_RIGHT_FLAMME:
            return "Right Flame";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_07_CLOCHE_TOP:
            return "Top Cloche";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_08_CLOCHE_BOT:
            return "Bot Cloche";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_09_CLOCHE_LEFT:
            return "Left Cloche";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_10_CLOCHE_RIGHT:
            return "Right Cloche";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_11_WATER_DROP_TOP:
            return "Top Water Drop";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_12_WATER_DROP_BOT:
            return "Bot Water Drop";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_13_WATER_DROP_LEFT:
            return "Left Water Drop";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_14_WATER_DROP_RIGHT:
            return "Right Water Drop";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_15_TOP_LEFT_BUBBLE:
            return "Top Left Bubble";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_16_TOP_RIGHT_BUBBLE:
            return "Top Right Bubble";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_17_BOT_LEFT_BUBBLE:
            return "Bot Left Bubble";
         case ITechGradient.GRADIENT_TYPE_ELLIPSE_18_BOT_RIGHT_BUBBLE:
            return "Bot Right Bubble";
         default:
            return "Unknown " + value;
      }
   }

   public static String debugMaskPreset(int preset) {
      switch (preset) {
         case ITechMask.MASK_PRESET_0HAL0:
            return "0";
         case ITechMask.MASK_PRESET_1HAL0:
            return "2";
         case ITechMask.MASK_PRESET_2HAL0:
            return "2";
         case ITechMask.MASK_PRESET_3HAL0:
            return "3";
         default:
            throw new IllegalArgumentException("Preset " + preset);
      }
   }

   public static String debugPaintMode(int mode) {
      switch (mode) {
         case GraphicsX.MODE_0_SCREEN:
            return "SCREEN";
         case GraphicsX.MODE_1_IMAGE:
            return "IMAGE";
         case GraphicsX.MODE_2_RGB_IMAGE:
            return "RGB_IMAGE";
         case GraphicsX.MODE_3_RGB:
            return "RGB";
         case GraphicsX.MODE_4_NULL:
            return "NULL";
         default:
            return "UNKNOWN " + mode;
      }
   }

   public static String debugMaskBlend(int blend) {
      switch (blend) {
         case ITechMask.MASK_BLEND_0:
            return "0";
         case ITechMask.MASK_BLEND_1:
            return "2";
         case ITechMask.MASK_BLEND_2:
            return "2";
         default:
            throw new IllegalArgumentException("Blend " + blend);
      }

   }

   public static String getImplit(int value) {
      switch (value) {
         case 0:
            return "+";
         case 1:
            return "0";
         case 2:
            return "-";
         default:
            return "Unknown" + value;
      }
   }

   public static String debugStrGradRect(int value) {
      switch (value) {
         case ITechGradient.GRADIENT_TYPE_RECT_05_BOTLEFT:
            return "BotLeft";
         case ITechGradient.GRADIENT_TYPE_RECT_06_BOTRIGHT:
            return "BotRight";
         case ITechGradient.GRADIENT_TYPE_RECT_01_HORIZ:
            return "Horizontal";
         case ITechGradient.GRADIENT_TYPE_RECT_00_SQUARE:
            return "Square";
         case ITechGradient.GRADIENT_TYPE_RECT_03_TOPLEFT:
            return "TopLeft";
         case ITechGradient.GRADIENT_TYPE_RECT_04_TOPRIGHT:
            return "TopRight";
         case ITechGradient.GRADIENT_TYPE_RECT_02_VERT:
            return "Vertical";
         case ITechGradient.GRADIENT_TYPE_RECT_07_L_TOP:
            return "L Top";
         case ITechGradient.GRADIENT_TYPE_RECT_08_L_BOT:
            return "L Bot";
         case ITechGradient.GRADIENT_TYPE_RECT_09_L_LEFT:
            return "L Left";
         case ITechGradient.GRADIENT_TYPE_RECT_10_L_RIGHT:
            return "L Right";

         default:
            return "Unknown " + value;
      }
   }

   public static String debugStrGradTrig(int value) {
      switch (value) {
         case ITechGradient.GRADIENT_TYPE_TRIG_00_TENT:
            return "Tent";
         case ITechGradient.GRADIENT_TYPE_TRIG_01_TENT_JESUS:
            return "JesusTent";
         case ITechGradient.GRADIENT_TYPE_TRIG_02_TOP_JESUS:
            return "JesusTop";
         case ITechGradient.GRADIENT_TYPE_TRIG_03_TUNNEL:
            return "Tunnel";
         case ITechGradient.GRADIENT_TYPE_TRIG_04_FULL:
            return "Full";
         case ITechGradient.GRADIENT_TYPE_TRIG_05_OPAQUEBASE:
            return "OpaqueBase";
         case ITechGradient.GRADIENT_TYPE_TRIG_06_OPAQUE_CENTER:
            return "OpaqueCenter";
         case ITechGradient.GRADIENT_TYPE_TRIG_07_ARROW:
            return "Arrow";
         case ITechGradient.GRADIENT_TYPE_TRIG_08_NORMAL:
            return "Normal";
         case ITechGradient.GRADIENT_TYPE_TRIG_09_HALO:
            return "Halo";
         case ITechGradient.GRADIENT_TYPE_TRIG_10_SWIPE:
            return "Swipe";

         default:
            return "Unknown " + value;
      }
   }

   public static String debugScaleType(int type) {
      switch (type) {
         case ITechScaler.SCALER_TYPE_0_FIT_NONE:
            return "None";
         case ITechScaler.SCALER_TYPE_1_FIT_BOTH:
            return "Fit Both";
         case ITechScaler.SCALER_TYPE_2_FIT_W:
            return "Fit W";
         case ITechScaler.SCALER_TYPE_3_FIT_H:
            return "Fit H";
         case ITechScaler.SCALER_TYPE_4_FIT_FIRST:
            return "Fit First";
         case ITechScaler.SCALER_TYPE_5_FIT_LAST:
            return "Fit First";
         default:
            return "Unknown = " + type;
      }
   }

   /**
    * Info about anchor
    * @return
    */
   public static String debugFigFlag(ByteObject fig) {
      StringBBuilder sb = new StringBBuilder(fig.getUCtx());
      debugFigFlag(sb, fig, ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_1_ANCHOR, " Anchor");
      debugFigFlag(sb, fig, ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_2_GRADIENT, " Gradient");
      debugFigFlag(sb, fig, ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_3_COLOR_ARRAY, " ColorArray");
      debugFigFlag(sb, fig, ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_4_MASK, " Mask");
      debugFigFlag(sb, fig, ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_5_FILTER, " Filter");
      debugFigFlag(sb, fig, ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_6_ANIMATED, " Anim");
      debugFigFlag(sb, fig, ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_7_SUB_FIGURE, " Sub");
      return sb.toString();
   }

   public static void debugFigFlag(StringBBuilder sb, ByteObject p, int offset, int flag, String str) {
      if (p.hasFlag(offset, flag)) {
         sb.append(str);
      }
   }

   public static String debugFigPerfFlag(ByteObject p) {
      StringBBuilder sb = new StringBBuilder(p.getUCtx());
      debugFigFlag(sb, p, ITechFigure.FIG__OFFSET_03_FLAGP, ITechFigure.FIG_FLAGP_1RGB, " RGB");
      debugFigFlag(sb, p, ITechFigure.FIG__OFFSET_03_FLAGP, ITechFigure.FIG_FLAGP_2EXTRA_BOUNDARY, " Overfill");
      debugFigFlag(sb, p, ITechFigure.FIG__OFFSET_03_FLAGP, ITechFigure.FIG_FLAGP_3OPAQUE, " Opaque");
      debugFigFlag(sb, p, ITechFigure.FIG__OFFSET_03_FLAGP, ITechFigure.FIG_FLAGP_4SUBFIGURE, " SubFigures");
      debugFigFlag(sb, p, ITechFigure.FIG__OFFSET_03_FLAGP, ITechFigure.FIG_FLAGP_5IGNORE_ALPHA, " IgnoreAlpha");
      return sb.toString();
   }

   public static String debugScaleID(int id) {
      switch (id) {
         case ITechScaler.SCALER_ID_0_LINEAR:
            return "Linear";
         case ITechScaler.SCALER_ID_1_BI_LINEAR:
            return "Bi Linear";
         case ITechScaler.SCALER_ID_2_BI_CUBIC:
            return "Bi Cubic";
         default:
            return "Unknown = " + id;
      }
   }

   public static String toStringInterpol(int key) {

      switch (key) {
         case ITechSkew.SKEW_TYPE_0_NEAREST_NEIGHBOUR:
            return "Nearest neighbour";
         case ITechSkew.SKEW_TYPE_1_BILINEAR:
            return "BiLinear";
         case ITechSkew.SKEW_TYPE_2_BILINEAR_4SPLIT:
            return "BiLinearSplit4";
         default:
            return "Unknown Interpolation " + key;
      }
   }

   public static String toStringEdge(int key) {
      switch (key) {
         case ITechSkew.SKEW_EDGE_0_ZERO:
            return "Zero";
         case ITechSkew.SKEW_EDGE_1_CLAMP:
            return "Clamp";
         case ITechSkew.SKEW_EDGE_2_WRAP:
            return "Wrap";
         case ITechSkew.SKEW_EDGE_3_FULLY_TRANS_PIXEL:
            return "Clamp Trans";
         case ITechSkew.SKEW_EDGE_4_PIXEL:
            return "Pixel";
         case ITechSkew.SKEW_EDGE_5_WHITE:
            return "White";
         default:
            return "Unknown Edge " + key;
      }
   }

   public static String debugBlend(int mode) {
      switch (mode) {
         case ITechBlend.BLENDING_00_OVER:
            return "OVER";
         case ITechBlend.BLENDING_01_SRC:
            return "SRC";
         case ITechBlend.BLENDING_04_MERGE_ARGB:
            return "MergeARGB";
         case ITechBlend.BLENDING_05_BEHIND:
            return "Behind";
         case ITechBlend.BLENDING_07_INVERSE:
            return "Inverse";
         case ITechBlend.BLENDING_08_DISSOLVE:
            return "Dissolve";
         case ITechBlend.BLENDING_09_INVERSE:
            return "Inverse";
         case ITechBlend.BLENDING_10_HUE:
            return "Hue";
         case ITechBlend.BLENDING_11_HUE_SAT:
            return "Hue Saturation";
         case ITechBlend.BLENDING_12_HUE_LUM:
            return "Hue Luminance";
         case ITechBlend.BLENDING_13_SATURATION:
            return "Saturation";
         case ITechBlend.BLENDING_14_SAT_LUM:
            return "SaturationLuminance";
         case ITechBlend.BLENDING_15_LUMINANCE:
            return "Luminance";
         case ITechBlend.BLENDING_16_MULTIPLY_BURN:
            return "Multiply Burn";
         case ITechBlend.BLENDING_17_COLOR_BURN:
            return "Color Burn";
         case ITechBlend.BLENDING_18_LINEAR_BURN:
            return "Luminance";
         case ITechBlend.BLENDING_19_SCREEN_DODGE:
            return "Screen Dodge";
         case ITechBlend.BLENDING_20_COLOR_DODGE:
            return "Color Dodge";
         case ITechBlend.BLENDING_21_LINEAR_DODGE:
            return "Linear Dodge";
         case ITechBlend.BLENDING_22_HARD_MIX:
            return "Hard Mix";
         case ITechBlend.BLENDING_23_ADDITION:
            return "Addition";
         case ITechBlend.BLENDING_26_:
            return "Substraction";
         case ITechBlend.BLENDING_25_DIVIDE:
            return "Divide";
         case ITechBlend.BLENDING_24_DIFFERENCE:
            return "Difference";
         case ITechBlend.BLENDING_27_EXCLUSION_NEGATION:
            return "Exclusion";
         case ITechBlend.BLENDING_28_:
            return "_";
         case ITechBlend.BLENDING_29_PIN_LIGHT:
            return "Pin Light";
         case ITechBlend.BLENDING_02_DARKEN:
            return "Darken";
         case ITechBlend.BLENDING_03_LIGHTEN:
            return "Lighten";

         default:
            return "Unknown " + mode;
      }
   }

   public static String debugAlpha(int mode) {
      switch (mode) {
         case ITechBlend.ALPHA_0_OVER:
            return "Over";
         default:
            return "Unknown " + mode;
      }
   }

   public static String debugOpacity(int op) {
      switch (op) {
         case ITechBlend.OPACITY_00_SRC:
            return "src";
         case ITechBlend.OPACITY_01_MIN_OVERIDE_SRC:
            return "min src";
         case ITechBlend.OPACITY_02_MAX_OVERIDE_SRC:
            return "max src";
         case ITechBlend.OPACITY_03_OVERIDE_SRC:
            return "overide src";
         default:
            return "Unknown " + op;
      }
   }

   public static String debugOpDuff(int op) {
      switch (op) {
         case ITechBlend.OP_00_SRC_OVER:
            return "src over";
         case ITechBlend.OP_01_SRC:
            return "src";
         case ITechBlend.OP_02_SRC_IN:
            return "src in";
         case ITechBlend.OP_03_SRC_OUT:
            return "src out";
         case ITechBlend.OP_04_SRC_ATOP:
            return "src atop";
         case ITechBlend.OP_05_DST_OVER:
            return "dest over";
         case ITechBlend.OP_06_DST:
            return "dst";
         case ITechBlend.OP_07_DST_IN:
            return "dst in";
         case ITechBlend.OP_08_DST_OUT:
            return "dst out";
         case ITechBlend.OP_09_DST_ATOP:
            return "dst atop";
         case ITechBlend.OP_10_XOR:
            return "xor";
         case ITechBlend.OP_11_CLEAR:
            return "clear";
         default:
            return "Unknown " + op;
      }
   }
}
