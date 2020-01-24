package pasa.cbentley.framework.drawx.src4.color;


import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.functions.Function;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;

/**
 * Generates Gradient color values.
 * <br>
 * <br>
 * Dynamic init is done for one value only
 * <li>Base color
 * <li>Gradient size.
 * <br>
 * The other is initailized in constructor.
 * 
 * @author Charles-Philip Bentley
 *
 */
public class GradientFunction extends Function {
   int                c_pa;

   int                c_pb;

   int                c_pg;

   int                c_pr;

   int                c_sa;

   int                c_sb;

   int                c_sg;

   int                c_sr;

   int                common_red;

   int                common_blue;

   int                common_green;

   int                common_alpha;

   int                color_1;

   int                color_2;

   int                color_3;

   public int         colorCurrent;

   public int         colorNext;

   public int         colorPrevious;

   /**
    * Divides.
    * domain is ]0,[
    */
   double             divisor;

   /**
    * {@link ITechGradient#GRADIENT_FLAGK_3_PART1_EXCLUDE_LEFT}
    */
   boolean            isExcludeLeft;

   /**
    * {@link ITechGradient#GRADIENT_FLAGK_4_PART1_EXCLUDE_RIGHT}
    */
   boolean            isExcludeRight;

   /**
    * Internal flag set when the color set in the init method never changes. <br>
    * This prevent unnecessary computations.
    */
   private int        fullColor;

   /**
    * Gradient definition.
    */
   private ByteObject grad;

   boolean            isCAlpha;

   boolean            isCBlue;

   boolean            isCGreen;

   /**
    * Force a common red channel for all.
    * <br>
    * <br>
    * 
    * When true, keeps the primary red channel untouched during the whole gradient process
    * 
    */
   boolean            isCRed;

   boolean            isDoAlpha;

   /**
    * No Gradient. Just draw the same color (computed in init methods)
    */
   private boolean    isFull;

   /**
    * {@link ITechGradient#GRADIENT_FLAGK_1_FULL_LEFT}
    */
   boolean            isFullLeft;

   boolean            isFullRight;

   private boolean    isPastMiddle;

   /**
    * Variable size steps.
    */
   int                nextStepValue   = 0;

   /**
    * domain is [0-[
    */
   int                numSteps;

   /**
    * Domain is [0,1]
    * Evolving fraction p=f(i)
    */
   double             p;

   /**
    * Step at which the second part starts of the first part 
    */
   int                part2StartStep  = -1;

   /**
    * Size in pixel of the 1st part
    */
   private int        sizePart1;

   /**
    * Size in pixel of the 2nd part
    */
   private int        sizePart2;

   int                sizeCounter     = 0;

   int                sizeTotal       = 0;

   /**
    * Pixels left over of the stepping. 
    * <br>
    * Redistribute those pixels evenly or give them all to first/last step.
    */
   int                stepLeftOver;

   int                stepLeftOverCount;

   private int        stepSizeCounter = 0;

   /**
    * The size in pixels of a step
    */
   int                stepValue       = 1;

   /**
    * Weight for primary color. <br>
    * Domain is [0,1]
    */
   double             weight1;

   /**
    * Weight for secondary color. <br>
    * Domain is [0,1]
    */
   double             weight2;

   private DrwCtx drc;

   public GradientFunction(DrwCtx drc) {
      super(drc.getBOC());
      this.drc = drc;
   }

   private void breakChannels(int primaryColor, int secondaryColor) {
      // Break the primary color into red, green, and blue.
      c_pa = (primaryColor >> 24) & 0xFF;
      c_pr = (primaryColor >> 16) & 0xFF;
      c_pg = (primaryColor >> 8) & 0xFF;
      c_pb = (primaryColor & 0xFF);

      // Break the secondary color into red, green, and blue.i
      c_sa = (secondaryColor >> 24) & 0xFF;
      c_sr = (secondaryColor >> 16) & 0xFF;
      c_sg = (secondaryColor >> 8) & 0xFF;
      c_sb = (secondaryColor & 0xFF);
   }

   /**
    * Called at the init and at the middle to compute the divisor of the step to compute
    * <br>
    * The value computed is bigger than <b>1</b>
    * @param end divisor value
    */
   private void computeDivisor(int end) {
      if (isExcludeRight) {
         if (isExcludeLeft) {
            //p will never be 1.0 since i will end at END and divisor is END + 1
            divisor = end + 1;
         } else {
            divisor = end;
         }
      } else {
         //we want it to reach
         if (isExcludeLeft) {
            divisor = end + 1;
         } else {
            divisor = end - 1;
         }
      }
   }

   /**
    * Returns array of colors defined by the function.
    * <br>
    * <br>
    * @return
    */
   public int[] getColors() {
      int[] colors = new int[numSteps];
      for (int i = 0; i < numSteps; i++) {
         colors[i] = getNextColor(i);
      }
      return colors;
   }

   /**
    * 
    * @param step step of the gradient. Domain is [0,size1+size2[
    * 
    * @return
    */
   private int getNextColor(int step) {
      if (grad == null) {
         return color_1;
      }
      if (step == part2StartStep) {
         initPart2();
      }
      if (isFull) {
         return fullColor;
      }
      //compute the color ratio for this step
      //adjust i
      if (isPastMiddle) {
         step -= sizePart1;
      }
      if (isExcludeLeft) {
         //p will not be 0.0 because i starts at 1 and divisor is never 0
         p = (double) (step + 1) / divisor;
      } else {
         //p will start with 0.0 
         p = (double) step / divisor;
      }
      if (p < 0 || p > 1.0) {
         //#debug
         //ui.dLog().printDraw("#GradientFunction#getNextColor " + step);
         //#debug
         //ui.dLog().printDraw("#GradientFunction#getNextColor " + this);
         throw new RuntimeException("p =" + p + " is not valid with " + step + " / " + divisor);
      }
      //when p is zero the full primary color is drawn. when p = 1. it is not drawn at all.
      weight1 = 1.0 - p;
      //when v is zero the full secondary color is drawn
      weight2 = 1.0 - weight1;

      int red, blue, green, alpha;
      // Calculate the color for this line.
      if (isCRed) {
         red = common_red;
      } else {
         red = (int) (c_pr * weight1 + c_sr * weight2);
      }
      if (isCGreen) {
         green = common_green;
      } else {
         green = (int) (c_pg * weight1 + c_sg * weight2);
      }
      if (isCBlue) {
         blue = common_blue;
      } else {
         blue = (int) (c_pb * weight1 + c_sb * weight2);
      }
      //SystemLog.printDraw("red=" + red + " green=" + green + " blue=" + blue);
      if (isDoAlpha) {
         if (isCAlpha) {
            alpha = common_alpha;
         } else {
            alpha = (int) (c_pa * weight1 + c_sa * weight2);
         }
         return ColorUtils.getRGBInt(alpha, red, green, blue);
      } else {
         return ColorUtils.getRGBInt(red, green, blue);
      }
   }

   /**
    * Init a function process. 
    * <br>
    * <br>
    * For drawing several gradient in the same pass,
    * 
    * Colors are computed
    * <br>
    * <br>
    * @param primaryColor source gradient color
    * @param totalSize number of pixel steps. This value is pre-computed according to figure and gradient type
    * @param grad may be null, but function returns an array of primary color.
    */
   public void init(int primaryColor, int totalSize, ByteObject grad) {
      //reinit past variables.
      if (totalSize < 0) {
         //do a parameter warning
         totalSize = 0;
      }
      reset();
      sizeTotal = totalSize;
      if (grad == null) {
         color_1 = primaryColor;
         isFull = true;
         return;
      }
      this.grad = grad;
      initColors(primaryColor, grad);
      int stepping = grad.get1(ITechGradient.GRADIENT_OFFSET_07_STEP1);
      int size12 = totalSize;
      if (stepping > 1) {
         stepLeftOver = totalSize % stepping;
         stepLeftOverCount = stepLeftOver;
         size12 = totalSize / stepping;
         stepValue = stepping;
      } else {
         stepValue = 1;
      }
      if (size12 >= 0) {
         numSteps = size12;
      }
      int maxSec = grad.get1(ITechGradient.GRADIENT_OFFSET_05_SEC1);
      //divide in two. 3 cases: 1st part only, 2nd part only, or 1st and 2nd parts.
      double ratio = (double) maxSec / (double) 100;
      sizePart1 = (int) (size12 * ratio);
      sizePart2 = size12 - sizePart1;
      //SystemLog.printDraw("size12 = " + size12 + " size1 = " + size1 + " size2 = " + size2);
      if (sizePart1 == 0) {
         //second part only, masquerade as first part
         initPart2();
      } else if (sizePart2 == 0) {
         //first part only
         initPart1();
      } else {
         //both parts
         initPart1();
         //compute step at which part2 starts.
         part2StartStep = sizePart1;
      }

   }

   private void initColors(int primaryColor, ByteObject grad) {
      color_1 = primaryColor;
      color_2 = grad.get4(ITechGradient.GRADIENT_OFFSET_04_COLOR4);
      if (grad.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_8_REVERSE)) {
         int temp = color_1;
         color_1 = color_2;
         color_2 = temp;
      }
      isDoAlpha = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_4_USEALPHA);
      isCRed = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, ITechGradient.GRADIENT_FLAGC_2_CH_R);
      isCGreen = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, ITechGradient.GRADIENT_FLAGC_3_CH_G);
      isCBlue = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, ITechGradient.GRADIENT_FLAGC_4_CH_B);
      isCAlpha = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, ITechGradient.GRADIENT_FLAGC_1_CH_A);
      if (isCRed) {
         common_red = (primaryColor >> 16) & 0xFF;
      }
      if (isCGreen) {
         common_green = (primaryColor >> 8) & 0xFF;
      }
      if (isCBlue) {
         common_blue = (primaryColor >> 0) & 0xFF;
      }
      if (isCAlpha) {
         common_alpha = (primaryColor >> 24) & 0xFF;
      }

   }

   /**
    * Called when size1 is not zero.
    * Part1 exists when Gradient position is different than 0.
    */
   private void initPart1() {
      //SystemLog.printDraw("end=" + end + " size1=" + size1 + " size2=" + size2);
      isFullLeft = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_1_FULL_LEFT);
      isFull = isFullLeft;
      if (isFull) {
         fullColor = color_1;
      } else {
         isExcludeLeft = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_3_PART1_EXCLUDE_LEFT);
         isExcludeRight = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_4_PART1_EXCLUDE_RIGHT);
         breakChannels(color_1, color_2);
      }
      computeDivisor(sizePart1);

   }

   /**
    * Part2 exists when Gradient position is different than 100.
    * <br>
    * At middle, gradient color changes back to primary color or third color.
    */
   private void initPart2() {
      isPastMiddle = true;
      if (grad.hasFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_3_THIRD_COLOR)) {
         ByteObject p = grad.getSubFirst(IBOTypesBOC.TYPE_002_LIT_INT);
         if (p == null) {
            throw new NullPointerException("No Third Color");
         }
         color_1 = drc.getBOC().getLitteralIntOperator().getIntValueFromBO(p);
      }
      isFullRight = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_2_FULL_RIGHT);
      isFull = isFullRight;
      if (isFull) {
         fullColor = color_2;
      } else {
         int primaryColor = color_2;
         int secondaryColor = color_1;
         if (isFullLeft) {
            primaryColor = color_1;
            secondaryColor = color_2;
         }
         isExcludeLeft = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_5_PART2_EXCLUDE_LEFT);
         isExcludeRight = grad.hasFlag(ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, ITechGradient.GRADIENT_FLAGK_6_PART2_EXCLUDE_RIGHT);
         breakChannels(primaryColor, secondaryColor);
      }
      computeDivisor(sizePart2);
   }

   /**
    * input color
    */
   public void initFct(int x) {

   }

   private void reset() {
      part2StartStep = -1;
      stepSizeCounter = 0;
      stepLeftOverCount = 0;
      stepLeftOver = 0;
      stepValue = 1;
      numSteps = 0;
      isPastMiddle = false;
      sizeCounter = 0;
   }

   //#mdebug

   public void toString(Dctx sb) {
      sb.append("#GradientFunction");
      sb.nl();
      sb.append("size1=" + sizePart1 + " size2=" + sizePart2 + " part2StartStep=" + part2StartStep);
      sb.nl();
      sb.append("StepSizeCounter=" + stepSizeCounter + " stepLeftOver=" + stepLeftOver + " stepLeftOverCount=" + stepLeftOverCount);
      sb.append(" NumSteps=" + numSteps);
      sb.nl();
      sb.append("weight1=" + weight1 + " weight2=" + weight2 + " divisor=" + divisor + " p=" + p);
      sb.nl();
      ColorUtils cu = toStringGetUCtx().getColorU();
      sb.append("primary color = " + cu.toStringColor(color_1));
      sb.nl();
      sb.append("secondary color = " + cu.toStringColor(color_2));
      sb.nl();
      sb.append("tertiary color = " + cu.toStringColor(color_3));
      sb.nl();
      sb.append("isDoAlpha=" + isDoAlpha);
      sb.append(" isAlphaC=" + isCAlpha);
      sb.append(" isRedC=" + isCRed);
      sb.append(" isGreenC=" + isCGreen);
      sb.append(" isBlueC=" + isCBlue);
      sb.nl();
      sb.append("isFull=" + isFull);
      sb.append(" isFullLeft=" + isFullLeft);
      sb.append(" isFullRight=" + isFullRight);
      sb.append(" exLeft=" + isExcludeLeft);
      sb.append(" exRight=" + isExcludeRight);
      sb.nl();
      sb.append("middle=" + part2StartStep);
      sb.nl();
      sb.append("fullColor=" + part2StartStep);
      sb.nl();
      sb.nlLvl(grad);

      super.toString(sb.sup());
   }
   //#enddebug

}
