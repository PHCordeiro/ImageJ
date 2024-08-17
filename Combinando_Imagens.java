import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.plugin.PlugIn;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.IJ;
import ij.WindowManager;

public class Combinando_Imagens implements PlugIn {
    public void run(String arg) {
    	//Cria uma caixa de diálogo genérica
        GenericDialog caixaDialogo = new GenericDialog("Transformador RGB");
        caixaDialogo.addStringField("Titulo do canal red:", "");
        caixaDialogo.addStringField("Titulo do canal green:", "");
        caixaDialogo.addStringField("Titulo do canal blue:", "");
        caixaDialogo.showDialog();

        if (caixaDialogo.wasCanceled()) return;

        String redTitle = caixaDialogo.getNextString();
        String greenTitle = caixaDialogo.getNextString();
        String blueTitle = caixaDialogo.getNextString();

        ImagePlus redImage = WindowManager.getImage(redTitle);
        ImagePlus greenImage = WindowManager.getImage(greenTitle);
        ImagePlus blueImage = WindowManager.getImage(blueTitle);

        if (redImage == null || greenImage == null || blueImage == null) {
            IJ.error("Alguma imagem não existe");
            return;
        }

        ImagePlus rgbImage = NewImage.createRGBImage("Imagem RGB", redImage.getWidth(), redImage.getHeight(), 1, NewImage.FILL_BLACK);

        ImageProcessor redProcessor = redImage.getProcessor();
        ImageProcessor greenProcessor = greenImage.getProcessor();
        ImageProcessor blueProcessor = blueImage.getProcessor();
        ImageProcessor rgbProcessor = rgbImage.getProcessor();

        for (int x = 0; x < redImage.getWidth(); x++) {
            for (int y = 0; y < redImage.getHeight(); y++) {
                int red = redProcessor.get(x, y) & 0xff;
                int green = greenProcessor.get(x, y) & 0xff;
                int blue = blueProcessor.get(x, y) & 0xff;
                int rgb = (red << 16) | (green << 8) | blue;
                rgbProcessor.set(x, y, rgb);
            }
        }
        rgbImage.show();
    }
}
