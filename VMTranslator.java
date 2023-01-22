import java.io.*;
import java.util.*;

import javax.management.RuntimeErrorException;

public class VMTranslator {
    private static String output = "";
    // private static String fileName;
    private static int count = 0;

    /** Translate the segment from VM to corresponding Hack symbol */
    /* 7 segments: argument, local, static, this, that, pointer, temp */
    public static String translateSegment(String segment, int offset) {
        switch (segment) {
            case "argument":
                return "ARG";
            case "local":
                return "LCL";
            case "static":
                return String.valueOf(16 + offset);
            case "this":
                return "THIS";
            case "that":
                return "THAT";
            case "pointer":
                return "R" + String.valueOf(3 + offset);
            case "temp":
                return "R" + String.valueOf(5 + offset);
        }
        ;
        return "";
    }

    /** Generate Hack Assembly code for a VM push operation */
    /* Constant + 7 segments: argument, local, static, this, that, pointer, temp */
    public static String vm_push(String segment, int offset) {
        switch (segment) {
            case "constant":
                /*
                 * push constant 7
                 * 
                 * @7
                 * D=A
                 * 
                 * @SP
                 * A=M
                 * M=D
                 * 
                 * @SP
                 * M=M+1
                 */
                output = "@" + String.valueOf(offset) + "\n";
                output += "D=A\n";
                output += "@SP\n";
                output += "A=M\n";
                output += "M=D\n";
                output += "@SP\n";
                output += "M=M+1\n";
                break;
            case "static":
                /*
                 * push static 2
                 * 
                 * @16
                 * D=A
                 * 
                 * @2
                 * A=D+A
                 * D=M
                 * 
                 * @SP
                 * A=M
                 * M=D
                 * 
                 * //update the SP
                 * 
                 * @SP
                 * M=M+1
                 */
                // @fileName.offset
                // output = "@" + fileName + "." + String.valueOf(offset) + "\n";
                output = "@" + translateSegment(segment, offset) + "\n";
                output += "D=M\n";
                output += "@SP\n";
                output += "A=M\n";
                output += "M=D\n";
                output += "@SP\n";
                output += "M=M+1\n";
                break;
            case "argument":
            case "local":
            case "this":
            case "that":
                /*
                 * push argument 2
                 * 
                 * @ARG
                 * D=M
                 * 
                 * @2
                 * A=D+A
                 * D=M
                 * 
                 * @SP
                 * A=M
                 * M=D
                 * 
                 * @SP
                 * M=M+1
                 */
                output = "@" + translateSegment(segment, offset) + "\n";
                output += "D=M\n";
                output += "@" + String.valueOf(offset) + "\n";
                output += "A=D+A\n";
                output += "D=M\n";
                output += "@SP\n";
                output += "A=M\n";
                output += "M=D\n";
                output += "@SP\n";
                output += "M=M+1\n";
                break;
            case "pointer":
                output = "@" + translateSegment(segment, offset) + "\n";
                output += "D=M\n";
                output += "@SP\n";
                output += "A=M\n";
                output += "M=D\n";
                output += "@SP\n";
                output += "M=M+1\n";
                break;
            case "temp":
                output = "@" + translateSegment(segment, offset) + "\n";
                output += "D=M\n";
                output += "@SP\n";
                output += "A=M\n";
                output += "M=D\n";
                output += "@SP\n";
                output += "M=M+1\n";
                break;
        }
        ;
        return output;
    }

    /** Generate Hack Assembly code for a VM pop operation */
    public static String vm_pop(String segment, int offset) {
        switch (segment) {
            case "constant":
                throw new RuntimeErrorException(null, "Cannot pop to constant");
            case "static":
                /*
                 * pop static 2
                 * 
                 * @fileName.offset
                 * 
                 * @R13
                 * M=D
                 * 
                 * @SP
                 * AM=M-1
                 * D=M
                 * 
                 * @R13
                 * A=M
                 * M=D
                 * 
                 */
                // output = "@" + fileName + "." + String.valueOf(offset) + "\n";
                output = "@" + translateSegment(segment, offset) + "\n";
                output += "D=A\n";

                output += "@R13\n";
                output += "M=D\n";
                output += "@SP\n";
                output += "M=M-1\n";
                output += "A=M\n";
                output += "D=M\n";
                output += "@R13\n";
                output += "A=M\n";
                output += "M=D\n";
                break;
            case "argument":
            case "local":
            case "this":
            case "that":
                /*
                 * pop argument 2
                 * 
                 * @ARG
                 * D=M
                 * 
                 * @2
                 * D=D+A
                 * 
                 * @R13
                 * M=D
                 * 
                 * @SP
                 * AM=M-1
                 * D=M
                 * 
                 * @R13
                 * A=M
                 * M=D
                 */
                output = "@" + translateSegment(segment, offset) + "\n";
                output += "D=M\n";
                output += "@" + String.valueOf(offset) + "\n";
                output += "D=D+A\n";

                output += "@R13\n";
                output += "M=D\n";
                output += "@SP\n";
                output += "M=M-1\n";
                output += "A=M\n";
                output += "D=M\n";
                output += "@R13\n";
                output += "A=M\n";
                output += "M=D\n";
                break;
            case "pointer":
                output = "@" + translateSegment(segment, offset) + "\n";
                output += "D=A\n";

                output += "@R13\n";
                output += "M=D\n";
                output += "@SP\n";
                output += "M=M-1\n";
                output += "A=M\n";
                output += "D=M\n";
                output += "@R13\n";
                output += "A=M\n";
                output += "M=D\n";
                break;
            case "temp":
                output = "@" + translateSegment(segment, offset) + "\n";
                output += "D=A\n";

                output += "@R13\n";
                output += "M=D\n";
                output += "@SP\n";
                output += "M=M-1\n";
                output += "A=M\n";
                output += "D=M\n";
                output += "@R13\n";
                output += "A=M\n";
                output += "M=D\n";
                break;
        }
        return output;
    }

    /** Generate Hack Assembly code for a VM add operation */
    public static String vm_add() {
        /*
         * @SP
         * AM=M-1
         * D=M
         * A=A-1
         * M=M+D
         */
        output = "@SP\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "A=A-1\n";
        output += "M=M+D\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM sub operation */
    public static String vm_sub() {
        /*
         * @SP
         * AM=M-1
         * D=M
         * A=A-1
         * M=M-D
         */
        output = "@SP\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "A=A-1\n";
        output += "M=M-D\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM neg operation */
    public static String vm_neg() {
        /*
         * @SP
         * A=M-1
         * M=-M
         */
        output = "@SP\n";
        output += "A=M-1\n";
        output += "M=-M\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM eq operation */
    public static String vm_eq() {
        /*
         * @SP
         * AM=M-1
         * D=M
         * A=A-1
         * D=M-D
         * 
         * @labelTrue
         * D;JEQ
         * 
         * @SP
         * A=M-1
         * M=0
         * 
         * @END
         * 0;JMP
         * 
         * (labelTrue)
         * 
         * @SP
         * A=M-1
         * M=-1
         * 
         * (END)
         * 
         * @END
         * 0;JMP
         */
        output = "@SP\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "A=A-1\n";
        // output += "D=M-D\n";
        output += "D=D-M\n";
        output += "@eqlabelTrue." + String.valueOf(count) + "\n";
        output += "D;JEQ\n";
        output += "@SP\n";
        output += "A=M-1\n";
        output += "M=0\n";
        output += "@eqEND." + String.valueOf(count) + "\n";
        output += "0;JMP\n";
        output += "(eqlabelTrue." + String.valueOf(count) + ")\n";
        output += "@SP\n";
        output += "A=M-1\n";
        output += "M=-1\n";
        output += "(eqEND." + String.valueOf(count) + ")\n";
        // output += "@eqEND." + String.valueOf(count) + "\n";
        // output += "0;JMP\n";
        count++;
        return output;
    }

    /** Generate Hack Assembly code for a VM gt operation */
    public static String vm_gt() {
        /*
         * @SP
         * AM=M-1
         * D=M
         * A=A-1
         * D=M-D
         * 
         * @labelTrue
         * D;JGT
         * 
         * @SP
         * A=M-1
         * M=0
         * 
         * @END
         * 0;JMP
         * 
         * (labelTrue)
         * 
         * @SP
         * A=M-1
         * M=-1
         * (END)
         * 
         * @END
         * 0;JMP
         */
        output = "@SP\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "A=A-1\n";
        output += "D=M-D\n";
        output += "@gtlabelTrue." + String.valueOf(count) + "\n";
        output += "D;JGT\n";
        output += "@SP\n";
        output += "A=M-1\n";
        output += "M=0\n";
        output += "@gtEND." + String.valueOf(count) + "\n";
        output += "0;JMP\n";
        output += "(gtlabelTrue." + String.valueOf(count) + ")\n";
        output += "@SP\n";
        output += "A=M-1\n";
        output += "M=-1\n";
        output += "(gtEND." + String.valueOf(count) + ")\n";
        // output += "@gtEND." + String.valueOf(count) + "\n";
        // output += "0;JMP\n";
        count++;
        return output;
    }

    /** Generate Hack Assembly code for a VM lt operation */
    public static String vm_lt() {
        /*
         * @SP
         * AM=M-1
         * D=M
         * A=A-1
         * D=M-D
         * 
         * @labelTrue
         * D;JLT
         * 
         * @SP
         * A=M-1
         * M=0
         * 
         * @END
         * 0;JMP
         * 
         * (labelTrue)
         * 
         * @SP
         * A=M-1
         * M=-1
         * (END)
         * 
         * @END
         * 0;JMP
         */
        output = "@SP\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "A=A-1\n";
        output += "D=M-D\n";
        output += "@ltlabelTrue." + String.valueOf(count) + "\n";
        output += "D;JLT\n";
        output += "@SP\n";
        output += "A=M-1\n";
        output += "M=0\n";
        output += "@ltEND." + String.valueOf(count) + "\n";
        output += "0;JMP\n";
        output += "(ltlabelTrue." + String.valueOf(count) + ")\n";
        output += "@SP\n";
        output += "A=M-1\n";
        output += "M=-1\n";
        output += "(ltEND." + String.valueOf(count) + ")\n";
        // output += "@END." + String.valueOf(count) + "\n";
        // output += "0;JMP\n";
        count++;
        return output;
    }

    /** Generate Hack Assembly code for a VM and operation */
    public static String vm_and() {
        /*
         * @SP
         * AM=M-1
         * D=M
         * A=A-1
         * M=D&M
         */
        output = "@SP\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "A=A-1\n";
        output += "M=D&M\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM or operation */
    public static String vm_or() {
        /*
         * @SP
         * AM=M-1
         * D=M
         * A=A-1
         * M=D|M
         */
        output = "@SP\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "A=A-1\n";
        output += "M=D|M\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM not operation */
    public static String vm_not() {
        /*
         * @SP
         * A=M-1
         * M=!M
         */
        output = "@SP\n";
        output += "A=M-1\n";
        output += "M=!M\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM label operation */
    public static String vm_label(String label) {
        /*
         * (function_name$label))
         */
        output = "(" + label.replaceAll(" ", "") + ")" + "\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM goto operation */
    public static String vm_goto(String label) {
        /*
         * @function_name$label
         * 0;JMP
         */
        output = "@" + label.replaceAll(" ", "") + "\n";
        output += "0;JMP\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM if-goto operation */
    public static String vm_if(String label) {
        /*
         * @SP
         * AM=M-1
         * D=M
         * 
         * @function_name$label
         * D;JNE
         */
        output = "@SP\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "@" + label.replaceAll(" ", "") + "\n";
        output += "D;JNE\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM function operation */
    public static String vm_function(String function_name, int n_vars) {
        /*
         * (function_name)
         * 
         * @SP + A=M
         * 
         * //loop for n_vars times to put n_vars of 0 in the stack
         * M=0 + A=A+1
         * 
         * //update current SP value
         * D=A + @SP + M=D
         */
        // ----------- generate label -----------//
        output = "(" + function_name + ")\n";
        // ----------- initialise local variables to 0 for n_vars times -----------//
        output += "@SP\n";
        output += "A=M\n";
        for (int i = 0; i < n_vars; i++) {
            output += "M=0\n";
            output += "A=A+1\n";
            // output += "@0\n";
            // output += "D=A\n";
            // output += "@SP\n";
            // output += "A=M\n";
            // output += "M=D\n";
            // output += "@SP\n";
            // output += "M=M+1\n";
        }
        output += "D=A\n";
        output += "@SP\n";
        output += "M=D\n";
        return output;
    }

    /** Generate Hack Assembly code for a VM call operation */
    public static String vm_call(String function_name, int n_args) {
        /*
         * put original SP in R13
         * push returnAddress
         * push LCL
         * push ARG
         * push THIS
         * push THAT
         * ARG = SP-5-nArgs
         * LCL = SP
         * goto function_name
         * (returnAdd)
         */
        // ----------- generate label for program to return -----------//
        // output = "(" + function_name + ")\n";
        // ----------- save the stack frame -----------//
        // SP-5 = R13
        output = "@SP\n";
        output += "D=M\n";
        output += "@R13\n";
        output += "M=D\n";
        // ----------- update memory segment pointers -----------//
        // push returnAddress
        output += "@RET." + String.valueOf(count) + "\n";
        output += "D=A\n";

        output += "@SP\n";
        output += "A=M\n";
        output += "M=D\n";
        output += "@SP\n";
        output += "M=M+1\n";
        // push LCL
        output += "@LCL\n";
        output += "D=M\n";

        output += "@SP\n";
        output += "A=M\n";
        output += "M=D\n";
        output += "@SP\n";
        output += "M=M+1\n";
        // push ARG
        output += "@ARG\n";
        output += "D=M\n";

        output += "@SP\n";
        output += "A=M\n";
        output += "M=D\n";
        output += "@SP\n";
        output += "M=M+1\n";
        // push THIS
        output += "@THIS\n";
        output += "D=M\n";

        output += "@SP\n";
        output += "A=M\n";
        output += "M=D\n";
        output += "@SP\n";
        output += "M=M+1\n";
        // push THAT
        output += "@THAT\n";
        output += "D=M\n";

        output += "@SP\n";
        output += "A=M\n";
        output += "M=D\n";
        output += "@SP\n";
        output += "M=M+1\n";
        // ARG = SP-5-nargs = R13-nargs
        // output += "@SP\n";
        // output += "D=M\n";
        // output += "@" + String.valueOf(n_args) + "\n";
        // output += "D=D-A\n";
        // output += "@5\n";
        // output += "D=D-A\n";
        // output += "@ARG\n";
        // output += "M=D\n";
        output += "@R13\n";
        output += "D=M\n";
        output += "@" + String.valueOf(n_args) + "\n";
        output += "D=D-A\n";
        output += "@ARG\n";
        output += "M=D\n";

        // LCL = SP
        output += "@SP\n";
        output += "D=M\n";
        output += "@LCL\n";
        output += "M=D\n";

        // ----------- jump to the label -----------//
        output += vm_goto(function_name);
        // for return
        // output += "(RET." + String.valueOf(count) + ")\n";
        // count++;
        output += vm_label("RET." + String.valueOf(count));
        return output;
    }

    /** Generate Hack Assembly code for a VM return operation */
    public static String vm_return() {
        /*
         * frame = LCL
         * returnAddress = *(frame-5) = R13
         * *ARG = pop()
         * SP = ARG+1
         * THAT = *(frame-1), LCL1 = LCL-1
         * THIS = *(frame-2), LCL2 = LCL1-1
         * ARG = *(frame-3), LCL3 = LCL2-1
         * LCL = *(frame-4), LCL4 = LCL3-1
         * goto returnAddress
         */

        // ----------- copy return value -----------//
        // output = "@SP\n";
        // output += "A=M-1\n";
        // output += "D=M\n";
        // output += "@ARG\n";
        // output += "M=D\n";
        // ----------- restor segment -----------//
        // retAddr = *(frame-5) = R13
        output = "@LCL\n";
        output += "D=M\n";
        output += "@5\n";
        output += "A=D-A\n";
        output += "D=M\n";
        output += "@R13\n";
        output += "M=D\n";
        // output = "@LCL\n";
        // output += "D=M\n";
        // output += "@frame\n";
        // output += "M=D\n";
        // output += "@frame\n";
        // output += "D=M\n";
        // output += "@5\n";
        // output += "A=D-A\n";
        // output += "D=M\n";
        // output += "@RET." + String.valueOf(count) + "\n";
        // output += "M=D\n";

        // *ARG = pop()
        output += "@SP\n";
        // output += "AM=M-1\n";
        output += "M=M-1\n";
        output += "A=M\n";
        output += "D=M\n";
        output += "@ARG\n";
        output += "A=M\n";
        output += "M=D\n";

        // SP = ARG+1
        output += "@ARG\n";
        output += "D=M+1\n";
        output += "@SP\n";
        output += "M=D\n";

        // THAT = *(frame-1) = LCL--
        output += "@LCL\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "@THAT\n";
        output += "M=D\n";
        // output += "@frame\n";
        // output += "D=M\n";
        // output += "@1\n";
        // output += "A=D-A\n";
        // output += "D=M\n";
        // output += "@THAT\n";
        // output += "M=D\n";

        // THIS = *(frame-2) = LCL--
        output += "@LCL\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "@THIS\n";
        output += "M=D\n";
        // output += "@frame\n";
        // output += "D=M\n";
        // output += "@2\n";
        // output += "A=D-A\n";
        // output += "D=M\n";
        // output += "@THAT\n";
        // output += "M=D\n";

        // ARG = *(frame-3) = LCL--
        output += "@LCL\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "@ARG\n";
        output += "M=D\n";
        // output += "@frame\n";
        // output += "D=M\n";
        // output += "@3\n";
        // output += "A=D-A\n";
        // output += "D=M\n";
        // output += "@THAT\n";
        // output += "M=D\n";

        // LCL = *(frame-4) = LCL--
        output += "@LCL\n";
        output += "AM=M-1\n";
        output += "D=M\n";
        output += "@LCL\n";
        output += "M=D\n";
        // output += "@frame\n";
        // output += "D=M\n";
        // output += "@4\n";
        // output += "A=D-A\n";
        // output += "D=M\n";
        // output += "@THAT\n";
        // output += "M=D\n";

        // ----------- jump -----------//
        // goto returnAddress
        // output += "@R13\n";
        // output += "A=M\n";
        // output += "0;JMP\n";
        output += "@RET." + String.valueOf(count) + "\n";
        // output += "A=M\n";
        output += "0;JMP\n";
        return output;
    }

    /** A quick-and-dirty parser when run standalone. */
    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                Scanner sc = new Scanner(new File(args[0]));
                // fileName = args[0].replaceAll(".*/", "").split(".vm")[0];
                // System.out.println(fileName);
                while (sc.hasNextLine()) {
                    String[] tokens = sc.nextLine().trim().toLowerCase().split("\\s+");
                    if (tokens.length == 1) {
                        if (tokens[0].equals("add")) {
                            System.out.println(vm_add());
                        } else if (tokens[0].equals("sub")) {
                            System.out.println(vm_sub());
                        } else if (tokens[0].equals("neg")) {
                            System.out.println(vm_neg());
                        } else if (tokens[0].equals("eq")) {
                            System.out.println(vm_eq());
                        } else if (tokens[0].equals("gt")) {
                            System.out.println(vm_gt());
                        } else if (tokens[0].equals("lt")) {
                            System.out.println(vm_lt());
                        } else if (tokens[0].equals("and")) {
                            System.out.println(vm_and());
                        } else if (tokens[0].equals("or")) {
                            System.out.println(vm_or());
                        } else if (tokens[0].equals("not")) {
                            System.out.println(vm_not());
                        } else if (tokens[0].equals("return")) {
                            System.out.println(vm_return());
                        }
                    } else if (tokens.length == 2) {
                        if (tokens[0].equals("label")) {
                            System.out.println(vm_label(tokens[1]));
                        } else if (tokens[0].equals("goto")) {
                            System.out.println(vm_goto(tokens[1]));
                        } else if (tokens[0].equals("if")) {
                            System.out.println(vm_if(tokens[1]));
                        }
                    } else if (tokens.length == 3) {
                        int t2;
                        try {
                            t2 = Integer.parseInt(tokens[2]);
                        } catch (Exception e) {
                            System.err.println("Unable to parse int.");
                            break;
                        }
                        if (tokens[0].equals("push")) {
                            System.out.println(vm_push(tokens[1], t2));
                        } else if (tokens[0].equals("pop")) {
                            System.out.println(vm_pop(tokens[1], t2));
                        } else if (tokens[0].equals("function")) {
                            System.out.println(vm_function(tokens[1], t2));
                        } else if (tokens[0].equals("call")) {
                            System.out.println(vm_call(tokens[1], t2));
                        }
                    }
                }
                sc.close();
            } catch (FileNotFoundException e) {
                System.err.println("File not found.");
            }
        }
    }

}
