package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.factories.drawer.StringDrawer;

/**
 * Tracks the metering and drawing of a String of characters for the {@link StringDrawable}.
 * <br>
 * <br>
 * Provides access to character positions with the class {@link StringMetrics}. This allow the editing module
 * to position the caret Stuff used by both drawing and metrics.
 * <br>
 * <br>
 * 
 * Optimization. Buffer for String Editing.
 * <br>
 * <br>
 * <b>String selection</b>
 * <br>
 * <br>
 * String selection is implemented with an interval which is rendered using an additional Text Fx.
 * and setting interval {@link Stringer#setInterval(int, int)}
 * <br>
 * <br>
 * How  do you defined 10 different style of fx assigned by order or randomly to each character of a word?
 * The root fx defines 10 sub object of type {@link IDrwTypes#TYPE_050_FIGURE}
 * <br>
 * <br>
 * @see StringFx
 * @see StringMetrics
 * @see IFxStr
 * <br>
 * <br>
 * 
 * @author Charles-Philip Bentley
 *
 */
public class Stringer extends StringDrawer {

   public static final int BREAK_EXTRA_SIZE                  = 2;

   /**
    * 
    */
   public static final int BREAK_HEADER_SIZE                 = 1;

   public static final int BREAK_TRAILER_SIZE                = 1;

   public static final int BREAK_WINDOW_SIZE                 = 3;

   /**
    * Set when
    */
   public static final int STATE_01_CHAR_EFFECTS             = 1;

   /**
    * Set when all characters widths have been computed.
    */
   public static final int STATE_02_CHAR_WIDTHS              = 1 << 1;

   public static final int STATE_03_CHECK_CLIP               = 1 << 2;

   /**
    * Modifies the char array and reduces the length
    */
   public static final int STATE_04_TRIMMED                  = 1 << 3;

   /**
    * 
    */
   public static final int STATE_05_STR_WIDTH                = 1 << 4;

   /**
    * Set when characters coordinates have been computed.
    * <br>
    * <br>
    * 
    */
   public static final int STATE_06_CHAR_POSITIONS           = 1 << 5;

   /**
    * When the string had to be broken into several pieces.
    */
   public static final int STATE_07_BROKEN                   = 1 << 6;

   /**
    * Set when at least one Static style is impacting at least one character.
    */
   public static final int STATE_08_ACTIVE_STYLE             = 1 << 7;

   /**
    * This enable word indexing and looking for space and punctuation.
    * <br>
    * <br>
    * 
    */
   public static final int STATE_09_WORD_FX                  = 1 << 8;

   /**
    * Set when at least one dynamic style is impacting at least one character.
    */
   public static final int STATE_10_ACTIVE_DYNAMIC_STYLE     = 1 << 9;

   public static int       STATE_11_DIFFERENT_FONTS          = 1 << 10;

   /**
    * Set when there is a FX component to the String. anything other than basic color will set this flag
    * 
    */
   public static final int STATE_13_FX                       = 1 << 12;

   /**
    * Set when characters are set horizontally as provided by {@link GraphicsX#drawChars(char[], int, int, int, int, int)} method.
    * <br>
    * <br>
    * Colors could change though. in which case {@link Stringer#TYPE_1_SINGLE_LINE_FX}
    * <br>
    * <br>
    * 
    */
   public static final int STATE_14_BASIC_POSITIONING        = 1 << 13;

   /**
    * At least one {@link StringFx} defines a bg or a style hosted in a {@link FigDrawable}.
    * <br>
    * <br>
    * 
    */
   public static final int STATE_15_BG_DEFINED               = 1 << 14;

   public static final int STATE_16_STATIC_INDEX_FX          = 1 << 15;

   /**
    * Single line no breaks, no fx.
    * <br>
    * <br>
    * This is the straight forward line drawing. Are {@link StringMetrics} fully computed?
    * <br>
    * We don't really need them until editmodule requires specific char positions
    * <br>
    * This will be used temporarily for the line shape mask.
    * <br>
    * <br>
    * 
    */
   public static final int TYPE_0_SINGLE_LINE                = 0;

   /**
    * There is a {@link StringFx} but no breaks
    */
   public static final int TYPE_1_SINGLE_LINE_FX             = 1;

   /**
    * Breaks no special effect.
    * <br>
    * <br>
    * 
    */
   public static final int TYPE_2_BREAKS                     = 2;

   /**
    * Fx and breaks
    */
   public static final int TYPE_3_BREAKS_FX                  = 3;

   public static final int TYPE_7_LINE_BREAKS_WORD_BREAKS_FX = 7;

   /**
    * Type {@link IDrwTypes#TYPE_051_BOX}.
    * Anchors the String figure in the box.
    * <br>
    * However String have a locale associated with it.
    * Therefore the String is a 
    * <li> Bidi (bi directional) texts also are a challenge
    * <li>dextrosinistral text
    * <li>sinistrodextral text from the left to the right
    */
   ByteObject              anchor;

   /**
    * Used for alignment
    */
   int                     areaH;

   /**
    * Used for alignment
    */
   int                     areaW;

   /**
    * the x coordinate at which the draw the String.
    * <br>
    * <br>
    * When drawing shapes on a {@link RgbImage} for mask, this value is not used
    */
   int                     areaX;

   int                     areaY;

   /**
    * When many different styles are used at the character level, the synthesis of merges is set here.
    * <br>
    * <br>
    * The merge is done for line-word-char.
    * <br>
    * <br>
    * Therefore char specific fx will always be highest priority.
    * <br>
    * <br>
    * 
    */
   StringFx[]              charFxs;

   /**
    * Modified when Trimming. Thus the word Charles trimmed at letter 5 will be Cha..
    * <br>
    * Original reference used by {@link Stringer#initFig(ByteObject, int, int, int, int, char[], int, int)}.
    * <br>
    * <br>
    * This char array is fully controlled. While source char array may be from a bigger source.
    * <br>
    * What happens when the chars include the .. of a trim cue?
    */
   char[]                  chars;

   /**
    * Stores original string before trimming
    */
   char[]                  charsBackup;

   public boolean          debugArea                         = false;

   int                     drawLineType;

   int                     drawWordType;

   /**
    * Global Text effects definition. Never null. First it is the translation of String figure values.
    * <br>
    * Then it is merged with generic fx fields . char mask.
    * <br>
    * i.e a line fx that applies to all lines is stored here.
    * <br>
    * 
    * The {@link StringFx} define alternate {@link StringFx} which are used based on index or an interval
    * <br>
    * <br>
    * 
    */
   StringFx                fx                                = null;

   /**
    * This array hosts the dynamic defintion starting at index 1. Index 1 means the reset to default
    * styles for the given interval in method {@link Stringer#setInterval(int, int, int)}.
    * <br>
    * <br>
    * A dynamic style treats the interval as a String. Thus, such a text effect may have its own
    * set of static text effects. For instance a special fx for the first and last index of the interval.
    * <br>
    * <br>
    * 
    * The anchor of additional styles are ignored. Whenever there is a conflict, common sense prevails and
    * root text effects are applied.
    * <br>
    * <br>
    * 
    */
   StringFx[]              fxsDynamic;

   /**
    * Definition of dynamic fxs.
    */
   ByteObject[]            fxsDynamicDefinition;

   /**
    * Array for the static fx that will be applied to characters/words using the rule based system for deciding 
    * on what index a given static style is applied.
    * <br>
    * <br>
    * <li>{@link IFxStr#FX_OFFSET_04_INDEX2}
    * <li>{@link IFxStr#FX_OFFSET_05_INDEX_PATTERN1}
    * <br>
    * <br>
    * Array never contains nulls.
    * <br>
    * <br>
    * 
    */
   StringFx[]              fxsStatic;

   private StringFx[]      fxsStaticIndex;

   /**
    * Tracks the intervals for the Dynamic Styles.
    * <br>
    * <br>
    * {@link IDrwTypes#TYPE_070_TEXT_EFFECTS} already affecting an index will merge with the dynamic style.
    * <br>
    * <br>
    * By Default Dynamic text effect merges over.
    * <br>
    * <br>
    * 
    * Created on demand. So by default extra fxs are not loaded since most String items will use only the default
    * styling until the user makes an action to modify.
    * <br>
    * <br>
    * 
    */
   IntBuffer[]             intervals;

   /**
    * the length of characters starting offsetChars.
    */
   int                     lengthChars;

   /**
    * When specific line fxs are used.
    */
   StringFx[]              lineFxs                           = null;

   StringMetrics           metrics                           = null;

   /**
    * Absolute offset for reading characters in the character array.
    */
   int                     offsetChars;

   /**
    * Provides the {@link Stringer} with easily accessible state information.
    * <br>
    * <br>
    * <li> {@link Stringer#STATE_04_TRIMMED}
    */
   private int             states;

   /**
    * The {@link StringDraw} based on areaX and areaY.
    */
   StringDraw              stringDraw                        = null;

   /**
    * 
    */
   int[]                   styleFlags;

   /**
    * 
    */
   ByteObject              text;

   public Stringer(DrwCtx drc) {
      super(drc);
      metrics = new StringMetrics(drc, this);
      fx = new StringFx(drc, this);
      stringDraw = new StringDraw(drc, this);
   }

   /**
    * Called by 
    * <br>
    * Updates the breaks
    * <br>
    * @param cs update char array
    * @param index
    * @param c
    */
   public void addChar(char[] cs, int index, char c) {
      chars = cs;
      metrics.addChar(index, c);
      lengthChars++;
   }

   public synchronized void deleteCharAt(char[] cs, int indexRelative) {
      chars = cs;
      //metrics uses the previous len value
      metrics.deleteCharAt(indexRelative);
      lengthChars--;
   }

   /**
    * Entry point but draws all the characters
    * <br>
    * <br>
    * X and Y positions are the ones currently set at the {@link StringDraw} tracker.
    * <br>
    * <br>
    * Metrics have been initialized? If String hasn't been broken, everything is drawn on one line.
    * <br>
    * <br>
    * 
    * @param g
    */
   public void draw(GraphicsX g) {
      if (debugArea) {
         g.setColor(ColorUtils.FULLY_OPAQUE_RED);
         g.drawRect(areaX - 1, areaY - 1, areaW + 1, areaH + 1);
      }
      if (fx.scale != null) {
         drawScaled(g);
      } else {
         drawOffsets(g, areaX, areaY, 0, lengthChars, 0, metrics.getNumOfLines());
      }
      //System.out.println(this.toString());
   }

   /**
    * Draw the given character index from (x,y) position last given in the {@link Stringer#initFig(ByteObject, int, int, int, int, char[], int, int)}
    * <br>
    * <br>
    * Used cached instance.
    * Draws any charFx of this line.
    * <br>
    * What about Line FXs?
    * <br>
    * <br>
    * Called by user of {@link Stringer} when a specific character needs to be drawn again.
    * <br>
    * <br>
    * 
    * @param g
    * @param caretIndex
    */
   public void drawChar(GraphicsX g, int indexRelative) {
      if (hasState(STATE_01_CHAR_EFFECTS)) {
         getDraw().drawCharFx(g, chars[offsetChars + indexRelative], indexRelative);
      } else {
         getDraw().drawChar(g, chars[offsetChars + indexRelative], indexRelative);
      }
   }

   /**
    * 
    * Entry point for the {@link StringDrawable} draw content method to paint parts of the String on {@link GraphicsX}.
    * <br>
    * <br>
    * Draws the desired lines and characters specified by the scrolling offsets.
    * It tries to honour the offset. Line offsets are meaningless if there is one line.
    * <br>
    * <br>
    * This is fine for line {@link StringFx} but what happens to Paragraph or {@link IFxStr#FX_SCOPE_4_TEXT}
    * when a line is drawn again?
    * <br>
    * <br>
    * {@link StringFx} global graphical artifacts must be drawn again.
    * <br>
    * <br>
    * Imagine a line fx artifact that draws a figure at the start of a line and another figure at the end of
    * a line. It is not a global artifact, but a position sensivite one which means when the line
    * width changes, those artifacts must be drawn again.
    * <br>
    * <br>
    * Uses a shift to get the correct x,y
    * @param g
    * @param x root x coordinate on {@link GraphicsX} may change from init value for the purpose of mask/scaling
    * @param y root y coordinate
    * @param wOffset start character index. Scrolling related. Default:0 
    * @param wNum number of characters to draw. Default: len of characters. A bigger value will scale back to the maximum possible.
    * @param hOffset start line offset. Default:0
    * @param hNum number of lines. Default:1
    * <br>
    * <br>
    * 
    */
   public void drawOffsets(GraphicsX g, int x, int y, int wOffset, int wNum, int hOffset, int hNum) {
      //System.out.println("#Stringer#draw at " + x + "," + y);
      StringDraw stringDraw = getDraw();
      if (x != areaX || y != areaY) {
         stringDraw = new StringDraw(drc, this);
         stringDraw.init(x, y);
      }
      int[] breaks = metrics.breaks;
      if (breaks == null) {
         //draw everything on a single line
         int offset = this.offsetChars + wOffset;
         int len = Math.min(wNum, this.lengthChars);

         stringDraw.drawLine(g, offset, len, 0);

      } else {
         int numLines = metrics.getNumOfLines();
         int end = Math.min(numLines, hNum);
         int firstLineIndex = hOffset;
         //
         if (wOffset != 0 || hOffset != 0) {
            stringDraw = new StringDraw(drc, this);
            int dx = x;
            int dy = y;
            if (wOffset > 0) {
               dx -= metrics.getCharX(wOffset);
            }
            if (hOffset > 0) {
               dy -= getLineY(firstLineIndex);
            }
            stringDraw.init(dx, dy);
         }
         for (int i = 0; i < end; i++) {
            int lineIndex = hOffset + i;
            int index = BREAK_HEADER_SIZE + (i + hOffset) * BREAK_WINDOW_SIZE;
            int startOffset = breaks[index];
            int charNum = breaks[index + 1];
            //wOffset decides what to draw
            int len = Math.min(wNum, charNum);
            stringDraw.drawLine(g, startOffset + wOffset, len, lineIndex);
         }
      }
   }

   public void drawScaled(GraphicsX g) {
      int bgColor = 0;
      if (g.isVirgin(areaX, areaY, areaW, areaH)) {
         bgColor = g.getBgColor();

      } else {
         //we must use a color not used
         bgColor = 0;

         //or we can use the background color of hosting figure in StringDrawable.
      }
      int scaledImageW = metrics.getPrefWidth() + 1;
      int scaledImageH = metrics.getPrefHeight() + 1;
      //draw on image and scale to fit area. metrics
      //TODO check if figure has defined a bgcolor else use transparent
      RgbImage baseImage = drc.getCache().create(scaledImageW, scaledImageH, bgColor);
      GraphicsX gi = baseImage.getGraphicsX();
      int scalePosX = 1; //glitch needed for pretty scaling.
      int scalePoxY = 0;
      drawOffsets(gi, scalePosX, scalePoxY, 0, lengthChars, 0, metrics.getNumOfLines());

      //removes background pixels if the x,y area is not virgin of background color

      //TODO fix the double blending
      RgbImage scaledImage = drc.getRgbImageOperator().scaleRgbImage(baseImage, areaW, areaH, fx.scale);

      g.drawRgbImage(scaledImage, areaX, areaY);
      baseImage.dispose();
      scaledImage.dispose();
   }

   public char getCharAtRelative(int indexRel) {
      return chars[offsetChars + indexRel];
   }

   /**
    * Get the char fx object for that char index. Look up the intervals of styling.
    * <br>
    * <br>
    * @param index
    */
   public StringFx getCharFx(int index) {
      if (charFxs != null && charFxs[index] != null) {
         return charFxs[index];
      } else if (intervals == null) {
         //check for static
         return fx;
      } else {
         //if only dynamic style look up interval. not very efficient
         for (int i = 0; i < intervals.length; i++) {
            IntBuffer ib = intervals[i];
            int size = ib.getSize();
            for (int j = 0; j < size; j += 2) {
               int off = ib.get(j);
               int len = ib.get(j + 1);
               if (index >= off && index < off + len) {
                  return fxsDynamic[i];
               }
            }
         }
      }
      return fx;
   }

   /**
    * Returns a copy of the displayed string.
    * @return
    */
   public String getDisplayedString() {
      return new String(chars, offsetChars, lengthChars);
   }

   public StringDraw getDraw() {
      return stringDraw;
   }

   public int getDrawType() {
      return drawLineType;
   }

   /**
    * Returns an {@link IntBuffer} containing the intervals using the given text effect id.
    * <br>
    * <br>
    * 
    * @param style
    * @return
    */
   public IntBuffer getIntervals(int style) {
      if (intervals != null) {
         return intervals[style];
      } else {
         return null;
      }
   }

   public int getLen() {
      return lengthChars;
   }

   /**
    * Returns the root {@link StringFx} for the given line.
    * <br>
    * <br>
    * Returns the default.
    * 
    * @param i
    * @return
    */
   public StringFx getLineFx(int i) {

      return fx;
   }

   public int getLineY(int i) {
      return metrics.lineYs[i];
   }

   public StringMetrics getMetrics() {
      return metrics;
   }

   private int getNumDynamic(ByteObject textFigure) {
      ByteObject[] subs = textFigure.getSubs(TYPE_070_TEXT_EFFECTS);
      int count = 0;
      if (subs != null) {
         for (int i = 0; i < subs.length; i++) {
            if (subs[i] != null && subs[i].hasFlag(IFxStr.FX_OFFSET_02_FLAGX, IFxStr.FX_FLAGX_2_DYNAMIC)) {
               count++;
            }
         }
      }
      return count;
   }

   public int getNumOfLines() {
      return metrics.getNumOfLines();
   }

   private int getNumStaticIndex(ByteObject[] subs) {
      int count = 0;
      for (int i = 0; i < subs.length; i++) {
         if (subs[i] != null && !subs[i].hasFlag(IFxStr.FX_OFFSET_02_FLAGX, IFxStr.FX_FLAGX_2_DYNAMIC)) {
            if (subs[i].hasFlag(IFxStr.FX_OFFSET_02_FLAGX, IFxStr.FX_FLAGX_6_DEFINED_INDEX)) {
               count++;
            }
         }
      }
      return count;
   }

   public StringFx getStringFx() {
      return fx;
   }

   public int[] getWordBreaks(int lineIndex) {
      if (metrics.lineWordBreaks != null) {
         return metrics.lineWordBreaks[lineIndex];
      }
      return null;
   }

   public boolean hasState(int state) {
      return BitUtils.hasFlag(states, state);
   }

   /**
    * Init String figure for a draw by computing style character widths and heights.
    * <br>
    * <br>
    * What happens when several char scoped fx are defined in the string figure?
    * <br>
    * The root is found if any. 
    * Then static indexed fxs are stored in static.
    * <br>
    * Whenever two {@link StringFx} for the same target are found. They are merged. Second over the first.
    * <br>
    * <br>
    *  
    * 
    * @param textFigure when null? use Default?
    * @param x
    * @param y
    * @param w
    * @param h
    * @param chars reference to the original char array
    * @param offset
    * @param len
    */
   public void initFig(ByteObject textFigure, int x, int y, int w, int h, char[] chars, int offset, int len) {
      textFigure.checkType(TYPE_050_FIGURE);
      this.charsBackup = null;
      if (chars == null) {
         throw new NullPointerException();
      }
      this.chars = chars;
      this.offsetChars = offset;
      this.lengthChars = len;
      this.text = textFigure;
      if (text.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_1ANCHOR)) {
         anchor = text.getSubFirst(TYPE_051_BOX);
      }
      if (text.hasFlag(FIG__OFFSET_02_FLAG, FIG_FLAG_4MASK)) {
         //figure has a mask, move it to text mask fx.
      }

      states = 0;
      areaX = x;
      areaY = y;
      areaW = w;
      areaH = h;
      //no effects. we extract figure information from text to create the StringFx object.
      fx.init(textFigure);
      stringDraw.init(areaX, areaY);
      metrics.init();
      //all txt effect are stored sequencially
      ByteObject[] subs = textFigure.getSubs(TYPE_070_TEXT_EFFECTS);
      if (subs != null && subs.length != 0) {
         initTextEffects(textFigure, subs);
      }
   }

   public void initFig(ByteObject textFigure, int x, int y, int w, int h, String str) {
      this.initFig(textFigure, x, y, w, h, str.toCharArray(), 0, str.length());
   }

   private void initMetrics() {
      metrics.init();
   }

   /**
    * Reads the different {@link IFxStr} definitions and sort them.
    * @param textFigure
    * @param subs not null by contract size above 0 and elements inside are not null
    */
   public void initTextEffects(ByteObject textFigure, ByteObject[] subs) {
      setState(STATE_08_ACTIVE_STYLE, true);
      int dsize = getNumDynamic(textFigure);
      if (dsize > 0) {
         fxsDynamic = new StringFx[dsize + 1];
         fxsDynamic[0] = fx;
         fxsDynamicDefinition = new ByteObject[dsize + 1];
         intervals = new IntBuffer[1 + dsize];
      }
      int numIndexStatic = getNumStaticIndex(subs);
      fxsStatic = new StringFx[subs.length - dsize - numIndexStatic];
      fxsStaticIndex = new StringFx[numIndexStatic];
      int countDynamic = 1;
      int countStatic = 0;
      int countStaticIndex = 0;
      for (int i = 0; i < subs.length; i++) {
         if (subs[i] != null) {
            StringFx fxi = new StringFx(drc, this);
            fxi.init(subs[i]);
            if (subs[i].hasFlag(IFxStr.FX_OFFSET_02_FLAGX, IFxStr.FX_FLAGX_2_DYNAMIC)) {
               fxsDynamic[countDynamic] = fxi;
               fxsDynamicDefinition[countDynamic] = subs[i];
               intervals[countDynamic] = new IntBuffer(drc.getUCtx(), 4);
               countDynamic++;
            } else {
               if (subs[i].hasFlag(IFxStr.FX_OFFSET_02_FLAGX, IFxStr.FX_FLAGX_6_DEFINED_INDEX)) {
                  fxsStaticIndex[countStaticIndex] = fxi;
                  countStaticIndex++;
               } else {
                  fxsStatic[countStatic] = fxi;
                  countStatic++;
               }
            }
         }
      }
      //create the static final style that by default applies to all chars
      for (int i = 0; i < fxsStatic.length; i++) {
         fx = fx.add(fxsStatic[i]);
      }
      //create the index
      for (int i = 0; i < fxsStaticIndex.length; i++) {
         int scope = fxsStaticIndex[i].fxDefinition.get1(IFxStr.FX_OFFSET_03_SCOPE1);
         int index = fxsStaticIndex[i].fxDefinition.get2(IFxStr.FX_OFFSET_04_INDEX2);

         if (scope == IFxStr.FX_SCOPE_0_CHAR) {
            if (charFxs == null) {
               charFxs = new StringFx[lengthChars];
            }
            //check index pattern? what if part of
            charFxs[index] = fx.cloneMerge(fxsStaticIndex[i]);

         } else if (scope == IFxStr.FX_SCOPE_1_WORD) {

         } else if (scope == IFxStr.FX_SCOPE_2_LINE) {
            if (lineFxs == null) {
               lineFxs = new StringFx[getNumOfLines()];
            }

         }
      }
      if (countStaticIndex != 0) {
         setState(STATE_16_STATIC_INDEX_FX, true);
      }
      if (countDynamic == 0) {
         setState(STATE_10_ACTIVE_DYNAMIC_STYLE, false);
      }
      if (countStatic != 0) {
         setState(STATE_13_FX, true);
      }
      //from the definitions
      updateActiveFXs();
   }

   public void meterString() {
      this.meterString(IStr.BREAK_0_NONE, 0);
   }

   /**
    * Break the String using areaW and areaH.
    * <br>
    * <br>
    * 
    * @param breakType {@link IStr#BREAK_4_TRIM_SINGLE_LINE} etc.
    * @param maxLines
    */
   public void meterString(int breakType, int maxLines) {
      this.meterString(areaW, areaH, breakType, maxLines);
   }

   /**
    * Meters the string size and break it to fit the break area and break type.
    * <br>
    * <br>
    * @param breakWidth
    * @param breakHeight
    * @param breakType {@link IStr#BREAK_4_TRIM_SINGLE_LINE} etc.
    * @param maxLines
    */
   public void meterString(int breakWidth, int breakHeight, int breakType, int maxLines) {
      getMetrics().breakStringEntry(breakType, maxLines, breakWidth, breakHeight);
      //decide type
      boolean isFX = hasState(STATE_08_ACTIVE_STYLE);
      if (metrics.breaks != null) {
         if (isFX) {
            drawLineType = TYPE_3_BREAKS_FX;
         } else {
            drawLineType = TYPE_2_BREAKS;
         }
      } else {
         if (isFX) {
            drawLineType = TYPE_1_SINGLE_LINE_FX;
         } else {
            drawLineType = TYPE_0_SINGLE_LINE;
         }
      }
   }

   /**
    * Removes all intervals using the given {@link StringFx} id.
    * <br>
    * <br>
    * Technically, this reduces the number of active {@link StringFx}.
    * <br>
    * <br>
    * 
    * @param style
    */
   public void removeInterval(int style) {
      if (intervals != null) {
         intervals[style].clear();
         boolean desactiveDynamic = true;
         for (int i = 0; i < intervals.length; i++) {
            if (intervals[i].getSize() != 0) {
               desactiveDynamic = false;
            }
         }
         setState(STATE_10_ACTIVE_DYNAMIC_STYLE, !desactiveDynamic);
         updateDynamicFXs();
      }
   }

   /**
    * Externally set anchor?
    * <br>
    * <br>
    * Well yes, {@link StringDrawable} style decides anchoring independantly of the String figure.
    * <br>
    * <br>
    * 
    * @param anchor
    */
   public void setAnchor(ByteObject anchor) {
      this.anchor = anchor;
   }

   /**
    * 
    * @param i
    * @param c
    */
   public void setCharAtRelative(int indexRel, char c) {
      chars[offsetChars + indexRel] = c;
   }

   /**
    * Sets the single style interval for that txt effect id removing previous interval(s).
    * <br>
    * <br>
    * 
    * @param offset
    * @param len
    */
   public void setInterval(int offset, int len, int style) {
      if (intervals != null) {
         if (style >= 0 && style < intervals.length) {
            this.intervals[style].clear();
            this.intervals[style].addInt(offset);
            this.intervals[style].addInt(len);
            //modifies Stringer state
            setState(STATE_10_ACTIVE_DYNAMIC_STYLE, true);
            //update fx state status. dynamic style may change
            updateDynamicFXs();
         }
      }
   }

   /**
    * 
    * @param style
    * @param intervals
    */
   public void setIntervals(int style, IntBuffer intervals) {
      if (intervals != null) {
         this.intervals[style] = intervals;
      }
      //update metrics if the style modifies the font
   }

   void setState(int state, boolean v) {
      states = BitUtils.setFlag(states, state, v);
   }

   //#mdebug

   public void toString(Dctx sb) {
      sb.root(this, "Stringer");
      sb.appendWithSpace("(" + offsetChars + "," + lengthChars + ") ");
      super.toString(sb.sup());

      if (chars == null) {
         chars = "STRINGER NULL CHAR[]".toCharArray();
         offsetChars = 0;
         lengthChars = chars.length;
      }
      sb.append(new String(chars, offsetChars, lengthChars));
      sb.nl();
      sb.append(" Area = " + areaX + "," + areaY + " " + areaW + "," + areaH);
      sb.append(" type=" + drawLineType);
      if (states != 0) {
         sb.append(" States=");
         if (hasState(STATE_01_CHAR_EFFECTS)) {
            sb.append("CharEffects");
         }
         if (hasState(STATE_02_CHAR_WIDTHS)) {
            sb.append("CharWidths");
         }
         if (hasState(STATE_04_TRIMMED)) {
            sb.append("Trimmed");
         }
      }
      sb.nlLvlArray("DynamicFXs", fxsDynamic);
      sb.nlLvlArray("StaticFxs", fxsStatic);
      sb.nlLvlArray("Intervals", intervals);

      sb.nlLvl(anchor);

      sb.nlLvl(metrics);
      sb.nlLvl(stringDraw);
      sb.nlLvl(fx);

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "Stringer");
   }
   //#enddebug

   /**
    * Trim the {@link Stringer}, that is reduces the len field to number of characters.
    * <br>
    * Replaces some characters with Trim cue '.'
    * <li> P..
    * <li> ..
    * <li> .
    * <br>
    * <br>
    * <br>
    * One or two character words are not trimmed.
    * <br>
    * <br>
    * You cannot trim a logical init of -1 -2 -x.
    * <br> 
    * @param lastIndex the last absolute index at which the string should end. cannot be smaller than Stringer offset.
    */
   public void trim(int lastIndex) {
      if (lastIndex < offsetChars) {
         throw new IllegalArgumentException("" + lastIndex);
      }
      if (lengthChars <= 2) {
         return;
      }
      setState(Stringer.STATE_04_TRIMMED, true);
      charsBackup = chars;
      int numChars = lastIndex - offsetChars;
      chars = new char[numChars];
      if (numChars <= 0) {
         lengthChars = 0;
      } else if (numChars <= 1) {
         lengthChars = 1;
         chars[offsetChars] = '.';
      } else {
         int fin = 0;
         lengthChars = numChars;
         fin = lastIndex - 2;
         for (int i = 0; i < fin; i++) {
            chars[i] = charsBackup[i];
         }
         chars[fin] = '.';
         chars[fin + 1] = '.';
      }
   }

   /**
    * TODO relation with state style update modifying structure? Is it acceptable?
    * <br>
    * Sometimes we want the update to change the structure.
    * For simple state change to BOLD, we do not want this.
    * <br>
    * Somehow {@link Stringer} must find out if different style change computed metrics.
    * <li>Check Font
    * <li>Check Fx
    * <br>
    * <br>
    * @param text
    * @param x
    * @param y
    * @param w
    * @param h
    */
   public void update(ByteObject text, int x, int y, int w, int h) {
      if (this.text != text) {
         this.text = text;
         fx.init(text);
      }
   }

   /**
    * From the definitions, create the {@link Stringer#charFxs} if necessary.
    * <br>
    * <br>
    * 
    */
   private void updateActiveFXs() {
      boolean isGranular = false;
      for (int i = 0; i < fxsStatic.length; i++) {
         StringFx fx = fxsStatic[i];
         if (fx.fxDefinition.hasFlag(IFxStr.FX_OFFSET_02_FLAGX, IFxStr.FX_FLAGX_6_DEFINED_INDEX)) {
            isGranular = true;
         }
      }
      if (isGranular) {
         charFxs = new StringFx[lengthChars];
      }
   }

   public void updateArea(int contentW, int contentH) {
      areaW = contentW;
      areaH = contentH;
   }

   public void updateCharAt(int i, char c) {
      metrics.updateCharAt(i, c);
   }

   private void updateDynamicFXs() {
      for (int i = 1; i < intervals.length; i++) {
         IntBuffer ib = intervals[i];
         ByteObject dynDef = fxsDynamicDefinition[i];
         int size = ib.getSize();
         //iterate over the intervals for the dynamic fx
         for (int j = 0; j < size; j += 2) {
            int off = ib.get(j);
            int len = ib.get(j + 1);
            //
            if (charFxs != null) {
               //merge them individually
               for (int k = off; k < off + len; k++) {
                  StringFx sf = charFxs[k];
                  //when null goes to default FX.
                  if (sf == null) {
                     sf = fx;
                  }
                  if (!sf.hasDynamicID(i)) {
                     //add it
                     charFxs[k] = sf.cloneMergeTop(dynDef, i);
                  }
               }
            } else {
               if (fxsDynamic[i] == null) {
                  //merge default 
                  StringFx sf = fx.cloneMergeTop(fxsDynamicDefinition[i], i);
                  fxsDynamic[i] = sf;
               }
            }
         }
      }
   }

   public void updatePosition(int contentX, int contentY) {
      areaX = contentX;
      areaY = contentY;
      stringDraw.init(areaX, areaY);
   }
}
