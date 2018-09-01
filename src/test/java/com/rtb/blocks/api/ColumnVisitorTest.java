package com.rtb.blocks.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rtb.blocks.api.builder.BlockBuilders;
import com.rtb.blocks.api.column.IColumnBlock;
import com.rtb.blocks.api.column.visitor.IColumnVisitor;
import com.rtb.blocks.api.column.visitor.IColumnVisitor.IColumnMajorVisitor;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ColumnVisitorTest {

    @Test
    public void visitColumnBySimulation() {
        IColumnBlock<LocalDate, String> zeroRatesBlock = BlockBuilders.BLOCK_BUILDERS.getColumnBlock(
                ImmutableList.of("B", "1"),
                ImmutableList.of(LocalDate.of(2018, 01, 01), LocalDate.of(2019, 01, 01)),
                (r, s) -> 1);

        List<Map<LocalDate, Double>> results = Lists.newArrayList();

        IColumnMajorVisitor<LocalDate, String> toCurveVisitor = new IColumnMajorVisitor<LocalDate, String>() {
            private Map<LocalDate, Double> curve;

            @Override
            public void onSimulationStart(String simulation) {
                curve = Maps.newHashMap();
            }

            @Override
            public void onSimulationEnd(String simulation) {
                results.add(curve);
            }

            @Override
            public void visit(double value, LocalDate maturity, String simulationId) {
                curve.put(maturity, value);
            }
        };

        zeroRatesBlock.accept(toCurveVisitor);
    }
}
