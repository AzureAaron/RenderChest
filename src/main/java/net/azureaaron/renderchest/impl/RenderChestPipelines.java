package net.azureaaron.renderchest.impl;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;

import net.minecraft.client.renderer.RenderPipelines;

public final class RenderChestPipelines {
	public static final RenderPipeline CUSTOM_OUTLINE_CULL = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.OUTLINE_SNIPPET)
			.withLocation(RenderChest.id("custom_outline_depth_cull"))
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			.build());
	public static final RenderPipeline CUSTOM_OUTLINE_NO_CULL = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.OUTLINE_SNIPPET)
			.withLocation(RenderChest.id("custom_outline_depth_no_cull"))
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			.withCull(false)
			.build());

	private RenderChestPipelines() {}
}
