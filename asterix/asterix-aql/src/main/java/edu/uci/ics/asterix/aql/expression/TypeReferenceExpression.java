package edu.uci.ics.asterix.aql.expression;

import edu.uci.ics.asterix.aql.expression.visitor.IAqlExpressionVisitor;
import edu.uci.ics.asterix.aql.expression.visitor.IAqlVisitorWithVoidReturn;
import edu.uci.ics.asterix.common.exceptions.AsterixException;

public class TypeReferenceExpression extends TypeExpression {

    private final Identifier ident;

    public TypeReferenceExpression(Identifier ident) {
        this.ident = ident;
    }

    public Identifier getIdent() {
        return ident;
    }

    @Override
    public TypeExprKind getTypeKind() {
        return TypeExprKind.TYPEREFERENCE;
    }

    @Override
    public <R, T> R accept(IAqlExpressionVisitor<R, T> visitor, T arg) throws AsterixException {
        return visitor.visitTypeReferenceExpression(this, arg);
    }

    @Override
    public <T> void accept(IAqlVisitorWithVoidReturn<T> visitor, T arg) throws AsterixException {
        visitor.visit(this, arg);
    }

}
