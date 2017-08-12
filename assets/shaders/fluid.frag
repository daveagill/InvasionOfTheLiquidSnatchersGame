varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

const float threshold = 0.6;
const float window = 0.1;

void main()
{
	vec4 col = texture2D(u_texture, v_texCoords);
	float field = col.a;
	col.a = smoothstep(threshold - window, threshold + window, field);
	gl_FragColor = col;
}