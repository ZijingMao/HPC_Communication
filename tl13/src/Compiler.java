import java.io.InputStream;
import java.io.FileInputStream;

public class Compiler {
	public static void main (String[] args) throws Exception {
        String inputFileName = args[0];
        int baseNameOffset = inputFileName.length() - 5;

        String baseName;
        if (inputFileName.substring(baseNameOffset).equals(".tl13"))
            baseName = inputFileName.substring(0,baseNameOffset);
        else
            throw new RuntimeException("inputFileName does not end in .tl13");

        String parseOutName = baseName + ".pt.dot";
        String cfgName = baseName + ".iloc.cfg.dot";
        String mips = baseName + ".s";

		System.out.println("Input file: " + inputFileName);
		System.out.println("Output file: " + parseOutName);
		System.out.println("Output CFG: " + cfgName);
		System.out.println("mips: "+ mips);
		
		InputStream tl13In = new FileInputStream(inputFileName);
		Scanner scanner = new Scanner(tl13In);
		scanner.scan();
		
		Parser parser = new Parser(scanner, parseOutName, cfgName,mips);
		parser.getAst();
	}
}
