/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.color;

import java.util.Random;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.functions.Function;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.interfaces.IDLogDraw;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;

/**
 * Iterate over a color array that was generated with a {@link Function}.
 * <br>
 * <br>
 * Class provides different iteration means over the color array in relation to pixel values.
 * <br>
 * <br>
 * Similar to a ticking {@link Function}.
 * <br>
 * <li>Total Size decides the number of colors 
 * <li>Color num is fixed and a rule applies to the number of steps. 
 * <li>
 * <br>
 * <br>
 * What happens to the alpha channel in the gradient building
 * @author Charles-Philip Bentley
 * 
 */
public class ColorIterator implements IStringable {

   private final DrwCtx drc;

   public ColorIterator(DrwCtx drc) {
      this.drc = drc;
   }

   public ColorIterator(DrwCtx drc, int[] colors) {
      this.drc = drc;
      this.init(colors, colors.length, 1);
   }

   /**
    * Colors for each step.
    */
   int[]       colors;

   /**
    * Counts the pixels being iterated over.
    * <br>
    * <br>
    * 
    * At each step, count is incremented by increment size.
    * <br>
    * <br>
    * 
    */
   int         countPixel;

   /**
    * Until it reache sizeTotal
    */
   int         countSize      = 0;

   int         countStepLeftOver;

   private int countStepSize  = 0;

   /**
    * Property that changes at each step.
    * <br>
    * Does not change when all steps have the same color.
    */
   public int  currentColor;

   /**
    * generator
    */
   ByteObject  grad;

   /**
    * Variable size steps.
    */
   int         nextStepValue  = 0;

   /**
    * 
    */
   int         numSteps;

   /**
    * Treat the color array as start at offset and being circular ends at offset-1.
    * <br>
    * <br>
    * 
    */
   int         offset;

   /**
    * Number of pixel iterated over.
    */
   int         sizeTotal      = 0;

   /**
    * Current step. 
    * <br>
    * Domain is [0,numsteps[.
    * <br>
    * Starts at 0.
    */
   private int step           = 0;

   /**
    * Pixels left over of the stepping. 
    * <br>
    * Redistribute those pixels evenly or give them all to first/last step.
    */
   int         stepLeftOver;

   /**
    * The size in pixels of an iteration step.
    * <br>
    * <br>
    * {@link ITechGradient#GRADIENT_OFFSET_07_STEP1}.
    */
   int         stepPixelValue = 1;

   /**
    * Not null when steps have different values
    */
   int[]       stepValues;

   private void doSetColor(GraphicsX g) {
      currentColor = getCurrentColor();
      g.setColor(currentColor);
   }

   public int[] getColors() {
      return colors;
   }

   /**
    * The current color according to the step. When step has reached the final 
    * <br>
    * <br>
    * @return
    */
   public int getCurrentColor() {
      if (offset == 0) {
         if (step < colors.length) {
            return colors[step];
         } else {
            return colors[0];
         }
      } else {
         return colors[(step + offset) % colors.length];
      }
   }

   /**
    * The next color at current step.
    * <br>
    * <br>
    * If no next color, returns current color
    * <br>
    * <br>
    * @return
    */
   public int getNextColor() {
      if (offset == 0) {
         if (step + 1 < colors.length) {
            return colors[step + 1];
         } else {
            return currentColor;
         }
      } else {
         return colors[(step + 1 + offset) % colors.length];
      }
   }

   /**
    * The previous color at this step. Looping only if looping is allowed
    * @return
    */
   public int getPreviousColor() {
      if (offset == 0) {
         if (step - 1 >= 0) {
            return colors[step - 1];
         } else {
            return currentColor;
         }
      } else {
         return colors[(step - 1 + offset) % colors.length];
      }
   }

   /**
    * The number of pixels to iterate over.
    * <br>
    * <br>
    * Second element in {@link ColorIterator#init(int[], int, int)} and last element in
    * {@link ColorIterator#getMe(int, ByteObject, int)}.
    * <br>
    * <br>
    * 
    * @return
    */
   public int getTotalSize() {
      return sizeTotal;
   }

   /**
    * Stepping is a function. Depends on the total size and colors
    * 
    * @param colors
    * @param totalSize
    * @param stepping
    */
   public void init(int[] colors, int totalSize, ByteObject stepping) {
      int size12 = totalSize;
      numSteps = size12;
   }

   /**
    * Init the iterator
    * 
    * @param colors
    * @param totalSize size
    * @param stepping
    *            unique base stepping
    */
   public void init(int[] colors, int totalSize, int stepping) {
      this.init(colors, totalSize, totalSize, stepping);
   }

   public void init(int[] colors, int totalSize, int gradsize, int stepping) {
      this.colors = colors;
      sizeTotal = totalSize;
      int numberOfSteps = totalSize;
      if (stepping > 1) {
         // compute left over pixels and redistribute those pixels
         stepLeftOver = totalSize % stepping;
         countStepLeftOver = stepLeftOver;
         numberOfSteps = totalSize / stepping;
         stepPixelValue = stepping;
      } else {
         stepPixelValue = 1;
      }
      numSteps = numberOfSteps;
   }

   /**
    * Simply gets the next color in the array.
    * <br>
    * <br>
    * TODO fix the step
    * <br>
    * <br>
    * The first time it is called, it returns the first color in the array.
    * <br>
    * <br>
    * This call never stops. it goes over and over the color array in a circular fashion.
    * <br>
    * <br>
    * The caller is responsible to know when to stop drawing.
    * <br>
    * <br>
    * 
    * @return
    */
   public int iterateColor() {
      currentColor = getCurrentColor();
      step++;
      if (step >= colors.length) {
         step = 0;
      }
      return currentColor;
   }

   /**
    * Increase step counter or returns -1
    * <br>
    * <br>
    * Sets the step color
    * 
    * @param g
    * @param isStep
    * @return
    */
   private int iteratePixel(GraphicsX g, boolean isStep) {
      if (step == numSteps + 1) {
         return -1;
      } else {
         doSetColor(g);
         int stepPixelVal = stepPixelValue;
         int pixelCounter = countPixel;
         countPixel += stepPixelValue;
         // decides what to do with pixels in excess
         if (step == numSteps) {
            // last step
            countPixel += countStepLeftOver;
            stepPixelVal += countStepLeftOver;
         } else {
            if (countStepLeftOver != 0) {
               countPixel += 1;
               stepPixelVal += 1;
               countStepLeftOver--;
            } else {

            }
         }
         // increment step
         step++;
         if (isStep) {
            return stepPixelVal;
         } else {
            return pixelCounter;
         }
      }
   }

   /**
    * 
    * @param ci
    */
   public void mix(ColorIterator ci) {

   }

   /**
    * Iterates by returning a random value
    * @return
    */
   public int iterateRandom(Random r) {
      return colors[r.nextInt(colors.length)];
   }

   /**
    * Set the step color and return the number of pixels computed since the beginning but before the call of this method. 
    * <br>
    * Thus the first call returns 0. Second call returns 1 or more if step value is bigger. When the number of pixels have been iterated, it returns -1
    * <br>
    * <br>
    * With a step size of 1 and 10 total size, it will return [0,1,2,3,4,5,6,7,8,9,-1]
    * <br>
    * <br>
    * 
    * <code>
    * int count = 0; <br>
    * while( (count = gh.iteratePixelCount(g)) != -1) {<br>
    * <br>
    * }<br>
    * </code> <br>
    * <br>
    * Most commonly used.
    * <br>
    * Used to draw increasingly litte rectangle areas.
    * x + count, y + count, w - count2, h - count2
    * <br>
    * <br>
    * @param g {@link GraphicsX} on which to set the color.
    * @return pixel counter since the beginning of the gradient iteration or -1 when finished
    */
   public int iteratePixelCount(GraphicsX g) {
      // return iteratePixel(g, false);
      if (step == numSteps) {
         return -1;
      } else {
         // set step color
         doSetColor(g);
         int pixelCounter = countPixel; // value to be returned
         countPixel += stepPixelValue;

         // decides what to do with pixels in excess
         if (step == numSteps - 1) {
            // last step
            countPixel += countStepLeftOver;
         } else {
            if (countStepLeftOver != 0) {
               countPixel += 1;
               countStepLeftOver--;
            }
         }
         // increment step
         step++;
         return pixelCounter;
      }
   }

   /**
    * Set the step color and return the number of pixels/value of this step. 
    * <br>
    * <br>
    * While {@link ColorIterator#iteratePixelCount(GraphicsX)} returns the number of pixels consumed
    * since the start, this method returns only the pixels at the given step.
    * <br>
    * <br>
    * 
    * <code>
    * int count = 0; <br>
    * while( (count == iteratePixelStep(g)) != -1) {<br>
    * <br>
    * }<br>
    * </code>
    * <br>
    * <br>
    * Used by 360 circle iteration. Each iteration, increment the angle value with the returned value.
    * <br>
    * <br>
    * @param g {@link GraphicsX} on which to set the color.
    * @return iteration pixel step value or -1 when finished
    */
   public int iteratePixelStep(GraphicsX g) {
      return iteratePixel(g, true);
   }

   /**
    * Arbitrarily sets the current color step and set that color to {@link GraphicsX}.
    * <br>
    * <br>
    * 
    * @param g
    * @param step
    */
   public void iterateSetStep(GraphicsX g, int step) {
      this.step = step;
      doSetColor(g);
   }

   /**
    * Sets the step color on {@link GraphicsX} and return size counter whose domain is [0,gradSize[ 
    * <br>
    * <br>
    * Call this when gradient user iterate over Total Size. The trick
    * <br>
    * <br>
    * <b>When to call this method?</b>
    * <br>
    * 
    * Sometimes Gradient user has gradient step only for color and not for structural drawings. 
    * <br> 
    * In other words, drawing iteration is unrelated to the step. Step size does not influence drawing code.
    * <br>
    * In this case, call this method.
    * <br>
    * The method must be called totalSize times for the gradient process to complete, even with steps bigger than 1.
    * <br>
    * <br>
    * That means with a step of 2, it will set the same color over and over again.
    * <br>
    * <br>
    * 
    * @param g
    * @return -1 when finished, iteration count otherwise [0,gradSize[.
    */
   public int iterateTotalSize(GraphicsX g) {
      if (countSize == sizeTotal) {
         return -1;
      }
      if (countSize == 0) {
         doSetColor(g);
         step++;
         nextStepValue = stepPixelValue;
         if (countStepLeftOver != 0) {
            countPixel += 1;
            nextStepValue += 1;
            countStepLeftOver--;
         }
         countStepSize = 1;
      } else {
         // only call next step
         if (countStepSize == nextStepValue) {
            doSetColor(g);
            step++;
            nextStepValue = stepPixelValue;
            if (countStepLeftOver != 0) {
               countPixel += 1;
               nextStepValue += 1;
               countStepLeftOver--;
            }
            countStepSize = 1;
         } else {
            countStepSize++;
         }
      }
      int ret = countSize;
      countSize++;
      return ret;
   }

   /**
    * rest the iterator to start over with {@link ColorIterator#iterateColor()}
    */
   public void reset() {
      step = 0;
      countPixel = 0;
      countStepLeftOver = 0;
      countSize = 0;
      countStepSize = 0;
   }

   //#mdebug
   /**
    * Prints the first line on the current level, then next lines
    * are indented 
    * @param dc
    */
   public void toString(Dctx dc) {
      dc.root(this, "ColorIterator");
      dc.nl();
      dc.append("sizeTotal=" + sizeTotal);
      dc.nl();
      dc.append(" countStepLeftOver=" + countStepLeftOver);
      dc.append(" countPixel=" + countPixel);
      dc.append(" countPixel=" + countSize);
      dc.append(" countPixel=" + countStepSize);
      dc.nl();
      dc.append(" numSteps=" + numSteps);
      dc.append(" step=" + step);
      dc.append(" stepLeftOver=" + stepLeftOver);
      dc.append(" stepPixelValue=" + stepPixelValue);
      dc.append(" nextStepValue=" + nextStepValue);
      dc.nlLvl(grad);
      
      dc.nlLvl("stepValues", stepValues, 1);
      
      dc.append("#colors = " + colors.length);

      if (dc.hasFlagData(drc, IDLogDraw.DATA_FLAG_21_MANY_COLORS)) {
         ColorUtils cu = toStringGetUCtx().getColorU();
         for (int i = 0; i < colors.length; i++) {
            dc.nl();
            dc.append(cu.toStringColor(colors[i]));
         }
      }
   }

   /**
    * Called when  {@link Dctx} see the same object for another time.
    * <br>
    * Prints without calling new lines..content smaller
    * @param dc
    */
   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "ColorIterator");
   }

   public String toString() {
      return Dctx.toString(this);
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   public UCtx toStringGetUCtx() {
      return drc.getUCtx();
   }

   //#enddebug

}
