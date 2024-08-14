package pasa.cbentley.framework.drawx.src4.anim;

import java.util.Random;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.IntUtils;
import pasa.cbentley.core.src4.utils.interfaces.IColors;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;

/**
 * Line shifter of an RgbImage. 
 * <br>
 * <br>
 * Horizontal shift is easier to implement and is a little faster.
 * Horizontally:
 * <li><b>|>|d|</b> Left to Right In for {@link ByteObject#ANIM_TIME_1_ENTRY} types of animations 
 * <li><b>|d|>|</b> Left to Right Out for {@link ByteObject#ANIM_TIME_2_EXIT} types of animations 
 * <li><b>|d|<|</b> Right to Left Int for {@link ByteObject#ANIM_TIME_1_ENTRY} types of animations 
 * <li><b>|<|d|</b> Right to Left Out for {@link ByteObject#ANIM_TIME_2_EXIT} types of animations 
 * <br>
 * <br>
 * Vertically
 * <li>Top to Bottom In
 * <li>Top to Bottom Out
 * <b>Options</b> <br>
 * <li>Shifts in to disappeance. Clip on Drawable area the position
 * <li>Shift Move
 * <br>
 * <br>
 * <b>Animations Mix</b> : <br>
 * TODO How do you mix LineShift and AlphaChange on the same Array? 
 * <br>
 * <br>
 * Function Base shift <br>
 * Size of function, Amplitude of function.
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 *
 * @see ImgAnimable
 */
public class AnimRgbImageShiftLines extends AnimRgbImage implements ITechShiftLine {

   /**
    * Equal to {@link Drawable}'s height in case of horizontal shifting.
    */
   private int     bh;

   private int[]   buffer;

   /**
    * The number of additional pixels. That is bw - dw.
    */
   private int     bufferIncrement;

   /**
    * 1 time the Drawable width + the shift size.
    */
   private int     bw;

   /**
    * When true shift the buffer when drawing.
    */
   private boolean isBufferAtDrawable = true;

   private int     lastPixelShift;

   /**
    * 
    * Line offset from Left/Top in the buffer.
    * <br>
    * <br>
    * Sets to -1 when line has arrived to final position.
    */
   private int[]   linesX;

   /**
    * Decrementing number of lines
    */
   private int     numLinesNotFullyShifted;

   /**
    * Incrementing number of lines to shift at a given turn.
    * 
    */
   private int     numLinesToShift;

   Random          r;

   /**
    * The number of pixels lines shift at animation turns.
    */
   private int     shiftPixelStep;

   public AnimRgbImageShiftLines(DrwCtx gc, ByteObject def) {
      super(gc, def);
   }

   /**
    * Iterate over each line and check if it can be shifted.
    */
   private void doShiftLeftToRight() {
      int imgHeight = img.getHeight();
      int imgWidth = img.getWidth();
      for (int i = 0; i < imgHeight; i++) {
         //the shift size for this line is a function of the turn and of neighbouring lines
         if (linesX[i] >= 0) {
            int shiftSize = getShiftSize();
            int lineStartOffset = linesX[i]; //in buffer
            int lineEndOffset = lineStartOffset + imgWidth; //in buffer
            if (lineEndOffset + shiftSize >= bw * (i + 1)) {
               linesX[i] = -2;
               numLinesNotFullyShifted--;
               shiftSize = bw * (i + 1) - lineEndOffset;
            } else {
               linesX[i] += shiftSize;
            }
            IntUtils.shiftInt(buffer, shiftSize, lineStartOffset, lineEndOffset, true);

         } else {
            //check if we can enable line shifting
            //next turn will start shifting
            if (linesX[i] == -1) {
               //not started
            }
         }
      }
   }

   /**
    * Image data in buffer starts at the left.
    * Shift starts with top line and each turn, next line starts shifting.
    * @param iw
    */
   private void doShiftLeftToRight(int iw) {
      StringBBuilder sb = new StringBBuilder(drc.getUC());
      for (int i = 0; i < numLinesToShift; i++) {
         if (i != 0) {
            sb.append(",");
         }
         sb.append(linesX[i]);
         if (linesX[i] != -1) {
            int shiftSize = shiftPixelStep;
            int start = linesX[i];
            int end = start + iw;
            if (linesX[i] + shiftSize + iw >= bw * (i + 1)) {
               linesX[i] = -1;
               numLinesNotFullyShifted--;
               //shift it to the end
               shiftSize = bw * (i + 1) - end;
            } else {
               linesX[i] += shiftSize;
            }
            IntUtils.shiftInt(buffer, shiftSize, start, end, true);

         }
      }
      //increase the number of lines
      numLinesToShift++;
      if (numLinesToShift > bh) {
         numLinesToShift = bh;
      }
   }

   /**
    * Values in linesX goes to 0
    */
   private void doShiftRightToLeft() {
      int iw = img.getWidth();
      for (int i = 0; i < numLinesToShift; i++) {
         if (linesX[i] != -1) {
            int shiftSize = shiftPixelStep;
            int start = linesX[i];
            int end = start + iw;
            if (linesX[i] - shiftSize <= bw * i) {
               shiftSize = (bw * i) - (linesX[i] - shiftSize);
               linesX[i] = -1;
               numLinesNotFullyShifted--;
            } else {
               linesX[i] -= shiftSize;
            }
            IntUtils.shiftInt(buffer, -shiftSize, start, end, true);
         }
      }
   }

   private void doShiftTopToBottom() {
      int imgHeight = img.getHeight();
      int imgWidth = img.getWidth();
      for (int i = 0; i < imgWidth; i++) {
         int shiftsize = getShiftSize();

         shiftLineDown(i, shiftsize);

      }

   }

   /**
    * Random or fixed. 
    * In the random case, random but with a maximum different with previous line.
    * @return
    */
   private int getShiftSize() {
      if (r != null) {
         int shift = r.nextInt(shiftPixelStep) + 1;
         lastPixelShift = shift;
         return shift;
      }
      return shiftPixelStep;
   }

   /**
    * Looks if index may go at this turn
    * @param index
    * @param turn
    * @return
    */
   private int getShiftSize(int index, int turn) {
      return 2;
   }

   private boolean hasFlagShiftLine(int flag) {
      return definition.hasFlag(TECH_OFFSET_1_FLAG, flag);
   }

   /**
    * Initialize the buffer
    */
   public void lifeStart() {

      int[] imgData = img.getRgbCopy();
      bufferIncrement = definition.get2(TECH_OFFSET_3_SHIFT_SIZE2);
      if (hasFlagShiftLine(TECH_FLAG_1UP_AND_DOWN)) {
         if (hasFlagShiftLine(TECH_FLAG_4MULTIPLY)) {
            bufferIncrement = img.getHeight() * (bufferIncrement - 1);
         }
         bw = img.getWidth();
         bh = img.getHeight() + bufferIncrement;
         numLinesNotFullyShifted = img.getWidth();
      } else {
         if (hasFlagShiftLine(TECH_FLAG_4MULTIPLY)) {
            bufferIncrement = img.getWidth() * (bufferIncrement - 1);
         }
         bw = img.getWidth() + bufferIncrement;
         bh = img.getHeight();
         numLinesNotFullyShifted = img.getHeight();
      }

      buffer = new int[bw * bh];

      int bufferOffset = 0;
      int offset = 0;

      IntUtils.fill(buffer, IColors.FULLY_OPAQUE_BLUE);

      //copy the image data in right place
      if (hasFlagShiftLine(TECH_FLAG_2REVERSE)) {
         bufferOffset = bufferIncrement;
      }
      for (int i = 0; i < img.getHeight(); i++) {
         System.arraycopy(imgData, offset, buffer, bufferOffset, img.getWidth());
         bufferOffset += bw;
         offset += img.getWidth();
      }
      shiftPixelStep = 1;
      numLinesToShift = 1;
      linesX = new int[img.getHeight()];
      for (int i = 0; i < linesX.length; i++) {
         linesX[i] = i * bw;
      }
   }



   private void shiftLineDown(int index, int shiftsize) {
      int endStart = linesX[index] + img.getHeight();
      int startTopLine = linesX[index];
      int offsetShift = bw * shiftsize;
      for (int i = endStart; i >= startTopLine; i--) {
         //take pixel and move it down
         int pixelOffset = (i * bw) + index;
         buffer[pixelOffset + offsetShift] = buffer[pixelOffset];
      }
   }

   private void shiftLineLeft(int index, int shiftsize) {

   }

   private void shiftLineRight(int index, int shiftSize, int lineStartOffset, int lineEndOffset) {
      IntUtils.shiftInt(buffer, shiftSize, lineStartOffset, lineEndOffset, true);
   }

   private void shiftLineUp(int index, int shiftsize) {
      int endStart = linesX[index] + img.getHeight();
      int startTopLine = linesX[index];
      int offsetShift = bw * shiftsize;
      for (int i = startTopLine; i < endStart; i++) {
         //take pixel and move it down
         int pixelOffset = (i * bw) + index;
         buffer[pixelOffset - offsetShift] = buffer[pixelOffset];
      }
   }

   
   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, "ShiftLines");
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   private void toStringPrivate(Dctx dc) {
      dc.append("bw=" + bw + " bh=" + bh);
      if (linesX != null) {
      }
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "ShiftLines");
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug
   

}
