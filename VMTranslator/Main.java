import java.util.Scanner;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class Main {

    public static void main(String[] args){
        Parser parser = new Parser();
        CodeWriter codeWriter = new CodeWriter();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("output.asm"))){
            try(Scanner reader = new Scanner(Paths.get("program.txt"))){
                while(reader.hasNextLine()){
                    String line = reader.nextLine();
                    String command = parser.trimString(line);
                    String[] array= parser.getParsed(command);
                    //////////////////////////////////////////////////////////////////////
                    if(isEmptyOrComment(line)){   // Is the line empty or comment?
                        continue;
                    }
                    if(isMemorySegment(line)){    //Is the line a Memory Operation?
                        String asm = codeWriter.translateMemory(array);
                        writer.write(asm);
                    }
                    if(isOperation(line)){    //Is the line an Arithmeric or Logical operation?
                        String asm2 = codeWriter.translateOperation(command);
                        writer.write(asm2);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
            
        
    }

    public static boolean isMemorySegment(String line){
        return line.contains("pop") || line.contains("push");
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
}

