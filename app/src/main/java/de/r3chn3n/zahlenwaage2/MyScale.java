package de.r3chn3n.zahlenwaage2;

import lombok.Data;

@Data
public class MyScale {
    //       60 - 80
    private float startX;
    private float endX;
    private float startY;
    private float endY;
    private int sum = 0;
    private boolean containsX = false;
}
