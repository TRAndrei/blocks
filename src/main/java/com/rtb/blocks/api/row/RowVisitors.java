package com.rtb.blocks.api.row;

import com.rtb.blocks.api.row.IRowBlock.ObjectDoubleFunction;
import com.rtb.blocks.api.row.IRowValueBlock.IRowConsumer;

import java.util.function.IntFunction;

public class RowVisitors {
    private RowVisitors() {
        //
    }

    public static <Value, Sim> Value[] getRowValues(IRowValueBlock<Value, Sim> rowValueBlock,
                                                    IntFunction<Value[]> generator) {
        return rowValueBlock.collect(() -> generator.apply(rowValueBlock.getSimulationCount()),
                new RowValueArrayVisitor<>());
    }

    public static <Sim> double[] getRowValues(IRowBlock<Sim> rowBlock) {
        return rowBlock.collect(() -> new double[rowBlock.getSimulationCount()], new RowArrayVisitor<>());
    }

    private static final class RowValueArrayVisitor<Value, Sim> implements IRowConsumer<Value[], Value, Sim> {
        private int idx = 0;


        @Override
        public Value[] consume(Value[] result, Value value, Sim simulation) {
            result[idx++] = value;
            return result;
        }
    }

    private static final class RowArrayVisitor<Sim> implements ObjectDoubleFunction<double[], Sim> {
        int idx = 0;

        @Override
        public double[] apply(double[] result, double value, Sim simulation) {
            result[idx++] = value;
            return result;
        }
    }
}
