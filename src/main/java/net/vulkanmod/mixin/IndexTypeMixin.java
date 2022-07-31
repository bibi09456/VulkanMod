package net.vulkanmod.mixin;

import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(VertexFormat.IndexType.class)
public class IndexTypeMixin {

    /**
     * @author
     */
    @Overwrite
    public static VertexFormat.IndexType smallestFor(int number) {
        return VertexFormat.IndexType.SHORT;
    }
}