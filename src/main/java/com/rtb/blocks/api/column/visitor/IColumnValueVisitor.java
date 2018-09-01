package com.rtb.blocks.api.column.visitor;

public interface IColumnValueVisitor<Row, Value, Sim> {
    void visit(Value value, Row row, Sim simulationId);

    interface IColumnMajorVisitor<Row, Value, Sim> extends IColumnValueVisitor<Row, Value, Sim> {
        void onSimulationStart(Sim simulation);

        void onSimulationEnd(Sim simulation);
    }

    interface IRowMajorVisitor<Row, Value, Sim> extends IColumnValueVisitor<Row, Value, Sim> {
        void onRowStart(Row row);

        void onRowEnd(Row row);
    }
}
