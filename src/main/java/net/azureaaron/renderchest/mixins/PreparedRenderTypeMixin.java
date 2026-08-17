package net.azureaaron.renderchest.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.azureaaron.renderchest.impl.GlowDepthTextureManager;
import net.azureaaron.renderchest.impl.RenderChestPipelines;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;

@Mixin(PreparedRenderType.class)
abstract class PreparedRenderTypeMixin {
	@Shadow
	public abstract RenderPipeline pipeline();

	@ModifyExpressionValue(method = "drawFromBuffer(Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/IndexType;III)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;getDepthTextureView()Lcom/mojang/blaze3d/textures/GpuTextureView;"))
	private GpuTextureView renderChest$useGlowDepthTex(GpuTextureView original) {
		if (this.pipeline() == RenderChestPipelines.CUSTOM_OUTLINE_CULL || this.pipeline() == RenderChestPipelines.CUSTOM_OUTLINE_NO_CULL) {
			return GlowDepthTextureManager.INSTANCE.textureView();
		}

		return original;
	}
}
