package edu.uci.ics.asterix.runtime.evaluators.constructors;

import java.io.DataOutput;
import java.io.IOException;

import edu.uci.ics.asterix.common.functions.FunctionConstants;
import edu.uci.ics.asterix.formats.nontagged.AqlBinaryComparatorFactoryProvider;
import edu.uci.ics.asterix.formats.nontagged.AqlSerializerDeserializerProvider;
import edu.uci.ics.asterix.om.base.AFloat;
import edu.uci.ics.asterix.om.base.AMutableFloat;
import edu.uci.ics.asterix.om.base.ANull;
import edu.uci.ics.asterix.om.types.ATypeTag;
import edu.uci.ics.asterix.om.types.BuiltinType;
import edu.uci.ics.asterix.runtime.evaluators.base.AbstractScalarFunctionDynamicDescriptor;
import edu.uci.ics.hyracks.algebricks.core.algebra.functions.FunctionIdentifier;
import edu.uci.ics.hyracks.algebricks.core.algebra.runtime.base.IEvaluator;
import edu.uci.ics.hyracks.algebricks.core.algebra.runtime.base.IEvaluatorFactory;
import edu.uci.ics.hyracks.algebricks.core.api.exceptions.AlgebricksException;
import edu.uci.ics.hyracks.api.dataflow.value.IBinaryComparator;
import edu.uci.ics.hyracks.api.dataflow.value.ISerializerDeserializer;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.ArrayBackedValueStorage;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.IDataOutputProvider;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.IFrameTupleReference;

public class AFloatConstructorDescriptor extends AbstractScalarFunctionDynamicDescriptor {

    private static final long serialVersionUID = 1L;
    public final static FunctionIdentifier FID = new FunctionIdentifier(FunctionConstants.ASTERIX_NS, "float", 1, false);
    private final static byte SER_STRING_TYPE_TAG = ATypeTag.STRING.serialize();
    private final static byte SER_NULL_TYPE_TAG = ATypeTag.NULL.serialize();

    @Override
    public IEvaluatorFactory createEvaluatorFactory(final IEvaluatorFactory[] args) {
        return new IEvaluatorFactory() {
            private static final long serialVersionUID = 1L;

            @Override
            public IEvaluator createEvaluator(final IDataOutputProvider output) throws AlgebricksException {
                return new IEvaluator() {

                    private DataOutput out = output.getDataOutput();
                    private ArrayBackedValueStorage outInput = new ArrayBackedValueStorage();
                    private IEvaluator eval = args[0].createEvaluator(outInput);
                    private String errorMessage = "This can not be an instance of float";
                    private final byte[] POSITIVE_INF = { 0, 3, 'I', 'N', 'F' };
                    private final byte[] NEGATIVE_INF = { 0, 4, '-', 'I', 'N', 'F' };
                    private final byte[] NAN = { 0, 3, 'N', 'a', 'N' };
                    // private int offset = 3, value = 0, pointIndex = 0, eIndex
                    // = 1;
                    // private int integerPart = 0, fractionPart = 0,
                    // exponentPart = 0;
                    // float floatValue = 0;
                    // boolean positiveInteger = true, positiveExponent = true,
                    // expectingInteger = true,
                    // expectingFraction = false, expectingExponent = false;
                    IBinaryComparator utf8BinaryComparator = AqlBinaryComparatorFactoryProvider.UTF8STRING_POINTABLE_INSTANCE
                            .createBinaryComparator();
                    private AMutableFloat aFloat = new AMutableFloat(0);
                    @SuppressWarnings("unchecked")
                    private ISerializerDeserializer<AFloat> floatSerde = AqlSerializerDeserializerProvider.INSTANCE
                            .getSerializerDeserializer(BuiltinType.AFLOAT);
                    @SuppressWarnings("unchecked")
                    private ISerializerDeserializer<ANull> nullSerde = AqlSerializerDeserializerProvider.INSTANCE
                            .getSerializerDeserializer(BuiltinType.ANULL);

                    @Override
                    public void evaluate(IFrameTupleReference tuple) throws AlgebricksException {

                        try {
                            outInput.reset();
                            eval.evaluate(tuple);
                            byte[] serString = outInput.getBytes();
                            if (serString[0] == SER_STRING_TYPE_TAG) {
                                if (utf8BinaryComparator
                                        .compare(serString, 1, outInput.getLength(), POSITIVE_INF, 0, 5) == 0) {
                                    aFloat.setValue(Float.POSITIVE_INFINITY);
                                } else if (utf8BinaryComparator.compare(serString, 1, outInput.getLength(),
                                        NEGATIVE_INF, 0, 6) == 0) {
                                    aFloat.setValue(Float.NEGATIVE_INFINITY);
                                } else if (utf8BinaryComparator.compare(serString, 1, outInput.getLength(), NAN, 0, 5) == 0) {
                                    aFloat.setValue(Float.NaN);
                                } else
                                    aFloat.setValue(Float.parseFloat(new String(serString, 3, outInput.getLength() - 3,
                                            "UTF-8")));
                                floatSerde.serialize(aFloat, out);

                            } else if (serString[0] == SER_NULL_TYPE_TAG)
                                nullSerde.serialize(ANull.NULL, out);
                            else
                                throw new AlgebricksException(errorMessage);
                        } catch (IOException e1) {
                            throw new AlgebricksException(errorMessage);
                        }
                    }

                    // private float parseFloat(byte[] serString) throws
                    // AlgebricksException {
                    //
                    // if (serString[offset] == '+')
                    // offset++;
                    // else if (serString[offset] == '-') {
                    // offset++;
                    // positiveInteger = false;
                    // }
                    //
                    // if ((serString[offset] == '.') || (serString[offset] ==
                    // 'e') || (serString[offset] == 'E')
                    // || (serString[outInput.getLength() - 1] == '.')
                    // || (serString[outInput.getLength() - 1] == 'E')
                    // || (serString[outInput.getLength() - 1] == 'e'))
                    // throw new AlgebricksException(errorMessage);
                    //
                    // for (; offset < outInput.getLength(); offset++) {
                    // if (serString[offset] >= '0' && serString[offset] <= '9')
                    // {
                    // value = value * 10 + serString[offset] - '0';
                    // } else
                    // switch (serString[offset]) {
                    // case '.':
                    // if (expectingInteger) {
                    // if (serString[offset + 1] < '0' || serString[offset + 1]
                    // > '9')
                    // throw new AlgebricksException(errorMessage);
                    // expectingInteger = false;
                    // expectingFraction = true;
                    // integerPart = value;
                    // value = 0;
                    // pointIndex = offset;
                    // eIndex = outInput.getLength();
                    // } else
                    // throw new AlgebricksException(errorMessage);
                    // break;
                    // case 'e':
                    // case 'E':
                    // if (expectingInteger) {
                    // expectingInteger = false;
                    // integerPart = value;
                    // pointIndex = offset - 1;
                    // eIndex = offset;
                    // value = 0;
                    // expectingExponent = true;
                    // } else if (expectingFraction) {
                    //
                    // expectingFraction = false;
                    // fractionPart = value;
                    // eIndex = offset;
                    // value = 0;
                    // expectingExponent = true;
                    // } else
                    // throw new AlgebricksException();
                    //
                    // if (serString[offset + 1] == '+')
                    // offset++;
                    // else if (serString[offset + 1] == '-') {
                    // offset++;
                    // positiveExponent = false;
                    // } else if (serString[offset + 1] < '0' ||
                    // serString[offset + 1] > '9')
                    // throw new AlgebricksException(errorMessage);
                    // break;
                    // default:
                    // throw new AlgebricksException(errorMessage);
                    // }
                    // }
                    //
                    // if (expectingInteger)
                    // integerPart = value;
                    // else if (expectingFraction)
                    // fractionPart = value;
                    // else if (expectingExponent)
                    // exponentPart = value * (positiveExponent ? 1 : -1);
                    //
                    // // floatValue = (float) ( integerPart + ( fractionPart *
                    // (1.0f / Math.pow(10.0f, eIndex - pointIndex - 1))));
                    // // floatValue *= (float) Math.pow(10.0f, exponentPart);
                    //
                    // floatValue = Float.parseFloat(integerPart+"."+
                    // fractionPart+"e"+ exponentPart);
                    //
                    // if (integerPart != 0
                    // && (floatValue == Float.POSITIVE_INFINITY || floatValue
                    // == Float.NEGATIVE_INFINITY || floatValue == 0))
                    // throw new AlgebricksException(errorMessage);
                    //
                    // if (floatValue > 0 && !positiveInteger)
                    // floatValue *= -1;
                    //
                    // return floatValue;
                    // }
                };
            }
        };
    }

    @Override
    public FunctionIdentifier getIdentifier() {
        return FID;
    }
}