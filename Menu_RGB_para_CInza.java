import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;

public class Menu_RGB_para_Cinza implements PlugIn, DialogListener {
    public void run(String arg) {
        ImagePlus imagem = IJ.getImage();
        
        if (imagem == null || imagem.getType() != ImagePlus.COLOR_RGB) {
            IJ.error("Apenas imagens RGB são suportadas.");
            return;
        }
        
        gerandoMenu_RGB_Cinza(imagem);
    }
    
    public void gerandoMenu_RGB_Cinza(ImagePlus imagem) {
        ImageProcessor processador = imagem.getProcessor();
        
        int pixelRGB, red, green, blue;
        int width = processador.getWidth();
        int height = processador.getHeight();
        
        //A interface genérica
        GenericDialog interfaceGrafica = new GenericDialog("Menu RGB to Cinza");
        //Fazendo um 'ouvinte' na interface
        interfaceGrafica.addDialogListener(this);
        
        String[] estrategia = {"Estratégia 1", "Estratégia 2", "Estratégia 3"}; 
        interfaceGrafica.addRadioButtonGroup("Botões para escolher uma dentre várias estratégias", estrategia, 1, 3, "Estratégia 1");
        interfaceGrafica.addCheckbox("Manter a Imagem Atual", true);
        interfaceGrafica.showDialog();
        
        if (interfaceGrafica.wasCanceled()) {
            IJ.showMessage("PlugIn cancelado!");
        }
        
        else {
            if (interfaceGrafica.wasOKed()) {
                String respostaRadioButton = interfaceGrafica.getNextRadioButton(); 
                boolean respostaCheckbox = interfaceGrafica.getNextBoolean();
                
                //Processador para criar uma nova imagem SE for preciso
                ImageProcessor novoProcessador = processador.duplicate();
                
                switch (respostaRadioButton) {
                    case "Estratégia 1":
                        //Lendo pixel a pixel
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                //Pega o valor RGB daquele pixel
                                pixelRGB = processador.getPixel(x, y);
                                
                                //Pega os componentes R, G, B daquele pixel
                                red = (pixelRGB >> 16) & 0xff;
                                green = (pixelRGB >> 8) & 0xff;
                                blue = pixelRGB & 0xff;
                                
                                //Aplicando a estratégia da média simples
                                int cinza = (red + green + blue) / 3;
                                
                                //Criando uma cor em escala de cinza para cada cor RGB
                                int novaCor = (cinza << 16) | (cinza << 8) | cinza;
                                novoProcessador.putPixel(x, y, novaCor);
                            }
                        }
                        break;
                    case "Estratégia 2":
                        //Lendo pixel a pixel
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                //Pega o valor RGB daquele pixel
                                pixelRGB = processador.getPixel(x, y);
                                
                                //Pega os componentes R, G, B
                                red = (pixelRGB >> 16) & 0xff;
                                green = (pixelRGB >> 8) & 0xff;
                                blue = pixelRGB & 0xff;
                                
                                double wr = 0.2125;
                                double wg = 0.7154;
                                double wb = 0.072;
                                
                                //Aplicando a estratégia da média ponderada
                                double cinza = red * wr + green * wg + blue * wb;
                                
                                //Vendo se o valor de cinza está no intervalo de 0 a 255
                                cinza = Math.min(Math.max(cinza, 0), 255);
                                int cinzaInt = (int) cinza;
                                
                                //Criando uma cor em escala de cinza para cada cor RGB
                                int novaCor = (cinzaInt << 16) | (cinzaInt << 8) | cinzaInt;
                                novoProcessador.putPixel(x, y, novaCor);
                            }
                        }
                        break;
                    case "Estratégia 3":
                        	
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                //Pega o valor RGB daquele pixel
                                pixelRGB = processador.getPixel(x, y);
                                
                                //Pega os componentes R, G, B
                                red = (pixelRGB >> 16) & 0xff;
                                green = (pixelRGB >> 8) & 0xff;
                                blue = pixelRGB & 0xff;
                                
                                double wr = 0.299;
                                double wg = 0.587;
                                double wb = 0.0114;
                                
                                //Aplicando a estratégia da média ponderada
                                double cinza = red * wr + green * wg + blue * wb;
                                
                                //Vendo se o valor de cinza está no intervalo de 0 a 255
                                cinza = Math.min(Math.max(cinza, 0), 255);
                                int cinzaInt = (int) cinza;
                                
                                //Criando uma cor em escala de cinza para cada cor RGB
                                int novaCor = (cinzaInt << 16) | (cinzaInt << 8) | cinzaInt;
                                novoProcessador.putPixel(x, y, novaCor);
                            }
                        }
                        
                        break;
                    default:
                        IJ.log("Nenhuma estratégia válida selecionada");
                        break;
                }
                
                if(respostaCheckbox){
                    ImagePlus imagemCinza = new ImagePlus("Imagem em Escala de Cinza", novoProcessador);
                    imagemCinza.show();
                } else {
                	imagem.close();
                	ImagePlus imagemCinza = new ImagePlus("Imagem em Escala de Cinza", novoProcessador);
                	imagemCinza.show();
                }
            }
        }
    }

    @Override
    public boolean dialogItemChanged(GenericDialog interfaceGrafica, AWTEvent e) {
        if (interfaceGrafica.wasCanceled()) return false;
        IJ.log("Resposta do botão de rádio: " + interfaceGrafica.getNextRadioButton());
        IJ.log("Resposta do checkbox: " + interfaceGrafica.getNextBoolean());
        IJ.log("\n");       
        return true;
    }

}

