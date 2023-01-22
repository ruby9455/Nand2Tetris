import java.util.HashMap;

public class SymbolTable {
    // attributes
    private static HashMap<String, Integer> symbolTable;

    /**
     * Constructor for the Symbol table
     */
    public SymbolTable() {
        // Your code here
        symbolTable = new HashMap<String, Integer>(30);

        for (int i = 0; i < 16; i++) {
            String key = "R" + i;
            symbolTable.put(key, i);
        }

        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
    }

    /**
     * Adds a symbol to the symbol table
     * 
     * @param symbol The name of the symbol
     * @param value  The address for the symbol
     */
    public static void addSymbol(String symbol, int value) {
        // System.out.println("addSymbol is called");
        // Your code here
        if (!symbolTable.containsKey(symbol)) {
            symbolTable.put(symbol, value);
            // System.out.println(symbol + " is added to the Symbol Table");
        }
    }

    /**
     * Gets a symbol from the symbol table
     * 
     * @param symbol The name of the symbol
     * @return The address for the symbol or -1 if the symbol isn't in the table
     */
    public static int getSymbol(String symbol) {
        // Your code here
        if (symbolTable.containsKey(symbol)) {
            return symbolTable.get(symbol);
        }
        return -1;
    }

}
