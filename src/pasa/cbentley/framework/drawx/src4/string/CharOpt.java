/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;

/**
 * Class for drawing Strings and characters without using objects. Utilz class
 * <br>
 * <br>
 * 
 * @author Charles-Philip Bentley
 *
 */
public class CharOpt {
   final static int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };

   // Requires positive x
   static int stringSize(int x) {
	 for (int i = 0;; i++)
	    if (x <= sizeTable[i])
		  return i + 1;
   }

   /**
    * All possible chars for representing a number as a String
    */
   final static char[] digits    = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
	    'w', 'x', 'y', 'z'     };

   final static char[] DigitTens = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3',
	    '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
	    '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9', };

   final static char[] DigitOnes = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1',
	    '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', };

   /**
    * Places characters representing the integer i into the
    * character array buf. The characters are placed into
    * the buffer backwards starting with the least significant
    * digit at the specified index (exclusive), and working
    * backwards from there.
    *
    * Will fail if i == Integer.MIN_VALUE
    */
   static void getChars(int i, int index, char[] buf) {
	 int q, r;
	 int charPos = index;
	 char sign = 0;

	 if (i < 0) {
	    sign = '-';
	    i = -i;
	 }

	 // Generate two digits per iteration
	 while (i >= 65536) {
	    q = i / 100;
	    // really: r = i - (q * 100);
	    r = i - ((q << 6) + (q << 5) + (q << 2));
	    i = q;
	    buf[--charPos] = DigitOnes[r];
	    buf[--charPos] = DigitTens[r];
	 }

	 // Fall thru to fast mode for smaller numbers
	 // assert(i <= 65536, i);
	 for (;;) {
	    q = (i * 52429) >>> (16 + 3);
	    r = i - ((q << 3) + (q << 1)); // r = i-(q*10) ...
	    buf[--charPos] = digits[r];
	    i = q;
	    if (i == 0)
		  break;
	 }
	 if (sign != 0) {
	    buf[--charPos] = sign;
	 }
   }

   public static int draw(GraphicsX g, int i, int x, int y, IMFont f, int color, int bgColor) {
	 int size = (i < 0) ? stringSize(-i) + 1 : stringSize(i);
	 char[] cbuf = getFreeBuf();
	 getChars(i, size, cbuf);
	 int count = 0;
	 for (int j = 0; j < size; j++) {
	    char ch = cbuf[j];
	    count += f.charWidth(ch);
	 }
	 g.setColor(bgColor);
	 g.fillRect(x, y, count, f.getHeight());
	 g.setColor(color);
	 count = 0;
	 for (int j = 0; j < size; j++) {
	    char ch = cbuf[j];
	    g.drawChar(ch, x + count, y, GraphicsX.ANCHOR);
	    count += f.charWidth(ch);
	 }
	 closeBuf(cbuf);
	 return count;
   }

   public static int draw(GraphicsX g, int i, int x, int y, IMFont f) {
	 //number of chars to draw
	 int size = (i < 0) ? stringSize(-i) + 1 : stringSize(i);
	 char[] cbuf = getFreeBuf();
	 getChars(i, size, cbuf);
	 int count = 0;
	 for (int j = 0; j < size; j++) {
	    char ch = cbuf[j];
	    g.drawChar(ch, x + count, y, GraphicsX.ANCHOR);
	    count += f.charWidth(ch);
	 }
	 closeBuf(cbuf);
	 return count;
   }

   static void closeBuf(char[] buf) {
	 if (buf == cbuf1)
	    cbufCount1 = 0;
	 if (buf == cbuf2)
	    cbufCount2 = 0;
   }

   /**
    * Supports up to 2 concurrent thread
    * @return
    */
   static char[] getFreeBuf() {
	 if (cbufCount1 == 0)
	    return cbuf1;
	 if (cbufCount2 == 0)
	    return cbuf2;
	 return new char[33];
   }

   static char[] cbuf1      = new char[33];

   static char[] cbuf2      = new char[33];

   static int    cbufCount1 = 0;

   static int    cbufCount2 = 0;

   public static int draw(GraphicsX g, String str, int x, int y, IMFont f) {
	 g.drawString(str, x, y, GraphicsX.ANCHOR);
	 return f.stringWidth(str);
   }

}
