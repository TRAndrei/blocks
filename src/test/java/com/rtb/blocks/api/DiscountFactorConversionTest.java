package com.rtb.blocks.api;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.rtb.blocks.api.builder.BlockBuilders;
import com.rtb.blocks.api.column.IColumnBlock;
import org.junit.Test;

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
        IColumnBlock<LocalDate, String> discountFactors = zeroRatesBlock.convertValues(
                r -> ChronoUnit.DAYS.between(today, r), row -> (s, r, v) -> Math.exp(-1 * v * s / 365));

        int rowCount = discountFactors.getRowCount();
    }

    @Test
    public void testConversionPerf() {
        int iteration = 10000;
        int simulationCount = 10;
        int rowCount = 10;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        LocalDate today = LocalDate.of(2017, 01, 01);

        List<LocalDate> maturities =
                IntStream.range(0, rowCount).mapToObj(today::plusDays).collect(Collectors.toList());
        List<String> simulations = IntStream.range(0, simulationCount).mapToObj(String::valueOf).collect(Collectors.toList());

        double buildDf = 0;
        double conversion = 0;
        double array = 0;

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        int val = 0;
        for (int iter = 0; iter < iteration; iter++) {
            long start = stopwatch.elapsedTime(timeUnit);

            IColumnBlock<LocalDate, String> zeroRatesBlock = BlockBuilders.BLOCK_BUILDERS.getColumnBlock(
                    simulations, maturities, (r, s) -> 1);

            long currentDf = stopwatch.elapsedTime(timeUnit);
            buildDf += currentDf - start;

            IColumnBlock<LocalDate, String> discountFactors = zeroRatesBlock.convertValues(
                    r -> ChronoUnit.DAYS.between(today, r), row -> (s, r, v) -> v + s);

            long currentConversion = stopwatch.elapsedTime(timeUnit);
            conversion += currentConversion - currentDf;
            val += discountFactors.getRowCount();

            array += stopwatch.elapsedTime(timeUnit) - currentConversion;
        }

        stopwatch.stop();
        int r = val;

        System.out.println("ZR building in " + buildDf / iteration + " " + timeUnit);
        System.out.println("DF conversion in " + conversion / iteration + " " + timeUnit);
        System.out.println("DF array building in " + array / iteration + " " + timeUnit);

    }

    @Test
    public void testRaw() {
        int size = 100;
        int iteration = 10000;

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        for (int iter = 0; iter < iteration; iter++) {
            double[] zr = new double[size];
            double[] df = new double[size];

            for (int i = 0; i < size; i++) {
                zr[i] = 1;
            }

            for (int i = 0; i < size; i++) {
                zr[i] = df[i] + 1;
            }
        }

        double elapsed = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
        stopwatch.stop();

        System.out.println("Raw test in " + elapsed / iteration);
    }
}
