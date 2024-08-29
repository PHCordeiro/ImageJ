import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;

import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;

public class Expansao_Equalizacao_Histograma implements PlugIn, DialogListener {
    public void run(String arg) {
        ImagePlus imagem = IJ.getImage();
        
        if (imagem == null || imagem.getType() != ImagePlus.GRAY8) {
            IJ.error("Apenas imagens 8-bits são suportadas.");
            return;
        }
        
        gerandoMenu(imagem);
    }
    
    public void gerandoMenu(ImagePlus imagem) {
        ImageProcessor processador = imagem.getProcessor();
        
        //A interface genérica
        GenericDialog interfaceGrafica = new GenericDialog("Expansão e Equalização Menu");
        //Fazendo um 'ouvinte' na interface
        interfaceGrafica.addDialogListener(this);
        
        String[] estrategia = {"Expansão", "Equalização"}; 
        interfaceGrafica.addRadioButtonGroup("Botões para escolher uma dentre várias estratégias", estrategia, 1, 2, "Expansão");
        interfaceGrafica.showDialog();
        
        if (interfaceGrafica.wasCanceled()) {
            IJ.showMessage("PlugIn cancelado!");
        } else {
            if (interfaceGrafica.wasOKed()) {
                String respostaRadioButton = interfaceGrafica.getNextRadioButton(); 
                
                //Processador para criar uma nova imagem SE for preciso
                ImageProcessor novoProcessador = processador.duplicate();
                
                switch (respostaRadioButton) {
                    case "Expansão":
                    	
                    	expandindoHistograma(novoProcessador);
                        
                        //Dps de processar todos os pixels cria a nova imagem
                        ImagePlus imagemExpandida = new ImagePlus("Imagem Expandida", novoProcessador);
                        imagemExpandida.show();
                        break;
                        
                    case "Equalização":
                    	
                        equalizandoHistograma(novoProcessador);
                        
                        ImagePlus imagemEqualizada = new ImagePlus("Imagem Equalizada", novoProcessador);
                        imagemEqualizada.show();
                        break;

                    default:
                        IJ.log("Nenhuma estratégia válida selecionada");
                        break;
                }
            }
        }
    }

    private void expandindoHistograma(ImageProcessor processador) {

        int width = processador.getWidth();
        int height = processador.getHeight();
        //Declarei min como 255 e max como 0 para trocar dps
        int itensidadeMinima = 255;
        int itensidadeMaxima = 0;
        
        //Pegando a intensidade mínima e máxima
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	int intensidade = processador.getPixel(x, y);
                
                //Se intensity for menor que a mínima troca...
                if (intensidade < itensidadeMinima) {
                	itensidadeMinima = intensidade;
                }
                if (intensidade > itensidadeMaxima) {
                	itensidadeMaxima = intensidade;
                }
            }
        }
        
        if(itensidadeMaxima != itensidadeMinima) {
	        //Expandindo o Histograma
	        for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	            	int intensidade = processador.getPixel(x, y);
	                int novaIntensidade = (intensidade - itensidadeMinima) * 255 / (itensidadeMaxima - itensidadeMinima);
	                processador.putPixel(x, y, novaIntensidade);
	            }
	        }
        }
    }
    
    private void equalizandoHistograma(ImageProcessor processador) {
    	
        int width = processador.getWidth();
        int height = processador.getHeight();
        //Função responsável por pegar o histograma da imagem
        //histograma guarda o n de pixels para cada nível de intensidade 
        int histograma[] = processador.getHistogram();
        //Armazena o histograma acumulado
        int histogramaAcumulado[] = new int[256];
        int totalPixels = width * height;
        
        //Calculando o histograma acumulado
        histogramaAcumulado[0] = histograma[0];
        for (int i = 1; i < histograma.length; i++) {
        	//Para cada intensidade soma o valor atual do histograma ao valor acumulado anterior 
        	histogramaAcumulado[i] = histogramaAcumulado[i - 1] + histograma[i];
        }
        
        //Equalizando :)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int itensidade = processador.getPixel(x, y);
                int novaItensidade = histogramaAcumulado[itensidade] * 255 / totalPixels;
                processador.putPixel(x, y, novaItensidade);
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

    @Override
    public boolean dialogItemChanged(GenericDialog interfaceGrafica, AWTEvent e) {
        if (interfaceGrafica.wasCanceled()) return false;
        IJ.log("Resposta do botão de rádio: " + interfaceGrafica.getNextRadioButton());
        IJ.log("\n");       
        return true;
    }

}
