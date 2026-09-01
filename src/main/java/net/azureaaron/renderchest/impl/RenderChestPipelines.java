package net.azureaaron.renderchest.impl;

import java.util.Optional;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;

import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;

public final class RenderChestPipelines {
	public static final RenderPipeline BLIT_DEPTH = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
			.withLocation(RenderChest.id("blit_depth"))
			.withVertexShader("core/screenquad")
			.withFragmentShader(RenderChest.id("blit_depth"))
			.withBindGroupLayout(BindGroupLayouts.IN_SAMPLER)
			.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
			// I don't actually want this but 26.2 doesn't let you have no colour target :(
			.withColorTargetState(new ColorTargetState(Optional.empty(), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_NONE))
			.withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
			.build());
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
