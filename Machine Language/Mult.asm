// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

@R0
D=M
@m1
M=D  //m1 = RAM[0]

@R1
D=M
@m2
M=D //m2 = RAM[1]

@sum
M=0

@m1
D=M
@m2
D=D-M
@BIGM1
D;JGT // if(m1 > m2) goto BIGM1

@i
M=1
(LOOP)
@m2
D=M
@i
D=D-M
@RES
D;JLT  //if checking 
@sum
D=M
@m1
D=D+M
@sum
M=D  //Storing sum in sum variable
@i
D=M+1
M=D
@LOOP
0;JMP //Loop back



(BIGM1)
@i
M=1
(LOOP2)
@m1
D=M
@i
D=D-M
@RES
D;JGT //if checking
@sum
D=M
@m2
D=D+M
@sum
M=D  //Storing sum
@i
D=M+1
M=D
@LOOP2
0;JMP  //Loop back



(RES)
@sum
D=M
@R2
M=D  //Storing in RAM[2]

(END)
@END
0;JMP




