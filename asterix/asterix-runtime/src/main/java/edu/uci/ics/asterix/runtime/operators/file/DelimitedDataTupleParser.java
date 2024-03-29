/*
 * Copyright 2009-2012 by The Regents of the University of California
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uci.ics.asterix.runtime.operators.file;

import edu.uci.ics.asterix.om.types.ARecordType;
import edu.uci.ics.hyracks.api.context.IHyracksTaskContext;
import edu.uci.ics.hyracks.dataflow.common.data.parsers.IValueParserFactory;

/**
 * An extension of AbstractTupleParser that provides functionality for
 * parsing delimited files.
 */
public class DelimitedDataTupleParser extends AbstractTupleParser {

    private final DelimitedDataParser dataParser;

    public DelimitedDataTupleParser(IHyracksTaskContext ctx, ARecordType recType,
            IValueParserFactory[] valueParserFactories, char fieldDelimter) {
        super(ctx, recType);
        dataParser = new DelimitedDataParser(recType, valueParserFactories, fieldDelimter);
    }

    @Override
    public IDataParser getDataParser() {
        return dataParser;
    }

}
