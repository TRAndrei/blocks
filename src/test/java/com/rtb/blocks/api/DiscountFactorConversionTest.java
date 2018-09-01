package com.rtb.blocks.api;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.builder.BlockBuilders;
import com.rtb.blocks.api.column.IColumnBlock;
import com.rtb.blocks.api.row.RowVisitors;
import org.junit.Test;

import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DiscountFactorConversionTest {

    @Test
    public void testConversion() {
        IColumnBlock<LocalDate, String> zeroRatesBlock = BlockBuilders.BLOCK_BUILDERS.getColumnBlock(
                ImmutableList.of("B", "1"),
                ImmutableList.of(LocalDate.of(2018, 01, 01), LocalDate.of(2019, 01, 01)),
                (r, s) -> 1);

        LocalDate today = LocalDate.of(2017, 01, 01);
        IColumnBlock<LocalDate, String> discountFactors = zeroRatesBlock.convertValues(r -> true,
                r -> ChronoUnit.DAYS.between(today, r), (s, r, v) -> Math.exp(-1 * v * s /365 ));

        double[] dfs = RowVisitors.getRowValues(discountFactors.getRowBlock(LocalDate.of(2018, 01, 01)));
    }

    @Test
    public void testConversionPerf() {
        int iteration = 1000;
        int simulationCount = 100;
        int rowCount = 100;
        TimeUnit timeUnit = TimeUnit.MICROSECONDS;

        LocalDate today = LocalDate.of(2017, 01, 01);

        List<LocalDate> maturities =
                IntStream.range(0, rowCount).mapToObj(today::plusDays).collect(Collectors.toList());
        List<String> simulations = IntStream.range(0, simulationCount).mapToObj(String::valueOf).collect(Collectors.toList());

        double buildDf = 0;
        double conversion = 0;
        double array = 0;

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        for (int iter = 0; iter < iteration; iter++){
            if (iter == 10) {
                buildDf = conversion = array = 0;
            }
            long start = stopwatch.elapsedTime(timeUnit);

            IColumnBlock<LocalDate, String> zeroRatesBlock = BlockBuilders.BLOCK_BUILDERS.getColumnBlock(
                    simulations, maturities, (r, s) -> 1);

            long currentDf = stopwatch.elapsedTime(timeUnit);
            buildDf += currentDf - start;

            IColumnBlock<LocalDate, String> discountFactors = zeroRatesBlock.convertValues(r -> true,
                    r -> ChronoUnit.DAYS.between(today, r), (s, r, v) -> v + s);

            long currentConversion = stopwatch.elapsedTime(timeUnit);
            conversion += currentConversion - currentDf;

            double[] dfs = RowVisitors.getRowValues(discountFactors.getRowBlock(maturities.get(0)));

            array += stopwatch.elapsedTime(timeUnit) - currentConversion;
        }

        stopwatch.stop();

        System.out.println("ZR building in " + buildDf/iteration + " " + timeUnit);
        System.out.println("DF conversion in " + conversion/iteration + " " + timeUnit);
        System.out.println("DF array building in " + array/iteration + " " + timeUnit);

    }

}