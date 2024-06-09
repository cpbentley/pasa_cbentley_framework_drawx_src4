package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.factories.AbstractDrwFactory;
import pasa.cbentley.framework.drawx.src4.factories.FigureFactory;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStr;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAux;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxFormat;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxFx;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxFxApplicator;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxFxStruct;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxSpecialCharDirective;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;

public class StringAuxFactory extends AbstractDrwFactory implements IBOFxStr, IBOTypesDrawX, IBOStrAuxFxApplicator, IBOStrAuxFxStruct {

   private ByteObject StrAuxFormat_Default   = null;

   private ByteObject StrAuxSpecials_Default = null;

   public StringAuxFactory(DrwCtx dc) {
      super(dc);
   }

   /**
    * Creates a {@link ByteObject} with 
    * <li> {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX} as type 
    * <li> {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_3_APPLICATOR} as subtype 
    * 
    * @return never null {@link ByteObject}
    */
   public ByteObject createStrAuxApplicator() {
      ByteObject p = getBOFactory().createByteObject(TYPE_DRWX_07_STRING_AUX, IBOStrAuxFxApplicator.FXA_BASIC_SIZE);
      p.set1(STR_AUX_OFFSET_1_EXT_TYPE1, TYPE_DRWX_07_STRING_AUX_3_APPLICATOR);
      return p;
   }

   public ByteObject createStrAuxFormat() {
      ByteObject p = getBOFactory().createByteObject(TYPE_DRWX_07_STRING_AUX, IBOStrAuxFormat.STR_FORMAT_BASIC_SIZE);
      p.set1(STR_AUX_OFFSET_1_EXT_TYPE1, TYPE_DRWX_07_STRING_AUX_1_FORMAT);
      return p;
   }

   /**
    * Creates a {@link ByteObject} with 
    * <li> {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX} as type 
    * <li> {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_0_FX} as subtype 
    * 
    * @return never null {@link ByteObject}
    */
   public ByteObject createStrAuxFx() {
      ByteObject p = getBOFactory().createByteObject(TYPE_DRWX_07_STRING_AUX, IBOStrAuxFx.STR_AUX_FX_BASIC_SIZE);
      p.set1(STR_AUX_OFFSET_1_EXT_TYPE1, TYPE_DRWX_07_STRING_AUX_0_FX);
      return p;
   }

   /**
    * Creates a {@link ByteObject} with 
    * <li> {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX} as type 
    * <li> {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C} as subtype 
    * 
    * @return never null {@link ByteObject}
    */
   public ByteObject createStrAuxSpecialCharsDirective() {
      ByteObject p = getBOFactory().createByteObject(TYPE_DRWX_07_STRING_AUX, IBOStrAuxSpecialCharDirective.AUX_CHARS_BASIC_SIZE);
      p.set1(STR_AUX_OFFSET_1_EXT_TYPE1, TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C);
      return p;
   }

   /**
    * Creates a {@link ByteObject} with 
    * <li> {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX} as type 
    * <li> {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_1_FORMAT} as subtype 
    * 
    * @return never null {@link ByteObject}
    */
   public ByteObject createStrAuxFxStruct() {
      ByteObject p = getBOFactory().createByteObject(TYPE_DRWX_07_STRING_AUX, IBOStrAuxFxStruct.STR_AUX_FX_BASIC_SIZE);
      p.set1(STR_AUX_OFFSET_1_EXT_TYPE1, TYPE_DRWX_07_STRING_AUX_4_FX_STRUCT);
      return p;
   }

   public ByteObject getFigStringMonoPlain(int size, int color, ByteObject auxFormat, ByteObject auxSpecials) {
      ByteObject bo = drc.getFigureFactory().getFigString(ITechFont.FACE_MONOSPACE, ITechFont.STYLE_PLAIN, size, color);
      bo.addByteObjectNotNullFlagged(auxFormat, IBOFigString.FIG_STRING_OFFSET_02_FLAGX, IBOFigString.FIG_STRING_FLAGX_3_DEFINED_FORMAT);
      bo.addByteObjectNotNullFlagged(auxSpecials, IBOFigString.FIG_STRING_OFFSET_02_FLAGX, IBOFigString.FIG_STRING_FLAGX_4_DEFINED_SPECIALS);
      return bo;
   }

   public ByteObject getFigStringSystemPlain(int size, int color, ByteObject auxFormat, ByteObject auxSpecials) {
      ByteObject bo = drc.getFigureFactory().getFigString(ITechFont.FACE_SYSTEM, ITechFont.STYLE_PLAIN, size, color);
      bo.addByteObjectNotNullFlagged(auxFormat, IBOFigString.FIG_STRING_OFFSET_02_FLAGX, IBOFigString.FIG_STRING_FLAGX_3_DEFINED_FORMAT);
      bo.addByteObjectNotNullFlagged(auxSpecials, IBOFigString.FIG_STRING_OFFSET_02_FLAGX, IBOFigString.FIG_STRING_FLAGX_4_DEFINED_SPECIALS);
      return bo;
   }

   /**
    * Line text effect.
    * <br>
    * @param xf xf modifier from normal char width (signed byte)
    * @param yf yf modifier from normal font height (signed byte)
    * @return
    */
   public ByteObject getFxLine(int xf, int yf) {
      ByteObject p = createStrAuxFormat();
      p.setFlag(FXLINE_OFFSET_02_CHAR_X_OFFSET1, FXLINE_FLAG_5_DEFINED_XF, true);
      p.setFlag(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, FXLINE_FLAG_6_DEFINED_YF, true);
      p.setValue(FXLINE_OFFSET_02_CHAR_X_OFFSET1, xf, 1);
      p.setValue(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, yf, 1);
      return p;
   }

   public ByteObject getFxStructDiag(int yPixels) {
      ByteObject p = createStrAuxFxStruct();
      p.setFlag(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, FXLINE_FLAG_6_DEFINED_YF, true);
      p.set1(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, yPixels);
      return p;
   }

   public int getNewLineDirective(ByteObject strFig) {
      if (strFig.hasFlag(IBOFigString.FIG_STRING_OFFSET_02_FLAGX, IBOFigString.FIG_STRING_FLAGX_4_DEFINED_SPECIALS)) {
         int typeMain = IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX;
         int index = IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1;
         int size = 1;
         int subType = IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C;
         ByteObject auxSpecials = strFig.getSubFirst(typeMain, index, size, subType);
         return auxSpecials.get1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_02_NEWLINE1);
      }
      //returns the default directive for newlines
      return ITechStringer.SPECIALS_NEWLINE_3_WORK;
   }

   public ByteObject getStrAuxFormat(boolean isTrimArtifact, int wrapW, int wrapH, int spacetrim, int maxLine) {
      ByteObject bo = createStrAuxFormat();
      bo.setFlag(IBOStrAuxFormat.STR_FORMAT_OFFSET_01_FLAG, IBOStrAuxFormat.STR_FORMAT_FLAG_3_TRIM_ARTIFACT, isTrimArtifact);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1, wrapW);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1, wrapH);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1, spacetrim);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1, maxLine);
      return bo;
   }

   public ByteObject getStrAuxFormat(ByteObject strFig) {
      if (strFig.hasFlag(IBOFigString.FIG_STRING_OFFSET_02_FLAGX, IBOFigString.FIG_STRING_FLAGX_3_DEFINED_FORMAT)) {
         int typeMain = IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX;
         int index = IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1;
         int size = 1;
         int subType = IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_1_FORMAT;
         ByteObject auxFormat = strFig.getSubFirst(typeMain, index, size, subType);
         if (auxFormat == null) {
            throw new NullPointerException("Cannot be null if defined. Miscontruction");
         }
         return auxFormat;
      }
      //returns the default directive for newlines
      return getStrAuxFormat_Default();
   }

   public ByteObject getStrAuxFormat_Anywhere() {
      return getStrAuxFormat_Anywhere(0);
   }

   public ByteObject getStrAuxFormat_Anywhere(int maxLines) {
      ByteObject bo = createStrAuxFormat();
      bo.setFlag(IBOStrAuxFormat.STR_FORMAT_OFFSET_01_FLAG, IBOStrAuxFormat.STR_FORMAT_FLAG_3_TRIM_ARTIFACT, true);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1, ITechStringer.WORDWRAP_1_ANYWHERE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1, ITechStringer.SPACETRIM_1_NORMAL);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1, maxLines);
      return bo;
   }

   public ByteObject getStrAuxFormat_Default() {
      if (StrAuxFormat_Default == null) {
         StrAuxFormat_Default = createStrAuxSpecialCharsDirective();
      }
      //returns the default directive for newlines
      return StrAuxFormat_Default;
   }

   public ByteObject getStrAuxFormat_FitHeight(int maxLines) {
      ByteObject bo = createStrAuxFormat();
      bo.setFlag(IBOStrAuxFormat.STR_FORMAT_OFFSET_01_FLAG, IBOStrAuxFormat.STR_FORMAT_FLAG_3_TRIM_ARTIFACT, true);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1, ITechStringer.WORDWRAP_1_ANYWHERE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1, ITechStringer.SPACETRIM_1_NORMAL);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1, maxLines);
      return bo;
   }

   public ByteObject getStrAuxFormat_NiceHyphenation(int maxLines, boolean trimArtifact) {
      ByteObject bo = createStrAuxFormat();
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1, ITechStringer.WORDWRAP_3_NICE_HYPHENATION);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1, ITechStringer.SPACETRIM_1_NORMAL);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1, maxLines);
      bo.setFlag(IBOStrAuxFormat.STR_FORMAT_OFFSET_01_FLAG, IBOStrAuxFormat.STR_FORMAT_FLAG_3_TRIM_ARTIFACT, trimArtifact);
      return bo;
   }

   /**
    * Nice word word wrap with normal space trim. No maximum lines.
    * @return
    */
   public ByteObject getStrAuxFormat_NiceWordNormalTrim() {
      return getStrAuxFormat_NiceWordNormalTrim(0);
   }

   public ByteObject getStrAuxFormat_NiceWordNormalTrim(int maxLines) {
      ByteObject bo = createStrAuxFormat();
      bo.setFlag(IBOStrAuxFormat.STR_FORMAT_OFFSET_01_FLAG, IBOStrAuxFormat.STR_FORMAT_FLAG_3_TRIM_ARTIFACT, true);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1, ITechStringer.WORDWRAP_2_NICE_WORD);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1, ITechStringer.SPACETRIM_1_NORMAL);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1, maxLines);
      return bo;
   }

   public ByteObject getStrAuxFormat_NiceWrapJustified() {
      ByteObject bo = createStrAuxFormat();
      bo.setFlag(IBOStrAuxFormat.STR_FORMAT_OFFSET_01_FLAG, IBOStrAuxFormat.STR_FORMAT_FLAG_3_TRIM_ARTIFACT, true);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1, ITechStringer.WORDWRAP_2_NICE_WORD);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1, ITechStringer.SPACETRIM_2_JUSTIFIED);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1, 0);
      return bo;
   }

   public ByteObject getStrAuxFormat_NoWrapJustified() {
      ByteObject bo = createStrAuxFormat();
      bo.setFlag(IBOStrAuxFormat.STR_FORMAT_OFFSET_01_FLAG, IBOStrAuxFormat.STR_FORMAT_FLAG_3_TRIM_ARTIFACT, true);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1, ITechStringer.SPACETRIM_2_JUSTIFIED);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1, 0);
      return bo;
   }

   public ByteObject getStrAuxFormat_NoWrapNormalTrim() {
      ByteObject bo = createStrAuxFormat();
      bo.setFlag(IBOStrAuxFormat.STR_FORMAT_OFFSET_01_FLAG, IBOStrAuxFormat.STR_FORMAT_FLAG_3_TRIM_ARTIFACT, true);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1, ITechStringer.SPACETRIM_1_NORMAL);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1, 0);
      return bo;
   }

   public ByteObject getStrAuxFormat_NoWrapNoTrim() {
      ByteObject bo = createStrAuxFormat();
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1, ITechStringer.WORDWRAP_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1, ITechStringer.SPACETRIM_0_NONE);
      bo.set1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1, 0);
      return bo;
   }

   public ByteObject getStrAuxSpecials(ByteObject strFig) {
      if (strFig.hasFlag(IBOFigString.FIG_STRING_OFFSET_02_FLAGX, IBOFigString.FIG_STRING_FLAGX_4_DEFINED_SPECIALS)) {
         int typeMain = IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX;
         int index = IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1;
         int size = 1;
         int subType = IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C;
         ByteObject auxSpecials = strFig.getSubFirst(typeMain, index, size, subType);
         if (auxSpecials == null) {
            throw new NullPointerException("Cannot be null if defined. Miscontruction");
         }
         return auxSpecials;
      }
      //returns the default directive for newlines
      return getStrAuxSpecials_Default();
   }

   public ByteObject getStrAuxSpecials(int newline, int tab) {
      ByteObject bo = createStrAuxSpecialCharsDirective();
      bo.set1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_02_NEWLINE1, newline);
      bo.set1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_04_MANAGER_TAB1, tab);
      return bo;
   }

   public ByteObject getStrAuxSpecials_Default() {
      if (StrAuxSpecials_Default == null) {
         StrAuxSpecials_Default = createStrAuxSpecialCharsDirective();
      }
      //returns the default directive for newlines
      return StrAuxSpecials_Default;
   }

   public ByteObject getStrAuxSpecials_IgnoreNewLineTabSingleSpace() {
      ByteObject bo = createStrAuxSpecialCharsDirective();
      bo.set1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_02_NEWLINE1, ITechStringer.SPECIALS_NEWLINE_0_IGNORED);
      bo.set1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_03_FORMFEED1, ITechStringer.SPECIALS_FORMFEED_0_IGNORED);
      bo.set1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_04_MANAGER_TAB1, ITechStringer.SPECIALS_TAB_1_SPACE_SPECIAL);
      return bo;
   }

   public ByteObject getStrAuxSpecials_NewLineWorkSingleSpaceTab() {
      ByteObject bo = createStrAuxSpecialCharsDirective();
      bo.set1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_02_NEWLINE1, ITechStringer.SPECIALS_NEWLINE_3_WORK);
      bo.set1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_04_MANAGER_TAB1, ITechStringer.SPECIALS_TAB_1_SPACE_SPECIAL);
      return bo;
   }

   public boolean toStringStrAux(ByteObject bo, Dctx dc) {
      dc.rootN(bo, "IBOStrAux", FigureFactory.class, 1672);
      final int subType = bo.get1(IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1);

      dc.nl(); //new line for the start of the sub type
      switch (subType) {
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_0_FX:
            toStringStrAuxFx(bo, dc);
            break;
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_1_FORMAT:
            toStringStrAuxFormat(bo, dc);
            break;
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C:
            toStringStrAuxSpecials(bo, dc);
            break;
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_3_APPLICATOR:
            toStringStrAuxFxApplicator(bo, dc);
            break;
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_4_FX_STRUCT:
            toStringStrAuxFxStruct(bo, dc);
            break;
         default:
            //subtype is not known by this module
            return false;
      }
      return true;
   }

   public boolean toStringStrAux1Line(ByteObject bo, Dctx dc) {
      dc.rootN(bo, "IBOStrAux", FigureFactory.class, 1672);
      final int subType = bo.get1(IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1);

      dc.nl(); //new line for the start of the sub type
      switch (subType) {
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_0_FX:
            toStringStrAuxFx(bo, dc);
            break;
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_1_FORMAT:
            toStringStrAuxFormat(bo, dc);
            break;
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C:
            toStringStrAuxSpecials(bo, dc);
            break;
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_3_APPLICATOR:
            toStringStrAuxFxApplicator(bo, dc);
            break;
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_4_FX_STRUCT:
            toStringStrAuxFxStruct(bo, dc);
            break;
         default:
            //subtype is not known by this module
            return false;
      }
      return true;
   }
   /**
    * Coming from {@link FigureFactory#toStringFigure(ByteObject, Dctx)}
    * @param bo
    * @param dc
    */
   private void toStringStrAuxFormat(ByteObject bo, Dctx dc) {
      dc.rootN(bo, "IBOStrAuxFormat", StringAuxFactory.class, 317);
      int wordwrap = bo.get1(IBOStrAuxFormat.STR_FORMAT_OFFSET_02_WRAP_WIDTH1);
      dc.appendVarWithNewLine("wrapwidth", wordwrap);
      dc.appendBracketedWithSpace(ToStringStaticDrawx.toStringWordWrap(wordwrap));

      int linewrap = bo.get1(IBOStrAuxFormat.STR_FORMAT_OFFSET_03_WRAP_HEIGHT1);
      dc.appendVarWithNewLine("wrapheight", linewrap);
      dc.appendBracketedWithSpace(ToStringStaticDrawx.toStringLineWrap(linewrap));

      int maxlines = bo.get1(IBOStrAuxFormat.STR_FORMAT_OFFSET_05_MAXLINES1);
      dc.appendVarWithNewLine("maxlines", maxlines);

      int spacetrim = bo.get1(IBOStrAuxFormat.STR_FORMAT_OFFSET_04_SPACE_TRIM1);
      dc.appendVarWithNewLine("spacetrim", spacetrim);
      dc.appendBracketedWithSpace(ToStringStaticDrawx.toStringSpaceTrim(spacetrim));


   }

   private void toStringStrAuxFx(ByteObject bo, Dctx dc) {
      dc.rootN(bo, "IBOStrAuxSpecials", StringAuxFactory.class, 317);
      int newline = bo.get1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_02_NEWLINE1);
      dc.appendVarWithNewLine("newline", newline);
      dc.appendBracketedWithSpace(ToStringStaticDrawx.toStringDirectiveNewLine(newline));
   }

   private void toStringStrAuxFxApplicator(ByteObject bo, Dctx dc) {
      dc.rootN(bo, "IBOStrAuxSpecials", StringAuxFactory.class, 317);
      int newline = bo.get1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_02_NEWLINE1);
      dc.appendVarWithNewLine("newline", newline);
      dc.appendBracketedWithSpace(ToStringStaticDrawx.toStringDirectiveNewLine(newline));
   }

   private void toStringStrAuxFxStruct(ByteObject bo, Dctx dc) {
      dc.rootN(bo, "IBOStrAuxSpecials", StringAuxFactory.class, 317);
      int newline = bo.get1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_02_NEWLINE1);
      dc.appendVarWithNewLine("newline", newline);
      dc.appendBracketedWithSpace(ToStringStaticDrawx.toStringDirectiveNewLine(newline));
   }

   /**
    * Coming from {@link FigureFactory#toStringFigure(ByteObject, Dctx)}
    * @param bo
    * @param dc
    */
   private void toStringStrAuxSpecials(ByteObject bo, Dctx dc) {
      dc.rootN(bo, "IBOStrAuxSpecials", StringAuxFactory.class, 317);
      int newline = bo.get1(IBOStrAuxSpecialCharDirective.AUX_CHARS_OFFSET_02_NEWLINE1);
      dc.appendVarWithNewLine("newline", newline);
      dc.appendBracketedWithSpace(ToStringStaticDrawx.toStringDirectiveNewLine(newline));
   }

}
