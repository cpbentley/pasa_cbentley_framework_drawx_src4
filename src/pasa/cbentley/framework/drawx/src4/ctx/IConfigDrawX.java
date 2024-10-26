/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.ctx.IConfigBO;

/**
 * Configuration for {@link DrwCtx} module.
 * 
 * @author Charles Bentley
 *
 */
public interface IConfigDrawX extends IConfigBO {

   /**
    * {@link IToStringFlagsDrw}
    * @return
    */
   public int getFlagsDrw();

   /**
    * {@link IToStringFlagsDrw#D_FLAG_01_STYLE}
    * {@link IToStringFlagsDrw#D_FLAG_01_STYLE}
    * {@link IToStringFlagsDrw#D_FLAG_25_IGNORE_IGRAPHICS}
    * {@link IToStringFlagsDrw#D_FLAG_26_GRAPHCISX}
    * {@link IToStringFlagsDrw#D_FLAG_27_CACHE}
    * {@link IToStringFlagsDrw#D_FLAG_28_IGNORE_FONT}
    * @return
    */
   public int getFlagsStringDrw();

   /**
    *  
    * @return
    * @see ConfigDrawXDefault#getLineBreakChars() for ideas
    */
   public char[] getLineBreakChars();

   /**
    * 
    * @return
    */
   public char getNewLine();

   /**
    * Any
    * @return
    */
   public char[] getWordSeparators();
}
