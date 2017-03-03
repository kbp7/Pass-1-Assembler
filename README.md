Kevin Poon
n00900355
COP3404 Intro to Systems Software

Project 3:

Purpose: Implement a pass 1 assembler. Pass 1 constructs a symbol table, 
addresses of each instruction, and the addresses of each label.
This program utilizes hashing to create the tables and quadratic probing.
Calculating addresses is done using integers, which are then converted to 
hex when ready to print. An error is reported when it encounters an invalid
mneumonic. It is capable of reading files with incorrect formatting, such as
erroneous blank lines and too many tabs/spaces.

This program checks each instruction by first creating a table of valid
mnemonics by hashing the elements in SICOPS.txt, which includes the opcodes and
bytes consumed by each mnemonic.

START, END, BASE, WORD, RESW, BYTE, RESB are hardcoded. 

Source file: Project3.java

input files: input.txt (command line argument)

output files: output.txt (optional)

Instructions: Use the makefile for all compiling and running.
To output all to the terminal:
$make clean
$make
$make run

To output all to output.txt:
$make clean
$make
$make runout
$make out

The "make out" command opens the output.txt file.
