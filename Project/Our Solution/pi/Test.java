
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// set default parameters
		String fileName = null;
		double support = 3;
		double confidence = 65.0;
		// read arguments & set parameters
		if(args.length>0){
			if(args[0]!= null){
				fileName= args[0];
			}
			if(args.length > 1 && args[1]!=null){
				support = Double.parseDouble(args[1]);
			}
			
			if(args.length > 2 && args[2]!=null){
				confidence = Double.parseDouble(args[2]);
			}
			
			
			ProgramCall pg = new ProgramCall();
			pg.anlyseLLVMoutPut(fileName);//read file, generate HashSet "methods" & HashMap "program"
			pg.deleteNullCaller();//delete "null function"
			pg.createPairs();//read from HashMap "program", generate HashMap "pairs" 
			pg.analyse();//read HashMap "pairs" & HashMap "programCounter", generate HashMap "analysis"
			pg.displayBugs(support, confidence);//read HashMap "analysis" and HashMap "program",generate bug report
			
			// for Debugging
			//pg.sortPair();
			//pg.displayPairs();
			//pg.analysePairs();
			//pg.displayAnalysis();

			

		
		}

	}

}
