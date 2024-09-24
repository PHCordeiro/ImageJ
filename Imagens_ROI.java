import ij.*;
import ij.io.*;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;

import java.io.File;

public class Imagens_ROI implements PlugIn {

    public void run(String arg) {
        
        DirectoryChooser diretorioEntrada = new DirectoryChooser("Diretório de entrada");
        String caminhoDiretorioEntrada = diretorioEntrada.getDirectory();

        if (caminhoDiretorioEntrada == null) {
        	IJ.error("Nenhum Diretório de Entrada encontrado...");
            return;  
        }

        DirectoryChooser diretorioSaida = new DirectoryChooser("Diretório de saída");
        String caminhoDiretorioSaida = diretorioSaida.getDirectory();
        
        if (caminhoDiretorioSaida == null) {
        	IJ.error("Nenhum Diretório de Saída encontrado...");
            return;
        }
        
        //Salvando todos os arquivos numa lista
        File imagensDiretorioEntrada = new File(caminhoDiretorioEntrada);
        String imagens[] = imagensDiretorioEntrada.list();
        
        if (imagens != null) {
            for (String file : imagens) {
                if (file.endsWith(".png") || file.endsWith(".jpg")) {
                    processandoImagem(new File(caminhoDiretorioEntrada, file), caminhoDiretorioSaida);
                }
            }
        }
    }

    private void processandoImagem(File caminhoCompleto, String caminhoDiretorioSaida) {
        ImagePlus imagem = IJ.openImage(caminhoCompleto.getAbsolutePath());
        
        if (imagem == null) {
        	IJ.error("Nenhuma imagem encontrada...");
            return;
        }
        
        ImagePlus imagemOriginal = imagem.duplicate();  
        
        IJ.run(imagem, "8-bit", ""); 
        IJ.setAutoThreshold(imagem, "Otsu");
        IJ.run(imagem, "Make Binary", ""); 
        
        int areasDeInteresse = 0;
        RoiManager roiManager = RoiManager.getInstance();
        if (roiManager == null) {
            roiManager = new RoiManager();
        }
        
        for (int i = 0; i < roiManager.getCount(); i++) {
        	areasDeInteresse++;
        }
        
        if(areasDeInteresse >= 30) {
            IJ.run(imagem, "Dilate", "");
            IJ.run(imagem, "Dilate", "");
        }
        
        IJ.run(imagem, "Fill Holes", ""); 
        
        //Adicionando ROIs ao RoiManager
        IJ.run(imagem, "Analyze Particles...", "add");  
        
        int totalRois = roiManager.getCount();
        double somaArea = 0;
        for (int i = 0; i < totalRois; i++) {
            roiManager.select(imagem, i);  
            ImageStatistics stats = imagem.getStatistics();  
            somaArea += stats.area; 
        }
        double mediaArea = somaArea / totalRois;
        double limiteMinimo = mediaArea * 0.25; 
        
        //Salvando cada Roi como uma imagem individual
        //roiManager.getCount retorna numero total de rois
        for (int i = 0; i < roiManager.getCount(); i++) {
            roiManager.select(imagem, i);
            ImageStatistics stats = imagem.getStatistics(); 
            
            if (stats.area >= limiteMinimo) {
	            //Apliquei o ROI na imagem original
	            imagemOriginal.setRoi(roiManager.getRoi(i)); 
	            ImagePlus roiImage = imagemOriginal.crop(); 
	            String roiFileName = caminhoDiretorioSaida + File.separator + "ROI_" + i + "_" + caminhoCompleto.getName();
	            IJ.saveAs(roiImage, "png", roiFileName);  
            }else {
            	continue;
            }
        }
        
        //Limpar as ROIs após cada imagem processada
        roiManager.reset();
    }
}
