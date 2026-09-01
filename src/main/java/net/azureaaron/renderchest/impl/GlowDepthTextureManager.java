package net.azureaaron.renderchest.impl;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassDescriptor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.Minecraft;

public final class GlowDepthTextureManager implements AutoCloseable {
	public static final GlowDepthTextureManager INSTANCE = new GlowDepthTextureManager();
	private final Minecraft minecraft;
	private @Nullable GpuTexture texture;
	private @Nullable GpuTextureView textureView;

	private GlowDepthTextureManager() {
		this.minecraft = Minecraft.getInstance();
	}

	public GpuTextureView textureView() {
		// It must have been initialized before this point so hard assert it to be non-null
		return Objects.requireNonNull(this.textureView);
	}

	public void update() {
		GpuDevice device = RenderSystem.getDevice();
		RenderTarget mainTarget = this.minecraft.gameRenderer.mainRenderTarget();
		GpuTextureView mainDepthTexture = Objects.requireNonNull(mainTarget.getDepthTextureView());
		int neededWidth = mainDepthTexture.getWidth(0);
		int neededHeight = mainDepthTexture.getHeight(0);

		// Update the texture if it needs resizing or creating
		if ((this.texture == null && this.textureView == null) || this.texture.getWidth(0) != neededWidth || this.texture.getHeight(0) != neededHeight) {
			// Close textures if they exist
			if (this.texture != null && this.textureView != null) {
				this.textureView.close();
				this.texture.close();
			}

			this.texture = device.createTexture("Render Chest Glow Depth Tex", GpuTexture.USAGE_RENDER_ATTACHMENT | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST, GpuFormat.D32_FLOAT, neededWidth, neededHeight, 1, 1);
			this.textureView = device.createTextureView(this.texture);
		}

		// Blit the depth from the main depth texture (uses a blit pass since some drivers don't work with copyTextureToTexture)
		RenderPassDescriptor descriptor = RenderPassDescriptor.create(() -> "Render Chest depth blit")
				// I don't want or need this colour attachment but I'm forced to have it :(
				.withColorAttachment(mainTarget.getColorTextureView())
				.withDepthAttachment(this.textureView)
				.withRenderArea(new RenderPass.RenderArea(0, 0, neededWidth, neededHeight));
		GpuSampler nearestSampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);

		try (RenderPass renderPass = device.createCommandEncoder().createRenderPass(descriptor)) {
			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setPipeline(RenderChestPipelines.BLIT_DEPTH);
			renderPass.bindTexture("InSampler", mainDepthTexture, nearestSampler);
			renderPass.draw(3, 1, 0, 0);
		}
	}

	@Override
	public void close() {
		if (this.texture != null && this.textureView != null) {
			this.texture.close();
			this.textureView.close();
		}
	}
}
