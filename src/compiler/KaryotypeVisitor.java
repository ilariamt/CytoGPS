// Generated from Karyotype.g4 by ANTLR 4.7

package compiler; 
import java.util.*; 

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link KaryotypeParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface KaryotypeVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#row}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRow(KaryotypeParser.RowContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#rowTypeI}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRowTypeI(KaryotypeParser.RowTypeIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#firstClone}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFirstClone(KaryotypeParser.FirstCloneContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#clone}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClone(KaryotypeParser.CloneContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#nonclonalClone}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonclonalClone(KaryotypeParser.NonclonalCloneContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#rowTypeII}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRowTypeII(KaryotypeParser.RowTypeIIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#firstStemlineGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFirstStemlineGroup(KaryotypeParser.FirstStemlineGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#otherStemlineGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOtherStemlineGroup(KaryotypeParser.OtherStemlineGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#stemlineClone}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStemlineClone(KaryotypeParser.StemlineCloneContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#additionalStemlineClone}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditionalStemlineClone(KaryotypeParser.AdditionalStemlineCloneContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#sidelineCloneTypeI}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSidelineCloneTypeI(KaryotypeParser.SidelineCloneTypeIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#sidelineCloneTypeII}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSidelineCloneTypeII(KaryotypeParser.SidelineCloneTypeIIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#additionalClone}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditionalClone(KaryotypeParser.AdditionalCloneContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#karyotypeI}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKaryotypeI(KaryotypeParser.KaryotypeIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#karyotypeII}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKaryotypeII(KaryotypeParser.KaryotypeIIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#karyotypeIII}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKaryotypeIII(KaryotypeParser.KaryotypeIIIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#karyotypeIV}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKaryotypeIV(KaryotypeParser.KaryotypeIVContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#karyotypeV}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKaryotypeV(KaryotypeParser.KaryotypeVContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#ploidy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPloidy(KaryotypeParser.PloidyContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#tooManySlant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTooManySlant(KaryotypeParser.TooManySlantContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectSlant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectSlant(KaryotypeParser.IncorrectSlantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code correctModalNum}
	 * labeled alternative in {@link KaryotypeParser#modalNum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorrectModalNum(KaryotypeParser.CorrectModalNumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incorrectModalNum}
	 * labeled alternative in {@link KaryotypeParser#modalNum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectModalNum(KaryotypeParser.IncorrectModalNumContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#modalNumContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModalNumContent(KaryotypeParser.ModalNumContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#gender}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGender(KaryotypeParser.GenderContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#tooManyComma}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTooManyComma(KaryotypeParser.TooManyCommaContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#orSex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrSex(KaryotypeParser.OrSexContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#idemSpecial}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdemSpecial(KaryotypeParser.IdemSpecialContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#slSpecial}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSlSpecial(KaryotypeParser.SlSpecialContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#sdlSpecial}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSdlSpecial(KaryotypeParser.SdlSpecialContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#regularEvent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegularEvent(KaryotypeParser.RegularEventContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectComma}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectComma(KaryotypeParser.IncorrectCommaContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#idemEvent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdemEvent(KaryotypeParser.IdemEventContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#slEvent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSlEvent(KaryotypeParser.SlEventContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#slMark}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSlMark(KaryotypeParser.SlMarkContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#sdlEvent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSdlEvent(KaryotypeParser.SdlEventContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#sdlMark}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSdlMark(KaryotypeParser.SdlMarkContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multipleInterpretationRegularEvent}
	 * labeled alternative in {@link KaryotypeParser#regEvent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultipleInterpretationRegularEvent(KaryotypeParser.MultipleInterpretationRegularEventContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simpleRegularEvent}
	 * labeled alternative in {@link KaryotypeParser#regEvent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleRegularEvent(KaryotypeParser.SimpleRegularEventContext ctx);
	/**
	 * Visit a parse tree produced by the {@code undecodedSpecialEvent}
	 * labeled alternative in {@link KaryotypeParser#regEventType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUndecodedSpecialEvent(KaryotypeParser.UndecodedSpecialEventContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aberrationEvent}
	 * labeled alternative in {@link KaryotypeParser#regEventType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAberrationEvent(KaryotypeParser.AberrationEventContext ctx);
	/**
	 * Visit a parse tree produced by the {@code undecodedEvent}
	 * labeled alternative in {@link KaryotypeParser#regEventType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUndecodedEvent(KaryotypeParser.UndecodedEventContext ctx);
	/**
	 * Visit a parse tree produced by the {@code gainLossChrEvent}
	 * labeled alternative in {@link KaryotypeParser#regEventType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGainLossChrEvent(KaryotypeParser.GainLossChrEventContext ctx);
	/**
	 * Visit a parse tree produced by the {@code gainChrEvent}
	 * labeled alternative in {@link KaryotypeParser#gainLossChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGainChrEvent(KaryotypeParser.GainChrEventContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lossChrEvent}
	 * labeled alternative in {@link KaryotypeParser#gainLossChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLossChrEvent(KaryotypeParser.LossChrEventContext ctx);
	/**
	 * Visit a parse tree produced by the {@code gainLossChrError}
	 * labeled alternative in {@link KaryotypeParser#gainLossChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGainLossChrError(KaryotypeParser.GainLossChrErrorContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#undecoded}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUndecoded(KaryotypeParser.UndecodedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code derAberration}
	 * labeled alternative in {@link KaryotypeParser#aberration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDerAberration(KaryotypeParser.DerAberrationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code basicAberration}
	 * labeled alternative in {@link KaryotypeParser#aberration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasicAberration(KaryotypeParser.BasicAberrationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code uncertainBasicAberration}
	 * labeled alternative in {@link KaryotypeParser#aberration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUncertainBasicAberration(KaryotypeParser.UncertainBasicAberrationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code derAberrationError}
	 * labeled alternative in {@link KaryotypeParser#aberration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDerAberrationError(KaryotypeParser.DerAberrationErrorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code basicAberrationError}
	 * labeled alternative in {@link KaryotypeParser#aberration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasicAberrationError(KaryotypeParser.BasicAberrationErrorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code normalRearrangement}
	 * labeled alternative in {@link KaryotypeParser#rearrangementElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormalRearrangement(KaryotypeParser.NormalRearrangementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multipleInterpretationRearrangement}
	 * labeled alternative in {@link KaryotypeParser#rearrangementElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultipleInterpretationRearrangement(KaryotypeParser.MultipleInterpretationRearrangementContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#rearrangement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRearrangement(KaryotypeParser.RearrangementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code correctDerChrList}
	 * labeled alternative in {@link KaryotypeParser#derChrList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorrectDerChrList(KaryotypeParser.CorrectDerChrListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incorrectDerChrList}
	 * labeled alternative in {@link KaryotypeParser#derChrList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectDerChrList(KaryotypeParser.IncorrectDerChrListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#derChrListElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDerChrListElements(KaryotypeParser.DerChrListElementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectDerChrListElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectDerChrListElements(KaryotypeParser.IncorrectDerChrListElementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectLeftParenthesis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectLeftParenthesis(KaryotypeParser.IncorrectLeftParenthesisContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectRightParenthesis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectRightParenthesis(KaryotypeParser.IncorrectRightParenthesisContext ctx);
	/**
	 * Visit a parse tree produced by the {@code correctDerBreakpointsList}
	 * labeled alternative in {@link KaryotypeParser#derBreakpointsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorrectDerBreakpointsList(KaryotypeParser.CorrectDerBreakpointsListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incorrectDerBreakpointsList}
	 * labeled alternative in {@link KaryotypeParser#derBreakpointsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectDerBreakpointsList(KaryotypeParser.IncorrectDerBreakpointsListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#derBreakpointsListElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDerBreakpointsListElements(KaryotypeParser.DerBreakpointsListElementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectDerBreakpointsListElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectDerBreakpointsListElements(KaryotypeParser.IncorrectDerBreakpointsListElementsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code normalDerBreakpoints}
	 * labeled alternative in {@link KaryotypeParser#derBreakpointsInOneChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormalDerBreakpoints(KaryotypeParser.NormalDerBreakpointsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multipleInterpretationDerBreakpoints}
	 * labeled alternative in {@link KaryotypeParser#derBreakpointsInOneChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultipleInterpretationDerBreakpoints(KaryotypeParser.MultipleInterpretationDerBreakpointsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code correctChrList}
	 * labeled alternative in {@link KaryotypeParser#chrList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorrectChrList(KaryotypeParser.CorrectChrListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incorrectChrList}
	 * labeled alternative in {@link KaryotypeParser#chrList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectChrList(KaryotypeParser.IncorrectChrListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#chrListElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChrListElements(KaryotypeParser.ChrListElementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectChrListElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectChrListElements(KaryotypeParser.IncorrectChrListElementsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code correctBreakpointsList}
	 * labeled alternative in {@link KaryotypeParser#breakpointsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorrectBreakpointsList(KaryotypeParser.CorrectBreakpointsListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code detailedBreakpointsList}
	 * labeled alternative in {@link KaryotypeParser#breakpointsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetailedBreakpointsList(KaryotypeParser.DetailedBreakpointsListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incorrectBreakpointsList}
	 * labeled alternative in {@link KaryotypeParser#breakpointsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectBreakpointsList(KaryotypeParser.IncorrectBreakpointsListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#breakpointsListElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakpointsListElements(KaryotypeParser.BreakpointsListElementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectBreakpointsListElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectBreakpointsListElements(KaryotypeParser.IncorrectBreakpointsListElementsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code normalBreakpoints}
	 * labeled alternative in {@link KaryotypeParser#breakpointsInOneChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormalBreakpoints(KaryotypeParser.NormalBreakpointsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multipleInterpretationBreakpoints}
	 * labeled alternative in {@link KaryotypeParser#breakpointsInOneChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultipleInterpretationBreakpoints(KaryotypeParser.MultipleInterpretationBreakpointsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code correctRChrList}
	 * labeled alternative in {@link KaryotypeParser#rChrList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorrectRChrList(KaryotypeParser.CorrectRChrListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incorrectRChrList}
	 * labeled alternative in {@link KaryotypeParser#rChrList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectRChrList(KaryotypeParser.IncorrectRChrListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code correctRBreakpointsList}
	 * labeled alternative in {@link KaryotypeParser#rBreakpointsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorrectRBreakpointsList(KaryotypeParser.CorrectRBreakpointsListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code detailedRBreakpointsList}
	 * labeled alternative in {@link KaryotypeParser#rBreakpointsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetailedRBreakpointsList(KaryotypeParser.DetailedRBreakpointsListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incorrectRBreakpointsList}
	 * labeled alternative in {@link KaryotypeParser#rBreakpointsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectRBreakpointsList(KaryotypeParser.IncorrectRBreakpointsListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#idUncertainChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdUncertainChr(KaryotypeParser.IdUncertainChrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code correctUncertainChr}
	 * labeled alternative in {@link KaryotypeParser#uncertainChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorrectUncertainChr(KaryotypeParser.CorrectUncertainChrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incorrectUncertainChr}
	 * labeled alternative in {@link KaryotypeParser#uncertainChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectUncertainChr(KaryotypeParser.IncorrectUncertainChrContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#uncertainChrContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUncertainChrContent(KaryotypeParser.UncertainChrContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#arm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArm(KaryotypeParser.ArmContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#band}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBand(KaryotypeParser.BandContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#breakpoint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakpoint(KaryotypeParser.BreakpointContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#breakpoints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakpoints(KaryotypeParser.BreakpointsContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#c}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC(KaryotypeParser.CContext ctx);
	/**
	 * Visit a parse tree produced by the {@code correctCellNum}
	 * labeled alternative in {@link KaryotypeParser#cellNum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCorrectCellNum(KaryotypeParser.CorrectCellNumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code incorrectCellNum}
	 * labeled alternative in {@link KaryotypeParser#cellNum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectCellNum(KaryotypeParser.IncorrectCellNumContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#cellNumContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCellNumContent(KaryotypeParser.CellNumContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectLeftParenthesisII}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectLeftParenthesisII(KaryotypeParser.IncorrectLeftParenthesisIIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#incorrectRightParenthesisII}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncorrectRightParenthesisII(KaryotypeParser.IncorrectRightParenthesisIIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#cen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCen(KaryotypeParser.CenContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#chr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChr(KaryotypeParser.ChrContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#chrNum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChrNum(KaryotypeParser.ChrNumContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#cp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCp(KaryotypeParser.CpContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#derBreakpoints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDerBreakpoints(KaryotypeParser.DerBreakpointsContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#derChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDerChr(KaryotypeParser.DerChrContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#derId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDerId(KaryotypeParser.DerIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#detailedFormula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetailedFormula(KaryotypeParser.DetailedFormulaContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#detailedSegmentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetailedSegmentList(KaryotypeParser.DetailedSegmentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#detailedSegment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetailedSegment(KaryotypeParser.DetailedSegmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#detailedBreakpoint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetailedBreakpoint(KaryotypeParser.DetailedBreakpointContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#digit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigit(KaryotypeParser.DigitContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#dminNum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDminNum(KaryotypeParser.DminNumContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#gainChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGainChr(KaryotypeParser.GainChrContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(KaryotypeParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#inh}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInh(KaryotypeParser.InhContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#integer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger(KaryotypeParser.IntegerContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#lossChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLossChr(KaryotypeParser.LossChrContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#marNum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMarNum(KaryotypeParser.MarNumContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#modalDesc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModalDesc(KaryotypeParser.ModalDescContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#modalLevel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModalLevel(KaryotypeParser.ModalLevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#modalPrefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModalPrefix(KaryotypeParser.ModalPrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#modalSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModalSuffix(KaryotypeParser.ModalSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#mosChi}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMosChi(KaryotypeParser.MosChiContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#multiplication}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplication(KaryotypeParser.MultiplicationContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#numRangeTypeI}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumRangeTypeI(KaryotypeParser.NumRangeTypeIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#numRangeTypeII}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumRangeTypeII(KaryotypeParser.NumRangeTypeIIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#numRangeTypeIII}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumRangeTypeIII(KaryotypeParser.NumRangeTypeIIIContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#prefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefix(KaryotypeParser.PrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#prefixMinus}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixMinus(KaryotypeParser.PrefixMinusContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#prefixPlus}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixPlus(KaryotypeParser.PrefixPlusContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#rId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRId(KaryotypeParser.RIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#sex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSex(KaryotypeParser.SexContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#sexChr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSexChr(KaryotypeParser.SexChrContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#suffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuffix(KaryotypeParser.SuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#subband}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubband(KaryotypeParser.SubbandContext ctx);
	/**
	 * Visit a parse tree produced by {@link KaryotypeParser#undeterminedPrefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUndeterminedPrefix(KaryotypeParser.UndeterminedPrefixContext ctx);
}