import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;

public class Morfologias_Matematicas implements PlugIn, DialogListener {
    private ImagePlus imagem = IJ.getImage();
    private ImagePlus imagemOriginal;

    public void run(String arg) {
        if (imagem == null || imagem.getType() != ImagePlus.GRAY8) {
            IJ.error("Apenas imagens 8-bits são suportadas.");
            return;
        }
        imagemOriginal = imagem.duplicate();
        gerandoMenu(imagem);
    }

    public void gerandoMenu(ImagePlus imagem) {
        GenericDialog interfaceGrafica = new GenericDialog("Morfologias Matemáticas");
        interfaceGrafica.addDialogListener(this);

        String[] estrategia = { "Dilatação", "Erosão", "Abertura", "Fechamento", "Borda"};
        interfaceGrafica.addRadioButtonGroup("Opções", estrategia, 2, 2, null);
        interfaceGrafica.showDialog();

        if (interfaceGrafica.wasCanceled()) {
            IJ.showMessage("PlugIn cancelado!");
        }if (interfaceGrafica.wasOKed()) {
            ImageProcessor processadorOriginal = imagemOriginal.getProcessor();
            ImageProcessor processadorAtual = imagem.duplicate().getProcessor(); 
            String respostaRadioButton = interfaceGrafica.getNextRadioButton();

            switch (respostaRadioButton) {
                case "Dilatação":
                	aplicandoDilatacao(processadorAtual,processadorOriginal);
                    ImagePlus imagem_dilatada = new ImagePlus("Imagem Dilatada", processadorAtual);
                    imagem_dilatada.show();
                    break;

                case "Erosão":
                	aplicandoErosao(processadorAtual,processadorOriginal);
                    ImagePlus imagem_erosao = new ImagePlus("Imagem Erosão", processadorAtual);
                    imagem_erosao.show();
                    break;

                case "Abertura":
                	ImageProcessor processador_Abertura = imagem.duplicate().getProcessor(); 
                	aplicandoErosao(processadorAtual,processadorOriginal);
                	aplicandoDilatacao(processador_Abertura,processadorAtual);
                    ImagePlus imagem_abertura = new ImagePlus("Imagem Abertura", processador_Abertura);
                    imagem_abertura.show();
                    break;
                    
                case "Fechamento":
                	ImageProcessor processador_Fechamento = imagem.duplicate().getProcessor(); 
                	aplicandoDilatacao(processadorAtual,processadorOriginal);
                	aplicandoErosao(processador_Fechamento,processadorAtual);
                    ImagePlus imagem_fechamento = new ImagePlus("Imagem Fechamento", processador_Fechamento);
                    imagem_fechamento.show();
                    break;

                case "Borda":
                	aplicandoBorda(processadorAtual,processadorOriginal);
                    ImagePlus imagem_borda = new ImagePlus("Imagem Borda", processadorAtual);
                    imagem_borda.show();
                    break;

                default:
                    IJ.log("Nenhuma estratégia válida selecionada");
                    break;
            }
        }
    }

    @Override
    public boolean dialogItemChanged(GenericDialog interfaceGrafica, AWTEvent e) {
        return true;
    }
    
    public void aplicandoDilatacao(ImageProcessor processadorAtual, ImageProcessor processadorOriginal) {
        int width = processadorOriginal.getWidth();
        int height = processadorOriginal.getHeight();

        int kernel[][] = {
            { 1, 1, 1 },
            { 1, 1, 1 },
            { 1, 1, 1 }
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int minValorPixel = 255;
                //Anda pelos vizinhos e aplica a dilatação
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int valorPixelVizinho = processadorOriginal.getPixel(x + kx, y + ky) * kernel[ky + 1][kx + 1];;
                        //Se o valor do vizinho for menor (mais preto), ele domina
                        if (valorPixelVizinho < minValorPixel) {
                        	minValorPixel = limitador(valorPixelVizinho);
                        }
                    }
                }
                
                processadorAtual.putPixel(x, y, minValorPixel);
            }
        }
    }
    
    public void aplicandoErosao(ImageProcessor processadorAtual, ImageProcessor processadorOriginal) {
        int width = processadorOriginal.getWidth();
        int height = processadorOriginal.getHeight();

        int kernel[][] = {
            { 1, 1, 1 },
            { 1, 1, 1 },
            { 1, 1, 1 }
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int minValorPixel = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int valorPixelVizinho = processadorOriginal.getPixel(x + kx, y + ky) * kernel[ky + 1][kx + 1];;
                        
                        if (valorPixelVizinho > minValorPixel) {
                        	minValorPixel = limitador(valorPixelVizinho);
                        }
                    }
                }
                
                processadorAtual.putPixel(x, y, minValorPixel);
            }
        }
    }

    public void aplicandoBorda(ImageProcessor processadorAtual, ImageProcessor processadorOriginal) {
        int width = processadorOriginal.getWidth();
        int height = processadorOriginal.getHeight();
        aplicandoErosao(processadorAtual, processadorOriginal);

        //Diferença entre a imagem original e a imagem erodida para obter a borda
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int valorOriginal = processadorOriginal.getPixel(x, y);
                int valorErodido = processadorAtual.getPixel(x, y);
                int valorBorda = valorErodido - valorOriginal;
                
                valorBorda = limitador(valorBorda);
                
                processadorAtual.putPixel(x, y, valorBorda);
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

