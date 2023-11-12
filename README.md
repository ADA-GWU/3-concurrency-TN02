<br/>
<p align="center">
  <a href="https://github.com/TN02/">
    <img src="https://avatars.githubusercontent.com/u/68858841?s=200&v=4" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">CSCI 6221 – Advanced Software Paradigms - Nazim Talibzade</h3>

  <p align="center">
    Assignment 3
    <br/>
    <br/>
  </p>
</p>



## Table Of Contents

* [About the Task](#task)
* [Built With](#built-with)
* [Author](#author)

## Task

To find the average color for the (square size) x (square size) boxes in the given image and set the color of the whole square to this average color.

Program accepts three values from the command line as input: file name, square size and the processing mode (S or M):

file name: the name of the graphic file of jpg format (no size constraints)
square size: the side of the square for the averaging
processing mode: 'S' - single threaded and 'M' - multi threaded

In single-threaded mode, the programme begins at the top left (0,0 coordinates) and changes the pixels in the given box size to the average colour. The "blurring" process works from left to right; when one row is finished, the boxes on the next row begin "blurring."

Multiple threads (the number of computer cores) run concurrently in multi-threaded mode.

Finally, after the colours have been changed, the image is saved in the "result.jpg" file.

### Usage:

1. Run the Java file. Ex: Main.java somefile.jpg 5 S
2. You will see the process in real-time
3. The resulting file will be saved with the name: result.jpg

## Built With

Java

## Author

* **Nazim Talibzade**
