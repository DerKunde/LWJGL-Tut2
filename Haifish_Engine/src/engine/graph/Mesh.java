package engine.graph;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Mesh {

    private static final Vector3f DEFAULT_COLOR = new Vector3f(0.7f, 0.7f, 0.4f);

    private final int vaoID;

    private final List<Integer> vboIdList;

    private final int vertexCount;

    private Material material;

    public Mesh(float[] positions, int[] indices, float[] textCoord, float[] normals) {
        FloatBuffer posBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer textureBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        System.out.println("Mesh create!");
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<>();

            vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);

            // Position VBO
            int vboID = glGenBuffers();
            vboIdList.add(vboID);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            memFree(posBuffer);

            // Index VBO
            vboID = glGenBuffers();
            vboIdList.add(vboID);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
            memFree(indicesBuffer);

            // Texture VBO
            vboID = glGenBuffers();
            vboIdList.add(vboID);
            textureBuffer = MemoryUtil.memAllocFloat(textCoord.length);
            textureBuffer.put(textCoord).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1,2,GL_FLOAT, false, 0,0);

            // Vertex normals VBO
            vboID = glGenBuffers();
            vboIdList.add(vboID);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

//            // Color VBO
//            colVboID = glGenBuffers();
//            colorBuffer = MemoryUtil.memAllocFloat(color.length);
//            colorBuffer.put(color).flip();
//            glBindBuffer(GL_ARRAY_BUFFER, colVboID);
//            glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
//            glEnableVertexAttribArray(1);
//            glVertexAttribPointer(1,3,GL_FLOAT, false, 0,0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if(posBuffer != null) {
                memFree(posBuffer);
            }
            if(indicesBuffer != null) {
                memFree(indicesBuffer);
            }
            if(textureBuffer != null) {
                memFree(textureBuffer);
            }
            if(vecNormalsBuffer != null) {
                memFree(vecNormalsBuffer);
            }
        }
    }

    public void render() {
        //Activate texture

            Texture texture = material.getTexture();
            if (texture != null) {
                glActiveTexture(GL_TEXTURE0);
                //Bind Texture
                glBindTexture(GL_TEXTURE_2D, texture.getID());
            }



        //Draw Mesh
        glBindVertexArray(getVaoID());

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        //Clear Buffer
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        //Delete VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for(int vboID : vboIdList) {
            glDeleteBuffers(vboID);
        }

        //Delete Texture
        if(material != null) {
            Texture texture = material.getTexture();
            if(texture != null) {
                texture.cleanUp();
            }
        }

        //Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
    }
}
