package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;

/**
 * FWJS expressions.
 */
public interface Expression {
    /**
     * Evaluate the expression in the context of the specified environment.
     */
    public Value evaluate(Environment env);
}

// NOTE: Using package access so that all implementations of Expression
// can be included in the same file.

/**
 * FWJS constants.
 */
class ValueExpr implements Expression {
    private Value val;
    public ValueExpr(Value v) {
        this.val = v;
    }
    public Value evaluate(Environment env) {
        return this.val;
    }
}

/**
 * Expressions that are a FWJS variable.
 */
class VarExpr implements Expression {
    private String varName;
    public VarExpr(String varName) {
        this.varName = varName;
    }
    public Value evaluate(Environment env) {
        return env.resolveVar(varName);
    }
}

/**
 * A print expression.
 */
class PrintExpr implements Expression {
    private Expression exp;
    public PrintExpr(Expression exp) {
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        Value v = exp.evaluate(env);
        System.out.println(v.toString());
        return v;
    }
}
/**
 * Binary operators (+, -, *, etc).
 * Currently only numbers are supported.
 */
class BinOpExpr implements Expression {
    private Op op;
    private Expression e1;
    private Expression e2;
    public BinOpExpr(Op op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @SuppressWarnings("incomplete-switch")
    public Value evaluate(Environment env) {
        Value a = e1.evaluate(env);
        Value b = e2.evaluate(env);
        if (!((a instanceof IntVal) && (b instanceof IntVal)))
            throw new RuntimeException();
        int n1 = ((IntVal) a).toInt();
        int n2 = ((IntVal) b).toInt();
        switch (op) {
            case ADD:
                return new IntVal(n1 + n2);
            case SUBTRACT:
                return new IntVal(n1 - n2);
            case MULTIPLY:
                return new IntVal(n1 * n2);
            case DIVIDE:
                return new IntVal(n1 / n2);
            case MOD:
                return new IntVal(n1 % n2);
            case GT:
                return new BoolVal(n1 > n2);
            case GE:
                return new BoolVal(n1 >= n2);
            case LT:
                return new BoolVal(n1 < n2);
            case LE:
                return new BoolVal(n1 <= n2);
            case EQ:
                return new BoolVal(a.equals(b));
            default:
                return new NullVal();
        }
    }
}

/**
 * If-then-else expressions.
 * Unlike JS, if expressions return a value.
 */
class IfExpr implements Expression {
    private Expression cond;
    private Expression thn;
    private Expression els;
    public IfExpr(Expression cond, Expression thn, Expression els) {
        this.cond = cond;
        this.thn = thn;
        this.els = els;
    }
    public Value evaluate(Environment env) {
        Value a = cond.evaluate(env);
        if (!(a instanceof BoolVal) || (!((((BoolVal) a).toBoolean() == true) || (((BoolVal) a).toBoolean() == false))))
            throw new RuntimeException();
        return ((BoolVal) a).toBoolean() ? thn.evaluate(env) : els.evaluate(env);
    }
}

/**
 * While statements (treated as expressions in FWJS, unlike JS).
 */
class WhileExpr implements Expression {
    private Expression cond;
    private Expression body;
    public WhileExpr(Expression cond, Expression body) {
        this.cond = cond;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        Value a = cond.evaluate(env);
        if (!(a instanceof BoolVal) || (!((((BoolVal) a).toBoolean() == true) || (((BoolVal) a).toBoolean() == false))))
            throw new RuntimeException();
        if(((BoolVal) a).toBoolean()) {
            body.evaluate(env);
            return (new WhileExpr(cond, body)).evaluate(env);
        }
        return new NullVal();
    }
}

/**
 * Sequence expressions (i.e. 2 back-to-back expressions).
 */
class SeqExpr implements Expression {
    private Expression e1;
    private Expression e2;
    public SeqExpr(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    public Value evaluate(Environment env) {
        Value a = e1.evaluate(env);
        Value b = e2.evaluate(env);
        if(b == null || (new NullVal()).equals(b))
            return a;
        return b;
    }
}

/**
 * Declaring a variable in the local scope.
 */
class VarDeclExpr implements Expression {
    private String varName;
    private Expression exp;
    public VarDeclExpr(String varName, Expression exp) {
        this.varName = varName;
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        Value a = exp.evaluate(env);
        try { env.createVar(varName, a); }
        catch (Exception e) { throw new RuntimeException(); }
        return a;
    }
}

/**
 * Updating an existing variable.
 * If the variable is not set already, it is added
 * to the global scope.
 */
class AssignExpr implements Expression {
    private String varName;
    private Expression e;
    public AssignExpr(String varName, Expression e) {
        this.varName = varName;
        this.e = e;
    }
    public Value evaluate(Environment env) {
        Value a = e.evaluate(env);
        env.updateVar(varName, a);
        return a;
    }
}

/**
 * A function declaration, which evaluates to a closure.
 */
class FunctionDeclExpr implements Expression {
    private List<String> params;
    private Expression body;
    public FunctionDeclExpr(List<String> params, Expression body) {
        this.params = params;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        return new ClosureVal(params, body, env);
    }
}

/**
 * Function application.
 */
class FunctionAppExpr implements Expression {
    private Expression f;
    private List<Expression> args;
    public FunctionAppExpr(Expression f, List<Expression> args) {
        this.f = f;
        this.args = args;
    }
    public Value evaluate(Environment env) {
        Value a = f.evaluate(env);
        if (!(a instanceof ClosureVal) || a == null)
            throw new RuntimeException();
        Value b = new NullVal();
        List<Value> valueList = new ArrayList<Value>();
        for(int i = 0; i < args.size(); i++)
            valueList.add((args.get(i)).evaluate(env));
        try {
            b = ((ClosureVal) a).apply(valueList);
        } catch(Exception e) { throw new RuntimeException(); }
        return b;
    }
}