ImageJ Java Scripts

This repository contains Java scripts developed to streamline image manipulation and processing tasks using ImageJ. The scripts automate various workflows, enabling you to perform batch operations or customized image alterations with ease.

#Combining Images

This script, Combinando_Imagens.java, merges three separate RGB channels (red, green, and blue) into a single composite RGB image.

Functionality:

Prompts for User Input: A dialog box appears, requesting titles for each channel image (red, green, and blue) you want to combine.
Retrieves Images: The script retrieves the specified images based on the provided titles from the currently opened images in ImageJ.
Validates Input: An error message pops up if any of the entered titles don't correspond to existing images.
Creates New Image: A new RGB image with the dimensions matching the retrieved images is created.
Merges Channels: The script iterates through each pixel of the retrieved images, extracts the red, green, and blue channel values, and combines them into a single RGB value. This RGB value is then assigned to the corresponding pixel in the new composite image.
Displays Result: The final merged RGB image is displayed in ImageJ.
