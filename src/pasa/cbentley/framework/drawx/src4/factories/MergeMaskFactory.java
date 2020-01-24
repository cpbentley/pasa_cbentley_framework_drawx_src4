package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.tech.ITechMergeMask;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeMaskFigure;

public class MergeMaskFactory extends AbstractDrwFactory implements ITechMergeMask {

   public MergeMaskFactory(DrwCtx drc) {
      super(drc);
   }


   /**
    * Merge mask for {@link ITechMergeMaskFigure}
    * @return
    */
   public ByteObject getMergeMaskFigure() {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * Create a mask over a single flag.
    * <br>
    * <br>
    * root OVER merge => flagged in MM replace root
    * root IV merge => flagged in MM root replaced merge
    * <br>
    * <br>
    * {@link IBOTypesDrw#TYPE_MERGE_MASK}.
    * <br>
    * <br>
    * @param pointer
    * @param flag
    * @return
    * <br>
    * @see ByteObject
    */
   public ByteObject getMergeMask(int pointer, int flag) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesBOC.TYPE_011_MERGE_MASK, MERGE_MASK_BASIC_SIZE);
      p.setFlag(pointer, flag, true);
      return p;
   }

   /**
    * Sets the Incomplete Flag to true and adds the merge mask to the ByteObject array
    * @param mm
    * @param o
    */
   public void setMergeMask(ByteObject mm, ByteObject o) {
      o.setFlag(A_OBJECT_OFFSET_2_FLAG, A_OBJECT_FLAG_1_INCOMPLETE, true);
      o.addByteObject(mm);
   }

   /**
    * Gives a merge mask to {@link ByteObject} on flag pointer
    * <br>
    * <br>
    * @param obj
    * @param pointer
    * @param flag
    */
   public void setMergeMask(ByteObject obj, int pointer, int flag) {
      ByteObject mm = getMergeMask(pointer, flag);
      obj.addByteObject(mm);
      obj.setFlag(A_OBJECT_OFFSET_2_FLAG, A_OBJECT_FLAG_1_INCOMPLETE, true);
   }
   

   //#mdebug
   public String toString(ByteObject bo, String nl) {
      StringBBuilder sb = new StringBBuilder(drc.getUCtx());
      sb.append("#MergeMask");

      return sb.toString();
   }

   public void toString(Dctx dc, ByteObject bo) {
      dc.append("#MergeMask");
      toStringMMFlag(MERGE_MASK_OFFSET_1FLAG1, "Flag1", dc, bo);
      toStringMMFlag(MERGE_MASK_OFFSET_2FLAG1, "Flag2", dc, bo);
      toStringMMFlag(MERGE_MASK_OFFSET_3FLAG1, "Flag3", dc, bo);
      toStringMMFlag(MERGE_MASK_OFFSET_4FLAG1, "Flag4", dc, bo);
      toStringMMFlag(MERGE_MASK_OFFSET_5VALUES1, "Values1", dc, bo);
      toStringMMFlag(MERGE_MASK_OFFSET_6VALUES1, "Values2", dc, bo);
   }

   private void toStringMMFlag(int offsetFlag, String name, Dctx sb, ByteObject bo) {
      if (bo.get1(offsetFlag) != 0) {
         sb.append(name);
         sb.append(" =");
         for (int i = 0; i < 8; i++) {
            int flag = 1 << i;
            if (bo.hasFlag(offsetFlag, flag)) {
               sb.append(' ');
               sb.append(flag);
            }
         }
      }
   }
   //#enddebug

}
