package com.challenge.centerstage.ticketservice.utils;

import com.challenge.centerstage.ticketservice.domain.PerformanceVenue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TheaterSeatMapPrintServiceImpl implements TheaterSeatMapPrintService {
    @Autowired
    PerformanceVenue performanceVenue;

    @Override
    public void printSeatMap() {

        for (int i = 0; i < performanceVenue.getSeatReservedMatrix().length; i++) {
            for (int j = 0; j < performanceVenue.getSeatReservedMatrix()[i].length; j++) {
                System.out.print((performanceVenue.getSeatReservedMatrix())[i][j].substring(0, 3) + " ");
            }
            System.out.println();
        }
    }
}
