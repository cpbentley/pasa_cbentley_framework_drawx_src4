/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;

/**
 * 
 * @author Charles Bentley
 *
 */
public interface IBOArtifact extends IByteObject {

   /**
    * 1 byte flag
    * 1 byte w
    * 1 byte h
    * 1 byte spacing
    * 1 byte type
    */
   public static final int ARTIFACT_BASIC_SIZE             = A_OBJECT_BASIC_SIZE + 10;

   /**
    * 
    */
   public static final int ARTIFACT_FLAG_1_RANDOM_W        = 1 << 0;

   public static final int ARTIFACT_FLAG_2_RANDOM_H        = 1 << 1;

   public static final int ARTIFACT_FLAG_3_RANDOM_SPACING  = 1 << 2;

   public static final int ARTIFACT_FLAG_4_RANDOM_FIG      = 1 << 3;

   public static final int ARTIFACT_FLAG_8_NO_TRIANGLES    = 1 << 7;

   public static final int ARTIFACT_OFFSET_01_FLAG         = A_OBJECT_BASIC_SIZE;

   public static final int ARTIFACT_OFFSET_02_W1           = A_OBJECT_BASIC_SIZE + 1;

   public static final int ARTIFACT_OFFSET_03_H1           = A_OBJECT_BASIC_SIZE + 2;

   public static final int ARTIFACT_OFFSET_04_SPACING_CAP1 = A_OBJECT_BASIC_SIZE + 3;

   public static final int ARTIFACT_OFFSET_05_TYPE1        = A_OBJECT_BASIC_SIZE + 4;

   /**
    * 
    */
   public static final int ARTIFACT_OFFSET_06_FIG1         = A_OBJECT_BASIC_SIZE + 5;

   /**
    * When zero, use the random seed
    */
   public static final int ARTIFACT_OFFSET_07_SEED4        = A_OBJECT_BASIC_SIZE + 6;

}
