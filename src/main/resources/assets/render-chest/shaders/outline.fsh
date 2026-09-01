#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:fog.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
	vec4 color = texture(Sampler0, texCoord0);
	float fogValue = total_fog_value(sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd);

	// When the outline is behind fog (vertexDistance >= fogEnd) the value is 1.0
	// so we want to discard (hide) it in that case
    if (color.a == 0.0 || fogValue == 1.0) {
		discard;
	}

	fragColor = vec4(ColorModulator.rgb * vertexColor.rgb, ColorModulator.a);
}
