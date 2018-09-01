package com.rtb.blocks.api.column.visitor;

public interface IColumnVisitor<Row, Sim> {
    void visit(double value, Row row, Sim simulationId);

    interface IRowMajorVisitor<Row, Sim> extends IColumnVisitor<Row, Sim> {
        void onRowStart(Row row);

        void onRowEnd(Row row);
    }
}
