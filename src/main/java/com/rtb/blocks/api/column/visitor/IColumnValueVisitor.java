package com.rtb.blocks.api.column.visitor;

public interface IColumnValueVisitor<Row, Value, Sim> {
    void visit(Value value, Row row, Sim simulationId);

    interface IRowMajorVisitor<Row, Value, Sim> extends IColumnValueVisitor<Row, Value, Sim> {
        void onRowStart(Row row);

        void onRowEnd(Row row);
    }
}
