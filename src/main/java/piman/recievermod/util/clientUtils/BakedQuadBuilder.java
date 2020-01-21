package piman.recievermod.util.clientUtils;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class BakedQuadBuilder {

	private Vector3f[] positions = new Vector3f[4];
	private Vector4f uv;
	private long color;
	private TextureAtlasSprite texture;
	
	public BakedQuadBuilder setPosition(Vector3f position, int index) {
		this.positions[index] = position;
		return this;
	}
	
	public BakedQuadBuilder setUV(Vector4f uv) {
		this.uv = uv;
		return this;
	}
	
	public BakedQuadBuilder setColor(long color) {
		this.color = color;
		return this;
	}
	
	public BakedQuadBuilder setTexture(TextureAtlasSprite texture) {
		this.texture = texture;
		return this;
	}
	
	public BakedQuadBuilder applyRotation(Vector3f rotation, Vector3f center) {
		
		Matrix4f points = this.buildPositionMatrix();
		
		Matrix4f transformation = new TransformationBuilder().add(null, rotation, center, null, 0).buildMatirx();
		
		points.mul(transformation, points);
		
		this.resolvePositionMatrix(points);
		
		return this;
	}
	
	public BakedQuad build() {
		
		int[] ints = new int[28];
		
		for (int i = 0; i < 4; i++) {
			ints[i*7 + 0] = Float.floatToRawIntBits(positions[i].getX());
			ints[i*7 + 1] = Float.floatToRawIntBits(positions[i].getY());
			ints[i*7 + 2] = Float.floatToRawIntBits(positions[i].getZ());
			ints[i*7 + 3] = (int) color;
			ints[i*7 + 4] = Float.floatToRawIntBits(texture.getInterpolatedU(this.getUPoint(i)));
			ints[i*7 + 5] = Float.floatToRawIntBits(texture.getInterpolatedV(this.getVPoint(i)));
		}
		
		return new BakedQuad(ints, (int) color, null, texture, true, DefaultVertexFormats.ITEM);
		
	}
	
	private float getUPoint(int i) {
		if (i == 0 || i == 3) {
			return uv.x;
		}
		else {
			return uv.z;
		}
	}
	
	private float getVPoint(int i) {
		if (i == 0 || i == 1) {
			return uv.w;
		}
		else {
			return uv.y;
		}
	}
	
	private void resolvePositionMatrix(Matrix4f points) {

		for (int i = 0; i < 4; i++) {
			float[] floats = new float[4];
			points.getColumn(i, floats);
			positions[i] = new Vector3f(floats);
		}
		
	}

	private Matrix4f buildPositionMatrix() {
		
		Matrix4f points = new Matrix4f();
		
		for (int i = 0; i < 4; i++) {
			points.setColumn(i, new Vector4f(positions[i]));
		}
		
		points.setRow(3, 1, 1, 1, 1);
		
		return points;
		
	}
	
	
}