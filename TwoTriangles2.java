public class TwoTriangles2 {
       
  public static final float[] vertices = {      // position, colour, tex coords
        0.5f, 0.0f, 0.75f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // top left
        0.5f, 0.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
        -0.5f, 0.0f, 0.0f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // bottom right
        -0.5f, 0.0f, 0.75f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f   // top right
      };
      
      public static final int[] indices = {        
          0, 1, 2,
          0, 2, 3
      }; 
}
