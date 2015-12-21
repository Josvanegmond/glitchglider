#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_color;
attribute vec4 a_normal;

varying vec2 v_texCoord0;
varying float height;
varying vec4 v_color;
varying float v_normalAngle;
varying float v_fog;
varying vec3 v_posEye;
varying vec3 v_normEye;
varying vec3 v_position;

uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_viewTrans;
uniform vec4 u_camPos;

void main() {
    v_color = a_color;
    v_texCoord0 = a_texCoord0 * 5.0;
    float dotNormal = dot( vec3(0.0, 0.0, 1.0), a_normal.xyz );
    v_normalAngle = abs( acos( dotNormal ) );
    v_position = a_position.xyz;

    v_posEye = vec3( u_viewTrans * u_worldTrans * vec4( a_position.xyz, 1.0 ) );
    v_normEye = vec3( u_viewTrans * u_worldTrans * vec4( a_normal.xyz, 0.0 ) );

    vec3 flen = u_camPos.xyz - vec4(u_worldTrans * a_position).xyz;
    float fog = dot(flen, flen) * u_camPos.w * 1.5;
    v_fog = min(fog, 1.0);

    gl_Position = a_position;

    height = smoothstep(-1.0, 1.0, vec4(u_worldTrans * a_position).y);
    gl_Position = u_projTrans * u_worldTrans * a_position;
}