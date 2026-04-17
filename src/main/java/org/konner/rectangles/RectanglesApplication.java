package org.konner.rectangles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RectanglesApplication {

    public static void main(String[] args) {
        String[] effectiveArgs = args.length == 0 ? new String[]{"demo"} : args;
        SpringApplication.run(RectanglesApplication.class, effectiveArgs);
    }

}
