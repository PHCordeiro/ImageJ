# Image Processing Plugins for ImageJ

This repository contains a collection of plugins developed for image processing using ImageJ. These plugins perform a variety of operations on images, from basic transformations to more complex image analyses. 

## Plugins and Scripts

### Combinando_Images.java
- **Description:** A plugin to generate an RGB image by combining 8-bit images produced by previous plugins, each representing a channel.

### Dividindo_Imagens.java
- **Description:** A plugin to create an RGB image by combining 8-bit images from previous plugins, representing different channels.

### Dividindo_Imagens_3_Cores.java
- **Description:** This plugin applies Lookup Tables (LUTs) to each resulting image channel. Separate LUTs are created for red, green, and blue. For example, a red LUT is made of three vectors (R, G, and B), each with 256 positions, where the R vector stores values from 0 to 255, while the others store 0 in all positions. These vectors are used to create the LUT object to be applied to the image.

### Expansao_Equalizacao_Histograma.java
- **Description:** A plugin implementing Histogram Expansion and Equalization techniques. It includes a graphical interface with radio buttons to choose the desired operation. The Ok and Cancel buttons allow for applying or canceling the operations.

### Filtros_Lineares.java
- **Description:** A plugin that applies a mean low-pass filter, a high-pass filter, and an edge detection filter as described in class slides. It includes a graphical interface with radio buttons for selecting the filter to be applied, using 3x3 dimension kernels.

### Filtros_Nao_Lineares.java
- **Description:** A plugin applying the non-linear Sobel filter, both vertically and horizontally, to a current image. The results are presented in two new images, with a third image created by combining the two results (formula provided in the last slide of the class).

### Imagens_ROI.java
- **Description:** A plugin to identify existing ROIs (Regions of Interest) in an image and save each one as an individual image file in the system. The plugin prompts for an input and an output directory. All images in the input directory have their ROIs extracted and saved in the output directory.

### Menu_RGB_para_CInza.java
- **Description:** A plugin to convert an RGB image to grayscale using one of three methods described in the book *Principles of Digital Image Processing* (Page 202 - Section 8.2.1). The graphical interface allows users to select one of the three strategies via radio buttons, and a checkbox is provided to create a new grayscale image without altering the original.

### Menu_Slider.java
- **Description:** A plugin to adjust brightness and contrast, and to apply solarization and desaturation to an image. The graphical interface features four sliders (one for each technique), along with "Ok" and "Cancel" buttons. Moving the sliders adjusts the image characteristics, and pressing "Ok" applies changes permanently, while "Cancel" reverts to the original image.

### Morfologias_Matematicas.java
- **Description:** A plugin for applying morphological operations on binary images, implementing dilation, erosion, closing, opening, and outline techniques. The graphical interface includes radio buttons for selecting the desired technique. A square structuring element with 3x3 dimensions is used for all techniques.

### processamento_de_imagens.py
- **Description:** A Python script for a sequence of image processing steps to identify regions of interest using OpenCV in Colab. Steps include:
  - Reading the image (used in the ROI separation activity).
  - Converting the image to grayscale.
  - Applying a low-pass filter.
  - Segmenting the image using Otsu's method.
  - Applying morphological operations to improve the segmented image.
  - Identifying regions of interest with different colors.
  - Creating individual images for each detected region of interest.
  - Saving the images to the file system.

### rotulando_componentes_conexos.txt
- **Description:** A script that labels each connected component with a shade of gray. It is recommended to use a small image and a limited number of connected components for testing.

### imagens_treinamento_rede_neural.py
- **Description:** A Python script to create images by combining segmented trees from the previous task with backgrounds found online. A .txt file is generated containing training data.

### yolo.py
- **Description:** A Python script to train YOLO v5 using the images generated in the `imagens_treinamento_rede_neural.py` script.
