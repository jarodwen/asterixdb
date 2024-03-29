package edu.uci.ics.asterix.dataflow.data.nontagged.printers.json;

import edu.uci.ics.hyracks.algebricks.data.IPrinter;
import edu.uci.ics.hyracks.algebricks.data.IPrinterFactory;



public class AInt8PrinterFactory implements IPrinterFactory {

    private static final long serialVersionUID = 1L;
    public static final AInt8PrinterFactory INSTANCE = new AInt8PrinterFactory();

    @Override
    public IPrinter createPrinter() {
        return AInt8Printer.INSTANCE;
    }

}