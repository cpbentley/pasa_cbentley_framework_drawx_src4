package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

public class TextStats extends ObjectDrw {

   private int     biggestLineH;

   private int     biggestLineW;

   private int     charBiggestWidth;

   private boolean hasZeroWidthChars;

   private boolean isTestHeight;

   private boolean isTrimmedH;

   private boolean isTrimmedW;

   private int     linesTotalH;

   private int     maxHeight;

   /**
    * Flag telling us, that even if we have not a flagged font as monospace and/or different styles
    * 
    * all characters have indeed the same width. The {@link LineAlgo} will try to invalidate
    * this flag.
    */
   private boolean sameCharWidthFact;

   private int     sameCharWidthFactValue;

   private boolean tempDiffFontHeights;

   private int     tempMaxLineHeight;

   private int tabLineCount;

   public TextStats(DrwCtx drc) {
      super(drc);
   }

   public int getBiggestLineH() {
      return biggestLineH;
   }

   public int getBiggestLineW() {
      return biggestLineW;
   }

   public int getCharBiggestWidth() {
      return charBiggestWidth;
   }

   public int getLineMaxH() {
      return tempMaxLineHeight;
   }

   public int getLinesTotalH() {
      return linesTotalH;
   }

   public int getMaxHeight() {
      return maxHeight;
   }

   public int getSameCharWidthFactValue() {
      return sameCharWidthFactValue;
   }

   public boolean hasLineDiffFontHeights() {
      return tempDiffFontHeights;
   }

   public boolean hasZeroWidthChars() {
      return hasZeroWidthChars;
   }

   public boolean isTestHeight() {
      return isTestHeight;
   }

   public boolean isTrimmedH() {
      return isTrimmedH;
   }

   public void lineCheckHeight(int fontHeight) {
      if (tempMaxLineHeight == -1) {
         tempMaxLineHeight = fontHeight;
      } else {
         if (fontHeight != tempMaxLineHeight) {
            tempDiffFontHeights = true;
         }
         if (tempMaxLineHeight < fontHeight) {
            tempMaxLineHeight = fontHeight;
         }
      }
   }

   public void processChar(CharAlgo ca) {
      int cw = ca.getWidth();
      //deal with character size stats
      if (cw == 0) {
         hasZeroWidthChars = true;
         sameCharWidthFact = false;
      }
      //check if they are the same width anyways in case
      if (cw > getCharBiggestWidth()) {
         charBiggestWidth = cw;
      }
      if (sameCharWidthFact) {
         if (sameCharWidthFactValue == 0) {
            sameCharWidthFactValue = cw;
         } else {
            if (cw != sameCharWidthFactValue) {
               sameCharWidthFact = false;
            }
         }
      }
   }

   public void processLine(LineStringer line) {
      int pixelsHLine = line.getPixelsH();
      if (isTestHeight && (getLinesTotalH() + pixelsHLine) > maxHeight) {
         isTrimmedH = true;
      } else {
         linesTotalH = getLinesTotalH() + pixelsHLine;
         if (pixelsHLine > getBiggestLineH()) {
            biggestLineH = pixelsHLine;
         }
         if (line.getPixelsW() > getBiggestLineW()) {
            biggestLineW = line.getPixelsW();
         }
      }
      
   }
   
   public int getTabLineCountAndIncrement() {
      int v = tabLineCount;
      tabLineCount++;
      return v;
   }

   public void resetLineStats(int lineFontH) {
      tempMaxLineHeight = lineFontH;
      tempDiffFontHeights = false;
      tabLineCount = 0;
   }

   public void setMaxHeight(int maxHeight) {
      this.maxHeight = maxHeight;
   }

   public void setTestHeight(boolean isTestHeight) {
      this.isTestHeight = isTestHeight;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, TextStats.class, 75);
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.appendVarWithNewLine("isTestHeight", isTestHeight);
      dc.appendVarWithNewLine("maxHeight", maxHeight);
      dc.appendVarWithSpace("biggestLineH", biggestLineH);
      dc.appendVarWithSpace("biggestLineW", biggestLineW);

      dc.appendVarWithNewLine("linesTotalH", getLinesTotalH());
      dc.appendVarWithSpace("isTrimmedH", isTrimmedH);
      dc.appendVarWithSpace("isTrimmedW", isTrimmedW);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, TextStats.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {

   }
   //#enddebug

}
