package net.azureaaron.renderchest.mixins;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;

@Mixin(ItemFeatureRenderer.class)
class ItemFeatureRendererMixin {
	@ModifyExpressionValue(method = "prepareOutlineSubmit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderType;outline()Ljava/util/Optional;"))
	private Optional<RenderType> renderChest$useCustomOutlineRenderType(Optional<RenderType> original, ItemFeatureRenderer.Submit submit, @Local(name = "material") BakedQuad.MaterialInfo material) {
		if (submit.renderChest$hasCustomOutline()) {
			return material.itemRenderType().renderChest$getCustomOutlineRenderType();
		}

		return original;
	}
}
