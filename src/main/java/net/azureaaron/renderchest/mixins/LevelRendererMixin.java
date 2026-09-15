package net.azureaaron.renderchest.mixins;

import java.util.Optional;
import java.util.OptionalDouble;

import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.commands.RenderPass;

import net.azureaaron.renderchest.impl.RenderStateKeys;
import net.azureaaron.renderchest.impl.injected.EntitySubmitMarker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher.PreparedFrame;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;

@Mixin(LevelRenderer.class)
class LevelRendererMixin implements EntitySubmitMarker {
	@Shadow
	@Final
	private static Vector4fc ZERO_CLEAR_COLOR;
	@Shadow
	@Final
	private LevelRenderState levelRenderState;
	@Shadow
	@Final
	private RenderTarget entityOutlineTarget;
	@Shadow
	@Final
	private LevelTargetBundle targets;
	@Unique
	private @Nullable EntityRenderState currentStateBeingRendered = null;
	@Unique
	private @Nullable RenderPass newSolidPass = null;

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

	@Inject(method = "executeSolid", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;renderGroup(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/textures/GpuSampler;Lcom/mojang/renderpearl/api/textures/GpuTextureView;Z)V", shift = At.Shift.AFTER))
	private void renderChest$executeCustomOutline(CallbackInfo ci, @Local(name = "featureFrame") PreparedFrame featureFrame, @Local(name = "renderPass") LocalRef<RenderPass> solidPass, @Local(name = "profiler") ProfilerFiller profiler) {
		if (this.levelRenderState.getDataOrDefault(RenderStateKeys.FRAME_USES_CUSTOM_GLOW, false)) {
			// Close previous pass
			solidPass.get().close();
			profiler.popPush("renderChest$customOutline");

			CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
			RenderTarget mainTarget = this.targets.main.get();

			// Execute the custom outline pass just after solid and cutout chunk contents are rendered
			try (RenderPass outlinePass = commandEncoder
					.createRenderPass(
							() -> "Render Chest Custom Outline",
							this.entityOutlineTarget.getColorTextureView(),
							Optional.of(ZERO_CLEAR_COLOR),
							mainTarget.getDepthTextureView(),
							OptionalDouble.empty()
							)
					) {
				RenderSystem.bindDefaultUniforms(outlinePass);
				featureFrame.renderChest$executeCustomOutline(outlinePass);
			}

			// Open a new the solid pass for the rest of the stuff
			this.newSolidPass = commandEncoder
					.createRenderPass(
							() -> "Solid #2",
							mainTarget.getColorTextureView(),
							Optional.empty(),
							mainTarget.getDepthTextureView(),
							OptionalDouble.empty()
							);
			solidPass.set(this.newSolidPass);
		}
	}

	@Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;executeSolid(Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;Lcom/mojang/renderpearl/api/commands/RenderPass;)V", shift = At.Shift.AFTER))
	private void renderChest$updateGlowDepthTexDepth(CallbackInfo ci, @Local(name = "renderPass") LocalRef<RenderPass> renderPass) {
		if (this.newSolidPass != null) {
			renderPass.set(this.newSolidPass);
		}
	}

	@ModifyArg(method = "executeOutline", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/api/commands/CommandEncoder;createRenderPass(Ljava/util/function/Supplier;Lcom/mojang/renderpearl/api/textures/GpuTextureView;Ljava/util/Optional;Lcom/mojang/renderpearl/api/textures/GpuTextureView;Ljava/util/OptionalDouble;)Lcom/mojang/renderpearl/api/commands/RenderPass;"))
	private Optional<Vector4fc> renderChest$replaceOutlinePassClearColour(Optional<Vector4fc> original) {
		// If the custom outline applies to this frame then the colour clear already happened so we'd want to skip it otherwise the custom glow gets overwritten
		return this.newSolidPass != null ? Optional.empty() : original;
	}

	@Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;executeOutline(Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;)V", shift = At.Shift.AFTER))
	private void renderChest$reseNewSolidPassField(CallbackInfo ci) {
		this.newSolidPass = null;
	}
}
