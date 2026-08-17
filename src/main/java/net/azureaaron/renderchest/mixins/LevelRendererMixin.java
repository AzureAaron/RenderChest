package net.azureaaron.renderchest.mixins;

import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.renderchest.impl.GlowDepthTextureManager;
import net.azureaaron.renderchest.impl.RenderStateKeys;
import net.azureaaron.renderchest.impl.injected.EntitySubmitMarker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.LevelRenderState;

@Mixin(LevelRenderer.class)
class LevelRendererMixin implements EntitySubmitMarker {
	@Shadow
	@Final
	private LevelRenderState levelRenderState;
	@Unique
	private @Nullable EntityRenderState currentStateBeingRendered = null;

	@Override
	public @Nullable EntityRenderState renderChest$getEntityStateBeingSubmitted() {
		return this.currentStateBeingRendered;
	}

	@Inject(method = "submitEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
	private void renderChest$markEntityStateBeingRendered(CallbackInfo ci, @Local(name = "state") EntityRenderState state) {
		this.currentStateBeingRendered = state;
	}

	@Inject(method = "submitEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V", shift = At.Shift.AFTER))
	private void renderChest$clearEntityStateBeingRendered(CallbackInfo ci) {
		this.currentStateBeingRendered = null;
	}

	@Inject(method = "submitFeatures",
			slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/SubmitNodeCollection;outline:Lnet/minecraft/client/renderer/feature/phase/SimpleFeatureRenderPhase;", opcode = Opcodes.GETFIELD)),
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/phase/SimpleFeatureRenderPhase;clear()V", ordinal = 0)
	)
	private void renderChest$clearCustomOutlineIfAllOutlinesDisabled(CallbackInfo ci, @Local(name = "collection") SubmitNodeCollection collection) {
		collection.renderChest$getCustomOutlinePhase().clear();
	}

	@Inject(method = "lambda$addMainPass$0",
			slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/LevelRenderState;shouldShowEntityOutlines:Z", opcode = Opcodes.GETFIELD)),
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;Lorg/joml/Vector4fc;Lcom/mojang/blaze3d/textures/GpuTexture;D)V", ordinal = 0, shift = At.Shift.AFTER)
	)
	private void renderChest$updateGlowDepthTexDepth(CallbackInfo ci) {
		if (this.levelRenderState.getDataOrDefault(RenderStateKeys.FRAME_USES_CUSTOM_GLOW, false)) {
			GlowDepthTextureManager.INSTANCE.update();
		}
	}

	@Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;executeOutline()V"))
	private void renderChest$executeCustomOutline(CallbackInfo ci, @Local(name = "featureFrame") FeatureRenderDispatcher.PreparedFrame featureFrame) {
		featureFrame.renderChest$executeCustomOutline();
	}
}
