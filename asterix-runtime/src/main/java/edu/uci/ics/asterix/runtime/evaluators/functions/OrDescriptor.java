package edu.uci.ics.asterix.runtime.evaluators.functions;

import edu.uci.ics.asterix.dataflow.data.nontagged.serde.ABooleanSerializerDeserializer;
import edu.uci.ics.asterix.formats.nontagged.AqlSerializerDeserializerProvider;
import edu.uci.ics.asterix.om.base.ABoolean;
import edu.uci.ics.asterix.om.base.ANull;
import edu.uci.ics.asterix.om.types.ATypeTag;
import edu.uci.ics.asterix.om.types.BuiltinType;
import edu.uci.ics.asterix.runtime.evaluators.base.AbstractScalarFunctionDynamicDescriptor;
import edu.uci.ics.hyracks.algebricks.core.algebra.functions.AlgebricksBuiltinFunctions;
import edu.uci.ics.hyracks.algebricks.core.algebra.functions.FunctionIdentifier;
import edu.uci.ics.hyracks.algebricks.core.algebra.runtime.base.IEvaluator;
import edu.uci.ics.hyracks.algebricks.core.algebra.runtime.base.IEvaluatorFactory;
import edu.uci.ics.hyracks.algebricks.core.api.exceptions.AlgebricksException;
import edu.uci.ics.hyracks.api.dataflow.value.ISerializerDeserializer;
import edu.uci.ics.hyracks.api.exceptions.HyracksDataException;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.ArrayBackedValueStorage;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.IDataOutputProvider;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.IFrameTupleReference;

public class OrDescriptor extends AbstractScalarFunctionDynamicDescriptor {

    private static final long serialVersionUID = 1L;
    public final static FunctionIdentifier FID = new FunctionIdentifier(AlgebricksBuiltinFunctions.ALGEBRICKS_NS, "or",
            FunctionIdentifier.VARARGS, true);
    private final static byte SER_NULL_TYPE_TAG = ATypeTag.NULL.serialize();

    @Override
    public FunctionIdentifier getIdentifier() {
        return FID;
    }

    @Override
    public IEvaluatorFactory createEvaluatorFactory(final IEvaluatorFactory[] args) throws AlgebricksException {

        return new IEvaluatorFactory() {
            private static final long serialVersionUID = 1L;

            @Override
            public IEvaluator createEvaluator(final IDataOutputProvider output) throws AlgebricksException {
                // one temp. buffer re-used by all children
                final ArrayBackedValueStorage argOut = new ArrayBackedValueStorage();
                final IEvaluator[] evals = new IEvaluator[args.length];
                for (int i = 0; i < evals.length; i++) {
                    evals[i] = args[i].createEvaluator(argOut);
                }

                return new IEvaluator() {
                    @SuppressWarnings("unchecked")
                    private ISerializerDeserializer<ABoolean> booleanSerde = AqlSerializerDeserializerProvider.INSTANCE
                            .getSerializerDeserializer(BuiltinType.ABOOLEAN);
                    @SuppressWarnings("unchecked")
                    private ISerializerDeserializer<ANull> nullSerde = AqlSerializerDeserializerProvider.INSTANCE
                            .getSerializerDeserializer(BuiltinType.ANULL);

                    @Override
                    public void evaluate(IFrameTupleReference tuple) throws AlgebricksException {
                        try {
                            int n = args.length;
                            boolean res = false;
                            boolean metNull = false;
                            for (int i = 0; i < n; i++) {
                                argOut.reset();
                                evals[i].evaluate(tuple);
                                if (argOut.getBytes()[0] == SER_NULL_TYPE_TAG) {
                                    metNull = true;
                                    continue;
                                }
                                boolean argResult = ABooleanSerializerDeserializer.getBoolean(argOut.getBytes(), 1);
                                if (argResult == true) {
                                    res = true;
                                    break;
                                }
                            }
                            if (metNull) {
                                if (!res) {
                                    ABoolean aResult = ABoolean.FALSE;
                                    booleanSerde.serialize(aResult, output.getDataOutput());
                                } else
                                    nullSerde.serialize(ANull.NULL, output.getDataOutput());
                            } else {
                                ABoolean aResult = res ? (ABoolean.TRUE) : (ABoolean.FALSE);
                                booleanSerde.serialize(aResult, output.getDataOutput());
                            }
                        } catch (HyracksDataException hde) {
                            throw new AlgebricksException(hde);
                        }
                    }
                };
            }
        };
    }

}