package edu.sjsu.fwjs;
import java.util.ArrayList;
import java.util.List;

public class Interpreter {

    public static void main(String[] args) throws Exception {
        //Expression prog = new BinOpExpr(Op.ADD, new ValueExpr(new IntVal(3)), new ValueExpr(new IntVal(4)));


        // x=112358; (function() { x=42; x; })(); x;
        Environment env = new Environment();
        // SeqExpr seq = new SeqExpr(
        //     new SeqExpr(
        //         new VarDeclExpr("x", new ValueExpr(new IntVal(112358))),
        //         new FunctionAppExpr(
        //             new FunctionDeclExpr(
        //                 new ArrayList<String>(),
        //                 new SeqExpr(
        //                     new AssignExpr("x", new ValueExpr(new IntVal(42))),
        //                     new VarExpr("x")
        //                 )
        //             ), new ArrayList<Expression>()
        //         )
        //     ),
        //     new VarExpr("x")
        // );
        // Value v = seq.evaluate(env);
        Value z = (new VarDeclExpr("x", new ValueExpr(new IntVal(112358)))).evaluate(env);
        System.out.println(z.toString());
        Value v = (new FunctionAppExpr(
                    new FunctionDeclExpr(
                        new ArrayList<String>(),
                        new SeqExpr(
                            new AssignExpr("x", new ValueExpr(new IntVal(42))),
                            new VarExpr("x")
                        )
                    ), new ArrayList<Expression>()
                )).evaluate(env);
        System.out.println(v.toString());
        System.out.println(env.toString());
        //System.out.println("'3 + 4;' evaluates to " + prog.evaluate(new Environment()));
    }
}
