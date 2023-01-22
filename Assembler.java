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

    /**
     * Assembler first pass; populates symbol table with label locations.
     * 
     * @param instructions An array of the assembly language instructions.
     * @param symbolTable  The symbol table to populate.
     */
    public static void doFirstPass(String[] instructions, SymbolTable symbolTable) {
        // Your code here
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
        // Your code here
        return ""; // replace this
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
        return InstructionType.NULL;
    }

    /**
     * Parses the destination of the provided C-instruction
     * 
     * @param instruction The assembly language representation of a C-instruction.
     * @return The destination of the instruction (A, D, M, AM, AD, MD, AMD, NULL)
     */
    public static InstructionDest parseInstructionDest(String instruction) {
        // Your code here
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
        // Your code here
        return InstructionComp.NULL;
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
        // Your code here
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
        return "000";
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
        return "000";
    }

    /**
     * Generates the binary bits of the computation/op-code part of a C-instruction
     * 
     * @param comp The computation/op-code for the instruction
     * @return A String containing the 7 binary computation/op-code bits that
     *         correspond to the given comp value.
     */
    public static String translateComp(InstructionComp comp) {
        // Your code here
        return "0000000";
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
        return "000000000000000";
    }

    /** 
     * A quick-and-dirty driver when run standalone.
     * When testing your code, we encourage you to also write your own classes to check
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
