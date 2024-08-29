import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;

public class FIltros_Lineares implements PlugIn, DialogListener {
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
        GenericDialog interfaceGrafica = new GenericDialog("Filtros Lineares");
        interfaceGrafica.addDialogListener(this);

        String[] estrategia = { "Passa-Baixas de Média", "Passa-Alta", "Filtro de Borda" };
        interfaceGrafica.addRadioButtonGroup("Botões para escolher uma dentre várias estratégias", estrategia, 1, 2, null);
        interfaceGrafica.showDialog();

        if (interfaceGrafica.wasCanceled()) {
            IJ.showMessage("PlugIn cancelado!");
        }if (interfaceGrafica.wasOKed()) {
            ImageProcessor processadorOriginal = imagemOriginal.getProcessor();
            ImageProcessor processadorAtual = imagem.duplicate().getProcessor(); 
            String respostaRadioButton = interfaceGrafica.getNextRadioButton();

            switch (respostaRadioButton) {
                case "Passa-Baixas de Média":
                    aplicandoPassaBaixa(processadorAtual, processadorOriginal);

                    ImagePlus imagem_passa_baixa = new ImagePlus("Passa-Baixas de Média", processadorAtual);
                    imagem_passa_baixa.show();
                    break;

                case "Passa-Alta":
                    aplicandoPassaAlta(processadorAtual, processadorOriginal);

                    ImagePlus imagem_passa_alta = new ImagePlus("Passa-Alta", processadorAtual);
                    imagem_passa_alta.show();
                    break;

                case "Filtro de Borda":
                	aplicandoFiltroBorda(processadorAtual, processadorOriginal);

                    ImagePlus imagem_borda = new ImagePlus("Filtro de Borda", processadorAtual);
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

    public void aplicandoPassaBaixa(ImageProcessor processadorAtual, ImageProcessor processadorOriginal) {
        int width = processadorOriginal.getWidth();
        int height = processadorOriginal.getHeight();

        //Ignorei as bordas enquanto ando
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int soma = 0;
                //Somando todos os pixels do kernel
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        soma += processadorOriginal.getPixel(x + kx, y + ky);
                    }
                }

                int media = soma / 9;

                processadorAtual.putPixel(x, y, media);
            }
        }
    }

    public void aplicandoPassaAlta(ImageProcessor processadorAtual, ImageProcessor processadorOriginal) {
        int width = processadorOriginal.getWidth();
        int height = processadorOriginal.getHeight();

        int kernel[][] = {
            { -1, -1,  -1 },
            {-1,  9, -1 },
            { -1, -1,  -1 }
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
            	//Armazenará o valor final
                int valorPixel = 0;
                
                //Percorro os pixels vizinhos, multiplicando pela matriz kernel e somando
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        valorPixel += processadorOriginal.getPixel(x + kx, y + ky) * kernel[ky + 1][kx + 1];
                    }
                }

                valorPixel = limitador(valorPixel);

                processadorAtual.putPixel(x, y, valorPixel);
            }
        }
    }
    
    public void aplicandoFiltroBorda(ImageProcessor processadorAtual, ImageProcessor processadorOriginal) {
        int width = processadorOriginal.getWidth();
        int height = processadorOriginal.getHeight();

        int kernel[][] = {
            { 1, 1,  1 },
            { 1, -2, 1 },
            { -1, -1, -1 }
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int valorPixel = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        valorPixel += processadorOriginal.getPixel(x + kx, y + ky) * kernel[ky + 1][kx + 1];
                    }
                }

                valorPixel = limitador(valorPixel);

                processadorAtual.putPixel(x, y, valorPixel);
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

