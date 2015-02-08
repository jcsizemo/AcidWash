package com.sizemore.filter.acid;

public class ImageToolkit {
	
	private int w;
	private int h;
	
	public ImageToolkit() {
		
	}
	
	public int[] convolve(int w, int h, int[] pixels, double[] kernel, boolean preserveBrightness,
			boolean psychedelic) {
		this.w = w;
		this.h = h;
		
		kernel = reflectKernel(kernel);
		
		if (preserveBrightness) {
			kernel = preserveBrightness(kernel);
		}
		
		int[] convolved = new int[w*h];
		
		for (int x = 0; x < w; x++) {
			int result = 0;
			for (int y = 0; y < h; y++) {
				int[] matrix = getMatrix(pixels,x,y,3,1);
				result = dotProduct(matrix,kernel,psychedelic);
				convolved[x+y*w] = result;
			}
		}
		
		return convolved;
	}
	
	public double[] reflectKernel(double[] kernel) {
		double[] reversed = new double[kernel.length];
		for (int i = 0; i < kernel.length; i++) {
			reversed[i] = kernel[(kernel.length - 1) - i];
		}
		return reversed;
	}
	
	public double[] preserveBrightness(double[] kernel) {
		double sum = 0;
		for (double value : kernel) {
			sum += value;
		}
		if (sum == 0) sum = 1;
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] = (kernel[i] / sum);
		}
		return kernel;
	}
	
	public int[] getMatrix(int[] pixels, int x, int y, int matrixDim, int convOffset) {
		int[] matrix = {0,0,0,0,pixels[x+y*w],0,0,0,0};
		boolean leftEdge = (x == 0) ? true : false;
		boolean rightEdge = (x == (w - 1)) ? true : false;
		boolean topEdge = (y == 0) ? true : false;
		boolean bottomEdge = (y == (h - 1)) ? true : false;
		boolean topLeftCorner = leftEdge && topEdge;
		boolean bottomLeftCorner = leftEdge && bottomEdge;
		boolean topRightCorner = rightEdge && topEdge;
		boolean bottomRightCorner = rightEdge && bottomEdge;
		
		if (topLeftCorner) {
			matrix[5] = pixels[(x+1)+y*w];
			matrix[7] = pixels[x+((y+1)*w)];
			matrix[8] = pixels[(x+1)+((y+1)*w)];
		}
		else if (bottomLeftCorner) {
			matrix[1] = pixels[x+((y-1)*w)];
			matrix[2] = pixels[(x+1)+((y-1)*w)];
			matrix[5] = pixels[(x+1)+y*w];
		}
		else if (topRightCorner) {
			matrix[3] = pixels[(x-1)+y*w];
			matrix[6] = pixels[(x-1)+((y+1)*w)];
			matrix[7] = pixels[x+((y+1)*w)];
		}
		else if (bottomRightCorner) {
			matrix[0] = pixels[(x-1)+((y-1)*w)];
			matrix[1] = pixels[x+((y-1)*w)];
			matrix[3] = pixels[(x-1)+y*w];
		}
		else if (topEdge) {
			matrix[3] = pixels[(x-1)+y*w];
			matrix[5] = pixels[(x+1)+y*w];
			matrix[6] = pixels[(x-1)+((y+1)*w)];
			matrix[7] = pixels[x+((y+1)*w)];
			matrix[8] = pixels[(x+1)+((y+1)*w)];
		}
		else if (rightEdge) {
			matrix[0] = pixels[(x-1)+((y-1)*w)];
			matrix[1] = pixels[x+((y-1)*w)];
			matrix[3] = pixels[(x-1)+y*w];
			matrix[6] = pixels[(x-1)+((y+1)*w)];
			matrix[7] = pixels[x+((y+1)*w)];
		}
		else if (bottomEdge) {
			matrix[0] = pixels[(x-1)+((y-1)*w)];
			matrix[1] = pixels[x+((y-1)*w)];
			matrix[2] = pixels[(x+1)+((y-1)*w)];
			matrix[3] = pixels[(x-1)+y*w];
			matrix[5] = pixels[(x+1)+y*w];
		}
		else if (leftEdge) {
			matrix[1] = pixels[x+((y-1)*w)];
			matrix[2] = pixels[(x+1)+((y-1)*w)];
			matrix[5] = pixels[(x+1)+y*w];
			matrix[7] = pixels[x+((y+1)*w)];
			matrix[8] = pixels[(x+1)+((y+1)*w)];
		}
		else {
			matrix[0] = pixels[(x-1)+((y-1)*w)];
			matrix[1] = pixels[x+((y-1)*w)];
			matrix[2] = pixels[(x+1)+((y-1)*w)];
			matrix[3] = pixels[(x-1)+y*w];
			matrix[5] = pixels[(x+1)+y*w];
			matrix[6] = pixels[(x-1)+((y+1)*w)];
			matrix[7] = pixels[x+((y+1)*w)];
			matrix[8] = pixels[(x+1)+((y+1)*w)];
		}
		return matrix;
	}
	
	public int dotProduct(int[] matrix, double[] kernel, boolean psychedelic) {
		int result = 0;
		double red = 0;
		double green = 0;
		double blue = 0;
		for (int i = 0; i < matrix.length; i++) {
			red += (((0x00FF0000 & matrix[i]) >> 16) * kernel[i]);
			green += (((0x0000FF00 & matrix[i]) >> 8) * kernel[i]);
			blue += ((0x000000FF & matrix[i]) * kernel[i]);
		}
		if (!psychedelic) {
			if (red > 255) red = 255;
			if (green > 255) green = 255;
			if (blue > 255) blue = 255;
			if (red < 0) red = 0;
			if (green < 0) green = 0;
			if (blue < 0) blue = 0;
		}
		
		result = 0xFF000000 | (((int) red) << 16) | (((int) green) << 8) | (int) blue;
		return result;
	}
	
}
