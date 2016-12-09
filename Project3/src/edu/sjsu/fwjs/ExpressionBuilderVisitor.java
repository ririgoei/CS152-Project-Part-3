package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.fwjs.parser.FeatherweightJavaScriptBaseVisitor;
import edu.sjsu.fwjs.parser.FeatherweightJavaScriptParser;

public class ExpressionBuilderVisitor extends FeatherweightJavaScriptBaseVisitor<Expression>{
    @Override
    public Expression visitProg(FeatherweightJavaScriptParser.ProgContext ctx) {
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i=0; i<ctx.stat().size(); i++) {
            Expression exp = visit(ctx.stat(i));
            if (exp != null) stmts.add(exp);
        }
        return listToSeqExp(stmts);
    }

    @Override
    public Expression visitBareExpr(FeatherweightJavaScriptParser.BareExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Expression visitIfThenElse(FeatherweightJavaScriptParser.IfThenElseContext ctx) {
        Expression cond = visit(ctx.expr());
        Expression thn = visit(ctx.block(0));
        Expression els = visit(ctx.block(1));
        return new IfExpr(cond, thn, els);
    }

    @Override
    public Expression visitIfThen(FeatherweightJavaScriptParser.IfThenContext ctx) {
        Expression cond = visit(ctx.expr());
        Expression thn = visit(ctx.block());
        return new IfExpr(cond, thn, null);
    }

    @Override
    public Expression visitInt(FeatherweightJavaScriptParser.IntContext ctx) {
        int val = Integer.valueOf(ctx.INT().getText());
        return new ValueExpr(new IntVal(val));
    }

    @Override
    public Expression visitParens(FeatherweightJavaScriptParser.ParensContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Expression visitFullBlock(FeatherweightJavaScriptParser.FullBlockContext ctx) {
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i=1; i<ctx.getChildCount()-1; i++) {
            Expression exp = visit(ctx.getChild(i));
            stmts.add(exp);
        }
        return listToSeqExp(stmts);
    }

    @Override
    public Expression visitWhile(FeatherweightJavaScriptParser.WhileContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitPrint(FeatherweightJavaScriptParser.PrintContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitMulDivMod(FeatherweightJavaScriptParser.MulDivModContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitAddSub(FeatherweightJavaScriptParser.AddSubContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitCompare(FeatherweightJavaScriptParser.CompareContext ctx){
    	//HALP
    }

    @Override
    public Expression visitFuncDec(FeatherweightJavaScriptParser.FuncDecContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitVarDec(FeatherweightJavaScriptParser.VarDecContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitFuncApp(FeatherweightJavaScriptParser.FuncAppContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitVarRef(FeatherweightJavaScriptParser.VarRefContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitAssign(FeatherweightJavaScriptParser.AssignContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitBool(FeatherweightJavaScriptParser.BoolContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitNull(FeatherweightJavaScriptParser.NullContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitWithParam(FeatherweightJavaScriptParser.WithParamContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitEmptyParam(FeatherweightJavaScriptParser.EmptyParamContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitWithArg(FeatherweightJavaScriptParser.WithArgContext ctx){
    	//HALP
    }
    
    @Override
    public Expression visitEmptyArg(FeatherweightJavaScriptParser.EmptyArgContext ctx){
    	//HALP
    }
    
    /**
     * Converts a list of expressions to one sequence expression,
     * if the list contained more than one expression.
     */
    private Expression listToSeqExp(List<Expression> stmts) {
        if (stmts.isEmpty()) return null;
        Expression exp = stmts.get(0);
        for (int i=1; i<stmts.size(); i++) {
            exp = new SeqExpr(exp, stmts.get(i));
        }
        return exp;
    }

    @Override
    public Expression visitSimpBlock(FeatherweightJavaScriptParser.SimpBlockContext ctx) {
        return visit(ctx.stat());
    }
}