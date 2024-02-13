/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import java.util.Enumeration;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.io.XString;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.core.src4.structs.IntInterval;
import pasa.cbentley.core.src4.structs.IntIntervals;
import pasa.cbentley.core.src4.structs.IntToStrings;
import pasa.cbentley.core.src4.text.StringInterval;
import pasa.cbentley.core.src4.text.TextModel;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.coredraw.src4.ctx.IFlagToStringCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStr;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStrChar;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringDrw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;
import pasa.cbentley.layouter.src4.interfaces.ILayoutable;

/**
 * Tracks the metering and drawing of a String of characters.
 * 
 * <p>
 * 
 * Provides access to character positions with the class {@link StringMetrics}. This allow the editing module
 * to position the caret at any characters.
 * </p>
 * <p>
 * Usage : Initialize the Stringer
 * <li> {@link Stringer#setStringFig(ByteObject, String)}
 * <li> {@link Stringer#initTextEffects(ByteObject, ByteObject[])}
 * 
 * <li> {@link Stringer#buildAgain()} 
 * </p>
 * <p>
 * Optimization. Buffer for String Editing.
 * <br>
 * </p>
 * 
 * <p>
 * <b>String selection</b><br>
 * String selection is implemented with an interval which is rendered using an additional Text Fx.
 * and setting interval {@link Stringer#setInterval(int, int)}
 * </p>
 * <br>
 * Selection style will override some of the underlying style, like the foreground color and background color.
 * Like in Eclipse, it may decide to key the font and the face.
 * <br>
 * 
 * <p>
 * 
 * How  do you defined 10 different style of fx assigned by order or randomly to each character of a word?
 * The root fx defines 10 sub object of type {@link IDrwTypes#TYPE_050_FIGURE}
 * </p>
 * <br>
 * <p>
 * <b>Layers</b> : {@link Stringer#getStyleLayer(int)}
 * <li> 0 based
 * <li> identifies overlaping styles. 0 for base, 1 for selection
 * </p>
 * 
 * 
 * @see StringMetrics
 * @see StringDraw
 * @see StringFx
 * @see StringerEditor
 * @see IBOFxStr
 * 
 * @author Charles-Philip Bentley
 *
 */
public class Stringer extends ObjectDrw implements IStringable, ITechFigure, IBOTypesDrw, IBOFxStr, ITechStringer {

   public static final String        NAME_ROOT_STYLE_LAYER = "Root";

   /**
    * 
    * {@link ITechAnchor}
    * 
    * Anchors the String figure in the box defined by the Stringer area.
    * <br>
    * However String have a locale associated with it.
    * Therefore the String is a 
    * <li> Bidi (bi directional) texts also are a challenge
    * <li>dextrosinistral text
    * <li>sinistrodextral text from the left to the right
    */
   ByteObject                        anchor;

   /**
    * Used for alignment
    */
   int                               areaH;

   /**
    * Used for alignment
    */
   int                               areaW;

   /**
    * the x coordinate at which the draw the String.
    * <br>
    * <br>
    * When drawing shapes on a {@link RgbImage} for mask, this value is not used
    */
   int                               areaX;

   int                               areaY;

   int                               breakH;

   private int                       breakMaxLines;

   /**
    * 
    */
   private int                       breakType;

   int                               breakW;

   StringBBuilder                    buffer;

   /**
    * 
    * The char array used to compute the displayed String on screen.
    * 
    * <p>
    * When reference equals to charsSource, it has not been modified for format purposes.
    * Indeed a new array is created when the source data is trimmed. 
    * </p>
    * 
    * 
    * What happens when the chars include the .. of a trim cue?
    * 
    * " " spaces when text is justified
    * 
    * "\" character for image/figure area
    * 
    * " " space for holes generated by figures
    * 
    * When markup/markdown text is read, it must be interpreted, generating stytle intervals.
    * <br>
    * 
    * "Markdown is a text-to-HTML conversion tool for web writers"
    * 
    * Stores original string before trimming and imageing
    * 
    * This array is never modified or written to.
    * <p>
    * A figure/image is considered like a single character at its anchor
    * </p>
    * <li>\img{$1} the second image in the table, height is 1 line, width is ratio sized
    * <li>\img{$1} when no width.. it takes the size of one _ when no height, 1 line
    * <li>\img{tree.png} 
    * <li>\fig{$0} 0 index figure , color is font color
    * 
    * <p>
    * replace image area with special character. Paint
    * </p>
    */
   char[]                            chars;

   /**
    * Active interval
    */
   private StringInterval            currentInterval;

   int                               drawWordType;

   private StringerEditor            editor;

   /**
    * Payloads are {@link StringFxLeaf} objects
    */
   private IntIntervals              intervalOfStringLeaves;

   /**
    * Computed when {@link Stringer} needs to apply fx scoped to words
    */
   private IntIntervals              intervalOfWords;

   private boolean isShowHiddenChars = false;

   private boolean                   isTrimArtifacts;

   private int                       lastNumDrawnChars     = 0;

   private StringFxLeaf[]            leaves                = new StringFxLeaf[4];

   /**
    * the length of characters starting offsetChars.
    */
   int                               lengthChars;

   private int                       newLineManager;

   private int                       numLinesPerPage;

   /**
    * Absolute offset for reading characters in the character array.
    */
   int                               offsetChars;

   private ByteObject                scale;

   private StringStyleApplicatorWord searchApp;

   private int                       spaceTrimManager;

   /**
    * Provides the {@link Stringer} with easily accessible state information.
    * <br>
    * <br>
    * <li> {@link ITechStringer#STATE_04_TRIMMED}
    */
   private int                       states;

   /**
    * The {@link StringDraw} based on areaX and areaY.
    */
   StringDraw                        stringDraw;

   /**
    * root fx. Null when fx has not been built
    * <br>
    * Then it is merged with generic fx fields . char mask.
    * <br>
    * i.e a line fx that applies to all lines is stored here.
    * <br>
    * 
    * The {@link StringFx} define alternate {@link StringFx} which are used based on index or an interval
    * <br>
    * <br>
    * Not null after call of {@link Stringer#buildTextEffects()}
    */
   StringFx                          stringFx;

   /**
    * Never null. Created in Constructor. Support object for metrics.
    */
   StringMetrics                     stringMetrics;

   StringStyleApplicator[]           styleApplicators;

   /**
    * 
    */
   int[]                             styleFlags;

   /**
    * Array with {@link StringStyleLayer} and their {@link IntIntervals} of style definitions
    * 
    * <p>
    * Null by default. Getter creates it as required. Therefore should always be accessed with
    * {@link Stringer#getStyleLayers()}
    * </p>
    * 
    * The {@link IntInterval} carry their specific {@link ByteObject} fx definitions, if its different from
    * the root one defined in {@link Stringer}.
    * 
    * <p>
    * One creates a {@link StringStyleLayer} with {@link Stringer#createLayer(String, int)} for building complex
    * documents or a selection layer style. Usually, the root layer will be used to style words and a second layer
    * controls the selection style.
    * </p>
    * @see Stringer#getStyleLayers()
    * @see Stringer#buildTextEffects()
    * 
    */
   private StringStyleLayer[]        styleLayers;

   /**
    * Layers generated by {@link ByteObject} fx definitions.
    * Applied BEFORE any dynamic style. They belong to the root style.
    * For example, first letter of each word kind of style.
    * 
    * <li>Style for punctuation. {@link IBOFxStrChar#FXCHAR_FLAG_1_CHARS_BASED}
    * <li>Style for some characters.. {@link IBOFxStrChar#FXCHAR_FLAG_1_CHARS_BASED}
    */
   StringStyleLayer[]                styleLayersBO;

   private int                       tabManager;

   /**
    * Text figure of type  {@link IBOFigString}.
    */
   ByteObject                        text;

   /**
    * Initalized when {@link Stringer} requires word, punc, sentence based Fx styling.
    */
   private TextModel                 textModel;

   /**
    * Draw a debug rectangle around the area
    */
   private boolean                   toStringDebugArea;

   private boolean                   toStringDebugBreakLines;

   private int                       wordwrap;

   /**
    * 
    * @param drc
    */
   public Stringer(DrwCtx drc) {
      super(drc);
      stringMetrics = new StringMetrics(drc, this);
      stringDraw = new StringDraw(drc, this);
   }

   /**
    * Adds the fx over the whole text 
    * or
    * Reset 
    * TODO only one fx over the whole text.
    * what we ant to add if index based byteobject.. that transforms
    * when conflict? order first
    * if not, it will merge with stack of fx 
    * 
    * dynamic effect are set using a {@link StringStyleLayer}
    * @param fx
    */
   public void addFx(ByteObject fx) {
      //#debug
      fx.checkType(IBOTypesDrw.TYPE_070_TEXT_EFFECTS);

   }

   /**
    * layerID is 0, i.e. the root default one.
    * 
    * @param offset
    * @param len
    * @param fx
    * @return
    */
   public IntInterval addInterval(int offset, int len, ByteObject fx) {
      return addInterval(offset, len, 0, fx);
   }

   /**
    * Overwrites the interval in the chosen layer.
    * 
    * This is a external dynamic style.
    * <br>
    * Internal dynamic is defined at the level of the text figure
    * Adding on the same layer, will generate an overwrite of payload.
    * 
    * <p>
    * If you want a fx merge, you need to create a new layer on top
    * </p>
    * 
    * Resets the Fx config
    * @param offset
    * @param len
    * @param layerID a valid ID. must be created first. 0 id is always valid
    * @param styleID
    * @return
    * @throws ArrayIndexOutOfBoundsException if layerID is not valid
    * @see Stringer#getStyleLayer(int)
    */
   public IntInterval addInterval(int offset, int len, int layerID, ByteObject fx) {
      resetFxDefinition();
      StringStyleLayer layer = getStyleLayer(layerID);
      IntInterval ii = layer.addInterval(offset, len, fx);
      return ii;
   }

   /**
    * You can only add a few layers over the default one..
    * @return its ID.. 
    */
   public int addLayer(String name) {
      StringStyleLayer[] styleLayers = getStyleLayers();
      int nextID = styleLayers.length;
      createLayer(name, nextID);
      return nextID;
   }

   /**
    * Dev commands to add a newline at current caret
    */
   public void appendNewLine() {
      areaY += stringMetrics.getLineHeight();
      stringDraw.initTrackerXY(0, areaY);
   }

   public void buildAgain() {
      setTextFigure(text);
      buildTextEffects();
      stringMetrics.meterString(); //FX must be deployed
   }

   /**
    * Keep existing string
    * @param textFigure
    */
   public void buildForDisplayWith(ByteObject textFigure) {
      setTextFigure(textFigure);
      buildTextEffects();
      stringMetrics.meterString(); //FX must be deployed
   }

   public void buildForDisplayWith(ByteObject textFigure, String str) {
      setStringFig(textFigure, str);
      buildTextEffects();
      stringMetrics.meterString(); //FX must be deployed
   }

   /**
    * Build from scratch
    * 
    * When adding characters, we may take existing {@link IntIntervals} and modify it
    * @param intIntervals
    */
   public StringInterval[] buildIntervalWords() {
      if (textModel == null) {
         textModel = new TextModel(drc.getUCtx());
         textModel.setChars(chars, offsetChars, lengthChars);
         textModel.buildModel();
      }
      return textModel.getIntervalsWords();
   }

   public IntIntervals buildIntervalWordsAsI() {
      StringInterval[] sis = this.buildIntervalWords();
      IntIntervals in = new IntIntervals(getUC(), sis.length);
      for (int i = 0; i < sis.length; i++) {
         in.addInterval(sis[i].getInterval());
      }
      return in;
   }

   /**
    * Building is done before string breaking..
    * Why ?
    * 
    * But after Fx setup with {@link Stringer#initTextEffects(ByteObject, ByteObject[])}
    * We need to know if a {@link TextModel} is required.
    * 
    * 
    * <p>
    * 
    * TODO When big blobs of text, we can't put everything into memory.
    * So wrapper around Stringer only feeds portion of the text
    * 
    * There is a maxchar limit that the Stringer can used.. To avoid crashes if the user feeds
    * a big blob of text.
    * 
    * Config this ?
    * 
    * 
    * </p>
    * 
    * When style is changed for some leaves, the cached computed widths are cleared.
    * 
    * <p>
    * TODO monospace optimization
    * {@link ITechStringer#STATE_18_FULL_MONOSPACE}. This flag was computed in the FxSetup
    * </p>
    * 
    */
   private void buildStringFxLeaves() {

      FxCache fxCache = drc.getFxCache();

      //todo optimized drawing by drawing all leaves with same FX in one pass
      //that will depends on the fx.. complex fx might depends on letter flow

      //string leaf is initialized with base StringFx
      //and encompasses the whole area.
      //we use start end buffered structure for building leaves
      StringFxLeaf leafRoot = new StringFxLeaf(drc, this, stringFx);
      //TODO if boFxRoot declares index based styling? we need to create intervals for it before.
      setState(STATE_11_DIFFERENT_FONTS, false);
      if (stringFx.getFont().isMonospace()) {
         setState(STATE_18_FULL_MONOSPACE, true);
      }
      IntIntervals intervalsOfLeaves = new IntIntervals(drc.getUCtx());
      intervalsOfLeaves.setPayLoadCheck(true); //we want to break down intervals with different payloads

      IntInterval rootInterval = intervalsOfLeaves.addInterval(0, lengthChars, leafRoot);

      //scope to scope.. a charFx will inherit the lineFx
      //how do you deal with lineFx mixing with charFx/wordFx
      //rootfx says lines have a color function.. for bg and fg.
      //what happens when charFx specific in middle of a line?

      //apply first interval of first enumeration
      //each layer splits the leaves IntIntervals further and further, 
      //each time merging the ByteObject style with the IntInterval payload fx.
      StringStyleLayer[] styleLayers = this.getStyleLayers();
      for (int i = 0; i < styleLayers.length; i++) {
         StringStyleLayer layer = styleLayers[i];
         if (layer == null) {
            continue;
         }
         Enumeration layerIntervals = layer.getIntervals();
         while (layerIntervals.hasMoreElements()) {
            IntInterval intervalFromStyleLayer = (IntInterval) layerIntervals.nextElement();
            //leafRoot.setOffset(intervalWithStyle.getOffset());
            //leafRoot.setLen(intervalWithStyle.getLen());

            //look on the existing leaves if there is intersection with this one below this interval and create leaves
            IntInterval[] intersect = intervalsOfLeaves.getIntersection(intervalFromStyleLayer);

            //payload of interval from style layer is the ByteObject txt effect definition
            Object payload = intervalFromStyleLayer.getPayload();
            if (payload == null) {
               //warning this should not occur
               throw new NullPointerException();
               //continue;
            }
            ByteObject boFxTop = null;
            if (payload instanceof ByteObject) {
               boFxTop = (ByteObject) payload;
            } else {
               throw new IllegalStateException();
            }

            if (intersect.length == 0) {
               throw new IllegalStateException("Interval Added with out of bounds values " + intervalFromStyleLayer);
            } else {
               //if no leaves under it.. use root leaf for constructing the style
               for (int j = 0; j < intersect.length; j++) {

                  IntInterval intersectBelow = intersect[j];

                  StringFxLeaf leafBelow = (StringFxLeaf) intersectBelow.getPayload();
                  ByteObject boFxBelow = leafBelow.getFx().getSrcFx();
                  //optimize to reuse existing merges
                  ByteObject boFxMerged = fxCache.getFxCacheMerge(boFxBelow, boFxTop);

                  IntInterval intervalFromInter = intervalFromStyleLayer.getIntersectionWith(intersectBelow);

                  StringFx fxMerged = new StringFx(drc, this, boFxMerged);
                  if (fxMerged.getFont() != stringFx.getFont()) {
                     setState(STATE_11_DIFFERENT_FONTS, true);
                     setState(STATE_18_FULL_MONOSPACE, false);
                  }

                  StringFxLeaf leafNew = new StringFxLeaf(drc, this, fxMerged);

                  //add the interval and its leaf to the set of leaves representing the styled chunks of text
                  intervalFromInter.setPayload(leafNew);
                  intervalsOfLeaves.addInterval(intervalFromInter);
               }
            }
         }
      }

      Enumeration leafIntervals = intervalsOfLeaves.getIntervalEnumeration();
      while (leafIntervals.hasMoreElements()) {
         //construct the fx
         IntInterval ii = (IntInterval) leafIntervals.nextElement();
         StringFxLeaf boFxTop = (StringFxLeaf) ii.getPayload();

         //compute stringfx and charwidth?
      }

      this.intervalOfStringLeaves = intervalsOfLeaves;

   }

   public void buildTextEffects() {
      this.buildTextEffects(text);
   }

   public void buildTextEffects(ByteObject textFigure) {
      int num = textFigure.getSubNum(TYPE_070_TEXT_EFFECTS);
      ByteObject fxRoot = drc.getFxStringFactory().createFxFromFigure(textFigure);

      if (num == 0 && styleLayers == null) {
         //TODO we don't need all the Fx plumbing. just draw font and color that's it.
         //well we might have stylelayer styles
         //we just build the root fx
         stringFx = new StringFx(drc, this, fxRoot);

         setState(STATE_11_DIFFERENT_FONTS, false);
         boolean isMono = stringFx.getFont().isMonospace();
         setState(STATE_18_FULL_MONOSPACE, isMono);

         StringFxLeaf leafRoot = new StringFxLeaf(drc, this, stringFx);
         intervalOfStringLeaves = new IntIntervals(getUC(), 1);
         intervalOfStringLeaves.setPayLoadCheck(true); //we want to break down intervals with different payloads
         if (lengthChars == 0) {

         } else {
            intervalOfStringLeaves.addInterval(0, lengthChars, leafRoot);
         }

      } else {

         //create a Fx object from textFigure parameters
         ByteObject[] fxs = new ByteObject[num + 1];
         fxs[0] = fxRoot;
         textFigure.getSubsAppend(TYPE_070_TEXT_EFFECTS, fxs, 1);

         //merges everything and use that
         ByteObject fxSrc = fxRoot;
         for (int i = 0; i < fxs.length; i++) {
            fxSrc = drc.getFxStringOperator().mergeTxtEffects(fxSrc, fxs[i]);
         }

         stringFx = new StringFx(drc, this, fxSrc);

         if (hasState(STATE_29_MODEL_WORD_FX)) {
            buildIntervalWords();
         }
         buildStringFxLeaves();
      }
      setState(STATE_17_COMPUTED_FX, true);
      setState(STATE_19_FX_SETUP, true);
   }

   private void checkStateRun() {
      if (text == null) {
         throw new IllegalStateException("No Text Figure Set");
      }
      if (!hasState(STATE_19_FX_SETUP)) {
         throw new IllegalStateException("FX has not been initialized");
      }
   }

   public StringStyleLayer createLayer(String string) {
      return createLayer(string, getStyleLayers().length);
   }

   /**
    * Create layer and add it to the collection using ID as index
    * @param string
    * @param id
    * @return
    */
   public StringStyleLayer createLayer(String string, int id) {
      StringStyleLayer sls = new StringStyleLayer(drc, this, id);
      sls.setName(string);
      StringStyleLayer[] styleLayersRoot = getStyleLayers();
      styleLayers = ensureCapacity(styleLayersRoot, id);
      styleLayers[id] = sls;
      return sls;
   }

   /**
    * 
    * @param cs
    * @param indexRelative
    */
   public synchronized void deleteCharAt(char[] cs, int indexRelative) {
      chars = cs;
      //metrics uses the previous len value
      stringMetrics.deleteCharAt(indexRelative);
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
      //#mdebug
      if (ToStringIsDebugArea()) {
         g.setColor(ColorUtils.FULLY_OPAQUE_RED);
         g.drawRect(areaX - 1, areaY - 1, areaW + 1, areaH + 1);
      }
      //#enddebug

      if (scale != null) {
         drawScaled(g);
      } else {
         StringDraw stringDraw = getDraw();
         stringDraw.initTrackerXY(areaX, areaY);
         stringDraw.initRequestChars(0, lengthChars);
         stringDraw.initRequestLines(0, stringMetrics.getNumOfLines());
         stringDraw.initRequestArea(breakW, breakH);
         stringDraw.drawOffsetsLines(g);
      }
   }

   /**
    * Draw the given character index from (x,y) position last given 
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
      if (hasState(ITechStringer.STATE_01_CHAR_EFFECTS)) {
         getDraw().drawUniqueCharFx(g, chars[offsetChars + indexRelative], indexRelative);
      } else {
         getDraw().drawUniqueCharBasic(g, chars[offsetChars + indexRelative], indexRelative);
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
    * This is fine for line {@link StringFx} but what happens to Paragraph or {@link IBOFxStr#FX_SCOPE_0_TEXT}
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
      StringDraw stringDraw = getDraw();
      stringDraw.initTrackerXY(x, y);
      stringDraw.initRequestChars(wOffset, wNum);
      stringDraw.initRequestLines(hOffset, hNum);
      stringDraw.initRequestArea(breakW, breakH);
      stringDraw.setAbsoluteXY(false);
      stringDraw.drawOffsetsLines(g);
   }

   /**
    * 
    * @param g
    */
   public void drawScaled(GraphicsX g) {
      int bgColor = 0;
      if (g.isVirgin(areaX, areaY, areaW, areaH)) {
         bgColor = g.getBgColor();

      } else {
         //we must use a color not used
         bgColor = 0;

         //or we can use the background color of hosting figure in StringDrawable.
      }
      int scaledImageW = stringMetrics.getPrefWidth() + 1;
      int scaledImageH = stringMetrics.getPrefHeight() + 1;
      //draw on image and scale to fit area. metrics
      //TODO check if figure has defined a bgcolor else use transparent
      RgbImage baseImage = drc.getCache().create(scaledImageW, scaledImageH, bgColor);
      GraphicsX gi = baseImage.getGraphicsX();
      int scalePosX = 1; //glitch needed for pretty scaling.
      int scalePoxY = 0;
      drawOffsets(gi, scalePosX, scalePoxY, 0, lengthChars, 0, stringMetrics.getNumOfLines());

      //removes background pixels if the x,y area is not virgin of background color

      //TODO fix the double blending
      RgbImage scaledImage = drc.getRgbImageOperator().scaleRgbImage(baseImage, areaW, areaH, scale);

      g.drawRgbImage(scaledImage, areaX, areaY);
      baseImage.dispose();
      scaledImage.dispose();
   }

   public StringInterval[] ensureCapacity(StringInterval[] ar, int val) {
      if (ar == null) {
         return new StringInterval[val + 1];
      }
      if (ar.length <= val) {
         StringInterval[] newa = new StringInterval[val + 1];
         for (int i = 0; i < ar.length; i++) {
            newa[i] = ar[i];
         }
         return newa;
      }
      return ar;
   }

   public StringStyleLayer[] ensureCapacity(StringStyleLayer[] ar, int val) {
      if (ar == null) {
         return new StringStyleLayer[val + 1];
      }
      if (ar.length <= val) {
         StringStyleLayer[] newa = new StringStyleLayer[val + 1];
         for (int i = 0; i < ar.length; i++) {
            newa[i] = ar[i];
         }
         return newa;
      }
      return ar;
   }

   public int getAreaH() {
      return areaH;
   }

   public int getAreaW() {
      return areaW;
   }

   /**
    * 
    * @return
    */
   public int getAreaX() {
      return areaX;
   }

   public int getAreaY() {
      return areaY;
   }

   /**
    * Value set by the code using the {@link Stringer} using {@link Stringer#setBreakWH(int, int)}
    * 
    * <p>
    * When not set, return 0. This means height space is not a specific constraint while metering the string
    * </p>
    * @return
    */
   public int getBreakH() {
      return breakH;
   }

   /**
    * The maximum number of displayed lines, first decided by {@link IBOFigString#FIG_STRING_OFFSET_10_MAXLINES1}.
    * <p>
    * 0 or negative means no maximum
    * </p>
    * Unless set by {@link Stringer#setBreakMaxLines(int)}
    * @return
    */
   public int getBreakMaxLines() {
      return breakMaxLines;
   }

   /**
    * <li> {@link ITechStringDrw#BREAK_0_NONE}
    * <li> {@link ITechStringDrw#BREAK_1_WIDTH}
    * <li> {@link ITechStringDrw#BREAK_2_NATURAL}
    * <li> {@link ITechStringDrw#BREAK_3_ONE_LINE}
    * @return
    * @see Stringer#setBreakType(int)
    */
   public int getBreakType() {
      return breakType;
   }

   /**
    * Value set by the code using the {@link Stringer} using {@link Stringer#setBreakWH(int, int)} 
    * 
    * <p>
    * When not set, return 0. This means width space is not a constraint
    * </p>
    * @return
    */
   public int getBreakW() {
      return breakW;
   }

   public char getCharAtRelative(int indexRel) {
      return chars[offsetChars + indexRel];
   }

   /**
    * Returns the {@link StringFx} used to draw the character at index
    * <br>
    * <br>
    * TODO how to code for a character color on each line from a color function? is this a leaf for each line?
    * <br>
    * Yes it is a leaf one each line.
    * <br>
    * This method is used by {@link StringMetrics#getCharWidthCompute(char, int)} when breaking down the String.
    * 
    * As a consequence LineFx does not impact string breaking ?
    * 
    * @param index
    * @return {@link StringFx}
    */
   public StringFx getCharFx(int index) {
      //there is at least the base interval
      IntInterval owner = getIntervalsOfLeaves().getIntervalIntersect(index);
      if (owner == null) {
         return stringFx;
         //throw new IllegalStateException();
      }
      StringFxLeaf leaf = (StringFxLeaf) owner.getPayload();
      return leaf.getFx();
   }

   /**
    * 
    * @return
    */
   public char[] getCharsRef() {
      return chars;
   }

   public int getCharsStart() {
      return offsetChars;
   }

   /**
    * Returns a copy of the displayed string, including special characters
    * @return
    */
   public String getDisplayedString() {
      if (chars == null) {
         return "";
      }
      return new String(chars, offsetChars, lengthChars);
   }

   public StringDraw getDraw() {
      return stringDraw;
   }

   /**
    * 
    * @return
    */
   public StringerEditor getEditor() {
      if (editor == null) {
         editor = new StringerEditor(this);
      }
      return editor;
   }

   /**
    * The base {@link StringFx} for the whole string.
    * @return
    */
   public StringFx getFx() {
      //#debug
      drc.toStringCheckNull(stringFx);
      return stringFx;
   }

   /**
    * Leaves for characters/words
    * 
    * For line fx ?
    * {@link Stringer#getLineFx(int)}
    * @return
    */
   public IntIntervals getIntervalsOfLeaves() {
      if (intervalOfStringLeaves == null) {
         throw new IllegalStateException("Cannot call this method before FX setup");
      }
      return intervalOfStringLeaves;
   }

   public IntIntervals getIntervalsOfWords() {
      if (intervalOfWords == null) {
         intervalOfWords = new IntIntervals(getUC());
         intervalOfWords.setPayLoadCheck(true); //we
      }
      return intervalOfWords;
   }

   public String getIntervalString(IntInterval interval) {
      return new String(chars, interval.getOffset(), interval.getLen());
   }

   public int getLastNumDrawnChars() {
      return lastNumDrawnChars;
   }

   /**
    * Return the leave on the given index
    * 
    * @param index
    * @return
    */
   public StringFxLeaf getLeafFor(int index) {
      for (int i = 0; i < leaves.length; i++) {
         if (leaves[i].contains(index)) {
            return leaves[i];
         }
      }
      return null;
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

      return stringFx;
   }

   public int getLineIndexFromCharIndex(int indexRelative) {
      return stringMetrics.getLineIndexFromCharIndex(indexRelative);
   }

   public StringMetrics getMetrics() {
      return stringMetrics;
   }

   /**
    * Value telling {@link Stringer} how to deal with new line characters
    * <li> {@link ITechStringer#NEWLINE_MANAGER_0_IGNORE}
    * <li> {@link ITechStringer#NEWLINE_MANAGER_1_WORK}
    * @return
    * @see Stringer#setNewLineManager(int)
    */
   public int getNewLineManager() {
      return newLineManager;
   }

   /**
    * Returns the number of Fx with the flag {@link IBOFxStr#FX_FLAGZ_2_DYNAMIC}
    * 
    * <p>
    * Opposite method of {@link Stringer#getNumFxStaticIndex(ByteObject[])}
    * </p>
    * @param textFigure
    * @return
    */
   int getNumFxDynamic(ByteObject[] subs) {
      int count = 0;
      if (subs != null) {
         for (int i = 0; i < subs.length; i++) {
            if (subs[i] != null && subs[i].hasFlag(FX_OFFSET_04_FLAGZ, FX_FLAGZ_2_DYNAMIC)) {
               count++;
            }
         }
      }
      return count;
   }

   /**
    * Opposite method of {@link Stringer#getNumFxDynamic(ByteObject[])}
    * @param subs
    * @return
    */
   int getNumFxStaticIndex(ByteObject[] subs) {
      int count = 0;
      for (int i = 0; i < subs.length; i++) {
         if (subs[i] != null && !subs[i].hasFlag(FX_OFFSET_04_FLAGZ, FX_FLAGZ_2_DYNAMIC)) {
            if (subs[i].hasFlag(FX_OFFSET_04_FLAGZ, FX_FLAGZ_1_STATIC_INDEX)) {
               count++;
            }
         }
      }
      return count;
   }

   public int getNumLinesPerPage() {
      if (numLinesPerPage == 0) {
         //generate it from areaH
         numLinesPerPage = 5; //TODO param from config
      }
      return numLinesPerPage;
   }

   /**
    * 
    * @return
    */
   public int getNumOfLines() {
      return stringMetrics.getNumOfLines();
   }

   public int getOffsetChar() {
      return offsetChars;
   }

   public int getSpaceTrimManager() {
      return spaceTrimManager;
   }

   /**
    * The base {@link StringFx} for the whole string.
    * @return
    */
   public StringFx getStringFx() {
      return stringFx;
   }

   public XString getStringX() {
      return new XString(drc.getUCtx(), chars, offsetChars, lengthChars);
   }

   /**
    * {@link StringStyleLayer} on which you can set new intervals.
    * 
    * @param layerID 0 for base style layer, 1 for selection layer etc
    * @return {@link StringStyleLayer}
    */
   public StringStyleLayer getStyleLayer(int layerID) {
      return getStyleLayers()[layerID];
   }

   public StringStyleLayer getStyleLayerRoot() {
      return getStyleLayers()[0];
   }

   /**
    * 
    * @return
    */
   public StringStyleLayer[] getStyleLayers() {
      if (styleLayers == null) {
         styleLayers = new StringStyleLayer[1]; //at start only 1 layer
         styleLayers[0] = createLayer(NAME_ROOT_STYLE_LAYER, 0);
      }
      return styleLayers;
   }

   public int getTabManager() {
      return tabManager;
   }

   /**
    * Compute the TBLR value with implicit {@link ILayoutable} etalon being the area X,Y-W,H.
    * 
    * @param tblr possibly having a sizer requiring an {@link ILayoutable}
    * @param pos {@link C#POS_0_TOP}
    * @return
    */
   public int getTBLRValueStringerArea(ByteObject tblr, int pos) {
      LayoutOperator layoutOperator = drc.getLayoutOperator();
      int value = layoutOperator.getTBLRValue(tblr, pos, this.areaX, this.areaY, this.areaW, this.areaH);
      return value;
   }

   public ByteObject getTextFigure() {
      return text;
   }

   /**
    * Format String to fit into the <code>width</code> parameter given the {@link Font}.
    * <br>
    * <br>
    * Does not modify the state of the {@link Stringer}.
    * <br>
    * <br>
    * 
    * When maxLines is 1, trim as soon as width is consumed.
    * <br>
    * When maxLines is 2, trim on the second line after a line break.
    * <br>
    * <br>
    * <b>Structure of Integer Array</b> <br>
    * index[0] = control value<br>
    * index[1] = start index of 1st line<br>
    * index[2] = number of characters on 1st line<br>
    * index[3] = charwidth computed on 1st line<br>
    * index[4] = start index of 2nd line<br>
    * index[5] = number of characters on 2nd line<br>
    * index[6] = charwidth computed on  2nd line<br>
    * index[7] = ctrl flag
    * <br>
    * This structure allows to skip newline entirely. It allows to draw tabs \t.
    * 
    * <li>array's length is the number of lines * 2 + 1
    * <li>The first value is a control value.
    * <li>A line may be empty.
    * <br>
    * <br>
    * TODO Write a switch to stop the format for really big strings. The maxline is a first security.                 
    * <br>
    * <br>
    * @param width width given for formatting the string. If width not big enough for one character. Method fits at least one letter by line.
    * @param maxLines the number of lines after -1 if infinity of lines. Automatically sets the TRIM state to the 
    * @return integer array 
    */
   public int[] getTrimFormat(int width, int maxLines) {
      int lineWidth = 0;
      int lineCount = 1;
      int numCharOnLine = 0;
      IntBuffer data = new IntBuffer(drc.getUCtx());
      StringMetrics sm = this.getMetrics();
      int charw = 0;
      int lineStartOffset = 0;
      boolean isTrimNeeded = false;
      for (int step = 0; step < this.lengthChars; step++) {
         charw = sm.getCharWidth(step);
         lineWidth += charw;
         if (lineWidth > width) {
            if (lineCount == maxLines) {
               //we reached the end of available lines. trim the last 2 characters to replace them with the trim cue.
               lineWidth -= charw;
               isTrimNeeded = true;
               break;//end the algo
            } else {
               //special case where not even one character can fit the space. only one dot will be drawn.
               if (numCharOnLine == 0) {
                  numCharOnLine = 1;
                  lineWidth = 1;
                  isTrimNeeded = true;
                  break;
               } else {
                  //finish current line
                  data.addInt(lineStartOffset);
                  data.addInt(numCharOnLine);
                  data.addInt(lineWidth - charw);
                  lineStartOffset = step;
                  lineCount++;
                  lineWidth = charw;
                  numCharOnLine = 1;
               }
            }
         } else {
            //increment the number of characters on this line
            numCharOnLine += 1;
         }
      }
      //finalize line if there is enough space
      data.addInt(lineStartOffset);
      data.addInt(numCharOnLine);
      data.addInt(lineWidth);
      int flag = 0;
      if (!isTrimNeeded) {
         flag = 1;
      }
      data.addInt(flag);
      return data.getIntsRef();
   }

   /**
    * The words intervals as int[] array, not including space, commas and various punctuations characters
    * @param lineIndex
    * @return
    */
   public int[] getWordBreaks(int lineIndex) {
      LineStringer line = stringMetrics.getLine(lineIndex);

      return line.getWordBreaks();
   }

   //#mdebug

   /**
    * Word breaks on this line for the given interval..
    * @param offset offset is relative to line offset
    * @param len
    * @return
    */
   public int[] getWordBreaks(int offset, int len) {
      StringUtils strU = drc.getUCtx().getStrU();
      int offsetStartLine = this.offsetChars + offset;
      return strU.getBreaksWord(this.chars, offsetStartLine, len);
   }

   public int[] getWordBreaks(IntInterval interval) {
      return getWordBreaks(interval.getOffset(), interval.getLen());
   }

   public int getWordwrap() {
      return wordwrap;
   }

   /**
    * <li> {@link ITechStringer#STATE_01_CHAR_EFFECTS}
    * <li> {@link ITechStringer#STATE_02_CHAR_WIDTHS}
    * <li> {@link ITechStringer#STATE_03_CHECK_CLIP}
    * <li> {@link ITechStringer#STATE_04_TRIMMED}
    * @param state
    * @return
    */
   public boolean hasState(int state) {
      return BitUtils.hasFlag(states, state);
   }

   /**
    * Index finding on the visible chars.
    * @param str
    * @param offset
    * @return
    */
   public int indexOf(String str, int offset) {
      return StringUtils.indexOf(chars, offsetChars, lengthChars, str, offset);
   }

   /**
    * Invalidates all the computed data because the string was completely changed
    */
   public void invalidateFlags() {
      states = 0;
      stringMetrics.reset(); //TODO
   }

   public boolean isProtected() {
      return hasState(STATE_30_PROTECTED);
   }

   public boolean isShowHiddenChars() {
      return isShowHiddenChars;
   }

   public boolean isStyleReady() {
      return stringFx != null;
   }

   public boolean isTrimArtifacts() {
      return isTrimArtifacts;
   }

   public boolean isValidAbsoluteIndex(int index) {
      boolean b = index >= this.offsetChars && index < this.offsetChars + this.lengthChars;
      return b;
   }

   /**
    * Called when char data has changed.
    * 
    * Slightly different than {@link Stringer#resetFxDefinition()}
    */
   public void resetFigure() {
      stringMetrics.reset();
      if (intervalOfWords != null) {
         intervalOfWords.clear();
      }
      if (intervalOfStringLeaves != null) {
         intervalOfStringLeaves.clear();
      }
      setState(STATE_06_CHAR_POSITIONS, false);
      setState(STATE_04_TRIMMED, false);
      setState(STATE_07_BROKEN, false);
      setState(STATE_20_METERED_FULL, false);
      setState(STATE_21_ZERO_WIDTH_CHARS, false);

   }

   public void resetFxDefinition() {
      setState(STATE_19_FX_SETUP, false);
      setState(STATE_17_COMPUTED_FX, false);
      setState(STATE_20_METERED_FULL, false);
      setState(STATE_02_CHAR_WIDTHS, false);
      setState(STATE_06_CHAR_POSITIONS, false);
      setState(STATE_07_BROKEN, false);

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

   public void setAreaWH(int contentW, int contentH) {
      areaW = contentW;
      areaH = contentH;
   }

   /**
    * Sets the area.. used for computing alignments
    * 
    * <p>
    * Break width is not necessarily equal to width of area
    * </p>
    * @param x
    * @param y
    * @param contentW
    * @param contentH
    */
   public void setAreaXYWH(int x, int y, int contentW, int contentH) {
      areaX = x;
      areaY = y;
      areaW = contentW;
      areaH = contentH;

   }

   public void setBreakHeight(int breakH) {
      this.breakH = breakH;
   }

   public void setBreakMaxLines(int breakMaxLines) {
      this.breakMaxLines = breakMaxLines;
   }

   /**
    * Sets break width and height on the area.
    * 
    * <p>
    * Will work only if {@link ITechStringer#WORDWRAP_0_NONE} is not active
    * </p>
    */
   public void setBreakOnArea() {
      breakW = areaW - areaX;
      breakH = areaH - areaY;
   }

   /**
    * <li> {@link ITechStringDrw#BREAK_0_NONE}
    * <li> {@link ITechStringDrw#BREAK_1_WIDTH}
    * <li> {@link ITechStringDrw#BREAK_2_NATURAL}
    * <li> {@link ITechStringDrw#BREAK_3_ONE_LINE}
    * @param breakType
    */
   public void setBreakType(int breakType) {
      this.breakType = breakType;
   }

   /**
    * When 0, areaW / areaH will be used
    * @param breakW
    * @param breakH
    */
   public void setBreakWH(int breakW, int breakH) {
      this.breakW = breakW;
      this.breakH = breakH;
   }

   public void setBreakWidth(int breakW) {
      this.breakW = breakW;
   }

   public void setCharAt(int i, char c) {
      stringMetrics.setCharAt(i, c);
   }

   /**
    * 
    * @param i
    * @param c
    */
   public void setCharAtRelative(int indexRel, char c) {
      chars[offsetChars + indexRel] = c;
   }

   void setChars(char[] chars, int offset, int len) {
      if (chars == null) {
         throw new NullPointerException();
      }

      this.chars = chars;
      this.offsetChars = offset;
      this.lengthChars = len;

      this.resetFigure();

   }

   /**
    * Used internally 
    * @param chars
    * @param offset
    * @param len
    */
   void setCharsInternal(char[] chars, int offset, int len) {
      this.chars = chars;
      this.offsetChars = offset;
      this.lengthChars = len;
   }

   /**
    * Moi j'ai pas beaucoup de dépenses physiques mais je dors bien 🙂
    * 😕 😩 😩 pas assez de depense d'energies physiques
    * ca va? 😘 ☔ ☀ ♠ ♡ ♢ ♣ ♤ ♥ ♦ ♧
    */
   private void setDynamicFXs() {
   }

   /**
    * <li> {@link ITechStringer#NEWLINE_MANAGER_0_IGNORE}
    * <li> {@link ITechStringer#NEWLINE_MANAGER_1_WORK}
    * @see Stringer#getNewLineManager()
    * @param newLineManager
    */
   public void setNewLineManager(int newLineManager) {
      this.newLineManager = newLineManager;
   }

   public void setNumLinesPerPage(int numLinesPerPage) {
      this.numLinesPerPage = numLinesPerPage;
   }

   public void setPosition(int contentX, int contentY) {
      areaX = contentX;
      areaY = contentY;
   }

   public void setProtected(boolean v) {
      this.setState(STATE_30_PROTECTED, v);
   }

   public void setShowHiddenChars(boolean isShowHiddenChars) {
      this.isShowHiddenChars = isShowHiddenChars;
   }

   /**
    * Sets the {@link StringBBuilder} as the source of chars..
    * 
    * This allows us to efficiently display a {@link StringBBuilder} without creating another String array
    * 
    * It will use the char offset and length at the time of this call.
    * @param sb
    */
   public void setSource(StringBBuilder sb) {
      this.setChars(sb.getArrayRef(), 0, sb.getCount());
   }

   public void setSpaceTrimManager(int spaceTrimManager) {
      this.spaceTrimManager = spaceTrimManager;
   }

   void setState(int state, boolean v) {
      states = BitUtils.setFlag(states, state, v);
   }

   public void setString(char[] chars, int offset, int len) {
      this.setChars(chars, offset, len);
   }

   public void setString(String str) {
      char[] charArray = str.toCharArray();
      this.setChars(charArray, 0, charArray.length);
   }

   /**
    * Initializes the {@link Stringer} with a new text and its figure artifacts defined in {@link ByteObject}
    * @param textFigure
    * @param chars
    * @param offset
    * @param len
    */
   public void setStringFig(ByteObject textFigure, char[] chars, int offset, int len) {
      setChars(chars, offset, len);
      setTextFigure(textFigure);
   }

   /**
    * Syntaxic sugar for {@link Stringer#setStringFig(ByteObject, char[], int, int)}
    * @param textFigure
    * @param str
    * @see Stringer#setStringFig(ByteObject, char[], int, int)
    */
   public void setStringFig(ByteObject textFigure, String str) {
      this.setStringFig(textFigure, str.toCharArray(), 0, str.length());
   }

   /**
    * Sets the {@link Stringer} with new text figure.
    * 
    * @param textFigure
    */
   public void setTextFigure(ByteObject textFigure) {
      //#debug
      textFigure.checkType(TYPE_050_FIGURE);

      //invalidate
      resetFigure();
      resetFxDefinition();

      this.text = textFigure;
      this.scale = text.getSubFirst(TYPE_055_SCALE);
      this.newLineManager = text.get1(IBOFigString.FIG_STRING_OFFSET_14_MANAGER_NEWLINE1);
      this.wordwrap = text.get1(IBOFigString.FIG_STRING_OFFSET_07_WRAP_WIDTH1);
      this.breakMaxLines = text.get1(IBOFigString.FIG_STRING_OFFSET_10_MAXLINES1);
      this.tabManager = text.get1(IBOFigString.FIG_STRING_OFFSET_13_MANAGER_TAB1);
      this.spaceTrimManager = text.get1(IBOFigString.FIG_STRING_OFFSET_09_SPACE_TRIM1);
      this.isShowHiddenChars = text.hasFlag(IBOFigString.FIG_STRING_OFFSET_01_FLAG, IBOFigString.FIG_STRING_FLAG_2_SHOW_HIDDEN_CHARS);
      this.isTrimArtifacts = text.hasFlag(IBOFigString.FIG_STRING_OFFSET_01_FLAG, IBOFigString.FIG_STRING_FLAG_3_TRIM_ARTIFACT);

      if (text.hasFlag(IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_1_ANCHOR)) {
         anchor = text.getSubFirst(TYPE_069_ANCHOR);
      }
      if (text.hasFlag(IBOFigure.FIG__OFFSET_02_FLAG, IBOFigure.FIG_FLAG_4_MASK)) {
         //figure has a mask, move it to text mask fx.

      }

   }

   public void setTrimArtifacts(boolean isTrimArtifacts) {
      this.isTrimArtifacts = isTrimArtifacts;
   }

   /**
    * Every instances of this word will be drawn using this Fx merge on top of static fx
    * layerID provides at which level it should be merged
    * TODO
    * Or create a new layer on which to set this. parse char and set the intinterval.
    * 
    * Must be done everytime the stringer is updated
    * 
    * dynamic static ?
    * First Char -> Fx A static index
    * Whole Word -> Fx B static
    * @param str
    * @param fx
    */
   public void setWordFxSearch(String str, ByteObject fx) {
      if (searchApp == null) {
         StringStyleApplicatorWord ssaw = new StringStyleApplicatorWord(drc);
         ssaw.setStringer(this);
         //create specific layer for searches, clear it
         StringStyleLayer ssl = createLayer("search");
         ssaw.setLayer(ssl);
         searchApp = ssaw;
      }

      searchApp.reset();
      searchApp.setFxSrc(fx);
      searchApp.setWord(str);

      //invalidates the styles
      resetFxDefinition();
   }

   public void setWordwrap(int wordwrap) {
      this.wordwrap = wordwrap;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, Stringer.class, "1695");
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.appendWithNewLine("Area = [" + areaX + "," + areaY + " - " + areaW + "," + areaH + "]");
      
      IntToStrings stateFlags = ToStringStaticDrawx.stringerStateFlagMap(toStringGetUCtx());
      dc.nl();
      dc.appendFlagsPositive(states, "States ->", stateFlags);
      dc.nl();
      dc.appendFlagsNegative(states, "States ->", stateFlags);

      dc.nlLvl(text, "textFigure");

      IMFont stringFont = this.drc.getFxStringOperator().getStringFont(text);

      dc.setFlagData(drc.getCoreDrawCtx(), IFlagToStringCoreDraw.TOSTRING_FLAG_3_IGNORE_FONT_ATTRIBUTES, true);
      dc.setFlagData(drc.getCoreDrawCtx(), IFlagToStringCoreDraw.TOSTRING_FLAG_4_SHOW_FONT_ENVIRONEMT, false);

      dc.nlLvl(stringFont, "stringFont");


      dc.nlLvl(anchor,"anchor");

      dc.nlLvl(stringMetrics);
      dc.nlLvl(stringDraw);
      dc.nlLvl(stringFx);

      dc.nlLvl("currentInterval", currentInterval);
      dc.nlLvl("intervalOfWords", intervalOfWords);
      dc.nlLvl("intervalOfStringLeaves", intervalOfStringLeaves);

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, Stringer.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   public boolean ToStringIsDebugArea() {
      return toStringDebugArea;
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("offsetChars", offsetChars);
      dc.appendVarWithSpace("lengthChars", lengthChars);
      if (chars == null) {
         dc.appendWithSpace("chars is null");
      }
      String strDis = getDisplayedString();
      dc.append(" DisplayedString=\"");
      dc.append(strDis);
      dc.append("\"");
   }

   public void ToStringSetDebugArea(boolean toStringDebugArea) {
      this.toStringDebugArea = toStringDebugArea;
   }
   //#enddebug

   public void ToStringSetDebugBreakLines(boolean toStringSetDebugBreakLines) {
      this.toStringDebugBreakLines = toStringSetDebugBreakLines;
   }

}
