import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.IJ;
import ij.ImagePlus;

public class FIltros_Não_Lineares implements PlugIn {
    public void run(String arg) {
    	ImagePlus imagem = IJ.getImage();
    	
        if (imagem == null || imagem.getType() != ImagePlus.GRAY8) {
            IJ.error("Apenas imagens 8-bits são suportadas.");
            return;
        }
        
        ImagePlus imagemOriginal = imagem.duplicate();
        ImageProcessor processadorOriginal = imagemOriginal.getProcessor();
        ImageProcessor processadorAtualHorizontal = imagem.duplicate().getProcessor(); 
        ImageProcessor processadorAtualVertical = imagem.duplicate().getProcessor(); 
        ImageProcessor processadorResultado = imagem.duplicate().getProcessor();

        aplicandoSobel(processadorAtualHorizontal,processadorAtualVertical,processadorOriginal);
        
        imagemResultante(processadorResultado, processadorAtualHorizontal, processadorAtualVertical);
        
        ImagePlus imagem_sobel_horizontal = new ImagePlus("Imagem Sobel Horizontal", processadorAtualHorizontal);
        imagem_sobel_horizontal.show();
        
        ImagePlus imagem_sobel_vertical = new ImagePlus("Imagem Sobel Vertical", processadorAtualVertical);
        imagem_sobel_vertical.show();
        
        ImagePlus imagem_resultante = new ImagePlus("Imagem Resultante", processadorResultado);
        imagem_resultante.show();

    }
    
    public void aplicandoSobel(ImageProcessor processadorAtualHorizontal, ImageProcessor processadorAtualVertical, ImageProcessor processadorOriginal) {
        int width = processadorOriginal.getWidth();
        int height = processadorOriginal.getHeight();

        int kernelHorizontal[][] = {
            { 1, 2,  1 },
            { 0, 0, 0 },
            { -1, -2, -1 }
        };
        
        int kernelVertical[][] = {
                { -1, 0, 1 },
                { -2, 0, 2 },
                { -1, 0, 1 }
            };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int valorPixelHorizontal = 0;
                int valorPixelVertical = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                    	valorPixelHorizontal += processadorOriginal.getPixel(x + kx, y + ky) * kernelHorizontal[ky + 1][kx + 1];
                    	valorPixelVertical += processadorOriginal.getPixel(x + kx, y + ky) * kernelVertical[ky + 1][kx + 1];
                    }
                }

                valorPixelHorizontal = limitador(valorPixelHorizontal);
                valorPixelVertical = limitador(valorPixelVertical);

                processadorAtualHorizontal.putPixel(x, y, valorPixelHorizontal);
                processadorAtualVertical.putPixel(x, y, valorPixelVertical);
            }
        }
    }
    
    public void imagemResultante(ImageProcessor processadorResultado, ImageProcessor processadorHorizontal, ImageProcessor processadorVertical) {
        int width = processadorResultado.getWidth();
        int height = processadorResultado.getHeight();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = processadorHorizontal.getPixel(x, y);
                int gy = processadorVertical.getPixel(x, y);

                //sqrt calcula a raiz quadrada
                int resultado = (int) Math.sqrt(gx * gx + gy * gy);

                resultado = limitador(resultado);

                processadorResultado.putPixel(x, y, resultado);
            }
        }
    }
    
    public int limitador(int value) {
        if(value <= 0) {
        	return 0;
        }
        if(value >= 255) {
        	return 255;
        }
		return value;
    }
}

