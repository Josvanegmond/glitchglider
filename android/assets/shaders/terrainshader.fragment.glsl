#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying vec4 v_color;
varying vec2 v_texCoord0;
varying float v_fog;
varying float height;
varying float v_normalAngle;
varying vec3 v_posEye;
varying vec3 v_normEye;
varying vec3 v_position;

uniform samplerCube u_environmentCubemap;
uniform mat4 u_invViewTrans;

uniform sampler2D u_groundTexture;
uniform sampler2D u_groundOverlay;
uniform sampler2D u_groundMask;

uniform sampler2D u_wallTexture;
uniform sampler2D u_wallOverlay;
uniform sampler2D u_wallMask;

uniform vec4 u_fogColor;

void main()
{
    vec3 groundTextureColor = texture2D( u_groundTexture, v_texCoord0 ).rgb;
    vec3 groundOverlayColor = texture2D( u_groundOverlay, v_texCoord0 ).rgb;
    float groundMaskValue = length( texture2D( u_groundMask, v_texCoord0 ).rgb );
    float smoothRange = 0.02;
    float groundMask = smoothstep( groundMaskValue - smoothRange, groundMaskValue + smoothRange, length( u_fogColor.rgb ) );

    vec3 wallTextureColor = texture2D( u_wallTexture, v_texCoord0 ).rgb;
    vec3 wallOverlayColor = texture2D( u_wallOverlay, v_texCoord0 ).rgb;
    vec3 wallMaskColor = texture2D( u_wallMask, v_texCoord0 ).rgb;
    float wallMask = length( wallMaskColor );

    vec3 incident_eye = normalize( v_posEye );
    vec3 normal = normalize( v_normEye ) * (groundMaskValue * 0.2 + 0.8);
    vec3 reflected = reflect( incident_eye, normal );
    reflected = vec3( u_invViewTrans * vec4( reflected, 0.0 ) );
    vec3 skyColor = textureCube( u_environmentCubemap, reflected ).rgb * 0.5;

    vec3 finalTex = mix(
        mix( groundOverlayColor * (0.25 + u_fogColor.rgb) + skyColor, groundTextureColor * (0.75 + u_fogColor.rgb * 0.25), groundMask),
        mix(wallTextureColor, wallOverlayColor, wallMask),
        v_normalAngle
    );

    gl_FragColor = v_color + vec4( mix( finalTex, u_fogColor.rgb, v_fog ), 1.0 );
}