package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;

public class StringStyleApplicatorWord extends StringStyleApplicator {

   
   private String word;
   
   
   public StringStyleApplicatorWord(DrwCtx drc) {
      super(drc);
   }

   public String getWord() {
      return word;
   }

   public void setWord(String word) {
      this.word = word;
   }

   
   public void apply() {
      if(word == null) {
         throw new NullPointerException("");
      }
      int offset = 0;
      int indexOf = -1;
      int length = word.length();
      while( (indexOf = word.indexOf(word, offset)) != -1) {
         getLayer().addInterval(indexOf, length, getFxSrc());
         offset = offset + length;
      }
      
   }
   
   public String getName() {
      return "word:"+word;
   }
}
