/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.ctx.ConfigAbstractBO;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;

public class ConfigDrawXDefault extends ConfigAbstractBO implements IConfigDrawX, IToStringFlagsDrw, ITechCtxSettingsDrwx {

   private char[] lineBreaks;

   public ConfigDrawXDefault(UCtx uc) {
      super(uc);
   }

   /**
    * <li> {@link IToStringFlagsDrw#D_FLAG_01_STYLE}
    * <li> {@link IToStringFlagsDrw#D_FLAG_03_STRINGER}
    * <li> {@link IToStringFlagsDrw#D_FLAG_25_IGNORE_IGRAPHICS}
    * <li> {@link IToStringFlagsDrw#D_FLAG_26_GRAPHCISX}
    */
   public int getFlagsStringDrw() {
      return D_FLAG_01_STYLE | D_FLAG_03_STRINGER;
   }

   /**
    * {@link ITechCtxSettingsDrwx#CTX_DRW_FLAG_1_USER_IMAGE_CACHE}
    */
   public int getFlagsDrw() {
      return 0;
   }

   /**
    * New array to prevent unwanted modification?
    */
   public char[] getLineBreakChars() {
      return new char[] { ' ', '?', ';', ',', '.', '!', ':', '-', '=', '(', ')', '[', ']' };
   }

   /**
    * Can be modified
    * @return
    */
   public char[] getLineBreakCharsCache() {
      if (lineBreaks == null) {
         lineBreaks = getLineBreakChars();
      }
      return lineBreaks;
   }

   public char getNewLine() {
      return '\n';
   }

   public char[] getWordSeparators() {
      return new char[] { ' ', '?', ';', ',', '.', '!', ':', '=', '(', ')', '[', ']' };
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, ConfigDrawXDefault.class);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, ConfigDrawXDefault.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("getFlagsDrw", getFlagsDrw());
   }

   //#enddebug

}
