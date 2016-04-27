package com.voicenotes.manages;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL;

import android.opengl.GLES20;

public class OpenGLesDisplay {
	private Boolean isProgBuilt = false;
	
	private float[] squareVertices = new float[] {
			1.0f, -1.0f,
			1.0f, 1.0f,
			-1.0f, -1.0f,
			-1.0f, 1.0f
	};
	
	private float[] coordVertices = new float[] {
			0.0f, 1.0f,
			1.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f
	};
	
	private String VERTEX_SHADER = "attribute vec4 vPosition;\n" + "attribute vec2 a_texCoord;\n"  
	        + "varying vec2 tc;\n" + "void main() {\n" + "gl_Position = vPosition;\n" + "tc = a_texCoord;\n" + "}\n";
	
	private String FRAGMENT_SHADER = "precision mediump float;\n" + "uniform sampler2D tex_y;\n"  
	        + "uniform sampler2D tex_u;\n" + "uniform sampler2D tex_v;\n" + "varying vec2 tc;\n" + "void main() {\n"  
	        + "vec4 c = vec4((texture2D(tex_y, tc).r - 16./255.) * 1.164);\n"  
	        + "vec4 U = vec4(texture2D(tex_u, tc).r - 128./255.);\n"  
	        + "vec4 V = vec4(texture2D(tex_v, tc).r - 128./255.);\n" + "c += V * vec4(1.596, -0.813, 0, 0);\n"  
	        + "c += U * vec4(0, -0.392, 2.017, 0);\n" + "c.a = 1.0;\n" + "gl_FragColor = c;\n" + "}\n";  

	
	private ByteBuffer _vertice_buffer;
	private ByteBuffer _coord_buffer;
	
	private int _program = 0;
	private int _positionHandle;
	private int _coordHanlde;
	private int _yhandle;
	private int _uhandle;
	private int _vhandle;
	
	private int _video_width;
	private int _video_height;
	private int _ytid;
	private int _utid;
	private int _vtid;
	
	public Boolean isProgramBuilt() {
		return isProgBuilt;
	}
	
	public void buildProgram() {
		creatBuffer(squareVertices, coordVertices);
		if (_program <= 0) {
			_program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
		}
		
		_positionHandle = GLES20.glGetAttribLocation(_program, "vPosition");
		checkGlError("glGetAttribLocation vPostion");
		if (_positionHandle == -1) {
			throw new RuntimeException("Could not get attribute location for vPosition");
		}
		_coordHanlde = GLES20.glGetAttribLocation(_program, "a_texCoord");
		checkGlError("glGetAttribLocation a_texCoord");  
	    if (_coordHanlde == -1) {  
	        throw new RuntimeException("Could not get attribute location for a_texCoord");  
	    } 
	    
	    _yhandle = GLES20.glGetUniformLocation(_program, "tex_y");
	    checkGlError("glGetUniformLocation tex_y");  
	    if (_yhandle == -1) {  
	        throw new RuntimeException("Could not get uniform location for tex_y");  
	    }  
	    
	    _uhandle = GLES20.glGetUniformLocation(_program, "tex_u");
	    checkGlError("glGetUniformLocation tex_u");  
	    if (_uhandle == -1) {  
	        throw new RuntimeException("Could not get uniform location for tex_u");  
	    } 
	    
	    _vhandle = GLES20.glGetUniformLocation(_program, "tex_v");
	    checkGlError("glGetUniformLocation tex_v");  
	    if (_vhandle == -1) {  
	        throw new RuntimeException("Could not get uniform location for tex_v");  
	    } 
	    
	    isProgBuilt = true;
	}
	
	public void buildTextures(Buffer y, Buffer u, Buffer v, int width, int heigth) {
		boolean videoSizeChanged = ((width != _video_width) || (heigth != _video_height));
		if (videoSizeChanged) {
			_video_width = width;
			_video_height = heigth;
		}
		if (_ytid < 0 || videoSizeChanged) {
			if (_ytid >= 0) {
				GLES20.glDeleteTextures(1, new int[] {_ytid}, 0);
				checkGlError("glDeleteTextures");
			}
			int[] textures = new int[1];
			GLES20.glGenTextures(1, textures, 0);
			checkGlError("glGenTextures");
			_ytid = textures[0];
		}
		bindTextures(_ytid, y, _video_width, _video_height);
		
		if (_utid < 0 || videoSizeChanged) {
			if (_utid >= 0) {
				GLES20.glDeleteTextures(1, new int[] {_utid}, 0);
				checkGlError("glDeleteTextures");
			}
			int[] textures = new int[1];
			GLES20.glGenTextures(1, textures, 0);
			checkGlError("glGenTextures");
			_utid = textures[0];
		}
		bindTextures(_utid, u, _video_width / 2, _video_height / 2);
		
		if (_vtid < 0 || videoSizeChanged) {
			if (_vtid >= 0) {
				GLES20.glDeleteTextures(1, new int[] {_vtid}, 0);
				checkGlError("glDeleteTextures");
			}
			int[] textures = new int[1];
			GLES20.glGenTextures(1, textures, 0);
			checkGlError("glGenTextures");
			_vtid = textures[0];
		}
		bindTextures(_vtid, v, _video_width / 2, _video_height / 2);
	}
	
	private void bindTextures(int textureId, Buffer data, int width, int height) {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		checkGlError("glBindTexture");
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, data);
		checkGlError("glTexImage2D");
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	}
	
	public void drawFrame() {
		GLES20.glUseProgram(_program);
		checkGlError("glUseProgram");
		
		GLES20.glVertexAttribPointer(_positionHandle, 2, GLES20.GL_FLOAT, false, 8, _vertice_buffer);
		checkGlError("glVertexAttribPointer mPositionHanle");
		GLES20.glEnableVertexAttribArray(_positionHandle);
		 
		GLES20.glVertexAttribPointer(_coordHanlde, 2, GLES20.GL_FLOAT, false, 8, _coord_buffer);
		checkGlError("glVertexAttribPointer mTextureHandle");
		GLES20.glEnableVertexAttribArray(_coordHanlde);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _ytid);
		GLES20.glUniform1i(_yhandle, 0);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _utid);
		GLES20.glUniform1i(_uhandle, 1);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _vtid);
		GLES20.glUniform1i(_vhandle, 2);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		GLES20.glFinish();
		
		GLES20.glDisableVertexAttribArray(_positionHandle);
		GLES20.glDisableVertexAttribArray(_coordHanlde);
	}
	
	private void creatBuffer(float[] vert, float[] coord) {
		_vertice_buffer = ByteBuffer.allocateDirect(vert.length * 4);
		_vertice_buffer.order(ByteOrder.nativeOrder());
		_vertice_buffer.asFloatBuffer().put(vert);
		_vertice_buffer.position(0);
		
		if (_coord_buffer == null) {
			_coord_buffer = ByteBuffer.allocateDirect(coord.length * 4);
			_coord_buffer.order(ByteOrder.nativeOrder());
			_coord_buffer.asFloatBuffer().put(coord);
			_coord_buffer.position(0);
			}
	}
	
	private int createProgram(String verterSource, String fragmentSource) {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, verterSource);
		int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		
		int program = GLES20.glCreateProgram();
		if (program != 0) {
			GLES20.glAttachShader(program, vertexShader);
			checkGlError("glAttachShader");
			GLES20.glAttachShader(program, pixelShader);
			checkGlError("glAttachShader");
			GLES20.glLinkProgram(program);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] != GLES20.GL_TRUE) {
				GLES20.glDeleteProgram(program);
				program = 0;
			}
		}
		return program;
	}
	
	//º”‘ÿshader
	private int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		if (shader != 0) {
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);
			int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == 0) {
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		return shader;
	}
	
	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			throw new RuntimeException(op + ": glError" + error);
		}
	}
}
