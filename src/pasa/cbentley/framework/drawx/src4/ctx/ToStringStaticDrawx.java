/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.logging.ToStringStaticBase;
import pasa.cbentley.core.src4.structs.IntToStrings;
import pasa.cbentley.framework.coredraw.src4.ctx.ToStringStaticCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOMask;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOPass;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringDrw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.framework.drawx.src4.style.IBOStyle;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
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
public class ToStringStaticDrawx extends ToStringStaticBase {

   public static String a_Template(int type) {
      String str = a_TemplateNull(type);
      if (str == null) {
         str = "Unknown stringerType " + type;
      }
      return str;
   }

   public static String a_TemplateNull(int type) {
      switch (type) {
         case 0:
            return "";
         default:
            return null;
      }
   }

   public static String toStringFigFlag(int type) {
      switch (type) {
         case IBOFigure.FIG_FLAG_1_ANCHOR:
            return "Anchor";
         case IBOFigure.FIG_FLAG_2_GRADIENT:
            return "Gradient";
         case IBOFigure.FIG_FLAG_3_COLOR_ARRAY:
            return "ColorArray";
         case IBOFigure.FIG_FLAG_4_MASK:
            return "Mask";
         case IBOFigure.FIG_FLAG_5_FILTER:
            return "Filter";
         case IBOFigure.FIG_FLAG_6_ANIMATED:
            return "Animated";
         case IBOFigure.FIG_FLAG_7_SUB_FIGURE:
            return "SubFigure";
         case IBOFigure.FIG_FLAG_8_:
            return "8";
         default:
            return "Unknown FigFlag" + type;
      }
   }
   public static String toStringFigFlagP(int type) {
      switch (type) {
         case IBOFigure.FIG_FLAGP_1_RGB:
            return "RGB";
         case IBOFigure.FIG_FLAGP_2_EXTRA_BOUNDARY:
            return "ExtraBoundary";
         case IBOFigure.FIG_FLAGP_3_OPAQUE:
            return "Opaque";
         case IBOFigure.FIG_FLAGP_4_IS_SUBFIGURE:
            return "IsSubFigure";
         case IBOFigure.FIG_FLAGP_5_IGNORE_ALPHA:
            return "IgnoreAlpha";
         case IBOFigure.FIG_FLAGP_6_NO_FIT:
            return "NoFit";
         case IBOFigure.FIG_FLAGP_7_TRANS_FIG:
            return "TransFig";
         case IBOFigure.FIG_FLAGP_8_POSTPONE:
            return "PostPone";
         default:
            return "Unknown FigFlag" + type;
      }
   }
   public static String toStringFigFlagZ(int type) {
      switch (type) {
         case IBOFigure.FIG_FLAGZ_1_DEFINED_BLENDER:
            return "Blender";
         case IBOFigure.FIG_FLAGZ_2_:
            return "2";
         case IBOFigure.FIG_FLAGZ_3_:
            return "3";
         case IBOFigure.FIG_FLAGZ_4_:
            return "4";
         case IBOFigure.FIG_FLAGZ_5_:
            return "5";
         case IBOFigure.FIG_FLAGZ_6_:
            return "6";
         case IBOFigure.FIG_FLAGZ_7_AXIS:
            return "Axis";
         case IBOFigure.FIG_FLAGZ_8_DIRECTION:
            return "Direction";
         default:
            return "Unknown FigFlag" + type;
      }
   }
   public static String toStringFigFlagX(int type) {
      switch (type) {
         case IBOFigure.FIG_FLAGX_1_AREA_FUNCTION:
            return "";
         case IBOFigure.FIG_FLAGX_2_CLIP:
            return "";
         case IBOFigure.FIG_FLAGX_3_:
            return "";
         case IBOFigure.FIG_FLAGX_4_OPAQUE_COLORS:
            return "";
         case IBOFigure.FIG_FLAGX_5_SCALER:
            return "";
         case IBOFigure.FIG_FLAGX_6_:
            return "";
         case IBOFigure.FIG_FLAGX_7_ALIAS_ON:
            return "";
         case IBOFigure.FIG_FLAGX_8_ALIAS_OFF:
            return "";
         default:
            return "Unknown FigFlag" + type;
      }
   }
   /**
    * Info about anchor
    * @return
    */
   public static String debugFigFlag(ByteObject fig) {
      StringBBuilder sb = new StringBBuilder(fig.getUCtx());
      debugFigFlag(sb, fig, IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_1_ANCHOR, " Anchor");
      debugFigFlag(sb, fig, IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_2_GRADIENT, " Gradient");
      debugFigFlag(sb, fig, IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_3_COLOR_ARRAY, " ColorArray");
      debugFigFlag(sb, fig, IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_4_MASK, " Mask");
      debugFigFlag(sb, fig, IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_5_FILTER, " Filter");
      debugFigFlag(sb, fig, IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_6_ANIMATED, " Anim");
      debugFigFlag(sb, fig, IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_7_SUB_FIGURE, " Sub");
      return sb.toString();
   }

   public static void debugFigFlag(StringBBuilder sb, ByteObject p, int offset, int flag, String str) {
      if (p.hasFlag(offset, flag)) {
         sb.append(str);
      }
   }

   public static String debugFigPerfFlag(ByteObject p) {
      StringBBuilder sb = new StringBBuilder(p.getUCtx());
      debugFigFlag(sb, p, IBOFigure.FIG__OFFSET_03_FLAGP, IBOFigure.FIG_FLAGP_1_RGB, " RGB");
      debugFigFlag(sb, p, IBOFigure.FIG__OFFSET_03_FLAGP, IBOFigure.FIG_FLAGP_2_EXTRA_BOUNDARY, " Overfill");
      debugFigFlag(sb, p, IBOFigure.FIG__OFFSET_03_FLAGP, IBOFigure.FIG_FLAGP_3_OPAQUE, " Opaque");
      debugFigFlag(sb, p, IBOFigure.FIG__OFFSET_03_FLAGP, IBOFigure.FIG_FLAGP_4_IS_SUBFIGURE, " SubFigures");
      debugFigFlag(sb, p, IBOFigure.FIG__OFFSET_03_FLAGP, IBOFigure.FIG_FLAGP_5_IGNORE_ALPHA, " IgnoreAlpha");
      return sb.toString();
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


   public static String toStringFontBrackets(IMFont f) {
      if (f == null) {
         return "NULL FONT";
      }
      String s = "[" + ToStringStaticCoreDraw.debugFontFace(f.getFace()) + " " + ToStringStaticCoreDraw.debugFontStyle(f.getStyle()) + " " + ToStringStaticCoreDraw.debugFontSize(f.getSize()) + "]";
      return s;
   }

   public static String stringerStateFlag(int flag) {
      String str = toStringStringerStateFlagNull(flag);
      if (str == null) {
         str = "Unknown stringerStateFlag " + flag;
      }
      return str;
   }
   public static IntToStrings flagsFigureFlag(UCtx uc) {
      IntToStrings itos = new IntToStrings(uc);
      itos.add(IBOFigure.FIG__OFFSET_02_FLAG, toStringFigFlag(IBOFigure.FIG_FLAG_1_ANCHOR));
      itos.add(IBOFigure.FIG__OFFSET_02_FLAG, toStringFigFlag(IBOFigure.FIG_FLAG_2_GRADIENT));
      itos.add(IBOFigure.FIG__OFFSET_02_FLAG, toStringFigFlag(IBOFigure.FIG_FLAG_3_COLOR_ARRAY));
      itos.add(IBOFigure.FIG__OFFSET_02_FLAG, toStringFigFlag(IBOFigure.FIG_FLAG_4_MASK));
      itos.add(IBOFigure.FIG__OFFSET_02_FLAG, toStringFigFlag(IBOFigure.FIG_FLAG_5_FILTER));
      itos.add(IBOFigure.FIG__OFFSET_02_FLAG, toStringFigFlag(IBOFigure.FIG_FLAG_6_ANIMATED));
      itos.add(IBOFigure.FIG__OFFSET_02_FLAG, toStringFigFlag(IBOFigure.FIG_FLAG_7_SUB_FIGURE));
      itos.add(IBOFigure.FIG__OFFSET_02_FLAG, toStringFigFlag(IBOFigure.FIG_FLAG_8_));
      return itos;
   }
   public static IntToStrings flagsFigureFlagP(UCtx uc) {
      IntToStrings itos = new IntToStrings(uc);
      itos.add(IBOFigure.FIG__OFFSET_03_FLAGP, toStringFigFlag(IBOFigure.FIG_FLAGP_1_RGB));
      itos.add(IBOFigure.FIG__OFFSET_03_FLAGP, toStringFigFlag(IBOFigure.FIG_FLAGP_2_EXTRA_BOUNDARY));
      itos.add(IBOFigure.FIG__OFFSET_03_FLAGP, toStringFigFlag(IBOFigure.FIG_FLAGP_3_OPAQUE));
      itos.add(IBOFigure.FIG__OFFSET_03_FLAGP, toStringFigFlag(IBOFigure.FIG_FLAGP_4_IS_SUBFIGURE));
      itos.add(IBOFigure.FIG__OFFSET_03_FLAGP, toStringFigFlag(IBOFigure.FIG_FLAGP_5_IGNORE_ALPHA));
      itos.add(IBOFigure.FIG__OFFSET_03_FLAGP, toStringFigFlag(IBOFigure.FIG_FLAGP_6_NO_FIT));
      itos.add(IBOFigure.FIG__OFFSET_03_FLAGP, toStringFigFlag(IBOFigure.FIG_FLAGP_7_TRANS_FIG));
      itos.add(IBOFigure.FIG__OFFSET_03_FLAGP, toStringFigFlag(IBOFigure.FIG_FLAGP_8_POSTPONE));
      return itos;
   }
   public static IntToStrings flagsFigureFlagZ(UCtx uc) {
      IntToStrings itos = new IntToStrings(uc);
      itos.add(IBOFigure.FIG__OFFSET_07_FLAGZ1, toStringFigFlag(IBOFigure.FIG_FLAGZ_1_DEFINED_BLENDER));
      itos.add(IBOFigure.FIG__OFFSET_07_FLAGZ1, toStringFigFlag(IBOFigure.FIG_FLAGZ_2_));
      itos.add(IBOFigure.FIG__OFFSET_07_FLAGZ1, toStringFigFlag(IBOFigure.FIG_FLAGZ_3_));
      itos.add(IBOFigure.FIG__OFFSET_07_FLAGZ1, toStringFigFlag(IBOFigure.FIG_FLAGZ_4_));
      itos.add(IBOFigure.FIG__OFFSET_07_FLAGZ1, toStringFigFlag(IBOFigure.FIG_FLAGZ_5_));
      itos.add(IBOFigure.FIG__OFFSET_07_FLAGZ1, toStringFigFlag(IBOFigure.FIG_FLAGZ_6_));
      itos.add(IBOFigure.FIG__OFFSET_07_FLAGZ1, toStringFigFlag(IBOFigure.FIG_FLAGZ_7_AXIS));
      itos.add(IBOFigure.FIG__OFFSET_07_FLAGZ1, toStringFigFlag(IBOFigure.FIG_FLAGZ_8_DIRECTION));
      return itos;
   }
   public static IntToStrings flagsFigureFlagX(UCtx uc) {
      IntToStrings itos = new IntToStrings(uc);
      itos.add(IBOFigure.FIG__OFFSET_04_FLAGX, toStringFigFlag(IBOFigure.FIG_FLAGX_1_AREA_FUNCTION));
      itos.add(IBOFigure.FIG__OFFSET_04_FLAGX, toStringFigFlag(IBOFigure.FIG_FLAGX_2_CLIP));
      itos.add(IBOFigure.FIG__OFFSET_04_FLAGX, toStringFigFlag(IBOFigure.FIG_FLAGX_3_));
      itos.add(IBOFigure.FIG__OFFSET_04_FLAGX, toStringFigFlag(IBOFigure.FIG_FLAGX_4_OPAQUE_COLORS));
      itos.add(IBOFigure.FIG__OFFSET_04_FLAGX, toStringFigFlag(IBOFigure.FIG_FLAGX_5_SCALER));
      itos.add(IBOFigure.FIG__OFFSET_04_FLAGX, toStringFigFlag(IBOFigure.FIG_FLAGX_6_));
      itos.add(IBOFigure.FIG__OFFSET_04_FLAGX, toStringFigFlag(IBOFigure.FIG_FLAGX_7_ALIAS_ON));
      itos.add(IBOFigure.FIG__OFFSET_04_FLAGX, toStringFigFlag(IBOFigure.FIG_FLAGX_8_ALIAS_OFF));
      return itos;
   }
   

   public static IntToStrings stringerStateFlagMap(UCtx uc) {
      IntToStrings flags = new IntToStrings(uc);
      int flag = ITechStringer.STATE_01_CHAR_EFFECTS;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_02_CHAR_WIDTHS;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_03_CHECK_CLIP;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_04_TRIMMED;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_05_STR_WIDTH;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_06_CHAR_POSITIONS;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_07_BROKEN;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_08_ACTIVE_STYLE;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_29_MODEL_WORD_FX;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_10_ACTIVE_DYNAMIC_STYLE;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_11_DIFFERENT_FONTS;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_13_FX;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_14_BASIC_POSITIONING;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_15_BG_DEFINED;
      flags.add(flag, stringerStateFlag(flag));
      flag = ITechStringer.STATE_16_STATIC_INDEX_FX;
      flags.add(flag, stringerStateFlag(flag));
      return flags;

   }

   public static String toStringAlign(int a) {
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

   public static String toStringColor(int c) {
      return "(" + ((c >> 24) & 0xFF) + "," + ((c >> 16) & 0xFF) + "," + ((c >> 8) & 0xFF) + "," + (c & 0xFF) + ")";
   }

   public static String toStringDrwType(final int type) {
      switch (type) {
         case IBOTypesDrawX.TYPE_DRWX_00_FIGURE:
            return "FIGURE";
         case IBOTypesDrawX.TYPE_DRWX_01_BOX:
            return "ANCHOR";
         case IBOTypesDrawX.TYPE_DRWX_06_MASK:
            return "MASK";
         case IBOTypesBOC.TYPE_038_GRADIENT:
            return "GRADIENT";
         case IBOTypesDrawX.TYPE_DRWX_11_TEXT_EFFECTS:
            return "TEXT_EFFECTS";
         case IBOTypesBOC.TYPE_040_COLOR_FILTER:
            return "COLOR_FILTER";
         default:
            return "UNKNOWN " + type;
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

   public static String toStringFigType(final int type) {
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
         case ITechFigure.FIG_TYPE_03_TRIANGLE:
            return "Triangle";
         case ITechFigure.FIG_TYPE_12_ARROW:
            return "ARROW";
         case ITechFigure.FIG_TYPE_09_PIXELS:
            return "PIXELS";
         default:
            return "UNKNOWN FIGURE NAME " + type;
      }
   }

   public static String toStringFxScope(int scope) {
      switch (scope) {
         case ITechStringDrw.FX_SCOPE_1_CHAR:
            return "Char";
         case ITechStringDrw.FX_SCOPE_2_WORD:
            return "Word";
         case ITechStringDrw.FX_SCOPE_3_PARA:
            return "Paragraph";
         case ITechStringDrw.FX_SCOPE_0_TEXT:
            return "Text";
         default:
            return "Unknown scope " + scope;
      }
   }

   public static String toStringImplicit(int value) {
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

   public static String toStringLineWrap(int type) {
      switch (type) {
         case ITechStringer.LINEWRAP_0_NONE:
            return "None";
         case ITechStringer.LINEWRAP_1_ANYWHERE:
            return "Anywhere";
         default:
            return "Unknown LineWrap" + type;
      }
   }

   public static String toStringMaskBlend(int blend) {
      switch (blend) {
         case IBOMask.MASK_BLEND_0:
            return "0";
         case IBOMask.MASK_BLEND_1:
            return "2";
         case IBOMask.MASK_BLEND_2:
            return "2";
         default:
            throw new IllegalArgumentException("Blend " + blend);
      }

   }

   public static String toStringMaskPreset(int preset) {
      switch (preset) {
         case IBOMask.MASK_PRESET_0HAL0:
            return "0";
         case IBOMask.MASK_PRESET_1HAL0:
            return "2";
         case IBOMask.MASK_PRESET_2HAL0:
            return "2";
         case IBOMask.MASK_PRESET_3HAL0:
            return "3";
         default:
            throw new IllegalArgumentException("Preset " + preset);
      }
   }

   public static String toStringNewLineManager(int type) {
      switch (type) {
         case ITechStringer.NEWLINE_MANAGER_0_IGNORE:
            return "Ignore";
         case ITechStringer.NEWLINE_MANAGER_1_WORK:
            return "Work";
         case ITechStringer.NEWLINE_MANAGER_2_WORD:
            return "Word";
         default:
            return "Unknown NewLineManager" + type;
      }
   }

   public static String toStringPaintMode(int mode) {
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

   public static String toStringPass(int p) {
      switch (p) {
         case IBOPass.PASS_0_FIGURE:
            return "Figure";
         case IBOPass.PASS_1_MOSAIC:
            return "Mosaic";
         case IBOPass.PASS_2_SKEW:
            return "Skew";
         case IBOPass.PASS_3_SCALE:
            return "Scale";
         case IBOPass.PASS_4_ROTATE:
            return "Rotate";
         default:
            return "UnknownPass " + p;
      }
   }

   public static String toStringScaleID(int id) {
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

   public static String toStringScaleType(int type) {
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

   public static String toStringSpaceTrim(int type) {
      switch (type) {
         case ITechStringer.SPACETRIM_0_NONE:
            return "None";
         case ITechStringer.SPACETRIM_1_NORMAL:
            return "Normal";
         case ITechStringer.SPACETRIM_2_JUSTIFIED:
            return "Justified";
         default:
            return "Unknown SpaceTrim" + type;
      }
   }

   public static String toStringStringerStateFlagNull(int flag) {
      switch (flag) {
         case ITechStringer.STATE_01_CHAR_EFFECTS:
            return "CharEffect";
         case ITechStringer.STATE_02_CHAR_WIDTHS:
            return "CharWidths";
         case ITechStringer.STATE_03_CHECK_CLIP:
            return "CheckClip";
         case ITechStringer.STATE_04_TRIMMED:
            return "Trimmed";
         case ITechStringer.STATE_05_STR_WIDTH:
            return "StrWidth";
         case ITechStringer.STATE_06_CHAR_POSITIONS:
            return "CharPositions";
         case ITechStringer.STATE_07_BROKEN:
            return "Broken";
         case ITechStringer.STATE_08_ACTIVE_STYLE:
            return "ActiveStyle";
         case ITechStringer.STATE_09_:
            return "9";
         case ITechStringer.STATE_10_ACTIVE_DYNAMIC_STYLE:
            return "ActiveDynStyle";
         case ITechStringer.STATE_11_DIFFERENT_FONTS:
            return "DifferentFonts";
         case ITechStringer.STATE_12_:
            return "12";
         case ITechStringer.STATE_13_FX:
            return "Fx";
         case ITechStringer.STATE_14_BASIC_POSITIONING:
            return "BasicPositioning";
         case ITechStringer.STATE_15_BG_DEFINED:
            return "BgDefined";
         case ITechStringer.STATE_16_STATIC_INDEX_FX:
            return "StaticIndexFx";
         case ITechStringer.STATE_17_COMPUTED_FX:
            return "ComputedFx";
         case ITechStringer.STATE_18_FULL_MONOSPACE:
            return "FullMonospace";
         case ITechStringer.STATE_19_FX_SETUP:
            return "FxSetup";
         case ITechStringer.STATE_20_METERED_FULL:
            return "MeteredFull";
         case ITechStringer.STATE_21_ZERO_WIDTH_CHARS:
            return "21ZeroWidthChars";
         case ITechStringer.STATE_22_:
            return "22";
         case ITechStringer.STATE_23_:
            return "23";
         case ITechStringer.STATE_24_:
            return "24";
         case ITechStringer.STATE_25_:
            return "25";
         case ITechStringer.STATE_26_MODEL_SEPARATORS_FX:
            return "26";
         case ITechStringer.STATE_27_MODEL_SPACE_FX:
            return "26";
         case ITechStringer.STATE_28_MODEL_SENTENCE_FX:
            return "28";
         case ITechStringer.STATE_29_MODEL_WORD_FX:
            return "WordFx";
         case ITechStringer.STATE_30_PROTECTED:
            return "Protected";
         case ITechStringer.STATE_31_:
            return "31";
         case ITechStringer.STATE_32_:
            return "32";
         default:
            return null;
      }
   }

   public static String toStringStyleAnchor(int i) {
      switch (i) {
         case IBOStyle.STYLE_ANC_0_BORDER:
            return "AT_BORDER";
         case IBOStyle.STYLE_ANC_1_MARGIN:
            return "AT_MARGIN";
         case IBOStyle.STYLE_ANC_2_CONTENT:
            return "AT_CONTENT";
         case IBOStyle.STYLE_ANC_3_PADDING:
            return "AT_PADDING";
         default:
            return "INVALID_ERROR";
      }
   }

   public static String toStringTabManager(int type) {
      switch (type) {
         case ITechStringer.TAB_MANAGER_0_SINGLE_SPACE:
            return "SingleSpace";
         case ITechStringer.TAB_MANAGER_1_ESCAPED:
            return "Escaped";
         case ITechStringer.TAB_MANAGER_2_COLUMN:
            return "Column";
         case ITechStringer.TAB_MANAGER_3_NOTEPAD:
            return "Notepad";
         default:
            return "Unknown TabManager" + type;
      }
   }

   public static String toStringWordWrap(int type) {
      switch (type) {
         case ITechStringer.WORDWRAP_0_NONE:
            return "None";
         case ITechStringer.WORDWRAP_1_ANYWHERE:
            return "Any";
         case ITechStringer.WORDWRAP_2_NICE_WORD:
            return "NiceWord";
         case ITechStringer.WORDWRAP_3_NICE_HYPHENATION:
            return "Hyphenation";
         default:
            return "Unknown WordWrap" + type;
      }
   }
}
