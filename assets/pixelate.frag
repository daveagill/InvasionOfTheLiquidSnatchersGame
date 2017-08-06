varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

const float vx_offset = 0.5;
const float rt_w = 800.0;
const float rt_h = 600.0;
const float pixel_w = 15.0;
const float pixel_h = 10.0;

void main() 
{ 
  vec2 uv = v_texCoords;
  
  vec3 tc = vec3(1.0, 0.0, 0.0);
  if (uv.x < (vx_offset-0.005))
  {
    float dx = pixel_w*(1./rt_w);
    float dy = pixel_h*(1./rt_h);
    vec2 coord = vec2(dx*floor(uv.x/dx),
                      dy*floor(uv.y/dy));
    tc = texture2D(u_texture, coord).rgb;
  }
  else if (uv.x>=(vx_offset+0.005))
  {
    tc = texture2D(u_texture, uv).rgb;
  }
	gl_FragColor = vec4(tc, 1.0);
}