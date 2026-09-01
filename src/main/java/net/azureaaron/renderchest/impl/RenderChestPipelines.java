package net.azureaaron.renderchest.impl;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;

import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;

public final class RenderChestPipelines {
	private static final RenderPipeline.Snippet CUSTOM_OUTLINE_SNIPPET = RenderPipeline.builder()
			.withVertexShader(RenderChest.id("outline"))
			.withFragmentShader(RenderChest.id("outline"))
			.withBindGroupLayout(BindGroupLayouts.FOG)
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			.buildSnippet();
	public static final RenderPipeline CUSTOM_OUTLINE_CULL = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.OUTLINE_SNIPPET, CUSTOM_OUTLINE_SNIPPET)
			.withLocation(RenderChest.id("custom_outline_cull"))
			.build());
	public static final RenderPipeline CUSTOM_OUTLINE_NO_CULL = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.OUTLINE_SNIPPET, CUSTOM_OUTLINE_SNIPPET)
			.withLocation(RenderChest.id("custom_outline_no_cull"))
			.withCull(false)
			.build());

	private RenderChestPipelines() {}
}
