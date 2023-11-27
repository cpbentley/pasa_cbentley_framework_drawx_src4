/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.tech.ITechFunction;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.tech.ITechArtifact;
import pasa.cbentley.framework.drawx.src4.tech.ITechColorFunction;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;

/**
 * Wrapper around the {@link GradientC}.
 * <br>
 * @author Charles Bentley
 *
 */
public class GradientFactory extends AbstractDrwFactory implements ITechFunction {

   public GradientFactory(DrwCtx drc) {
      super(drc);
   }

   /**
    * Divide evenly the gradient size and go through all those colors and finally back to primary color.
    * In this kind of gradient, there is no position
    * <br>
    * For uneven size, another int[] array decide the ratio for each color
    * @param colors
    * @param step
    * @return
    */
   public  ByteObject getGradient(int[] colors, int step) {
      return getGradient(colors, step,null);
   }
   
   public  ByteObject getGradient(int[] colors, int step, int[] ratios) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_059_GRADIENT, ITechGradient.GRADIENT_BASIC_SIZE);
      p.setValue(ITechGradient.GRADIENT_OFFSET_07_STEP1, step, 1);
      p.setFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_5_INT_ARRAY, true);
      p.addByteObject(boc.getLitteralIntFactory().getLitteralArray(colors));
      if(ratios != null) {
         p.addByteObject(boc.getLitteralIntFactory().getLitteralArray(ratios));
      }
      return p;
   }
   
   
   /**
    * 
    * @param type
    * @param w
    * @param h
    * @param spac
    * @param rw
    * @param rh
    * @param rs
    * @return
    */
   public  ByteObject getArtifact(int type, int w, int h, int spac, boolean rw, boolean rh, boolean rs) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_052_ARTIFACT, ITechArtifact.ARTIFACT_BASIC_SIZE);
      p.setValue(ITechArtifact.ARTIFACT_OFFSET_2W1, w, 1);
      p.setValue(ITechArtifact.ARTIFACT_OFFSET_3H1, h, 1);
      p.setValue(ITechArtifact.ARTIFACT_OFFSET_4SPACING_CAP1, spac, 1);
      p.setValue(ITechArtifact.ARTIFACT_OFFSET_5TYPE1, type, 1);
      p.setFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_1RANDOM_W, rw);
      p.setFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_2RANDOM_H, rh);
      p.setFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_3RANDOM_SPACING, rs);
      return p;
   }

   /**
    * From primary color, compute random step with a threshold.
    * 
    * @return
    */
   public  ByteObject getGradientFctRandom() {
      ByteObject fct = boc.getFunctionFactory().getFunctionRnd(0, 255);
      fct.setFlag(FUN_OFFSET_03_FLAGP, FUN_FLAGP_4_CHANNELS, true);
      fct.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_6RED, true);
      fct.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_7GREEN, true);
      fct.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_8BLUE, true);
      return fct;
   }

   /**
    * Make the alpha channel evolve.
    * <br>
    * <br>
    * Very costly. When possible rectangle gradients, it is advised to implement with a int array.
    * @param param
    * @return
    */
   public  ByteObject getGradientAlpha(int finalAlpha) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_059_GRADIENT, ITechGradient.GRADIENT_BASIC_SIZE);
      return p;
   }

   /**
    * Goes to black according to the steps. <br>
    * <br>
    * Works on RGB channels.
    * @param param
    * @return
    */
   public  ByteObject getGradientFctDarken(int param) {
      return drc.getColorFunctionFactory().getColorFunction(1, -param, false, true, true, true);
   }

   /**
    * Takes primary color and increase each channel with 
    * <br>
    * <br>
    * Step centric function.
    * 
    * @return
    */
   public  ByteObject getGradientFctLighten(int stepWeight) {
      return drc.getColorFunctionFactory().getColorFunction(1, stepWeight, false, true, true, true);
   }

   /**
    * Starts with primary colors and merge it with array. Go according to index operator.
    * Value function to adds primary color, alternate using given operator.
    * 24bits functions.
    * @param colors array must be one size more. 0 index is reserved for primary color.
    * @return
    */
   public  ByteObject getGradientFctAlernate(int[] colors) {
      ByteObject fct = boc.getFunctionFactory().getFunctionValues(colors);
      fct.setFlag(FUN_OFFSET_03_FLAGP, FUN_FLAGP_1_ADDCOLOR, true);
      fct.setFlag(FUN_OFFSET_02_FLAG, FUN_FLAG_3LOOPING, true);
      return fct;
   }

   public  ByteObject getGradient(int scolor, int sec) {
      return getGradient(scolor, sec, 0, null);
   }

   /**
    * 
    * @param scolor
    * @param sec
    * @param fillVert
    * @param split
    * @return
    */
   public  ByteObject getGradient(int scolor, int sec, boolean fillVert, boolean split) {
      return getGradient(scolor, sec, fillVert, split, false);
   }

   public  ByteObject getGradient(int scolor, int sec, boolean fillVert, boolean split, boolean doAlpha) {
      ByteObject grad = getGradient(scolor, sec, 0);
      grad.setFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_1_SWITCH_2TYPES, fillVert);
      grad.setFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_4_USEALPHA, doAlpha);
      grad.setFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_1_FULL_LEFT, split);
      grad.setFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_2_FULL_RIGHT, split);
      return grad;
   }

   public  ByteObject getGradient(int scolor, int sec, int type) {
      return getGradient(scolor, sec, type, null);
   }

   public  ByteObject getGradient(int scolor, int sec, int type, ByteObject tcolor) {
      return getGradient(scolor, sec, type, 0, 0, 0, tcolor);
   }

   public  ByteObject getGradient(int scolor, int sec, int type, int color) {
      return getGradient(scolor, sec, type, boc.getLitteralIntFactory().getIntBO(color));
   }

   /**
    * 
    * @param scolor
    * @param sec
    * @param type
    * @param step {@link ITechGradient#GRADIENT_OFFSET_07_STEP1}
    * @param tcolor
    * @return
    */
   public  ByteObject getGradient(int scolor, int sec, int type, int step, ByteObject tcolor) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_059_GRADIENT, ITechGradient.GRADIENT_BASIC_SIZE);
      p.setValue(ITechGradient.GRADIENT_OFFSET_06_TYPE1, type, 1);
      p.setValue(ITechGradient.GRADIENT_OFFSET_05_SEC1, sec, 1);
      p.setValue(ITechGradient.GRADIENT_OFFSET_04_COLOR4, scolor, 4);
      p.setValue(ITechGradient.GRADIENT_OFFSET_07_STEP1, step, 1);
      if (tcolor != null) {
         p.addByteObject(tcolor);
         p.setFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_3_THIRD_COLOR, true);
      }
      return p;
   }

   /**
    * 
    * @param scolor
    * @param sec
    * @param type
    * @param mainFlag
    * @param exludeFlags
    * @param channelFlags
    * @param tcolor
    * @return
    */
   public  ByteObject getGradient(int scolor, int sec, int type, int mainFlag, int exludeFlags, int channelFlags, ByteObject tcolor) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_059_GRADIENT, ITechGradient.GRADIENT_BASIC_SIZE);
      p.setValue(ITechGradient.GRADIENT_OFFSET_06_TYPE1, type, 1);
      p.setValue(ITechGradient.GRADIENT_OFFSET_05_SEC1, sec, 1);
      p.setValue(ITechGradient.GRADIENT_OFFSET_04_COLOR4, scolor, 4);
      p.setValue(ITechGradient.GRADIENT_OFFSET_01_FLAG, mainFlag, 1);
      p.setValue(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, exludeFlags, 1);
      p.setValue(ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, channelFlags, 1);
      if (tcolor != null) {
         p.addByteObject(tcolor);
         p.setFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_3_THIRD_COLOR, true);
      }
      return p;
   }
   
   public void toStringArtifact(ByteObject bo, Dctx sb) {
      sb.append("#Artifact");
      sb.append(" w=" + bo.get1(ITechArtifact.ARTIFACT_OFFSET_2W1));
      sb.append(" h=" + bo.get1(ITechArtifact.ARTIFACT_OFFSET_3H1));
      sb.append(" spacing=" + bo.get1(ITechArtifact.ARTIFACT_OFFSET_4SPACING_CAP1));
      sb.append(" RndW=" + bo.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_1RANDOM_W));
      sb.append(" RndH=" + bo.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_2RANDOM_H));
      sb.append(" RndS=" + bo.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_3RANDOM_SPACING));
   }
   
   public void toStringGradient(ByteObject bo, Dctx sb) {
      sb.append("#Gradient");
      sb.append(" scolor=" + (ToStringStaticDrawx.toStringColor(bo.get4(ITechGradient.GRADIENT_OFFSET_04_COLOR4))));
      sb.append(" maxSec=" + (bo.getValue(ITechGradient.GRADIENT_OFFSET_05_SEC1, 1)));
      sb.append(" type=" + (bo.getValue(ITechGradient.GRADIENT_OFFSET_06_TYPE1, 1)));
      sb.append(" step=" + (bo.getValue(ITechGradient.GRADIENT_OFFSET_07_STEP1, 1)));
      sb.append(" vertical=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_1_SWITCH_2TYPES)));
      sb.append(" doAlpha=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_4_USEALPHA)));
      sb.append(" artifact=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_7_ARTIFACTS)));
      sb.append(" Reverse=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_8_REVERSE)));
      sb.nl();
      sb.append(" fullLeft=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_1_FULL_LEFT)));
      sb.append(" fullRight=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_2_FULL_RIGHT)));
      sb.append(" leftExLeft=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_3_PART1_EXCLUDE_LEFT)));
      sb.append(" leftExRight=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_4_PART1_EXCLUDE_RIGHT)));
      sb.append(" rightExLeft=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_5_PART2_EXCLUDE_LEFT)));
      sb.append(" rightExRight=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_6_PART2_EXCLUDE_RIGHT)));
      sb.nl();
      sb.append(" a=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, ITechGradient.GRADIENT_FLAGC_1_CH_A)));
      sb.append(" r=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, ITechGradient.GRADIENT_FLAGC_2_CH_R)));
      sb.append(" g=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, ITechGradient.GRADIENT_FLAGC_3_CH_G)));
      sb.append(" b=" + (bo.hasFlag(ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, ITechGradient.GRADIENT_FLAGC_4_CH_B)));

   }

}
