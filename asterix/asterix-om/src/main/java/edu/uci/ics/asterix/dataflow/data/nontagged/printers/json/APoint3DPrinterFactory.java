package edu.uci.ics.asterix.dataflow.data.nontagged.printers.json;

import edu.uci.ics.hyracks.algebricks.data.IPrinter;
import edu.uci.ics.hyracks.algebricks.data.IPrinterFactory;

public class APoint3DPrinterFactory implements IPrinterFactory {

    private static final long serialVersionUID = 1L;
    public static final APoint3DPrinterFactory INSTANCE = new APoint3DPrinterFactory();

    @Override
    public IPrinter createPrinter() {
        return APoint3DPrinter.INSTANCE;
    }

}