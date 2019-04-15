# VisualComplexIntegrator
Quick project for my theory of complex functions class to help illustrate Cauchy integral formula

Here is a sample graph of the function `1/z`:

![sample graph](https://i.imgur.com/L2rcIbg.png)

To use, set whatever expression you want in `Operation.java` (maybe something like `new Complex(1, 0).divide(in.multiply(in).add(1))` for `1/(z^2+1)` and then compile & run. Left click on various portions of the graph to begin making a path, and it will show the integral in the top left of the path. Once you are done making your path, right click, and it will automatically close the path, display the result of the closed path integral on the top left, and then clear your path so you can make a new one. You can also change the bounds of the graph in `Canvas.java`.
