package calculator;

import java.math.BigInteger;
import java.util.*;

public class Main {

    static String input;
    static StringTokenizer stringTokenizer;
    static Map<String , BigInteger> variables = new HashMap<>();
    static Stack<String> operators = new Stack<>();
    static Stack<String> postfix = new Stack<>();
    static Stack<String> postfixReverse = new Stack<>();
    static Stack<BigInteger> result = new Stack<>();
    static Scanner scanner = new Scanner(System.in);
    public static boolean varnameOk(String s){
        String regex ="\\s*[a-zA-Z]+";
        return s.matches(regex);
    }

    public static void command(String s){
        if (Objects.equals(s, "/exit")){
            System.out.println("Bye!");
            System.exit(0);
        } else if (Objects.equals(s, "/help")) {
            System.out.println("The programm calculates the sum of numbers");
            System.out.println("+ for addition");
            System.out.println("- for substraction");
            System.out.println("/help for help");
            System.out.println("/exit to quit");
        }else{
            System.out.println("Unknown command");
        }
    }

    public static void assign(String s){
        String processedInput = s.replaceAll("\\s","");
        String var = processedInput.substring(0,processedInput.indexOf("="));
        if(varnameOk(var)){
            String value = processedInput.substring(processedInput.indexOf("=")+1);
            try{
                BigInteger i = new BigInteger(value);
                variables.put(var,i);
            }catch (Exception e){
                if(variables.containsKey(value)){
                    variables.put(var, variables.get(value));
                }else{
                    System.out.println("Invalid assignement");
                }
            }
        }else{
            System.out.println("Invalid identifier");
        }
    }

    public static void getValue(String s){
        String in = s.replaceAll("\\s","");
        if (varnameOk(in)){
            if (variables.containsKey(in)){
                System.out.println(variables.get(in));
            }else{
                System.out.println("Unknown variable");
            }
        }else{
            System.out.println("Invalid identifier");
        }
    }
    public static boolean parentheseScan(String s){
        String tmp = s.replaceAll("\\s","");
        int a = 0;
        int b = 0;
        for (int j = 0; j < tmp.length(); j++){
            if(tmp.charAt(j)=='('){
                a++;
            }
        }
        for (int k = 0; k < tmp.length(); k++){
            if(tmp.charAt(k)==')'){
                b++;
            }
        }
        return b==a;
    }
    public static void stacksClear(){
        operators.clear();
        postfix.clear();
        postfixReverse.clear();
    }

    public static void inToPost(String s){
        stringTokenizer = new StringTokenizer(s);
        String tmp;
        String var;
        while(stringTokenizer.hasMoreTokens()){
            tmp = stringTokenizer.nextToken().replaceAll("\\s","");
            if (varnameOk(tmp)){
                postfix.push(tmp);
            }else if(tmp.matches("[+]*[0-9]+")){
                var = tmp.replaceAll("\\+","");
                postfix.push(var);
            }else if(tmp.matches("[-]*[0-9]+")){
                int i = tmp.lastIndexOf('-');
                if (i % 2 == 0){
                    var = tmp.substring(i+1);
                }else{
                    var = tmp.substring(i);
                }
                //postfix.push(var);
            }else if (tmp.matches("\\+*\\**-*/*")) {
                String help = "";
                if (tmp.matches("-*")) {
                    if (tmp.length() % 2 == 0) {
                        help += "+";
                    } else {
                        help += "-";
                    }
                } else if (tmp.matches("\\**")) {
                    if (tmp.matches("\\*")){
                        help += "*";
                    }else{
                        System.out.println("Invalid expression");
                        stacksClear();
                        waitForInput();
                    }
                } else if (tmp.matches("\\+*")) {
                    help += "+";
                } else if (tmp.matches("/*")) {
                    if (tmp.matches("/")){
                        help += "/";
                    }else{
                        System.out.println("Invalid expression");
                        stacksClear();
                        waitForInput();
                    }
                }
                if (operators.empty() || Objects.equals(operators.peek(), "(")) {
                    operators.push(help);
                    //System.out.println("Operators:"+operators);
                } else if (operatorRank(help) > operatorRank(operators.peek())) {
                    operators.push(help);
                    //System.out.println("Operators:"+operators);
                } else if (operatorRank(help) <= operatorRank(operators.peek())) {
                    //System.out.println("hey");

                    do {
                        //System.out.println("hey222");
                        postfix.push(operators.pop());
                        //System.out.println("Operators:"+operators);
                        //System.out.println("Postfix:"+postfix);

                    } while (!operators.empty()&& !Objects.equals(operators.peek(), "("));
                    operators.push(help);
                    //System.out.println("Operators:"+operators);
                }
            }else{
                if(tmp.contains("(")){
                    int i = tmp.lastIndexOf('(');
                    String val = tmp.substring(i+1);
                    for (int j = 0; j < i+1; j++){
                        //System.out.println("Hier los!");
                        operators.push("(");
                        //System.out.println("Operators:"+operators);
                    }
                    postfix.push(val);
                    //System.out.println("Postfix:"+postfix);
                }else if(tmp.matches("\\){2,5}")){
                    String val = tmp.substring(0,tmp.indexOf(')'));
                    int i = tmp.length() - val.length();
                    //postfix.push(val);
                    while(!(operators.empty()) && !Objects.equals(operators.peek(), "(")){
                        postfix.push(operators.pop());
                    }
                    for (int j = 0; j < i;j++) {
                        operators.pop();
                        //System.out.println("Operators:" + operators);
                    }
                }else if(tmp.contains(")")){
                    String val = tmp.substring(0,(tmp.indexOf(')')));
                    postfix.push(val);
                    while(!(operators.empty()) && !Objects.equals(operators.peek(), "(")){
                        postfix.push(operators.pop());
                        //System.out.println("Operators:" + operators);
                    }
                    operators.pop();
                }
            }
            //System.out.println("Postfix:"+postfix);
        }
        while(!operators.empty()){
            if (Objects.equals(operators.peek(), "(")){
                operators.pop();
                continue;
            }
            postfix.push(operators.pop());
        }

        while(!postfix.empty()){
            postfixReverse.push(postfix.pop());
        }
    }
    public static int operatorRank(String s){
        return switch (s) {
            case "*", "/" -> 4;
            case "+", "-" -> 2;
            default -> 0;
        };
    }

    public static BigInteger calculate(){
        String op;
        BigInteger number;
        while(!postfixReverse.isEmpty()){
            op = postfixReverse.pop();
            try{
                number = new BigInteger(op);
                result.push(number);
                continue;
            }catch (Exception e) {
                if (varnameOk(op)) {
                    result.push(variables.get(op));
                    continue;
                } else{
                    BigInteger a = result.pop();
                    BigInteger b = result.pop();
                    switch (op) {
                        case "*":
                            result.push(a.multiply(b));
                            break;
                        case "/":
                            result.push(b.divide(a));
                            break;
                        case "+":
                            result.push(a.add(b));
                            break;
                        case "-":
                            result.push(b.subtract(a));
                            break;
                    }
                    //result.push(c);
                }
            }
            //System.out.println("result:"+result);
            //System.out.println(operators);
        }
        return result.pop();
    }
    public static void waitForInput(){
        while(true){
            input = scanner.nextLine();
            if (Objects.equals(input, "")){
                continue;
            } else if(input.matches("/[a-zA-Z]+")){
                command(input);
            }else if(input.contains("=")){
                assign(input);
            }/*else if ((input.contains("+") || input.contains("-")) & (!input.contains("*") || !input.contains("/"))){
                stringTokenizer = new StringTokenizer(input);
                int sum;
                String firstOperand = stringTokenizer.nextToken();
                try{
                    sum = Integer.parseInt(firstOperand);
                }catch (Exception e){
                    if (varnameOk(firstOperand)) {
                        if (variables.containsKey(firstOperand)){
                            sum = variables.get(firstOperand);
                        }else{
                            System.out.println("Unknown variable");
                            continue;
                        }
                    }else{
                        System.out.println("Invalid expression");
                        continue;
                    }
                }
                while(stringTokenizer.hasMoreTokens()){
                    String sign = stringTokenizer.nextToken();
                    String secondOperand = stringTokenizer.nextToken();
                    if (sign.contains("+")){
                        sum = getSumAdd(sum, secondOperand);
                    }else if(sign.contains("-")){
                        if (sign.length() % 2 ==0){
                            sum = getSumAdd(sum, secondOperand);
                            continue;
                        }else{
                            sum = getSumTake(sum, secondOperand);
                            continue;
                        }
                    }
                }
                System.out.println(sum);
            }*/
            else if (input.matches("\\s*[a-zA-Z]+\\s*")){
                getValue(input);
            }else{
                if (parentheseScan(input)) {
                    inToPost(input);
                    //System.out.println(postfix);
                    //System.out.println("Postfixreverse:"+postfixReverse);
                    System.out.println(calculate());
                }else{
                    System.out.println("Invalid expression");
                }
            }
        }
    }
    public static void main(String[] args) {
        //Scanner scanner = new Scanner(System.in);
        // put your code here
        waitForInput();



    }

    public static BigInteger getSumAdd(BigInteger sum, String secondOperand) {
        BigInteger op2;
        BigInteger result;
        try{
            op2 = new BigInteger(secondOperand);
            result = sum.add(op2);
        }catch (Exception e){
            if (varnameOk(secondOperand)) {
                if (variables.containsKey(secondOperand)){
                    result =sum.add(variables.get(secondOperand));
                }else{
                    System.out.println("Unknown variable");
                    return sum;
                }
            }else{
                System.out.println("Invalid expression");
                return sum;
            }
        }
        return result;
    }

    public static BigInteger getSumTake(BigInteger sum, String secondOperand) {
        BigInteger op2;
        BigInteger result;
        try{
            op2= new BigInteger(secondOperand);
            result = sum.subtract(op2);
        }catch (Exception e){
            if (varnameOk(secondOperand)) {
                if (variables.containsKey(secondOperand)){
                    result =sum.subtract(variables.get(secondOperand));
                }else{
                    System.out.println("Unknown variable");
                    return sum;
                }
            }else{
                System.out.println("Invalid expression");
                return sum;
            }
        }
        return result;
    }
}
