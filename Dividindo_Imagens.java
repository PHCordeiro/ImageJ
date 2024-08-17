import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;
import ij.plugin.PlugIn;

public class Dividindo_Imagens implements PlugIn {
    public void run(String arg) {
        
        ImagePlus imagem = IJ.getImage();
        if (imagem == null || imagem.getType() != ImagePlus.COLOR_RGB) {
            IJ.error("Apenas imagens RGB são suportadas.");
            return;
        }
        
        ImageProcessor processador_imagem = imagem.getProcessor();

        ImagePlus redChannel = createGrayChannelImage(processador_imagem, 0); 
        ImagePlus greenChannel = createGrayChannelImage(processador_imagem, 1); 
        ImagePlus blueChannel = createGrayChannelImage(processador_imagem, 2); 

        redChannel.show();
        greenChannel.show();
        blueChannel.show();
    }

    private ImagePlus createGrayChannelImage(ImageProcessor processador_imagem, int channel) {
        int width = processador_imagem.getWidth();
        int height = processador_imagem.getHeight();
        int vetorRGB[] = new int[3];
        ImagePlus grayImage = NewImage.createByteImage("Gray Channel " + channel, width, height, 1, NewImage.FILL_BLACK);
        ImageProcessor grayIp = grayImage.getProcessor();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                processador_imagem.getPixel(x, y, vetorRGB);
                grayIp.putPixel(x, y, vetorRGB[channel]);
            }
        }
        return grayImage;
    }
}

