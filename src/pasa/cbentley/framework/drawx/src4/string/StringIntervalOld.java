//package pasa.cbentley.framework.drawx.src4.string;
//
//import pasa.cbentley.byteobjects.src4.core.ByteObject;
//import pasa.cbentley.core.src4.logging.Dctx;
//import pasa.cbentley.core.src4.structs.IntBuffer;
//import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
//import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
//
///**
// * A {@link StringIntervalOld} has a {@link StringFx} overlay applied to all its {@link StringLeaf}.
// * <br>
// * 
// * Q: Is <b>public</b> in <code>public class ... </code> a StringInterval?<br>
// * A: It belongs to a {@link Stringer}
// * <br>
// * <br>
// * 
// * Q: What happens when you select characters from 2 different {@link StringIntervalOld}? <br>
// * A: You create new intervals and the select style is applied
// * <br>
// * <br>
// * 
// * A word in bold with have its own {@link StringIntervalOld}
// * 
// * {@link StringIntervalOld} is the user model, used to model 
// * {@link StringLeaf} is the code model used to draw
// * 
// * When a {@link StringIntervalOld} is changed, it modifies all the {@link StringLeaf} 
// * @author Charles Bentley
// *
// */
//public class StringIntervalOld extends ObjectDrw implements IBOFxStr {
//
//   /**
//    * Character based Fx for the {@link StringIntervalOld} characters.
//    * 
//    * When many different styles are used at the character level, the synthesis of merges is set here.
//    * <br>
//    * <br>
//    * The merge is done for line-word-char.
//    * Therefore char specific fx will always be highest priority.
//    * <br>
//    * 
//    */
//   StringFx[]               charFxs;
//
//   /**
//    * 
//    */
//   private StringFx         fx;
//
//   /**
//    * This array hosts the dynamic definition starting at index 1. Index 1 means the reset to default
//    * styles for the given interval in method {@link Stringer#setInterval(int, int, int)}.
//    * <br>
//    * <br>
//    * A dynamic style treats the interval as a String. Thus, such a text effect may have its own
//    * set of static text effects. For instance a special fx for the first and last index of the interval.
//    * <br>
//    * <br>
//    * 
//    * The anchor of additional styles are ignored. Whenever there is a conflict, common sense prevails and
//    * root text effects are applied.
//    * <br>
//    * <br>
//    * 
//    */
//   StringFx[]               fxsDynamic;
//
//   /**
//    * Definition of dynamic fxs.
//    */
//   ByteObject[]             fxsDynamicDefinition;
//
//   /**
//    * Array for the static fx that will be applied to characters/words using the rule based system for deciding 
//    * on what index a given static style is applied.
//    * <br>
//    * <br>
//    * <li>{@link IBOFxStr#FX_OFFSET_04_INDEX2}
//    * <li>{@link IBOFxStr#FX_OFFSET_05_INDEX_PATTERN1}
//    * <br>
//    * <br>
//    * Array never contains nulls.
//    * <br>
//    * <br>
//    * Static Fx applies to the same
//    * 
//    * Used for first and last letters fx.
//    * 
//    * Or doing 1 2 3, 1 2 3 patterns
//    */
//   StringFx[]               fxsStatic;
//
//   private StringFx[]       fxsStaticIndex;
//
//   private int              len;
//
//   private int              offset;
//
//   /**
//    * The {@link StringIntervalOld} in which
//    * this.offset < parent.offset
//    * this.len < parent.len
//    */
//   private StringIntervalOld   parent;
//
//   protected final Stringer st;
//
//   private int              states;
//
//   public StringIntervalOld(DrwCtx drc, Stringer st) {
//      super(drc);
//      this.st = st;
//
//   }
//
//   /**
//    * The {@link StringFx} for this interval of text
//    * @return
//    */
//   public StringFx getFx() {
//      return fx;
//   }
//
//   public int getLen() {
//      return len;
//   }
//
//   /**
//    * The offset in {@link Stringer} at which this {@link StringIntervalOld} starts.
//    * @return
//    */
//   public int getOffset() {
//      return offset;
//   }
//
//   public StringIntervalOld getParent() {
//      return parent;
//   }
//
// 
//
//   /**
//    * Reads the different {@link IBOFxStr} definitions and sort them.
//    * @param textFigure
//    * @param subs not null by contract size above 0 and elements inside are not null
//    */
//   public void initTextEffects(ByteObject textFigure, ByteObject[] subs) {
//      st.setState(ITechStringer.STATE_08_ACTIVE_STYLE, true);
//      int dsize = st.getNumDynamic(textFigure);
//      if (dsize > 0) {
//         fxsDynamic = new StringFx[dsize + 1];
//         fxsDynamic[0] = fx;
//         fxsDynamicDefinition = new ByteObject[dsize + 1];
//         intervals = new IntBuffer[1 + dsize];
//      }
//      int numIndexStatic = st.getNumStaticIndex(subs);
//      fxsStatic = new StringFx[subs.length - dsize - numIndexStatic];
//      fxsStaticIndex = new StringFx[numIndexStatic];
//      int countDynamic = 1;
//      int countStatic = 0;
//      int countStaticIndex = 0;
//      for (int i = 0; i < subs.length; i++) {
//         if (subs[i] != null) {
//            StringFx fxi = new StringFx(drc, st);
//            fxi.init(subs[i]);
//            if (subs[i].hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_2_DYNAMIC)) {
//               fxsDynamic[countDynamic] = fxi;
//               fxsDynamicDefinition[countDynamic] = subs[i];
//               intervals[countDynamic] = new IntBuffer(drc.getUCtx(), 4);
//               countDynamic++;
//            } else {
//               if (subs[i].hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_6_DEFINED_INDEX)) {
//                  fxsStaticIndex[countStaticIndex] = fxi;
//                  countStaticIndex++;
//               } else {
//                  fxsStatic[countStatic] = fxi;
//                  countStatic++;
//               }
//            }
//         }
//      }
//      //create the static final style that by default applies to all chars
//      for (int i = 0; i < fxsStatic.length; i++) {
//         fx = fx.add(fxsStatic[i]);
//      }
//      //create the index
//      for (int i = 0; i < fxsStaticIndex.length; i++) {
//         int scope = fxsStaticIndex[i].fxDefinition.get1(FX_OFFSET_04_TYPE_SCOPE1);
//         int index = fxsStaticIndex[i].fxDefinition.get2(FX_OFFSET_04_INDEX2);
//
//         if (scope == FX_SCOPE_0_CHAR) {
//            if (charFxs == null) {
//               charFxs = new StringFx[lengthChars];
//            }
//            //check index pattern? what if part of
//            charFxs[index] = fx.cloneMerge(fxsStaticIndex[i]);
//
//         } else if (scope == FX_SCOPE_1_WORD) {
//
//         } else if (scope == FX_SCOPE_2_LINE) {
//            if (lineFxs == null) {
//               lineFxs = new StringFx[getNumOfLines()];
//            }
//
//         }
//      }
//      if (countStaticIndex != 0) {
//         st.setState(ITechStringer.STATE_16_STATIC_INDEX_FX, true);
//      }
//      if (countDynamic == 0) {
//         st.setState(ITechStringer.STATE_10_ACTIVE_DYNAMIC_STYLE, false);
//      }
//      if (countStatic != 0) {
//         st.setState(ITechStringer.STATE_13_FX, true);
//      }
//      //from the definitions
//      st.setActiveFXs();
//   }
//
//   public void setLen(int len) {
//      this.len = len;
//   }
//
//   public void setOffset(int offset) {
//      this.offset = offset;
//   }
//
//   /**
//    * True when index is inside the {@link StringIntervalOld}
//    * @param index
//    * @return
//    */
//   public boolean isInside(int index) {
//      return offset <= index && (offset + len) < index;
//   }
//
//   public void setParent(StringIntervalOld parent) {
//      this.parent = parent;
//   }
//
//   //#mdebug
//   public void toString(Dctx dc) {
//      dc.root(this, StringIntervalOld.class, "@line5");
//      toStringPrivate(dc);
//      super.toString(dc.sup());
//   }
//
//   public void toString1Line(Dctx dc) {
//      dc.root1Line(this, StringIntervalOld.class);
//      toStringPrivate(dc);
//      super.toString1Line(dc.sup1Line());
//   }
//
//   private void toStringPrivate(Dctx dc) {
//
//   }
//
//   //#enddebug
//
//}
