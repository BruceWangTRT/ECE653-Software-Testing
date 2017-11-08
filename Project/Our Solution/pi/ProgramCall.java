import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProgramCall {

	// methods:In this HashSet we store all the method of our program
	// program: this Map represent our program the Key is the method the value
	// is the st of callee
	// pairs: this Map contain the set of pairs and the location where this pair
	// appear
	// analysis: this map contain the pair the support of confidence
	// programCounter: this map contain the method and the number of time it
	// appears
	// bugs: in this set we store the bugs that we get and we need it to reduce
	// falsePositives

	private Set<Method> methods = new HashSet<Method>();
	private Map<Method, Set<Method>> program = new HashMap<Method, Set<Method>>();
	private HashMap<Pair, HashSet<Method>> pairs = new HashMap<Pair, HashSet<Method>>();
	private Map<Pair, double[]> analysis = new HashMap<Pair, double[]>();
	private HashMap<Method, int[]> programCounter = new HashMap<Method, int[]>();
	public Set<Bug> bugs = new HashSet<Bug>();

	// this two sets are used for the recursive approach to expand methods
	private Set<Method> tempMethods;
	private Set<Method> excludedMethods;

	/*
	 * Analyse the output of LLVM and create the Map program
	 */

	public void anlyseLLVMoutPut(String fileName) {
		if (fileName == null)
			return;
		String currentCaller = null;
		String currentCallee = null;
		Method caller = null;
		Method callee = null;

		/* regular expression for match */
		String regEx1 = "'(.+)'"; // string within ' '
		String regEx2 = "<<(.+)>><"; // string within << >>

		Pattern pattern1 = Pattern.compile(regEx1);
		Pattern pattern2 = Pattern.compile(regEx2);

		try {
			String encoding = "UTF8"; // large character set
			File file = new File(fileName);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);

				/* use bufferedreader to improve performance */
				BufferedReader bufferedReader = new BufferedReader(read);
				String singleLine = null;

				/* read file line by line */
				while ((singleLine = bufferedReader.readLine()) != null) {
					/* line starts with call is recognized as caller */
					if (singleLine.trim().startsWith("Call ")) {
						Matcher matcher1 = pattern1.matcher(singleLine);
						Matcher matcher2 = pattern2.matcher(singleLine);
						if (matcher1.find()) {
							currentCaller = matcher1.group(1);
							caller = new Method(currentCaller.hashCode(),
									currentCaller);
							this.addMethod(caller);
						} else if (matcher2.find()) {
							currentCaller = matcher2.group(1);
							caller = new Method(currentCaller.hashCode(),
									currentCaller);
							this.addMethod(caller);
						}
					}
					/* line starts with CS is recognized as callee */
					else if (singleLine.trim().startsWith("CS")) {
						Matcher matcher1 = pattern1.matcher(singleLine);
						if (matcher1.find()) {
							currentCallee = matcher1.group(1);
							callee = new Method(currentCallee.hashCode(),
									currentCallee);
							this.addCallee(caller, callee);
						}
					}
				}
				read.close();
			}
		} catch (Exception ex) {
			System.out.println("Oops, failed");
			ex.printStackTrace();
		}
	}

	/*
	 * add the method to the set of methods
	 */
	public void addMethod(Method m) {
		if (!methods.contains(m)) {
			methods.add(m);
			program.put(m, new HashSet<Method>());
		}
	}

	/*
	 * Add the method m2 tho the set of calee of the method m1
	 */

	public void addCallee(Method m1, Method m2) {
		if (!methods.contains(m2)) {
			addMethod(m2);
		}
		Set<Method> callees = program.get(m1);
		callees.add(m2);

	}

	public void deleteNullCaller() {
		Method nullCaller = new Method("null function".hashCode(),
				"null function");
		program.remove(nullCaller);
		methods.remove(nullCaller);
	}

	public void createPairs() {
		for (Method entry : methods) {// go through all methods
			int length = program.get(entry).size();
			Object[] array = program.get(entry).toArray();
			// create a temporary set for outer loop, it contains already met
			// methods in this callee set
			HashSet<Method> tempContainer = new HashSet<Method>();
			for (int i = 0; i < length; i++) {// outer loop
				Method m1 = (Method) array[i];
				// if have not met, add to temporary set & programCounter for
				// this method +1
				if (!tempContainer.contains(m1)) {
					if (programCounter.containsKey(m1)) {// Met in other callee
															// set, count++
						int[] counter = new int[1];
						counter[0] = programCounter.get(m1)[0] + 1;
						programCounter.put(m1, counter);
						tempContainer.add(m1);
					} else {// First meeting,count=1
						int[] counter = new int[1];
						counter[0] = 1;
						programCounter.put(m1, counter);
						tempContainer.add(m1);
					}
				}
				for (int j = i + 1; j < length; j++) {// inner loop
					Method m2 = (Method) array[j];
					if (m1.getId() != m2.getId()) {
						Pair newPair = new Pair(m1, m2);
						if (pairs.containsKey(newPair)) {// if exist, just add
															// this site
							if (pairs.get(newPair).contains(entry)) {
								continue;
							} else {
								pairs.get(newPair).add(entry);
							}
						} else {// if not exist, add pair & add site
							pairs.put(newPair, new HashSet<Method>());
							pairs.get(newPair).add(entry);
						}
					}
				}
			}
		}
	}

	/*
	 * analyse the support and confidence for each pair and store the result in
	 * the map Analysis
	 */

	public void analyse() {
		Iterator<Pair> itr = pairs.keySet().iterator();
		int supportM1, supportM2, supportPair;
		supportM1 = supportM2 = supportPair = 0;
		while (itr.hasNext()) {
			Pair pair = itr.next();
			if (programCounter.containsKey(pair.getMethod1())) {
				supportM1 = programCounter.get(pair.getMethod1())[0];
			}// else supportM1 = 0 as initial
			if (programCounter.containsKey(pair.getMethod2())) {
				supportM2 = programCounter.get(pair.getMethod2())[0];
			}// else supportM2 = 0 as initial
			supportPair = pairs.get(pair).size();
			double[] result = new double[5];
			result[0] = supportM1;
			result[1] = supportM2;
			result[2] = supportPair;
			if (supportM1 != 0) {
				double confidenceM1 = (double) supportPair / (double) supportM1
						* 100;
				result[3] = confidenceM1;
			}
			if (supportM2 != 0) {
				double confidenceM2 = (double) supportPair / (double) supportM2
						* 100;
				result[4] = confidenceM2;
			}

			analysis.put(pair, result);
		}
	}

	public void displayAnalysis() {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		for (Pair key : analysis.keySet()) {
			double[] tab = analysis.get(key);
			// output format is changed
			System.out.println(key.getMethod1().toString() + "  "
					+ key.getMethod2().toString() + " | " + nf.format(tab[0])
					+ " | " + nf.format(tab[1]) + " | " + nf.format(tab[2])
					+ " | " + nf.format(tab[3]) + " | " + nf.format(tab[4]));
		}

	}

	/*
	 * display the bug report based on the given support and confidence values
	 */
	public void displayBugs(double support, double confidence) {
		DecimalFormat nf1 = new DecimalFormat("#"); // change the output format
		DecimalFormat nf2 = new DecimalFormat("#.00");
		for (Pair key : analysis.keySet()) {
			double[] tab = analysis.get(key);
			if (tab[2] >= support && tab[3] >= confidence) {
				for (Method keyM : program.keySet()) {

					Set<Method> calledMethods = program.get(keyM);
					if (calledMethods.contains(key.getMethod1())
							&& !calledMethods.contains(key.getMethod2())) {

						// output format is changed
						System.out.println("bug: "
								+ key.getMethod1().toString() + " in "
								+ keyM.toString() + ", pair: ("
								+ key.toString() + "), support: "
								+ nf1.format(tab[2]) + ", confidence: "
								+ nf2.format(tab[3]) + "%");
					}

				}
			}
			if (tab[2] >= support && tab[4] >= confidence) {
				for (Method keyM : program.keySet()) {

					Set<Method> calledMethods = program.get(keyM);
					if (calledMethods.contains(key.getMethod2())
							&& !calledMethods.contains(key.getMethod1())) {
						// output format is changed
						System.out.println("bug: "
								+ key.getMethod2().toString() + " in "
								+ keyM.toString() + ", pair: ("
								+ key.toString() + "), support: "
								+ nf1.format(tab[2]) + ", confidence: "
								+ nf2.format(tab[4]) + "%");
					}

				}
			}

		}

	}

	/*
	 * the following methods are used for part c
	 */

	// analyse the support and confidence for pairs already created
	public void analysePairs() {
		Iterator<Pair> itr = pairs.keySet().iterator();
		;
		int supporM1, supportM2, supportPair;
		boolean testM1, testM2;
		while (itr.hasNext()) {
			Pair pair = itr.next();
			supporM1 = supportM2 = supportPair = 0;
			for (Method key : program.keySet()) {
				if (key.toString().equals("null function")) // add the if
															// statement !!!
					continue;
				Set<Method> calledMethods = program.get(key);
				testM1 = testM2 = false;
				if (calledMethods.contains(pair.getMethod1())) {
					testM1 = true;
					supporM1++;
				}
				if (calledMethods.contains(pair.getMethod2())) {
					testM2 = true;
					supportM2++;
				}
				if (testM1 == true && testM2 == true) {
					supportPair++;
				}
			}
			double[] result = new double[5];
			result[0] = supporM1;
			result[1] = supportM2;
			result[2] = supportPair;
			if (supporM1 != 0) {
				double confidenceM1 = (double) supportPair / (double) supporM1
						* 100;
				result[3] = confidenceM1;
			}
			if (supportM2 != 0) {
				double confidenceM2 = (double) supportPair / (double) supportM2
						* 100;
				result[4] = confidenceM2;
			}

			analysis.put(pair, result);

		}
	}

	// find and store bugs into the set of bug used to reduce the number of
	// false positives
	public void storeBugs(double support, double confidence) {
		DecimalFormat nf1 = new DecimalFormat("#"); // change the output format
		DecimalFormat nf2 = new DecimalFormat("#.00");
		for (Pair key : analysis.keySet()) {
			double[] tab = analysis.get(key);
			if (tab[2] >= support && tab[3] >= confidence) {
				for (Method keyM : program.keySet()) {

					Set<Method> calledMethods = program.get(keyM);
					if (calledMethods.contains(key.getMethod1())
							&& !calledMethods.contains(key.getMethod2())) {
						// create bug
						Bug bug = new Bug(key.getMethod1(), keyM, key, tab[2],
								tab[3]);
						bugs.add(bug);

						// output format is changed
						System.out.println("bug: "
								+ key.getMethod1().toString() + " in "
								+ keyM.toString() + ", pair: ("
								+ key.toString() + "), support: "
								+ nf1.format(tab[2]) + ", confidence: "
								+ nf2.format(tab[3]) + "%");
					}

				}
			}
			if (tab[2] >= support && tab[4] >= confidence) {
				for (Method keyM : program.keySet()) {

					Set<Method> calledMethods = program.get(keyM);
					if (calledMethods.contains(key.getMethod2())
							&& !calledMethods.contains(key.getMethod1())) {
						// create bug
						Bug bug = new Bug(key.getMethod2(), keyM, key, tab[2],
								tab[4]);
						bugs.add(bug);

						// output format is changed
						System.out.println("bug: "
								+ key.getMethod2().toString() + " in "
								+ keyM.toString() + ", pair: ("
								+ key.toString() + "), support: "
								+ nf1.format(tab[2]) + ", confidence: "
								+ nf2.format(tab[4]) + "%");
					}

				}
			}

		}

	}

	/*
	 * For each bug we get the list of the callee related to the location, we
	 * parse this set, expand each called method only one level and test if the
	 * missing method of the pair appears or not
	 */
	public void falsePositiveFirstLevel() {

		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		Iterator<Bug> itr = bugs.iterator();
		while (itr.hasNext()) {
			Bug bug = (Bug) itr.next();
			Set<Method> callee = program.get(bug.getLocation());
			Iterator<Method> itr2 = callee.iterator();
			Boolean falsePositive = false;
			while (!falsePositive && itr2.hasNext()) {
				Method m = (Method) itr2.next();
				if (m.equals(bug.getError()) == false
						&& program.get(m).contains(
								bug.getPair().getTwin(bug.getError()))) {
					falsePositive = true;
				}
			}
			if (!falsePositive) {

				System.out.println("bug: " + bug.getError().toString() + " in "
						+ bug.getLocation().toString() + " , pair: ("
						+ bug.getPair().toString() + "), support: "
						+ nf.format(bug.getSupport()) + ", confidence: "
						+ nf.format(bug.getConfidence()) + "%");
			}
		}

	}

	/*
	 *expand the deep level for all method 
	 */
	public Map<Method, Set<Method>> createCompleteProgram(
			Map<Method, Set<Method>> program) {
		Map<Method, Set<Method>> completeProgram = new HashMap<Method, Set<Method>>();
		Iterator<Method> i = program.keySet().iterator();
		// Program key iterator
		while (i.hasNext()) {
			Method key = i.next();
			if (!key.toString().equals("null function")) {
				tempMethods = new HashSet<Method>();
				excludedMethods = new HashSet<Method>();
				Iterator<Method> j = program.get(key).iterator();
				while (j.hasNext()) {
					Method m = j.next();
					if (!m.equals(key))
						createCompleteMethodSet(m, program);
				}
				completeProgram.put(key, tempMethods);
			}

		}
		return completeProgram;
	}
	/*
	 * recursive call to expand methods
	 */
	private void createCompleteMethodSet(Method method,
			Map<Method, Set<Method>> program) {
		if (program.get(method).size() == 0)
			tempMethods.add(method);
		else {
			excludedMethods.add(method);
			Iterator<Method> j = program.get(method).iterator();
			while (j.hasNext()) {
				Method m = j.next();
				if (!excludedMethods.contains(m))
					createCompleteMethodSet(m, program);
			}
		}

	}
	/*
	 * Analyze the pairs after deep level expansion
	 */
	public void analyseDeepLevel() {
		analysis.clear();
		bugs.clear();
		programCounter.clear();
		program = createCompleteProgram(program);
		analysePairs();

	}
	/*
	 *  Recreate pairs and analyze the deep level expansion of methods
	 */

	public void analyseDeepLevelNewPairs() {
		analysis.clear();
		bugs.clear();
		pairs.clear();
		programCounter.clear();
		program = createCompleteProgram(program);
		createPairs();
		analyse();

	}

}
