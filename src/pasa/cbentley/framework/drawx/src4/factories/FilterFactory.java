/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.functions.Function;
import pasa.cbentley.byteobjects.src4.tech.ITechFunction;
import pasa.cbentley.byteobjects.src4.tech.ITechOperator;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechFilter;
import pasa.cbentley.framework.drawx.src4.utils.ToStringStaticDraw;

/**
 * Color filter definitions creation class.
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class FilterFactory extends AbstractDrwFactory implements ITechFunction, ITechOperator {

   public FilterFactory(DrwCtx drc) {
      super(drc);
   }

   private final int DEFAULT_FILTER_BG_COLOR = 0;

   /**
    * Simple color filter applying to all pixels
    * @param fct
    * @return
    */
   public ByteObject getFilterAll(ByteObject fct) {
      return getFilterFct(ITechFilter.FILTER_TYPE_00_FUNCTION_ALL, fct);
   }

   public ByteObject getFilterAll(Function fct) {
      return getFilterFct(ITechFilter.FILTER_TYPE_00_FUNCTION_ALL, fct);
   }

   /**
    * Color Filter that visit all pixels setting alpha to given value for all pixels
    * of color
    * @param color
    * @param astart
    * @return
    */
   public ByteObject getFilterAlpaColor(int color, int alpha) {
      return getFilterAlphaColor(color, false, alpha);
   }

   /**
    * Alpha Color
    * @param color color whose alpha channel is to be changed to alpha
    * @param exact if true, alpha channel of image pixel must match that one of color
    * @param alpha
    * @return
    */
   public ByteObject getFilterAlphaColor(int color, boolean exact, int alpha) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, ITechFilter.FILTER_TYPE_03_ALPHA_TO_COLOR, 1);
      p.setFlag(ITechFilter.FILTER_OFFSET_02_FLAG1, ITechFilter.FILTER_FLAG_7_EXACT_MATCH, exact);
      p.setValue(ITechFilter.FILTER_OFFSET_04_FUNCTION2, alpha, 2);
      p.setValue(ITechFilter.FILTER_OFFSET_05_COLOR4, color, 4);
      return p;
   }

   /**
    * A color filter that applies only on corner of the image
    * @return
    */
   public ByteObject getFilterCorners() {
      //TODO
      return null;
   }

   /**
    * Create the ColorFilter shell for a function definition and the filter type
    * @param type type of pixel filtering (all,tblr,...).
    * @param funDef
    * @return
    */
   ByteObject getFilterFct(int type, ByteObject funDef) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, type, 1);
      p.addByteObject(funDef);
      return p;
   }

   public ByteObject getFilterFct(int type, Function fct) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, type, 1);
      p.setFlag(ITechFilter.FILTER_OFFSET_02_FLAG1, ITechFilter.FILTER_FLAG_6_FUNCTION_ID, true);
      p.setValue(ITechFilter.FILTER_OFFSET_04_FUNCTION2, boc.getFunctionFactory().addFunction(fct), 2);
      setTBLRFlagPs(p);
      return p;
   }

   /**
    * Retrieve the Function object from a color filter.
    * <br>
    * Function is declaratively described or hardcoded in a sub class.
    * @param filter
    * @return
    */
   public Function getFilterFunction(ByteObject filter) {
      filter.checkType(IBOTypesDrw.TYPE_056_COLOR_FILTER);
      if (filter.hasFlag(ITechFilter.FILTER_OFFSET_02_FLAG1, ITechFilter.FILTER_FLAG_6_FUNCTION_ID)) {
         int id = filter.getValue(ITechFilter.FILTER_OFFSET_04_FUNCTION2, 2);
         return boc.getFunctionFactory().getFunctionFromID(id);
      } else {
         ByteObject def = filter.getSubFirst(IBOTypesBOC.TYPE_021_FUNCTION);
         return boc.getFunctionFactory().createFunction(def);
      }
   }

   /**
    * {@link ITechFilter#FILTER_TYPE_01_GRAYSCALE}
    * @return
    */
   public ByteObject getFilterGrayScale() {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, ITechFilter.FILTER_TYPE_01_GRAYSCALE, 1);
      return p;
   }

   public ByteObject getFilterRemoveColor(int color) {
      return getFilterAlphaColor(color, true, 0);
   }

   public ByteObject getFilter(int filterType) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, filterType, 1);
      return p;
   }

   public ByteObject getFilterSimpleAlpha(int a) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, ITechFilter.FILTER_TYPE_04_SIMPLE_ALPHA, 1);
      p.setValue(ITechFilter.FILTER_OFFSET_04_FUNCTION2, a, 2);
      return p;
   }

   /**
    * Color Filter with no Function definition.
    * Function is implicit
    * @return
    */
   public ByteObject getFilterSmoothStep() {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, ITechFilter.FILTER_TYPE_06_STEP_SMOOTH, 1);
      return p;
   }

   /**
    * Stick pixels to other pixels
    * TBLR order
    * @return
    */
   public ByteObject getFilterStick() {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, ITechFilter.FILTER_TYPE_09_STICK, 1);
      setTBLRFlagPs(p);
      return p;
   }

   public ByteObject getFilterStick(int stickColor) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, ITechFilter.FILTER_TYPE_09_STICK, 1);
      p.setValue(ITechFilter.FILTER_OFFSET_05_COLOR4, stickColor, 4);
      setTBLRFlagPs(p);
      return p;
   }

   /**
    * 
    * @param fct function to modifies the pixel
    * @param tblrFct
    * @return
    */
   public ByteObject getFilterTBLR(ByteObject fct) {
      return getFilterTBLR(fct, true, true, true, true);
   }

   /**
    * TBLR function with FunDef
    * @param fct
    * @param T
    * @param B
    * @param L
    * @param R
    * @return
    */
   public ByteObject getFilterTBLR(ByteObject fct, boolean T, boolean B, boolean L, boolean R) {
      ByteObject p = getFilterFct(ITechFilter.FILTER_TYPE_07_TBLR, fct);
      setTBLRFlagPs(p, T, B, L, R);
      return p;
   }

   public ByteObject getFilterTBLR(Function fct) {
      return getFilterFct(ITechFilter.FILTER_TYPE_07_TBLR, fct);
   }

   /**
    * Uses default Mask Color (Opaque White)
    * <br>
    * <br>
    * Accepts all pixels.
    * <br>
    * <br>
    * @param size
    * @param alpha
    * @param incr
    * @return
    */
   public ByteObject getFilterTBLRAlpha(int size, int start, int end) {
      return getFilterTBLRAlpha(size, start, end, DEFAULT_FILTER_BG_COLOR, false);
   }

   /**
    * 
    * @param size
    * @param start
    * @param end
    * @param acc
    * @return
    */
   public ByteObject getFilterTBLRAlpha(int size, int start, int end, ByteObject acc) {
      return getFilterTBLRAlpha(size, start, end, FUN_COUNTER_OP_0_ASC, OP_POST_1_X_MAX, acc);
   }

   /**
    * Linear between astart and aend
    * alpha set
    * @param size size of TBLR penetration
    * @param start 
    * @param end
    * @param maskColor color to reject. Specifying a color may allow the code to skip
    * @param doMask count rejections
    * @return
    */
   public ByteObject getFilterTBLRAlpha(int size, int start, int end, int maskColor, boolean doMask) {
      ByteObject acc = boc.getAcceptorFactory().getAcceptor(maskColor, false, doMask);
      return getFilterTBLRAlpha(size, start, end, FUN_COUNTER_OP_0_ASC, OP_POST_1_X_MAX, acc);
   }

   /**
    * 
    * @param size
    * @param start
    * @param end
    * @param indexop
    * @param postop
    * @param doMask
    * @return
    */
   public ByteObject getFilterTBLRAlpha(int size, int start, int end, int indexop, int postop, boolean doMask) {
      //the acceptor defines which color will be ignored
      ByteObject acc = boc.getAcceptorFactory().getAcceptor(DEFAULT_FILTER_BG_COLOR, false, doMask);
      return getFilterTBLRAlpha(size, start, end, indexop, postop, acc);
   }

   /**
    * Filter that applies a Function on Alpha channel in a TBLR way, using an Acceptor on the input color.
    * <br>
    * <br>
    * Color function.
    * Acceptor input is the full pixel.
    * <br>
    * <br>
    * @param size Penetration size of alpha filter in pixels. this means this function is absolute
    * @param start
    * @param incr
    * @param smaller if true, function applies if new value is smaller than current alpha value
    * @param bigger If both set to true,
    * @param random
    * @param maskColor color to be ignore.
    * @param countRejects if true, mask pixels are counter in the function counter
    * @return
    */
   public ByteObject getFilterTBLRAlpha(int size, int start, int end, int indexop, int postop, ByteObject acceptor) {
      if (size < 0) {
         throw new IllegalArgumentException();
      }
      int[] vals = drc.getUCtx().getIU().getValues(size, start, end);
      //create a function definition for those values
      ByteObject funDef = drc.getColorFunctionFactory().getColorFunction(vals, true, false, false, false);
      funDef.setValue(FUN_OFFSET_08_POST_OPERATOR1, postop, 1);
      funDef.setValue(FUN_OFFSET_07_AUX_OPERATOR1, indexop, 1);
      boc.getAcceptorFactory().addAcceptorToFunDef(acceptor, funDef);
      //the function definition is finished. just wrap it around a ColorFilter
      return getFilterTBLR(funDef);
   }

   /**
    * 
    * @param touchColor all colors that touch those pixels will have the function applied to them
    * @param or48
    * @param fct
    * @return
    */
   public ByteObject getFilterTouch(int touchColor, boolean or48, ByteObject fct) {
      return getFilterTouch(touchColor, or48, null, fct);
   }

   private ByteObject getFilterTouch(int touchColor, boolean or48, Function f, ByteObject fct) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_056_COLOR_FILTER, ITechFilter.FILTER_TOUCH_BASIC_SIZE);
      p.setValue(ITechFilter.FILTER_OFFSET_01_TYPE1, ITechFilter.FILTER_TYPE_08_TOUCHES, 1);
      p.setFlag(ITechFilter.FILTER_OFFSET_02_FLAG1, ITechFilter.FILTER_FLAG_5_OR48, or48);
      p.setValue(ITechFilter.FILTER_OFFSET_05_COLOR4, touchColor, 4);
      if (f != null) {
         p.setFlag(ITechFilter.FILTER_OFFSET_02_FLAG1, ITechFilter.FILTER_FLAG_6_FUNCTION_ID, true);
         p.setValue(ITechFilter.FILTER_OFFSET_04_FUNCTION2, boc.getFunctionFactory().addFunction(f), 2);
      } else {
         p.addByteObject(fct);
      }
      return p;
   }

   public ByteObject getFilterTouch(int touchColor, boolean or48, Function f) {
      return getFilterTouch(touchColor, or48, f, null);
   }

   public void addFilter(ByteObject figure, ByteObject filter) {
      if (filter != null) {
         figure.addSub(filter);
         figure.setFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_5_FILTER, true);
      }

   }

   /**
    * Set switches for TBLR filter.
    * @param filter
    * @param T
    * @param B
    * @param L
    * @param R
    */
   void setTBLRFlagPs(ByteObject filter, boolean T, boolean B, boolean L, boolean R) {
      filter.setFlag(ITechFilter.FILTER_OFFSET_03_FLAGP1, ITechFilter.FILTER_FLAGP_1_TOP, T);
      filter.setFlag(ITechFilter.FILTER_OFFSET_03_FLAGP1, ITechFilter.FILTER_FLAGP_2_BOT, B);
      filter.setFlag(ITechFilter.FILTER_OFFSET_03_FLAGP1, ITechFilter.FILTER_FLAGP_3_LEFT, L);
      filter.setFlag(ITechFilter.FILTER_OFFSET_03_FLAGP1, ITechFilter.FILTER_FLAGP_4_RIGHT, R);
   }

   void setTBLRFlagPs(ByteObject p) {
      setTBLRFlagPs(p, true, true, true, true);
   }
   
   public void toStringFilter(ByteObject bo, Dctx sb) {
      sb.append("#ColorFilter ");
      ByteObject rawString = bo.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
      if (rawString != null) {
         sb.append(boc.getLitteralStringOperator().getLitteralString(rawString));
      }
      final int type = bo.get1(ITechFilter.FILTER_OFFSET_01_TYPE1);
      switch (type) {
         case ITechFilter.FILTER_TYPE_00_FUNCTION_ALL:
            sb.append("All Pixels ");
            break;
         case ITechFilter.FILTER_TYPE_03_ALPHA_TO_COLOR:
            sb.append("Alpha Color ");
            sb.nl();
            sb.append("Exact = " + bo.hasFlag(ITechFilter.FILTER_OFFSET_02_FLAG1, ITechFilter.FILTER_FLAG_7_EXACT_MATCH));
            sb.append(" Alpha=" + bo.getValue(ITechFilter.FILTER_OFFSET_04_FUNCTION2, 2));
            sb.append(" Color=" + ToStringStaticDraw.toStringColor(bo.get4(ITechFilter.FILTER_OFFSET_05_COLOR4)));
            break;
         case ITechFilter.FILTER_TYPE_07_TBLR:
            sb.append("TBLR");
            sb.nl();
            boolean T = bo.hasFlag(ITechFilter.FILTER_OFFSET_03_FLAGP1, ITechFilter.FILTER_FLAGP_1_TOP);
            boolean B = bo.hasFlag(ITechFilter.FILTER_OFFSET_03_FLAGP1, ITechFilter.FILTER_FLAGP_2_BOT);
            boolean L = bo.hasFlag(ITechFilter.FILTER_OFFSET_03_FLAGP1, ITechFilter.FILTER_FLAGP_3_LEFT);
            boolean R = bo.hasFlag(ITechFilter.FILTER_OFFSET_03_FLAGP1, ITechFilter.FILTER_FLAGP_4_RIGHT);
            sb.append("T=" + T);
            sb.append(" B=" + B);
            sb.append(" L=" + L);
            sb.append(" R=" + R);
            break;
         case ITechFilter.FILTER_TYPE_04_SIMPLE_ALPHA:
            sb.append("SIMPLE ALPHA To All Pixels ");
            break;
         case ITechFilter.FILTER_TYPE_08_TOUCHES:
            sb.append("Touches");
            sb.nl();
            sb.append("TouchColor=" + ToStringStaticDraw.toStringColor(bo.get4(ITechFilter.FILTER_OFFSET_05_COLOR4)));
            sb.append("or48=" + bo.hasFlag(ITechFilter.FILTER_OFFSET_02_FLAG1, ITechFilter.FILTER_FLAG_5_OR48));
            sb.append("FunctionID=" + bo.hasFlag(ITechFilter.FILTER_OFFSET_02_FLAG1, ITechFilter.FILTER_FLAG_6_FUNCTION_ID));
            break;
         case ITechFilter.FILTER_TYPE_09_STICK:
            sb.append("Stick");
            break;
         case ITechFilter.FILTER_TYPE_01_GRAYSCALE:
            sb.append("GrayScale");
            break;
         case ITechFilter.FILTER_TYPE_02_BILINEAR:
            sb.append("Bilinear");
            break;
         case ITechFilter.FILTER_TYPE_06_STEP_SMOOTH:
            sb.append("Smooth Step");
            break;
         default:
            throw new IllegalArgumentException("Unknown Filter Type");
      }
      if (bo.hasFlag(ITechFilter.FILTER_OFFSET_02_FLAG1, ITechFilter.FILTER_FLAG_6_FUNCTION_ID)) {
         sb.nl();
         int id = bo.getValue(ITechFilter.FILTER_OFFSET_04_FUNCTION2, 2);
         Function f = boc.getFunctionFactory().getFunctionFromID(id);
         if (f != null) {
            sb.nlLvl("", f);
         } else {
            sb.append("#ERORR => Null Function ID =" + id);
         }
      } else {
         //don't draw here it will be handled by the toString of DrwParam
      }
   }

}
