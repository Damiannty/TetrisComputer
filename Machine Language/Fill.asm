

@isBlack
M=0
(IDLE)
@KBD
D=M
@INPUT
D;JNE  //Input Jump
@isBlack
D=M
@CLEAN
D;JGT
@IDLE
0;JMP

(INPUT)
@isBlack
M=1
@i
M=0

@SCREEN
D=A
@ScrAdr //Creating the pointer for the screen
M=D
(LOOP)
@ScrAdr
A=M
M=-1
@i    //i increment
D=M
M=M+1
@ScrAdr
M=M+1
@i
D=M
@8192
D=D-A
@LOOP
D;JLT   //Loop repeat if check
@IDLE
0;JMP

(CLEAN)
@i
M=0
@SCREEN
D=A
@ScrAdr //Creating the pointer for the screen
M=D
(LOOP2)
@ScrAdr
A=M
M=0
@i    //i increment
M=M+1

@ScrAdr
M=M+1
@i
D=M
@8192
D=D-A
@LOOP2
D;JLT   //Loop repeat if check
@IDLE
0;JMP





