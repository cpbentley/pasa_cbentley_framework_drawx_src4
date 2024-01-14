/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.ctx.IConfigBO;

public interface IConfigDrawx extends IConfigBO {

   /**
    * {@link IFlagsToStringDrw}
    * @return
    */
   public int getFlagsDrw();

   public int getFlagsStringDrw();
   
   public char getNewLine();

   /**
    *  
    * @return
    * @see ConfigDrawXDefault#getLineBreakChars() for ideas
    */
   public char[] getLineBreakChars();

   /**
    * Any
    * @return
    */
   public char[] getWordSeparators();
}
