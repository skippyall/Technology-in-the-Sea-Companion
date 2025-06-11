package io.github.skippyall.technology_in_the_sea_companion.test;

import net.minecraft.util.math.BlockPos;

public class StepTest {
    public static void main(String[] args) {
        int stepX = 0;
        int stepY = -1;
        for (int i = 0; i < 10; i++) {
            BlockPos pos = new BlockPos(stepX, 0, stepY);

            if (stepX >= 0 && stepY < 0) {
                stepX++;
                stepY++;
            }else if (stepX > 0 && stepY >= 0) {
                stepX--;
                stepY++;
            }else if (stepX <= 0 && stepY > 0) {
                stepX--;
                stepY--;
            }else if (stepX < 0 && stepY <= 0) {
                stepX++;
                stepY--;

                if (stepX == 0) {
                    stepY--;
                }
            }

            System.out.println(pos);
        }
    }
}
