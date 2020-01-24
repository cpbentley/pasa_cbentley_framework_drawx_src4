package pasa.cbentley.framework.drawx.src4.color;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.functions.Function;
import pasa.cbentley.byteobjects.src4.tech.ITechFunction;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.core.src4.utils.IntUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.factories.AbstractDrwFactory;
import pasa.cbentley.framework.drawx.src4.tech.ITechColorFunction;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;

public class ColorFunctionFactory extends AbstractDrwFactory implements ITechColorFunction, ITechFunction {

   public ColorFunctionFactory(DrwCtx drc) {
      super(drc);

   }

   /**
    * Inspect {@link ByteObject} gradient definition. Builds color array for the given size. 
    * <br>
    * <br>
    * 
    * @param color
    * @param grad may be null, in which case, {@link ColorIterator} returns color at all steps.
    * @param gradSize any value below 1, will be set to one
    * @return
    */
   public ColorIterator getColorIterator(int color, ByteObject grad, int gradSize) {
      ColorIterator ci = new ColorIterator(drc);
      ci.grad = grad;
      int stepSize = 1;
      int offset = 0;
      if (grad != null && grad.hasFlag(ITechGradient.GRADIENT_OFFSET_09_FLAGX1, ITechGradient.GRADIENT_FLAGX_7_GRADSIZE)) {
         gradSize = grad.get2(ITechGradient.GRADIENT_OFFSET_10_GRADSIZE2);
      }
      //in all cases, gradsize must be 1 at least
      if (gradSize <= 0) {
         gradSize = 1;
      }

      //size to be used to compute
      int fgradSize = grad.get2(ITechGradient.GRADIENT_OFFSET_11_FAKE_SIZE2);

      int[] colors = null;
      if (grad == null) {
         colors = new int[gradSize];
         IntUtils.fill(colors, color);
      } else {
         stepSize = grad.get1(ITechGradient.GRADIENT_OFFSET_07_STEP1);
         if (grad.hasFlag(ITechGradient.GRADIENT_OFFSET_09_FLAGX1, ITechGradient.GRADIENT_FLAGX_6_OFFSET)) {
            offset = grad.get2(ITechGradient.GRADIENT_OFFSET_08_OFFSET2);
         }
         //another function than the default Gradient function. used for randomized 
         if (grad.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_2_EXTERNAL_FUNCTION)) {
            ByteObject fctDef = grad.getSubFirst(IBOTypesBOC.TYPE_021_FUNCTION);
            //SystemLog.printDraw(fctDef.toString());
            Function f = boc.getFunctionFactory().createFunction(fctDef);

            //if value functions. insert primary color in first place.
            if (fctDef.hasFlag(FUN_OFFSET_03_FLAGP, FUN_FLAGP_1_ADDCOLOR)) {
               f.getValues()[0] = color;
            }
            // compute number of steps.
            int numSteps = gradSize;
            if (stepSize > 1) {
               numSteps = gradSize / stepSize;
            }
            int[] vals = new int[numSteps];
            for (int i = 0; i < numSteps; i++) {
               vals[i] = f.fx(color);
               color = vals[i];
            }
            colors = vals;
            //SystemLog.printDraw("numSteps="+ numSteps);
            //         for (int i = 0; i < colors.length; i++) {
            //            SystemLog.printDraw(DrawUtilz.debugColor(colors[i]));
            //         }
         } else if (grad.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_5_INT_ARRAY)) {

            //TODO how to put several gradients in one? chain gradients
            //divide gradSize in the number of gradients
            //we want A to B to C in one def, then C to D to A
            ByteObject ar = grad.getSubFirst(IBOTypesBOC.TYPE_007_LIT_ARRAY_INT); //defines gradient colors
            //TODO 
            ByteObject[] subs = grad.getSubs(IBOTypesDrw.TYPE_059_GRADIENT);
            //
            int[] arr = boc.getLitteralIntOperator().getLitteralArray(ar);

            //function for generating colors
            if (grad.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAGX_3_RAW)) {
               colors = arr;
            } else {
               int exSize = grad.get2(ITechGradient.GRADIENT_OFFSET_11_FAKE_SIZE2);
               if (exSize == 0)
                  exSize = 1;
               //how do you interpret those values. that's depending on flags
               //
               int numGrads = arr.length + 1;
               int count = 0;
               int primaryColor = color;
               int[] gradSizes = new int[numGrads];
               IntBuffer buff = new IntBuffer(drc.getUCtx(), gradSize);

               //we do an exclude on last color so only 1 step shows boundary colors
               for (int i = 0; i < numGrads; i++) {
                  GradientFunction gf = new GradientFunction(drc);
                  ByteObject gradd = grad.cloneCopyHead();
                  gradd.set4(ITechGradient.GRADIENT_OFFSET_04_COLOR4, arr[count++]);
                  if (grad.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_3_THIRD_COLOR)) {
                     ByteObject intBO = boc.getLitteralIntFactory().getIntBO(arr[count]);
                     gradd.addByteObject(intBO);
                  }
                  gf.init(primaryColor, exSize, gradd);
                  buff.addInt(gf.getColors());
               }
               colors = buff.getIntsClonedTrimmed();
            }
         } else {
            //case of 
            GradientFunction gf = new GradientFunction(drc);
            gf.init(color, gradSize, grad);
            colors = gf.getColors();
            // SystemLog.printDraw(gf.toString());
         }
      }

      ci.init(colors, gradSize, stepSize);
      if (ci.colors.length == 0) {
         offset = 0;
      } else {
         offset = offset % ci.colors.length;
         if (offset < ci.colors.length) {
            ci.offset = offset;
         }
      }
      // SystemLog.printDraw(ci.toString());
      return ci;
   }

   public ByteObject getColorFunction(int a, int c, boolean alpha, boolean red, boolean green, boolean blue) {
      return getColorFunction(a, c, alpha, red, green, blue, 0);
   }

   /**
    * Function modifying the alpha channel exclusively
    * <br>
    * <br>
    * @param values
    * @param random
    * @param upAndDown
    * @return
    */
   public ByteObject getFunctionAlpha(int[] values, int indexop) {
      ByteObject p = drc.getColorFunctionFactory().getColorFunction(values, true, false, false, false);
      p.setValue(FUN_OFFSET_07_AUX_OPERATOR1, indexop, 1);
      return p;
   }

   /**
    * Breaks down input value into 4 channels and apply function on each true channel.
    * <br>
    * Operator is Ax+C
    * @param a
    * @param c
    * @param alpha
    * @param red
    * @param green
    * @param blue
    * @param postop
    * @return
    */
   public ByteObject getColorFunction(int a, int c, boolean alpha, boolean red, boolean green, boolean blue, int postop) {
      ByteObject p = boc.getFunctionFactory().getFunctionAxC(a, c);
      boolean channel = alpha | red | green | blue;
      p.setFlag(FUN_OFFSET_03_FLAGP, FUN_FLAGP_4_CHANNELS, channel);
      p.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_5ALPHA, alpha);
      p.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_6RED, red);
      p.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_7GREEN, green);
      p.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_8BLUE, blue);
      p.setFlag(FUN_OFFSET_02_FLAG, FUN_FLAG_6_EXTENSION, true);
      p.setValue(FUN_OFFSET_09_EXTENSION_TYPE1, IBOTypesDrw.TYPE_057_COLOR_FUNCTION, 1);
      if (postop != 0) {
         p.setFlag(FUN_OFFSET_02_FLAG, FUN_FLAG_1POSTOP, true);
         p.setValue(FUN_OFFSET_08_POST_OPERATOR1, postop, 1);
      }
      return p;
   }

   /**
    * Function 
    * @param values
    * @param alpha
    * @param red
    * @param green
    * @param blue
    * @return
    */
   public ByteObject getColorFunction(int[] values, boolean alpha, boolean red, boolean green, boolean blue) {
      ByteObject fun = boc.getFunctionFactory().getFunctionValues(values);
      return getColorFunction(fun, alpha, red, green, blue);
   }

   public ByteObject getColorFunction(ByteObject fun, boolean alpha, boolean red, boolean green, boolean blue) {
      fun.setFlag(FUN_OFFSET_03_FLAGP, FUN_FLAGP_4_CHANNELS, true);
      fun.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_5ALPHA, alpha);
      fun.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_6RED, red);
      fun.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_7GREEN, green);
      fun.setFlag(FUN_OFFSET_03_FLAGP, ITechColorFunction.FUNCTION_FLAGP_8BLUE, blue);
      fun.setFlag(FUN_OFFSET_02_FLAG, FUN_FLAG_6_EXTENSION, true);
      fun.setValue(FUN_OFFSET_09_EXTENSION_TYPE1, IBOTypesDrw.TYPE_057_COLOR_FUNCTION, 1);
      return fun;
   }

   /**
    * Simply Randomize input value into another color
    * <br>
    * <br>
    * @return
    */
   public ByteObject getColorFunctionRandom(boolean perchannel) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesBOC.TYPE_021_FUNCTION, FUN_BASIC_SIZE);
      p.setValue(FUN_OFFSET_01_TYPE1, FUN_TYPE_06_COLOR, 1);
      p.addByteObject(getColorRandom(perchannel));
      return p;
   }

   public ByteObject getColorFunctionRandom(ByteObject rand) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesBOC.TYPE_021_FUNCTION, FUN_BASIC_SIZE);
      p.setValue(FUN_OFFSET_01_TYPE1, FUN_TYPE_06_COLOR, 1);
      p.addByteObject(rand);
      return p;
   }

   public ByteObject getColorRandom(boolean perchannel) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_061_COLOR_RANDOM, RND_COLORS_BASIC_SIZE);
      if (perchannel) {
         p.setValue(RND_COLORS_OFFSET_06_TYPE1, RND_COLORS_TYPE_1_CHANNEL, 1);
      } else {
         p.setValue(RND_COLORS_OFFSET_06_TYPE1, RND_COLORS_TYPE_0_RND_32BITS, 1);
      }
      p.setFlag(RND_COLORS_OFFSET_03_FLAGC, RND_COLORS_FLAG_C_8_ALL_COLOR, true);
      return p;
   }

   public ColorFunction createColorFunction(ByteObject def) {
      ColorFunction cf = new ColorFunction(drc, def);
      cf.reset(def);
      return cf;
   }

   /**
    * 
    * @return
    */
   public ColorFunction getColorFunctionRandom() {
      return createColorFunction(getColorFunctionRandom(false));
   }

}
