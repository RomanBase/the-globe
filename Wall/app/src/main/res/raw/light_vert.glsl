uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform vec3 u_LightPos;
uniform vec2 u_TextureOffset;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Texture;

varying vec3 v_LightDir;
varying float v_Dist;
varying vec3 v_Normal;
varying vec2 v_Texture;
varying vec2 v_TextureTint;

void main(){

    vec4 pos = u_MVPMatrix * a_Position;
    vec3 n_pos = vec3(-pos.x, pos.y, -pos.z);

    v_LightDir = normalize(u_LightPos - n_pos);
    v_Dist = 1.0 - length(u_LightPos - n_pos) * 0.0075;

    v_Normal = normalize(vec3(u_MVMatrix * vec4(a_Normal, 0.0)));
    v_Texture = a_Texture;
    v_TextureTint = a_Texture + u_TextureOffset;

    gl_Position = pos;
}