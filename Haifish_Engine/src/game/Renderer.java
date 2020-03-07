package game;

import engine.GameItem;
import engine.Utils;
import engine.graph.*;
import engine.io.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class Renderer {

    private ShaderProgram shaderProgram;

    /**
     * Field of View in Radians
     */

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.0f;

    private Matrix4f projectionMatrix;

    private Transformation transformation;

    private float specularPower;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {

        //Create Shaders
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource(new String("/resources/vertex.vs.glsl")));
        shaderProgram.createFragmentShader(Utils.loadResource(new String("/resources/fragment.fs.glsl")));
        shaderProgram.link();

        //Create projectionMatrix, modelView Matrix an texture
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");

        //Create uniforms for Material
        shaderProgram.createMaterialUniform("material");

        //Create lighting uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLight("directionalLight");
    }

    public void render(Window window, Camera camera, GameItem[] gameItems, Vector3f ambientLight, PointLight pointLight, DirectionalLight dirLight) {
        clear();

        if(window.isResized()) {
            glViewport(0,0,window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        shaderProgram.bind();

        //Update Projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        //Update View Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        //Update Light Uniforms
        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);
        //Transform LightPos Coords in View Coords
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos,1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.setUniform("pointLight", currPointLight);

        //Transform dirLight to View Coords
        DirectionalLight currDirLight = new DirectionalLight(dirLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirLight);


        shaderProgram.setUniform("texture_sampler", 0);

        for(GameItem gameItem : gameItems) {
            Mesh mesh = gameItem.getMesh();

            //Set world Matrix for Item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

            shaderProgram.setUniform("material", mesh.getMaterial());

            //Render Mesh from gameItem
            mesh.render();
        }
        shaderProgram.unbind();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {
        if(shaderProgram != null) {
            shaderProgram.cleanup();
        }

    }
}
