/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import static org.apache.log4j.TestContants.TARGET_OUTPUT_PREFIX;
import static org.apache.log4j.TestContants.TEST_INPUT_PREFIX;
import static org.apache.log4j.TestContants.TEST_WITNESS_PREFIX;

import org.apache.log4j.util.AbsoluteDateAndTimeFilter;
import org.apache.log4j.util.AbsoluteTimeFilter;
import org.apache.log4j.util.Compare;
import org.apache.log4j.util.ControlFilter;
import org.apache.log4j.util.Filter;
import org.apache.log4j.util.ISO8601Filter;
import org.apache.log4j.util.LineNumberFilter;
import org.apache.log4j.util.Log4jAndNothingElseFilter;
import org.apache.log4j.util.RelativeTimeFilter;
import org.apache.log4j.util.Transformer;

import junit.framework.TestCase;

public class PatternLayoutTestCase extends TestCase {

  static String TEMP = TARGET_OUTPUT_PREFIX+"patternLayoutTest.out";
  static String FILTERED = TARGET_OUTPUT_PREFIX+"filtered";

  Logger root; 
  Logger logger;

  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";
  static String EXCEPTION4 = "\\s*at .*\\(.*Compiled Code\\)";
  static String EXCEPTION5 = "\\s*at .*\\(.*libgcj.*\\)";

  static String PAT0 = "\\[main]\\ (TRACE|DEBUG|INFO |WARN |ERROR|FATAL) .* - Message \\d{1,2}";
  static String PAT1 = Filter.ISO8601_PAT + " " + PAT0;
  static String PAT2 = Filter.ABSOLUTE_DATE_AND_TIME_PAT+ " " + PAT0;
  static String PAT3 = Filter.ABSOLUTE_TIME_PAT+ " " + PAT0;
  static String PAT4 = Filter.RELATIVE_TIME_PAT+ " " + PAT0;

  static String PAT5 = "\\[main]\\ (TRACE|DEBUG|INFO |WARN |ERROR|FATAL) .* : Message \\d{1,2}";
  static String PAT6 = "\\[main]\\ (TRACE|DEBUG|INFO |WARN |ERROR|FATAL) org.apache.log4j.PatternLayoutTestCase.common\\(PatternLayoutTestCase.java(:\\d{1,4})?\\): Message \\d{1,2}";

  static String PAT11a = "^(TRACE|DEBUG|INFO |WARN |ERROR|FATAL) \\[main]\\ log4j.PatternLayoutTestCase: Message \\d{1,2}";
  static String PAT11b = "^(TRACE|DEBUG|INFO |WARN |ERROR|FATAL) \\[main]\\ root: Message \\d{1,2}";

  static String PAT12 = "^\\[main]\\ (TRACE|DEBUG|INFO |WARN |ERROR|FATAL) "+
    "org.apache.log4j.PatternLayoutTestCase.common\\(PatternLayoutTestCase.java:\\d{3}\\): "+
    "Message \\d{1,2}";

  static String PAT13 = "^\\[main]\\ (TRACE|DEBUG|INFO |WARN |ERROR|FATAL) "+
    "apache.log4j.PatternLayoutTestCase.common\\(PatternLayoutTestCase.java:\\d{3}\\): "+
    "Message \\d{1,2}";

  static String PAT14 = "^(TRACE|DEBUG| INFO| WARN|ERROR|FATAL)\\ \\d{1,2}\\ *- Message \\d{1,2}";

  public PatternLayoutTestCase(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger(PatternLayoutTestCase.class);
  }

  public void tearDown() {  
    root.getLoggerRepository().resetConfiguration();
  }

  public void test1() throws Exception {
	String testname = "patternLayout1";
	  		
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+testname+".properties");
    common();
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        new LineNumberFilter(), new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.1"));
  }

  public void test2() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout2.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT1, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new ISO8601Filter(), new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.2"));
  }

  public void test3() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout3.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT1, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new ISO8601Filter(),
        new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.3"));
  }

  // Output format:
  // 06 avr. 2002 18:30:58,937 [main] DEBUG rnLayoutTestCase - Message 0  
  public void test4() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout4.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT2, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new AbsoluteDateAndTimeFilter(),
        new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.4"));
  }

  public void test5() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout5.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT2, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new AbsoluteDateAndTimeFilter(),
        new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.5"));
  }

  // 18:54:19,201 [main] DEBUG rnLayoutTestCase - Message 0
  public void test6() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout6.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT3, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new AbsoluteTimeFilter(),
        new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.6"));
  }


  public void test7() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout7.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT3, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new AbsoluteTimeFilter(),
        new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.7"));
  }

  public void test8() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout8.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT4, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new RelativeTimeFilter(),
        new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.8"));
  }

  public void test9() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout9.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT5, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new Log4jAndNothingElseFilter(),
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.9"));
  }

  public void test10() throws Exception {
    PropertyConfigurator.configure(TestContants.TEST_INPUT_PREFIX+"patternLayout10.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT6, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.10"));
  }

  public void test11() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout11.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT11a, PAT11b, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new Log4jAndNothingElseFilter(),
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.11"));
  }

  public void test12() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout12.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT12, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.12"));
  }

  public void test13() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout13.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT13, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.13"));
  }

  public void test14() throws Exception {
    PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout14.properties");
    common();
    ControlFilter cf1 = new ControlFilter(new String[]{PAT14, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});
    Transformer.transform(
      TEMP, FILTERED,
      new Filter[] {
        cf1, new LineNumberFilter(), new Log4jAndNothingElseFilter()
      });
    assertTrue(Compare.compare(FILTERED, TEST_WITNESS_PREFIX+"patternLayout.14"));
  }

    public void testMDC1() throws Exception {
      PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout.mdc.1.properties");
      MDC.put("key1", "va11");
      MDC.put("key2", "va12");
      logger.debug("Hello World");
      MDC.remove("key1");
      MDC.remove("key2");

      assertTrue(Compare.compare(TEMP, TEST_WITNESS_PREFIX+"patternLayout.mdc.1"));
    }

    public void testMDCClear() throws Exception {
      PropertyConfigurator.configure(TEST_INPUT_PREFIX+"patternLayout.mdc.1.properties");
      MDC.put("key1", "va11");
      MDC.put("key2", "va12");
      logger.debug("Hello World");
      MDC.clear();
      logger.debug("Hello World");

      assertTrue(Compare.compare(TEMP, TEST_WITNESS_PREFIX+"patternLayout.mdc.clear"));
    }



  void common() {
    String oldThreadName = Thread.currentThread().getName();
    Thread.currentThread().setName("main");

    int i = -1;

    logger.trace("Message " + ++i);
    root.trace("Message " + i);

    logger.debug("Message " + ++i);
    root.debug("Message " + i);

    logger.info ("Message " + ++i);
    root.info("Message " + i);

    logger.warn ("Message " + ++i);
    root.warn("Message " + i);

    logger.error("Message " + ++i);
    root.error("Message " + i);

    logger.log(Level.FATAL, "Message " + ++i);
    root.log(Level.FATAL, "Message " + i);

    Exception e = new Exception("Just testing");
    logger.trace("Message " + ++i, e);
    logger.debug("Message " + ++i, e);
    logger.info("Message " + ++i, e);
    logger.warn("Message " + ++i , e);
    logger.error("Message " + ++i, e);
    logger.log(Level.FATAL, "Message " + ++i, e);

    Thread.currentThread().setName(oldThreadName);
  }


}
