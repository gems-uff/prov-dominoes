# About

Prov-Dominoes is a tool designed to explore provenance data interactively, by organizing its relationships into multiple matrices treated as domino tiles. The tiles can be visually connected to derive additional domino tiles or have its contents explored through a set of matrix operations. Besides the operations, a set of filters can be applied to the matrix contents to ease visualization or prepare for some combination.

The domino-matrix abstraction was first conceived by [Dominoes](https://github.com/gems-uff/dominoes), a tool designed to assist exploratory analysis on data from Git repositories. Dominoes uses GPU capabilities to achieve efficient processing of large matrices that represent Git repository data (developers, commits, modified files, etc.). Prov-Dominoes distinguishes itself by forking Dominoes to allow data combination and visualization over provenance data, but retaining the GPU capabilities of the latter. Extending the operations over domino tiles introduced by Dominoes, our tool aggregates other features such as filters, Eigenvector Centrality and Transitive Closure.

The tool is compatible with the PROV-N notation, allowing its adoption in different domains and applications. The provenance data is processed to generate provenance pieces, the building blocks of the tool. 

# Team

* Victor Alencar (joined in January 2019)
* Jose Ricardo da Silva Junior (joined in January 2019)
* Leonardo Gresta Paulino Murta (joined in January 2019)
* Troy Kohwalter (joined in January 2020)
* Vanessa Braganholo (joined in January 2020)

# Documentation

* [Prov-Dominoes: An Exploratory Analysis Approach for Provenance Data](http://http://www2.ic.uff.br/~leomurta/papers/provdominoes2020.pdf)
* [Dominoes: An Interactive Exploratory Data Analysis tool for Software Relationships](http://www2.ic.uff.br/~leomurta/papers/silvajunior2020.pdf)
* [Niche vs. breadth: Calculating expertise over time through a fine-grained analysis](http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=7081851&tag=1)
* [Multi-Perspective Exploratory Analysis of Software Development Data](http://www.worldscientific.com/doi/abs/10.1142/S0218194015400033)
* [Exploratory Data Analysis of Software Repositories via GPU Processing](http://ksiresearchorg.ipage.com/seke/seke14paper/seke14paper_173.pdf)

# Usage

To run Prov-Dominoes, you can download one of the recent releases (latest: [v1.6](https://github.com/gems-uff/prov-dominoes/releases/download/v1.6/prov-dominoes-v1.6.zip)) and execute the JAR File or the prov-dominoes.bat for Windows. It's necessary to have Java Runtime Environment on the target machine.

# Performance Assessment

To execute the performance assesment for CPU, type the following in a command prompt (Windows):

```
prov-dominoes.bat eval:cpu
```
Estimated time for completion: ~20 minutes. At the end, a [performance-results-cpu.txt](https://github.com/gems-uff/prov-dominoes/blob/master/performance-assessment/performance-results-cpu.txt) file will be generated in the same directory of the batch script.

To execute the performance assesment for GPU, type the following in a command prompt (Windows):
```
prov-dominoes.bat eval:gpu
```
Estimated time for completion: ~5 minutes. At the end, a [performance-results-gpu.txt](https://github.com/gems-uff/prov-dominoes/blob/master/performance-assessment/performance-results-gpu.txt) file will be generated in the same directory of the batch script.

# Compilation

To compile Prov-Dominoes, it is necessary:
- Install [PROV-Matrix](https://github.com/gems-uff/prov-matrix) maven module; and
- Compile and install de PROV-CUDA module, available in the prov-cuda directory.

The PROV-CUDA module has precompiled CUDA library for windows x64. For compiling library for other architectures please refer to the [makefile](https://github.com/gems-uff/prov-dominoes/blob/master/prov-cuda/src/cuda/makefile).

# Development

* [Source Code](https://github.com/gems-uff/prov-dominoes)
* [Issue Tracking](https://github.com/gems-uff/prov-dominoes/issues)

# Technologies

* [Java](http://java.com)
* [JavaFX](http://docs.oracle.com/javafx/)
* [W3C PROV](https://www.w3.org/TR/2013/NOTE-prov-overview-20130430/)
* [PROV-Matrix](https://github.com/gems-uff/prov-matrix)
* [CUDA](http://www.nvidia.com/object/cuda_home_new.html)
  - [CUSP Library](https://cusplibrary.github.io/)
  - [EIGEN Library](http://eigen.tuxfamily.org/index.php?title=Main_Page)


# License

Copyright (c) 2018-2020 Universidade Federal Fluminense (UFF)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
