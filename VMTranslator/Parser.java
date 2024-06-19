


public class Parser {
    
    public Parser(){}

    public String[] getParsed(String line){
        String[] array = line.split(" ");
        return array;
    }
    

    public String trimString(String line){
        StringBuilder preTrim = new StringBuilder(line);
        int i = 0;
        
        while(i < preTrim.length()){
            if(preTrim.charAt(i)=='/'){
                preTrim.delete(i, preTrim.length());
                break;
            }
            if(preTrim.charAt(i)!='/'){
                i++;
            }
        }
        String res = preTrim.toString();
        res = res.trim();
        return res;
    }


}
