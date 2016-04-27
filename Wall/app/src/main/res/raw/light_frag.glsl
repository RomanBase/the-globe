precision mediump float;

uniform sampler2D u_Texture;
uniform sampler2D u_TextureTint;
uniform vec4 u_Color;
uniform float u_Ambient;

varying vec3 v_LightDir;
varying float v_Dist;
varying vec3 v_Normal;
varying vec2 v_Texture;
varying vec2 v_TextureTint;

void main()
{
    vec4 color = texture2D(u_Texture, v_Texture);
    vec4 color_lum = texture2D(u_TextureTint, v_TextureTint) * u_Color;
    vec4 c = mix(color_lum, color, color.a);

    float intensity = max(dot(normalize(v_Normal), v_LightDir), u_Ambient);

    intensity = intensity * v_Dist;

    gl_FragColor = vec4(c.rgb * intensity, 1.0);
}