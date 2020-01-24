package pasa.cbentley.framework.drawx.src4.engine;

import java.util.Random;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.tech.ITechBlend;
import pasa.cbentley.framework.drawx.src4.utils.HSBUtilz;

/**
 * Function class for blending pixels between 2 images.
 * <br>
 * F(x,y) function type.
 * <br>
 * <br>
 * Alpha has its own blending modes.
 * <br>
 * Each channel may be blended 
 * <br>
 * The Blend OP implements the Mask process.
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class BlendOp implements IStringable {

   protected static float[] blendRS = new float[3];

   static int               cacheDest;

   static int               cacheRest;

   static int               cacheSrc;

   public static String debugAlpha(int mode) {
      switch (mode) {
         case ITechBlend.ALPHA_0_OVER:
            return "Over";
         default:
            return "Unknown " + mode;
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
         case ITechBlend.BLENDING_06_INVERSE_ALPHA:
            return "InverseAlpha";
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

   private static int fctAddition(int blendRed, int baseRed) {
      return Math.min(blendRed + baseRed, 255);
   }

   /**
    * Divides the inverted src color by the base color, then inverts the result
    * .
    * Darkens the top layer increasing the contrast to reflect the color of the bottom layer.
    * The darker the bottom layer, the more its colour is used.
    * @param blendRed
    * @param baseRed
    * @return
    */
   private static int fctColorBurn(int src, int baseRed) {
      if (baseRed == 0) {
         return 0;
      }
      return 255 - (((255 - src) * 255) / (baseRed));
   }

   /**
    * Divide the bottom layer with the inverted top layer.
    * <br>
    * This decreases the contrast to make the bottom layer reflect the top layer: the brighter the top layer, the more its color affects
    * the bottom layer.
    * <li>Blending with white gives white.
    * <li>Blending with black does not change the image.
    * @param blendRed
    * @param baseRed
    * @return
    */
   private static int fctColorDodge(int src, int baseRed) {
      if (src == 255) {
         return 255;
      }
      return (baseRed * 255) / (255 - src);
   }

   /**
    * Takes the min of each RGB channel
    * @param blend
    * @param base
    * @return
    */
   public static int fctDarken(int blend, int base) {
      return Math.min(blend, base);
   }

   /**
    * similar to 
    * @param foreground
    * @param background
    * @return
    */
   private static int fctDifference(int foreground, int background) {
      return Math.abs(foreground - background);
   }

   /**
    * Takes random pixel
    * @param blend
    * @param base
    * @return
    */
   private static int fctDissolve(int blend, int base, Random r) {
      if (blend == 0)
         return base;
      int val = r.nextInt(blend) - 128;
      if (val > 0) {
         return blend;
      }
      return base;
   }

   private static int fctDivideDest(int dest, int src) {
      return Math.min(255, 256 * src / dest + 1);
   }

   /**
    * http://ie.technion.ac.il/CC/Gimp/node55.html
    * 
    * A division is normalize by 255 (here 256 because of the +1 to avoid dividing by zero
    * @param foreground
    * @param background
    * @return
    */
   private static int fctDivideSrc(int dest, int src) {
      return Math.min(255, 256 * dest / src + 1);
   }

   /**
    * Takes the green channel of blend. red and blue channel of base
    * @param blend
    * @param base
    * @return
    */
   private static int fctGreen(int blend, int base, float alpha) {
      int rBase = gr(base);
      int gBase = gg(base);
      int bBase = gb(base);
      int aBase = ga(base);
      int aRes = Math.min(255, aBase + ga(blend));
      return getPixMix(alpha, aBase, rBase, gBase, bBase, aRes, rBase, gg(blend), bBase);
   }

   /**
    * Combines Color Dodge and Color Burn
    * Dodge applies to values lighter than middle gray
    * Burn to values darker than middle gray.
    * @param blend
    * @param base
    * @return
    */
   public static int fctHardMix(int blend, int base) {
      if (blend > 127) {
         return fctColorDodge(blend, base);
      } else {
         return fctColorBurn(blend, base);
      }
   }

   private static int fctInverse(int base, int blend) {
      float redFix = (float) blend / (float) 255;
      float invRedFix = ((float) 1) - redFix;
      int redResult = (int) ((blend * invRedFix) + (base * redFix));
      return redResult;
   }

   private static int fctInverse16bits(int blend, int base) {
      int blendFix = 255 - blend;
      int baseFix = 255 - base;
      int redResult = (int) ((blend * baseFix) + (base * blendFix)) / 255;
      return redResult;
   }

   public static int fctLighten(int blend, int base) {
      return Math.max(blend, base);
   }

   //   /**
   //    * What about the Alpha blending?
   //    * <li>SRC alpha
   //    * <li>DEST alpha
   //    * <li>blend alpha
   //    * @param base
   //    * @param blend
   //    * @return
   //    */
   //   public static int mergeDarken(int base, int blend) {
   //      int blendAlpha = ((blend >> 24) & 0xFF);
   //      int blendRed = ((blend >> 16) & 0xFF);
   //      int blendGreen = ((blend >> 8) & 0xFF);
   //      int blendBlue = ((blend >> 0) & 0xFF);
   //      if (blendAlpha == 0) {
   //         return base;
   //      } else if (blendAlpha == 255) {
   //
   //      } else {
   //
   //      }
   //      int alphaResult = blendAlpha;
   //      int baseAlpha = ((base >> 24) & 0xFF);
   //      int baseRed = ((base >> 16) & 0xFF);
   //      int baseGreen = ((base >> 8) & 0xFF);
   //      int baseBlue = ((base >> 0) & 0xFF);
   //      int redResult = fctDarken(blendRed, baseRed);
   //      int greenResult = fctDarken(blendGreen, baseGreen);
   //      int blueResult = fctDarken(blendBlue, baseBlue);
   //
   //      //values are computed in base 16.
   //      int val = (alphaResult << 24) + (redResult << 16) + (greenResult << 8) + (blueResult);
   //      return val;
   //   }

   /**
    * Sums the values and substracts 1.
    * @param blendRed
    * @param baseRed
    * @return
    */
   private static int fctLinearBurn(int blend, int base) {
      return Math.max((blend + base) - 255, 0);
   }

   /**
    * Same as Sum
    * @param blend
    * @param base
    * @return
    */
   private static int fctLinearDodge(int blend, int base) {
      return fctAddition(blend, base);
   }

   /**
    * Add both pixel and substract their multiplication.
    * Return a base16.
    * <br>
    * Similar to lighten since it increase the light. Black disapears.
    * But different
    * @param blend
    * @param base
    * @return
    */
   public static int fctMerge(int blend, int base) {
      return ((blend << 8) + (base << 8) - (blend * base)) & 0xFF00;
   }

   /**
    * Blacks stay blacks. Darkens colors
    * @param blendRed
    * @param baseRed
    * @return
    */
   private static int fctMultiply(int dest, int src) {
      return (dest * src) / 255;
   }

   /**
    * Overlay is a combination of screen and multiply
    * @param dest
    * @param src
    * @return
    */
   private static int fctOverlayDest(int dest, int src) {
      int m = fctMultiply(dest, src);
      int sc = fctScreenFct(dest, src);
      return (dest * sc + (1 - dest) * m) / 255;
   }

   private static int fctOverlaySrc(int dest, int src) {
      int m = fctMultiply(dest, src);
      int sc = fctScreenFct(dest, src);
      return (src * sc + (1 - src) * m) / 255;
   }

   private static int fctScreenFct(int dest, int src) {
      return 255 - (((255 - dest) * (255 - src)) / 255);
   }

   private static int fctSubstract(int foreground, int background) {
      return Math.max(background - foreground, 0);
   }

   private static int ga(int px) {
      return ((px >> 24) & 0xFF);
   }

   private static int gb(int px) {
      return ((px >> 0) & 0xFF);
   }

   protected static int getAlphaFixed(int base, int blend, int baseRed, int baseGreen, int baseBlue, int redResult, int greenResult, int blueResult) {
      int blendAlpha = ((blend >> 24) & 0xFF);
      int baseAlpha = ((base >> 24) & 0xFF);
      int alphaResult16 = 0;
      if (blendAlpha == 0) {
         return base;
      } else {
         alphaResult16 = ((blendAlpha << 8) + (baseAlpha << 8) - (blendAlpha * baseAlpha)) & 0xFF00;
      }

      if (blendAlpha != 255) {
         float alphaFix = (float) blendAlpha / (float) 255;
         float invAlphaFix = ((float) 1) - alphaFix;
         //fix alpha
         redResult = BlendOp.overOperator(baseRed & 0xFF, redResult & 0xFF, alphaFix, invAlphaFix) & 0xFF;
         greenResult = BlendOp.overOperator(baseGreen & 0xFF, greenResult & 0xFF, alphaFix, invAlphaFix) & 0xFF;
         blueResult = BlendOp.overOperator(baseBlue & 0xFF, blueResult & 0xFF, alphaFix, invAlphaFix) & 0xFF;

      }
      //values are computed in base 16.
      int val = (alphaResult16 << 16) + (redResult << 16) + (greenResult << 8) + (blueResult);
      return val;
   }


   /**
    * Default mixing method. other methods are possible.
    * @param alpha
    * @param baseA
    * @param baseRed
    * @param baseGreen
    * @param baseBlue
    * @param blendA
    * @param redResult
    * @param greenResult
    * @param blueResult
    * @return
    */
   protected static int getPixMix(float alpha, int baseA, int baseRed, int baseGreen, int baseBlue, int blendA, int redResult, int greenResult, int blueResult) {
      int rA = (int) (baseA + (blendA - baseA) * alpha) & 0xFF;
      int rR = (int) (baseRed + (redResult - baseRed) * alpha) & 0xFF;
      int rG = (int) (baseGreen + (greenResult - baseGreen) * alpha) & 0xFF;
      int rB = (int) (baseBlue + (blueResult - baseA) * baseBlue) & 0xFF;
      int val = (rA << 24) + (rR << 16) + (rG << 8) + (rB);
      return val;
   }

   protected static int getPixMix(int dest, int src, float alpha) {
      return (int) (dest + (src - dest) * alpha) & 0xFF;
   }

   private static int gg(int px) {
      return ((px >> 8) & 0xFF);
   }

   private static int gr(int px) {
      return (px >> 16 & 0xFF);
   }

   public static int mergeDarken(int base, int blend) {

      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      int redResult = fctDarken(blendRed, baseRed);
      int greenResult = fctDarken(blendGreen, baseGreen);
      int blueResult = fctDarken(blendBlue, baseBlue);

      return getAlphaFixed(base, blend, baseRed, baseGreen, baseBlue, redResult, greenResult, blueResult);
   }

   public static int mergeLighten(int base, int blend) {

      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      //int alphaResult = Math.max(blendAlpha,baseAlpha);

      int redResult = Math.max(blendRed, baseRed);
      int greenResult = Math.max(blendGreen, baseGreen);
      int blueResult = Math.max(blendBlue, baseBlue);

      return getAlphaFixed(base, blend, baseRed, baseGreen, baseBlue, redResult, greenResult, blueResult);
   }

   /**
    * Multiply
    * @param base
    * @param blend
    * @return
    */
   public static int mergePixelsARGB(int base, int blend) {
      int blendAlpha = ((blend >> 24) & 0xFF);
      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseAlpha = ((base >> 24) & 0xFF);
      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      //totally transparent
      if (blendAlpha == 0) {
         return base;
      }
      //work in base 256
      //baseAlpha = 0;
      int alphaResult = ((blendAlpha << 8) + (baseAlpha << 8) - (blendAlpha * baseAlpha)) & 0xFF00;

      int redResult = ((blendRed << 8) + (baseRed << 8) - (blendRed * baseRed)) & 0xFF00;
      int greenResult = ((blendGreen << 8) + (baseGreen << 8) - (blendGreen * baseGreen)) & 0xFF00;
      int blueResult = ((blendBlue << 8) + (baseBlue << 8) - (blendBlue * baseBlue)) & 0xFF00;

      //values are computed in base 16.
      int val = (alphaResult << 16) + (redResult << 8) + (greenResult << 0) + (blueResult >> 8);

      return val;
   }

   public static int mergePixelsDifference(int base, int blend) {
      int blendAlpha = ((blend >> 24) & 0xFF);
      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseAlpha = ((base >> 24) & 0xFF);
      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      int alphaResult = fctDifference(blendAlpha, baseAlpha);
      int redResult = fctDifference(blendRed, baseRed);
      int greenResult = fctDifference(blendGreen, baseGreen);
      int blueResult = fctDifference(blendBlue, baseBlue);
      int val = (alphaResult << 16) + (redResult << 16) + (greenResult << 8) + (blueResult << 0);
      return val;
   }

   public static int mergePixelsDissolve(int base, int blend, Random r) {
      int blendAlpha = ((blend >> 24) & 0xFF);
      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseAlpha = ((base >> 24) & 0xFF);
      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      int alphaResult = fctDissolve(blendAlpha, baseAlpha, r);
      int redResult = fctDissolve(blendRed, baseRed, r);
      int greenResult = fctDissolve(blendGreen, baseGreen, r);
      int blueResult = fctDissolve(blendBlue, baseBlue, r);
      int val = (alphaResult << 16) + (redResult << 16) + (greenResult << 8) + (blueResult << 0);
      return val;
   }

   public static int mergePixelsDivide(int base, int blend) {
      int blendAlpha = ((blend >> 24) & 0xFF);
      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseAlpha = ((base >> 24) & 0xFF);
      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      int alphaResult = fctDivideSrc(blendAlpha, baseAlpha);
      int redResult = fctDivideSrc(blendRed, baseRed);
      int greenResult = fctDivideSrc(blendGreen, baseGreen);
      int blueResult = fctDivideSrc(blendBlue, baseBlue);
      int val = (alphaResult << 16) + (redResult << 16) + (greenResult << 8) + (blueResult << 0);
      return val;
   }

   public static int mergePixelsHardMix(int base, int blend) {
      int blendAlpha = ((blend >> 24) & 0xFF);
      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseAlpha = ((base >> 24) & 0xFF);
      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      int alphaResult = fctHardMix(blendAlpha, baseAlpha);
      int redResult = fctHardMix(blendRed, baseRed);
      int greenResult = fctHardMix(blendGreen, baseGreen);
      int blueResult = fctHardMix(blendBlue, baseBlue);

      int val = (alphaResult << 24) + (redResult << 16) + (greenResult << 8) + (blueResult << 0);

      return val;
   }

   /**
    * Do the hue on base blended
    * @param base
    * @param blend
    * @param baseR
    * @param blendR
    * @return
    */
   public static int mergePixelsHSBHue(int base, int blend, float[] baseR, float[] blendR) {

      HSBUtilz.RGBtoHSB(base, baseR);
      HSBUtilz.RGBtoHSB(blend, blendR);
      //hue, saturation, luminance
      return HSBUtilz.HSBtoRGB(baseR[0], baseR[1], blendR[2]);

   }

   public static int mergePixelsHSBHueLum(int base, int blend, float[] baseR, float[] blendR) {
      HSBUtilz.RGBtoHSB(base, baseR);
      HSBUtilz.RGBtoHSB(blend, blendR);
      //hue, saturation, luminance
      return HSBUtilz.HSBtoRGB(baseR[0], baseR[1], blendR[2]);

   }

   public static int mergePixelsHSBHueSat(int base, int blend, float[] baseR, float[] blendR) {
      HSBUtilz.RGBtoHSB(base, baseR);
      HSBUtilz.RGBtoHSB(blend, blendR);
      //hue, saturation, luminance
      return HSBUtilz.HSBtoRGB(blendR[0], blendR[1], baseR[2]);
   }

   public static int mergePixelsHSBLum(int base, int blend, float[] baseR, float[] blendR) {
      HSBUtilz.RGBtoHSB(base, baseR);
      HSBUtilz.RGBtoHSB(blend, blendR);
      //hue, saturation, luminance
      return HSBUtilz.HSBtoRGB(baseR[0], baseR[1], blendR[2]);

   }

   public static int mergePixelsHSBSat(int base, int blend, float[] baseR, float[] blendR) {
      HSBUtilz.RGBtoHSB(base, baseR);
      HSBUtilz.RGBtoHSB(blend, blendR);
      //hue, saturation, luminance
      return HSBUtilz.HSBtoRGB(baseR[0], baseR[1], blendR[2]);

   }

   /**
    * 
    * @param base
    * @param blend
    * @param baseR
    * @param blendR
    * @return
    */
   public static int mergePixelsHSBSatLum(int base, int blend, float[] baseR, float[] blendR) {
      HSBUtilz.RGBtoHSB(base, baseR);
      HSBUtilz.RGBtoHSB(blend, blendR);
      //hue, saturation, luminance
      return HSBUtilz.HSBtoRGB(baseR[0], baseR[1], blendR[2]);

   }

   public static int mergePixelsInverse(int base, int blend) {
      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      int redResult = fctInverse(baseRed, blendRed);
      int greenResult = fctInverse(baseGreen, blendGreen);
      int blueResult = fctInverse(baseBlue, blendBlue);

      return getAlphaFixed(base, blend, baseRed, baseGreen, baseBlue, redResult, greenResult, blueResult);

   }

   public static int mergePixelsMultiply(int base, int blend) {
      int blendAlpha = ((blend >> 24) & 0xFF);
      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseAlpha = ((base >> 24) & 0xFF);
      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      int alphaResult = fctMultiply(blendAlpha, baseAlpha);
      int redResult = fctMultiply(blendRed, baseRed);
      int greenResult = fctMultiply(blendGreen, baseGreen);
      int blueResult = fctMultiply(blendBlue, baseBlue);
      int val = (alphaResult << 16) + (redResult << 16) + (greenResult << 8) + (blueResult << 0);
      return val;
   }

   /**
    * Merge two pixels using the classical function. 
    * <br>
    * <br>
    * Blend depending on alpha value
    * <br>
    * <br>
    * @param bgPixel is opaque
    * @param newPixel
    * @return
    */
   public static int mergePixelsOver(int bgPixel, int newPixel) {
      int newAlpha = ((newPixel >> 24) & 0xFF);
      if (newAlpha == 255) {
         return newPixel;
      } else if (newAlpha == 0) {
         return bgPixel;
      } else {
         if (BlendOp.cacheDest == bgPixel && BlendOp.cacheSrc == newPixel) {
            return BlendOp.cacheRest;
         }
         float alphaFix = (float) newAlpha / (float) 255;
         float invAlphaFix = ((float) 1) - alphaFix;
         //mix
         int redResult = BlendOp.overOperator((bgPixel >> 16) & 0xFF, (newPixel >> 16) & 0xFF, alphaFix, invAlphaFix) & 0xFF;
         int greenResult = BlendOp.overOperator((bgPixel >> 8) & 0xFF, (newPixel >> 8) & 0xFF, alphaFix, invAlphaFix) & 0xFF;
         int blueResult = BlendOp.overOperator((bgPixel >> 0) & 0xFF, (newPixel >> 0) & 0xFF, alphaFix, invAlphaFix) & 0xFF;
         int destAlpha = ((bgPixel >> 24) & 0xFF);
         //work in base 256
         int alphaResult = ((newAlpha << 8) + (destAlpha << 8) - (newAlpha * destAlpha)) & 0xFF00;
         //alpha is shifted 16 before already in base + 8
         int val = (alphaResult << 16) + (redResult << 16) + (greenResult << 8) + blueResult;
         BlendOp.cacheDest = bgPixel;
         BlendOp.cacheSrc = newPixel;
         BlendOp.cacheRest = val;
         //	    int normA = ((alphaResult >> 8) & 0xFF);
         //	    if (((val >> 24) & 0xFF) != normA) {
         //		  int diff = Math.abs(((val >> 24) & 0xFF) - normA);
         //		  String str = "Alpha=" + ((val >> 24) & 0xFF) + "!=" + normA + " Diff=" + diff;
         //		  SystemLog.printDraw(str);
         //		  if (diff > 2) {
         //			SystemLog.printDraw(DrawUtilz.debugColor(bgPixel));
         //			SystemLog.printDraw(DrawUtilz.debugColor(newPixel));
         //			SystemLog.printDraw("redResult=" + redResult + " greenResult=" + greenResult + " blueResult=" + blueResult);
         //			SystemLog.printDraw(DrawUtilz.debugColor(val));
         //		  }
         //	    }
         return val;
      }
   }

   public static int mergePixelsOver(int bgPixel, int newPixel, int alphaop) {
      int srcAlpha = ((newPixel >> 24) & 0xFF);
      if (srcAlpha == 255) {
         return newPixel;
      } else if (srcAlpha == 0) {
         return bgPixel;
      } else {
         float alphaFix = (float) srcAlpha / (float) 255;
         float invAlphaFix = ((float) 1) - alphaFix;
         //mix
         int redResult = BlendOp.overOperator((bgPixel >> 16) & 0xFF, (newPixel >> 16) & 0xFF, alphaFix, invAlphaFix) & 0xFF;
         int greenResult = BlendOp.overOperator((bgPixel >> 8) & 0xFF, (newPixel >> 8) & 0xFF, alphaFix, invAlphaFix) & 0xFF;
         int blueResult = BlendOp.overOperator((bgPixel >> 0) & 0xFF, (newPixel >> 0) & 0xFF, alphaFix, invAlphaFix) & 0xFF;
         int destAlpha = ((bgPixel >> 24) & 0xFF);
         //work in base 256
         int alphaResult = ((srcAlpha << 8) + (destAlpha << 8) - (srcAlpha * destAlpha)) & 0xFF00;
         //alpha is shifted 16 before already in base + 8
         int val = (alphaResult << 16) + (redResult << 16) + (greenResult << 8) + blueResult;
         return val;
      }
   }

   public static int mergePixelsScreen(int base, int blend) {
      int blendAlpha = ((blend >> 24) & 0xFF);
      int blendRed = ((blend >> 16) & 0xFF);
      int blendGreen = ((blend >> 8) & 0xFF);
      int blendBlue = ((blend >> 0) & 0xFF);

      int baseAlpha = ((base >> 24) & 0xFF);
      int baseRed = ((base >> 16) & 0xFF);
      int baseGreen = ((base >> 8) & 0xFF);
      int baseBlue = ((base >> 0) & 0xFF);

      int alphaResult = fctScreenFct(blendAlpha, baseAlpha);

      int redResult = fctScreenFct(blendRed, baseRed);
      int greenResult = fctScreenFct(blendGreen, baseGreen);
      int blueResult = fctScreenFct(blendBlue, baseBlue);

      int val = (alphaResult << 24) + (redResult << 16) + (greenResult << 8) + (blueResult << 0);

      return val;
   }

   /**
    * Inverts the alpha value. Opaque becomes fully transparent
    * @param base
    * @param blend
    * @return
    */
   public static int mergePixelsTransShape(int base, int blend) {

      int blendAlpha = ((blend >> 24) & 0xFF);
      if (blendAlpha == 0) {
         return base;
      } else if (blendAlpha == 255) {
         return 0;
      } else {
         int inv = 255 - blendAlpha;
         return (inv << 24) + (base & 0xFFFFFF);
      }
   }

   public static int mergeSaturation(int base, int blend) {
      int val = 0;
      return val;
   }

   /**
    * 
    * @param dest background pixel destination
    * @param src
    * @param alpha
    * @param alphaInv
    * @return
    */
   public static final int overOperator(int dest, int src, float alpha, float alphaInv) {
      return (int) ((dest * alphaInv) + (src * alpha));

   }

   public static final int overOperatorWiki(int dest, int src, float alpha, float alphaDestFix) {
      return (int) (((dest * alphaDestFix) + (src * alpha) / (alpha + alphaDestFix)));

   }

   public static final int overOperatorWiki(int dest, int src, float alpha, float alphaInv, float destAlpha) {
      return (int) (((dest * destAlpha * alphaInv) + (src * alpha) / (alpha + destAlpha * alphaInv)));

   }

   float[]                  arrayPerf;

   protected float[]        baseR       = new float[3];

   protected float[]        baseRS      = new float[3];

   protected float[]        blendR      = new float[3];

   /**
    * Once the channels have been blended.. method for compositing back
    */
   private int              compositeOperator;

   /**
    * By default 0
    */
   protected int            emptyDest;

   /**
    * By default 0
    */
   protected int            emptySrc;

   private boolean          is;

   /**
    * Ignore 
    */
   protected boolean        isA;

   /**
    * Swap Alpha of base and blender
    */
   protected boolean        isAlphaSwap;

   /**
    * Process Blue channel?
    * <br>
    * When processing 
    */
   protected boolean        isB;

   protected boolean        isG;

   private boolean          isPorterAlpha;

   protected boolean        isR;

   protected int            mode;

   /**
    * Tells what to do with the alpha channel
    * 
    * <li> {@link ITechBlend#ALPHA_0_OVER}
    * <li> {@link BlendOp#ALPHA_1_SRC}
    * <li> {@link ITechBlend#ALPHA_2_OVER}
    */
   protected int            modeAlpa;

   /**
    * {@link ITechBlend#BLEND_OFFSET_06_COMPO_OP1}
    * <li> {@link ITechBlend#OPACITY_00_SRC}
    * <li> {@link ITechBlend#OPACITY_01_MIN_OVERIDE_SRC}
    * <li> {@link ITechBlend#OPACITY_02_MAX_OVERIDE_SRC}
    * 
    */
   private int              opacityOperator;

   /**
    * 0-255 {@link ITechBlend#BLEND_OFFSET_05_OPACITY1}
    */
   private int              overRideOpacityIntValue;

   /**
    * {@link ITechBlend#BLEND_OFFSET_01_DUFF_OP1} 
    */
   private int              porterDuffOperator;

   protected Random         r;

   protected int            virginPixel = 0;

   private final DrwCtx drc;

   public BlendOp(DrwCtx drc, ByteObject byo) {
      this.drc = drc;
      porterDuffOperator = byo.get1(ITechBlend.BLEND_OFFSET_01_DUFF_OP1);
      this.modeAlpa = byo.get1(ITechBlend.BLEND_OFFSET_02_ALPHA1);
      this.mode = byo.get2(ITechBlend.BLEND_OFFSET_03_TYPE2);
      this.overRideOpacityIntValue = byo.get1(ITechBlend.BLEND_OFFSET_05_OPACITY1);
      this.opacityOperator = byo.get1(ITechBlend.BLEND_OFFSET_04_OPACITY_OP1);
      this.compositeOperator = byo.get1(ITechBlend.BLEND_OFFSET_06_COMPO_OP1);
      isPorterAlpha = byo.hasFlag(ITechBlend.BLEND_OFFSET_07_FLAG1, ITechBlend.BLEND_FLAG_1_PORTER_ALPHA);
      isAlphaSwap = byo.hasFlag(ITechBlend.BLEND_OFFSET_07_FLAG1, ITechBlend.BLEND_FLAG_2_SWAP_ALPHA);
      isA = !byo.hasFlag(ITechBlend.BLEND_OFFSET_07_FLAG1, ITechBlend.BLEND_FLAG_3_IGNORE_ALPHA);
      isR = !byo.hasFlag(ITechBlend.BLEND_OFFSET_07_FLAG1, ITechBlend.BLEND_FLAG_4_IGNORE_RED);
      isG = !byo.hasFlag(ITechBlend.BLEND_OFFSET_07_FLAG1, ITechBlend.BLEND_FLAG_5_IGNORE_GREEN);
      isB = !byo.hasFlag(ITechBlend.BLEND_OFFSET_07_FLAG1, ITechBlend.BLEND_FLAG_6_IGNORE_BLUE);

      if (mode == ITechBlend.BLENDING_08_DISSOLVE) {
         r = drc.getRandom();
      }
   }

   public BlendOp(DrwCtx drc, int mode) {
      this(drc, mode, ITechBlend.ALPHA_0_OVER);
   }

   public BlendOp(DrwCtx drc, int mode, int alphaMode) {
      this.drc = drc;
      this.mode = mode;
      this.modeAlpa = alphaMode;
      if (mode == ITechBlend.BLENDING_08_DISSOLVE) {
         r = drc.getRandom();
      }
   }

   public int[] blend(int[] src, int[] dst) {
      return new int[] { dst[0], dst[1], src[2], Math.min(255, src[3] + dst[3]) };
   }

   public int blendComposite(int dest, int src) {
      int newPixel = dest;
      switch (mode) {
         case ITechBlend.BLENDING_00_OVER:
            newPixel = mergePixelsOver(dest, src);
            break;
         case ITechBlend.BLENDING_01_SRC:
            newPixel = src;
            break;
         case ITechBlend.BLENDING_02_DARKEN:
            newPixel = mergeDarken(dest, src);
            break;
         case ITechBlend.BLENDING_03_LIGHTEN:
            newPixel = mergeLighten(dest, src);
            break;
         case ITechBlend.BLENDING_04_MERGE_ARGB:
            newPixel = mergePixelsARGB(dest, src);
            break;
         case ITechBlend.BLENDING_06_INVERSE_ALPHA:
            newPixel = mergePixelsTransShape(dest, src);
            break;
         case ITechBlend.BLENDING_07_INVERSE:
            newPixel = mergePixelsInverse(dest, src);
            break;
         case ITechBlend.BLENDING_08_DISSOLVE:
            newPixel = mergePixelsDissolve(dest, src, r);
            break;
         case ITechBlend.BLENDING_10_HUE:
            newPixel = mergePixelsHSBHue(dest, src, baseRS, blendRS);
            break;
         case ITechBlend.BLENDING_11_HUE_SAT:
            newPixel = mergePixelsHSBHueSat(dest, src, baseRS, blendRS);
            break;
         case ITechBlend.BLENDING_12_HUE_LUM:
            newPixel = mergePixelsHSBHueLum(dest, src, baseRS, blendRS);
            break;
         case ITechBlend.BLENDING_13_SATURATION:
            newPixel = mergePixelsHSBSat(dest, src, baseRS, blendRS);
            break;
         case ITechBlend.BLENDING_14_SAT_LUM:
            newPixel = mergePixelsHSBSatLum(dest, src, baseRS, blendRS);
            break;
         case ITechBlend.BLENDING_15_LUMINANCE:
            newPixel = mergePixelsHSBLum(dest, src, baseRS, blendRS);
            break;
         case ITechBlend.BLENDING_16_MULTIPLY_BURN:
            newPixel = mergePixelsMultiply(dest, src);
            break;
         case ITechBlend.BLENDING_19_SCREEN_DODGE:
            newPixel = mergePixelsScreen(dest, src);
            break;
         case ITechBlend.BLENDING_22_HARD_MIX:
            newPixel = mergePixelsHardMix(dest, src);
            break;
         case ITechBlend.BLENDING_25_DIVIDE:
            newPixel = mergePixelsDivide(dest, src);
            break;
         default:
            //SystemLog.printDraw("#BlendOp WARNING Unknown Blend Operator " + mode + " Returning Base Value");
            break;
      }
      return newPixel;
   }

   /**
    * Based on the opacity o
    * @param src
    * @param alpha TODO
    * @param dest
    * @return
    */
   public int blendOpacity(int src, int alpha) {
      int srcAlpha = ((src >> 24) & 0xFF);
      switch (opacityOperator) {
         case ITechBlend.OPACITY_00_SRC:
            return src;
         case ITechBlend.OPACITY_01_MIN_OVERIDE_SRC:
            return (Math.max(srcAlpha, alpha) << 24) | src & (0x00FFFFFF);
         case ITechBlend.OPACITY_02_MAX_OVERIDE_SRC:
            return (Math.min(srcAlpha, alpha) << 24) | src & (0x00FFFFFF);
         case ITechBlend.OPACITY_03_OVERIDE_SRC:
            return (alpha << 24) | (src & 0x00FFFFFF);
         default:
            return src;
      }
   }

   /**
    * Entry point for blending 2 values.
    * @param dest background
    * @param src foreground
    * @return
    */
   public int blendPixel(int dest, int src) {
      return blendPixelPorter(dest, src);
   }

   public int blendPixelPorter(int dest, int src) {
      switch (porterDuffOperator) {
         case ITechBlend.OP_00_SRC_OVER:
            if (dest == emptyDest) {
               return blendOpacity(src, overRideOpacityIntValue);
            }
            if (src == emptySrc) {
               return dest; //destination opacity is not changed if source is empty
            }
            return blendComposite(dest, src);
         case ITechBlend.OP_01_SRC:
            return src;
         case ITechBlend.OP_02_SRC_IN:
            if (dest != emptyDest && src != emptySrc) {
               return blendOpacity(src, overRideOpacityIntValue);
            }
            return dest;
         case ITechBlend.OP_03_SRC_OUT:
            if (src != emptySrc) {
               if (dest == emptyDest) {
                  //part outside the destination, we replace destination
                  return blendOpacity(src, overRideOpacityIntValue);
               } else {
                  return emptySrc;
               }
            } else {
               //outside the src
               if (isPorterAlpha) {
                  return dest;
               } else {
                  //source erases destination
                  return emptySrc;
               }
            }
         case ITechBlend.OP_04_SRC_ATOP:
            if (src != emptySrc) {
               //when pixel src is not empty, 
               return blendComposite(dest, src);
            }
            return dest;
         case ITechBlend.OP_05_DST_OVER:
            if (dest == emptyDest) {
               return src;
            }
            if (src == emptySrc) {
               return dest;
            }
            return blendComposite(src, dest);
         case ITechBlend.OP_06_DST:
            return dest;
         case ITechBlend.OP_07_DST_IN:
            if (src != emptySrc) {
               return blendOpacity(dest, overRideOpacityIntValue);
            }
            return dest;
         case ITechBlend.OP_08_DST_OUT:
            if (src == emptySrc) {
               //return dest outside source
               return dest;
            }
            //make destination pixel fully transparent when opacity is 100%
            return blendOpacity(dest, 255 - overRideOpacityIntValue);
         case ITechBlend.OP_09_DST_ATOP:
            if (src != emptySrc) {
               return blendComposite(src, dest);
            }
            return dest;
         case ITechBlend.OP_10_XOR:
            if (src != emptySrc && dest != emptyDest) {
               //inverse opacity. override on the dest pixel.
               return blendOpacity(dest, 255 - overRideOpacityIntValue);
            }
            if (dest == emptyDest) {
               return blendOpacity(src, overRideOpacityIntValue);
            }
            return dest;
         case ITechBlend.OP_11_CLEAR:
            if (src != emptySrc) {
               return emptyDest;
            }
            return dest;
         default:
            break;
      }
      return dest;
   }

   /**
    * Only works for RGB blending.
    * @param blend
    * @param base
    * @param param
    * @return
    */
   public int blendRGBSwitch(int blend, int base, float[] param) {
      switch (mode) {
         case ITechBlend.BLENDING_00_OVER:
            return overOperator(blend, base, param[0], param[1]);
         case ITechBlend.BLENDING_01_SRC:
            return blend;
         default:
            break;
      }
      return base;
   }

   public int getMode() {
      return mode;
   }

   /**
    * Method to call when different BlendOp for different channels.
    * @param base
    * @param blend
    * @param alphaOp
    * @return
    */
   private int mergeGenericARGB(int base, int blend, int alphaOp) {
      int blendAlpha = ((blend >> 24) & 0xFF);

      int alphaResult = 0;
      int redResult = 0;
      int greenResult = 0;
      int blueResult = 0;
      switch (alphaOp) {
         case ITechBlend.ALPHA_0_OVER:
            if (blendAlpha == 255) {
               return alphaResult = 255;
            } else if (alphaResult == 0) {
               return base;
            } else {
               float alphaFix = (float) blendAlpha / (float) 255;
               float invAlphaFix = ((float) 1) - alphaFix;
            }
            break;

         default:
            break;
      }
      if (isR) {
         int blendRed = ((blend >> 16) & 0xFF);
         int baseRed = ((base >> 16) & 0xFF);
         redResult = blendRGBSwitch(blendRed, baseRed, null);
      }
      if (isG) {
         int blendGreen = ((blend >> 8) & 0xFF);
         int baseGreen = ((base >> 8) & 0xFF);
         greenResult = blendRGBSwitch(blendGreen, baseGreen, null);
      }
      if (isB) {
         int blendBlue = (blend & 0xFF);
         int baseBlue = (base & 0xFF);
         blueResult = blendRGBSwitch(blendBlue, baseBlue, null);
      }
      int val = (alphaResult << 24) + (redResult << 16) + (greenResult << 8) + (blueResult);
      return val;
   }

   /**
    * For merging anti aliased borders...
    * <br>
    * 
    * @param dest
    * @param src
    * @param alphaop
    * @param opacity
    * @return
    */
   private int mergeOver(int dest, int src, int alphaop, float opacity) {
      //in all cases, when src is virgin pixel, return dest
      if (src == virginPixel) {
         return dest;
      }
      int srcAlpha = ((src >> 24) & 0xFF);
      int destA = ((dest >> 24) & 0xFF);
      switch (opacityOperator) {
         case ITechBlend.OPACITY_00_SRC:
            break;
         case ITechBlend.OPACITY_01_MIN_OVERIDE_SRC:
            srcAlpha = Math.min(srcAlpha, overRideOpacityIntValue);
         default:
            break;
      }
      float alphaSrcFix = (float) srcAlpha / (float) 255;
      float alphaDestFix = (float) destA / (float) 255;
      float invAlphaSrcFix = ((float) 1) - alphaSrcFix;
      float alphaFix = alphaDestFix * invAlphaSrcFix; //src over alpha op
      int blendRed = ((src >> 16) & 0xFF);
      int blendGreen = ((src >> 8) & 0xFF);
      int blendBlue = ((src >> 0) & 0xFF);

      int destRed = ((dest >> 16) & 0xFF);
      int destGreen = ((dest >> 8) & 0xFF);
      int destBlue = ((dest >> 0) & 0xFF);

      int rR = 0;
      int rG = 0;
      int rB = 0;
      int rA = 0;
      rA = Math.min(255, (int) (alphaSrcFix + alphaFix * (float) 255));
      float alpha = alphaSrcFix + alphaFix;
      if (alpha > 1) {
         alpha = 1;
      }
      switch (compositeOperator) {
         case 0:
            rR = overOperatorWiki(destRed, blendRed, alphaSrcFix, alphaFix) & 0xFF;
            rG = overOperatorWiki(destGreen, blendGreen, alphaSrcFix, alphaFix) & 0xFF;
            rB = overOperatorWiki(destBlue, blendBlue, alphaSrcFix, alphaFix) & 0xFF;
            break;
         case 1:
            rR = getPixMix(destRed, blendRed, alpha);
            rG = getPixMix(destGreen, blendGreen, alpha);
            rB = getPixMix(destBlue, blendBlue, alpha);
            rA = getPixMix(destA, srcAlpha, alpha);
            break;
         default:
            break;
      }
      int val = (rA << 24) + (rR << 16) + (rG << 8) + (rB);
      return val;
   }

   public void setAlphaMode(int alphaMode) {
      this.modeAlpa = alphaMode;
   }

   
   //#mdebug
   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, "BlendOp");
      toStringPrivate(dc);
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   private void toStringPrivate(Dctx sb) {
      sb.append("#BlendOp ");
      sb.append(debugBlend(mode));
      sb.append(" ");
      sb.append(debugAlpha(modeAlpa));
      sb.append(" ");
      sb.append(debugOpacity(opacityOperator));
      sb.append(" ");
      sb.append(debugOpDuff(porterDuffOperator));
      sb.nl();
      sb.append("PorterAlpha=" + isPorterAlpha);
      sb.append(" AlphaSwap=" + isAlphaSwap);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "BlendOp");
      toStringPrivate(dc);
   }

   public UCtx toStringGetUCtx() {
      return drc.getUCtx();
   }

   //#enddebug
   


}
