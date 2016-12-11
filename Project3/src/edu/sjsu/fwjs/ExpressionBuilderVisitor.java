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
    
    // @Override
    // public Expression visitEmpty(FeatherweightJavaScriptParser.EmptyContext ctx){
    //     return null;
    // }

    @Override
    public Expression visitWhile(FeatherweightJavaScriptParser.WhileContext ctx){
        Expression cond = visit(ctx.expr());
        Expression body = visit(ctx.block());
        return new WhileExpr(cond, body);
    }
    
    @Override
    public Expression visitPrintExpr(FeatherweightJavaScriptParser.PrintExprContext ctx){
    	Expression expr = visit(ctx.expr());
    	return new PrintExpr(expr);
    }
    
    @Override
    public Expression visitMulDivMod(FeatherweightJavaScriptParser.MulDivModContext ctx){
        Expression lhs = visit(ctx.expr(0));
        Expression rhs = visit(ctx.expr(1));
        return BinOpHelper(ctx.op.getType(), lhs, rhs);
    }
    
    @Override
    public Expression visitAddSub(FeatherweightJavaScriptParser.AddSubContext ctx){
    	Expression lhs = visit(ctx.expr(0));
        Expression rhs = visit(ctx.expr(1));
        return BinOpHelper(ctx.op.getType(), lhs, rhs);
    }
    
    @Override
    public Expression visitCompare(FeatherweightJavaScriptParser.CompareContext ctx){
    	Expression lhs = visit(ctx.expr(0));
        Expression rhs = visit(ctx.expr(1));
        return BinOpHelper(ctx.op.getType(), lhs, rhs);
    }
    
    public Expression BinOpHelper(int ctxOp, Expression lhs, Expression rhs){
        Op op;
        switch(ctxOp) {
                case FeatherweightJavaScriptParser.ADD:
                        op = Op.ADD;
                        break;
                case FeatherweightJavaScriptParser.SUB:
                        op = Op.SUBTRACT;
                        break;
                case FeatherweightJavaScriptParser.MUL:
                        op = Op.MULTIPLY;
                        break;
                case FeatherweightJavaScriptParser.DIV:
                        op = Op.DIVIDE;
                        break;
                case FeatherweightJavaScriptParser.MOD:
                        op = Op.MOD;
                        break;
                case FeatherweightJavaScriptParser.LT:
                        op = Op.LT;
                        break;
                case FeatherweightJavaScriptParser.LE:
                        op = Op.LE;
                        break;
                case FeatherweightJavaScriptParser.GT:
                        op = Op.GT;
                        break;
                case FeatherweightJavaScriptParser.GE:
                        op = Op.GE;
                        break;
                case FeatherweightJavaScriptParser.EQ:
                        op = Op.EQ;
                        break;
                default: op= null;
                         break;
        }
        return new BinOpExpr(op, lhs, rhs);
    }
    

    // @Override
    // public Expression visitFunctionDeclrExpr(FeatherweightJavaScriptParser.FunctionDeclExprContext ctx){
    //     List<String> params = new List<String>();
    // 	Expression body = visit(ctx.body());
    	
    // 	return new FunctionDeclrExpr(params, body);
    // }
    
    
    // private List<String> params;
    // private Expression body;
    // public FunctionDeclExpr(List<String> params, Expression body) {
    //     this.params = params;
    //     this.body = body;
    // }

    
    @Override
    public Expression visitVarDeclrExpr(FeatherweightJavaScriptParser.VarDeclrExprContext ctx){
    	Expression expr = visit(ctx.expr());
    	String varName = ctx.ID().getText();
    	return new VarDeclrExpr(varName, expr);
    }
    
    @Override
    public Expression visitFuncApp(FeatherweightJavaScriptParser.FuncAppContext ctx){
        Expression f = visit(ctx.expr());
        List<Expression> args = new ArrayList<Expression>();
        
        for(int i = 1; i < ctx.getChildCount()-1; i++)
        {
            Expression exp = visit(ctx.getChild(i));
            args.add(exp);
        }
 
        return listToSeqExp(args);    
    }
    
    @Override
    public Expression visitVarRef(FeatherweightJavaScriptParser.VarRefContext ctx){
    	//HALP
    }
    
    @Override 
    public Expression visitAssignExpr(FeatherweightJavaScriptParser.AssignExprContext ctx){
    	Expression expr = visit(ctx.expr());
    	String varName = ctx.ID().getText(); //String? ctx.getText()?
    	return new AssignExpr(varName, expr);
    }
    
    @Override
    public Expression visitBool(FeatherweightJavaScriptParser.BoolContext ctx){
        boolean val = Boolean.valueOf(ctx.BOOL().getText());
        return new ValueExpr(new BoolVal(val));
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