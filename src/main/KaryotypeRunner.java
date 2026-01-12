package main;

import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import business.BiologicalOutcome;
import business.Clone;
import business.DetailedFormulaParser;
import business.FinalResult;
import business.ParseEvent;
import compiler.KaryotypeCleaner;
import compiler.KaryotypeLexer;
import compiler.KaryotypeLoader;
import compiler.KaryotypeParser;
import validation.ValidationError;
import validation.Validator;

/**
 * @author Lin Zhang
 * Programmer / Software Development Engineer
 * Institute for Informatics
 * Washington University School of Medicine in St. Louis
 * 
 * Date: August 30, 2021 
 * Update: January 02, 2026
 */
public class KaryotypeRunner {

	public static FinalResult getFinalResult(String input){
		FinalResult finalResult = new FinalResult();
		String inputNoSpace = input.replaceAll("\\s","");

		// Strip leading/trailing quotes (common in database exports)
		inputNoSpace = inputNoSpace.trim();
		if (inputNoSpace.startsWith("\"") && inputNoSpace.endsWith("\"")) {
			inputNoSpace = inputNoSpace.substring(1, inputNoSpace.length() - 1);
		}

		// Check if this is a multi-clone karyotype (contains / separating clones)
		// Multi-clone karyotypes MUST go through ANTLR to parse clone structure
		boolean isMultiClone = inputNoSpace.contains("]/") || inputNoSpace.matches(".*\\]\\s*/\\s*\\d+.*");

		// NEW: Check if detailed formula exists in the input
		if (DetailedFormulaParser.isDetailedFormula(inputNoSpace)) {
			// For non-multi-clone inputs with detailed formulas, use parseMixedFormat
			// Multi-clone inputs should go through standard ANTLR parsing directly
			if (isMultiClone) {
				// Multi-clone with detailed formulas: try standard ANTLR first
				// If that fails, fall through to handle it
			} else if (isMixedFormat(inputNoSpace)) {
				// Single-clone mixed format: parse both standard and detailed events
				return parseMixedFormat(inputNoSpace, finalResult);
			} else {
				// Pure detailed formula (single-clone): route to detailed formula parser only
				DetailedFormulaParser detailedParser = new DetailedFormulaParser();
				BiologicalOutcome b = detailedParser.parseDetailedFormula(inputNoSpace);

				finalResult.getBiologicalOutcomeList().add(b);
				finalResult.getBiologicalInterpretationList().add(
					BiologicalOutcome.getBiologicalInterpretation(b)
				);
				
				// Set clone information for JSON output
				// For pure detailed formulas, create a single clone with the full karyotype code
				List<Clone> singleClone = new java.util.ArrayList<>();
				Clone clone = new Clone();
				clone.setCloneCode(inputNoSpace);
				singleClone.add(clone);
				finalResult.setCloneCodeList(finalResult.getCloneCodeList(singleClone));
				finalResult.setCellNumList(finalResult.getCellNumList(singleClone));
				finalResult.setRelationshipList(finalResult.getRelationshipList(singleClone));
				
				return finalResult;
			}
		}

		// Preprocess to handle '?' symbols
		// Detect if input contains '?' and strip it for parsing
		boolean containsQuestionMark = inputNoSpace.contains("?");
		if (containsQuestionMark) {
			inputNoSpace = inputNoSpace.replaceAll("\\?", "");
			// Note: This loses information about WHERE the '?' was located --> so if there was a cytoband after '?' it is taken as certain, but if not, it is treates as uncertain
		}
		// Continues with normal ANTLR parsing:
		try {
			KaryotypeLexer lexer = new KaryotypeLexer(CharStreams.fromString(inputNoSpace));
			lexer.removeErrorListeners();
			lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			KaryotypeParser parser = new KaryotypeParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			ParseTree tree = parser.row();
			ParseTreeWalker walker = new ParseTreeWalker();
			KaryotypeLoader loader = new KaryotypeLoader();
			walker.walk(loader, tree);
			
			List<String> multiCloneErrorMsgList = loader.getErrorMsgList();
			if (multiCloneErrorMsgList.size() > 0) {
				finalResult.setContainingValidationError(true);
				finalResult.setValidationMessage(multiCloneErrorMsgList);
			} else {
				List<Clone> rowClones = loader.getRowClones();	
				finalResult.setCloneCodeList(finalResult.getCloneCodeList(rowClones));
				new ParseEvent().processMissingBreakpoints(rowClones);
				if (Validator.isValidRowClones(rowClones)) {
					new ParseEvent().markUncertainDerEvent(rowClones);
					for (BiologicalOutcome b: new ParseEvent().getMultipleCloneRowOutcome(rowClones)) {		
						finalResult.getBiologicalOutcomeList().add(b);
						finalResult.getBiologicalInterpretationList().add(BiologicalOutcome.getBiologicalInterpretation(b));
					}
					finalResult.setCellNumList(finalResult.getCellNumList(rowClones));
					finalResult.setRelationshipList(finalResult.getRelationshipList(rowClones));
				} else {
					finalResult.setContainingValidationError(true);
					finalResult.setValidationMessage(ValidationError.getRowClonesError(rowClones));
				}
			}		
			
		} catch (ParseCancellationException ex) {
			// Check if this is multi-clone with detailed formulas (failed ANTLR parsing)
			if (isMultiClone && DetailedFormulaParser.isDetailedFormula(inputNoSpace)) {
				// Try to parse multi-clone with detailed formulas by handling each clone separately
				return parseMultiCloneWithDetailedFormulas(inputNoSpace, finalResult);
			}
			
			finalResult.setContainingLexerParserError(true);
			KaryotypeLexer lexer = new KaryotypeLexer(CharStreams.fromString(inputNoSpace));
			ListErrorListener listErrorListener = new ListErrorListener();
			lexer.removeErrorListeners();
			lexer.addErrorListener(listErrorListener);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			KaryotypeParser parser = new KaryotypeParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(listErrorListener);
			parser.row();
			
			listErrorListener.getErrorList(inputNoSpace, listErrorListener.getErrorStrings())
			                 .forEach(e-> finalResult.getTokenErrorList().add(e));
				
			KaryotypeLexer lexer1 = new KaryotypeLexer(CharStreams.fromString(inputNoSpace));
			lexer1.removeErrorListeners();
			CommonTokenStream tokens1 = new CommonTokenStream(lexer1);
			KaryotypeParser parser1 = new KaryotypeParser(tokens1);
			parser1.removeErrorListeners();
			RuleContext tree1 = parser1.row();
			ParseTreeWalker walker1 = new ParseTreeWalker();
			KaryotypeCleaner cleaner = new KaryotypeCleaner(tokens1);
			walker1.walk(cleaner, tree1);
			String inputRevised = cleaner.getRewriter().getText();
//			System.out.println(inputRevised);
			try {
				KaryotypeLexer lexer2 = new KaryotypeLexer(CharStreams.fromString(inputRevised));
				lexer2.removeErrorListeners();
				lexer2.addErrorListener(ThrowingErrorListener.INSTANCE);
				CommonTokenStream tokens2 = new CommonTokenStream(lexer2);
				KaryotypeParser parser2 = new KaryotypeParser(tokens2);
				parser2.removeErrorListeners();
				parser2.addErrorListener(ThrowingErrorListener.INSTANCE);
				parser2.row();
//				String errorMessage = "Maybe you mean the following karyotype:\r\n" + inputRevised + "\r\n";	
				finalResult.setRevisedKaryotype(inputRevised);
//				finalResult.setErrorMessage(errorMessage); // I don't need this, as I have already created a list of TokenError
			} catch (ParseCancellationException e) {				
			}	
			
		}
		
		return finalResult;
	}

	/**
	 * Check if the input contains mixed format (both standard and detailed formulas)
	 * Mixed format example: 46,XX,del(5)(q13q31),der(13)(13pter->13q10::15q10->15q21::13q14->13qter)
	 */
	private static boolean isMixedFormat(String input) {
		// Check if there are both standard and detailed formulas in the input
		// Standard events: del, dup, inv, t, add, der (without detailed notation), dic (without detailed notation), r, +, -
		// Detailed events: der/dic/r with -> or :: in the notation part

		// First, remove all detailed formulas from the input to avoid false positives
		String inputWithoutDetailed = input.replaceAll("(?:der|dic|r)\\([^)]+\\)\\([^)]*(?:->|::)[^)]*\\)", "");

		// Look for standard events in the remaining input
		// Standard events: del(...), dup(...), inv(...), t(...), add(...), der(...), dic(...), r(...), i(...), +, -
		String[] standardEvents = {"del\\(", "dup\\(", "inv\\(", "t\\(", "add\\(", "i\\(",
		                            "idic\\(", "ins\\(", "trp\\(", "qdp\\(", 
		                            "der\\(", "dic\\(", "r\\(", "\\+", "-"};

		for (String event : standardEvents) {
			if (inputWithoutDetailed.matches(".*" + event + ".*")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Parse mixed format karyotype (standard ISCN + detailed formulas)
	 * Strategy:
	 * 1. Extract detailed formulas first
	 * 2. Remove them from input, leaving standard ISCN
	 * 3. Parse standard ISCN with ANTLR
	 * 4. Parse detailed formulas with DetailedFormulaParser
	 * 5. Merge LGF results
	 */
	private static FinalResult parseMixedFormat(String input, FinalResult finalResult) {
		// Check if this is multi-clone (has ] followed by /)
		boolean isMultiClone = input.contains("]/");

		// Step 1: Extract detailed formulas using balanced parenthesis parsing
		java.util.List<String> detailedFormulas = extractDetailedFormulas(input);

		// Step 1b: Remove detailed formulas from input to get standard-only input
		String standardInput = input;
		for (String formula : detailedFormulas) {
			standardInput = standardInput.replace(formula, "");
		}
		
		// Clean up the standard input
		standardInput = standardInput
			.replaceAll(",,+", ",")  // Remove double commas
			.replaceAll(",$", "")     // Remove trailing comma
			.replaceAll(",\\)", ")"); // Remove comma before closing paren

		// For multi-clone input, handle each clone separately
		if (isMultiClone && hasStandardEvents(standardInput)) {
			// Split by ]/ to get individual clones
			String[] cloneSections = standardInput.split("\\]/");
			StringBuilder reconstructedInput = new StringBuilder();

			for (int i = 0; i < cloneSections.length; i++) {
				String clone = cloneSections[i];
				if (i < cloneSections.length - 1) {
					// Not the last clone - add back the ][count] part
					// Extract the count (e.g., "[8]" becomes "8")
					java.util.regex.Pattern countPattern = java.util.regex.Pattern.compile("\\[(\\d+)\\]$");
					java.util.regex.Matcher matcher = countPattern.matcher(clone);
					if (matcher.find()) {
						clone = matcher.replaceAll(""); // Remove the count
					}
					reconstructedInput.append(clone).append("]/");
				} else {
					// Last clone - just append as-is
					reconstructedInput.append(clone);
				}
			}

			standardInput = reconstructedInput.toString();
		}

		// Step 2: Parse standard ISCN events (if any remain)
		BiologicalOutcome standardOutcome = null;
		List<Clone> rowClones = new java.util.ArrayList<>();
		
		if (hasStandardEvents(standardInput)) {
			// Preprocess to handle '?' symbols
			String processedStandardInput = standardInput;
			if (processedStandardInput.contains("?")) {
				processedStandardInput = processedStandardInput.replaceAll("\\?", "");
			}

			try {
				KaryotypeLexer lexer = new KaryotypeLexer(CharStreams.fromString(processedStandardInput));
				lexer.removeErrorListeners();
				lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				KaryotypeParser parser = new KaryotypeParser(tokens);
				parser.removeErrorListeners();
				parser.addErrorListener(ThrowingErrorListener.INSTANCE);
				ParseTree tree = parser.row();
				ParseTreeWalker walker = new ParseTreeWalker();
				KaryotypeLoader loader = new KaryotypeLoader();
				walker.walk(loader, tree);

				rowClones = loader.getRowClones();
				new ParseEvent().processMissingBreakpoints(rowClones);
				new ParseEvent().markUncertainDerEvent(rowClones);

				// Get outcomes for all clones in multi-clone case
				List<BiologicalOutcome> outcomes = new ParseEvent().getMultipleCloneRowOutcome(rowClones);
				if (!outcomes.isEmpty()) {
					standardOutcome = outcomes.get(0);
				}
			} catch (ParseCancellationException ex) {
				// Standard parsing failed, continue with detailed formulas only
			}
		}

		// Step 3: Parse detailed formulas
		BiologicalOutcome detailedOutcome = null;
		if (!detailedFormulas.isEmpty()) {
			// Reconstruct input with only detailed formulas for the parser
			String detailedOnlyInput = String.join(",", detailedFormulas);
			DetailedFormulaParser detailedParser = new DetailedFormulaParser();
			detailedOutcome = detailedParser.parseDetailedFormula(detailedOnlyInput);
		}

		// Step 4: Merge LGF results
		BiologicalOutcome mergedOutcome = mergeBiologicalOutcomes(standardOutcome, detailedOutcome);

		if (mergedOutcome != null) {
			finalResult.getBiologicalOutcomeList().add(mergedOutcome);
			finalResult.getBiologicalInterpretationList().add(
				BiologicalOutcome.getBiologicalInterpretation(mergedOutcome)
			);
			
			// Set clone information for JSON output (needed by AggregateJsonForBatchFile)
			finalResult.setCloneCodeList(finalResult.getCloneCodeList(rowClones));
			finalResult.setCellNumList(finalResult.getCellNumList(rowClones));
			finalResult.setRelationshipList(finalResult.getRelationshipList(rowClones));
		}

		return finalResult;
	}

	/**
	 * Check if input has standard ISCN events (after removing detailed formulas)
	 */
	private static boolean hasStandardEvents(String input) {
		// Check if there's anything meaningful left besides chromosome count and sex chromosomes
		// Remove chromosome count (e.g., "46,XX,")
		String withoutCount = input.replaceAll("^\\d+,[XY]+,?", "");
		return !withoutCount.trim().isEmpty() && !withoutCount.trim().equals(",");
	}

	/**
	 * Merge two BiologicalOutcome objects by combining their LGF vectors
	 */
	private static BiologicalOutcome mergeBiologicalOutcomes(BiologicalOutcome standard, BiologicalOutcome detailed) {
		if (standard == null && detailed == null) {
			return null;
		}
		if (standard == null) {
			return detailed;
		}
		if (detailed == null) {
			return standard;
		}

		// Merge LGF vectors (element-wise addition)
		java.util.List<java.util.List<Integer>> mergedLGF = new java.util.ArrayList<>();
		java.util.List<java.util.List<Integer>> standardLGF = standard.getKaryotypeLGF();
		java.util.List<java.util.List<Integer>> detailedLGF = detailed.getKaryotypeLGF();

		// Merge loss, gain, and fusion vectors
		for (int i = 0; i < 3; i++) {
			java.util.List<Integer> mergedVector = new java.util.ArrayList<>();
			for (int j = 0; j < standardLGF.get(i).size(); j++) {
				int standardValue = standardLGF.get(i).get(j);
				int detailedValue = detailedLGF.get(i).get(j);
				mergedVector.add(standardValue + detailedValue);
			}
			mergedLGF.add(mergedVector);
		}

		// Merge uncertain events and detailed system lists
		java.util.List<String> mergedUncertain = new java.util.ArrayList<>(standard.getUncertainEventsList());
		mergedUncertain.addAll(detailed.getUncertainEventsList());

		java.util.List<String> mergedDetailed = new java.util.ArrayList<>(standard.getDetailedSystem());
		mergedDetailed.addAll(detailed.getDetailedSystem());

		return new BiologicalOutcome(mergedLGF, mergedUncertain, mergedDetailed);
	}

	/**
	 * Extract all detailed formulas from input string using balanced parenthesis parsing.
	 * This correctly handles nested parentheses and multiple detailed formulas.
	 * Only extracts formulas with -> or :: inside (actual detailed formulas).
	 * 
	 * Examples:
	 * - "der(11)(11pter->11q11::22q11->22q13::11q22->11qter)" -> [der(11)(11pter->11q11::22q11->22q13::11q22->11qter)]
	 * - "der(9)t(9;17)(q11;q34)" -> [] (NOT extracted - no -> or ::)
	 */
	private static java.util.List<String> extractDetailedFormulas(String input) {
		java.util.List<String> detailedFormulas = new java.util.ArrayList<>();
		
		// Find all der/dic/r patterns with balanced parentheses
		int i = 0;
		while (i < input.length()) {
			// Look for der, dic, or r at position i
			// For der/dic, we need 4 chars: "der(" or "dic("
			// For r, we need 2 chars: "r(" and must ensure r is not part of "der" or "dic"
			
			boolean foundDer = i <= input.length() - 4 && input.startsWith("der(", i);
			boolean foundDic = i <= input.length() - 4 && input.startsWith("dic(", i);
			boolean foundR = i <= input.length() - 2 && input.startsWith("r(", i) && 
							 (i == 0 || input.charAt(i-1) == ','); // r must be after comma or at start
			
			if (foundDer || foundDic || foundR) {
				// Find the start of the formula
				int formulaStart = i;
				
				// Skip past "der(" or "dic(" or "r("
				int j = i + (foundR ? 2 : 4);
				
				// Find matching ) for the first set of parentheses (base chromosome spec)
				int parenCount = 1;
				while (j < input.length() && parenCount > 0) {
					if (input.charAt(j) == '(') parenCount++;
					else if (input.charAt(j) == ')') parenCount--;
					j++;
				}
				
				// Now j is right after the first )
				// Check if the next character is (
				if (j < input.length() && input.charAt(j) == '(') {
					// Find matching ) for the second set of parentheses (detailed formula)
					int secondParenStart = j;
					j++; // Skip the (
					parenCount = 1;
					while (j < input.length() && parenCount > 0) {
						if (input.charAt(j) == '(') parenCount++;
						else if (input.charAt(j) == ')') parenCount--;
						j++;
					}
					
					// Extract the complete formula
					String formula = input.substring(formulaStart, j);
					
					// Only add if it contains -> or :: (actual detailed formula)
					if (formula.contains("->") || formula.contains("::")) {
						detailedFormulas.add(formula);
					}
					
					i = j;
				} else {
					i++;
				}
			} else {
				i++;
			}
		}
		
		return detailedFormulas;
	}

	/**
	 * Parse multi-clone karyotype with detailed formulas by processing each clone separately
	 */
	private static FinalResult parseMultiCloneWithDetailedFormulas(String input, FinalResult finalResult) {
		try {
			// Split by ]/ to get individual clones
			String[] cloneParts = input.split("\\]/");
			List<BiologicalOutcome> allOutcomes = new java.util.ArrayList<>();
			List<String> allCodes = new java.util.ArrayList<>();
			List<Integer> allCellNums = new java.util.ArrayList<>();
			List<String> allRelationships = new java.util.ArrayList<>();
			
			for (int i = 0; i < cloneParts.length; i++) {
				String clonePart = cloneParts[i];
				if (i < cloneParts.length - 1) {
					// Not the last clone - add back the ] and extract count
					clonePart = clonePart + "]";
				}
				
				// Extract cell count if present
				int cellCount = 0;
				java.util.regex.Pattern countPattern = java.util.regex.Pattern.compile("\\[(\\d+)\\]");
				java.util.regex.Matcher matcher = countPattern.matcher(clonePart);
				if (matcher.find()) {
					cellCount = Integer.parseInt(matcher.group(1));
					// Remove the count from the clone part for parsing
					clonePart = clonePart.substring(0, matcher.start()) + clonePart.substring(matcher.end());
				}
				
				// Parse individual clone using standard getFinalResult logic
				FinalResult cloneResult = getFinalResult(clonePart);
				
				// Collect results from this clone
				if (!cloneResult.getBiologicalOutcomeList().isEmpty()) {
					for (BiologicalOutcome outcome : cloneResult.getBiologicalOutcomeList()) {
						allOutcomes.add(outcome);
						allCodes.add(clonePart);
						allCellNums.add(cellCount);
						allRelationships.add("");
					}
				}
			}
			
			// Add all collected outcomes to final result
			for (BiologicalOutcome outcome : allOutcomes) {
				finalResult.getBiologicalOutcomeList().add(outcome);
				finalResult.getBiologicalInterpretationList().add(
					BiologicalOutcome.getBiologicalInterpretation(outcome)
				);
			}
			
			finalResult.setCloneCodeList(allCodes);
			finalResult.setCellNumList(allCellNums);
			finalResult.setRelationshipList(allRelationships);
			
			return finalResult;
		} catch (Exception e) {
			// If multi-clone parsing fails, set error
			finalResult.setContainingLexerParserError(true);
			finalResult.setErrorMessage("Failed to parse multi-clone karyotype with detailed formulas: " + e.getMessage());
			return finalResult;
		}
	}

}

