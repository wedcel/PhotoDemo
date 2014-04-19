package com.example.demo;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * 书写一个图片管理类
 * 
 * @author lw
 * 
 */

public class PictureBean implements Serializable {
	private static final long serialVersionUID = 1L;
	// 图片数据对象
	private byte[] buffer;
	// 图片宽
	private int w;
	// 图片高
	private int h;

	public PictureBean(byte[] b, int w, int h) {
		this.buffer = b;
		this.w = w;
		this.h = h;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public static Bitmap beanToBitmap(PictureBean bean, boolean needDecode) {
		int RGBData[] = new int[bean.getW() * bean.getH()];
		byte[] mYUVData = new byte[bean.getBuffer().length];
		System.arraycopy(bean.getBuffer(), 0, mYUVData, 0,
				bean.getBuffer().length);
		if (needDecode)
			decodeYUV420SP(RGBData, mYUVData, bean.getW(), bean.getH());
		final Bitmap bitmap = Bitmap.createBitmap(bean.getW(), bean.getH(),
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(RGBData, 0, bean.getW(), 0, 0, bean.getW(),
				bean.getH());
		return bitmap;
	}

	private static void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
			int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}
}
