package net.vulkanmod.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.vulkanmod.vulkan.Drawer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BufferRenderer.class)
public class BufferRendererM {

    /**
     * @author
     */
    @Overwrite
    public static void unbindAll() {}

    /**
     * @author
     */
    @Overwrite
    public static void drawWithShader(BufferBuilder.BuiltBuffer buffer) {
        RenderSystem.assertOnRenderThread();
        buffer.release();

        BufferBuilder.DrawArrayParameters parameters = buffer.getParameters();

        int glMode;
        switch (parameters.mode()) {
            case QUADS:
            case LINES:
                glMode = 7;
                break;
            case TRIANGLE_FAN:
                glMode = 6;
                break;
            case TRIANGLE_STRIP:
            case LINE_STRIP:
                glMode = 5;
                break;
            default:
                glMode = 4;
        }

//      Drawer.setModelViewMatrix(RenderSystem.getModelViewMatrix());
//      Drawer.setProjectionMatrix(RenderSystem.getProjectionMatrix());

        Drawer drawer = Drawer.getInstance();
        drawer.draw(buffer.getVertexBuffer(), glMode, parameters.format(), parameters.vertexCount());
    }


}
