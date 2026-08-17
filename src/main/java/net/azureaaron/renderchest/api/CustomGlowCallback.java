package net.azureaaron.renderchest.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

/// Callback that is fired for each entity extracted for level rendering to optionally modify it's glowing effect.
///
/// The colour returned by a listener must be in the ARGB or RGB format. Return {@link EntityRenderState#NO_OUTLINE} as the
/// colour if the entity should not have any glow applied by listener.
///
/// The exact behaviour of the callback will vary as follows:
/// - **When an entity has no vanilla glow:** This will apply Render Chest's custom glow effect which does not show through walls.
/// - **When an entity has vanilla glow:** This will change the colour of the vanilla glow (and thus show through walls).
///
/// Aside from that it is up to users as to whether they want to perform any additional caching of whether an entity should glow or not.
@FunctionalInterface
public interface CustomGlowCallback {
	Event<CustomGlowCallback> EVENT = EventFactory.createArrayBacked(CustomGlowCallback.class, callbacks -> (entity, state) -> {
		for (CustomGlowCallback callback : callbacks) {
			int colour = callback.applyGlowColour(entity, state);

			if (colour != EntityRenderState.NO_OUTLINE) {
				return colour;
			}
		}

		return EntityRenderState.NO_OUTLINE;
	});

	int applyGlowColour(Entity entity, EntityRenderState state);
}
