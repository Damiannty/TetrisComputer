

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class CodeWriter {

    HashMap<String, String> map;
    static int labelCode = 0;
    static int retAddrNum = 0;

    public CodeWriter(){
        
        map = new HashMap<String, String>();
        map.put("local", "LCL");
        map.put("argument", "ARG");
        map.put("this", "THIS");
        map.put("that", "THAT");

        //Binary operations
        map.put("add", "D=D+M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        map.put("sub", "D=-D\nD=D+M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        map.put("eq", "JEQ\n");
        map.put("gt", "JGT\n");
        map.put("lt", "JLT\n");
        map.put("and", "D=D&M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        map.put("or", "D=D|M\n@SP\nA=M\nM=D\n@SP\nM=M+1");

    }

    public String translateMemory(String[] command){
        
        if(isSimpleCommand(command[1])){
            String res = transSimpleCommand(command);
            return res;
                
        }
        if(command[1].equals("constant")){
            String res = transConstant(command);
            return res;
        }
        if(command[1].equals("static")){
            String res = transStatic(command);
            return res;
        }
        if(command[1].equals("temp")){
            String res = transTemp(command);
            return res;
        }
        if(command[1].equals("pointer")){
            String res = transPointer(command);
            return res;
        }
        return "XD";
        
    }
    
    //Translation methods down here ===================================================
    
    public String transSimpleCommand(String[] command){
        // Is it pop or push?
        if(command[0].equals("push")){
            String comment = commentForTrans(command);
            String i = command[2];
            String mem = map.get(command[1]);
            
            return "//"+comment+"\n"+"@"+mem+"\nD=M\n@"+i+"\nD=D+A\nA=D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
            
        } else {
            String comment = commentForTrans(command);
            String i = command[2];
            String mem = map.get(command[1]);
            
            return "//"+comment+"\n@"+mem+"\nD=M\n@"+i+"\nD=D+A\n@addr\nM=D\n@SP\nM=M-1\nA=M\nD=M\nM=0\n@addr\nA=M\nM=D\n";
        }
        
    }
    public String transConstant(String[] command){
        String comment = commentForTrans(command);
        String i = command[2];
        
        return "//"+comment+"\n@"+i+"\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
    }
    public String transStatic(String[] command){
        File file = new File("C:\\Users\\danij\\Desktop\\I love Java\\VMTranslator\\program.txt");
        
        if(command[0].equals("pop")){
            String fileName = file.getName();
            String comment = commentForTrans(command);
            String i = command[2];
            
            return "//"+comment+"\n@SP\nM=M-1\nA=M\nD=M\nM=0\n@"+fileName+"."+i+"\nM=D\n";
            
        } 
        if(command[0].equals("push")){
            String fileName = file.getName();
            String comment = commentForTrans(command);
            String i = command[2];
            
            return "//"+comment+"\n@"+fileName+"."+i+"\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
        }
        return "XDXD";
    }
    public String transTemp(String[] command){
        if(command[0].equals("push")){
            String comment = commentForTrans(command);
            String i = command[2];
            
            return "//"+comment+"\n@5\nD=A\n@"+i+"\nD=D+A\n@addr\nM=D\nA=M\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
        }
        if(command[0].equals("pop")){
            String comment = commentForTrans(command);
            String i = command[2];
            
            return "//"+comment+"\n@5\nD=A\n@"+i+"\nD=D+A\n@addr\nM=D\n@SP\nM=M-1\nA=M\nD=M\nM=0\n@addr\nA=M\nM=D\n";
        }
        return "XDXDXD";
    }
    public String transPointer(String[] command){
        String point;
        if(command[2].equals("0")){
            point = "THIS";
        } else {
            point = "THAT";
        }
        ////////////////////////////////////////////
        if(command[0].equals("push")){
            String comment = commentForTrans(command);
            
            return "//"+comment+"\n@"+point+"\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
        }
        if(command[0].equals("pop")){
            String comment = commentForTrans(command);
            
            return "//"+comment+"\n@SP\nM=M-1\nA=M\nD=M\nM=D\nM=0\n@"+point+"\nM=D\n";
        }
        return "4XD";
    }
    public String translateOperation(String command){
        String res = "//"+command+"\n@SP\nM=M-1\nA=M\nD=M\n";
        if(isSingleOperation(command)){
            if(command.equals("not")){
                res = res + "D=!D\n";
            }
            if(command.equals("neg")){
                res = res + "D=-D\n";
            }
            
            res = res + "@SP\nA=M\nM=D\n@SP\nM=M+1\n";
            return res;
        } else {                                          // Its a binary operation
            res = res + "M=0\n@SP\nM=M-1\nA=M\n";
            if(command.equals("add")||command.equals("sub")){
                res = res + map.get(command);
                return res;
            }
            if(command.equals("gt")||command.equals("lt")||command.equals("eq")){
                res = res + "D=-D\nD=D+M\n@TRUE"+labelCode+"\nD;"+map.get(command)+"D=0\n@FALSE"+labelCode+"\n0;JMP\n(TRUE"+labelCode+")\nD=-1\n(FALSE"+labelCode+")\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
                labelCode++;
                return res;
            }
            if(command.equals("and")||command.equals("or")){
                res = res + map.get(command);
                return res;
            }

        }
        System.out.println(command);
        return "How did you reach this place?...";
    }

    public String bootstrapCode(){
        String[] syscall = {"call", "Sys.init", "0"};

        return "//Bootstrapping..."+"\n@256\nD=A\n@0\nM=D\n"+this.translateFunctionCommand(syscall)+"\n//Bootstapping ended.\n";
    }

    public String translateBranching(String[] command){
        if(command[0].contains("label")){
            return "//"+command[1]+"\n("+command[1]+")\n";
        }
        if(command[0].contains("if")){
            return "//"+command[0]+" "+command[1]+"\n@SP\nM=M-1\nA=M\nD=M\nM=0\n@"+command[1]+"\nD;JNE\n";
        } else {
            return "//"+command[0]+" "+command[1]+"\n@"+command[1]+"\n0;JMP\n";
        }
        
    }
    public String translateFunctionCommand(String[] command){
        if(command[0].contains("call")){
            //Save frame
            String res = "//"+commentForTrans(command)+"\n@RETADDR"+retAddrNum+"\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n" + frame();
            res+="//Setting Arguments..."+"\n@5\nD=A\n@"+command[2]+"\nD=D+A\n@SP\nD=M-D\n@ARG\nM=D\n";
            res+="@"+command[1]+"\n0;JMP\n(RETADDR"+retAddrNum+")\n";
            retAddrNum++;
            return res;
        }
        if(command[0].contains("function")){
            String res = "//"+commentForTrans(command)+"\n("+ command[1] +")"+"\n@SP\nD=M\n@LCL\nM=D\n";
            int count = Integer.valueOf(command[2]);
            String[] iteration = {"push", "constant", "0"};
            for(int i = 0; i < count; i++){
                res+=transConstant(iteration);
            }
            return res;
        } else {
            //TODO: Implement the return command. This will be more cubbersome.
            String res= "//return; EndFrame=LCL\n@LCL\nD=M\n@endFrame\nM=D\n";
            res += "//retAddr=*(ef-5)\n@5\nD=D-A\nA=D\nD=M\n@retAddr\nM=D\n";
            res += "//*ARG=pop()\n@SP\nM=M-1\nA=M\nD=M\nM=0\n@ARG\nA=M\nM=D\nD=A+1\n@SP\nM=D\n";
            res += returnFrame();
            res += "//return Control\n@retAddr\nA=M\n0;JMP\n";
            return res;
        }
    }
    public static String frame(){
        String[] mem = {"LCL", "ARG", "THIS", "THAT"};
        String res = "";
        for(int i = 0; i < 4; i++){
            res+="@"+mem[i]+"\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
        }
        return res;
    }
    public static String returnFrame(){
        String[] mem = {"THAT", "THIS", "ARG", "LCL"};
        String res = "//FrameRetrival\n";
        for(int i = 0; i < 4; i++){
            res += "@endFrame\nD=M\n@"+(i+1)+"\nD=D-A\nA=D\nD=M\n@"+mem[i]+"\nM=D\n";
        }
        return res;
    }
    
    // Boolean Method down here ======================================================
    
    public static boolean isSimpleCommand(String command){
       return command.equals("local") || command.equals("argument") || command.equals("this") || command.equals("that");
    }
    public String commentForTrans(String[] command){
        return command[0] + " " + command[1] + " " + command[2];
    }

    public static boolean isSingleOperation(String command){
        return command.equals("neg")||command.equals("not"); 
    }
    
}
