import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Instruction types */
enum InstructionType {
    A_INSTRUCTION, C_INSTRUCTION, L_INSTRUCTION, NULL
}

/** C-instruction destinations */
enum InstructionDest {
    A, D, M, AM, AD, MD, AMD, NULL
}

/** C-instruction jump conditions */
enum InstructionJump {
    JLT, JGT, JEQ, JLE, JGE, JNE, JMP, NULL
}

/** C-instruction computations/op-codes */
enum InstructionComp {
    CONST_0, CONST_1, CONST_NEG_1,
    A, M, D,
    NOT_A, NOT_M, NOT_D,
    NEG_A, NEG_M, NEG_D,
    A_ADD_1, M_ADD_1, D_ADD_1,
    A_SUB_1, M_SUB_1, D_SUB_1,
    D_ADD_A, D_ADD_M,
    D_SUB_A, D_SUB_M, A_SUB_D, M_SUB_D,
    D_AND_A, D_AND_M,
    D_OR_A, D_OR_M,
    NULL
}

public class Assembler {
    private static int nextAvailablePos = 16;
    private static int lineNumber = 0;

    /**
     * Assembler first pass; populates symbol table with label locations.
     * 
     * @param instructions An array of the assembly language instructions.
     * @param symbolTable  The symbol table to populate.
     */
    public static void doFirstPass(String[] instructions, SymbolTable symbolTable) {
        // Your code here
        for (int i = 0; i < instructions.length; i++) {
            if (instructions[i].charAt(0) == '(') { // if is label
                // create the label's symbol by removing space the ()
                String label = instructions[i].trim().substring(instructions[i].indexOf("(") + 1,
                        instructions[i].lastIndexOf(")")).trim().replaceAll(" ", "");
                // add label if it is not in SymbolTable
                if (SymbolTable.getSymbol(label) == -1) {
                    // System.out.println(label + " is not found in Symbol Table, calling
                    // addSymbol");
                    // System.out.println("Current Line: " + lineNumber);
                    SymbolTable.addSymbol(label, lineNumber);
                }
                lineNumber--;
            }
            lineNumber++;
        }
    }

    /**
     * Translates a set of instructions to machine code.
     * 
     * @param instructions An array of the assembly language instructions to be
     *                     converted to machine code.
     * @param symbolTable  The symbol table to reference/update.
     * @return A String containing the generated machine code as strings of 16-bit
     *         binary instructions.
     */
    public static String doSecondPass(String[] instructions, SymbolTable symbolTable) {
        String result = "";
        int line = 0;
        // Your code here
        // System.out.println("----------Second Pass----------");
        for (String instruction : instructions) {
            // System.out.println(instruction);
            if (instruction.trim().charAt(0) == '@') { // a-instruction
                // System.out.println("It's A-instruction");
                // System.out.println("Instruction before trimming: " + instruction);
                String variable = instruction.trim().substring(instruction.indexOf("@") + 1,
                        instruction.length()).replaceAll(" ", "");
                // System.out.println("Instruction after trimming: " + variable);
                // System.out.println(translateSymbol(variable, symbolTable));
                try {
                    Integer.parseInt(variable);
                    if (line == 0)
                        result = result + "0" + translateSymbol(variable, symbolTable);
                    else
                        result = result + "\n0" + translateSymbol(variable, symbolTable);

                } catch (NumberFormatException e) {
                    if (SymbolTable.getSymbol(variable) == -1) {
                        // if (translateSymbol(variable, symbolTable) == "-1") {
                        // System.out.println(variable + " is NOT in Symbol Table");
                        SymbolTable.addSymbol(variable, nextAvailablePos);
                        nextAvailablePos++;
                    }
                    if (line == 0)
                        result = result + "0" + translateSymbol(variable, symbolTable);
                    else
                        result = result + "\n0" + translateSymbol(variable, symbolTable);
                }
                /*
                 * if (line != instructions.length - 1)
                 * result = result + "0" + translateSymbol(variable, symbolTable) + "\n";
                 * else
                 * result = result + "0" + translateSymbol(variable, symbolTable);
                 */
                // return result;
            } else if (instruction.charAt(0) == '(') {
                // System.out.println("It's L-instruction");
                result = result + "";
                line--;
                // return "";
            } else { // c-instruction
                if (line == 0)
                    result = result + "111" + translateComp(parseInstructionComp(instruction))
                            + translateDest(parseInstructionDest(instruction))
                            + translateJump(parseInstructionJump(instruction));
                else
                    result = result + "\n111" + translateComp(parseInstructionComp(instruction))
                            + translateDest(parseInstructionDest(instruction))
                            + translateJump(parseInstructionJump(instruction));
                /*
                 * if (line != instructions.length - 1) {
                 * result += "111" + translateComp(parseInstructionComp(instruction))
                 * + translateDest(parseInstructionDest(instruction))
                 * + translateJump(parseInstructionJump(instruction)) + "\n";
                 * } else {
                 * result += "111" + translateComp(parseInstructionComp(instruction))
                 * + translateDest(parseInstructionDest(instruction))
                 * + translateJump(parseInstructionJump(instruction));
                 * }
                 */
            }
            line++;
        }
        return result; // replace this
    }

    /**
     * Parses the type of the provided instruction
     * 
     * @param instruction The assembly language representation of an instruction.
     * @return The type of the instruction (A_INSTRUCTION, C_INSTRUCTION,
     *         L_INSTRUCTION, NULL)
     */
    public static InstructionType parseInstructionType(String instruction) {
        // Your code here
        // System.out.println(instruction);
        // System.out.println("----------parseInstructionType----------");
        // preparing for c-instruction
        // Matcher matcher = Pattern.compile(".*=;.*").matcher(instruction);
        // Matcher matcher2 = Pattern.compile(".*;.*").matcher(instruction);

        // boolean matches = matcher.matches();
        // boolean matches2 = matcher2.matches();
        // a-instruction
        if (instruction.charAt(0) == '@') {
            // System.out.println("Result: A-instruction");
            return InstructionType.A_INSTRUCTION;
        }
        // label
        else if (instruction.charAt(0) == '(') {
            // System.out.println("Result: L-instruction");
            return InstructionType.L_INSTRUCTION;
        }
        // c-instruction
        /*
         * else if (matches == true || matches2 == true) {
         * // System.out.println("Result: C-instruction");
         * return InstructionType.C_INSTRUCTION;
         * }
         * return InstructionType.NULL;
         */
        // System.out.println("Result: C-instruction: " + instruction);
        return InstructionType.C_INSTRUCTION;
    }

    /**
     * Parses the destination of the provided C-instruction
     * 
     * @param instruction The assembly language representation of a C-instruction.
     * @return The destination of the instruction (A, D, M, AM, AD, MD, AMD, NULL)
     */
    public static InstructionDest parseInstructionDest(String instruction) {
        // System.out.println("----------parseInstructionDest----------");
        // Your code here
        if (parseInstructionType(instruction) == InstructionType.A_INSTRUCTION) {
            // System.out.println("Destination for A-instruction");
            return InstructionDest.A;
        } else if (parseInstructionType(instruction) == InstructionType.C_INSTRUCTION) {
            // System.out.println("Destination for C-instruction");
            // split the C-instruction at "="
            if (instruction.contains("=")) {
                String dest = instruction.split("=")[0].trim().replaceAll(" ", "");
                // System.out.println(dest);

                Matcher matcherAMD = Pattern.compile("AMD").matcher(dest);
                Matcher matcherAM = Pattern.compile("AM").matcher(dest);
                // Matcher matcherMA = Pattern.compile(".*MA.*").matcher(dest);
                Matcher matcherAD = Pattern.compile("AD").matcher(dest);
                // Matcher matcherDA = Pattern.compile(".*DA.*").matcher(dest);
                Matcher matcherMD = Pattern.compile("MD").matcher(dest);
                // Matcher matcherDM = Pattern.compile(".*DM.*").matcher(dest);
                Matcher matcherA = Pattern.compile("A").matcher(dest);
                Matcher matcherM = Pattern.compile("M").matcher(dest);
                Matcher matcherD = Pattern.compile("D").matcher(dest);

                if (matcherAMD.matches() == true)
                    return InstructionDest.AMD;
                else if (matcherAM.matches() == true)
                    return InstructionDest.AM;
                else if (matcherAD.matches() == true)
                    return InstructionDest.AD;
                else if (matcherMD.matches() == true)
                    return InstructionDest.MD;
                else if (matcherA.matches() == true)
                    return InstructionDest.A;
                else if (matcherM.matches() == true)
                    return InstructionDest.M;
                else if (matcherD.matches() == true)
                    return InstructionDest.D;
            }
        }
        return InstructionDest.NULL;
    }

    /**
     * Parses the jump condition of the provided C-instruction
     * 
     * @param instruction The assembly language representation of a C-instruction.
     * @return The jump condition for the instruction (JLT, JGT, JEQ, JLE, JGE, JNE,
     *         JMP, NULL)
     */
    public static InstructionJump parseInstructionJump(String instruction) {
        // Your code here
        if (instruction.contains(";")) {
            String[] parts = instruction.trim().replaceAll(" ", "").split(";");
            String jump = parts[1].split("/")[0].trim().replaceAll(" ", "");
            switch (jump) {
                case "JLT":
                    return InstructionJump.JLT;
                case "JGT":
                    return InstructionJump.JGT;
                case "JEQ":
                    return InstructionJump.JEQ;
                case "JLE":
                    return InstructionJump.JLE;
                case "JGE":
                    return InstructionJump.JGE;
                case "JNE":
                    return InstructionJump.JNE;
                case "JMP":
                    return InstructionJump.JMP;
                case "null":
                default:
                    return InstructionJump.NULL;
            }
        }
        return InstructionJump.NULL;
    }

    /**
     * Parses the computation/op-code of the provided C-instruction
     * 
     * @param instruction The assembly language representation of a C-instruction.
     * @return The computation/op-code of the instruction (CONST_0, ... ,D_ADD_A ,
     *         ... , NULL)
     */
    public static InstructionComp parseInstructionComp(String instruction) {
        String comp;
        // System.out.println("-----------parseInstructionComp-----------");
        // Your code here
        // if (matcherEq.matches() == true) {
        if (instruction.contains("=")) {
            String[] parts = instruction.trim().split("=");
            comp = parts[1].split("/")[0].split(";")[0].trim().replaceAll(" ", "");
        } else if (instruction.contains(";")) {
            comp = instruction.trim().split("/")[0].split(";")[0].replaceAll(" ", "");
        } else {
            comp = instruction;
            // System.out.println(comp);
        }

        switch (comp) {
            case "0":
                // System.out.println("It's a zero");
                return InstructionComp.CONST_0;
            case "1":
                return InstructionComp.CONST_1;
            case "-1":
                return InstructionComp.CONST_NEG_1;
            case "D":
                return InstructionComp.D;
            case "A":
                return InstructionComp.A;
            case "!D":
                return InstructionComp.NOT_D;
            case "!A":
                return InstructionComp.NOT_A;
            case "-D":
                return InstructionComp.NEG_D;
            case "-A":
                return InstructionComp.NEG_A;
            case "D+1":
                return InstructionComp.D_ADD_1;
            case "A+1":
                return InstructionComp.A_ADD_1;
            case "D-1":
                return InstructionComp.D_SUB_1;
            case "A-1":
                return InstructionComp.A_SUB_1;
            case "D+A":
            case "A+D":
                return InstructionComp.D_ADD_A;
            case "D-A":
                return InstructionComp.D_SUB_A;
            case "A-D":
                return InstructionComp.A_SUB_D;
            case "D&A":
                return InstructionComp.D_AND_A;
            case "D|A":
                return InstructionComp.D_OR_A;
            case "M":
                return InstructionComp.M;
            case "!M":
                return InstructionComp.NOT_M;
            case "-M":
                // System.out.println("-M");
                return InstructionComp.NEG_M;
            case "M+1":
            case "1+M":
                return InstructionComp.M_ADD_1;
            case "M-1":
                return InstructionComp.M_SUB_1;
            case "D+M":
            case "M+D":
                return InstructionComp.D_ADD_M;
            case "D-M":
                return InstructionComp.D_SUB_M;
            case "M-D":
                return InstructionComp.M_SUB_D;
            case "D&M":
            case "M&D":
                return InstructionComp.D_AND_M;
            case "D|M":
            case "M|D":
                return InstructionComp.D_OR_M;
            default:
                return InstructionComp.NULL;
        }
    }

    /**
     * Parses the symbol of the provided A/L-instruction
     * 
     * @param instruction The assembly language representation of a A/L-instruction.
     * @return A string containing either a label name (L-instruction),
     *         a variable name (A-instruction), or a constant integer value
     *         (A-instruction)
     */
    public static String parseSymbol(String instruction) {
        // System.out.println("parseSymbol is called");
        // Your code here
        if (parseInstructionType(instruction) == InstructionType.L_INSTRUCTION) {
            String label = instruction.trim().replaceAll(" ", "").substring(instruction.indexOf("(") + 1,
                    instruction.lastIndexOf(")"));
            // System.out.println(label);
            return label;
        } else if (parseInstructionType(instruction) == InstructionType.A_INSTRUCTION) {
            String s = instruction.split("@")[1].trim().replaceAll(" ", "");
            return s;
        }
        return "";
    }

    /**
     * Generates the binary bits of the dest part of a C-instruction
     * 
     * @param dest The destination of the instruction
     * @return A String containing the 3 binary dest bits that correspond to the
     *         given dest value.
     */
    public static String translateDest(InstructionDest dest) {
        // Your code here
        switch (dest) {
            case A:
                return "100";
            case AD:
                return "110";
            case AM:
                return "101";
            case AMD:
                return "111";
            case D:
                return "010";
            case M:
                return "001";
            case MD:
                return "011";
            default:
                return "000";
        }
    }

    /**
     * Generates the binary bits of the jump part of a C-instruction
     * 
     * @param jump The jump condition for the instruction
     * @return A String containing the 3 binary jump bits that correspond to the
     *         given jump value.
     */
    public static String translateJump(InstructionJump jump) {
        // Your code here
        switch (jump) {
            case JGT:
                return "001";
            case JEQ:
                return "010";
            case JGE:
                return "011";
            case JLT:
                return "100";
            case JNE:
                return "101";
            case JLE:
                return "110";
            case JMP:
                return "111";
            default:
                return "000";
        }
    }

    /**
     * Generates the binary bits of the computation/op-code part of a C-instruction
     * 
     * @param comp The computation/op-code for the instruction
     * @return A String containing the 7 binary computation/op-code bits that
     *         correspond to the given comp value.
     */
    public static String translateComp(InstructionComp comp) {
        // System.out.println("-----------translateComp-----------");
        // Your code here
        switch (comp) {
            case CONST_0:
                // System.out.println("It's a zero");
                return "0101010";
            case CONST_1:
                return "0111111";
            case CONST_NEG_1:
                return "0111010";
            case D:
                return "0001100";
            case A:
                return "0110000";
            case NOT_D:
                return "0001101";
            case NOT_A:
                return "0110001";
            case NEG_D:
                return "0001111";
            case NEG_A:
                return "0110011";
            case D_ADD_1:
                return "0011111";
            case A_ADD_1:
                return "0110111";
            case D_SUB_1:
                return "0001110";
            case A_SUB_1:
                return "0110010";
            case D_ADD_A:
                return "0000010";
            case D_SUB_A:
                return "0010011";
            case A_SUB_D:
                return "0000111";
            case D_AND_A:
                return "0000000";
            case D_OR_A:
                return "0010101";
            case M:
                return "1110000";
            case NOT_M:
                return "1110001";
            case NEG_M:
                // System.out.println("1110011");
                return "1110011";
            case M_ADD_1:
                return "1110111";
            case M_SUB_1:
                return "1110010";
            case D_ADD_M:
                return "1000010";
            case D_SUB_M:
                return "1010011";
            case M_SUB_D:
                return "1000111";
            case D_AND_M:
                return "1000000";
            case D_OR_M:
                return "1010101";
            default:
                return "0000000";
        }
    }

    /**
     * Generates the binary bits for an A-instruction, parsing the value, or looking
     * up the symbol name.
     * 
     * @param symbol      A string containing either a label name, a variable name,
     *                    or a constant integer value
     * @param symbolTable The symbol table for looking up label/variable names
     * @return A String containing the 15 binary bits that correspond to the given
     *         sybmol.
     */
    public static String translateSymbol(String symbol, SymbolTable symbolTable) {
        // Your code here
        String binary = "";
        try { // A-instruction
            int decimal = Integer.parseInt(symbol);
            while (decimal > 0) {
                binary = String.valueOf(decimal % 2) + binary;
                decimal = decimal / 2;
            }
            // turn n-bits binary into 16-bits
            int diff = 15 - binary.length();
            for (int i = 0; i < diff; i++) {
                binary = "0" + binary;
            }
            return binary;
        } catch (NumberFormatException e) { // variable/label
            int num = SymbolTable.getSymbol(symbol);
            // System.out.println(symbol + " is found in Symbol Table at pos: " +
            // SymbolTable.getSymbol(symbol));
            while (num > 0) {
                binary = String.valueOf(num % 2) + binary;
                num = num / 2;
            }
            // turn n-bits binary into 16-bits
            int diff = 15 - binary.length();
            for (int i = 0; i < diff; i++) {
                binary = "0" + binary;
            }
            return binary;
        }
    }

    /**
     * A quick-and-dirty driver when run standalone.
     * When testing your code, we encourage you to also write your own classes to
     * check
     * individual functions as the autograder will do
     */
    public static void main(String[] args) {
        if (args.length > 0) {

            SymbolTable symbolTable = new SymbolTable();
            LinkedList<String> instructionList = new LinkedList<String>();

            // Open file
            try {
                // Read line-by-line
                Scanner sc = new Scanner(new File(args[0]));
                while (sc.hasNextLine()) {
                    String rawInstruciton = sc.nextLine();
                    String[] subInst = rawInstruciton.split("//", 2);
                    String code_no_comment = subInst[0].trim();
                    if (code_no_comment.isEmpty()) {
                        continue;
                    }
                    instructionList.add(code_no_comment.trim()); // String.trim() removes all leading and trailing
                                                                 // whitespaces
                }
                // Close file
                sc.close();
            } catch (FileNotFoundException e) {
                System.err.println("File not found.");
            }
            // Convert to array
            String[] instructions = instructionList.toArray(new String[0]);

            // First pass
            doFirstPass(instructions, symbolTable);
            // Second pass
            String code = doSecondPass(instructions, symbolTable);
            // Print output
            System.out.println(code);
        }
    }

}
