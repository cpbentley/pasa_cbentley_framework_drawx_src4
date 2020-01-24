package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.factories.AbstractDrwFactory;

/**
 * Creator of {@link IFxStr} templates.
 * <br>
 * <br>
 * 
 * @author Charles-Philip Bentley
 *
 */
public class FxStringFactory extends AbstractDrwFactory implements IFxStr, IBOTypesDrw {

   public FxStringFactory(DrwCtx drc) {
      super(drc);
   }

   /**
    * Line text effect.
    * <br>
    * @param xf xf modifier from normal char width (signed byte)
    * @param yf yf modifier from normal font height (signed byte)
    * @return
    */
   public ByteObject getFxLine(int xf, int yf) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_070_TEXT_EFFECTS, FXLINE_BASIC_SIZE);
      p.setValue(FX_OFFSET_03_SCOPE1, FX_SCOPE_2_LINE, 1);
      p.setFlag(FXLINE_OFFSET_02_CHAR_X_OFFSET1, FXLINE_FLAG_5_DEFINED_XF, true);
      p.setFlag(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, FXLINE_FLAG_6_DEFINED_YF, true);
      p.setValue(FXLINE_OFFSET_02_CHAR_X_OFFSET1, xf, 1);
      p.setValue(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, yf, 1);
      return p;
   }

   /**
    * Sets specific index for that style
    * <br>
    * <br>
    * @param index
    * @param style
    * @return
    */
   public ByteObject getTextEffectChar(int index, ByteObject style) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FX_BASIC_SIZE);
      p.setValue(FX_OFFSET_03_SCOPE1, FX_SCOPE_0_CHAR, 1);
      p.setValue(FX_OFFSET_04_INDEX2, index, 2);
      p.setValue(FX_OFFSET_05_INDEX_PATTERN1, index, 1);

      p.addByteObject(style);
      return p;
   }

   /**
    * Figures to be drawn below
    * @param bgFigure
    * @return
    */
   public ByteObject getFxChar(ByteObject[] bgFigure, int[] indexes) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_03_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   public ByteObject getFxChar(int type) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_03_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   /**
    * Gets the effect Char Lvl, Line Level
    * <br>
    * <li> {@link ByteObject#TXT_LVL_CHAR}
    * <li> {@link ByteObject#TXT_LVL_LINE}
    * 
    * @param style
    * @param flagtype
    * @param flag
    * @return
    */
   public ByteObject getTxtEffectDrw(ByteObject fx, int flag, int scope) {
      return null;
   }

   /**
    * From main, get block, line or char effect
    * <br>
    * <br>
    * @param txt
    * @param type
    * @param flag
    * @return null if no such text effects
    */
   public ByteObject getSubFxEffect(ByteObject txt, int type, int flag) {
      if (txt.hasFlag(FX_OFFSET_01_FLAG, flag)) {
         ByteObject[] param = txt.getSubs();
         for (int i = 0; i < param.length; i++) {
            ByteObject p = param[i];
            if (p != null) {
               if (p.get1(FX_OFFSET_03_SCOPE1) == type)
                  return p;
            }
         }
      }
      return null;
   }

   /**
    * Create a Mask FX with the given scope
    * @param mask
    * @param scope
    * @return
    */
   public ByteObject getFxMask(ByteObject mask, int scope) {
      ByteObject p = getFx(scope);
      p.setFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_3_MASK, true);
      p.addByteObject(mask);
      return p;
   }

   public int getLineExtraH(ByteObject fxLine) {
      if (fxLine != null) {

      }
      return 0;
   }

   public int getLineExtraW(ByteObject fxLine) {
      if (fxLine != null) {

      }
      return 0;
   }

   public int getLineExtraBetween(ByteObject fxLine) {
      if (fxLine != null) {

      }
      return 0;
   }

   /**
    * Alternating char level fx for all characters.
    * <br>
    * <br>
    * Each char has a different color/font/fx
    * <br>
    * <br>
    * @param fxs
    * @return
    */
   public ByteObject getFxChar(ByteObject[] fxs) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_03_SCOPE1, FX_SCOPE_0_CHAR, 1);
      p.addByteObject(fxs);
      return p;
   }

   public ByteObject getFxChar(ByteObject[] masks, IMFont[] fonts, int[] indexes) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_03_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   public ByteObject getFxChar(ByteObject[] masks, IMFont[] fonts) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_03_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   public void addFxMask(ByteObject fx, ByteObject mask) {
      fx.addByteObject(mask);
      fx.setFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_3_MASK, true);
   }

   /**
    * 
    * @param mask
    * @return
    */
   public ByteObject getFxChar(ByteObject mask) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_03_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   public ByteObject getFxChar(ByteObject mask, int charIndex) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_03_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   public ByteObject getSubCharFx(ByteObject fx) {
      return getSubFxEffect(fx, FX_SCOPE_0_CHAR, FX_FLAG_8_CHAR);
   }

   /**
    * 
    * @param fx
    * @return
    */
   public ByteObject getSubLineFx(ByteObject fx) {
      return getSubFxEffect(fx, FX_SCOPE_2_LINE, FX_FLAG_7_LINE);
   }

   public ByteObject getFx(int scope) {
      int size = 0;
      if (scope == FX_SCOPE_0_CHAR) {
         size = FXCHAR_BASIC_SIZE;
      } else if (scope == FX_SCOPE_2_LINE) {
         size = FXLINE_BASIC_SIZE;
      } else {
         size = FX_BASIC_SIZE;
      }
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, size);
      p.set1(FX_OFFSET_03_SCOPE1, scope);
      return p;
   }

}
