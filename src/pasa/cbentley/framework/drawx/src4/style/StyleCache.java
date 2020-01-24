package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.factories.TblrFactory;
import pasa.cbentley.framework.drawx.src4.style.IStyle;
import pasa.cbentley.framework.drawx.src4.style.StyleOperator;
import pasa.cbentley.framework.drawx.src4.tech.ITechTblr;
import pasa.cbentley.layouter.src4.interfaces.ILayoutable;
import pasa.cbentley.layouter.src4.interfaces.ISizeCtx;

/**
 * The context for sizing style values is the {@link ILayoutable}
 * 
 * @author Charles Bentley
 *
 */
public class StyleCache implements IStyle, ITechTblr, IBOTypesDrw {

   private int[]           areas;

   private int             flagsValidity;

   public static final int FLAG_1_CONTENT_W  = 1 << 0;

   public static final int FLAG_2_CONTENT_H  = 1 << 1;

   public static final int FLAG_3_WIDTH_LEFT = 1 << 2;

   public static final int FLAG_4_WIDTH_RITE = 1 << 2;

   public static final int FLAG_8_AREAS      = 1 << 7;

   private int             contentH;

   private int             contentW;

   private int             styleWLeftPixels;

   private int             styleHorizPixels;

   private int             styleVerticalPixels;

   private int             styleRightPixels;

   private int             styleTopPixels;

   protected boolean       isValid;

   private StyleOperator   styleOp;

   private ByteObject      style;

   private DrwCtx          dc;

   private ILayoutable     ctx;

   /**
    * 
    * @param gc
    * @param d
    * @param ctx
    */
   public StyleCache(DrwCtx dc, ILayoutable ctx, ByteObject style) {
      this.dc = dc;
      this.ctx = ctx;
      this.style = style;
   }
   public int[] getStyleAreas() {
      return areas;
   }
   public int[] getStyleAreas(int x, int y, int w, int h) {
      if (isValueInvalid(FLAG_3_WIDTH_LEFT)) {
         areas = styleOp.getStyleAreas(x, y, w, h, style, ctx);
      }
      return areas;
   }

   public void setNewStyle(ByteObject style) {
      isValid = false;
      this.style = style;
   }

   public boolean isValid() {
      return isValid;
   }

   public void invalidateValues() {
      flagsValidity = -1;
   }

   public void setValid() {
      isValid = true;
   }

   public boolean isValueInvalid(int flag) {
      //global disable of cache

      return BitUtils.hasFlag(flagsValidity, flag);
   }

   public int getStyleHConsumed() {
      return styleHorizPixels;
   }

   private void checkValid() {
      if (!isValid) {
         //compute everything
      }
   }

   public int getStyleWLeftConsumed() {
      if (isValueInvalid(FLAG_3_WIDTH_LEFT)) {
         styleWLeftPixels = styleOp.getStyleLeftWConsumed(style);
      }
      return styleWLeftPixels;
   }

   public int getStyleRightWConsumed() {
      if (isValueInvalid(FLAG_4_WIDTH_RITE)) {
         styleRightPixels = styleOp.getStyleRightWConsumed(style);
      }
      return styleRightPixels;
   }

   public int getStyleTopHConsumed() {
      return styleTopPixels;
   }

   public int getStyleWBorder() {
      return 0;
   }

   public int getStyleWMargin() {
      // TODO Auto-generated method stub
      return 0;
   }

   public int getStyleWPadding() {
      // TODO Auto-generated method stub
      return 0;
   }

   public int getStyleHTopConsumed() {
      // TODO Auto-generated method stub
      return 0;
   }

   public int getStyleHBotConsumed() {
      // TODO Auto-generated method stub
      return 0;
   }

   public int getStyleWConsumed() {
      // TODO Auto-generated method stub
      return 0;
   }

   public int getStyleWRightConsumed() {
      // TODO Auto-generated method stub
      return 0;
   }

   //#mdebug
   public IDLog toDLog() {
      return toStringGetUCtx().toDLog();
   }

   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, "StyleCache");
      toStringPrivate(dc);
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "StyleCache");
      toStringPrivate(dc);
   }

   public UCtx toStringGetUCtx() {
      return dc.getUCtx();
   }

   //#enddebug

}