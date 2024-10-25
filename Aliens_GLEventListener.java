import gmaths.*;

import java.nio.*;

import javax.swing.text.TabSet;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

public class Aliens_GLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;

  public Aliens_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(0f, 5f, 25f));
    // this.camera.setTarget(new Vec3(0f,0f,5f));
  }

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);
    gl.glEnable(GL.GL_CULL_FACE);
    gl.glCullFace(GL.GL_BACK);
    initialise(gl);
    startTime = getSeconds();
  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float) width / (float) height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    light2.dispose(gl);
    spotLight.dispose(gl);
    floor.dispose(gl);
    backdrop.dispose(gl);
    sphere.dispose(gl);
    textures.destroy(gl);
  }

  private boolean alienAnimation = false;
  private boolean alien2Animation = false;
  private boolean alien1HeadRoll = false;
  private boolean alien2HeadRoll = false;
  private boolean light1Switch = false;
  private boolean light2Switch = false;
  private boolean spotLightSwtich = false, spotRotate = false;
  private double savedTime = 0;

  public void startAlien1Rock() {
    if (!alienAnimation) {
      alienAnimation = true;
      rotateAllAngleStart = 30;
      rotateAllAngle = 0;
      startTime = getSeconds() - savedTime;
    } else {
      alienAnimation = false;
      rotateAllAngleStart = 0;
      rotateAllAngle = 0;
      savedTime = getSeconds();
      rockAlien1();
    }
  }

  public void startAlien2Rock() {
    if (!alien2Animation) {
      alien2Animation = true;
      rotateAllAngleStart2 = 30;
      rotateAllAngle2 = 0;
      startTime = getSeconds() - savedTime;
    } else {
      alien2Animation = false;
      rotateAllAngleStart2 = 0;
      rotateAllAngle2 = 0;
      savedTime = getSeconds();
      rockAlien2();
    }
  }

  public void startAlien1Roll() {
    if (!alien1HeadRoll) {
      alien1HeadRoll = true;
      rotateUpperAngle = 0;
      rotateUpperAngleStart = 20;
      startTime = getSeconds() - savedTime;
    } else {
      alien1HeadRoll = false;
      rotateUpperAngle = 0;
      rotateUpperAngleStart = 0;
      savedTime = getSeconds();
      rollAlien1();
    }
  }

  public void startAlien2Roll() {
    if (!alien2HeadRoll) {
      alien2HeadRoll = true;
      rotateUpperAngle2 = 0;
      rotateUpperAngleStart2 = 20;
      startTime = getSeconds() - savedTime;
    } else {
      alien2HeadRoll = false;
      rotateUpperAngle2 = 0;
      rotateUpperAngleStart2 = 0;
      savedTime = getSeconds();
      rollAlien2();
    }
  }

  public void lightSwitch() {
    if (light1Switch) {
      light1Switch = false;
    } else {
      light1Switch = true;
    }
  }

  public void light2Switch() {
    if (light2Switch) {
      light2Switch = false;
    } else {
      light2Switch = true;
    }
  }

  public void spotLightSwitch() {
    if (spotLightSwtich) {
      spotLightSwtich = false;
    } else {
      spotLightSwtich = true;
    }
  }

  public void rotateSpotLight() {
    if (!spotRotate) {
      spotRotate = true;
    } else {
      spotRotate = false;
    }
  }

  // textures
  private TextureLibrary textures;

  private Camera camera;
  private Mat4 perspective;
  private Model backdrop, sphere, floor;
  private Light light, light2, spotLight;
  private SGNode twoBranchRoot, lampRoot;

  private TransformNode translateXY, rotateAll, rotateUpper, translateHandX, translateRHandX, rotateLeftHand,
      rotateRightHand;
  private TransformNode translateLEar, translateREar, translateLEye, translateREye, translateAntenaBranch,
      translateAntena;
  private TransformNode translateXY2, rotateAll2, rotateUpper2, translateHandX2, translateRHandX2, rotateLeftHand2,
      rotateRightHand2;
  private TransformNode translateLEar2, translateREar2, translateLEye2, translateREye2, translateAntenaBranch2,
      translateAntena2, translateLamp;
  private TransformNode rotateLamp;
  private float xPosition = -2.5f, zPosition = 10;
  private float rotateAllAngleStart = 30, rotateAllAngle = 0;
  private float rotateUpperAngleStart = 20, rotateUpperAngle = 0;
  private float rotateAllAngleStart2 = 30, rotateAllAngle2 = 0;
  private float rotateUpperAngleStart2 = 20, rotateUpperAngle2 = 0;
  private float rotateHandAngleStart = 85, rotateHandAngle = rotateHandAngleStart;

  private void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    // textures.add(gl, "chequerboard", "textures/chequerboard.jpg");
    // textures.add(gl, "diffuse", "textures/jade.jpg");
    // textures.add(gl, "specular", "textures/jade_specular.jpg");
    // textures.add(gl, "wattbook", "textures/wattBook.jpg");
    textures.add(gl, "winter", "textures/winterVillage.jpg");
    textures.add(gl, "snow", "textures/snowOver.jpg");
    textures.add(gl, "ground", "textures/ground.jpg");

    // textures.add(gl, "aaa", "textures/ven0aaa2.jpg");
    textures.add(gl, "body1", "textures/image1.jpeg");
    textures.add(gl, "eyes", "textures/eyes1.jpg");
    textures.add(gl, "red", "textures/red.jpg");
    textures.add(gl, "body2", "textures/body2.jpg");
    textures.add(gl, "blue", "textures/blue.jpg");
    textures.add(gl, "black", "textures/black.jpg");
    textures.add(gl, "antena", "textures/antena.jpg");
    textures.add(gl, "pole", "textures/pole2.jpg");
    textures.add(gl, "eyes", "textures/eyes1.jpg");
    textures.add(gl, "rust", "textures/rust_texture.jpg");
    textures.add(gl, "dirt", "textures/dirt_texture.jpg");
    textures.add(gl, "scratch", "textures/scratch_texture.jpg");

    light = new Light(gl);
    light.setCamera(camera);

    light2 = new Light(gl);
    light2.setCamera(camera);

    spotLight = new Light(gl);
    spotLight.setPosition(-6.2f, 5.6f, 7.6f);
    spotLight.setCamera(camera);

    floor = makeFloor(gl, textures.get("ground"));
    backdrop = makeBackDrop(gl, textures.get("winter"), textures.get("snow"));
    sphere = makeSphere(gl, textures.get("scratch"));

    twoBranchRoot = new NameNode("two-branch structure");
    lampRoot = new NameNode("Lamp root");

    sphere = makeSphere(gl, textures.get("scratch"));
    SGNode lowerBranch = makeLowerBranch(sphere);
    SGNode upperBranch = makeUpperBranch(sphere);
    SGNode leftHand = makeLeftHand(sphere);
    SGNode rightHand = makeRightHand(sphere);
    sphere = makeSphere(gl, textures.get("eyes"));
    SGNode leftEar = makeLeftEar(sphere);
    SGNode rightEar = makeRightEar(sphere);
    sphere = makeSphere(gl, textures.get("red"));
    SGNode leftEye = makeLeftEye(sphere);
    SGNode rightEye = makeRightEye(sphere);

    sphere = makeSphere(gl, textures.get("blue"));
    SGNode atennaBranch = makeAttenaBranch(sphere);
    SGNode antena = makeAntena(sphere);

    sphere = makeSphere(gl, textures.get("dirt"));
    SGNode lowerBranch2 = makeLowerBranch(sphere);
    SGNode upperBranch2 = makeUpperBranch(sphere);
    SGNode leftHand2 = makeLeftHand(sphere);
    SGNode rightHand2 = makeRightHand(sphere);

    sphere = makeSphere(gl, textures.get("red"));
    SGNode leftEar2 = makeLeftEar(sphere);
    SGNode rightEar2 = makeRightEar(sphere);
    sphere = makeSphere(gl, textures.get("eyes"));
    SGNode leftEye2 = makeLeftEye(sphere);
    SGNode rightEye2 = makeRightEye(sphere);

    sphere = makeSphere(gl, textures.get("antena"));
    SGNode atennaBranch2 = makeAttenaBranch(sphere);
    SGNode antena2 = makeAntena(sphere);

    sphere = makeSphere(gl, textures.get("rust"));
    SGNode lampBase = makeLampBase(sphere);
    SGNode lamp = makeLamp(sphere);

    TransformNode translateToTop = new TransformNode("translate(0,4,0)", Mat4Transform.translate(0, 3.0f, 0));
    translateXY = new TransformNode("translate(" + xPosition + ",0,0)", Mat4Transform.translate(xPosition, 0, 10));
    rotateAll = new TransformNode("rotateAroundZ(" + rotateAllAngle + ")", Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper = new TransformNode("rotateAroundZ(" + rotateUpperAngle + ")",
        Mat4Transform.rotateAroundZ(rotateUpperAngle));
    translateHandX = new TransformNode("translateHand", Mat4Transform.translate(-1.2f, 1.8f, 0));
    rotateLeftHand = new TransformNode("rotateLeftHand", Mat4Transform.rotateAroundZ(50));
    translateRHandX = new TransformNode("translateHand", Mat4Transform.translate(1.2f, 1.8f, 0));
    rotateRightHand = new TransformNode("rotateRightHand", Mat4Transform.rotateAroundZ(-50));
    translateLEar = new TransformNode("translateLeftEar", Mat4Transform.translate(-0.75f, 0, 0));
    translateREar = new TransformNode("translateRightEar", Mat4Transform.translate(0.75f, 0, 0));
    translateLEye = new TransformNode("translateLeftEye", Mat4Transform.translate(-0.2f, 0.10f, 0.70f));
    translateREye = new TransformNode("translateRightEye", Mat4Transform.translate(0.3f, 0.10f, 0.70f));
    translateAntenaBranch = new TransformNode("translateAntenaBranch", Mat4Transform.translate(0, 0.85f, 0));
    translateAntena = new TransformNode("translateAntena", Mat4Transform.translate(0f, 0.2f, 0));

    TransformNode translateToTop2 = new TransformNode("translate(0,4,0)", Mat4Transform.translate(0, 3.0f, 0));
    translateXY2 = new TransformNode("translate(" + xPosition + ",0,0)", Mat4Transform.translate(3.5f, 0, 10));
    rotateAll2 = new TransformNode("rotateAroundZ(" + rotateAllAngle + ")",
        Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateUpper2 = new TransformNode("rotateAroundZ(" + rotateUpperAngle + ")",
        Mat4Transform.rotateAroundZ(rotateUpperAngle));
    translateHandX2 = new TransformNode("translateHand", Mat4Transform.translate(-1.2f, 1.8f, 0));
    rotateLeftHand2 = new TransformNode("rotateLeftHand", Mat4Transform.rotateAroundZ(50));
    translateRHandX2 = new TransformNode("translateHand", Mat4Transform.translate(1.2f, 1.8f, 0));
    rotateRightHand2 = new TransformNode("rotateRightHand", Mat4Transform.rotateAroundZ(-50));
    translateLEar2 = new TransformNode("translateLeftEar", Mat4Transform.translate(-0.75f, 0, 0));
    translateREar2 = new TransformNode("translateRightEar", Mat4Transform.translate(0.75f, 0, 0));
    translateLEye2 = new TransformNode("translateLeftEye", Mat4Transform.translate(-0.2f, 0.10f, 0.70f));
    translateREye2 = new TransformNode("translateRightEye", Mat4Transform.translate(0.3f, 0.10f, 0.70f));
    translateAntenaBranch2 = new TransformNode("translateAntenaBranch", Mat4Transform.translate(0, 0.85f, 0));
    translateAntena2 = new TransformNode("translateAntena", Mat4Transform.translate(0f, 0.2f, 0));
    translateLamp = new TransformNode("translateLamp", Mat4Transform.translate(-6.95f, 6.3f, 7.6f));

    rotateLamp = new TransformNode("rotateLamp", Mat4Transform.rotateAroundZ(-45));

    twoBranchRoot.addChild(translateXY);
    translateXY.addChild(rotateAll);
    rotateAll.addChild(lowerBranch);
    lowerBranch.addChild(rotateUpper);
    rotateUpper.addChild(translateToTop);
    translateToTop.addChild(upperBranch);
    upperBranch.addChild(translateLEar);
    translateLEar.addChild(leftEar);
    upperBranch.addChild(translateREar);
    translateREar.addChild(rightEar);
    upperBranch.addChild(translateLEye);
    translateLEye.addChild(leftEye);
    upperBranch.addChild(translateREye);
    translateREye.addChild(rightEye);
    upperBranch.addChild(translateAntenaBranch);
    translateAntenaBranch.addChild(atennaBranch);
    atennaBranch.addChild(translateAntena);
    translateAntena.addChild(antena);
    lowerBranch.addChild(translateHandX);
    translateHandX.addChild(rotateLeftHand);
    rotateLeftHand.addChild(leftHand);
    lowerBranch.addChild(translateRHandX);
    translateRHandX.addChild(rotateRightHand);
    rotateRightHand.addChild(rightHand);

    twoBranchRoot.addChild(translateXY2);
    translateXY2.addChild(rotateAll2);
    rotateAll2.addChild(lowerBranch2);
    lowerBranch2.addChild(rotateUpper2);
    rotateUpper2.addChild(translateToTop2);
    translateToTop2.addChild(upperBranch2);
    upperBranch2.addChild(translateLEar2);
    translateLEar2.addChild(leftEar2);
    upperBranch2.addChild(translateREar2);
    translateREar2.addChild(rightEar2);
    upperBranch2.addChild(translateLEye2);
    translateLEye2.addChild(leftEye2);
    upperBranch2.addChild(translateREye2);
    translateREye2.addChild(rightEye2);
    upperBranch2.addChild(translateAntenaBranch2);
    translateAntenaBranch2.addChild(atennaBranch2);
    atennaBranch2.addChild(translateAntena2);
    translateAntena2.addChild(antena2);
    lowerBranch2.addChild(translateHandX2);
    translateHandX2.addChild(rotateLeftHand2);
    rotateLeftHand2.addChild(leftHand2);
    lowerBranch2.addChild(translateRHandX2);
    translateRHandX2.addChild(rotateRightHand2);
    rotateRightHand2.addChild(rightHand2);
    twoBranchRoot.update();

    lampRoot.addChild(lampBase);
    lampBase.addChild(translateLamp);
    translateLamp.addChild(rotateLamp);
    rotateLamp.addChild(lamp);

    lampRoot.update();

  }

  // ----------------------- Alien Creation ------------------------------

  // the following two methods are quite similar and could be replaced with one
  // method with suitable parameterisation
  private SGNode makeLowerBranch(Model sphere) {
    NameNode lowerBranchName = new NameNode("lower branch");
    Mat4 m = Mat4Transform.scale(2.5f, 2.5f, 2.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
    TransformNode lowerBranch = new TransformNode("scale(2.5,2.5,2.5); translate(0,0.5,0)", m);
    ModelNode sphereNode = new ModelNode("Sphere(body)", sphere);
    lowerBranchName.addChild(lowerBranch);
    lowerBranch.addChild(sphereNode);
    return lowerBranchName;
  }

  private SGNode makeUpperBranch(Model sphere) {
    NameNode upperBranchName = new NameNode("upper branch");
    Mat4 m = Mat4Transform.scale(1.4f, 1.4f, 1.4f);
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0, 0));
    TransformNode upperBranch = new TransformNode("HeadBranch", m);
    ModelNode sphereNode = new ModelNode("Sphere(head)", sphere);
    upperBranchName.addChild(upperBranch);
    upperBranch.addChild(sphereNode);
    return upperBranchName;
  }

  private SGNode makeLeftHand(Model sphere) {
    NameNode leftHandName = new NameNode("left hand");
    Mat4 m = Mat4Transform.scale(0.25f, 1.5f, 0.25f);
    m = Mat4.multiply(m, Mat4Transform.translate(-1.6f, 0.25f, 0));
    TransformNode leftHand = new TransformNode("LeftHand", m);
    ModelNode sphereNode = new ModelNode("Sphere(LeftHand)", sphere);
    leftHandName.addChild(leftHand);
    leftHand.addChild(sphereNode);
    return leftHandName;
  }

  private SGNode makeRightHand(Model sphere) {
    NameNode leftHandName = new NameNode("right hand");
    Mat4 m = Mat4Transform.scale(0.25f, 1.5f, 0.25f);
    m = Mat4.multiply(m, Mat4Transform.translate(1.5f, 0.25f, 0));
    TransformNode leftHand = new TransformNode("RightHand", m);
    ModelNode sphereNode = new ModelNode("Sphere(RightHand)", sphere);
    leftHandName.addChild(leftHand);
    leftHand.addChild(sphereNode);
    return leftHandName;
  }

  private SGNode makeLeftEar(Model sphere) {
    NameNode leftEarName = new NameNode("left ear");
    Mat4 m = Mat4Transform.scale(0.10f, 1.0f, 0.15f);
    m = Mat4.multiply(m, Mat4Transform.translate(0f, 0.3f, 0));
    TransformNode leftEar = new TransformNode("scale(0.4f,2.4f,0.4f);translate(-2.5f,-0f,0)", m);
    ModelNode sphereNode = new ModelNode("Sphere(LeftEar)", sphere);
    leftEarName.addChild(leftEar);
    leftEar.addChild(sphereNode);
    return leftEarName;
  }

  private SGNode makeRightEar(Model sphere) {
    NameNode rightEarName = new NameNode("Right ear");
    Mat4 m = Mat4Transform.scale(0.10f, 1.0f, 0.15f);
    m = Mat4.multiply(m, Mat4Transform.translate(0f, 0.3f, 0));
    TransformNode rightEar = new TransformNode("scale(0.4f,2.4f,0.4f);translate(5.0f,-0.6f,0)", m);
    ModelNode sphereNode = new ModelNode("Sphere(RightEar)", sphere);
    rightEarName.addChild(rightEar);
    rightEar.addChild(sphereNode);
    return rightEarName;
  }

  private SGNode makeLeftEye(Model sphere) {
    NameNode leftEyeName = new NameNode("Left eye");
    Mat4 m = Mat4Transform.scale(0.25f, 0.25f, 0.25f);
    m = Mat4.multiply(m, Mat4Transform.translate(0f, 0.3f, 0));
    TransformNode leftEye = new TransformNode("LeftEye", m);
    ModelNode sphereNode = new ModelNode("Sphere(LeftEye)", sphere);
    leftEyeName.addChild(leftEye);
    leftEye.addChild(sphereNode);
    return leftEyeName;
  }

  private SGNode makeRightEye(Model sphere) {
    NameNode rightEyeName = new NameNode("Right eye");
    Mat4 m = Mat4Transform.scale(0.25f, 0.25f, 0.25f);
    m = Mat4.multiply(m, Mat4Transform.translate(0f, 0.3f, 0));
    TransformNode rightEye = new TransformNode("RightEye", m);
    ModelNode sphereNode = new ModelNode("Sphere(RightEye)", sphere);
    rightEyeName.addChild(rightEye);
    rightEye.addChild(sphereNode);
    return rightEyeName;
  }

  private SGNode makeAttenaBranch(Model sphere) {
    NameNode attenaBranchName = new NameNode("AttenaBranch");
    Mat4 m = Mat4Transform.scale(0.10f, 0.4f, 0.10f);
    m = Mat4.multiply(m, Mat4Transform.translate(0f, 0f, 0f));
    TransformNode attenaBranch = new TransformNode("AttenaBranch", m);
    ModelNode sphereNode = new ModelNode("Sphere(AttenaBranch)", sphere);
    attenaBranchName.addChild(attenaBranch);
    attenaBranch.addChild(sphereNode);
    return attenaBranchName;
  }

  private SGNode makeAntena(Model sphere) {
    NameNode antenaName = new NameNode("Antena");
    Mat4 m = Mat4Transform.scale(0.25f, 0.25f, 0.25f);
    m = Mat4.multiply(m, Mat4Transform.translate(0f, 0f, 0f));
    TransformNode antena = new TransformNode("Antena", m);
    ModelNode sphereNode = new ModelNode("Sphere(Attena)", sphere);
    antenaName.addChild(antena);
    antena.addChild(sphereNode);
    return antenaName;
  }

  // ---------------------------------- Creation of Alien done
  // -------------------------------------

  private SGNode makeLampBase(Model sphere) {
    NameNode lampBase = new NameNode("Lamp Base");
    Mat4 m = Mat4Transform.scale(0.5f, 6.5f, 0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(-14f, 0.5f, 15f));
    TransformNode lamp = new TransformNode("Lamp Base", m);
    ModelNode sphereNode = new ModelNode("Sphere(Lamp Base)", sphere);
    lampBase.addChild(lamp);
    lamp.addChild(sphereNode);
    return lampBase;
  }

  private SGNode makeLamp(Model sphere) {
    NameNode lampBase = new NameNode("Lamp");
    TransformNode lampTranslate = new TransformNode(" translate", Mat4Transform.translate(0, 6.3f, 0));
    Mat4 m = Mat4Transform.scale(2.0f, 0.5f, 0.5f);
    TransformNode lampTransform = new TransformNode("body transform", m);
    ModelNode sphereNode = new ModelNode("Sphere (body)", sphere);
    lampBase.addChild(lampTransform);
    lampTransform.addChild(sphereNode);
    sphereNode.addChild(lampTranslate);
    return lampBase;
  }

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    light.setPosition(getLightPosition());
    light2.setPosition(getLight2Position());

    if (alienAnimation) {
      rockAlien1();
    }
    if (alien2Animation) {
      rockAlien2();
    }
    if (alien1HeadRoll) {
      rollAlien1();
    }
    if (alien2HeadRoll) {
      rollAlien2();
    }
    if (!light1Switch) {
      light.setMaterial(
          new Material(new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f), 5.0f));
      light.render(gl);
    } else {
      light.setMaterial(new Material(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(0, 0, 0), 1.0f));
      light.render(gl);

    }

    if (!light2Switch) {
      light2.setMaterial(
          new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f), 5.0f));
      light2.render(gl);
    } else {
      light2.setMaterial(new Material(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(0, 0, 0), 1.0f));
      light2.render(gl);
    }

    if (!spotLightSwtich) {
      spotLight.setMaterial(
          new Material(new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f), 5.0f));
      spotLight.render(gl);
    } else {
      spotLight.setMaterial(new Material(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(0, 0, 0), 1.0f));
      spotLight.render(gl);
    }

    if (spotRotate) {
      updateLamp();
    }

    floor.render(gl);
    backdrop.setMove(gl, getSeconds() - startTime);
    backdrop.render(gl);

    twoBranchRoot.draw(gl);
    lampRoot.draw(gl);
  }

  // ----------------- Animate Aliens ------------------
  private void rockAlien1() {
    double elapsedTime = getSeconds() - startTime;
    rotateAllAngle = rotateAllAngleStart * (float) Math.sin(elapsedTime);
    rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));

    twoBranchRoot.update();
  }

  private void rockAlien2() {
    double elapsedTime = getSeconds() - startTime;
    rotateAllAngle2 = rotateAllAngleStart2 * (float) Math.sin(elapsedTime);
    rotateAll2.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle2));
    twoBranchRoot.update();
  }

  private void rollAlien1() {
    double elapsedTime = getSeconds() - startTime;
    rotateUpperAngle = rotateUpperAngleStart * (float) Math.sin(elapsedTime);
    rotateUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
    twoBranchRoot.update();
  }

  private void rollAlien2() {
    double elapsedTime = getSeconds() - startTime;
    rotateUpperAngle2 = rotateUpperAngleStart2 * (float) Math.sin(elapsedTime);
    rotateUpper2.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle2));
    twoBranchRoot.update();
  }

  // --------------------Animate alien Complete -----------------------------

  // --------------------------Move Spotlight------------------------------------
  private void updateLamp() {
    double elapsedTime = getSeconds() - startTime;
    float rotateLampAngle = 15 * (float) elapsedTime;
    Mat4 m = Mat4Transform.translate(0, 0, 0);
    m = m.multiply(Mat4Transform.rotateAroundZ(-45), m);
    m = m.multiply(Mat4Transform.rotateAroundY(rotateLampAngle), m);
    rotateLamp.setTransform(m);
    lampRoot.update();

  }

  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds() - startTime;
    float x = 6f;
    float y = 8.7f;
    float z = 20.0f;
    return new Vec3(x, y, z);
  }

  private Vec3 getLight2Position() {
    double elapsedTime = getSeconds() - startTime;
    // float x = 15.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    // float y = 2.7f;
    // float z = 15.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    float x = -2f;
    float y = 8.7f;
    float z = 20.0f;
    return new Vec3(x, y, z);
    // return new Vec3(5f,3.4f,5f);
  }

  private Vec3 getSpotlightPosition() {
    double elapsedTime = getSeconds() - startTime;
    float x = -7.5f * (float) (Math.sin(Math.toRadians(elapsedTime * 10)));
    float y = 6.5f;
    float z = -4f * (float) Math.cos(Math.toRadians(elapsedTime * 10));
    return new Vec3(x, y, z);
  }

  private Model makeFloor(GL3 gl, Texture t1) {
    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles2.vertices.clone(), TwoTriangles2.indices.clone());
    Shader shader = new Shader(gl, "vs_standard.txt", "shaders/fs_standard_m_1t.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f),
        new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16, 1f, 16);
    Light[] l = { light, light2, spotLight };
    Model floor = new Model(name, mesh, modelMatrix, shader, material, l, camera, t1);
    return floor;
  }

  private Model makeBackDrop(GL3 gl, Texture t1, Texture t2) {
    String name = "backdrop";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_T05.txt", "shaders/fs_standard_m_2t.txt");
    Material material = new Material(new Vec3(0.1f, 0.5f, 0.81f), new Vec3(0.32f, 0.5f, 0.81f),
        new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16, 1f, 16);
    Light[] l = { light, light2, spotLight };
    Model backdrop = new Model(name, mesh, modelMatrix, shader, material, l, camera, t1, t2);
    return backdrop;
  }

  private Model makeSphere(GL3 gl, Texture t1) {
    String name = "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "vs_standard.txt", "shaders/fs_standard_m_1t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f),
        new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4, 4, 4), Mat4Transform.translate(0, 0.5f, 0));
    Light[] l = { light, light2, spotLight };
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, l, camera, t1);
    return sphere;
  }

  private double startTime;

  private double getSeconds() {
    return System.currentTimeMillis() / 1000.0;
  }

  private int NUM_RANDOMS = 1000;
  private float[] randoms;

  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i = 0; i < NUM_RANDOMS; ++i) {
      randoms[i] = (float) Math.random();
    }
  }

}