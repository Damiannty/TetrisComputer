
import java.util.Scanner;
import java.io.*;


public class Main {
    
    public static void main(String[] args) throws IOException{
        Parser parser = new Parser();
        CodeWriter codeWriter = new CodeWriter();
        File file = new File(args[0]);
        
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("output.asm"))){

            if(file.isDirectory()){
                System.out.println("Translating directory...");
                File[] files = file.listFiles();
                for (File i : files){
                    if(i.getName().contains("Main") && !(files[0].getName().contains("Main"))){
                        File aux = files[0];
                        files[0] = i;
                        files[1] = aux;
                    }
                }
                //Bootstrap code
                writer.write(codeWriter.bootstrapCode());
                for(File run : files){
                    if(run.isFile() && run.getName().endsWith(".vm")){
                        TranslateFile(run, parser, codeWriter, writer);
                    } 
                }
                
            } else {
                System.out.println("Translating single file...");
                writer.write(codeWriter.bootstrapCode());
                TranslateFile(file, parser, codeWriter, writer);
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }   
    }
    
    public static void TranslateFile(File inFile, Parser parser, CodeWriter codeWriter, BufferedWriter writer){
        
        try(Scanner reader = new Scanner(inFile)){

            while(reader.hasNextLine()){
                String line = reader.nextLine();
                String command = parser.trimString(line);
                String[] array= parser.getParsed(command);
                //////////////////////////////////////////////////////////////////////
                if(isEmptyOrComment(line)){   // Is the line empty or comment?
                    continue;
                }
                if(isMemorySegment(array[0])){    //Is the line a Memory Operation?
                    String asm = codeWriter.translateMemory(array);
                    writer.write(asm);
                }
                if(isOperation(array[0])){    //Is the line an Arithmeric or Logical operation?
                    String asm2 = codeWriter.translateOperation(command);
                    writer.write(asm2);
                }
                if(isBranching(array[0])){    // Is the line a Branching command?
                   String asm3 = codeWriter.translateBranching(array);
                   writer.write(asm3);
                }
                if(isFunctionCommand(array[0])){    // Is the line a function command
                    String asm4 = codeWriter.translateFunctionCommand(array);
                    writer.write(asm4);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    public static boolean isMemorySegment(String line){
        return line.equals("pop") || line.equals("push");
    }
    public static boolean isOperation(String line){
        return line.contains("add")||line.contains("sub")||line.contains("neg")||line.contains("eq")||line.contains("gt")||line.contains("lt")||line.contains("and")||line.contains("or")||line.contains("not");
    }
    public static boolean isEmptyOrComment(String line){
        
        for(char i : line.toCharArray()){
            if(i != ' ' && i != '/'){
                return false;
            }
            if(i == '/'){
                return true;
            }
        }
        return true;
    }
    public static boolean isBranching(String line){
        return line.contains("goto") || line.contains("if-goto") || line.contains("label");
    }
    public static boolean isFunctionCommand(String line){
        return line.contains("call") || line.contains("function") || line.contains("return");
    }
}

