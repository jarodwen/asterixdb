package edu.uci.ics.asterix.algebra.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import edu.uci.ics.hyracks.algebricks.core.algebra.base.Counter;
import edu.uci.ics.hyracks.algebricks.core.algebra.base.ILogicalExpression;
import edu.uci.ics.hyracks.algebricks.core.algebra.base.LogicalVariable;
import edu.uci.ics.hyracks.algebricks.core.algebra.expressions.AbstractFunctionCallExpression;
import edu.uci.ics.hyracks.algebricks.core.algebra.expressions.AggregateFunctionCallExpression;
import edu.uci.ics.hyracks.algebricks.core.algebra.expressions.ConstantExpression;
import edu.uci.ics.hyracks.algebricks.core.algebra.expressions.IExpressionAnnotation;
import edu.uci.ics.hyracks.algebricks.core.algebra.expressions.ScalarFunctionCallExpression;
import edu.uci.ics.hyracks.algebricks.core.algebra.expressions.StatefulFunctionCallExpression;
import edu.uci.ics.hyracks.algebricks.core.algebra.expressions.UnnestingFunctionCallExpression;
import edu.uci.ics.hyracks.algebricks.core.algebra.expressions.VariableReferenceExpression;
import edu.uci.ics.hyracks.algebricks.core.algebra.visitors.ILogicalExpressionVisitor;
import edu.uci.ics.hyracks.algebricks.core.api.exceptions.AlgebricksException;

public class LogicalExpressionDeepCopyVisitor implements ILogicalExpressionVisitor<ILogicalExpression, Void> {
    private final Counter counter;
    private final HashMap<LogicalVariable, LogicalVariable> variableMapping;

    public LogicalExpressionDeepCopyVisitor(Counter counter, HashMap<LogicalVariable, LogicalVariable> variableMapping) {
        this.counter = counter;
        this.variableMapping = variableMapping;
    }

    public ILogicalExpression deepCopy(ILogicalExpression expr) throws AlgebricksException {
        return expr.accept(this, null);
    }

    private void deepCopyAnnotations(AbstractFunctionCallExpression src, AbstractFunctionCallExpression dest) {
        Map<Object, IExpressionAnnotation> srcAnnotations = src.getAnnotations();
        Map<Object, IExpressionAnnotation> destAnnotations = dest.getAnnotations();
        for (Object k : srcAnnotations.keySet()) {
            IExpressionAnnotation annotation = srcAnnotations.get(k).copy();
            destAnnotations.put(k, annotation);
        }
    }

    public MutableObject<ILogicalExpression> deepCopyExpressionReference(Mutable<ILogicalExpression> exprRef)
            throws AlgebricksException {
        return new MutableObject<ILogicalExpression>(deepCopy(exprRef.getValue()));
    }

    // TODO return List<...>
    public ArrayList<Mutable<ILogicalExpression>> deepCopyExpressionReferenceList(List<Mutable<ILogicalExpression>> list)
            throws AlgebricksException {
        ArrayList<Mutable<ILogicalExpression>> listCopy = new ArrayList<Mutable<ILogicalExpression>>(list.size());
        for (Mutable<ILogicalExpression> exprRef : list) {
            listCopy.add(deepCopyExpressionReference(exprRef));
        }
        return listCopy;
    }

    @Override
    public ILogicalExpression visitAggregateFunctionCallExpression(AggregateFunctionCallExpression expr, Void arg)
            throws AlgebricksException {
        AggregateFunctionCallExpression exprCopy = new AggregateFunctionCallExpression(expr.getFunctionInfo(),
                expr.isTwoStep(), deepCopyExpressionReferenceList(expr.getArguments()));
        deepCopyAnnotations(expr, exprCopy);
        return exprCopy;
    }

    @Override
    public ILogicalExpression visitConstantExpression(ConstantExpression expr, Void arg) throws AlgebricksException {
        return new ConstantExpression(expr.getValue());
    }

    @Override
    public ILogicalExpression visitScalarFunctionCallExpression(ScalarFunctionCallExpression expr, Void arg)
            throws AlgebricksException {
        ScalarFunctionCallExpression exprCopy = new ScalarFunctionCallExpression(expr.getFunctionInfo(),
                deepCopyExpressionReferenceList(expr.getArguments()));
        deepCopyAnnotations(expr, exprCopy);
        return exprCopy;

    }

    @Override
    public ILogicalExpression visitStatefulFunctionCallExpression(StatefulFunctionCallExpression expr, Void arg)
            throws AlgebricksException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILogicalExpression visitUnnestingFunctionCallExpression(UnnestingFunctionCallExpression expr, Void arg)
            throws AlgebricksException {
        UnnestingFunctionCallExpression exprCopy = new UnnestingFunctionCallExpression(expr.getFunctionInfo(),
                deepCopyExpressionReferenceList(expr.getArguments()));
        deepCopyAnnotations(expr, exprCopy);
        return exprCopy;
    }

    @Override
    public ILogicalExpression visitVariableReferenceExpression(VariableReferenceExpression expr, Void arg)
            throws AlgebricksException {
        LogicalVariable var = expr.getVariableReference();
        LogicalVariable varCopy = variableMapping.get(var);
        if (varCopy == null) {
            counter.inc();
            varCopy = new LogicalVariable(counter.get());
            variableMapping.put(var, varCopy);
        }
        return new VariableReferenceExpression(varCopy);
    }
}