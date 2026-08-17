package net.azureaaron.renderchest.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

/// Callback that is fired for each entity extracted for level rendering to optionally modify it's glowing effect.
///
/// The exact behaviour of the callback will vary as follows:
/// - **When an entity has no vanilla glow:** This will apply Render Chest's custom glow effect which does not show through walls.
/// - **When an entity has vanilla glow:** This will change the colour of the vanilla glow (and thus show through walls).
///
/// The colour returned by a listener must be in the ARGB format. Any colour with a non-opaque alpha value is considered
/// a reserved value by the implementation and may enable special behaviour.
///
/// Special Return Values:
/// - {@link GlowConstants#NO_GLOW}: return this as the colour if the entity should not have any glow applied.
/// - {@link GlowConstants#REMOVE_GLOW}: return this as the colour if the entity should be stripped of any glow it may have.
///
/// Aside from that it is up to users as to whether they want to perform any additional caching of whether an entity should glow or not.
@FunctionalInterface
public interface CustomGlowCallback {
	Event<CustomGlowCallback> EVENT = EventFactory.createArrayBacked(CustomGlowCallback.class, callbacks -> (entity, state) -> {
		for (CustomGlowCallback callback : callbacks) {
			int colour = callback.applyGlowColour(entity, state);

			if (colour != GlowConstants.NO_GLOW) {
				return colour;
			}
		}

		return GlowConstants.NO_GLOW;
	});

	int applyGlowColour(Entity entity, EntityRenderState state);
}
