import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ij.plugin.PlugIn;

public class Dividindo_Imagens_3_Cores implements PlugIn {
    public void run(String arg) {
    	//Pegando imagem aberta
    	ImagePlus imagem = IJ.getImage();
    	//Criando auxiliares
		byte aux[]= new byte[256];
		byte aux2[]= new byte[256];
        
        if (imagem == null || imagem.getType() != ImagePlus.COLOR_RGB) {
            IJ.error("Apenas imagens RGB são suportadas.");
            return;
        }
        
        //Criando imagens em cinza
        ImagePlus imagem_vermelho = createGrayChannelImage(imagem, "Red"); 
        ImagePlus imagem_verde = createGrayChannelImage(imagem, "Green"); 
        ImagePlus imagem_azul = createGrayChannelImage(imagem, "Blue"); 

        //Inicia os vetores auxiliares
        //aux[i] varia de 0 a 255
		for(int i= 0; i <256; i++){
			aux[i]=(byte)i;
			aux2[i]=0;
		}
		
		//Alterando o LUT da imagem Red -> (Vermelho != 0, Verde = 0, Azul = 0)
		imagem_vermelho.setLut(new LUT(aux, aux2, aux2));
		imagem_verde.setLut(new LUT(aux2, aux, aux2));
		imagem_azul.setLut(new LUT(aux2, aux2, aux));
		
		//Mostrando as imagens coloridas
		imagem_vermelho.show();
		imagem_verde.show();
		imagem_azul.show();
    }

    private ImagePlus createGrayChannelImage(ImagePlus imagem, String canalRGB) {
    	//Iniciando o Processador da imagem
		ImageProcessor processador = imagem.getProcessor();
		
		int pixelRGB, red, green, blue;
        int vetorRGB[] = new int[3];
        int width = processador.getWidth();
        int height = processador.getHeight();
        
        //Nova Imagem passando tamanho e canal
		ImagePlus novaImagem = NewImage.createByteImage("Canal " + canalRGB, width,height, 1, NewImage.FILL_BLACK);
		ImageProcessor processadorNovaImagem = novaImagem.getProcessor();

		//For que percorre toda a imagem
		for (int y = 0; y < height; y++) {
		    for (int x = 0; x < width; x++) {
		    	processador.getPixel(x, y, vetorRGB);  // vetorRGB[0] = red, [1] = green, [2] = blue
		    	pixelRGB = processador.getPixel(x,y);
		    	
				switch (canalRGB) {
					case "Red":
						// Pega os bytes Vermelhos 
						red = (pixelRGB>>16)&0xff;  
						processadorNovaImagem.putPixel(x, y, red);
						break;
					case "Green":
						//Pega os bytes verdes
						green = (pixelRGB>>8)&0xff; 
						processadorNovaImagem.putPixel(x, y, green);
						break;
					case "Blue":
						//Pega os bytes azuis
						blue = pixelRGB&0xff;       
						processadorNovaImagem.putPixel(x, y, blue);
						break;
					default:
						break;
				}
		    }
		}
		return novaImagem;
    }
}

