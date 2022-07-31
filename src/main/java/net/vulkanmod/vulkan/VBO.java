package net.vulkanmod.vulkan;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.math.Matrix4f;
import net.vulkanmod.vulkan.memory.*;

import java.nio.ByteBuffer;

@Environment(EnvType.CLIENT)
public class VBO {
    private VertexBuffer vertexBuffer;
    private IndexBuffer indexBuffer;
    private VertexFormat.IndexType indexType;
    private int indexCount;
    private int vertexCount;
    private VertexFormat.DrawMode mode;
    private boolean sequentialIndices;
    private VertexFormat vertexFormat;

    private boolean autoIndexed = false;

    public VBO() {}

    public void upload_(BufferBuilder.BuiltBuffer buffer) {
        BufferBuilder.DrawArrayParameters parameters = buffer.getParameters();

        this.indexCount = parameters.indexCount();
        this.vertexCount = parameters.vertexCount();
        this.indexType = parameters.indexType();
        this.mode = parameters.mode();

        this.configureVertexFormat(parameters, buffer.getVertexBuffer());
        this.configureIndexBuffer(parameters, buffer.getIndexBuffer());

        buffer.release();

    }

    private VertexFormat configureVertexFormat(BufferBuilder.DrawArrayParameters parameters, ByteBuffer data) {
//        boolean bl = !parameters.format().equals(this.vertexFormat);
        if (!parameters.indexOnly()) {

            if(vertexBuffer == null) vertexBuffer = new VertexBuffer(data.remaining(), MemoryTypes.GPU_MEM);
            vertexBuffer.uploadWholeBuffer(data);
        }
        return parameters.format();
    }

    private void configureIndexBuffer(BufferBuilder.DrawArrayParameters parameters, ByteBuffer data) {
        if (parameters.sequentialIndex()) {
            AutoIndexBuffer autoIndexBuffer;
            if(this.mode != VertexFormat.DrawMode.TRIANGLE_FAN) {
                autoIndexBuffer = Drawer.getInstance().getQuadsIndexBuffer();
            } else {
                autoIndexBuffer = Drawer.getInstance().getTriangleFanIndexBuffer();
                this.indexCount = (vertexCount - 2) * 3;
            }

            if(indexBuffer != null && !this.autoIndexed) indexBuffer.freeBuffer();

            autoIndexBuffer.checkCapacity(vertexCount);
            indexBuffer = autoIndexBuffer.getIndexBuffer();
            this.autoIndexed = true;

            return;
        }

        if(indexBuffer == null) indexBuffer = new IndexBuffer(data.remaining(), MemoryTypes.GPU_MEM);
        indexBuffer.uploadWholeBuffer(data);
    }

    public void _drawWithShader(Matrix4f MV, Matrix4f P, Shader shader) {
        if (this.indexCount != 0) {
            RenderSystem.assertOnRenderThread();

            RenderSystem.setShader(() -> shader);

            VRenderSystem.applyMVP(MV, P);

            Drawer drawer = Drawer.getInstance();
            drawer.draw(vertexBuffer, indexBuffer, indexCount, mode.glMode);

            VRenderSystem.applyMVP(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix());

        }
    }

    public void drawChunkLayer() {
        if (this.indexCount != 0) {
            RenderSystem.assertOnRenderThread();
            Drawer drawer = Drawer.getInstance();
            drawer.drawIndexed(vertexBuffer, indexBuffer, indexCount);
        }
    }

    public void close() {
        if(vertexCount <= 0) return;
        vertexBuffer.freeBuffer();
        if(!autoIndexed) indexBuffer.freeBuffer();
    }

    public VertexFormat getFormat() {
        return this.vertexFormat;
    }

}
