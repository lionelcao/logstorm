package com.ebay.logstorm.server.utils;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class EagleBanner implements Banner {

    private static final String[] BANNER = { "((\n" +
            "\\\\``.\n" +
            "\\_`.``-. \n" +
            "( `.`.` `._  \n" +
            " `._`-.    `._ \n" +
            "   \\`--.   ,' `. \n" +
            "    `--._  `.  .`. \n" +
            "     `--.--- `. ` `. \n" +
            "         `.--  `;  .`._ \n" +
            "           :-   :   ;. `.__,.,__ __ \n" +
            "            `\\  :       ,-(     ';o`>.\n" +
            "              `-.`:   ,'   `._ .:  (,-`,\n" +
            "                 \\    ;      ;.  ,: \n" +
            "             ,\"`-._>-:        ;,'  `---.,---.\n" +
            "             `>'\"  \"-`       ,'   \"\":::::\".. `-.\n" +
            "              `;\"'_,  (\\`\\ _ `:::::::::::'\"     `---.\n" +
            "  - LogStorm -  `-(_,' -'),)\\`.       _      .::::\"'  `----._,-\"\")\n" +
            "  - v0.1           \\_,': `.-' `-----' `--;-.   `.   ``.`--.____/ \n" +
            "                     `-^--'                \\(-.  `.``-.`-=:-.__)\n" +
            "                                            `  `.`.`._`.-._`--.)\n" +
            "                                                 `-^---^--.`--"};

    private static final String SPRING_BOOT = " :: LogStorm :: ";

    private static final int STRAP_LINE_SIZE = 42;

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass,
                            PrintStream printStream) {
        for (String line : BANNER) {
            printStream.println(line);
        }
        String version = SpringBootVersion.getVersion();
        version = (version == null ? "" : " (v" + version + ")");
        String padding = "";
        while (padding.length() < STRAP_LINE_SIZE
                - (version.length() + SPRING_BOOT.length())) {
            padding += " ";
        }
        printStream.println(AnsiOutput.toString(AnsiColor.GREEN, SPRING_BOOT,
                AnsiColor.DEFAULT, padding, AnsiStyle.FAINT, version));
        printStream.println();
    }
}
