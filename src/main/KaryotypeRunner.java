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

		// NEW: Check if detailed formula exists in the input
		if (DetailedFormulaParser.isDetailedFormula(inputNoSpace)) {
			// Check if it's ONLY detailed formulas or mixed format
			if (isMixedFormat(inputNoSpace)) {
				// Mixed format: parse both standard and detailed events
				return parseMixedFormat(inputNoSpace, finalResult);
			} else {
				// Pure detailed formula: route to detailed formula parser only
				DetailedFormulaParser detailedParser = new DetailedFormulaParser();
				BiologicalOutcome b = detailedParser.parseDetailedFormula(inputNoSpace);

				finalResult.getBiologicalOutcomeList().add(b);
				finalResult.getBiologicalInterpretationList().add(
					BiologicalOutcome.getBiologicalInterpretation(b)
				);
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
		// Check if there are standard ISCN events alongside detailed formulas
		// Standard events include: del, dup, inv, t, add, etc.
		// They have the pattern: event(chr)(breakpoints) where breakpoints don't contain ::

		// Look for events that are NOT detailed formulas
		// Standard events: del(...), dup(...), inv(...), t(...), add(...), etc.
		String[] standardEvents = {"del\\(", "dup\\(", "inv\\(", "t\\(", "add\\(", "i\\(", "r\\(",
		                            "dic\\(", "idic\\(", "ins\\(", "trp\\(", "qdp\\(", "\\+", "-"};

		for (String event : standardEvents) {
			if (input.matches(".*" + event + ".*")) {
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
		// Step 1: Extract detailed formulas
		java.util.List<String> detailedFormulas = new java.util.ArrayList<>();
		java.util.regex.Pattern derPattern = java.util.regex.Pattern.compile("der\\(([^)]+)\\)\\(([^)]+)\\)");
		java.util.regex.Matcher derMatcher = derPattern.matcher(input);

		StringBuilder standardOnlyInput = new StringBuilder(input);
		int offset = 0; // Track position changes due to replacements

		while (derMatcher.find()) {
			String fullMatch = derMatcher.group(0);
			// Check if this is a detailed formula (contains -> or ::)
			if (fullMatch.contains("->") || fullMatch.contains("::")) {
				detailedFormulas.add(fullMatch);

				// Remove from standard input (replace with placeholder to maintain comma structure)
				int start = derMatcher.start() - offset;
				int end = derMatcher.end() - offset;

				// Check if there's a comma before or after that we need to handle
				String before = (start > 0) ? standardOnlyInput.substring(Math.max(0, start - 1), start) : "";
				String after = (end < standardOnlyInput.length()) ? standardOnlyInput.substring(end, Math.min(end + 1, standardOnlyInput.length())) : "";

				// Remove the detailed formula
				standardOnlyInput.delete(start, end);

				// Clean up extra commas
				if (before.equals(",") && after.equals(",")) {
					// Two commas in a row, remove one
					if (start < standardOnlyInput.length() && standardOnlyInput.charAt(start) == ',') {
						standardOnlyInput.deleteCharAt(start);
					}
				}

				offset += fullMatch.length();
			}
		}

		// Clean up the standard input
		String standardInput = standardOnlyInput.toString()
			.replaceAll(",,+", ",")  // Remove double commas
			.replaceAll(",$", "")     // Remove trailing comma
			.replaceAll(",\\)", ")"); // Remove comma before closing paren

		// Step 2: Parse standard ISCN events (if any remain)
		BiologicalOutcome standardOutcome = null;
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

				List<Clone> rowClones = loader.getRowClones();
				new ParseEvent().processMissingBreakpoints(rowClones);
				new ParseEvent().markUncertainDerEvent(rowClones);

				// Get the first biological outcome (assuming single clone for now)
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

}
