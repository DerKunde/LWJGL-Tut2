package game;

import engine.GameItem;
import engine.IGameLogic;
import engine.MouseInput;
import engine.OBJLoader;
import engine.graph.*;
import engine.io.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class DummyGame implements IGameLogic {

    private final Vector3f cameraInc;

    private static final float CAMERA_POS_STEP = 0.05f;

    private static final float MOUSE_SENSITIVITY = 0.35f;

    private final Renderer renderer;

    private GameItem[] gameItems;

    private Camera camera;

    private PointLight pointLight;

    private Vector3f ambientLight;

    private DirectionalLight directionalLight;

    private float lightAngle;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f();
        lightAngle = -90;
    }

    @Override
    public void create(Window window) throws Exception {
        renderer.init(window);

        float reflec = 1f;
        Texture texture = new Texture("D:\\GameDev\\Haifish_Engine\\resources\\grassblock.png");
        Mesh mesh = OBJLoader.loadMesh("/resources/block.obj");
        Material material = new Material(texture, reflec);
        mesh.setMaterial(material);

        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0,0,-2);
        gameItems = new GameItem[] {
          gameItem
        };

        ambientLight = new Vector3f(0.3f,0.3f,0.3f);
        Vector3f lightColor = new Vector3f(1,1,1);
        Vector3f lightPosition = new Vector3f(0.2f,0.2f,1.5f);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColor, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f,0.0f,1.0f);
        pointLight.setAttenuation(att);

        lightPosition = new Vector3f(-1, 0, 0);
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0,0,0);
        if(window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if(window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }

        if(window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if(window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = 1;
        } else if(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            cameraInc.y = -1;
        }
        float lightPos = pointLight.getPosition().z;
        if (window.isKeyPressed(GLFW_KEY_N)) {
            this.pointLight.getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_M)) {
            this.pointLight.getPosition().z = lightPos - 0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
       camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
                           cameraInc.y * CAMERA_POS_STEP,
                           cameraInc.z * CAMERA_POS_STEP);

       if(mouseInput.isLeftBtnPressed()) {
           Vector2f rotVec = mouseInput.getDisplayVec();
           camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
       }

       //Update directional light direction, intensity and color
        lightAngle += 1.1f;
       if(lightAngle > 90) {
           directionalLight.setIntensity(0);
           if(lightAngle >= 360) {
               lightAngle = -90;
           }
       } else if(lightAngle <= -80 || lightAngle >= 80) {
           float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
           directionalLight.setIntensity(factor);
           directionalLight.getColor().y = Math.max(factor, 0.9f);
           directionalLight.getColor().z = Math.max(factor, 0.5f);
       } else {
           directionalLight.setIntensity(1);
           directionalLight.getColor().x = 1;
           directionalLight.getColor().y = 1;
           directionalLight.getColor().z = 1;
       }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanUp();
        for(GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}
