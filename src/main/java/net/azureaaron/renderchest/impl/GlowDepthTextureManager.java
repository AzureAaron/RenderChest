package net.azureaaron.renderchest.impl;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
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
		GpuTexture mainDepthTexture = Objects.requireNonNull(this.minecraft.gameRenderer.mainRenderTarget().getDepthTexture());
		int neededWidth = this.minecraft.getWindow().getWidth();
		int neededHeight = this.minecraft.getWindow().getHeight();

		// Update the texture if it needs resizing or creating
		if ((this.texture == null && this.textureView == null) || this.texture.getWidth(0) != neededWidth || this.texture.getHeight(0) != neededHeight) {
			// Close textures if they exist
			if (this.texture != null && this.textureView != null) {
				this.textureView.close();
				this.texture.close();
			}

			// RenderTarget
			this.texture = device.createTexture("Render Chest Glow Depth Tex", GpuTexture.USAGE_RENDER_ATTACHMENT | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST, GpuFormat.D32_FLOAT, neededWidth, neededHeight, 1, 1);
			this.textureView = device.createTextureView(this.texture);
		}

		// Copy the depth from the main depth texture
		device.createCommandEncoder().copyTextureToTexture(mainDepthTexture, this.texture, 0, 0, 0, 0, 0, neededWidth, neededHeight);
	}

	@Override
	public void close() {
		if (this.texture != null && this.textureView != null) {
			this.texture.close();
			this.textureView.close();
		}
	}
}
