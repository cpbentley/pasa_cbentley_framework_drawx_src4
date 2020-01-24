package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.core.src4.utils.IntUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.factories.drawer.StringDrawer;

public class StringDrawUtils {

   /**
    * Breaks specified character array and returns a break matrix by line,
    * organized as [line][0 | 1] where 0 means starting offset in text for this
    * line, 1 means number of characters of this line.
    * 
    * @param text  The string to break
    * @param width Width in pixels to break on
    * @return A break index table
    */
   public static int[][] breakString(char[] text, int width, IMFont font) {
      // Count text lines
      int offset = 0;
      int lines = 0;
      int newOffset;
   
      //count the number of lines
      while (offset < text.length) {
         newOffset = findNextBreak(text, offset, text.length - offset, width, font);
         lines++;
         offset = newOffset;
      }
   
      int[][] indices = new int[lines][2];
   
      // Setting offset data
      lines = 0;
      offset = 0;
      while (offset < text.length) {
         newOffset = findNextBreak(text, offset, text.length - offset, width, font);
         indices[lines][0] = offset;
         indices[lines][1] = newOffset - offset;
         lines++;
         offset = newOffset;
      }
   
      return indices;
   }

   /**
    * Break text starting at offset
    * returns as soon as we have maxLines of data
    * @param text
    * @param offset
    * @param width
    * @param font
    * @return
    */
   public static int[][] breakString(char[] text, int startOffset, int width, IMFont font, int maxLines) {
      // Count text lines
      int offset = 0;
      int lines = 0;
      int newOffset;
   
      //count the number of lines
      while (offset < text.length) {
         newOffset = findNextBreak(text, offset, text.length - offset, width, font);
         lines++;
         offset = newOffset;
      }
      if (lines > maxLines)
         lines = maxLines;
      int[][] indices = new int[lines][2];
   
      // Setting offset data
      lines = 0;
      offset = startOffset;
   
      while (offset < text.length) {
         newOffset = findNextBreak(text, offset, text.length - offset, width, font);
         indices[lines][0] = offset;
         indices[lines][1] = newOffset - offset;
         lines++;
         offset = newOffset;
         if (lines >= indices.length)
            break;
      }
   
      return indices;
   }

   public static int[][] breakString(String str, int width, IMFont font) {
      return breakString(str.toCharArray(), width, font);
   }

   /**
    * Counts the number of lines with the given color
    * Start from the top
    * @param imgH
    * @param imgW
    * @param rgb
    * @return
    */
   public static int countLinesWithBlack(int imgH, int imgW, int[] rgb) {
      return countLinesWithColor(imgH, imgW, rgb, ColorUtils.FULLY_TRANSPARENT_BLACK);
   }

   public static int countLinesWithColor(int imgH, int imgW, int[] rgb, int color) {
      boolean started = false;
      int last = 0;
      int count = 0;
      int startLine = 0;
      for (int j = 0; j < imgH; j++) {
         boolean hasBlack = !IntUtils.isOnly(rgb, startLine, imgW, color);
         startLine += imgW;
         if (!started && hasBlack) {
            started = true;
            last = 0;
         }
         if (started)
            count++;
         if (started && !hasBlack) {
            last++;
         }
      }
      return count - last;
   }

   /**
    * Returns next break when breaking a string.
    * 
    * @param text   The chars to calculate on
    * @param offset From what offset to read in chars
    * @param len    How many characters to read
    * @param w      Width
    * @param f      Font
    * @return Offset of next break or length of text if no more breaks
    */
   public static int findNextBreak(char[] text, int offset, int len, int w, IMFont f) {
      int breakOffset = offset;
      int textW = 0;
      int niceB = -1;
      char c;
      charLoop: while (breakOffset <= offset + len && textW < w) {
         if (breakOffset == offset + len)
            c = StringDrawer.TEXTBREAKS[0]; // last character + 1, fake break char
         else
            c = text[breakOffset];
         if (c == StringDrawer.NEWLINE) {
            // got a nice break here, new line
            niceB = breakOffset;
            break charLoop;
         }
   
         // Try finding break charachters
         breakCharLoop: for (int i = StringDrawer.TEXTBREAKS.length - 1; i >= 0; i--) {
            if (c == StringDrawer.TEXTBREAKS[i]) {
               niceB = breakOffset;
               break breakCharLoop;
            }
         }
         if (breakOffset == offset + len - 1) {
            // Special case, skip the last character
            niceB = breakOffset + 1;
         }
         breakOffset++;
         textW += f.charWidth(c);
      }
      if (niceB > offset && niceB < offset + len - 2 && (text[niceB + 1] == ' '))
         return niceB + 2; // case: special case to get rid of extra spaces
      else if (niceB > offset && niceB < offset + len)
         return niceB + 1; // case: found a nice break, use this
      else if (breakOffset > offset + 1)
         return breakOffset - 1; // case: broke due to text width too big
      else if (breakOffset == offset)
         return breakOffset + 1; // case: broken on first char, step one more
      else
         return breakOffset; // case: default
   }

   public static char getChar(int offset, char[] ar, String str) {
      if (ar != null)
         return ar[offset];
      else
         return str.charAt(offset);
   }

   /**
    * Return the maximum size of a character in the string
    * @param str
    * @param font
    * @return
    */
   public static int getLetterMax(String str, IMFont font) {
      int max = font.stringWidth(" ");
      int size = 0;
      for (int i = 0; i < str.length(); i++) {
         size = font.substringWidth(str, i, 1);
         if (size > max)
            max = size;
      }
      return size;
   }

   /**
    * Trim on 1 lines
    * @param str
    * @param width
    * @param lines
    * @param f
    * @return
    *  null if String fits completely in Width and does not need to be trimmed
    */
   public static int[] getTrim(String str, int width, IMFont f) {
      int[] tr = getTrim(str, width, 1, f);
      if (tr[0] == str.length() - 1)
         return null;
      return tr;
   }

   /**
    * At least one letter per line
    * @param str
    * @param width
    * @param maxLines -1 if infinity of lines
    * @param f
    * @return
    * An array whose length is the number of lines + 1
    * The first value is a control value.
    * Each values is the number of characters on the line
    */
   public static int[] getTrim(String str, int width, int maxLines, IMFont f) {
      int wc = 0;
      int lineCount = 0;
      int[] trim = new int[maxLines];
      int len = str.length();
      for (int i = 0; i < len; i++) {
         wc += f.charWidth(str.charAt(i));
         if (wc >= width) {
            trim[lineCount] = i;
            if (lineCount + 1 == maxLines) {
               return trim;
            } else {
               lineCount++;
               wc = 0;
            }
         }
      }
      //no trimming if code reaches this line.
      trim[lineCount] = len - 1;
      return trim;
   }

   public static int maxNumChars(String s, int width, IMFont f) {
      int wc = 0;
      for (int i = 0; i < s.length(); i++) {
         if (wc >= width)
            return i;
         wc += f.charWidth(s.charAt(0));
      }
      return s.length();
   }

   /**
    * Returns the maximum number of characters that can be drawn in the given
    * width using the Font
    * @param s
    * @param width
    * @param lines number of lines available to draw s
    * @param style
    * @return
    */
   public static int maxNumChars(String s, int width, int lines, IMFont f) {
      int wc = 0;
      int lineCount = 1;
      for (int i = 0; i < s.length(); i++) {
         if (wc >= width) {
            if (lineCount == lines) {
               return i;
            } else {
               lineCount++;
               wc = 0;
            }
         }
         wc += f.charWidth(s.charAt(i));
      }
      return s.length();
   }

}
