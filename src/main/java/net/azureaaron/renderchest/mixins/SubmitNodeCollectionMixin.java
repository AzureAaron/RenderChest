package net.azureaaron.renderchest.mixins;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

import net.azureaaron.renderchest.impl.RenderStateKeys;
import net.azureaaron.renderchest.impl.injected.CustomOutlinePhaseHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;

@Mixin(SubmitNodeCollection.class)
class SubmitNodeCollectionMixin implements CustomOutlinePhaseHolder {
	@Unique
	private final SimpleFeatureRenderPhase customOutline = new SimpleFeatureRenderPhase();

	@ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;of([Ljava/lang/Object;)Ljava/util/List;"))
	private List<FeatureRenderPhase<?>> renderChest$addCustomOutlinePhaseToList(List<FeatureRenderPhase<?>> original) {
		List<FeatureRenderPhase<?>> modified = new ArrayList<>(original);
		modified.add(this.customOutline);

		return List.copyOf(modified);
	}

	@Inject(method = "submitModel", at = @At("RETURN"))
	private <S> void renderChest$glowModel(CallbackInfo ci, @Local(name = "model") Model<? super S> model, @Local(name = "state") S state, @Local(name = "renderType") RenderType renderType, @Local(name = "sprite") TextureAtlasSprite sprite, @Local(name = "pose") PoseStack.Pose pose) {
		int customGlowColour = getCustomGlowColour();

		if (customGlowColour != EntityRenderState.NO_OUTLINE) {
			RenderType outlineRenderType = getCustomGlowRenderType(renderType);

			if (outlineRenderType != null) {
				this.customOutline.submit(new ModelFeatureRenderer.Submit<>(outlineRenderType, pose, model, state, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, customGlowColour, sprite, null));
			}
		}
	}

	@Inject(method = "submitItem", at = @At("RETURN"))
	private void renderChest$glowItem(CallbackInfo ci, @Local(name = "displayContext") ItemDisplayContext displayContext, @Local(name = "quads") List<BakedQuad> quads, @Local(name = "pose") PoseStack.Pose pose) {
		int customGlowColour = getCustomGlowColour();

		if (customGlowColour != EntityRenderState.NO_OUTLINE) {
			ItemFeatureRenderer.Submit submit = new ItemFeatureRenderer.Submit(pose, displayContext, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, customGlowColour, ItemStackRenderState.LayerRenderState.EMPTY_TINTS, quads, ItemStackRenderState.FoilType.NONE);
			submit.renderChest$setHasCustomOutline();

			this.customOutline.submit(submit);
		}
	}

	@Unique
	private static int getCustomGlowColour() {
		EntityRenderState entityStateBeingSubmitted = Minecraft.getInstance().levelRenderer.renderChest$getEntityStateBeingSubmitted();

		return entityStateBeingSubmitted != null ? entityStateBeingSubmitted.getDataOrDefault(RenderStateKeys.ENTITY_CUSTOM_GLOW_COLOUR, EntityRenderState.NO_OUTLINE) : EntityRenderState.NO_OUTLINE;
	}

	@Unique
	private static RenderType getCustomGlowRenderType(RenderType renderType) {
		return renderType.isOutline() ? null : renderType.renderChest$getCustomOutlineRenderType().orElse(null);
	}

	@Override
	public SimpleFeatureRenderPhase renderChest$getCustomOutlinePhase() {
		return this.customOutline;
	}
}
