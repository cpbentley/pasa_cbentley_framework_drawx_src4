/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;

public interface ITechArtifact extends ITechByteObject {

   /**
    * 1 byte flag
    * 1 byte w
    * 1 byte h
    * 1 byte spacing
    * 1 byte type
    */
   public static final int ARTIFACT_BASIC_SIZE           = A_OBJECT_BASIC_SIZE + 5;

   /**
    * 
    */
   public static final int ARTIFACT_FLAG_1RANDOM_W       = 1;

   public static final int ARTIFACT_FLAG_2RANDOM_H       = 2;

   public static final int ARTIFACT_FLAG_3RANDOM_SPACING = 4;

   public static final int ARTIFACT_FLAG_8NO_TRIANGLES   = 128;

   public static final int ARTIFACT_OFFSET_1FLAG         = A_OBJECT_BASIC_SIZE;

   public static final int ARTIFACT_OFFSET_2W1           = A_OBJECT_BASIC_SIZE + 1;

   public static final int ARTIFACT_OFFSET_3H1           = A_OBJECT_BASIC_SIZE + 2;

   public static final int ARTIFACT_OFFSET_4SPACING_CAP1 = A_OBJECT_BASIC_SIZE + 3;

   public static final int ARTIFACT_OFFSET_5TYPE1        = A_OBJECT_BASIC_SIZE + 4;

}
