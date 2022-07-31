package net.vulkanmod.mixin.render;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Shader;
import net.minecraft.util.math.Matrix4f;
import net.vulkanmod.vulkan.VBO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexBuffer.class)
public class VertexBufferM {

    private VBO vbo;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(CallbackInfo ci) {
        vbo = new VBO();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glGenBuffers()I"))
    private int doNothing() {
        return 0;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glGenVertexArrays()I"))
    private int doNothing2() {
        return 0;
    }

    /**
     * @author
     */
    @Overwrite
    public void bind() {}

    /**
     * @author
     */
    @Overwrite
    public static void unbind() {}

    /**
     * @author
     */
    @Overwrite
    public void upload(BufferBuilder.BuiltBuffer buffer) {
        vbo.upload_(buffer);
    }

    /**
     * @author
     */
    @Overwrite
    public void drawInternal(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
        vbo._drawWithShader(viewMatrix, projectionMatrix, shader);
    }

    /**
     * @author
     */
    @Overwrite
    public void drawElements() {
        vbo.drawChunkLayer();
    }

    /**
     * @author
     */
    @Overwrite
    public void close() {
        vbo.close();
    }
}
