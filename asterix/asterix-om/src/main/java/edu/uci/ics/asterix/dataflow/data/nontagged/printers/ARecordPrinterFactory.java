package edu.uci.ics.asterix.dataflow.data.nontagged.printers;

import java.io.PrintStream;

import edu.uci.ics.asterix.om.pointables.PointableAllocator;
import edu.uci.ics.asterix.om.pointables.base.DefaultOpenFieldType;
import edu.uci.ics.asterix.om.pointables.base.IVisitablePointable;
import edu.uci.ics.asterix.om.pointables.printer.APrintVisitor;
import edu.uci.ics.asterix.om.types.ARecordType;
import edu.uci.ics.asterix.om.types.ATypeTag;
import edu.uci.ics.asterix.om.types.IAType;
import edu.uci.ics.hyracks.algebricks.common.exceptions.AlgebricksException;
import edu.uci.ics.hyracks.algebricks.common.utils.Pair;
import edu.uci.ics.hyracks.algebricks.data.IPrinter;
import edu.uci.ics.hyracks.algebricks.data.IPrinterFactory;

public class ARecordPrinterFactory implements IPrinterFactory {

    private static final long serialVersionUID = 1L;
    private final ARecordType recType;

    public ARecordPrinterFactory(ARecordType recType) {
        this.recType = recType;
    }

    @Override
    public IPrinter createPrinter() {

        PointableAllocator allocator = new PointableAllocator();
        final IAType inputType = recType == null ? DefaultOpenFieldType.getDefaultOpenFieldType(ATypeTag.RECORD)
                : recType;
        final IVisitablePointable recAccessor = allocator.allocateRecordValue(inputType);
        final APrintVisitor printVisitor = new APrintVisitor();
        final Pair<PrintStream, ATypeTag> arg = new Pair<PrintStream, ATypeTag>(null, null);

        return new IPrinter() {

            @Override
            public void init() throws AlgebricksException {
                arg.second = inputType.getTypeTag();
            }

            @Override
            public void print(byte[] b, int start, int l, PrintStream ps) throws AlgebricksException {
                try {
                    recAccessor.set(b, start, l);
                    arg.first = ps;
                    recAccessor.accept(printVisitor, arg);
                } catch (Exception ioe) {
                    throw new AlgebricksException(ioe);
                }
            }
        };
    }
}
