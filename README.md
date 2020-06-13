# About

Prov-Dominoes is a tool designed to explore provenance data interactively, by organizing its relationships into multiple matrices treated as dominoes. The dominoes can be visually connected to derive additional dominoes or have their contents explored through a set of matrix operations and transformations. The tool allows data combination and visualization over provenance data with GPU capabilities. Among the exploratory analysis features are: filters, Eigenvector Centrality, and Transitive Closure. Finally, the tool is compatible with the [PROV-N notation](https://www.w3.org/TR/prov-n/), allowing its adoption in different domains and applications.

This project was initiated by Victor Alencar and professors Leonardo Murta and Jose Ricardo da Silva Junior during Victor's master course at Universidade Federal Fluminense.

![Prov-Dominoes GUI](https://github.com/gems-uff/prov-dominoes/blob/media/gui.png?raw=true)

*This tool is a fork of [Dominoes](https://github.com/gems-uff/dominoes), a tool designed to assist exploratory analysis on data from Git repositories.*

# Team

* Victor Alencar (joined in January 2019)
* Jose Ricardo da Silva Junior (joined in January 2019)
* Leonardo Gresta Paulino Murta (joined in January 2019)
* Troy Kohwalter (joined in January 2020)
* Vanessa Braganholo (joined in January 2020)

# Installation

## Requirements

We assume you have [Java SE Runtime Enviroment 8](https://www.oracle.com/java/technologies/javase-jre8-downloads.html) installed on your computer. 

If GPU is available, the proper CUDA Runtime should be on the same directory of the application. Our releases come, by default, with the CUDA Runtime for Windows 64-bits. 

If you intend to run the GPU capabilities from other environments, please refer to [Compilation for other environments](https://github.com/gems-uff/prov-dominoes/wiki/Compilation).

**CUDA Runtime for Windows 64-bits: [cudart64_101.dll](https://github.com/gems-uff/prov-dominoes/blob/master/prov-cuda/src/main/resources/lib/win_64/cudart64_101.dll)**

## Usage
To use Prov-Dominoes, you can download one of the [latest releases](https://github.com/gems-uff/prov-dominoes/releases/latest), extract it and execute the JAR File:
```
java -jar prov-dominoes-vX.YY.jar
```

# Performance Assessment

## Requirements
**It's necessary [Java SE Runtime Environment 8](https://www.oracle.com/java/technologies/javase-jre8-downloads.html) 64bits to run the performance assessment.**

## On windows...

### Running the performance assessment for CPU...
Type the following in a command prompt (do not work on PowerShell!):
```
> prov-dominoes.bat eval:cpu
```
Estimated time for completion: ~20 minutes. In the end, a [performance-results-cpu.txt](https://github.com/gems-uff/prov-dominoes/blob/master/performance-assessment/performance-results-cpu.txt) file will be generated in the same directory of the batch script.

### Running the performance assessment for GPU...
Type the following in a command prompt (do not work on PowerShell!):
```
> prov-dominoes.bat eval:gpu
```
Estimated time for completion: ~5 minutes. In the end, a [performance-results-gpu.txt](https://github.com/gems-uff/prov-dominoes/blob/master/performance-assessment/performance-results-gpu.txt) file will be generated in the same directory of the batch script.

## On other environments...

### Running the performance assessment for CPU...
```
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=CPU --script=samples\twitter\subcollection-1\script2-user-types-mentions.ces >> performance-results-cpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=CPU --script=samples\twitter\subcollection-2\script2-user-types-mentions.ces >> performance-results-cpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=CPU --script=samples\twitter\subcollection-3\script2-user-types-mentions.ces >> performance-results-cpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=CPU --script=samples\twitter\subcollection-4\script2-user-types-mentions.ces >> performance-results-cpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=CPU --script=samples\twitter\subcollection-5\script2-user-types-mentions.ces >> performance-results-cpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=CPU --script=samples\twitter\subcollection-6\script2-user-types-mentions.ces >> performance-results-cpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=CPU --script=samples\twitter\subcollection-7\script2-user-types-mentions.ces >> performance-results-cpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=CPU --script=samples\twitter\script2-user-types-mentions.ces >> performance-results-cpu.txt
```
### Running the performance assessment for GPU...
```
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=GPU --script=samples\twitter\subcollection-1\script2-user-types-mentions.ces >> performance-results-gpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=GPU --script=samples\twitter\subcollection-2\script2-user-types-mentions.ces >> performance-results-gpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=GPU --script=samples\twitter\subcollection-3\script2-user-types-mentions.ces >> performance-results-gpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=GPU --script=samples\twitter\subcollection-4\script2-user-types-mentions.ces >> performance-results-gpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=GPU --script=samples\twitter\subcollection-5\script2-user-types-mentions.ces >> performance-results-gpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=GPU --script=samples\twitter\subcollection-6\script2-user-types-mentions.ces >> performance-results-gpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=GPU --script=samples\twitter\subcollection-7\script2-user-types-mentions.ces >> performance-results-gpu.txt
$ java -Xms4096m -Dfile.encoding=utf8 -jar prov-dominoes-vX.YY.jar --mode=GPU --script=samples\twitter\script2-user-types-mentions.ces >> performance-results-gpu.txt
```

# Documentation

* [Prov-Dominoes Wiki](https://github.com/gems-uff/prov-dominoes/wiki)
* [Prov-Dominoes Paper](http://#pending)

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
