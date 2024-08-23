import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;

import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;

public class Menu_Slider implements PlugIn, DialogListener {
	//Declarei aqui para usar no dialogItemChanged
	private ImagePlus imagem = IJ.getImage();
	//Será uma cópia da imagem normal lá embaixo.
	private ImagePlus imagemOriginal;
	
    public void run(String arg) {
        if (imagem == null || imagem.getType() != ImagePlus.COLOR_RGB) {
            IJ.error("Apenas imagens RGB são suportadas.");
            return;
        }
        
        imagemOriginal = imagem.duplicate();
      
        gerandoMenu_RGB_Cinza();
    }
    
    public void gerandoMenu_RGB_Cinza() {
    	
        //A interface genérica
        GenericDialog interfaceGrafica = new GenericDialog("Menu RGB to Cinza");
        //Fazendo um 'ouvinte' na interface
        interfaceGrafica.addDialogListener(this);
        
        interfaceGrafica.addSlider("Brilho", -255, 255, 0, 1);
        interfaceGrafica.addSlider("Contraste", 0, 255, 0, 1);
        interfaceGrafica.addSlider("Solarização", 0, 255, 0, 1);
        interfaceGrafica.addSlider("Dessaturação", 0, 255, 0, 1);
        //Mostra a Interface
        interfaceGrafica.showDialog();
        
        if (interfaceGrafica.wasCanceled()) {
        	restaurarOriginal();
            IJ.showMessage("PlugIn cancelado!");
        }
        
    }

    @Override
    public boolean dialogItemChanged(GenericDialog interfaceGrafica, AWTEvent e) {
        ImageProcessor processadorOriginal = imagemOriginal.getProcessor();
        ImageProcessor processadorAtual = imagem.getProcessor();
        
    	int red, green, blue;
    	int vetorRGB[] = new int[3];
        int width = processadorAtual.getWidth();
        int height = processadorAtual.getHeight();
		
	    int intensidade_brilho = (int) interfaceGrafica.getNextNumber();
	    int intensidade_contraste = (int) interfaceGrafica.getNextNumber();
	    int intensidade_solarizacao = (int) interfaceGrafica.getNextNumber();
	    int intensidade_dessaturacao = (int) interfaceGrafica.getNextNumber();
	    
	    float fator_contraste = (259*(intensidade_contraste+255)) / (255*(259-intensidade_contraste));
        
		//For que percorre toda a imagem
	    for (int y = 0; y < height; y++) {
	        for (int x = 0; x < width; x++) {
                //Pega o valor RGB daquele pixel na imagem original
                processadorOriginal.getPixel(x, y, vetorRGB);
                
                red = vetorRGB[0];
                green = vetorRGB[1];
                blue = vetorRGB[2];
                
                //Brilho
                int rBrilho = limitador(red + intensidade_brilho);
                int gBrilho = limitador(green + intensidade_brilho);
                int bBrilho = limitador(blue + intensidade_brilho);
                
                //Contraste
                int rContraste = limitador((int)(fator_contraste*(rBrilho-128)+128));
                int gContraste = limitador((int)(fator_contraste*(gBrilho-128)+128));
                int bContraste = limitador((int)(fator_contraste*(bBrilho-128)+128));
                
                //Solarização
                int rSolarizado = limitador(solarizacao(rContraste,intensidade_solarizacao));
                int gSolarizado = limitador(solarizacao(gContraste,intensidade_solarizacao));
                int bSolarizado = limitador(solarizacao(bContraste,intensidade_solarizacao));
                
                //Dessaturação
                int gray = (rSolarizado + gSolarizado + bSolarizado) / 3;

                int rFinal = dessaturar(rSolarizado, intensidade_dessaturacao, gray);
                int gFinal = dessaturar(gSolarizado, intensidade_dessaturacao, gray);
                int bFinal = dessaturar(bSolarizado, intensidade_dessaturacao, gray);
                
                // Atualizando o pixel na nova imagem
                processadorAtual.putPixel(x, y, (rFinal << 16) | (gFinal << 8) | bFinal);
     
	        }
	    }

	    imagem.updateAndDraw();
    	
        if (interfaceGrafica.wasCanceled()) return false;
        IJ.log("Resposta do slider Brilho: " + intensidade_brilho);
        IJ.log("Resposta do slider Contraste: " + intensidade_contraste);
        IJ.log("Resposta do slider Solarização: " + intensidade_solarizacao);
        IJ.log("Resposta do slider Dessaturação: " + intensidade_dessaturacao);
        IJ.log("\n");       
        return true;
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
    
    public void restaurarOriginal() {
        if (imagemOriginal != null) {
        	//Pego o processador da original e coloco na atual
        	ImageProcessor processadorOriginal = imagemOriginal.getProcessor();
            imagem.setProcessor(processadorOriginal);
            imagem.updateAndDraw();
        }
    }
    
    public int solarizacao(int value, int intensidade_solarizacao) {
    	if(value < intensidade_solarizacao) {
    		value = 255 - value;
    		return value;
    	}else {
    		return value;
    	}
    }
    
    public int dessaturar(int value, int intensidade_dessaturacao, int gray) {
        //Mesclando o valor original com o de cinza
        
    	//Proporção da cor inicial que será mantida
    	int proporcaoOriginal = value * (255 - intensidade_dessaturacao);
    	//Proporção do cinza que vou adicionar
    	int proporcaoCinza = gray * intensidade_dessaturacao;
    	int somaProporcoes = proporcaoOriginal + proporcaoCinza;
    	int valorFinal = somaProporcoes / 255;
    	return limitador(valorFinal);
    }

}

